package net.semppi.semppis_mythical_legends_mod.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.semppi.semppis_mythical_legends_mod.menu.CraftingOvenMenu;
import net.minecraft.client.gui.screens.Screen;

public class CraftingOvenScreen
        extends AbstractContainerScreen<CraftingOvenMenu>
        implements RecipeUpdateListener {

    private static final ResourceLocation WIDGETS =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/gui/crafting_oven_widgets.png"
            );

    private static final ResourceLocation NORMAL_TEXTURE =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/gui/crafting_oven_menu.png"
            );

    private static final ResourceLocation CHEST_TEXTURE =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/gui/crafting_oven_menu_chest.png"
            );

    private static final int FLAME_X = 20;
    private static final int FLAME_Y = 37;

    private static final int ARROW_X = 39;
    private static final int ARROW_Y = 28;

    /*
     * Custom Crafting Oven cooking arrow.
     *
     * This arrow points left, from the prepared-food side
     * toward the cooked-food output.
     */
    private static final int CRAFTING_ARROW_X = 122;
    private static final int CRAFTING_ARROW_Y = 19;

    private static final int CRAFTING_ARROW_U = 69;
    private static final int CRAFTING_ARROW_V = 8;

    private static final int CRAFTING_ARROW_WIDTH = 22;
    private static final int CRAFTING_ARROW_HEIGHT = 16;

    private static final int BASE_IMAGE_WIDTH = 198;
    private static final int BASE_IMAGE_HEIGHT = 190;

    @Override
    protected void init() {

        super.init();

        /*
         * Same narrow-screen logic used by vanilla
         * recipe-book screens.
         */
        this.widthTooNarrow =
                this.width < 379;

        /*
         * Connect Minecraft's normal crafting recipe book
         * to our CraftingOvenMenu.
         */
        this.recipeBookComponent.init(
                this.width,
                this.height,
                this.minecraft,
                this.widthTooNarrow,
                this.menu
        );

        /*
         * Opening the recipe book may shift the complete
         * Crafting Oven GUI horizontally.
         */
        updateScreenPosition();

        ImageButton recipeButton =
                new ImageButton(
                        this.leftPos + RECIPE_BOOK_BUTTON_X,
                        this.topPos
                                + getOvenYOffset()
                                + RECIPE_BOOK_BUTTON_Y,
                        20,
                        18,
                        RecipeBookComponent.RECIPE_BUTTON_SPRITES,
                        button -> {

                            this.recipeBookComponent
                                    .toggleVisibility();

                            updateScreenPosition();

                            /*
                             * Keep our button attached to the oven
                             * after the whole GUI moves.
                             */
                            button.setPosition(
                                    this.leftPos
                                            + RECIPE_BOOK_BUTTON_X,
                                    this.topPos
                                            + getOvenYOffset()
                                            + RECIPE_BOOK_BUTTON_Y
                            );
                        }
                );

        this.addRenderableWidget(
                recipeButton
        );

        this.addWidget(
                this.recipeBookComponent
        );
    }

    private final RecipeBookComponent recipeBookComponent =
            new RecipeBookComponent();

    private boolean widthTooNarrow;

    /*
     * Short client-side timer for the
     * "you forgot fuel" visual pulse.
     */
    private int fuelWarningTicks = 0;

    /*
     * Position of the vanilla recipe-book button
     * relative to our GUI texture.
     *
     * This is deliberately centralized so we can
     * move it after your first visual test.
     */

    public CraftingOvenScreen(
            CraftingOvenMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);

        this.imageWidth =
                BASE_IMAGE_WIDTH;

        int ovenYOffset =
                menu.hasAttachedChest()
                        ? CraftingOvenMenu.CHEST_EXTRA_HEIGHT
                        : 0;

        this.imageHeight =
                BASE_IMAGE_HEIGHT
                        + ovenYOffset;

        /*
         * These belong to the normal oven section,
         * so move them downward when the chest panel exists.
         */
        this.titleLabelY =
                6 + ovenYOffset;

        this.inventoryLabelY =
                96 + ovenYOffset;
    }

    @Override
    protected void containerTick() {

        super.containerTick();

        this.recipeBookComponent.tick();

        /*
         * Count down the temporary fuel warning.
         */
        if (this.fuelWarningTicks > 0) {
            this.fuelWarningTicks--;
        }
    }

    @Override
    protected void renderBg(
            GuiGraphics guiGraphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {
        int left = this.leftPos;
        int top = this.topPos;

        int ovenYOffset =
                getOvenYOffset();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        /*
         * ------------------------------------------------
         * BACKGROUND
         * ------------------------------------------------
         *
         * No chest:
         * use the normal 198 x 190 oven texture.
         *
         * Chest:
         * use the new 198 x 262 texture containing
         * the three chest rows above the oven.
         */
        ResourceLocation texture =
                this.menu.hasAttachedChest()
                        ? CHEST_TEXTURE
                        : NORMAL_TEXTURE;

        guiGraphics.blit(
                texture,
                left,
                top,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                this.imageWidth,
                this.imageHeight
        );

        /*
         * ------------------------------------------------
         * FUEL FLAME
         * ------------------------------------------------
         */
        int flamePixels =
                this.menu.getBurnProgress();

        if (flamePixels > 0) {

            guiGraphics.blit(
                    WIDGETS,
                    left + 18,
                    top
                            + ovenYOffset
                            + 37
                            + (13 - flamePixels),
                    5,
                    9 + (13 - flamePixels),
                    13,
                    flamePixels,
                    94,
                    32
            );
        }

        /*
         * ------------------------------------------------
         * NORMAL SMOKER ARROW
         * ------------------------------------------------
         */
        int arrowPixels =
                this.menu.getCookProgress();

        if (arrowPixels > 0) {

            guiGraphics.blit(
                    WIDGETS,
                    left + 40,
                    top
                            + ovenYOffset
                            + 19,
                    39,
                    8,
                    arrowPixels,
                    16,
                    94,
                    32
            );
        }

        /*
         * ------------------------------------------------
         * SPECIAL CRAFTING OVEN ARROW
         * ------------------------------------------------
         */
        int craftingArrowPixels =
                this.menu.getCraftingOvenCookProgress();

        if (craftingArrowPixels > 0) {

            int hiddenPixels =
                    CRAFTING_ARROW_WIDTH
                            - craftingArrowPixels;

            guiGraphics.blit(
                    WIDGETS,
                    left
                            + CRAFTING_ARROW_X
                            + hiddenPixels,
                    top
                            + ovenYOffset
                            + CRAFTING_ARROW_Y,
                    CRAFTING_ARROW_U
                            + hiddenPixels,
                    CRAFTING_ARROW_V,
                    craftingArrowPixels,
                    CRAFTING_ARROW_HEIGHT,
                    94,
                    32
            );
        }
    }

    @Override
    protected void renderLabels(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY
    ) {
        /*
         * Chest mode has its own label at the top.
         */
        if (this.menu.hasAttachedChest()) {

            guiGraphics.drawString(
                    this.font,
                    Component.translatable(
                            "container.chest"
                    ),
                    18,
                    6,
                    0x404040,
                    false
            );
        }

        /*
         * Crafting Oven title.
         */
        guiGraphics.drawString(
                this.font,
                this.title,
                this.titleLabelX,
                this.titleLabelY,
                0x404040,
                false
        );

        /*
         * Player inventory title.
         */
        guiGraphics.drawString(
                this.font,
                this.playerInventoryTitle,
                this.inventoryLabelX,
                this.inventoryLabelY,
                0x404040,
                false
        );
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        this.renderBackground(
                guiGraphics,
                mouseX,
                mouseY,
                partialTick
        );

        /*
         * Narrow-screen behavior:
         *
         * Recipe book overlays the normal menu.
         */
        if (this.recipeBookComponent.isVisible()
                && this.widthTooNarrow) {

            this.renderBg(
                    guiGraphics,
                    partialTick,
                    mouseX,
                    mouseY
            );

            this.recipeBookComponent.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

        } else {

            /*
             * Normal desktop behavior.
             *
             * Recipe book appears beside the oven and the
             * complete oven menu shifts horizontally.
             */
            super.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

            this.recipeBookComponent.render(
                    guiGraphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

            /*
             * Displays ghost ingredients when a recipe
             * has been selected from the book.
             */
            this.recipeBookComponent.renderGhostRecipe(
                    guiGraphics,
                    this.leftPos,
                    this.topPos,
                    true,
                    partialTick
            );
        }

        /*
         * Draw our small visual helpers over the main GUI.
         */
        this.renderQueuedCraftingCount(
                guiGraphics
        );

        this.renderFuelWarning(
                guiGraphics,
                partialTick
        );

        this.renderTooltip(
                guiGraphics,
                mouseX,
                mouseY
        );

        this.recipeBookComponent.renderTooltip(
                guiGraphics,
                this.leftPos,
                this.topPos,
                mouseX,
                mouseY
        );
    }

    private void renderQueuedCraftingCount(
            GuiGraphics guiGraphics
    ) {

        int queuedCount =
                this.menu.getQueuedCraftingCount();

        /*
         * Zero means nothing is queued,
         * so don't clutter the GUI with "0".
         */
        if (queuedCount <= 1) {
            return;
        }

        String text =
                Integer.toString(
                        queuedCount
                );

        /*
         * Center the number above the generic-food
         * preview slot.
         */
        int x =
                this.leftPos
                        + CraftingOvenMenu.SPECIAL_PREVIEW_X
                        + 8;

        int y =
                this.topPos
                        + getOvenYOffset()
                        + CraftingOvenMenu.SPECIAL_PREVIEW_Y
                        - 10;

        guiGraphics.drawCenteredString(
                this.font,
                text,
                x,
                y,
                0xFFFFFFFF
        );
    }

    private int getOvenYOffset() {
        return this.menu.hasAttachedChest()
                ? CraftingOvenMenu.CHEST_EXTRA_HEIGHT
                : 0;
    }

    private static final int RECIPE_BOOK_BUTTON_X = 41;
    private static final int RECIPE_BOOK_BUTTON_Y = 76;
    private static final int RECIPE_BOOK_MENU_SHIFT = 10;

    private void updateScreenPosition() {
        this.leftPos = this.recipeBookComponent.updateScreenPosition(
                this.width,
                this.imageWidth
        );

        if (this.recipeBookComponent.isVisible()) {
            this.leftPos += RECIPE_BOOK_MENU_SHIFT;
        }
    }

    @Override
    protected boolean isHovering(
            int x,
            int y,
            int width,
            int height,
            double mouseX,
            double mouseY
    ) {
        return (!this.widthTooNarrow
                || !this.recipeBookComponent.isVisible())
                && super.isHovering(
                x,
                y,
                width,
                height,
                mouseX,
                mouseY
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        /*
         * ------------------------------------------------
         * SPECIAL CRAFTING OVEN BUTTON
         * ------------------------------------------------
         */
        int specialButtonX =
                this.leftPos
                        + CraftingOvenMenu.SPECIAL_BUTTON_X;

        int specialButtonY =
                this.topPos
                        + getOvenYOffset()
                        + CraftingOvenMenu.SPECIAL_BUTTON_Y;

        int specialButtonSize = 13;

        boolean mouseOverSpecialButton =
                mouseX >= specialButtonX
                        && mouseX < specialButtonX
                        + specialButtonSize
                        && mouseY >= specialButtonY
                        && mouseY < specialButtonY
                        + specialButtonSize;

        /*
         * Shift + right click:
         * queue as many copies of this recipe as the
         * current ingredients allow, up to 64.
         */
        if (button == 0
                && Screen.hasShiftDown()
                && mouseOverSpecialButton) {

            if (this.menu.hasSpecialRecipePreview()
                    && !this.menu.hasFuelForCooking()) {

                this.fuelWarningTicks = 30;
            }

            if (this.minecraft != null
                    && this.minecraft.gameMode != null) {

                this.minecraft.gameMode
                        .handleInventoryButtonClick(
                                this.menu.containerId,
                                1
                        );
            }

            return true;
        }

        /*
         * Only allow clicking the oven button when the
         * recipe book is not covering the menu.
         */
        if (button == 0
                && !Screen.hasShiftDown()
                && mouseOverSpecialButton
                && (!this.widthTooNarrow
                || !this.recipeBookComponent.isVisible())) {

            if (this.menu.hasSpecialRecipePreview()
                    && !this.menu.hasFuelForCooking()) {

                this.fuelWarningTicks = 30;
            }

            if (this.minecraft != null
                    && this.minecraft.gameMode != null) {

                this.minecraft.gameMode
                        .handleInventoryButtonClick(
                                this.menu.containerId,
                                0
                        );
            }

            return true;
        }

        /*
         * ------------------------------------------------
         * VANILLA RECIPE BOOK CLICKING
         * ------------------------------------------------
         */
        if (this.recipeBookComponent.mouseClicked(
                mouseX,
                mouseY,
                button
        )) {

            this.setFocused(
                    this.recipeBookComponent
            );

            return true;
        }

        return (this.widthTooNarrow
                && this.recipeBookComponent.isVisible())
                || super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    private void renderFuelWarning(
            GuiGraphics guiGraphics,
            float partialTick
    ) {

        if (this.fuelWarningTicks <= 0) {
            return;
        }

        /*
         * Oscillate between dim and bright.
         */
        float time =
                this.fuelWarningTicks
                        - partialTick;

        float pulse =
                (float) (
                        (Math.sin(time * 0.65F) + 1.0D)
                                * 0.5D
                );

        int alpha =
                90
                        + (int) (
                        pulse * 150.0F
                );

        int color =
                (alpha << 24)
                        | 0x00FFB52E;

        /*
         * Slots are visually 18 x 18:
         * 16 item pixels plus the surrounding border.
         */
        int x =
                this.leftPos
                        + CraftingOvenMenu.FUEL_X
                        - 1;

        int y =
                this.topPos
                        + getOvenYOffset()
                        + CraftingOvenMenu.FUEL_Y
                        - 1;

        guiGraphics.renderOutline(
                x,
                y,
                18,
                18,
                color
        );
    }

    @Override
    protected boolean hasClickedOutside(
            double mouseX,
            double mouseY,
            int left,
            int top,
            int button
    ) {

        boolean outsideMenu =
                mouseX < left
                        || mouseY < top
                        || mouseX >= left + this.imageWidth
                        || mouseY >= top + this.imageHeight;

        return outsideMenu
                && this.recipeBookComponent.hasClickedOutside(
                mouseX,
                mouseY,
                this.leftPos,
                this.topPos,
                this.imageWidth,
                this.imageHeight,
                button
        );
    }

    @Override
    protected void slotClicked(
            Slot slot,
            int slotId,
            int mouseButton,
            ClickType clickType
    ) {

        super.slotClicked(
                slot,
                slotId,
                mouseButton,
                clickType
        );

        this.recipeBookComponent.slotClicked(
                slot
        );
    }

    @Override
    public void recipesUpdated() {

        this.recipeBookComponent
                .recipesUpdated();
    }

    @Override
    public RecipeBookComponent getRecipeBookComponent() {

        return this.recipeBookComponent;
    }
}