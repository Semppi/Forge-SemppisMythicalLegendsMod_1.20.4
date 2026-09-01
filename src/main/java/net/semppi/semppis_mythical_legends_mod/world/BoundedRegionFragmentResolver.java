package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Moves a small raw-region remnant to the larger neighboring owner of the
 * same surface biome. This places the surviving regional seam on the biome's
 * outer edge without allowing a large or ambiguous land area to be rewritten.
 */
public final class BoundedRegionFragmentResolver {
    private static final int MAX_FRAGMENT_CELLS = 2_048;
    private static final int MIN_BOUNDARY_SUPPORT = 3;
    private static final int MIN_BOUNDARY_LEAD = 2;
    private static final int MAX_CACHE_ENTRIES = 65_536;

    private static final int[][] CARDINAL_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    private static final Map<ServerLevel, FragmentCache> WORLD_CACHES =
            new WeakHashMap<>();

    private BoundedRegionFragmentResolver() {}

    public static Region resolve(
            ServerLevelAccessor level,
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
        Region rawOwner = ClimateDirectionAssignment.landRegion(level, x, z);

        // A previous pass already made a supported decision here. Preserve it
        // instead of allowing this cleanup pass to compete with that result.
        if (!current.equals(rawOwner)) {
            return current;
        }

        long startKey = cellKey(quartX, quartZ);
        FragmentCache cache = cacheFor(level.getLevel());
        FragmentDecision cached = cache.get(startKey, biomeId, rawOwner);
        if (cached != null) {
            return cached.winner() == null ? current : cached.winner();
        }

        FragmentSearch fragment = surveyFragment(
                level, quartX, quartZ, biomeId, rawOwner,
                MAX_FRAGMENT_CELLS
        );
        if (fragment.exceededBounds()) {
            cache.putAll(
                    fragment.cells(),
                    new FragmentDecision(biomeId, rawOwner, null)
            );
            return current;
        }

        Region winner = selectBoundaryWinner(fragment, biomeId);
        if (winner != null) {
            Cell neighbor = fragment.neighborSeeds().get(winner);
            FragmentSearch competitor = surveyFragment(
                    level,
                    neighbor.x(),
                    neighbor.z(),
                    biomeId,
                    winner,
                    fragment.cells().size()
            );
            if (!competitor.exceededBounds()
                    && competitor.cells().size() <= fragment.cells().size()) {
                winner = null;
            }
        }

        FragmentDecision decision = new FragmentDecision(
                biomeId, rawOwner, winner
        );
        cache.putAll(fragment.cells(), decision);
        return winner == null ? current : winner;
    }

    private static FragmentSearch surveyFragment(
            ServerLevelAccessor level,
            int startQuartX,
            int startQuartZ,
            ResourceLocation biomeId,
            Region owner,
            int maximumCells
    ) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        ArrayDeque<Cell> open = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        Map<Region, Integer> boundarySupport = new LinkedHashMap<>();
        Map<Region, Cell> neighborSeeds = new LinkedHashMap<>();
        open.add(new Cell(startQuartX, startQuartZ));
        boolean exceededBounds = false;

        while (!open.isEmpty()) {
            Cell cell = open.removeFirst();
            long key = cellKey(cell.x(), cell.z());
            if (!visited.add(key)) {
                continue;
            }
            if (visited.size() > maximumCells) {
                exceededBounds = true;
                break;
            }

            for (int[] direction : CARDINAL_DIRECTIONS) {
                int neighborX = cell.x() + direction[0];
                int neighborZ = cell.z() + direction[1];
                long neighborKey = cellKey(neighborX, neighborZ);
                if (visited.contains(neighborKey)) {
                    continue;
                }

                Holder<Biome> neighborBiome = biomeSource.getNoiseBiome(
                        neighborX, quartY, neighborZ, climateSampler
                );
                ResourceLocation neighborId = neighborBiome.unwrapKey()
                        .map(value -> value.location())
                        .orElse(null);
                if (!biomeId.equals(neighborId)) {
                    continue;
                }

                int blockX = neighborX * 4 + 2;
                int blockZ = neighborZ * 4 + 2;
                Region neighborOwner = ClimateDirectionAssignment.landRegion(
                        level, blockX, blockZ
                );
                if (owner.equals(neighborOwner)) {
                    open.addLast(new Cell(neighborX, neighborZ));
                } else if (!neighborOwner.ocean()) {
                    boundarySupport.merge(neighborOwner, 1, Integer::sum);
                    neighborSeeds.putIfAbsent(
                            neighborOwner,
                            new Cell(neighborX, neighborZ)
                    );
                }
            }
        }

        return new FragmentSearch(
                visited, boundarySupport, neighborSeeds, exceededBounds
        );
    }

    private static Region selectBoundaryWinner(
            FragmentSearch fragment,
            ResourceLocation biomeId
    ) {
        Region winner = null;
        int winnerSupport = 0;
        int runnerUpSupport = 0;

        for (Map.Entry<Region, Integer> entry
                : fragment.boundarySupport().entrySet()) {
            Region candidate = entry.getKey();
            if (TagRules.directionAffinity(
                    candidate.continent(), candidate.dir(), biomeId
            ) == TagRules.Affinity.STRONGLY_UNSUITABLE) {
                continue;
            }
            int support = entry.getValue();
            if (support > winnerSupport) {
                runnerUpSupport = winnerSupport;
                winner = candidate;
                winnerSupport = support;
            } else if (support > runnerUpSupport) {
                runnerUpSupport = support;
            }
        }

        if (winner == null
                || winnerSupport < MIN_BOUNDARY_SUPPORT
                || winnerSupport < runnerUpSupport + MIN_BOUNDARY_LEAD) {
            return null;
        }
        return winner;
    }

    private static FragmentCache cacheFor(ServerLevel level) {
        synchronized (WORLD_CACHES) {
            return WORLD_CACHES.computeIfAbsent(
                    level, ignored -> new FragmentCache()
            );
        }
    }

    private static long cellKey(int quartX, int quartZ) {
        return ((long) quartX << 32) ^ (quartZ & 0xFFFFFFFFL);
    }

    private record Cell(int x, int z) {}

    private record FragmentSearch(
            Set<Long> cells,
            Map<Region, Integer> boundarySupport,
            Map<Region, Cell> neighborSeeds,
            boolean exceededBounds
    ) {}

    private record FragmentDecision(
            ResourceLocation biomeId,
            Region original,
            Region winner
    ) {}

    private static final class FragmentCache {
        private final Map<Long, FragmentDecision> values =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, FragmentDecision> eldest
                    ) {
                        return size() > MAX_CACHE_ENTRIES;
                    }
                };

        private synchronized FragmentDecision get(
                long key,
                ResourceLocation biomeId,
                Region original
        ) {
            FragmentDecision decision = values.get(key);
            return decision != null
                    && decision.biomeId().equals(biomeId)
                    && decision.original().equals(original)
                    ? decision
                    : null;
        }

        private synchronized void putAll(
                Set<Long> cells,
                FragmentDecision decision
        ) {
            for (long cell : cells) {
                values.put(cell, decision);
            }
        }
    }
}
