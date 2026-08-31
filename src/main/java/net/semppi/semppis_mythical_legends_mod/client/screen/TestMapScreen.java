package net.semppi.semppis_mythical_legends_mod.client.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.client.ModKeyMappings;
import net.semppi.semppis_mythical_legends_mod.client.map.BiomeMapColorResolver;
import net.semppi.semppis_mythical_legends_mod.client.map.ClientMapSnapshotState;
import net.semppi.semppis_mythical_legends_mod.network.MapSnapshotPayload;
import net.semppi.semppis_mythical_legends_mod.network.MapRegionCode;
import net.semppi.semppis_mythical_legends_mod.network.SMLNetwork;
import net.semppi.semppis_mythical_legends_mod.world.Continent;
import net.semppi.semppis_mythical_legends_mod.world.Ocean;
import org.lwjgl.glfw.GLFW;

import java.util.List;

/**
 * Early test-map screen with a fixed biome canvas and continental overlay.
 */
public final class TestMapScreen extends Screen {

    /** The source texture still stores one pixel for every world block. */
    private static final int MAP_CANVAS_SIZE = 256;

    private static final int MAX_FRAME_DISPLAY_SIZE = 240;
    private static final int SCREEN_MARGIN = 12;
    private static final int FRAME_INSET = 10;
    private static final int SIDE_PANEL_GAP = 8;
    private static final int MAX_SIDE_PANEL_WIDTH = 88;
    private static final int MIN_SIDE_PANEL_WIDTH = 28;
    private static final int SIDE_PANEL_BORDER = 2;

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
    private static final int BIOME_EDGE_COLOR = 0x2B2924;
    private static final int CONTINENT_STRIPE_SPACING = 6;
    private static final int CONTINENT_STRIPE_WIDTH = 2;
    private static final int CONTINENT_STRIPE_ALPHA = 112;

    private static final int[] LAND_OVERLAY_COLORS = {
            0xD29A43, // Africa
            0xE7EEF4, // Frozen Pole
            0x9A63C7, // Asia
            0x4C9B63, // Europe
            0xD75A4A, // North America
            0x43A89B, // South America
            0xD8B643  // Australia
    };

