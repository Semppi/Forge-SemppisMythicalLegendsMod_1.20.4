package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Prepares bounded deterministic border routes without searching from the
 * runtime lookup path. A tile publishes either one complete route or Raw.
 */
public final class RegionBoundaryRouter {
    static final int TILE_QUARTS = 64;
    private static final int TILE_CELLS = TILE_QUARTS * TILE_QUARTS;
    private static final int MAX_CACHE_ENTRIES = 131_072;
    private static final int MAX_PREPARED_TILES = 8_192;
    private static final Map<ServerLevel, RouteCache> WORLD_CACHES =
            new WeakHashMap<>();

    private RegionBoundaryRouter() {}

    /** Runtime-safe: one bounded cache lookup, otherwise Raw. */
    public static Region resolvePreparedOrRaw(
            ServerLevelAccessor level, int x, int z,
            Holder<Biome> biome, Region rawOwner
    ) {
        Region prepared = cacheFor(level.getLevel()).get(
                cellKey(QuartPos.fromBlock(x), QuartPos.fromBlock(z))
        );
        return prepared == null ? rawOwner : prepared;
    }

    /** Explicit preparation used by diagnostics and a future budgeted worker. */
    public static Region prepareAndResolve(
            ServerLevelAccessor level, long seed, int x, int z,
            Holder<Biome> biome, Region rawOwner
    ) {
        int quartX = QuartPos.fromBlock(x);
        int quartZ = QuartPos.fromBlock(z);
        int tileX = Math.floorDiv(quartX, TILE_QUARTS);
        int tileZ = Math.floorDiv(quartZ, TILE_QUARTS);
        RouteCache cache = cacheFor(level.getLevel());
        long tileKey = cellKey(tileX, tileZ);
        long queryKey = cellKey(quartX, quartZ);
        if (!cache.isPrepared(tileKey) || cache.get(queryKey) == null) {
            cache.publish(tileKey, prepareTile(level, tileX, tileZ));
        }
        Region prepared = cache.get(queryKey);
        return prepared == null ? rawOwner : prepared;
    }

    private static PreparedTile prepareTile(
            ServerLevelAccessor level, int tileX, int tileZ
    ) {
        int originX = tileX * TILE_QUARTS;
        int originZ = tileZ * TILE_QUARTS;
        Region[] raw = new Region[TILE_CELLS];
        ResourceLocation[] biomes = new ResourceLocation[TILE_CELLS];
        boolean[] rivers = new boolean[TILE_CELLS];
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        Region first = null;
        Region second = null;
        for (int z = 0; z < TILE_QUARTS; z++) {
            for (int x = 0; x < TILE_QUARTS; x++) {
                int index = index(x, z);
                int quartX = originX + x;
                int quartZ = originZ + z;
                Region owner = ClimateDirectionAssignment.landRegion(
                        level, QuartPos.toBlock(quartX) + 2,
                        QuartPos.toBlock(quartZ) + 2
                );
                raw[index] = owner;
                if (!owner.ocean()) {
                    if (first == null) first = owner;
                    else if (!owner.equals(first) && second == null) {
                        second = owner;
                    } else if (!owner.equals(first)
                            && !owner.equals(second)) {
                        return PreparedTile.raw(originX, originZ, raw);
                    }
                }
                Holder<Biome> sample = biomeSource.getNoiseBiome(
                        quartX, quartY, quartZ, climateSampler
                );
                biomes[index] = sample.unwrapKey()
                        .map(key -> key.location()).orElse(null);
                rivers[index] = sample.is(BiomeTags.IS_RIVER);
            }
        }
        if (first == null || second == null) {
            return PreparedTile.raw(originX, originZ, raw);
        }
        Region[] routed = BoundedRegionPathRouter.route(
                raw, biomes, rivers, first, second
        );
        return routed == null
                ? PreparedTile.raw(originX, originZ, raw)
                : new PreparedTile(originX, originZ, routed);
    }

    static int index(int x, int z) {
        return z * TILE_QUARTS + x;
    }

    private static RouteCache cacheFor(ServerLevel level) {
        synchronized (WORLD_CACHES) {
            return WORLD_CACHES.computeIfAbsent(
                    level, ignored -> new RouteCache()
            );
        }
    }

    private static long cellKey(int quartX, int quartZ) {
        return ((long) quartX << 32) ^ (quartZ & 0xFFFFFFFFL);
    }

    private record PreparedTile(int originX, int originZ, Region[] regions) {
        private static PreparedTile raw(int x, int z, Region[] raw) {
            return new PreparedTile(x, z, Arrays.copyOf(raw, raw.length));
        }
    }

    private static final class RouteCache {
        private final Map<Long, Region> cells =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, Region> eldest
                    ) { return size() > MAX_CACHE_ENTRIES; }
                };
        private final Map<Long, Boolean> preparedTiles =
                new LinkedHashMap<>(128, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, Boolean> eldest
                    ) { return size() > MAX_PREPARED_TILES; }
                };

        private synchronized Region get(long key) { return cells.get(key); }
        private synchronized boolean isPrepared(long key) {
            return preparedTiles.containsKey(key);
        }
        private synchronized void publish(long key, PreparedTile prepared) {
            for (int z = 0; z < TILE_QUARTS; z++) {
                for (int x = 0; x < TILE_QUARTS; x++) {
                    cells.put(cellKey(prepared.originX() + x,
                            prepared.originZ() + z),
                            prepared.regions()[index(x, z)]);
                }
            }
            preparedTiles.put(key, Boolean.TRUE);
        }
    }
}
