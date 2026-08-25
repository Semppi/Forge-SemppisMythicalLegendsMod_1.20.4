package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.rules.SMLRules;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionCompat;
import net.semppi.semppis_mythical_legends_mod.world.RegionSurfaceClassifier;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class RegionGate {
    private RegionGate() {}

    private static final int CACHE_TTL_TICKS = 600;
    private static final Map<Long, CacheEntry> CACHE = new ConcurrentHashMap<>();
    private static final AtomicInteger PUTS = new AtomicInteger(0);

    private static final class CacheEntry {
        final long expireAtTick;
        final RegionSurfaceClassifier.Sample sample;

        CacheEntry(long expireAtTick, RegionSurfaceClassifier.Sample sample) {
            this.expireAtTick = expireAtTick;
            this.sample = sample;
        }
    }

    public static boolean allows(ServerLevel level, EntityType<?> type, BlockPos pos) {
        return allows(level, type, pos, null);
    }

    public static boolean allows(ServerLevel level, EntityType<?> type, BlockPos pos,
                                 MobSpawnType reason) {
        if (level.dimension() != Level.OVERWORLD) return true;
        if (!level.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) return true;
        if (!isOurMob(type)) return true;

        if (reason == MobSpawnType.SPAWN_EGG
                || reason == MobSpawnType.COMMAND
                || reason == MobSpawnType.DISPENSER
                || reason == MobSpawnType.CONVERSION) {
            return true;
        }

        if (reason == MobSpawnType.BREEDING
                && !RegionMobAllow.isBreedingRestricted(type)) {
            return true;
        }

        Region region = cachedSample(level, pos.getX(), pos.getZ()).region();
        if (region.ocean()) {
            return RegionMobAllow.isAllowedForSea(type, region.sea());
        }

        return RegionCompat.isAllowedForBiome(level.getBiome(pos), region)
                && RegionMobAllow.isAllowedForLand(
                        type, region.continent(), region.dir()
                );
    }

    private static RegionSurfaceClassifier.Sample cachedSample(
            ServerLevel level, int x, int z) {
        long now = level.getGameTime();
        long key = cacheKey(level, x, z);

        CacheEntry cached = CACHE.get(key);
        if (cached != null && now <= cached.expireAtTick) {
            return cached.sample;
        }

        RegionSurfaceClassifier.Sample sample =
                RegionSurfaceClassifier.sample(level, x, z);
        CACHE.put(key, new CacheEntry(now + CACHE_TTL_TICKS, sample));

        if ((PUTS.incrementAndGet() & 0xFFF) == 0) sweep(now);
        return sample;
    }

    public static Region peekRegion(ServerLevel level, int x, int z) {
        return cachedSample(level, x, z).region();
    }

    /**
     * Compatibility overload for callers from before surface classification.
     * The old aquatic guess is intentionally ignored.
     */
    public static Region peekRegion(ServerLevel level, int x, int z, boolean aquatic) {
        return peekRegion(level, x, z);
    }

    private static void sweep(long now) {
        CACHE.entrySet().removeIf(entry -> entry.getValue().expireAtTick < now);
    }

    private static long cacheKey(ServerLevel level, int x, int z) {
        long seed = level.getSeed();
        int dimensionHash = level.dimension().location().hashCode();

        // Four-block cells follow vanilla biome sampling more accurately than
        // one cache entry for an entire mixed coastline chunk.
        int cellX = x >> 2;
        int cellZ = z >> 2;

        long key = seed ^ (long) dimensionHash * 0x9E3779B97F4A7C15L;
        key ^= (long) cellX * 0xC2B2AE3D27D4EB4FL;
        key ^= (long) cellZ * 0x165667B19E3779F9L;
        return key;
    }

    public static Region sampleNow(ServerLevel level, int x, int z) {
        return RegionSurfaceClassifier.sample(level, x, z).region();
    }

    /**
     * Compatibility overload. Surface biome tags now decide land versus sea.
     */
    public static Region sampleNow(ServerLevel level, int x, int z, boolean aquatic) {
        return sampleNow(level, x, z);
    }

    private static boolean isOurMob(EntityType<?> type) {
        ResourceLocation id = type.builtInRegistryHolder().key().location();
        return SemppisMythicalLegendsMod.MOD_ID.equals(id.getNamespace());
    }
}
