package net.semppi.semppis_mythical_legends_mod.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.entity.player.StackedContents;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.recipe.CraftingOvenRecipe;
import net.semppi.semppis_mythical_legends_mod.recipe.ModRecipeTypes;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.SlotItemHandler;
import net.semppi.semppis_mythical_legends_mod.block.entity.CraftingOvenBlockEntity;

import java.util.Optional;

public class CraftingOvenMenu
        extends RecipeBookMenu<CraftingContainer> {

    private final CraftingOvenBlockEntity blockEntity;
    private final Level level;
    private final ContainerData ovenData;

    /*
     * Normal vanilla-style crafting table containers.
     */
    private final CraftingContainer craftSlots =
            new TransientCraftingContainer(
                    this,
                    3,
                    3
            );

    private final ResultContainer craftResult =
            new ResultContainer();

    /*
     * Special Crafting Oven result.
     *
     * For now this only displays the generic uncooked
     * food icon when an oven-only recipe matches.
     */
    private final ResultContainer specialResult =
            new ResultContainer();

    /*
     * ------------------------------------------------
     * GUI COORDINATES
     * ------------------------------------------------
     */

    public static final int SMOKER_INPUT_X = 18;
    public static final int SMOKER_INPUT_Y = 19;

    public static final int FUEL_X = 18;
    public static final int FUEL_Y = 55;

    public static final int SMOKER_OUTPUT_X = 76;
    public static final int SMOKER_OUTPUT_Y = 20;

    public static final int CRAFT_X = 72;
    public static final int CRAFT_Y = 48;

    public static final int CRAFT_RESULT_X = 168;
    public static final int CRAFT_RESULT_Y = 65;

    public static final int SPECIAL_BUTTON_X = 152;
    public static final int SPECIAL_BUTTON_Y = 14;

    public static final int SPECIAL_PREVIEW_X = 168;
    public static final int SPECIAL_PREVIEW_Y = 28;

    public CraftingOvenMenu(
            int containerId,
            Inventory playerInventory,
            FriendlyByteBuf extraData
    ) {
        this(
                containerId,
                playerInventory,
                (CraftingOvenBlockEntity)
                        playerInventory.player.level()
                                .getBlockEntity(
                                        extraData.readBlockPos()
                                )
        );
    }

    public CraftingOvenMenu(
            int containerId,
            Inventory playerInventory,
            CraftingOvenBlockEntity blockEntity
    ) {
        super(
                ModMenuTypes.CRAFTING_OVEN_MENU.get(),
                containerId
        );

        this.blockEntity =
                blockEntity;

        this.level =
                playerInventory.player.level();

        /*
         * Connect the block entity's four smoker values
         * to this menu:
         *
         * 0 = remaining fuel burn time
         * 1 = total fuel burn time
         * 2 = current cooking progress
         * 3 = total cooking time
         */
        this.ovenData =
                blockEntity.getData();

        this.addDataSlots(
                this.ovenData
        );

        addCraftingSlots(
                playerInventory.player
        );

        addSmokerSlots();

        addPlayerInventory(
                playerInventory
        );

        addPlayerHotbar(
                playerInventory
        );

        addSpecialPreviewSlot();
    }

    private void addSmokerSlots() {

        /*
         * ------------------------------------------------
         * SMOKER INPUT
         * ------------------------------------------------
         *
         * For now this is a normal input slot.
         * The server will simply refuse to cook anything
         * that has no minecraft:smoking recipe.
         */
        this.addSlot(
                new SlotItemHandler(
                        this.blockEntity.getItemHandler(),
                        CraftingOvenBlockEntity.SMOKER_INPUT_SLOT,
                        SMOKER_INPUT_X,
                        SMOKER_INPUT_Y
                )
        );

        /*
         * ------------------------------------------------
         * FUEL
         * ------------------------------------------------
         *
         * Only actual furnace/smoker fuel may be inserted.
         */
        this.addSlot(
                new SlotItemHandler(
                        this.blockEntity.getItemHandler(),
                        CraftingOvenBlockEntity.FUEL_SLOT,
                        FUEL_X,
                        FUEL_Y
                ) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return ForgeHooks.getBurnTime(
                                stack,
                                RecipeType.SMOKING
                        ) > 0;
                    }
                }
        );

        /*
         * ------------------------------------------------
         * SMOKER OUTPUT
         * ------------------------------------------------
         *
         * Players may remove cooked food,
         * but cannot manually put items into this slot.
         */
        this.addSlot(
                new SlotItemHandler(
                        this.blockEntity.getItemHandler(),
                        CraftingOvenBlockEntity.SMOKER_OUTPUT_SLOT,
                        SMOKER_OUTPUT_X,
                        SMOKER_OUTPUT_Y
                ) {

                    /*
                     * Nothing may be manually inserted into
                     * the cooked-food output slot.
                     */
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    /*
                     * When the player takes cooked food,
                     * release all cooking XP stored by the oven.
                     */
                    @Override
                    public void onTake(
                            Player player,
                            ItemStack stack
                    ) {

                        super.onTake(
                                player,
                                stack
                        );

                        CraftingOvenMenu.this
                                .blockEntity
                                .awardStoredExperience(
                                        player
                                );
                    }
                }
        );
    }

    private void addSpecialPreviewSlot() {

        /*
         * ------------------------------------------------
         * SPECIAL CRAFTING OVEN PREVIEW
         * ------------------------------------------------
         *
         * This is the upper result slot.
         *
         * For this first version:
         *
         * - the oven can place an icon here;
         * - the player cannot put anything into it;
         * - the player cannot take anything out of it yet.
         *
         * Later, the small button beside this slot will
         * send the prepared food to the cooking section.
         */
        this.addSlot(
                new Slot(
                        this.specialResult,
                        0,
                        SPECIAL_PREVIEW_X,
                        SPECIAL_PREVIEW_Y
                ) {

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public boolean mayPickup(Player player) {
                        return false;
                    }
                }
        );
    }

    private void addCraftingSlots(
            Player player
    ) {

        /*
         * Result slot first.
         *
         * This mirrors vanilla crafting menus internally
         * and makes recipe-book placement much cleaner.
         */
        this.addSlot(
                new ResultSlot(
                        player,
                        this.craftSlots,
                        this.craftResult,
                        0,
                        CRAFT_RESULT_X,
                        CRAFT_RESULT_Y
                )
        );

        /*
         * Then the 3 x 3 crafting grid.
         * These become menu slots 1 through 9.
         */
        for (int row = 0; row < 3; row++) {

            for (int column = 0; column < 3; column++) {

                this.addSlot(
                        new Slot(
                                this.craftSlots,
                                column + row * 3,
                                CRAFT_X + column * 18,
                                CRAFT_Y + row * 18
                        )
                );
            }
        }
    }

    @Override
    public void slotsChanged(
            net.minecraft.world.Container container
    ) {
        super.slotsChanged(container);

        if (container == this.craftSlots) {
            updateCraftingResult();
        }
    }

    /*
     * ------------------------------------------------
     * FIRST CRAFTING OVEN-ONLY RECIPE
     * ------------------------------------------------
     */

    private Optional<RecipeHolder<CraftingOvenRecipe>>
    getCraftingOvenRecipe() {

        return this.level
                .getRecipeManager()
                .getRecipeFor(
                        ModRecipeTypes
                                .CRAFTING_OVEN_TYPE
                                .get(),
                        this.craftSlots,
                        this.level
                );
    }

    private void updateCraftingResult() {

        if (this.level.isClientSide) {
            return;
        }

        /*
         * ------------------------------------------------
         * CRAFTING OVEN RECIPE
         * ------------------------------------------------
         */
        Optional<RecipeHolder<CraftingOvenRecipe>>
                ovenRecipe =
                getCraftingOvenRecipe();

        if (ovenRecipe.isPresent()) {

            CraftingOvenRecipe recipe =
                    ovenRecipe.get()
                            .value();

            /*
             * Crafting Oven recipes use the special
             * upper result, not the normal result slot.
             */
            this.craftResult.setItem(
                    0,
                    ItemStack.EMPTY
            );

            ItemStack finalFood =
                    recipe.getFinalResult();

            ItemStack preparedFood =
                    new ItemStack(
                            ModItems.GENERIC_UNCOOKED_FOOD.get()
                    );

            ResourceLocation finalFoodId =
                    BuiltInRegistries.ITEM
                            .getKey(
                                    finalFood.getItem()
                            );

            /*
             * Write the recipe information into this
             * individual Generic Uncooked Food stack.
             */
            preparedFood.getOrCreateTag()
                    .putString(
                            "CraftingOvenResult",
                            finalFoodId.toString()
                    );

            preparedFood.getOrCreateTag()
                    .putInt(
                            "CraftingOvenResultCount",
                            finalFood.getCount()
                    );

            preparedFood.getOrCreateTag()
                    .putInt(
                            "CraftingOvenCookTime",
                            recipe.getCookingTime()
                    );

            preparedFood.getOrCreateTag()
                    .putFloat(
                            "CraftingOvenExperience",
                            recipe.getExperience()
                    );

            this.specialResult.setItem(
                    0,
                    preparedFood
            );

            this.broadcastChanges();

            return;
        }

        /*
         * No Crafting Oven recipe matches.
         */
        this.specialResult.setItem(
                0,
                ItemStack.EMPTY
        );

        /*
         * ------------------------------------------------
         * NORMAL CRAFTING TABLE RECIPE
         * ------------------------------------------------
         */
        Optional<RecipeHolder<CraftingRecipe>>
                normalRecipe =
                this.level
                        .getRecipeManager()
                        .getRecipeFor(
                                RecipeType.CRAFTING,
                                this.craftSlots,
                                this.level
                        );

        ItemStack result =
                ItemStack.EMPTY;

        if (normalRecipe.isPresent()) {

            result =
                    normalRecipe.get()
                            .value()
                            .assemble(
                                    this.craftSlots,
                                    this.level
                                            .registryAccess()
                            );
        }

        this.craftResult.setItem(
                0,
                result
        );

        this.broadcastChanges();
    }

    @Override
    public void removed(
            Player player
    ) {
        super.removed(player);

        /*
         * Behave like a crafting table:
         * return unused crafting ingredients when the GUI closes.
         */
        this.clearContainer(
                player,
                this.craftSlots
        );
    }

    @Override
    public boolean stillValid(
            Player player
    ) {
        return !this.blockEntity.isRemoved()
                && player.distanceToSqr(
                this.blockEntity.getBlockPos().getX() + 0.5D,
                this.blockEntity.getBlockPos().getY() + 0.5D,
                this.blockEntity.getBlockPos().getZ() + 0.5D
        ) <= 64.0D;
    }

    /*
     * We will implement proper shift-click routing after
     * the basic GUI is visible and working.
     */

    /*
     * Checks whether this item has a valid smoker recipe.
     *
     * In simple terms:
     * "Can the Crafting Oven actually cook this?"
     */
    private boolean canSmoke(
            ItemStack stack
    ) {

        if (stack.isEmpty()) {
            return false;
        }

        return this.level
                .getRecipeManager()
                .getRecipeFor(
                        RecipeType.SMOKING,
                        new net.minecraft.world.SimpleContainer(
                                stack.copyWithCount(1)
                        ),
                        this.level
                )
                .isPresent();
    }

    @Override
    public boolean clickMenuButton(
            Player player,
            int buttonId
    ) {

        /*
         * Button 0 = send the prepared Crafting Oven
         * recipe into the cooking section.
         */
        if (buttonId != 0) {
            return false;
        }

        Optional<RecipeHolder<CraftingOvenRecipe>>
                ovenRecipe =
                getCraftingOvenRecipe();

        /*
         * The grid must still contain a valid
         * Crafting Oven recipe.
         */
        if (ovenRecipe.isEmpty()) {
            return false;
        }

        ItemStack preparedFood =
                this.specialResult.getItem(0);

        if (preparedFood.isEmpty()) {
            return false;
        }

        /*
         * For now we retain your existing behavior:
         * wait until the smoker input is free.
         */
        ItemStack smokerInput =
                this.blockEntity
                        .getItemHandler()
                        .getStackInSlot(
                                CraftingOvenBlockEntity
                                        .SMOKER_INPUT_SLOT
                        );

        if (!smokerInput.isEmpty()) {
            return false;
        }

        /*
         * Move a COPY of the special prepared food into
         * the real persistent smoker input.
         *
         * copy() preserves all of its result/time/XP data.
         */
        this.blockEntity
                .getItemHandler()
                .setStackInSlot(
                        CraftingOvenBlockEntity
                                .SMOKER_INPUT_SLOT,
                        preparedFood.copy()
                );

        /*
         * Because this first Crafting Oven recipe type is
         * shapeless and matches one ingredient per occupied
         * grid slot, consume one item from every occupied
         * crafting slot.
         */
        for (int slot = 0;
             slot < this.craftSlots.getContainerSize();
             slot++) {

            ItemStack stack =
                    this.craftSlots.getItem(slot);

            if (!stack.isEmpty()) {
                stack.shrink(1);
            }
        }

        /*
         * Recalculate the preview/result after consuming
         * the ingredients.
         */
        this.slotsChanged(
                this.craftSlots
        );

        this.broadcastChanges();

        return true;
    }

    @Override
    public ItemStack quickMoveStack(
            Player player,
            int index
    ) {

        /*
         * ------------------------------------------------
         * MENU SLOT NUMBERS
         * ------------------------------------------------
         *
         * Because of the order our slots are added:
         *
         * 0      = crafting result
         * 1-9    = crafting grid
         *
         * 10     = smoker input
         * 11     = fuel
         * 12     = smoker output
         *
         * 13-39  = player's main inventory
         * 40-48  = player's hotbar
         *
         * moveItemStackTo uses the END number as exclusive,
         * meaning "stop before this number".
         */

        final int CRAFT_RESULT_SLOT = 0;

        final int CRAFT_GRID_START = 1;
        final int CRAFT_GRID_END = 10;

        final int SMOKER_INPUT_SLOT = 10;
        final int FUEL_SLOT = 11;
        final int SMOKER_OUTPUT_SLOT = 12;

        final int PLAYER_INVENTORY_START = 13;
        final int PLAYER_INVENTORY_END = 40;

        final int HOTBAR_START = 40;
        final int HOTBAR_END = 49;

        ItemStack returnStack =
                ItemStack.EMPTY;

        Slot clickedSlot =
                this.slots.get(index);

        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack clickedStack =
                clickedSlot.getItem();

        returnStack =
                clickedStack.copy();

        /*
         * ------------------------------------------------
         * SMOKER OUTPUT -> PLAYER
         * ------------------------------------------------
         */
        if (index == SMOKER_OUTPUT_SLOT) {

            if (!this.moveItemStackTo(
                    clickedStack,
                    PLAYER_INVENTORY_START,
                    HOTBAR_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }

            /*
             * Needed for output-slot behavior such as
             * awarding cooking experience.
             */
            clickedSlot.onQuickCraft(
                    clickedStack,
                    returnStack
            );
        }

        /*
         * ------------------------------------------------
         * CRAFTING RESULT -> PLAYER
         * ------------------------------------------------
         *
         * Keep normal crafting-result shift-click behavior.
         *
         * We are NOT auto-filling the crafting grid.
         */
        else if (index == CRAFT_RESULT_SLOT) {

            if (!this.moveItemStackTo(
                    clickedStack,
                    PLAYER_INVENTORY_START,
                    HOTBAR_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }

            clickedSlot.onQuickCraft(
                    clickedStack,
                    returnStack
            );
        }

        /*
         * ------------------------------------------------
         * SMOKER INPUT / FUEL -> PLAYER
         * ------------------------------------------------
         */
        else if (index == SMOKER_INPUT_SLOT
                || index == FUEL_SLOT) {

            if (!this.moveItemStackTo(
                    clickedStack,
                    PLAYER_INVENTORY_START,
                    HOTBAR_END,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        /*
         * ------------------------------------------------
         * CRAFTING GRID -> PLAYER
         * ------------------------------------------------
         *
         * Shift-clicking something OUT of the crafting
         * grid is allowed.
         *
         * We simply never automatically put things INTO it.
         */
        else if (index >= CRAFT_GRID_START
                && index < CRAFT_GRID_END) {

            if (!this.moveItemStackTo(
                    clickedStack,
                    PLAYER_INVENTORY_START,
                    HOTBAR_END,
                    false
            )) {
                return ItemStack.EMPTY;
            }
        }

        /*
         * ------------------------------------------------
         * PLAYER INVENTORY / HOTBAR -> OVEN
         * ------------------------------------------------
         */
        else if (index >= PLAYER_INVENTORY_START
                && index < HOTBAR_END) {

            /*
             * First priority:
             * If this item has a smoking recipe,
             * send it to the smoker input.
             */
            if (canSmoke(clickedStack)) {

                if (!this.moveItemStackTo(
                        clickedStack,
                        SMOKER_INPUT_SLOT,
                        SMOKER_INPUT_SLOT + 1,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            }

            /*
             * Second priority:
             * Valid furnace/smoker fuel goes to fuel.
             */
            else if (ForgeHooks.getBurnTime(
                    clickedStack,
                    RecipeType.SMOKING
            ) > 0) {

                if (!this.moveItemStackTo(
                        clickedStack,
                        FUEL_SLOT,
                        FUEL_SLOT + 1,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            }

            /*
             * Not cookable and not fuel.
             *
             * Move between main inventory and hotbar,
             * exactly like vanilla menus generally do.
             */
            else if (index >= PLAYER_INVENTORY_START
                    && index < PLAYER_INVENTORY_END) {

                if (!this.moveItemStackTo(
                        clickedStack,
                        HOTBAR_START,
                        HOTBAR_END,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            }

            else if (index >= HOTBAR_START
                    && index < HOTBAR_END) {

                if (!this.moveItemStackTo(
                        clickedStack,
                        PLAYER_INVENTORY_START,
                        PLAYER_INVENTORY_END,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            }
        }

        /*
         * Update or clear the source slot after movement.
         */
        if (clickedStack.isEmpty()) {

            clickedSlot.set(
                    ItemStack.EMPTY
            );

        } else {

            clickedSlot.setChanged();
        }

        /*
         * Nothing actually moved.
         */
        if (clickedStack.getCount()
                == returnStack.getCount()) {

            return ItemStack.EMPTY;
        }

        /*
         * Important:
         * lets special slots react when their item
         * has been taken.
         *
         * Your smoker output uses this for XP.
         */
        clickedSlot.onTake(
                player,
                clickedStack
        );

        return returnStack;
    }

    private void addPlayerInventory(
            Inventory playerInventory
    ) {

        /*
         * The player's normal 3 x 9 inventory.
         */
        int startX = 18;
        int startY = 108;

        for (int row = 0; row < 3; row++) {

            for (int column = 0; column < 9; column++) {

                this.addSlot(
                        new Slot(
                                playerInventory,
                                column + row * 9 + 9,
                                startX + column * 18,
                                startY + row * 18
                        )
                );
            }
        }
    }

    private void addPlayerHotbar(
            Inventory playerInventory
    ) {
        int startX = 18;
        int y = 166;

        for (int column = 0; column < 9; column++) {
            this.addSlot(
                    new Slot(
                            playerInventory,
                            column,
                            startX + column * 18,
                            y
                    )
            );
        }
    }

    @Override
    public void fillCraftSlotsStackedContents(
            StackedContents stackedContents
    ) {
        this.craftSlots.fillStackedContents(
                stackedContents
        );
    }

    @Override
    public void clearCraftingContent() {

        this.craftSlots.clearContent();

        this.craftResult.clearContent();
    }

    @Override
    public boolean recipeMatches(
            RecipeHolder<? extends Recipe<CraftingContainer>> recipe
    ) {
        return recipe.value().matches(
                this.craftSlots,
                this.level
        );
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return 3;
    }

    @Override
    public int getGridHeight() {
        return 3;
    }

    @Override
    public int getSize() {

        /*
         * Result + 9 crafting inputs.
         *
         * The smoker slots are deliberately NOT part
         * of the recipe-book crafting area.
         */
        return 10;
    }

    public CraftingContainer getCraftSlots() {
        return this.craftSlots;
    }

    @Override
    public RecipeBookType getRecipeBookType() {

        /*
         * Use exactly the same recipe-book category
         * as a normal crafting table.
         */
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(
            int slotIndex
    ) {

        /*
         * Recipe-book crafting area:
         *
         * 0 = result
         * 1-9 = crafting grid
         */
        return slotIndex >= 0
                && slotIndex < 10;
    }

    public boolean isBurning() {
        return this.ovenData.get(0) > 0;
    }

    public int getBurnProgress() {

        int remaining =
                this.ovenData.get(0);

        int total =
                this.ovenData.get(1);

        if (remaining <= 0 || total <= 0) {
            return 0;
        }

        /*
         * Convert the remaining fuel into the 13 pixels
         * available in the flame sprite.
         *
         * Math.ceil rounds UP instead of down.
         * Therefore, as long as the oven is still burning,
         * at least one pixel of flame remains visible.
         */
        return (int) Math.ceil(
                (double) remaining * 13.0D
                        / (double) total
        );
    }

    public int getCookProgress() {

        int progress =
                this.ovenData.get(2);

        int total =
                this.ovenData.get(3);

        if (total <= 0 || progress <= 0) {
            return 0;
        }

        /*
         * The arrow is 22 pixels wide.
         *
         * Math.ceil rounds upward so the final part of
         * the cooking cycle does not lose the last pixel.
         */
        return (int) Math.ceil(
                (double) progress * 22.0D
                        / (double) total
        );
    }
}