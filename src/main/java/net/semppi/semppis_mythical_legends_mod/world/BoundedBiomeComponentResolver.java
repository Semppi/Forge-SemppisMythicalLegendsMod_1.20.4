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
 * Gives a finite connected surface-biome component to the land region that
 * already owns most of it. The search reads the generator biome source only;
 * it never loads chunks or changes biome generation.
 */
public final class BoundedBiomeComponentResolver {
    private static final int QUART_BLOCKS = 4;
    private static final int MAX_COMPONENT_CELLS = 8_192;
    private static final int MAX_CACHE_ENTRIES = 65_536;

    private static final int[][] CARDINAL_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    private static final Map<ServerLevel, ComponentCache> WORLD_CACHES =
            new WeakHashMap<>();

    private BoundedBiomeComponentResolver() {}

    public static Region resolve(ServerLevelAccessor level, long seed,
                                 int x, int z, Holder<Biome> biome,
                                 Region original) {
        ResourceLocation biomeId = biome.unwrapKey()
                .map(key -> key.location())
                .orElse(null);
        if (biomeId == null || original.ocean()) {
            return original;
        }

        TagRules.BiomeClimateProfile profile = TagRules.biomeProfile(biomeId);
        if (profile.isPlacementContext() || profile.isMountain()) {
            return original;
        }

        int startQuartX = QuartPos.fromBlock(x);
        int startQuartZ = QuartPos.fromBlock(z);
        long startKey = cellKey(startQuartX, startQuartZ);
        ComponentCache cache = cacheFor(level.getLevel());
        ComponentDecision cached = cache.get(startKey, biomeId);
        if (cached != null) {
            return cached.winner() == null ? original : cached.winner();
        }

        ComponentSearch search = survey(
                level, seed, startQuartX, startQuartZ, biomeId
        );
        ComponentDecision decision = chooseWinner(
                search, biomeId
        );
        cache.putAll(search.cells(), decision);
        return decision.winner() == null ? original : decision.winner();
    }

    private static ComponentSearch survey(
            ServerLevelAccessor level, long seed,
            int startQuartX, int startQuartZ, ResourceLocation biomeId) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        ArrayDeque<Cell> open = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        Map<Region, Integer> ownership = new LinkedHashMap<>();
        open.add(new Cell(startQuartX, startQuartZ));
        boolean exceededBounds = false;

        while (!open.isEmpty()) {
            Cell cell = open.removeFirst();
            long key = cellKey(cell.x(), cell.z());
            if (!visited.add(key)) {
                continue;
            }
            if (visited.size() > MAX_COMPONENT_CELLS) {
                exceededBounds = true;
                break;
            }

            int blockX = cell.x() * QUART_BLOCKS + QUART_BLOCKS / 2;
            int blockZ = cell.z() * QUART_BLOCKS + QUART_BLOCKS / 2;
            Region owner = ClimateDirectionAssignment.landRegion(
                    level, blockX, blockZ
            );
            ownership.merge(owner, 1, Integer::sum);

            for (int[] direction : CARDINAL_DIRECTIONS) {
                int neighborX = cell.x() + direction[0];
                int neighborZ = cell.z() + direction[1];
                long neighborKey = cellKey(neighborX, neighborZ);
                if (visited.contains(neighborKey)) {
                    continue;
                }

                Holder<Biome> neighbor = biomeSource.getNoiseBiome(
                        neighborX, quartY, neighborZ, climateSampler
                );
                ResourceLocation neighborId = neighbor.unwrapKey()
                        .map(value -> value.location())
                        .orElse(null);
                if (!biomeId.equals(neighborId)) {
                    continue;
                }

                open.addLast(new Cell(neighborX, neighborZ));
            }
        }

        return new ComponentSearch(visited, ownership, exceededBounds);
    }

    private static ComponentDecision chooseWinner(
            ComponentSearch search, ResourceLocation biomeId) {
        if (search.exceededBounds()) {
            return new ComponentDecision(biomeId, null);
        }

        Region winner = null;
        int winnerCells = 0;
        int runnerUpCells = 0;

        for (Map.Entry<Region, Integer> entry : search.ownership().entrySet()) {
            Region candidate = entry.getKey();
            if (TagRules.directionAffinity(
                    candidate.continent(), candidate.dir(), biomeId
            ) == TagRules.Affinity.STRONGLY_UNSUITABLE) {
                continue;
            }

            int cells = entry.getValue();
            if (cells > winnerCells) {
                runnerUpCells = winnerCells;
                winner = candidate;
                winnerCells = cells;
            } else if (cells > runnerUpCells) {
                runnerUpCells = cells;
            }
        }

        // An exact or nearly exact split retains the ordinary boundary. A
        // component needs a real two-cell lead before it may move the seam.
        if (winner == null || winnerCells < runnerUpCells + 2) {
            return new ComponentDecision(biomeId, null);
        }
        return new ComponentDecision(biomeId, winner);
    }

    private static ComponentCache cacheFor(ServerLevel level) {
        synchronized (WORLD_CACHES) {
            return WORLD_CACHES.computeIfAbsent(
                    level, ignored -> new ComponentCache()
            );
        }
    }

    private static long cellKey(int quartX, int quartZ) {
        return ((long) quartX << 32) ^ (quartZ & 0xFFFFFFFFL);
    }

    private record Cell(int x, int z) {}

    private record ComponentSearch(Set<Long> cells,
                                   Map<Region, Integer> ownership,
                                   boolean exceededBounds) {}

    /**
     * The biome identity is part of the cached result so /fillbiome and other
     * runtime biome editors cannot reuse a decision calculated for the biome
     * that previously occupied this coordinate.
     */
    private record ComponentDecision(
            ResourceLocation biomeId,
            Region winner
    ) {}

    private static final class ComponentCache {
        private final Map<Long, ComponentDecision> values =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, ComponentDecision> eldest) {
                        return size() > MAX_CACHE_ENTRIES;
                    }
                };

        private synchronized ComponentDecision get(
                long key, ResourceLocation biomeId) {
            ComponentDecision decision = values.get(key);
            return decision != null && decision.biomeId().equals(biomeId)
                    ? decision : null;
        }

        private synchronized void putAll(
                Set<Long> cells, ComponentDecision decision) {
            for (long cell : cells) {
                values.put(cell, decision);
            }
        }
    }
}
