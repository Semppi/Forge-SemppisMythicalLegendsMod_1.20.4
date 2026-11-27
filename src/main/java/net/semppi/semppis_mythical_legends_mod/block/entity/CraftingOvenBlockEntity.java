package net.semppi.semppis_mythical_legends_mod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import net.semppi.semppis_mythical_legends_mod.block.custom.CraftingOvenBlock;
import net.semppi.semppis_mythical_legends_mod.menu.CraftingOvenMenu;
import org.jetbrains.annotations.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.semppi.semppis_mythical_legends_mod.block.custom.CraftingOvenBlock;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;

import java.util.Optional;

public class CraftingOvenBlockEntity
        extends BlockEntity
        implements GeoBlockEntity, MenuProvider {

    public static final int SMOKER_INPUT_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int SMOKER_OUTPUT_SLOT = 2;

    public static final int CRAFTING_SLOT_START = 3;
    public static final int CRAFTING_SLOT_COUNT = 9;

    public static final int TOTAL_SLOT_COUNT =
            3 + CRAFTING_SLOT_COUNT;
    private int burnTime = 0;
    private int burnDuration = 0;
    private int cookProgress = 0;
    private int cookTotalTime = 100;
    private float storedExperience = 0.0F;

    public static final int MAX_QUEUED_CRAFTS = 64;

    /*
     * Special Crafting Oven recipe waiting to be cooked.
     *
     * This is NOT an ItemStack sitting in an inventory slot.
     */
    private boolean craftingRecipeQueued = false;

    private int queuedCraftingCount = 0;

    public int getQueuedCraftingCount() {
        return this.queuedCraftingCount;
    }

    private ItemStack queuedCraftingResult =
            ItemStack.EMPTY;

    private int queuedCraftingTime = 100;

    private float queuedCraftingExperience = 0.0F;

    /*
     * True once this queued recipe has actually taken control
     * of the oven's cooking progress.
     */
    private boolean cookingQueuedCraftingRecipe = false;

    public boolean hasQueuedCraftingRecipe() {
        return this.craftingRecipeQueued;
    }

    public boolean isCookingQueuedCraftingRecipe() {
        return this.cookingQueuedCraftingRecipe;
    }

    public boolean queueCraftingRecipe(
            ItemStack result,
            int cookingTime,
            float experience
    ) {

        /*
         * Make sure the crafting grid contains enough
         * ingredients to reserve one more complete craft.
         */
        if (!this.canQueueAnotherCraft()) {
            return false;
        }

        /*
         * First queued craft:
         * remember exactly what this batch is producing.
         */
        if (!this.craftingRecipeQueued) {

            this.craftingRecipeQueued = true;

            this.queuedCraftingResult =
                    result.copy();

            this.queuedCraftingTime =
                    Math.max(
                            1,
                            cookingTime
                    );

            this.queuedCraftingExperience =
                    experience;
        }

        /*
         * A batch already exists.
         *
         * Do not allow a different recipe to somehow get
         * appended to the same batch.
         */
        else {

            if (!ItemStack.isSameItemSameTags(
                    this.queuedCraftingResult,
                    result
            )) {
                return false;
            }

            if (this.queuedCraftingResult.getCount()
                    != result.getCount()) {
                return false;
            }
        }

        /*
         * One more meal is now reserved.
         */
        /*
         * One more meal is now reserved.
         */
        this.queuedCraftingCount++;

        /*
         * If nothing is currently part-way through cooking,
         * immediately give the queued Crafting Oven recipe
         * control of the cooking system.
         *
         * This prevents the recipe from entering the dangerous
         * state where its ingredients are locked but the oven
         * has not actually switched to special-recipe cooking.
         */
        if (this.cookProgress == 0) {
            this.cookingQueuedCraftingRecipe = true;
        }

        this.setChanged();

        return true;
    }


    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    /*
     * For this first GUI version, only the smoker section
     * belongs permanently to the block entity.
     */
    private final ItemStackHandler itemHandler =
            new ItemStackHandler(TOTAL_SLOT_COUNT) {

                @Override
                protected void onContentsChanged(int slot) {
                    setChanged();
                }
            };

    private LazyOptional<ItemStackHandler> lazyItemHandler =
            LazyOptional.empty();

    public CraftingOvenBlockEntity(
            BlockPos blockPos,
            BlockState blockState
    ) {
        super(
                ModBlockEntities.CRAFTING_OVEN_BLOCK_ENTITY.get(),
                blockPos,
                blockState
        );
    }

    public ItemStackHandler getItemHandler() {
        return this.itemHandler;
    }

    public void awardStoredExperience(Player player) {

        if (!(player.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        if (this.storedExperience <= 0.0F) {
            return;
        }

        /*
         * XP orbs can only contain whole XP amounts.
         *
         * Keep Minecraft's fractional recipe XP meaningful
         * by giving the remaining fraction a matching chance
         * to round upward.
         *
         * Example:
         * 2.7 stored XP =
         * 2 guaranteed XP + 70% chance of one more.
         */
        int experienceToAward =
                (int) Math.floor(
                        this.storedExperience
                );

        float fractionalExperience =
                this.storedExperience
                        - experienceToAward;

        if (serverLevel.random.nextFloat()
                < fractionalExperience) {

            experienceToAward++;
        }

        if (experienceToAward > 0) {

            ExperienceOrb.award(
                    serverLevel,
                    player.position(),
                    experienceToAward
            );
        }

        /*
         * The stored experience has now been claimed.
         */
        this.storedExperience = 0.0F;

        this.setChanged();
    }

    public void cancelQueuedCraftingRecipe() {

        if (!this.craftingRecipeQueued) {
            return;
        }

        /*
         * Remember whether the custom recipe currently owns
         * the cook-progress timer.
         *
         * If it is only waiting behind a normal smoker recipe,
         * we must NOT destroy the normal recipe's progress.
         */
        boolean wasActivelyCookingCustomRecipe =
                this.cookingQueuedCraftingRecipe;

        this.craftingRecipeQueued = false;
        this.cookingQueuedCraftingRecipe = false;

        this.queuedCraftingCount = 0;

        this.queuedCraftingResult =
                ItemStack.EMPTY;

        this.queuedCraftingTime = 100;
        this.queuedCraftingExperience = 0.0F;

        /*
         * This is equivalent to removing food from a furnace:
         * the current special-food progress is lost.
         */
        if (wasActivelyCookingCustomRecipe) {
            this.cookProgress = 0;
            this.cookTotalTime = 100;
        }

        this.setChanged();
    }

    public boolean canQueueAnotherCraft() {

        if (this.queuedCraftingCount
                >= MAX_QUEUED_CRAFTS) {
            return false;
        }

        /*
         * The next button press reserves one additional
         * use of every occupied crafting-grid slot.
         *
         * Example:
         *
         * queuedCraftingCount = 4
         *
         * Pressing again requires at least 5 items in
         * every occupied ingredient stack.
         */
        int requiredCount =
                this.queuedCraftingCount + 1;

        boolean foundIngredient = false;

        for (int slot = 0;
             slot < CRAFTING_SLOT_COUNT;
             slot++) {

            ItemStack ingredient =
                    this.itemHandler.getStackInSlot(
                            CRAFTING_SLOT_START + slot
                    );

            if (!ingredient.isEmpty()) {

                foundIngredient = true;

                if (ingredient.getCount()
                        < requiredCount) {

                    return false;
                }
            }
        }

        return foundIngredient;
    }

    private final ContainerData data =
            new ContainerData() {

                @Override
                public int get(int index) {
                    return switch (index) {
                        case 0 -> CraftingOvenBlockEntity.this.burnTime;
                        case 1 -> CraftingOvenBlockEntity.this.burnDuration;
                        case 2 -> CraftingOvenBlockEntity.this.cookProgress;
                        case 3 -> CraftingOvenBlockEntity.this.cookTotalTime;

                        /*
                         * 1 = special Crafting Oven recipe
                         * 0 = normal smoker cooking
                         */
                        case 4 ->
                                CraftingOvenBlockEntity.this
                                        .cookingQueuedCraftingRecipe
                                        ? 1
                                        : 0;

                        case 5 ->
                                CraftingOvenBlockEntity.this
                                        .queuedCraftingCount;

                        default -> 0;
                    };
                }

                @Override
                public void set(
                        int index,
                        int value
                ) {
                    switch (index) {
                        case 0 ->
                                CraftingOvenBlockEntity.this.burnTime =
                                        value;

                        case 1 ->
                                CraftingOvenBlockEntity.this.burnDuration =
                                        value;

                        case 2 ->
                                CraftingOvenBlockEntity.this.cookProgress =
                                        value;

                        case 3 ->
                                CraftingOvenBlockEntity.this.cookTotalTime =
                                        value;

                        case 4 ->
                                CraftingOvenBlockEntity.this
                                        .cookingQueuedCraftingRecipe =
                                        value != 0;

                        case 5 ->
                                CraftingOvenBlockEntity.this
                                        .queuedCraftingCount =
                                        value;

                        /*
                         * We don't need to set index 4 from the client.
                         */
                    }
                }

                @Override
                public int getCount() {
                    return 6;
                }
            };

    public ContainerData getData() {
        return this.data;
    }

    /*
     * Finds the invisible side/table section of the Crafting Oven.
     */
    public BlockPos getSideTablePos() {

        BlockState ovenState =
                this.getBlockState();

        Direction facing =
                ovenState.getValue(
                        CraftingOvenBlock.FACING
                );

        /*
         * CraftingOvenBlock places the side section
         * counter-clockwise from the main oven.
         */
        return this.worldPosition.relative(
                facing.getCounterClockWise()
        );
    }

    /*
     * The chest sits directly on top of the oven's
     * side/table section.
     */
    public BlockPos getAttachedChestPos() {

        return this.getSideTablePos()
                .above();
    }

    /*
     * Returns true only for a normal SINGLE vanilla chest
     * sitting directly on top of the side table.
     *
     * Double chests, trapped chests and modded containers
     * are intentionally rejected.
     */
    public boolean hasAttachedChest() {

        if (this.level == null) {
            return false;
        }

        BlockPos chestPos =
                this.getAttachedChestPos();

        BlockState chestState =
                this.level.getBlockState(
                        chestPos
                );

        /*
         * Exact vanilla normal chest only.
         */
        if (!chestState.is(Blocks.CHEST)) {
            return false;
        }

        /*
         * Reject double chests.
         */
        return chestState.getValue(
                ChestBlock.TYPE
        ) == ChestType.SINGLE;
    }

    /*
     * Returns the actual attached vanilla chest block entity.
     *
     * Returns null if there is no supported chest.
     */
    @Nullable
    public ChestBlockEntity getAttachedChest() {

        if (!this.hasAttachedChest()
                || this.level == null) {

            return null;
        }

        BlockEntity blockEntity =
                this.level.getBlockEntity(
                        this.getAttachedChestPos()
                );

        if (blockEntity instanceof ChestBlockEntity chest) {
            return chest;
        }

        return null;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(
                "block.semppis_mythical_legends_mod.crafting_oven"
        );
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(
            int containerId,
            Inventory inventory,
            Player player

    ) {
        System.out.println(
                "[SML CRAFTING OVEN] Side table: "
                        + this.getSideTablePos()
                        + " | Chest position: "
                        + this.getAttachedChestPos()
                        + " | Valid chest: "
                        + this.hasAttachedChest()
        );
        return new CraftingOvenMenu(
                containerId,
                inventory,
                this
        );
    }

    @Override
    public void onLoad() {
        super.onLoad();

        this.lazyItemHandler =
                LazyOptional.of(
                        () -> this.itemHandler
                );
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();

        this.lazyItemHandler.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(
            Capability<T> cap,
            @Nullable Direction side
    ) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.lazyItemHandler.cast();
        }

        return super.getCapability(
                cap,
                side
        );
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        /*
         * Save the three smoker inventory slots.
         */
        tag.put(
                "inventory",
                this.itemHandler.serializeNBT()
        );

        /*
         * Save the smoker's current state so leaving
         * the world does not erase an active fire/cook.
         */
        tag.putInt(
                "BurnTime",
                this.burnTime
        );

        tag.putInt(
                "BurnDuration",
                this.burnDuration
        );

        tag.putInt(
                "CookProgress",
                this.cookProgress
        );

        tag.putInt(
                "CookTotalTime",
                this.cookTotalTime
        );

        tag.putFloat(
                "StoredExperience",
                this.storedExperience
        );

        tag.putBoolean(
                "CraftingRecipeQueued",
                this.craftingRecipeQueued
        );

        tag.putBoolean(
                "CookingQueuedCraftingRecipe",
                this.cookingQueuedCraftingRecipe
        );

        tag.putInt(
                "QueuedCraftingCount",
                this.queuedCraftingCount
        );

        tag.putInt(
                "QueuedCraftingTime",
                this.queuedCraftingTime
        );

        tag.putFloat(
                "QueuedCraftingExperience",
                this.queuedCraftingExperience
        );

        if (!this.queuedCraftingResult.isEmpty()) {
            tag.put(
                    "QueuedCraftingResult",
                    this.queuedCraftingResult.save(
                            new CompoundTag()
                    )
            );
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        boolean oldCraftingRecipeQueued =
                tag.getBoolean(
                        "CraftingRecipeQueued"
                );

        /*
         * New saves contain the actual quantity.
         *
         * An oven saved with the previous version only has
         * CraftingRecipeQueued=true/false, so treat an old
         * queued recipe as a batch of one.
         */
        if (tag.contains("QueuedCraftingCount")) {

            this.queuedCraftingCount =
                    Math.max(
                            0,
                            tag.getInt(
                                    "QueuedCraftingCount"
                            )
                    );

        } else {

            this.queuedCraftingCount =
                    oldCraftingRecipeQueued
                            ? 1
                            : 0;
        }

        this.craftingRecipeQueued =
                this.queuedCraftingCount > 0;

        this.cookingQueuedCraftingRecipe =
                tag.getBoolean(
                        "CookingQueuedCraftingRecipe"
                );

        this.queuedCraftingTime =
                Math.max(
                        1,
                        tag.getInt(
                                "QueuedCraftingTime"
                        )
                );

        this.queuedCraftingExperience =
                tag.getFloat(
                        "QueuedCraftingExperience"
                );

        if (tag.contains("QueuedCraftingResult")) {
            this.queuedCraftingResult =
                    ItemStack.of(
                            tag.getCompound(
                                    "QueuedCraftingResult"
                            )
                    );
        } else {
            this.queuedCraftingResult =
                    ItemStack.EMPTY;
        }

        CompoundTag inventoryTag =
                tag.getCompound("inventory")
                        .copy();

        inventoryTag.putInt(
                "Size",
                TOTAL_SLOT_COUNT
        );

        this.itemHandler.deserializeNBT(
                inventoryTag
        );

        /*
         * Restore the smoker timers.
         */
        this.burnTime =
                tag.getInt("BurnTime");

        this.burnDuration =
                tag.getInt("BurnDuration");

        this.cookProgress =
                tag.getInt("CookProgress");

        this.cookTotalTime =
                tag.getInt("CookTotalTime");

        /*
         * Recover a queued Crafting Oven recipe that was saved
         * before it successfully entered active cooking mode.
         */
        if (this.craftingRecipeQueued
                && this.cookProgress == 0) {

            this.cookingQueuedCraftingRecipe = true;
        }

        /*
         * Old ovens/world saves may not yet contain
         * CookTotalTime, so give them the normal
         * smoker fallback instead of leaving it at 0.
         */
        if (this.cookTotalTime <= 0) {
            this.cookTotalTime = 100;
        }

        this.storedExperience =
                tag.getFloat("StoredExperience");
    }

    public boolean canQueueCraftingRecipeResult(
            ItemStack result
    ) {
        if (result.isEmpty()) {
            return false;
        }

        ItemStack output =
                this.itemHandler.getStackInSlot(
                        SMOKER_OUTPUT_SLOT
                );

        /*
         * Empty output is always fine.
         */
        if (output.isEmpty()) {
            return true;
        }

        /*
         * Existing output must be the exact same item.
         */
        if (!ItemStack.isSameItemSameTags(
                output,
                result
        )) {
            return false;
        }

        /*
         * And there must still be room for one result.
         */
        return output.getCount()
                + result.getCount()
                <= output.getMaxStackSize();
    }

    private boolean canAcceptQueuedCraftingResult() {

        if (!this.craftingRecipeQueued
                || this.queuedCraftingResult.isEmpty()) {
            return false;
        }

        ItemStack output =
                this.itemHandler.getStackInSlot(
                        SMOKER_OUTPUT_SLOT
                );

        if (output.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameTags(
                output,
                this.queuedCraftingResult
        )) {
            return false;
        }

        return output.getCount()
                + this.queuedCraftingResult.getCount()
                <= output.getMaxStackSize();
    }

    public static void serverTick(
            Level level,
            BlockPos pos,
            BlockState state,
            CraftingOvenBlockEntity oven
    ) {

        boolean changed = false;

        boolean wasBurning =
                oven.burnTime > 0;

        /*
         * Existing fuel loses one tick.
         */
        if (oven.burnTime > 0) {
            oven.burnTime--;
            changed = true;
        }

        /*
         * Vanilla smoker ambience.
         */
        if (oven.burnTime > 0
                && level.random.nextDouble() < 0.03D) {

            level.playSound(
                    null,
                    pos,
                    SoundEvents.SMOKER_SMOKE,
                    SoundSource.BLOCKS,
                    1.0F,
                    1.0F
            );
        }

        ItemStack input =
                oven.itemHandler.getStackInSlot(
                        SMOKER_INPUT_SLOT
                );

        ItemStack fuel =
                oven.itemHandler.getStackInSlot(
                        FUEL_SLOT
                );

        Optional<RecipeHolder<SmokingRecipe>> smokingRecipe =
                level.getRecipeManager()
                        .getRecipeFor(
                                RecipeType.SMOKING,
                                new SimpleContainer(input),
                                level
                        );

        boolean canCookNormal =
                smokingRecipe.isPresent()
                        && oven.canAcceptSmokingResult(
                        smokingRecipe.get()
                );

        /*
         * If a special recipe is waiting and no other item is
         * currently part-way through cooking, give the queued
         * Crafting Oven recipe control of the next cooking cycle.
         */
        if (oven.craftingRecipeQueued
                && !oven.cookingQueuedCraftingRecipe
                && oven.cookProgress == 0) {

            oven.cookingQueuedCraftingRecipe = true;

            changed = true;
        }

        boolean canCookQueued =
                oven.cookingQueuedCraftingRecipe
                        && oven.craftingRecipeQueued
                        && oven.canAcceptQueuedCraftingResult();

        /*
         * Once a queued recipe has claimed the next cooking cycle,
         * normal smoker food waits until it is finished.
         */
        boolean canCook =
                oven.cookingQueuedCraftingRecipe
                        ? canCookQueued
                        : canCookNormal;

        /*
         * Something is ready to cook but there is no active fire.
         * Consume one fuel item.
         */
        if (oven.burnTime <= 0
                && canCook
                && !fuel.isEmpty()) {

            int burnValue =
                    ForgeHooks.getBurnTime(
                            fuel,
                            RecipeType.SMOKING
                    );

            if (burnValue > 0) {

                oven.burnTime =
                        burnValue;

                oven.burnDuration =
                        burnValue;

                fuel.shrink(1);

                changed = true;
            }
        }

        /*
         * Cook while fuel is burning.
         */
        if (oven.burnTime > 0
                && canCook) {

            oven.cookProgress++;

            changed = true;

            if (oven.cookingQueuedCraftingRecipe) {

                oven.cookTotalTime =
                        oven.queuedCraftingTime;

            } else {

                oven.cookTotalTime =
                        smokingRecipe.get()
                                .value()
                                .getCookingTime();
            }

            /*
             * Cooking cycle completed.
             */
            if (oven.cookProgress
                    >= oven.cookTotalTime) {

                if (oven.cookingQueuedCraftingRecipe) {

                    oven.finishQueuedCraftingRecipe();

                } else {

                    oven.finishSmoking(
                            smokingRecipe.get()
                    );
                }

                oven.cookProgress = 0;

                changed = true;
            }

        } else {

            /*
             * If a normal smoker recipe becomes invalid halfway
             * through, discard that partial progress.
             *
             * A queued recipe whose output is temporarily blocked
             * keeps ownership of the next cycle and simply waits.
             */
            if (!oven.cookingQueuedCraftingRecipe
                    && oven.cookProgress != 0) {

                oven.cookProgress = 0;

                changed = true;
            }
        }

        boolean isBurning =
                oven.burnTime > 0;

        if (wasBurning != isBurning) {

            BlockState currentState =
                    level.getBlockState(pos);

            if (currentState.hasProperty(
                    CraftingOvenBlock.LIT
            )) {

                level.setBlock(
                        pos,
                        currentState.setValue(
                                CraftingOvenBlock.LIT,
                                isBurning
                        ),
                        3
                );
            }
        }

        if (changed) {
            oven.setChanged();
        }
    }

    private void finishQueuedCraftingRecipe() {

        if (!this.craftingRecipeQueued
                || this.queuedCraftingResult.isEmpty()) {

            return;
        }

        if (!this.canAcceptQueuedCraftingResult()) {
            return;
        }

        ItemStack output =
                this.itemHandler.getStackInSlot(
                        SMOKER_OUTPUT_SLOT
                );

        /*
         * Produce the final cooked food.
         */
        if (output.isEmpty()) {

            this.itemHandler.setStackInSlot(
                    SMOKER_OUTPUT_SLOT,
                    this.queuedCraftingResult.copy()
            );

        } else {

            output.grow(
                    this.queuedCraftingResult.getCount()
            );
        }

        /*
         * NOW consume one item from every occupied
         * Crafting Oven grid slot.
         *
         * Nothing was consumed when the queue button
         * was originally pressed.
         */
        for (int slot = 0;
             slot < CRAFTING_SLOT_COUNT;
             slot++) {

            int handlerSlot =
                    CRAFTING_SLOT_START + slot;

            ItemStack ingredient =
                    this.itemHandler.getStackInSlot(
                            handlerSlot
                    );

            if (!ingredient.isEmpty()) {
                ingredient.shrink(1);

                if (ingredient.isEmpty()) {
                    this.itemHandler.setStackInSlot(
                            handlerSlot,
                            ItemStack.EMPTY
                    );
                }
            }
        }

        /*
         * Store the recipe XP for when the player
         * removes the cooked output.
         */
        this.storedExperience +=
                this.queuedCraftingExperience;

        /*
         * One reserved meal has finished.
         */
        this.queuedCraftingCount--;

        /*
         * More copies remain in the batch.
         *
         * Keep ownership of the oven so another queued
         * Crafting Oven recipe starts immediately on the
         * next cooking cycle.
         */
        if (this.queuedCraftingCount > 0) {

            this.craftingRecipeQueued = true;
            this.cookingQueuedCraftingRecipe = true;
        }

        /*
         * The whole batch is finished.
         *
         * Release the oven back to normal smoker cooking.
         */
        else {

            this.queuedCraftingCount = 0;

            this.craftingRecipeQueued = false;
            this.cookingQueuedCraftingRecipe = false;

            this.queuedCraftingResult =
                    ItemStack.EMPTY;

            this.queuedCraftingTime = 100;
            this.queuedCraftingExperience = 0.0F;
        }

        this.setChanged();
    }

    private boolean canAcceptSmokingResult(
            RecipeHolder<SmokingRecipe> recipe
    ) {

        ItemStack result =
                recipe.value()
                        .assemble(
                                new SimpleContainer(
                                        this.itemHandler.getStackInSlot(
                                                SMOKER_INPUT_SLOT
                                        )
                                ),
                                this.level.registryAccess()
                        );

        if (result.isEmpty()) {
            return false;
        }

        ItemStack output =
                this.itemHandler.getStackInSlot(
                        SMOKER_OUTPUT_SLOT
                );

        if (output.isEmpty()) {
            return true;
        }

        if (!ItemStack.isSameItemSameTags(
                output,
                result
        )) {
            return false;
        }

        return output.getCount()
                + result.getCount()
                <= output.getMaxStackSize();
    }

    private void finishSmoking(
            RecipeHolder<SmokingRecipe> recipe
    ) {

        ItemStack input =
                this.itemHandler.getStackInSlot(
                        SMOKER_INPUT_SLOT
                );

        ItemStack result =
                recipe.value()
                        .assemble(
                                new SimpleContainer(input),
                                this.level.registryAccess()
                        );

        if (result.isEmpty()) {
            return;
        }

        ItemStack output =
                this.itemHandler.getStackInSlot(
                        SMOKER_OUTPUT_SLOT
                );

        input.shrink(1);

        if (output.isEmpty()) {

            this.itemHandler.setStackInSlot(
                    SMOKER_OUTPUT_SLOT,
                    result.copy()
            );

        } else {

            output.grow(
                    result.getCount()
            );
        }

        this.storedExperience +=
                recipe.value().getExperience();

        this.setChanged();
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
        // No animations yet.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}