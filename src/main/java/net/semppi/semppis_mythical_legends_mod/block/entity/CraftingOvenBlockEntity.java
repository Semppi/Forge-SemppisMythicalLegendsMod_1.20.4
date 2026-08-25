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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
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
    private int burnTime = 0;
    private int burnDuration = 0;
    private int cookProgress = 0;
    private int cookTotalTime = 100;
    private float storedExperience = 0.0F;


    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    /*
     * For this first GUI version, only the smoker section
     * belongs permanently to the block entity.
     */
    private final ItemStackHandler itemHandler =
            new ItemStackHandler(3) {

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

    private final ContainerData data = new ContainerData() {

        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> CraftingOvenBlockEntity.this.burnTime;
                case 1 -> CraftingOvenBlockEntity.this.burnDuration;
                case 2 -> CraftingOvenBlockEntity.this.cookProgress;
                case 3 -> CraftingOvenBlockEntity.this.cookTotalTime;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> CraftingOvenBlockEntity.this.burnTime = value;
                case 1 -> CraftingOvenBlockEntity.this.burnDuration = value;
                case 2 -> CraftingOvenBlockEntity.this.cookProgress = value;
                case 3 -> CraftingOvenBlockEntity.this.cookTotalTime = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public ContainerData getData() {
        return this.data;
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
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        /*
         * Restore the inventory.
         */
        this.itemHandler.deserializeNBT(
                tag.getCompound("inventory")
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
         * Existing fire loses one tick.
         */
        if (oven.burnTime > 0) {
            oven.burnTime--;
            changed = true;
        }

        /*
         * Vanilla smoker ambience while fuel is actively burning.
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

        Optional<RecipeHolder<SmokingRecipe>> recipe =
                level.getRecipeManager()
                        .getRecipeFor(
                                RecipeType.SMOKING,
                                new SimpleContainer(input),
                                level
                        );

        boolean genericFood =
                oven.isGenericUncookedFood(
                        input
                );

        boolean canCookGeneric =
                genericFood
                        && oven.canAcceptGenericResult(
                        input
                );

        boolean canCookNormal =
                recipe.isPresent()
                        && oven.canAcceptSmokingResult(
                        recipe.get()
                );

        boolean canCook =
                canCookGeneric
                        || canCookNormal;

        /*
         * We have something cookable but no active fire.
         * Try consuming one fuel item.
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
         * Cook while burning.
         */
        if (oven.burnTime > 0
                && canCook) {

            oven.cookProgress++;
            changed = true;

            /*
             * Vanilla smoking recipes generally use 100 ticks,
             * but use the recipe's own cooking time.
             */
            if (canCookGeneric) {

                oven.cookTotalTime =
                        oven.getGenericCookingTime(
                                input
                        );

            } else {

                oven.cookTotalTime =
                        recipe.get()
                                .value()
                                .getCookingTime();
            }

            if (oven.cookProgress
                    >= oven.cookTotalTime) {

                if (canCookGeneric) {

                    oven.finishGenericCooking(
                            input
                    );

                } else {

                    oven.finishSmoking(
                            recipe.get()
                    );
                }

                oven.cookProgress = 0;

                changed = true;
            }

        } else {

            /*
             * No valid active cook.
             */
            if (oven.cookProgress != 0) {
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

    /*
     * Is this one of our special prepared-food ItemStacks?
     */
    private boolean isGenericUncookedFood(
            ItemStack stack
    ) {

        return stack.is(
                ModItems.GENERIC_UNCOOKED_FOOD.get()
        )
                && stack.hasTag()
                && stack.getTag()
                .contains(
                        "CraftingOvenResult"
                );
    }


    /*
     * Read the final cooked food from the generic
     * prepared-food ItemStack.
     */
    private ItemStack getGenericCookingResult(
            ItemStack stack
    ) {

        if (!isGenericUncookedFood(stack)) {
            return ItemStack.EMPTY;
        }

        String resultName =
                stack.getTag()
                        .getString(
                                "CraftingOvenResult"
                        );

        ResourceLocation resultId =
                ResourceLocation.tryParse(
                        resultName
                );

        if (resultId == null) {
            return ItemStack.EMPTY;
        }

        int resultCount =
                Math.max(
                        1,
                        stack.getTag()
                                .getInt(
                                        "CraftingOvenResultCount"
                                )
                );

        return new ItemStack(
                BuiltInRegistries.ITEM.get(
                        resultId
                ),
                resultCount
        );
    }


    /*
     * Read how long this prepared food should cook.
     */
    private int getGenericCookingTime(
            ItemStack stack
    ) {

        if (!isGenericUncookedFood(stack)) {
            return 100;
        }

        int cookTime =
                stack.getTag()
                        .getInt(
                                "CraftingOvenCookTime"
                        );

        return Math.max(
                1,
                cookTime
        );
    }


    /*
     * Check whether the cooked result fits in
     * the normal smoker output slot.
     */
    private boolean canAcceptGenericResult(
            ItemStack input
    ) {

        ItemStack result =
                getGenericCookingResult(
                        input
                );

        if (result.isEmpty()) {
            return false;
        }

        ItemStack output =
                this.itemHandler
                        .getStackInSlot(
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

    private void finishGenericCooking(
            ItemStack input
    ) {

        ItemStack result =
                getGenericCookingResult(
                        input
                );

        if (result.isEmpty()) {
            return;
        }

        /*
         * Remember the XP before consuming the generic item.
         */
        float experience =
                input.hasTag()
                        ? input.getTag()
                        .getFloat(
                                "CraftingOvenExperience"
                        )
                        : 0.0F;

        /*
         * Consume one prepared food.
         */
        input.shrink(1);

        ItemStack output =
                this.itemHandler
                        .getStackInSlot(
                                SMOKER_OUTPUT_SLOT
                        );

        if (output.isEmpty()) {

            this.itemHandler
                    .setStackInSlot(
                            SMOKER_OUTPUT_SLOT,
                            result.copy()
                    );

        } else {

            output.grow(
                    result.getCount()
            );
        }

        /*
         * Use the same XP-storage system that already
         * works for normal smoked food.
         */
        this.storedExperience +=
                experience;

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