    private static final int[] OCEAN_OVERLAY_COLORS = {
            0xA8DDF0, // Arctic
            0x438FD0, // North Atlantic
            0x2C809C, // South Atlantic
            0x596FB6, // Indian
            0x5EA8E5, // North Pacific
            0x397F7C, // South Pacific
            0xC08AA4  // Southern
    };
    private static final int PANEL_BORDER_COLOR = 0xFF514532;
    private static final int PANEL_BACKGROUND_COLOR = 0xFFE3CC99;
    private static final int PANEL_INNER_EDGE_COLOR = 0xFFC2A978;
    private static final int PANEL_HIGHLIGHT_COLOR = 0xFFF0DCAD;
    private static final int MARKER_OUTLINE = 0xFF1B1B1B;
    private static final int MARKER_FILL = 0xFFF5F5F5;

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
        renderMap(guiGraphics);
    }

    private void renderMap(GuiGraphics guiGraphics) {
        int availableSize = Math.min(this.width, this.height)
                - SCREEN_MARGIN * 2;
        int frameSize = Math.min(MAX_FRAME_DISPLAY_SIZE, availableSize);
        if (frameSize <= FRAME_INSET * 2) {
            return;
        }

        int frameLeft = (this.width - frameSize) / 2;
        int frameTop = (this.height - frameSize) / 2;
        int frameRight = frameLeft + frameSize;
        int frameBottom = frameTop + frameSize;

        renderSidePanels(
                guiGraphics,
                frameLeft,
                frameTop,
                frameRight,
                frameBottom
        );

        guiGraphics.fill(
                frameLeft + 3,
                frameTop + 3,
                frameRight + 3,
                frameBottom + 3,
                MAP_SHADOW_COLOR
        );
        guiGraphics.fill(
                frameLeft - 1,
                frameTop - 1,
                frameRight + 1,
                frameBottom + 1,
                MAP_BORDER_COLOR
        );

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(frameLeft, frameTop, 0.0F);
        float frameScale = (float) frameSize / VANILLA_MAP_TEXTURE_SIZE;
        guiGraphics.pose().scale(frameScale, frameScale, 1.0F);
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
                frameLeft,
                frameTop,
                frameRight,
                frameBottom,
                MAP_FOREGROUND_WASH
        );

        int mapLeft = frameLeft + FRAME_INSET;
        int mapTop = frameTop + FRAME_INSET;
        int mapRight = frameRight - FRAME_INSET;
        int mapBottom = frameBottom - FRAME_INSET;

        renderBiomeLayer(
                guiGraphics,
                mapLeft,
                mapTop,
                mapRight,
                mapBottom
        );
        renderPlayerMarker(
                guiGraphics,
                mapLeft,
                mapTop,
                mapRight,
                mapBottom
        );
    }

    private void renderSidePanels(
            GuiGraphics guiGraphics,
            int frameLeft,
            int frameTop,
            int frameRight,
            int frameBottom
    ) {
        int availablePerSide = frameLeft
                - SCREEN_MARGIN
                - SIDE_PANEL_GAP;
        int panelWidth = Math.min(MAX_SIDE_PANEL_WIDTH, availablePerSide);
        if (panelWidth < MIN_SIDE_PANEL_WIDTH) {
            return;
        }

        // Anchor the trays outward. Any extra horizontal room becomes open
        // space around the centered map instead of unused screen-edge space.
        int leftPanelLeft = SCREEN_MARGIN;
        int leftPanelRight = leftPanelLeft + panelWidth;
        int rightPanelRight = this.width - SCREEN_MARGIN;
        int rightPanelLeft = rightPanelRight - panelWidth;

        renderSidePanel(
                guiGraphics,
                leftPanelLeft,
                frameTop,
                leftPanelRight,
                frameBottom
        );
        renderSidePanel(
                guiGraphics,
                rightPanelLeft,
                frameTop,
                rightPanelRight,
                frameBottom
        );
    }

    private static void renderSidePanel(
            GuiGraphics guiGraphics,
            int left,
            int top,
            int right,
            int bottom
    ) {
        guiGraphics.fill(
                left + 3,
                top + 3,
                right + 3,
                bottom + 3,
                MAP_SHADOW_COLOR
        );
        guiGraphics.fill(left, top, right, bottom, PANEL_BORDER_COLOR);
        guiGraphics.fill(
                left + SIDE_PANEL_BORDER,
                top + SIDE_PANEL_BORDER,
                right - SIDE_PANEL_BORDER,
                bottom - SIDE_PANEL_BORDER,
                PANEL_BACKGROUND_COLOR
        );

        // Empty inset treatment reserves a clear future control surface.
        guiGraphics.fill(
                left + SIDE_PANEL_BORDER,
                top + SIDE_PANEL_BORDER,
                right - SIDE_PANEL_BORDER,
                top + SIDE_PANEL_BORDER + 1,
                PANEL_HIGHLIGHT_COLOR
        );
        guiGraphics.fill(
                left + SIDE_PANEL_BORDER,
                top + SIDE_PANEL_BORDER,
                left + SIDE_PANEL_BORDER + 1,
                bottom - SIDE_PANEL_BORDER,
                PANEL_HIGHLIGHT_COLOR
        );
        guiGraphics.fill(
                left + SIDE_PANEL_BORDER,
                bottom - SIDE_PANEL_BORDER - 1,
                right - SIDE_PANEL_BORDER,
                bottom - SIDE_PANEL_BORDER,
                PANEL_INNER_EDGE_COLOR
        );
        guiGraphics.fill(
                right - SIDE_PANEL_BORDER - 1,
                top + SIDE_PANEL_BORDER,
                right - SIDE_PANEL_BORDER,
                bottom - SIDE_PANEL_BORDER,
                PANEL_INNER_EDGE_COLOR
        );
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
            float displayScale = (float) (right - left) / MAP_CANVAS_SIZE;
            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(left, top, 0.0F);
            guiGraphics.pose().scale(displayScale, displayScale, 1.0F);
            guiGraphics.blit(
                    mapTextureLocation,
                    0,
                    0,
                    0.0F,
                    0.0F,
                    MAP_CANVAS_SIZE,
                    MAP_CANVAS_SIZE,
                    MAP_CANVAS_SIZE,
                    MAP_CANVAS_SIZE
            );
            guiGraphics.pose().popPose();
        }
    }

    private void renderPlayerMarker(
            GuiGraphics guiGraphics,
            int mapLeft,
            int mapTop,
            int mapRight,
            int mapBottom
    ) {
        Minecraft minecraft = Minecraft.getInstance();
        MapSnapshotPayload snapshot = this.displayedSnapshot;
        if (snapshot == null
                || minecraft.player == null
                || minecraft.level == null
                || !minecraft.level.dimension().location().equals(
                        snapshot.dimension()
                )) {
            return;
        }

        double relativeX = minecraft.player.getX() - snapshot.originX();
        double relativeZ = minecraft.player.getZ() - snapshot.originZ();
        boolean inside = relativeX >= 0.0
                && relativeX < MAP_CANVAS_SIZE
                && relativeZ >= 0.0
                && relativeZ < MAP_CANVAS_SIZE;

        double displaySize = mapRight - mapLeft;
        double rawX = mapLeft
                + relativeX / MAP_CANVAS_SIZE * displaySize;
        double rawY = mapTop
                + relativeZ / MAP_CANVAS_SIZE * displaySize;
        double markerScale = inside ? 1.0 : 0.5;
        double markerHalfSize = 4.0 * markerScale;
        double markerX = Mth.clamp(
                rawX,
                mapLeft + markerHalfSize,
                mapRight - markerHalfSize
        );
        double markerY = Mth.clamp(
                rawY,
                mapTop + markerHalfSize,
                mapBottom - markerHalfSize
        );

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(markerX, markerY, 100.0F);
        guiGraphics.pose().mulPose(Axis.ZP.rotationDegrees(
                minecraft.player.getYRot() - 180.0F
        ));
        guiGraphics.pose().scale(
                (float) markerScale,
                (float) markerScale,
                1.0F
        );

        // Small vanilla-style pixel arrow, pointing upward before rotation.
        guiGraphics.fill(-1, -4, 1, -3, MARKER_OUTLINE);
        guiGraphics.fill(-2, -3, 2, -2, MARKER_OUTLINE);
        guiGraphics.fill(-3, -2, 3, -1, MARKER_OUTLINE);
        guiGraphics.fill(-2, -1, 2, 4, MARKER_OUTLINE);
        guiGraphics.fill(-1, -3, 1, -2, MARKER_FILL);
        guiGraphics.fill(-2, -2, 2, -1, MARKER_FILL);
        guiGraphics.fill(-1, -1, 1, 3, MARKER_FILL);
        guiGraphics.pose().popPose();
    }

    private void refreshMapTexture() {
        MapSnapshotPayload snapshot = ClientMapSnapshotState.get();
        if (snapshot == null || snapshot == displayedSnapshot) {
            return;
        }

        displayedSnapshot = snapshot;
        releaseMapTexture();

        int[] biomePixels = snapshot.biomePixels();
        byte[] regionPixels = snapshot.regionPixels();
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
                    boolean biomeEdge = isBiomeEdge(
                            biomePixels,
                            x,
                            z,
                            encodedBiome
                    );
                    int pixelColor = biomeEdge
                            ? BIOME_EDGE_COLOR
                            : paletteColors[encodedBiome - 1];
                    byte encodedRegion = regionPixels[row + x];
                    if (!biomeEdge
                            && encodedRegion != 0
                            && isContinentalStripe(
                                    snapshot.originX() + x,
                                    snapshot.originZ() + z
                            )) {
                        pixelColor = blendRgb(
                                pixelColor,
                                overlayColor(encodedRegion),
                                CONTINENT_STRIPE_ALPHA
                        );
                    }
                    image.setPixelRGBA(
                            x,
                            z,
                            rgbToAbgr(pixelColor)
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

    private static boolean isBiomeEdge(
            int[] biomePixels,
            int x,
            int z,
            int encodedBiome
    ) {
        // Right and bottom are sufficient to draw every boundary once. This
        // prevents the line from becoming two source pixels thick.
        if (x + 1 < MAP_CANVAS_SIZE) {
            int right = biomePixels[z * MAP_CANVAS_SIZE + x + 1];
            if (right != 0 && right != encodedBiome) {
                return true;
            }
        }
        if (z + 1 < MAP_CANVAS_SIZE) {
            int below = biomePixels[(z + 1) * MAP_CANVAS_SIZE + x];
            return below != 0 && below != encodedBiome;
        }
        return false;
    }

    private static boolean isContinentalStripe(int worldX, int worldZ) {
        // Screen Z grows downward, so x + z produces a line rising toward
        // the right: bottom-left to top-right. World coordinates keep the
        // hatch phase continuous when a neighboring map page is opened.
        return Math.floorMod(
                worldX + worldZ,
                CONTINENT_STRIPE_SPACING
        )
                < CONTINENT_STRIPE_WIDTH;
    }

    private static int overlayColor(byte encodedRegion) {
        if (MapRegionCode.isOcean(encodedRegion)) {
            Ocean ocean = MapRegionCode.ocean(encodedRegion);
            return OCEAN_OVERLAY_COLORS[ocean.ordinal()];
        }
        Continent continent = MapRegionCode.continent(encodedRegion);
        return LAND_OVERLAY_COLORS[continent.ordinal()];
    }

    private static int blendRgb(int background, int foreground, int alpha) {
        int inverseAlpha = 255 - alpha;
        int red = ((background >> 16 & 0xFF) * inverseAlpha
                + (foreground >> 16 & 0xFF) * alpha) / 255;
        int green = ((background >> 8 & 0xFF) * inverseAlpha
                + (foreground >> 8 & 0xFF) * alpha) / 255;
        int blue = ((background & 0xFF) * inverseAlpha
                + (foreground & 0xFF) * alpha) / 255;
        return red << 16 | green << 8 | blue;
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
