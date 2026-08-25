package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Classifies the Overworld surface before choosing a continental or ocean
 * label. Player height and underground cave biomes never affect the result.
 */
public final class RegionSurfaceClassifier {
    private static final RegionSampler SAMPLER = new RegionSampler();

    private RegionSurfaceClassifier() {}

    public enum SurfaceKind {
        LAND,
        RIVER,
        OCEAN
    }

    public record Sample(SurfaceKind kind, Region region) {}

    public static Sample sample(ServerLevelAccessor level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        y = Math.max(level.getMinBuildHeight(), Math.min(y, level.getMaxBuildHeight() - 1));

        Holder<Biome> biome = level.getBiome(new BlockPos(x, y, z));
        SurfaceKind kind = classify(biome);

        // Rivers use the local land overlay under their own coordinates. This
        // lets a long river change labels at continental boundaries instead of
        // carrying one continent through the entire river.
        Region region = kind == SurfaceKind.OCEAN
                ? SAMPLER.seaRegion(level.getLevel().getSeed(), x, z)
                : SAMPLER.landRegion(level.getLevel().getSeed(), x, z);

        return new Sample(kind, region);
    }

    public static SurfaceKind classify(Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_RIVER)) {
            return SurfaceKind.RIVER;
        }
        if (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_DEEP_OCEAN)) {
            return SurfaceKind.OCEAN;
        }

        // Beaches, snowy beaches, stony shores, mushroom islands and unknown
        // modded biomes remain land unless their biome tags say otherwise.
        return SurfaceKind.LAND;
    }
}
