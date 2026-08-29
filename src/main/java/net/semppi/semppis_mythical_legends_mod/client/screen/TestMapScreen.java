package net.semppi.semppis_mythical_legends_mod.client.screen;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.client.ModKeyMappings;
import net.semppi.semppis_mythical_legends_mod.client.map.BiomeMapColorResolver;
import net.semppi.semppis_mythical_legends_mod.client.map.ClientMapSnapshotState;
import net.semppi.semppis_mythical_legends_mod.network.MapSnapshotPayload;
import net.semppi.semppis_mythical_legends_mod.network.SMLNetwork;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Early test-map screen with a fixed biome canvas.
 * Boundary and continental overlays belong to later Jr. goals.
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
    private static final int UNEXPLORED_MASK = 0x88000000;

    private MapSnapshotPayload displayedSnapshot;
    private ResourceLocation mapTextureLocation;

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
        this.displayedSnapshot = null;
        releaseMapTexture();
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

        renderBiomeLayer(guiGraphics, left, top, right, bottom);
    }

    private void renderBiomeLayer(
            GuiGraphics guiGraphics,
            int left,
            int top,
            int right,
            int bottom
    ) {
        refreshMapTexture();

        // Until the server snapshot arrives, the complete page is unexplored.
        guiGraphics.fill(left, top, right, bottom, UNEXPLORED_MASK);

        if (mapTextureLocation != null) {
            guiGraphics.blit(
                    mapTextureLocation,
                    left,
                    top,
                    0.0F,
                    0.0F,
                    MAP_CANVAS_SIZE,
                    MAP_CANVAS_SIZE,
                    MAP_CANVAS_SIZE,
                    MAP_CANVAS_SIZE
            );
        }
    }

    private void refreshMapTexture() {
        MapSnapshotPayload snapshot = ClientMapSnapshotState.get();
        if (snapshot == null || snapshot == displayedSnapshot) {
            return;
        }

        displayedSnapshot = snapshot;
        releaseMapTexture();

        int[] biomePixels = snapshot.biomePixels();
        List<ResourceLocation> palette = snapshot.biomePalette();
        int[] paletteColors = new int[palette.size()];
        Minecraft minecraft = Minecraft.getInstance();

        for (int index = 0; index < palette.size(); index++) {
            paletteColors[index] = BiomeMapColorResolver.color(
                    minecraft,
                    palette.get(index)
            );
        }

        NativeImage image = new NativeImage(
                MAP_CANVAS_SIZE,
                MAP_CANVAS_SIZE,
                true
        );
        for (int z = 0; z < MAP_CANVAS_SIZE; z++) {
            int row = z * MAP_CANVAS_SIZE;
            for (int x = 0; x < MAP_CANVAS_SIZE; x++) {
                int encodedBiome = biomePixels[row + x];
                if (encodedBiome != 0) {
                    image.setPixelRGBA(
                            x,
                            z,
                            rgbToAbgr(paletteColors[encodedBiome - 1])
                    );
                } else {
                    image.setPixelRGBA(x, z, 0x00000000);
                }
            }
        }

        DynamicTexture mapTexture = new DynamicTexture(image);
        mapTexture.upload();
        this.mapTextureLocation = minecraft.getTextureManager().register(
                "sml_test_biome_map",
                mapTexture
        );
    }

    private static int rgbToAbgr(int rgb) {
        int red = rgb >> 16 & 0xFF;
        int green = rgb >> 8 & 0xFF;
        int blue = rgb & 0xFF;
        return 0xFF000000 | blue << 16 | green << 8 | red;
    }

    private void releaseMapTexture() {
        if (this.mapTextureLocation == null || this.minecraft == null) {
            return;
        }

        this.minecraft.getTextureManager().release(this.mapTextureLocation);
        this.mapTextureLocation = null;
    }

    @Override
    public void removed() {
        releaseMapTexture();
        super.removed();
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
