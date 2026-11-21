package net.semppi.semppis_mythical_legends_mod.client;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.lwjgl.glfw.GLFW;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = "semppis_mythical_legends_mod",
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class SMLFieldGuideInventoryButton {

    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 18;
    private static final int INVENTORY_X_OFFSET = 127;

    // Button sprites.
    private static final ResourceLocation NORMAL_TEXTURE =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "field_guide/sml_field_guide_button"
            );

    private static final ResourceLocation HOVER_TEXTURE =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "field_guide/sml_field_guide_button_highlighted"
            );

    private static final ResourceLocation EMPTY_TEXTURE =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "field_guide/sml_field_guide_button_empty_highlighted"
            );

    // Independent 2D book texture.
    private static final ResourceLocation BOOK_TEXTURE =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/field_guide/sml_field_guide_book.png"
            );

    private static final WidgetSprites BUTTON_SPRITES =
            new WidgetSprites(
                    NORMAL_TEXTURE,
                    NORMAL_TEXTURE,
                    HOVER_TEXTURE,
                    HOVER_TEXTURE
            );

    private static FieldGuideButton fieldGuideButton;

    private static boolean showDetachedBook = false;
    private static boolean showBookPreview = false;

    // Hop animation duration from Blockbench.
    private static final float HOP_DURATION = 0.4219F;

    // Small pause after the hop.
    private static final float SWING_DELAY = 0.08F;

    // Swing animation duration from Blockbench.
    private static final float SWING_DURATION = 0.5802F;

    // The swing starts after the hop and short pause.
    private static final float SWING_START =
            HOP_DURATION + SWING_DELAY;

    // Pause after the swing. Twice the first pause.
    private static final float FALL_DELAY = 0.16F;

    // Duration of the downward fall.
    private static final float FALL_DURATION = 0.40F;

    // When the swing finishes.
    private static final float SWING_END =
            SWING_START + SWING_DURATION;

    // When the book begins falling.
    private static final float FALL_START =
            SWING_END + FALL_DELAY;

    // When the complete 2D animation finishes.
    private static final float FALL_END =
            FALL_START + FALL_DURATION;

    // Time at which the player clicked the button.
    private static long hopStartNanos = 0L;

    public static boolean isBookPreviewVisible() {
        return showBookPreview;
    }

    /*
     * Return the timestamp of the current
     * Field Guide opening sequence.
     */
    public static long getOpeningStartNanos() {

        if (!showDetachedBook) {
            return 0L;
        }

        return hopStartNanos;
    }

    /*
     * Time elapsed since the 3D book should begin.
     *
     * Negative values mean the 3D animation
     * must not render yet.
     */
    public static float get3DBookElapsedSeconds() {

        if (!showDetachedBook || hopStartNanos == 0L) {
            return -1.0F;
        }

        float elapsed =
                (System.nanoTime() - hopStartNanos)
                        / 1_000_000_000.0F;

        // 2D fall finishes, followed by a 0.08-second pause.
        return elapsed - FALL_END - 0.08F;
    }

    public static void closeBookPreview() {
        showBookPreview = false;
        showDetachedBook = false;
        hopStartNanos = 0L;

        // Restore the button tooltip when the animation resets.
        if (fieldGuideButton != null) {
            fieldGuideButton.setTooltip(
                    Tooltip.create(Component.literal("SML Field Guide"))
            );
        }
    }

    /*
     * Create the inventory button.
     */
    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {

        if (!(event.getScreen() instanceof InventoryScreen screen)) {
            return;
        }

        closeBookPreview();

        int x = screen.getGuiLeft() + INVENTORY_X_OFFSET;
        int y = screen.height / 2 - 22;

        fieldGuideButton = new FieldGuideButton(x, y);

        fieldGuideButton.setTooltip(
                Tooltip.create(
                        Component.literal("SML Field Guide")
                )
        );

        event.addListener(fieldGuideButton);
    }

    /*
     * Keep the button aligned with the inventory.
     */
    @SubscribeEvent
    public static void onScreenRenderPre(ScreenEvent.Render.Pre event) {

        if (!(event.getScreen() instanceof InventoryScreen screen)) {
            return;
        }

        if (fieldGuideButton == null) {
            return;
        }

        if (!screen.children().contains(fieldGuideButton)) {
            return;
        }

        int x = screen.getGuiLeft() + INVENTORY_X_OFFSET;
        int y = screen.height / 2 - 22;

        fieldGuideButton.setX(x);
        fieldGuideButton.setY(y);
    }

    /*
     * Render the independently animated 2D book.
     */
    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {

        if (!(event.getScreen() instanceof InventoryScreen screen)) {
            return;
        }

        if (!showDetachedBook || fieldGuideButton == null) {
            return;
        }

        if (!screen.children().contains(fieldGuideButton)) {
            return;
        }

        // Time since the button was clicked.
        float elapsed = (System.nanoTime() - hopStartNanos)
                / 1_000_000_000.0F;

        float rotation = 0.0F;
        float offsetY = 0.0F;

// Additional downward movement during the fall.
        float fallOffsetY = 0.0F;

// Determines which corner the book rotates around.
        boolean swinging = false;

        /*
         * PHASE 1: HOP
         */
        if (elapsed <= HOP_DURATION) {

            float time = elapsed;

            if (time <= 0.1055F) {

                float progress = time / 0.1055F;

                rotation = lerp(
                        0.0F,
                        -7.5F,
                        progress
                );

            } else if (time <= 0.2637F) {

                float progress =
                        (time - 0.1055F)
                                / (0.2637F - 0.1055F);

                rotation = lerp(
                        -7.5F,
                        22.5F,
                        progress
                );

                // Your preferred 15-pixel hop.
                offsetY = lerp(
                        0.0F,
                        -15.0F,
                        progress
                );

            } else {

                float progress =
                        (time - 0.2637F)
                                / (HOP_DURATION - 0.2637F);

                rotation = lerp(
                        22.5F,
                        0.0F,
                        progress
                );

                offsetY = lerp(
                        -15.0F,
                        0.0F,
                        progress
                );
            }

            /*
             * PHASE 2: SHORT PAUSE
             */
        } else if (elapsed < SWING_START) {

            // Book rests in the frame.
            rotation = 0.0F;
            offsetY = 0.0F;

            /*
             * PHASE 3: SWING
             */
        } else if (elapsed < SWING_END) {

            swinging = true;

            float time = elapsed - SWING_START;

            if (time <= 0.3165F) {

                float progress = time / 0.3165F;

                rotation = lerp(
                        0.0F,
                        -60.0F,
                        progress
                );

            } else if (time <= 0.4747F) {

                float progress =
                        (time - 0.3165F)
                                / (0.4747F - 0.3165F);

                rotation = lerp(
                        -60.0F,
                        -37.5F,
                        progress
                );

            } else {

                float progress =
                        (time - 0.4747F)
                                / (SWING_DURATION - 0.4747F);

                rotation = lerp(
                        -37.5F,
                        -45.0F,
                        progress
                );
            }

            /*
             * PHASE 4: PAUSE WHILE HANGING
             */
        } else if (elapsed < FALL_START) {

            swinging = true;

            // Keep the book hanging at its final angle.
            rotation = -45.0F;

            /*
             * PHASE 5: FALL
             */
        } else if (elapsed < FALL_END) {

            swinging = true;
            rotation = -45.0F;

            float progress =
                    (elapsed - FALL_START) / FALL_DURATION;

            // Accelerate downward, like gravity.
            float easedProgress = progress * progress;

            // Move far enough to leave the entire screen.
            float fallDistance = screen.height + 40.0F;

            fallOffsetY = easedProgress * fallDistance;

            /*
             * PHASE 6: FINISHED
             */
        } else {

            // Stop drawing the standalone 2D book.
            // The empty highlighted button remains.
            return;
        }

        // Resting position inside the button.
        float bookX = fieldGuideButton.getX() + 2;
        float bookY = fieldGuideButton.getY() + 1;

        GuiGraphics graphics = event.getGuiGraphics();

        graphics.pose().pushPose();

        try {
            // Render in front of the inventory.
            graphics.pose().translate(0, 0, 300);

            if (swinging) {

                graphics.pose().translate(
                        bookX + 16,
                        bookY + fallOffsetY,
                        0
                );

                graphics.pose().mulPose(
                        Axis.ZP.rotationDegrees(rotation)
                );

                // Return to the book's drawing origin.
                graphics.pose().translate(
                        -16,
                        0,
                        0
                );

            } else {

                /*
                 * HOP:
                 * Rotate around the lower-left corner.
                 */
                graphics.pose().translate(
                        bookX,
                        bookY + 16 + offsetY,
                        0
                );

                graphics.pose().mulPose(
                        Axis.ZP.rotationDegrees(rotation)
                );

                graphics.pose().translate(
                        0,
                        -16,
                        0
                );
            }

            // Draw the standalone 16x16 book.
            graphics.blit(
                    BOOK_TEXTURE,
                    0,
                    0,
                    0,
                    0,
                    16,
                    16,
                    16,
                    16
            );

            graphics.flush();

        } finally {
            graphics.pose().popPose();
        }
    }

    /*
     * Simple interpolation between keyframes.
     */
    private static float lerp(
            float start,
            float end,
            float progress
    ) {
        return start + (end - start) * progress;
    }

    /*
     * The inventory is locked while the
     * Field Guide opening sequence is active.
     */
    private static boolean isOpeningInventoryActive() {

        return showDetachedBook
                && Minecraft.getInstance().screen
                instanceof InventoryScreen;
    }

    /*
     * Prevent clicking inventory slots during the animation.
     *
     * The Field Guide button remains clickable so we can
     * eventually use a second click to skip the animation.
     */
    @SubscribeEvent
    public static void onMousePressed(
            ScreenEvent.MouseButtonPressed.Pre event
    ) {

        if (!isOpeningInventoryActive()) {
            return;
        }

        if (event.getScreen() != Minecraft.getInstance().screen) {
            return;
        }

        // Allow a left click directly on the Field Guide button.
        if (fieldGuideButton != null
                && event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT
                && fieldGuideButton.isMouseOver(
                event.getMouseX(),
                event.getMouseY()
        )) {
            return;
        }

        // All other inventory clicks are blocked.
        event.setCanceled(true);
    }

    /*
     * Prevent items from being moved by mouse dragging.
     */
    @SubscribeEvent
    public static void onMouseDragged(
            ScreenEvent.MouseDragged.Pre event
    ) {

        if (isOpeningInventoryActive()) {
            event.setCanceled(true);
        }
    }

    /*
     * Prevent keyboard inventory actions.
     *
     * Esc and the player's inventory key remain usable,
     * so the inventory can always be closed.
     */
    @SubscribeEvent
    public static void onKeyPressed(
            ScreenEvent.KeyPressed.Pre event
    ) {

        if (!isOpeningInventoryActive()) {
            return;
        }

        int key = event.getKeyCode();

        // Allow Escape.
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            return;
        }

        // Allow the configured inventory key (normally E).
        if (Minecraft.getInstance().options.keyInventory.matches(
                key,
                event.getScanCode()
        )) {
            return;
        }

        // Block number-key swaps, Q drops, and other shortcuts.
        event.setCanceled(true);
    }

    /*
     * Prevent item tooltips from being rendered over
     * the 2D or 3D Field Guide animation.
     */
    @SubscribeEvent
    public static void onItemTooltip(
            RenderTooltipEvent.Pre event
    ) {

        if (isOpeningInventoryActive()) {
            event.setCanceled(true);
        }
    }

    /*
     * Remove the animation when the inventory closes.
     */
    @SubscribeEvent
    public static void onScreenClosing(ScreenEvent.Closing event) {

        if (event.getScreen() instanceof InventoryScreen) {
            closeBookPreview();
            fieldGuideButton = null;
        }
    }

    /*
     * Custom Field Guide button.
     */
    private static class FieldGuideButton extends ImageButton {

        public FieldGuideButton(int x, int y) {
            super(
                    x,
                    y,
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    BUTTON_SPRITES,
                    button -> {

                        if (showDetachedBook) {
                            // Second click resets the test.
                            closeBookPreview();

                        } else {
                            // Start the hop animation.
                            showDetachedBook = true;
                            hopStartNanos = System.nanoTime();

                            // Hide this button's tooltip during the transition.
                            button.setTooltip(null);
                        }
                    },
                    Component.literal("SML Field Guide")
            );
        }

        @Override
        public void renderWidget(
                GuiGraphics graphics,
                int mouseX,
                int mouseY,
                float partialTick
        ) {

            if (showDetachedBook) {

                // The empty blue frame stays stationary.
                graphics.blitSprite(
                        EMPTY_TEXTURE,
                        getX(),
                        getY(),
                        getWidth(),
                        getHeight()
                );

            } else {

                // Normal button with its book illustration.
                super.renderWidget(
                        graphics,
                        mouseX,
                        mouseY,
                        partialTick
                );
            }
        }
    }
}