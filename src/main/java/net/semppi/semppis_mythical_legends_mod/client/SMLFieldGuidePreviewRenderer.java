package net.semppi.semppis_mythical_legends_mod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.LightTexture;
import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import software.bernie.geckolib.renderer.GeoObjectRenderer;

@Mod.EventBusSubscriber(
        modid = "semppis_mythical_legends_mod",
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public class SMLFieldGuidePreviewRenderer {

    // Duration of the woosh animation.
    private static final float WOOSH_DURATION = 0.6341F;

    // Duration of the opening animation.
    private static final float OPEN_DURATION = 0.7083F;

    // Combined 3D animation duration.
    private static final float TOTAL_DURATION =
            WOOSH_DURATION + OPEN_DURATION;

    // Short hold before removing the 3D model.
    private static final float END_HOLD = 0.0F;

    // Temporary model appearance settings.
    private static final float BOOK_SCALE = 75.0F;
    private static final float ROTATION_X = 0.0F;
    private static final float ROTATION_Y = 180.0F;

    // Final size of the open book relative to the screen.
// 0.50 means the render scale is half the GUI width.
    private static final float FINAL_SCALE_FACTOR = 0.77F;

    // How far down the book moves as it approaches the screen.
// Relative to the GUI's height.
    private static final float FINAL_Y_OFFSET_FACTOR = 0.34F;

    // Move the book right as it approaches the screen.
    private static final float FINAL_X_OFFSET_FACTOR = 0.035F;

    // Begin enlarging shortly before the woosh finishes.
    private static final float GROW_START =
            WOOSH_DURATION - 0.12F;

    // Finish enlarging before the final page flips.
    private static final float GROW_END =
            WOOSH_DURATION + 0.60F;

    // GeckoLib renderer, using the existing model class.
    private static final GeoObjectRenderer<SMLFieldGuideAnimatable>
            BOOK_RENDERER =
            new GeoObjectRenderer<>(new SMLFieldGuideModel());

    // Fresh animatable for each opening sequence.
    private static SMLFieldGuideAnimatable book;

    // Used to detect a new button click.
    private static long lastOpeningStartNanos = 0L;

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {

        if (!(event.getScreen() instanceof InventoryScreen)) {
            return;
        }

        if (Minecraft.getInstance().screen != event.getScreen()) {
            return;
        }

        float animationTime =
                SMLFieldGuideInventoryButton
                        .get3DBookElapsedSeconds();

        // During the 2D animation, do not draw the 3D model.
        if (animationTime < 0.0F) {
            book = null;
            lastOpeningStartNanos = 0L;
            return;
        }

        // Stop rendering after both animations and the final hold.
        if (animationTime >= TOTAL_DURATION + END_HOLD) {
            return;
        }

        long openingStart =
                SMLFieldGuideInventoryButton
                        .getOpeningStartNanos();

        // Create a new book when a new opening begins.
        if (book == null || openingStart != lastOpeningStartNanos) {

            book = new SMLFieldGuideAnimatable();
            lastOpeningStartNanos = openingStart;
        }

        /*
         * Gradually enlarge the entire 3D book.
         *
         * Starts near the end of the woosh and
         * finishes shortly before book_open ends.
         */

        float finalScale =
                event.getScreen().width * FINAL_SCALE_FACTOR;

        // Progress from 0 to 1 during the enlargement.
        float growProgress =
                (animationTime - GROW_START)
                        / (GROW_END - GROW_START);

        // Keep progress between 0 and 1.
        growProgress = Math.max(
                0.0F,
                Math.min(1.0F, growProgress)
        );

        // Smooth acceleration and deceleration.
        float smoothProgress =
                growProgress * growProgress
                        * (3.0F - 2.0F * growProgress);

        // Actual model scale for this frame.
        float currentScale =
                BOOK_SCALE
                        + (finalScale - BOOK_SCALE)
                        * smoothProgress;

        // Move downward gradually as the book grows.
        float currentYOffset =
                event.getScreen().height
                        * FINAL_Y_OFFSET_FACTOR
                        * smoothProgress;

        // Move the book right gradually as it grows.
        float currentXOffset =
                event.getScreen().width
                        * FINAL_X_OFFSET_FACTOR
                        * smoothProgress;

        GuiGraphics graphics = event.getGuiGraphics();
        PoseStack poseStack = graphics.pose();

// Finish all pending inventory drawing first.
        graphics.flush();

        /*
         * Clear ONLY the depth buffer.
         *
         * This prevents inventory stack counts and other
         * previously rendered GUI elements from appearing
         * in front of the 3D Field Guide.
         *
         * Do not clear the color buffer.
         */
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, false);

// Begin our normal 3D rendering.
        poseStack.pushPose();

        try {
            /*
             * Temporary static GUI position.
             *
             * We are testing the Blockbench animation,
             * not the swooping movement yet.
             */
            poseStack.translate(
                    event.getScreen().width / 2.0F + currentXOffset,
                    event.getScreen().height / 2.0F + currentYOffset,
                    300.0F
            );

            poseStack.scale(
                    currentScale,
                    -currentScale,
                    currentScale
            );

            poseStack.mulPose(
                    Axis.XP.rotationDegrees(ROTATION_X)
            );

            poseStack.mulPose(
                    Axis.YP.rotationDegrees(ROTATION_Y)
            );

            // Compensate for GeoObjectRenderer's translation.
            poseStack.translate(
                    -0.5F,
                    -0.51F,
                    -0.5F
            );

            // Play the model's book_open animation.
            BOOK_RENDERER.render(
                    poseStack,
                    book,
                    graphics.bufferSource(),
                    null,
                    null,
                    LightTexture.FULL_BRIGHT
            );

            graphics.flush();

        } finally {
            poseStack.popPose();
        }
    }
}