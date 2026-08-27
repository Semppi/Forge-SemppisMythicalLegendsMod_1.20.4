package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Assigns a deterministic 0-10 activity number to a generated biome mass
 * inside one resolved region. This is diagnostic data only; it does not alter
 * spawning yet and never loads chunks.
 */
public final class BiomeActivitySampler {
    private static final int MAX_COMPONENT_CELLS = 4_096;
    private static final int OVERSIZED_CELL_BLOCKS = 1_024;
    private static final int MAX_CACHE_ENTRIES = 65_536;
    private static final int ACTIVITY_LEVELS = 11;

    private static final int[][] CARDINAL_DIRECTIONS = {
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    };

    private static final Map<ServerLevel, ActivityCache> WORLD_CACHES =
            new WeakHashMap<>();

    private BiomeActivitySampler() {}

    public static ActivitySample sample(
            ServerLevelAccessor level, int x, int z, Region region) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var randomState = chunkSource.randomState();
        int surfaceY = generator.getBaseHeight(
                x, z, Heightmap.Types.WORLD_SURFACE,
                level, randomState
        );
        surfaceY = Math.max(
                level.getMinBuildHeight(),
                Math.min(surfaceY, level.getMaxBuildHeight() - 1)
        );
        int quartX = QuartPos.fromBlock(x);
        int quartY = QuartPos.fromBlock(surfaceY);
        int quartZ = QuartPos.fromBlock(z);
        Holder<Biome> biome = generator.getBiomeSource().getNoiseBiome(
                quartX, quartY, quartZ, randomState.sampler()
        );
        ResourceLocation biomeId = biome.unwrapKey()
                .map(key -> key.location())
                .orElse(new ResourceLocation("sml", "unknown"));

        long startKey = cellKey(quartX, quartZ);
        ActivityCache cache = cacheFor(level.getLevel());
        ComponentIdentity identity = cache.get(startKey);
        if (identity == null) {
            ComponentSearch search = survey(
                    level, quartX, quartY, quartZ, biomeId
            );
            identity = search.oversized()
                    ? ComponentIdentity.large()
                    : ComponentIdentity.finite(
                            search.anchorX(), search.anchorZ()
                    );
            cache.putAll(search.cells(), identity);
        }

        if (identity.oversized()) {
            int cellX = Math.floorDiv(x, OVERSIZED_CELL_BLOCKS);
            int cellZ = Math.floorDiv(z, OVERSIZED_CELL_BLOCKS);
            int value = activityValue(
                    level.getLevel().getSeed(), biomeId, region,
                    cellX, cellZ, 0x6F76657273697A65L
            );
            return new ActivitySample(
                    value, biomeId, ActivitySource.OVERSIZED_CELL,
                    cellX, cellZ
            );
        }

        int value = activityValue(
                level.getLevel().getSeed(), biomeId, region,
                identity.anchorX(), identity.anchorZ(),
                0x66696E697465L
        );
        return new ActivitySample(
                value, biomeId, ActivitySource.FINITE_BIOME,
                identity.anchorX(), identity.anchorZ()
        );
    }

    private static ComponentSearch survey(
            ServerLevelAccessor level,
            int startX, int quartY, int startZ,
            ResourceLocation biomeId) {
        var chunkSource = level.getLevel().getChunkSource();
        var biomeSource = chunkSource.getGenerator().getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        ArrayDeque<Cell> open = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        open.add(new Cell(startX, startZ));
        int anchorX = startX;
        int anchorZ = startZ;
        boolean oversized = false;

        while (!open.isEmpty()) {
            Cell cell = open.removeFirst();
            long key = cellKey(cell.x(), cell.z());
            if (!visited.add(key)) continue;
            if (cell.x() < anchorX
                    || (cell.x() == anchorX && cell.z() < anchorZ)) {
                anchorX = cell.x();
                anchorZ = cell.z();
            }
            if (visited.size() > MAX_COMPONENT_CELLS) {
                oversized = true;
                break;
            }

            for (int[] direction : CARDINAL_DIRECTIONS) {
                int neighborX = cell.x() + direction[0];
                int neighborZ = cell.z() + direction[1];
                long neighborKey = cellKey(neighborX, neighborZ);
                if (visited.contains(neighborKey)) continue;
                Holder<Biome> neighbor = biomeSource.getNoiseBiome(
                        neighborX, quartY, neighborZ, climateSampler
                );
                ResourceLocation neighborId = neighbor.unwrapKey()
                        .map(keyValue -> keyValue.location())
                        .orElse(null);
                if (biomeId.equals(neighborId)) {
                    open.addLast(new Cell(neighborX, neighborZ));
                }
            }
        }
        return new ComponentSearch(
                visited, oversized, anchorX, anchorZ
        );
    }

    private static int activityValue(
            long seed, ResourceLocation biomeId, Region region,
            int areaX, int areaZ, long salt) {
        long value = seed ^ salt;
        value ^= (long) biomeId.toString().hashCode()
                * 0x9E3779B97F4A7C15L;
        value ^= regionKey(region) * 0xC2B2AE3D27D4EB4FL;
        value ^= (long) areaX * 0x165667B19E3779F9L;
        value ^= (long) areaZ * 0xD6E8FEB86659FD93L;
        return (int) Long.remainderUnsigned(
                mix64(value), ACTIVITY_LEVELS
        );
    }

    private static long regionKey(Region region) {
        if (region.ocean()) {
            return 0x100L + region.sea().ordinal();
        }
        return ((long) region.continent().ordinal() << 8)
                | region.dir().ordinal();
    }

    private static long mix64(long value) {
        value ^= value >>> 33;
        value *= 0xFF51AFD7ED558CCDL;
        value ^= value >>> 33;
        value *= 0xC4CEB9FE1A85EC53L;
        value ^= value >>> 33;
        return value;
    }

    private static ActivityCache cacheFor(ServerLevel level) {
        synchronized (WORLD_CACHES) {
            return WORLD_CACHES.computeIfAbsent(
                    level, ignored -> new ActivityCache()
            );
        }
    }

    private static long cellKey(int quartX, int quartZ) {
        return ((long) quartX << 32) ^ (quartZ & 0xFFFFFFFFL);
    }

    public enum ActivitySource {
        FINITE_BIOME,
        OVERSIZED_CELL
    }

    public record ActivitySample(
            int value,
            ResourceLocation biome,
            ActivitySource source,
            int areaX,
            int areaZ
    ) {}

    private record Cell(int x, int z) {}

    private record ComponentSearch(
            Set<Long> cells,
            boolean oversized,
            int anchorX,
            int anchorZ
    ) {}

    private record ComponentIdentity(
            boolean oversized,
            int anchorX,
            int anchorZ
    ) {
        private static ComponentIdentity large() {
            return new ComponentIdentity(true, 0, 0);
        }

        private static ComponentIdentity finite(int anchorX, int anchorZ) {
            return new ComponentIdentity(false, anchorX, anchorZ);
        }
    }

    private static final class ActivityCache {
        private final Map<Long, ComponentIdentity> values =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, ComponentIdentity> eldest) {
                        return size() > MAX_CACHE_ENTRIES;
                    }
                };

        private synchronized ComponentIdentity get(long key) {
            return values.get(key);
        }

        private synchronized void putAll(
                Set<Long> cells, ComponentIdentity identity) {
            for (long cell : cells) values.put(cell, identity);
        }
    }
}
