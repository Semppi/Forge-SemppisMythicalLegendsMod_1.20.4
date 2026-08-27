package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Public facade for the continental overlay.
 *
 * <p>Seed-only calls expose the underlying candidate geometry. Server calls
 * add the cached macro-climate direction assignment used by worldgen,
 * runtime, HUD and diagnostics.</p>
 */
public final class RegionSampler {
    /**
     * Candidate land lookup when no biome source is available.
     */
    public Region landRegion(long worldSeed, int x, int z) {
        return AuthoritativeRegionSampler.landRegion(worldSeed, x, z);
    }

    /**
     * Server convenience overload using exactly the same seed/X/Z path.
     */
    public Region landRegion(ServerLevel level, int x, int z) {
        return ClimateDirectionAssignment.landRegion(level, x, z);
    }

    public Region landRegion(ServerLevelAccessor level, int x, int z) {
        return ClimateDirectionAssignment.landRegion(level, x, z);
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
