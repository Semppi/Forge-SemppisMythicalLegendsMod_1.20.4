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

public class CraftingOvenScreen
        extends AbstractContainerScreen<CraftingOvenMenu>
        implements RecipeUpdateListener {

    private static final ResourceLocation WIDGETS =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/gui/crafting_oven_widgets.png"
            );

    private static final int FLAME_X = 20;
    private static final int FLAME_Y = 37;

    private static final int ARROW_X = 39;
    private static final int ARROW_Y = 28;

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
                        this.topPos + RECIPE_BOOK_BUTTON_Y,
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

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/gui/crafting_oven_menu.png"
            );

    private final RecipeBookComponent recipeBookComponent =
            new RecipeBookComponent();

    private boolean widthTooNarrow;

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

        this.imageWidth = 198;
        this.imageHeight = 190;

        this.inventoryLabelY = 96;
    }

    @Override
    protected void containerTick() {

        super.containerTick();

        this.recipeBookComponent.tick();
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

        /*
         * Draw the basic GUI texture.
         *
         * The texture dimensions should match imageWidth/imageHeight.
         */
        guiGraphics.blit(
                TEXTURE,
                left,
                top,
                0.0F,
                0.0F,
                this.imageWidth,
                this.imageHeight,
                this.imageWidth,
                this.imageHeight
        );

        int flamePixels = this.menu.getBurnProgress();

        if (flamePixels > 0) {
            guiGraphics.blit(
                    WIDGETS,
                    left + 18,
                    top + 37 + (13 - flamePixels),
                    5,
                    9 + (13 - flamePixels),
                    13,
                    flamePixels,
                    64,
                    32
            );
        }

        int arrowPixels = this.menu.getCookProgress();

        if (arrowPixels > 0) {
            guiGraphics.blit(
                    WIDGETS,
                    left + 40,
                    top + 19,
                    39,
                    8,
                    arrowPixels,
                    16,
                    64,
                    32
            );
        }
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
         * Only allow clicking the oven button when the
         * recipe book is not covering the menu.
         */
        if (button == 0
                && mouseOverSpecialButton
                && (!this.widthTooNarrow
                || !this.recipeBookComponent.isVisible())) {

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