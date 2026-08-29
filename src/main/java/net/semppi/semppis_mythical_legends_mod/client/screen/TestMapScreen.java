package net.semppi.semppis_mythical_legends_mod.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.client.ModKeyMappings;
import net.semppi.semppis_mythical_legends_mod.client.map.ClientMapSnapshotState;
import net.semppi.semppis_mythical_legends_mod.network.SMLNetwork;
import org.lwjgl.glfw.GLFW;

/**
 * First-stage map screen with a fixed empty map canvas.
 * Terrain and overlay rendering belong to later Jr. goals.
 */
public final class TestMapScreen extends Screen {

    /** One screen pixel currently represents one world block. */
    private static final int MAP_CANVAS_SIZE = 256;

    private static final int VANILLA_MAP_TEXTURE_SIZE = 128;

    private static final ResourceLocation EMPTY_MAP_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "minecraft",
                    "textures/map/map_background.png"
            );

    private static final int MAP_BORDER_COLOR = 0xFF2B241A;
    private static final int MAP_SHADOW_COLOR = 0x66000000;
    private static final int MAP_FOREGROUND_WASH = 0x55FFF1C1;

    private static final Component TITLE =
            Component.translatable(
                    "screen.semppis_mythical_legends_mod.test_map"
            );

    public TestMapScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();
        ClientMapSnapshotState.clear();
        SMLNetwork.requestMapSnapshot();
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

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        // The canvas is intentionally the final foreground layer. The world
        // darkening therefore never dims the map itself.
        renderEmptyMapCanvas(guiGraphics);
    }

    private void renderEmptyMapCanvas(GuiGraphics guiGraphics) {
        int left = (this.width - MAP_CANVAS_SIZE) / 2;
        int top = (this.height - MAP_CANVAS_SIZE) / 2;
        int right = left + MAP_CANVAS_SIZE;
        int bottom = top + MAP_CANVAS_SIZE;

        guiGraphics.fill(
                left + 3,
                top + 3,
                right + 3,
                bottom + 3,
                MAP_SHADOW_COLOR
        );
        guiGraphics.fill(
                left - 1,
                top - 1,
                right + 1,
                bottom + 1,
                MAP_BORDER_COLOR
        );

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(left, top, 0.0F);
        guiGraphics.pose().scale(2.0F, 2.0F, 1.0F);
        guiGraphics.blit(
                EMPTY_MAP_TEXTURE,
                0,
                0,
                0.0F,
                0.0F,
                VANILLA_MAP_TEXTURE_SIZE,
                VANILLA_MAP_TEXTURE_SIZE,
                VANILLA_MAP_TEXTURE_SIZE,
                VANILLA_MAP_TEXTURE_SIZE
        );
        guiGraphics.pose().popPose();

        // Vanilla's empty-map texture is deliberately muted. This light wash
        // keeps the test canvas readable over the darkened world backdrop.
        guiGraphics.fill(
                left,
                top,
                right,
                bottom,
                MAP_FOREGROUND_WASH
        );
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (ModKeyMappings.TEST_MAP.matches(keyCode, scanCode)
                || keyCode == GLFW.GLFW_KEY_E) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
