package net.semppi.semppis_mythical_legends_mod.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.semppi.semppis_mythical_legends_mod.client.ModKeyMappings;
import org.lwjgl.glfw.GLFW;

/**
 * Empty first-stage map screen. Map rendering belongs to later Jr. goals.
 */
public final class TestMapScreen extends Screen {

    private static final Component TITLE =
            Component.translatable(
                    "screen.semppis_mythical_legends_mod.test_map"
            );

    public TestMapScreen() {
        super(TITLE);
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
