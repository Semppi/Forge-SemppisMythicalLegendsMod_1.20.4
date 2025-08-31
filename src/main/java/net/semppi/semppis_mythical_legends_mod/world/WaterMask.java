package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;

public final class WaterMask {
    private WaterMask(){}

    /** @return true if the local 96-block radius is ≥ 80% water-like biomes. */
    public static boolean isWaterDominant(LevelAccessor level, BlockPos center) {
        final int R = 96;   // radius
        final int STEP = 16; // sampling stride (chunk-friendly)
        int water = 0, total = 0;

        for (int dz = -R; dz <= R; dz += STEP) {
            for (int dx = -R; dx <= R; dx += STEP) {
                Holder<Biome> b = level.getBiome(center.offset(dx, 0, dz));
                total++;
                if (isWaterBiome(b)) water++;
            }
        }
        return water * 100 >= total * 80; // ≥80% water
    }

    public static boolean isWaterBiome(Holder<Biome> biome) {
        return biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_DEEP_OCEAN)
                || biome.is(BiomeTags.IS_RIVER) || biome.is(BiomeTags.IS_BEACH);
    }
}