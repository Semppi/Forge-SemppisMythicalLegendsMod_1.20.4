package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.PukisEntity;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class PukisTextureManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final ResourceLocation BASE =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/entity/pukis_grayscale.png"
            );

    private static final ResourceLocation REGION_1 =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/entity/pukis_region_1.png"
            );

    private static final ResourceLocation REGION_2 =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/entity/pukis_region_2.png"
            );

    private static final ResourceLocation REGION_3 =
            new ResourceLocation(
                    SemppisMythicalLegendsMod.MOD_ID,
                    "textures/entity/pukis_region_3.png"
            );

    private static final Map<PaletteKey, ResourceLocation> CACHE =
            new HashMap<>();

    private PukisTextureManager() {
    }

    public static ResourceLocation getTexture(PukisEntity pukis) {
        PaletteKey key = new PaletteKey(
                pukis.getRegion1Color(),
                pukis.getRegion2Color(),
                pukis.getRegion3Color()
        );

        return CACHE.computeIfAbsent(
                key,
                PukisTextureManager::createTexture
        );
    }

    private static ResourceLocation createTexture(PaletteKey key) {
        String path = String.format(
                Locale.ROOT,
                "dynamic/pukis_%06x_%06x_%06x",
                key.region1() & 0xFFFFFF,
                key.region2() & 0xFFFFFF,
                key.region3() & 0xFFFFFF
        );

        ResourceLocation dynamicLocation =
                new ResourceLocation(
                        SemppisMythicalLegendsMod.MOD_ID,
                        path
                );

        NativeImage output = null;

        try (
                NativeImage base = readImage(BASE);
                NativeImage region1 = readImage(REGION_1);
                NativeImage region2 = readImage(REGION_2);
                NativeImage region3 = readImage(REGION_3)
        ) {
            checkDimensions(base, region1, REGION_1);
            checkDimensions(base, region2, REGION_2);
            checkDimensions(base, region3, REGION_3);

            output = new NativeImage(
                    base.getWidth(),
                    base.getHeight(),
                    true
            );

            for (int y = 0; y < base.getHeight(); y++) {
                for (int x = 0; x < base.getWidth(); x++) {

                    int basePixel = base.getPixelRGBA(x, y);

                    int alpha = (basePixel >>> 24) & 0xFF;

                    if (alpha == 0) {
                        output.setPixelRGBA(x, y, basePixel);
                        continue;
                    }

                    int weight1 =
                            region1.getPixelRGBA(x, y) & 0xFF;

                    int weight2 =
                            region2.getPixelRGBA(x, y) & 0xFF;

                    int weight3 =
                            region3.getPixelRGBA(x, y) & 0xFF;

                    int totalWeight =
                            weight1 + weight2 + weight3;

                    /*
                     * If all masks are black here, preserve the
                     * master texture exactly.
                     *
                     * This is useful for flames, eyes, transparent
                     * space, and anything else excluded from the
                     * color-region system.
                     */
                    if (totalWeight == 0) {
                        output.setPixelRGBA(x, y, basePixel);
                        continue;
                    }

                    int targetRed =
                            weightedChannel(
                                    red(key.region1()),
                                    red(key.region2()),
                                    red(key.region3()),
                                    weight1,
                                    weight2,
                                    weight3,
                                    totalWeight
                            );

                    int targetGreen =
                            weightedChannel(
                                    green(key.region1()),
                                    green(key.region2()),
                                    green(key.region3()),
                                    weight1,
                                    weight2,
                                    weight3,
                                    totalWeight
                            );

                    int targetBlue =
                            weightedChannel(
                                    blue(key.region1()),
                                    blue(key.region2()),
                                    blue(key.region3()),
                                    weight1,
                                    weight2,
                                    weight3,
                                    totalWeight
                            );

                    /*
                     * Because the master is grayscale, one channel
                     * is enough to recover its brightness.
                     */
                    int grayscale = basePixel & 0xFF;

                    int finalRed =
                            grayscale * targetRed / 255;

                    int finalGreen =
                            grayscale * targetGreen / 255;

                    int finalBlue =
                            grayscale * targetBlue / 255;

                    /*
                     * NativeImage uses ABGR:
                     *
                     * AAAAAAAA BBBBBBBB GGGGGGGG RRRRRRRR
                     */
                    int finalPixel =
                            (alpha << 24)
                                    | (finalBlue << 16)
                                    | (finalGreen << 8)
                                    | finalRed;

                    output.setPixelRGBA(x, y, finalPixel);
                }
            }

            DynamicTexture dynamicTexture =
                    new DynamicTexture(output);

            Minecraft.getInstance()
                    .getTextureManager()
                    .register(dynamicLocation, dynamicTexture);

            /*
             * DynamicTexture now owns the NativeImage.
             */
            output = null;

            return dynamicLocation;

        } catch (Exception exception) {

            if (output != null) {
                output.close();
            }

            LOGGER.error(
                    "Failed to generate dynamic Pukis texture {}",
                    dynamicLocation,
                    exception
            );

            return BASE;
        }
    }

    private static NativeImage readImage(
            ResourceLocation location
    ) throws IOException {

        try (InputStream stream =
                     Minecraft.getInstance()
                             .getResourceManager()
                             .open(location)) {

            return NativeImage.read(stream);
        }
    }

    private static void checkDimensions(
            NativeImage base,
            NativeImage mask,
            ResourceLocation maskLocation
    ) {

        if (base.getWidth() != mask.getWidth()
                || base.getHeight() != mask.getHeight()) {

            throw new IllegalStateException(
                    "Pukis texture mask "
                            + maskLocation
                            + " is "
                            + mask.getWidth()
                            + "x"
                            + mask.getHeight()
                            + " but base texture is "
                            + base.getWidth()
                            + "x"
                            + base.getHeight()
            );
        }
    }

    private static int weightedChannel(
            int color1,
            int color2,
            int color3,
            int weight1,
            int weight2,
            int weight3,
            int totalWeight
    ) {

        return (
                color1 * weight1
                        + color2 * weight2
                        + color3 * weight3
        ) / totalWeight;
    }

    private static int red(int color) {
        return (color >>> 16) & 0xFF;
    }

    private static int green(int color) {
        return (color >>> 8) & 0xFF;
    }

    private static int blue(int color) {
        return color & 0xFF;
    }

    private record PaletteKey(
            int region1,
            int region2,
            int region3
    ) {
    }
}