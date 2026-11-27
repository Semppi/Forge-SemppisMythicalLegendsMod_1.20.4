package net.semppi.semppis_mythical_legends_mod.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.entity.player.StackedContents;
import net.semppi.semppis_mythical_legends_mod.client.recipebook.ModRecipeBookTypes;
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
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
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
     * Optional vanilla chest attached to the Crafting Oven's
     * side table.
     *
     * Null means the oven was opened without a supported chest.
     */
    @Nullable
    private final ChestBlockEntity attachedChest;

    private final boolean hasAttachedChest;

    /*
     * Normal vanilla-style crafting table containers.
     */
    private final CraftingContainer craftSlots;

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

    public static final int CHEST_EXTRA_HEIGHT = 72;

    public int getQueuedCraftingCount() {
        return this.ovenData.get(5);
    }

    private int getOvenYOffset() {
        return this.hasAttachedChest
                ? CHEST_EXTRA_HEIGHT
                : 0;
    }

    private boolean updatingCraftingResult = false;

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

        this.attachedChest =
                blockEntity.getAttachedChest();

        this.hasAttachedChest =
                this.attachedChest != null;

        this.craftSlots =
                new PersistentCraftingContainer(
                        this,
                        blockEntity
                );

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

        if (this.hasAttachedChest) {
            addAttachedChestSlots();
        }
    }

    private void addSmokerSlots() {

        /*
         * ------------------------------------------------
         * SMOKER INPUT
         * ------------------------------------------------
         */
        this.addSlot(
                new SlotItemHandler(
                        this.blockEntity.getItemHandler(),
                        CraftingOvenBlockEntity.SMOKER_INPUT_SLOT,
                        SMOKER_INPUT_X,
                        SMOKER_INPUT_Y
                                + getOvenYOffset()
                )
        );

        /*
         * ------------------------------------------------
         * FUEL
         * ------------------------------------------------
         */
        this.addSlot(
                new SlotItemHandler(
                        this.blockEntity.getItemHandler(),
                        CraftingOvenBlockEntity.FUEL_SLOT,
                        FUEL_X,
                        FUEL_Y
                                + getOvenYOffset()
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
         */
        this.addSlot(
                new SlotItemHandler(
                        this.blockEntity.getItemHandler(),
                        CraftingOvenBlockEntity.SMOKER_OUTPUT_SLOT,
                        SMOKER_OUTPUT_X,
                        SMOKER_OUTPUT_Y
                                + getOvenYOffset()
                ) {

                    /*
                     * Players may take cooked food out,
                     * but cannot manually put food in.
                     */
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    /*
                     * Award any stored oven XP when the
                     * player takes cooked food.
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

    private void addAttachedChestSlots() {

        if (this.attachedChest == null) {
            return;
        }

        /*
         * TEMPORARY TEST POSITION.
         *
         * These coordinates intentionally place the chest
         * inventory above the existing Crafting Oven GUI.
         *
         * Once the slots work, we'll design the enlarged PNG
         * and choose their exact final positions.
         */
        int startX = 18;
        int startY = 18;

        /*
         * Vanilla single chest:
         *
         * 3 rows
         * 9 columns
         * 27 total slots
         */
        for (int row = 0; row < 3; row++) {

            for (int column = 0; column < 9; column++) {

                int chestSlot =
                        column
                                + row * 9;

                this.addSlot(
                        new Slot(
                                this.attachedChest,
                                chestSlot,
                                startX
                                        + column * 18,
                                startY
                                        + row * 18
                        )
                );
            }
        }
    }

    public boolean hasAttachedChest() {
        return this.hasAttachedChest;
    }

    @Override
    public void broadcastChanges() {

        /*
         * The Crafting Oven can consume crafting-grid ingredients
         * internally while cooking finishes.
         *
         * Re-check the crafting result during normal menu syncing
         * so the Generic Uncooked Food preview disappears as soon
         * as the final required ingredients are gone.
         */
        if (!this.level.isClientSide
                && !this.updatingCraftingResult) {

            this.updatingCraftingResult = true;

            this.updateCraftingResult();

            this.updatingCraftingResult = false;
        }

        super.broadcastChanges();
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
                                + getOvenYOffset()
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
         * Normal crafting result.
         */
        this.addSlot(
                new ResultSlot(
                        player,
                        this.craftSlots,
                        this.craftResult,
                        0,
                        CRAFT_RESULT_X,
                        CRAFT_RESULT_Y
                                + getOvenYOffset()
                )
        );

        /*
         * Persistent 3 x 3 Crafting Oven grid.
         *
         * These slots deliberately remain editable even
         * while a special recipe is queued.
         *
         * Changing one of them will cancel that queue.
         */
        for (int row = 0; row < 3; row++) {

            for (int column = 0; column < 3; column++) {

                this.addSlot(
                        new Slot(
                                this.craftSlots,
                                column + row * 3,
                                CRAFT_X + column * 18,
                                CRAFT_Y
                                        + getOvenYOffset()
                                        + row * 18
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

            /*
             * The Generic Uncooked Food is only a visual preview.
             *
             * Make its visible name match the food that this
             * Crafting Oven recipe will actually produce.
             *
             * Example:
             * Generic Uncooked Food -> "Pepes Telur Kodok"
             */
            preparedFood.setHoverName(
                    finalFood.getHoverName()
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

    public boolean hasSpecialRecipePreview() {
        return !this.specialResult
                .getItem(0)
                .isEmpty();

    }

    @Override
    public void removed(
            Player player
    ) {
        super.removed(player);

        /*
         * The 3x3 crafting grid now belongs permanently
         * to the Crafting Oven block entity.
         *
         * Closing the GUI must NOT return these items
         * to the player.
         */
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
         * Button 0:
         * normal click = queue one.
         *
         * Button 1:
         * Shift + right click = queue as many as possible.
         */
        if (buttonId != 0
                && buttonId != 1) {

            return false;
        }

        Optional<RecipeHolder<CraftingOvenRecipe>>
                ovenRecipe =
                this.getCraftingOvenRecipe();

        /*
         * The crafting grid must contain a valid
         * Crafting Oven recipe.
         */
        if (ovenRecipe.isEmpty()) {
            return false;
        }

        CraftingOvenRecipe recipe =
                ovenRecipe.get().value();

        ItemStack finalResult =
                recipe.getFinalResult();

        if (finalResult.isEmpty()) {
            return false;
        }

        /*
         * Do not reserve and lock the crafting grid if
         * the cooked-food output cannot accept this recipe.
         */
        if (!this.blockEntity
                .canQueueCraftingRecipeResult(
                        finalResult
                )) {

            return false;
        }

        /*
         * ------------------------------------------------
         * BUTTON 0
         * ------------------------------------------------
         *
         * Queue exactly one additional craft.
         */
        if (buttonId == 0) {

            boolean queued =
                    this.blockEntity
                            .queueCraftingRecipe(
                                    finalResult,
                                    recipe.getCookingTime(),
                                    recipe.getExperience()
                            );

            if (!queued) {
                return false;
            }

            this.broadcastChanges();

            return true;
        }

        /*
         * ------------------------------------------------
         * BUTTON 1
         * ------------------------------------------------
         *
         * Shift + right click:
         * queue as many as the current ingredients allow,
         * up to the hard limit of 64.
         */
        boolean queuedAnything = false;

        while (this.blockEntity
                .getQueuedCraftingCount()
                < CraftingOvenBlockEntity.MAX_QUEUED_CRAFTS) {

            boolean queued =
                    this.blockEntity
                            .queueCraftingRecipe(
                                    finalResult,
                                    recipe.getCookingTime(),
                                    recipe.getExperience()
                            );

            /*
             * queueCraftingRecipe() returns false when:
             *
             * - there aren't enough ingredients for another craft;
             * - the queue has reached 64;
             * - the result does not match the queued recipe.
             */
            if (!queued) {
                break;
            }

            queuedAnything = true;
        }

        if (queuedAnything) {
            this.broadcastChanges();
        }

        return queuedAnything;
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
         * 49     = special Crafting Oven preview
         *
         * 50-76  = attached chest, when present
         *
         * moveItemStackTo() uses an EXCLUSIVE end index.
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

        final int SPECIAL_PREVIEW_SLOT = 49;

        final int CHEST_START = 50;
        final int CHEST_END = 77;

        /*
         * Safety check.
         */
        if (index < 0
                || index >= this.slots.size()) {

            return ItemStack.EMPTY;
        }

        Slot clickedSlot =
                this.slots.get(index);

        if (!clickedSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack clickedStack =
                clickedSlot.getItem();

        ItemStack returnStack =
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

            clickedSlot.onQuickCraft(
                    clickedStack,
                    returnStack
            );
        }

        /*
         * ------------------------------------------------
         * CRAFTING RESULT -> PLAYER
         * ------------------------------------------------
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
         * ATTACHED CHEST -> PLAYER
         * ------------------------------------------------
         *
         * Mimic vanilla chest shift-click behavior:
         * search the player's inventory in reverse order,
         * which tends to fill the hotbar from the right side first.
         */
        else if (this.hasAttachedChest
                && index >= CHEST_START
                && index < CHEST_END) {

            if (!this.moveItemStackTo(
                    clickedStack,
                    PLAYER_INVENTORY_START,
                    HOTBAR_END,
                    true
            )) {
                return ItemStack.EMPTY;
            }
        }

        /*
         * ------------------------------------------------
         * PLAYER INVENTORY / HOTBAR
         * ------------------------------------------------
         */
        else if (index >= PLAYER_INVENTORY_START
                && index < HOTBAR_END) {

            /*
             * ------------------------------------------------
             * CHEST MODE
             * ------------------------------------------------
             *
             * When a chest is attached, shift-click becomes
             * storage-oriented.
             *
             * Do NOT automatically send food to smoker input
             * or fuel to the fuel slot.
             *
             * Everything goes into the attached chest instead.
             */
            if (this.hasAttachedChest) {

                if (!this.moveItemStackTo(
                        clickedStack,
                        CHEST_START,
                        CHEST_END,
                        false
                )) {
                    return ItemStack.EMPTY;
                }
            }

            /*
             * ------------------------------------------------
             * NORMAL OVEN MODE
             * ------------------------------------------------
             *
             * No chest attached:
             * retain the old convenient smoker shift-clicking.
             */
            else {

                /*
                 * First priority:
                 * cookable item -> smoker input.
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
                 * valid fuel -> fuel slot.
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
                 * Otherwise move between player inventory
                 * and hotbar like before.
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
        }

        /*
         * ------------------------------------------------
         * SPECIAL PREVIEW
         * ------------------------------------------------
         *
         * Slot 49 cannot be picked up anyway.
         */
        else if (index == SPECIAL_PREVIEW_SLOT) {
            return ItemStack.EMPTY;
        }

        else {
            return ItemStack.EMPTY;
        }

        /*
         * ------------------------------------------------
         * UPDATE SOURCE SLOT
         * ------------------------------------------------
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

        clickedSlot.onTake(
                player,
                clickedStack
        );

        return returnStack;
    }

    public boolean hasFuelForCooking() {

        /*
         * Fuel is already actively burning.
         */
        if (this.ovenData.get(0) > 0) {
            return true;
        }

        /*
         * Or there is fuel waiting in the oven's fuel slot.
         */
        return !this.blockEntity
                .getItemHandler()
                .getStackInSlot(
                        CraftingOvenBlockEntity.FUEL_SLOT
                )
                .isEmpty();
    }

    private void addPlayerInventory(
            Inventory playerInventory
    ) {

        /*
         * The player's normal 3 x 9 inventory.
         */
        int startX = 18;
        int startY =
                108
                        + getOvenYOffset();

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
        int y =
                166
                        + getOvenYOffset();

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
        return ModRecipeBookTypes.CRAFTING_OVEN;
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

        /*
         * The existing right-facing arrow belongs only
         * to normal smoker cooking.
         *
         * Special Crafting Oven recipes will use their
         * own new left-facing arrow.
         */
        if (this.isCookingCraftingOvenRecipe()) {
            return 0;
        }

        int progress =
                this.ovenData.get(2);

        int total =
                this.ovenData.get(3);

        if (total <= 0 || progress <= 0) {
            return 0;
        }

        return (int) Math.ceil(
                (double) progress * 22.0D
                        / (double) total
        );
    }

    public int getCraftingOvenCookProgress() {

        /*
         * The new left-facing arrow belongs only to
         * queued Crafting Oven recipes.
         */
        if (!this.isCookingCraftingOvenRecipe()) {
            return 0;
        }

        int progress =
                this.ovenData.get(2);

        int total =
                this.ovenData.get(3);

        if (total <= 0 || progress <= 0) {
            return 0;
        }

        /*
         * The complete arrow is 22 pixels wide.
         */
        return (int) Math.ceil(
                (double) progress * 22.0D
                        / (double) total
        );
    }

    public boolean isCookingCraftingOvenRecipe() {
        return this.ovenData.get(4) == 1;
    }

    private static final class PersistentCraftingContainer
            implements CraftingContainer {

        private final CraftingOvenMenu menu;
        private final CraftingOvenBlockEntity blockEntity;

        private PersistentCraftingContainer(
                CraftingOvenMenu menu,
                CraftingOvenBlockEntity blockEntity
        ) {
            this.menu = menu;
            this.blockEntity = blockEntity;
        }

        private int toHandlerSlot(int craftingSlot) {
            return CraftingOvenBlockEntity.CRAFTING_SLOT_START
                    + craftingSlot;
        }

        @Override
        public int getContainerSize() {
            return CraftingOvenBlockEntity.CRAFTING_SLOT_COUNT;
        }

        @Override
        public boolean isEmpty() {
            for (int slot = 0;
                 slot < this.getContainerSize();
                 slot++) {

                if (!this.getItem(slot).isEmpty()) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public ItemStack getItem(int slot) {
            return this.blockEntity
                    .getItemHandler()
                    .getStackInSlot(
                            this.toHandlerSlot(slot)
                    );
        }

        @Override
        public ItemStack removeItem(
                int slot,
                int amount
        ) {

            /*
             * Altering any reserved ingredient cancels
             * the entire custom cooking batch.
             */
            if (this.blockEntity
                    .hasQueuedCraftingRecipe()) {

                this.blockEntity
                        .cancelQueuedCraftingRecipe();
            }

            ItemStack removed =
                    this.blockEntity
                            .getItemHandler()
                            .extractItem(
                                    this.toHandlerSlot(slot),
                                    amount,
                                    false
                            );

            if (!removed.isEmpty()) {
                this.setChanged();
            }

            return removed;
        }

        @Override
        public ItemStack removeItemNoUpdate(
                int slot
        ) {

            if (this.blockEntity
                    .hasQueuedCraftingRecipe()) {

                this.blockEntity
                        .cancelQueuedCraftingRecipe();
            }

            ItemStack current =
                    this.getItem(slot);

            if (current.isEmpty()) {
                return ItemStack.EMPTY;
            }

            return this.blockEntity
                    .getItemHandler()
                    .extractItem(
                            this.toHandlerSlot(slot),
                            current.getCount(),
                            false
                    );
        }

        @Override
        public void setItem(
                int slot,
                ItemStack stack
        ) {

            /*
             * Any manual change to the recipe invalidates
             * the existing reserved batch.
             */
            if (this.blockEntity
                    .hasQueuedCraftingRecipe()) {

                this.blockEntity
                        .cancelQueuedCraftingRecipe();
            }

            this.blockEntity
                    .getItemHandler()
                    .setStackInSlot(
                            this.toHandlerSlot(slot),
                            stack
                    );

            this.setChanged();
        }

        @Override
        public void setChanged() {
            this.blockEntity.setChanged();

            this.menu.slotsChanged(this);
        }

        @Override
        public boolean stillValid(Player player) {
            return this.menu.stillValid(player);
        }

        @Override
        public void clearContent() {
            for (int slot = 0;
                 slot < this.getContainerSize();
                 slot++) {

                this.blockEntity
                        .getItemHandler()
                        .setStackInSlot(
                                this.toHandlerSlot(slot),
                                ItemStack.EMPTY
                        );
            }

            this.setChanged();
        }

        @Override
        public int getWidth() {
            return 3;
        }

        @Override
        public int getHeight() {
            return 3;
        }

        @Override
        public List<ItemStack> getItems() {
            List<ItemStack> items =
                    new ArrayList<>(
                            this.getContainerSize()
                    );

            for (int slot = 0;
                 slot < this.getContainerSize();
                 slot++) {

                items.add(
                        this.getItem(slot)
                );
            }

            return items;
        }

        @Override
        public void fillStackedContents(
                StackedContents stackedContents
        ) {
            for (int slot = 0;
                 slot < this.getContainerSize();
                 slot++) {

                stackedContents.accountStack(
                        this.getItem(slot)
                );
            }
        }
    }
}