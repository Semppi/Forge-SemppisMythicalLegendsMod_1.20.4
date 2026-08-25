package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Public facade for the continental overlay.
 *
 * <p>Every caller reaches {@link AuthoritativeRegionSampler}; there is no
 * separate worldgen, runtime, HUD or biome-consensus answer.</p>
 */
public final class RegionSampler {
    /**
     * Authoritative land lookup from immutable inputs.
     */
    public Region landRegion(long worldSeed, int x, int z) {
        return AuthoritativeRegionSampler.landRegion(worldSeed, x, z);
    }

    /**
     * Server convenience overload using exactly the same seed/X/Z path.
     */
    public Region landRegion(ServerLevel level, int x, int z) {
        return landRegion(level.getSeed(), x, z);
    }

    /**
     * Authoritative ocean-basin lookup from immutable inputs.
     */
    public Region seaRegion(long worldSeed, int x, int z) {
        return AuthoritativeRegionSampler.seaRegion(worldSeed, x, z);
    }

    /**
     * Server convenience overload using exactly the same seed/X/Z path.
     */
    public Region seaRegion(ServerLevel level, int x, int z) {
        return seaRegion(level.getSeed(), x, z);
    }

    /**
     * Retained for cache diagnostics only. Region ownership never depends on
     * whether this method returns a biome or {@code null}.
     */
    public static ResourceLocation biomeIdIfLoaded(ServerLevel level, int x, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        if (!level.getChunkSource().hasChunk(chunkX, chunkZ)) return null;

        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        y = Math.max(level.getMinBuildHeight(), Math.min(y, level.getMaxBuildHeight() - 1));
        Holder<Biome> biome = level.getBiome(new BlockPos(x, y, z));

        return level.registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                .getKey(biome.value());
    }
}
