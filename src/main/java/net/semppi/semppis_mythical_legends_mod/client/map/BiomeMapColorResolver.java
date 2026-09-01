package net.semppi.semppis_mythical_legends_mod.client.map;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.semppi.semppis_mythical_legends_mod.network.MapSnapshotPayload;

import java.util.Locale;

/** Simple first-pass biome colors for the test map. */
public final class BiomeMapColorResolver {
    private static final int UNKNOWN_LAND = 0x78905A;
    private static final int FROZEN = 0xDCECF0;
    private static final int BEACH = 0xD8C98B;
    private static final int DESERT = 0xD8BE68;
    private static final int BADLANDS = 0xB9673D;
    private static final int SWAMP = 0x65734A;
    private static final int MUSHROOM = 0x9B668E;

    private BiomeMapColorResolver() {}

    public static int color(
            Minecraft minecraft,
            ResourceLocation biomeId,
            byte surface
    ) {
        if (minecraft.level == null) {
            return UNKNOWN_LAND;
        }

        Registry<Biome> biomes = minecraft.level.registryAccess()
                .registryOrThrow(Registries.BIOME);
        Holder.Reference<Biome> holder = biomes.getHolder(
                ResourceKey.create(Registries.BIOME, biomeId)
        ).orElse(null);
        if (holder == null) {
            return fallbackColor(biomeId);
        }

        String path = biomeId.getPath().toLowerCase(Locale.ROOT);
        Biome biome = holder.value();

        if (surface == MapSnapshotPayload.SURFACE_WET) {
            return biome.getWaterColor();
        }
        if (containsAny(path, "frozen", "snow", "ice")) {
            return FROZEN;
        }
        if (holder.is(BiomeTags.IS_BEACH)) {
            return BEACH;
        }
        if (holder.is(BiomeTags.IS_BADLANDS)
                || path.contains("badlands")) {
            return BADLANDS;
        }
        if (path.contains("desert")) {
            return DESERT;
        }
        if (containsAny(path, "swamp", "marsh", "bog")) {
            return SWAMP;
        }
        if (path.contains("mushroom")) {
            return MUSHROOM;
        }

        if (surface == MapSnapshotPayload.SURFACE_DRY) {
            return biome.getGrassColor(0.0, 0.0);
        }
        if (holder.is(BiomeTags.IS_OCEAN)
                || holder.is(BiomeTags.IS_RIVER)) {
            return biome.getWaterColor();
        }
        return biome.getGrassColor(0.0, 0.0);
    }

    public static boolean isOcean(
            Minecraft minecraft,
            ResourceLocation biomeId
    ) {
        Holder.Reference<Biome> holder = biomeHolder(minecraft, biomeId);
        return holder != null
                ? holder.is(BiomeTags.IS_OCEAN)
                : biomeId.getPath().toLowerCase(Locale.ROOT).contains("ocean");
    }

    public static boolean isRiver(
            Minecraft minecraft,
            ResourceLocation biomeId
    ) {
        Holder.Reference<Biome> holder = biomeHolder(minecraft, biomeId);
        return holder != null
                ? holder.is(BiomeTags.IS_RIVER)
                : biomeId.getPath().toLowerCase(Locale.ROOT).contains("river");
    }

    private static Holder.Reference<Biome> biomeHolder(
            Minecraft minecraft,
            ResourceLocation biomeId
    ) {
        if (minecraft.level == null) {
            return null;
        }
        Registry<Biome> biomes = minecraft.level.registryAccess()
                .registryOrThrow(Registries.BIOME);
        return biomes.getHolder(
                ResourceKey.create(Registries.BIOME, biomeId)
        ).orElse(null);
    }

    private static int fallbackColor(ResourceLocation biomeId) {
        String path = biomeId.getPath().toLowerCase(Locale.ROOT);
        if (containsAny(path, "ocean", "river")) {
            return 0x3F76A8;
        }
        if (containsAny(path, "frozen", "snow", "ice")) {
            return FROZEN;
        }
        if (path.contains("beach")) {
            return BEACH;
        }
        if (path.contains("badlands")) {
            return BADLANDS;
        }
        if (path.contains("desert")) {
            return DESERT;
        }
        if (containsAny(path, "swamp", "marsh", "bog")) {
            return SWAMP;
        }
        if (path.contains("mushroom")) {
            return MUSHROOM;
        }
        return UNKNOWN_LAND;
    }

    private static boolean containsAny(String value, String... fragments) {
        for (String fragment : fragments) {
            if (value.contains(fragment)) {
                return true;
            }
        }
        return false;
    }
}
