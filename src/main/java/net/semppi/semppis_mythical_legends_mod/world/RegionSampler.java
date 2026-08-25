package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.server.level.ServerLevel;

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
}
