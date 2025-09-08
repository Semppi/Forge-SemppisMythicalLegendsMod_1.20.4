package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public final class WaterMask {
    private WaterMask(){}

    /** @return true if the local 96-block radius is ≥ 80% water-like biomes (surface-only, Overworld only). */
    public static boolean isWaterDominant(LevelAccessor level, BlockPos center) {
        // Only run in the Overworld
        if (level instanceof Level lvl && lvl.dimension() != Level.OVERWORLD) return false;

        final int R = 64;    // radius
        final int STEP = 32; // sampling stride (chunk-friendly)
        int water = 0, total = 0;

        for (int dz = -R; dz <= R; dz += STEP) {
            for (int dx = -R; dx <= R; dx += STEP) {
                int x = center.getX() + dx;
                int z = center.getZ() + dz;

                // sample at the surface, not underground
                int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
                Holder<Biome> b = level.getBiome(new BlockPos(x, y, z));

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