package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

/**
 * Allows biome suitability to nudge an existing land-region boundary without
 * allowing a biome to invent a region or replace the macro layout.
 */
public final class BoundedBiomeBorderAttractor {
    private static final int MAX_ATTRACTION = 192;
    private static final int BOUNDARY_PROBE = 256;
    private static final int BINARY_SEARCH_STEPS = 8;

    private static final double[][] DIRECTIONS = {
            { 1.0,  0.0}, { 0.70710678118,  0.70710678118},
            { 0.0,  1.0}, {-0.70710678118,  0.70710678118},
            {-1.0,  0.0}, {-0.70710678118, -0.70710678118},
            { 0.0, -1.0}, { 0.70710678118, -0.70710678118}
    };

    private BoundedBiomeBorderAttractor() {}

    public static Region attract(ServerLevelAccessor level, long seed, int x, int z,
                                 Holder<Biome> biome, Region original) {
        ResourceLocation biomeId = biome.unwrapKey()
                .map(key -> key.location())
                .orElse(null);
        if (biomeId == null || original.ocean()) {
            return original;
        }
        if (!canAttractBoundary(biomeId)) {
            return original;
        }

        int originalScore = affinityScore(original, biomeId);
        Candidate best = null;

        for (double[] direction : DIRECTIONS) {
            int probeX = offset(x, direction[0], BOUNDARY_PROBE);
            int probeZ = offset(z, direction[1], BOUNDARY_PROBE);
            Region competitor = AuthoritativeRegionSampler.landRegion(seed, probeX, probeZ);

            if (competitor.equals(original) || competitor.ocean()) {
                continue;
            }

            int competitorScore = affinityScore(competitor, biomeId);
            int advantage = competitorScore - originalScore;
            int allowedReach = attractionReach(advantage);
            if (allowedReach == 0) {
                continue;
            }

            double boundaryDistance = boundaryDistance(
                    seed, x, z, direction, original
            );
            if (boundaryDistance > allowedReach
                    || boundaryDistance > MAX_ATTRACTION) {
                continue;
            }
            if (!hasConnectedBiomePath(
                    level, x, z, direction, boundaryDistance,
                    original, competitor
            )) {
                continue;
            }

            Candidate candidate = new Candidate(
                    competitor, boundaryDistance, advantage
            );
            if (best == null
                    || candidate.distance() < best.distance()
                    || (candidate.distance() == best.distance()
                    && candidate.advantage() > best.advantage())) {
                best = candidate;
            }
        }

        return best == null ? original : best.region();
    }

    /**
     * Checks a short spine from this coordinate to the real neighboring cell.
     * This prevents a detached biome patch near a boundary from becoming an
     * isolated freckle of the neighboring region.
     */
    private static boolean hasConnectedBiomePath(
            ServerLevelAccessor level, int x, int z, double[] direction,
            double boundaryDistance, Region original, Region competitor) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        for (double distance = 32.0; distance < boundaryDistance; distance += 32.0) {
            int sampleX = offset(x, direction[0], distance);
            int sampleZ = offset(z, direction[1], distance);
            Holder<Biome> sampleBiome = biomeSource.getNoiseBiome(
                    QuartPos.fromBlock(sampleX), quartY,
                    QuartPos.fromBlock(sampleZ), climateSampler
            );
            ResourceLocation sampleId = sampleBiome.unwrapKey()
                    .map(key -> key.location())
                    .orElse(null);
            if (sampleId == null) {
                return false;
            }
            if (!canAttractBoundary(sampleId)) {
                return false;
            }

            int advantage = affinityScore(competitor, sampleId)
                    - affinityScore(original, sampleId);
            double remainingToBoundary = boundaryDistance - distance;
            if (attractionReach(advantage) < remainingToBoundary) {
                return false;
            }
        }
        return true;
    }

    /**
     * Only biomes carrying real macro-placement evidence may move a boundary.
     * Universal biomes, water/shore/cave context, mountains, special biomes
     * and unknown modded biomes all have zero placement weight.
     */
    private static boolean canAttractBoundary(ResourceLocation biomeId) {
        return TagRules.biomeProfile(biomeId).placementWeight() > 0;
    }

    private static double boundaryDistance(long seed, int x, int z,
                                           double[] direction, Region original) {
        double inside = 0.0;
        double outside = BOUNDARY_PROBE;

        for (int iteration = 0; iteration < BINARY_SEARCH_STEPS; iteration++) {
            double middle = (inside + outside) * 0.5;
            int sampleX = offset(x, direction[0], middle);
            int sampleZ = offset(z, direction[1], middle);
            Region sample = AuthoritativeRegionSampler.landRegion(seed, sampleX, sampleZ);

            if (sample.equals(original)) {
                inside = middle;
            } else {
                outside = middle;
            }
        }
        return outside;
    }

    private static int affinityScore(Region region, ResourceLocation biomeId) {
        return TagRules.directionAffinity(
                region.continent(), region.dir(), biomeId
        ).score();
    }

    /**
     * A one-point improvement is too weak to move a boundary. Better evidence
     * receives more room, but no result may move farther than 192 blocks.
     */
    private static int attractionReach(int advantage) {
        if (advantage >= 4) return 192;
        if (advantage == 3) return 144;
        if (advantage == 2) return 96;
        return 0;
    }

    private static int offset(int origin, double direction, double distance) {
        return origin + (int) Math.round(direction * distance);
    }

    private record Candidate(Region region, double distance, int advantage) {}
}
