package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Atomically assigns a complete, bounded surface-biome component to the Raw
 * region that already owns a clear majority of it. Accepted components move
 * as one piece; incomplete or ambiguous components retain Raw.
 */
public final class RegionBoundaryRouter {
    private static final int MAX_COMPONENT_CELLS = 4_096;
    private static final int MIN_MINORITY_CELLS = 3;
    private static final int MIN_WINNING_LEAD = 8;
    private static final int MIN_WINNING_PERCENT = 56;
    private static final int MAX_CACHE_ENTRIES = 98_304;
    private static final int NEGATIVE_TILE_SHIFT = 5;

    private static final int[][] CARDINAL_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };
    private static final Map<ServerLevel, ComponentCache> WORLD_CACHES =
            new WeakHashMap<>();

    private RegionBoundaryRouter() {}

    public static Region resolve(
            ServerLevelAccessor level, long seed, int x, int z,
            Holder<Biome> biome, Region rawOwner
    ) {
        ResourceLocation biomeId = biome.unwrapKey()
                .map(key -> key.location()).orElse(null);
        if (biomeId == null || rawOwner.ocean()) return rawOwner;

        TagRules.BiomeClimateProfile profile = TagRules.biomeProfile(biomeId);
        if (profile.isPlacementContext() || profile.isMountain()) {
            return rawOwner;
        }

        int quartX = QuartPos.fromBlock(x);
        int quartZ = QuartPos.fromBlock(z);
        long startKey = cellKey(quartX, quartZ);
        ComponentCache cache = cacheFor(level.getLevel());
        Region cached = cache.get(startKey, biomeId);
        if (cached != null) return cached;
        if (cache.isOversizedTile(quartX, quartZ, biomeId)) return rawOwner;

        ComponentSearch component = surveyComponent(
                level, quartX, quartZ, biomeId
        );
        if (component.exceededBounds()) {
            cache.markOversizedTiles(component.cells(), biomeId);
            return rawOwner;
        }

        Region winner = selectWinner(component.ownerCounts());
        if (winner == null) {
            cache.putRaw(component.cells(), biomeId, component.owners());
            return rawOwner;
        }

        cache.putWinner(component.cells(), biomeId, winner);
        return winner;
    }

    private static ComponentSearch surveyComponent(
            ServerLevelAccessor level, int startQuartX, int startQuartZ,
            ResourceLocation biomeId
    ) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        ArrayDeque<Cell> open = new ArrayDeque<>();
        Set<Long> cells = new HashSet<>();
        Map<Long, Region> owners = new HashMap<>();
        Map<Region, Integer> ownerCounts = new LinkedHashMap<>();
        open.add(new Cell(startQuartX, startQuartZ));

        while (!open.isEmpty()) {
            Cell cell = open.removeFirst();
            long key = cellKey(cell.x(), cell.z());
            if (!cells.add(key)) continue;
            if (cells.size() > MAX_COMPONENT_CELLS) {
                return new ComponentSearch(cells, owners, ownerCounts, true);
            }

            Region owner = ClimateDirectionAssignment.landRegion(
                    level, cell.x() * 4 + 2, cell.z() * 4 + 2
            );
            owners.put(key, owner);
            ownerCounts.merge(owner, 1, Integer::sum);

            for (int[] direction : CARDINAL_DIRECTIONS) {
                int neighborX = cell.x() + direction[0];
                int neighborZ = cell.z() + direction[1];
                long neighborKey = cellKey(neighborX, neighborZ);
                if (cells.contains(neighborKey)) continue;

                Holder<Biome> neighborBiome = biomeSource.getNoiseBiome(
                        neighborX, quartY, neighborZ, climateSampler
                );
                ResourceLocation neighborId = neighborBiome.unwrapKey()
                        .map(value -> value.location()).orElse(null);
                if (biomeId.equals(neighborId)) {
                    open.addLast(new Cell(neighborX, neighborZ));
                }
            }
        }
        return new ComponentSearch(cells, owners, ownerCounts, false);
    }

    private static Region selectWinner(Map<Region, Integer> ownerCounts) {
        if (ownerCounts.size() < 2) return null;

        Region winner = null;
        int winnerCount = 0;
        int runnerUpCount = 0;
        int total = 0;
        for (Map.Entry<Region, Integer> entry : ownerCounts.entrySet()) {
            int count = entry.getValue();
            total += count;
            if (count > winnerCount) {
                runnerUpCount = winnerCount;
                winner = entry.getKey();
                winnerCount = count;
            } else if (count > runnerUpCount) {
                runnerUpCount = count;
            }
        }

        if (winner == null
                || runnerUpCount < MIN_MINORITY_CELLS
                || winnerCount < runnerUpCount + MIN_WINNING_LEAD
                || winnerCount * 100 < total * MIN_WINNING_PERCENT) {
            return null;
        }
        return winner;
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

    private static long tileKey(int quartX, int quartZ) {
        return cellKey(quartX >> NEGATIVE_TILE_SHIFT,
                quartZ >> NEGATIVE_TILE_SHIFT);
    }

    private record Cell(int x, int z) {}
    private record ComponentSearch(
            Set<Long> cells, Map<Long, Region> owners,
            Map<Region, Integer> ownerCounts, boolean exceededBounds
    ) {}
    private record CachedDecision(ResourceLocation biomeId, Region region) {}

    private static final class ComponentCache {
        private final Map<Long, CachedDecision> values =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, CachedDecision> eldest
                    ) {
                        return size() > MAX_CACHE_ENTRIES;
                    }
                };
        private final Map<Long, ResourceLocation> oversizedTiles =
                new LinkedHashMap<>(128, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, ResourceLocation> eldest
                    ) {
                        return size() > 8_192;
                    }
                };

        private synchronized Region get(long key, ResourceLocation biomeId) {
            CachedDecision decision = values.get(key);
            return decision != null && decision.biomeId().equals(biomeId)
                    ? decision.region() : null;
        }

        private synchronized void putWinner(
                Set<Long> cells, ResourceLocation biomeId, Region winner
        ) {
            CachedDecision decision = new CachedDecision(biomeId, winner);
            for (long key : cells) values.put(key, decision);
        }

        private synchronized void putRaw(
                Set<Long> cells, ResourceLocation biomeId,
                Map<Long, Region> owners
        ) {
            for (long key : cells) {
                values.put(key, new CachedDecision(biomeId, owners.get(key)));
            }
        }

        private synchronized boolean isOversizedTile(
                int quartX, int quartZ, ResourceLocation biomeId
        ) {
            return biomeId.equals(oversizedTiles.get(tileKey(quartX, quartZ)));
        }

        private synchronized void markOversizedTiles(
                Set<Long> cells, ResourceLocation biomeId
        ) {
            for (long key : cells) {
                int quartX = (int) (key >> 32);
                int quartZ = (int) key;
                oversizedTiles.put(tileKey(quartX, quartZ), biomeId);
            }
        }
    }
}
