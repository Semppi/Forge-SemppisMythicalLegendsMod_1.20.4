package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Snaps a narrow resolved-region band to a nearby land-biome edge. Unlike the
 * component resolvers, this local fallback can operate when both territories
 * continue far beyond their bounded search limits.
 */
public final class BoundedBiomeEdgeSnapper {
    private static final int SAMPLE_STEP = 4;
    private static final int MAX_SIDE_DISTANCE = 64;
    private static final int MAX_BAND_WIDTH = 96;
    private static final int LANE_OFFSET = 8;
    private static final int MIN_LANE_SUPPORT = 2;
    private static final int MAX_CACHE_ENTRIES = 65_536;

    private static final int[][] DIRECTIONS = {
            {1, 0}, {1, 1}, {0, 1}, {-1, 1},
            {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
    };

    private static final Map<ServerLevel, SnapCache> WORLD_CACHES =
            new WeakHashMap<>();

    private BoundedBiomeEdgeSnapper() {}

    public static Region resolve(
            ServerLevelAccessor level,
            long seed,
            int x,
            int z,
            Holder<Biome> biome,
            Region current
    ) {
        ResourceLocation biomeId = biome.unwrapKey()
                .map(key -> key.location())
                .orElse(null);
        if (biomeId == null || current.ocean()) {
            return current;
        }

        TagRules.BiomeClimateProfile profile = TagRules.biomeProfile(biomeId);
        if (profile.isPlacementContext() || profile.isMountain()) {
            return current;
        }

        int quartX = QuartPos.fromBlock(x);
        int quartZ = QuartPos.fromBlock(z);
        long key = cellKey(quartX, quartZ);
        SnapCache cache = cacheFor(level.getLevel());
        SnapDecision cached = cache.get(key, biomeId, current);
        if (cached != null) {
            return cached.winner() == null ? current : cached.winner();
        }

        Probe probe = new Probe(level, seed);
        Candidate best = null;
        for (int[] direction : DIRECTIONS) {
            Candidate candidate = candidate(
                    probe, x, z, biomeId, current, direction
            );
            if (candidate == null) {
                continue;
            }

            int laneSupport = 1;
            int perpendicularX = -direction[1];
            int perpendicularZ = direction[0];
            for (int side : new int[]{-1, 1}) {
                int laneX = x + perpendicularX * LANE_OFFSET * side;
                int laneZ = z + perpendicularZ * LANE_OFFSET * side;
                Candidate lane = candidate(
                        probe, laneX, laneZ, biomeId, current, direction
                );
                if (lane != null
                        && lane.winner().equals(candidate.winner())) {
                    laneSupport++;
                }
            }
            if (laneSupport < MIN_LANE_SUPPORT) {
                continue;
            }

            if (best == null || candidate.width() < best.width()) {
                best = candidate;
            }
        }

        Region winner = best == null ? null : best.winner();
        cache.put(key, new SnapDecision(biomeId, current, winner));
        return winner == null ? current : winner;
    }

    private static Candidate candidate(
            Probe probe,
            int x,
            int z,
            ResourceLocation biomeId,
            Region current,
            int[] edgeDirection
    ) {
        if (!biomeId.equals(probe.biomeId(x, z))) {
            return null;
        }

        int edgeDistance = probe.landBiomeEdgeDistance(
                x, z, biomeId, edgeDirection
        );
        if (edgeDistance == 0) {
            return null;
        }

        OwnerMatch competitor = probe.competitorDistance(
                x, z, biomeId, current,
                -edgeDirection[0], -edgeDirection[1]
        );
        if (competitor == null
                || edgeDistance + competitor.distance() > MAX_BAND_WIDTH) {
            return null;
        }
        if (TagRules.directionAffinity(
                competitor.owner().continent(),
                competitor.owner().dir(),
                biomeId
        ) == TagRules.Affinity.STRONGLY_UNSUITABLE) {
            return null;
        }
        return new Candidate(
                competitor.owner(), edgeDistance + competitor.distance()
        );
    }

    private static SnapCache cacheFor(ServerLevel level) {
        synchronized (WORLD_CACHES) {
            return WORLD_CACHES.computeIfAbsent(
                    level, ignored -> new SnapCache()
            );
        }
    }

    private static long cellKey(int quartX, int quartZ) {
        return ((long) quartX << 32) ^ (quartZ & 0xFFFFFFFFL);
    }

    private record Candidate(Region winner, int width) {}

    private record OwnerMatch(Region owner, int distance) {}

    private record SnapDecision(
            ResourceLocation biomeId,
            Region original,
            Region winner
    ) {}

    private static final class Probe {
        private final ServerLevelAccessor level;
        private final long seed;
        private final net.minecraft.world.level.biome.BiomeSource biomeSource;
        private final net.minecraft.world.level.biome.Climate.Sampler sampler;
        private final int quartY;

        private Probe(ServerLevelAccessor level, long seed) {
            this.level = level;
            this.seed = seed;
            var chunkSource = level.getLevel().getChunkSource();
            var generator = chunkSource.getGenerator();
            this.biomeSource = generator.getBiomeSource();
            this.sampler = chunkSource.randomState().sampler();
            this.quartY = QuartPos.fromBlock(generator.getSeaLevel());
        }

        private ResourceLocation biomeId(int x, int z) {
            return biome(x, z).unwrapKey()
                    .map(key -> key.location())
                    .orElse(null);
        }

        private Holder<Biome> biome(int x, int z) {
            return biomeSource.getNoiseBiome(
                    QuartPos.fromBlock(x), quartY,
                    QuartPos.fromBlock(z), sampler
            );
        }

        private int landBiomeEdgeDistance(
                int x,
                int z,
                ResourceLocation originBiome,
                int[] direction
        ) {
            for (int distance = SAMPLE_STEP;
                 distance <= MAX_SIDE_DISTANCE;
                 distance += SAMPLE_STEP) {
                int sampleX = x + direction[0] * distance;
                int sampleZ = z + direction[1] * distance;
                Holder<Biome> sample = biome(sampleX, sampleZ);
                ResourceLocation sampleId = sample.unwrapKey()
                        .map(key -> key.location())
                        .orElse(null);
                if (originBiome.equals(sampleId)) {
                    continue;
                }
                if (sampleId == null) {
                    return 0;
                }
                if (RegionSurfaceClassifier.classify(sample)
                        != RegionSurfaceClassifier.SurfaceKind.LAND) {
                    return 0;
                }
                TagRules.BiomeClimateProfile profile =
                        TagRules.biomeProfile(sampleId);
                return profile.isPlacementContext()
                        || profile.isMountain()
                        || profile.placementWeight() <= 0
                        ? 0
                        : distance;
            }
            return 0;
        }

        private OwnerMatch competitorDistance(
                int x,
                int z,
                ResourceLocation originBiome,
                Region current,
                int directionX,
                int directionZ
        ) {
            for (int distance = SAMPLE_STEP;
                 distance <= MAX_SIDE_DISTANCE;
                 distance += SAMPLE_STEP) {
                int sampleX = x + directionX * distance;
                int sampleZ = z + directionZ * distance;
                Holder<Biome> sample = biome(sampleX, sampleZ);
                if (!originBiome.equals(sample.unwrapKey()
                        .map(key -> key.location()).orElse(null))) {
                    return null;
                }
                Region owner = RegionSurfaceClassifier
                        .resolveLandBeforeVacuum(
                                level, seed, sampleX, sampleZ, sample
                        );
                if (!current.equals(owner) && !owner.ocean()) {
                    return new OwnerMatch(owner, distance);
                }
            }
            return null;
        }
    }

    private static final class SnapCache {
        private final Map<Long, SnapDecision> values =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, SnapDecision> eldest
                    ) {
                        return size() > MAX_CACHE_ENTRIES;
                    }
                };

        private synchronized SnapDecision get(
                long key,
                ResourceLocation biomeId,
                Region original
        ) {
            SnapDecision decision = values.get(key);
            return decision != null
                    && decision.biomeId().equals(biomeId)
                    && decision.original().equals(original)
                    ? decision
                    : null;
        }

        private synchronized void put(long key, SnapDecision decision) {
            values.put(key, decision);
        }
    }
}
