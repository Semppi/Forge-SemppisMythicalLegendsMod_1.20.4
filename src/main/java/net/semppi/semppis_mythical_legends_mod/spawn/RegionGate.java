package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.rules.SMLRules;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionCompat;
import net.semppi.semppis_mythical_legends_mod.world.RegionSampler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class RegionGate {
    private RegionGate() {}

    // ----- sampling & cache -----
    private static final RegionSampler SAMPLER = new RegionSampler();

    // 1200 ticks ≈ 60 seconds at 20 TPS
    private static final int CACHE_TTL_TICKS = 600;

    private static final Map<Long, CacheEntry> CACHE = new ConcurrentHashMap<>();
    private static final AtomicInteger PUTS = new AtomicInteger(0);

    private static final class CacheEntry {
        final long expireAtTick;
        final Region region;

        CacheEntry(long expireAtTick, Region region) {
            this.expireAtTick = expireAtTick;
            this.region = region;
        }
    }

    /** Back-compat: reasonless call. */
    public static boolean allows(ServerLevel level, EntityType<?> type, BlockPos pos) {
        return allows(level, type, pos, null);
    }

    /** Gate *our* mobs in Overworld if gamerule is ON. BREEDING bypasses unless picky. */
    public static boolean allows(ServerLevel level, EntityType<?> type, BlockPos pos, MobSpawnType reason) {
        if (level.dimension() != Level.OVERWORLD) return true;
        if (!level.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) return true;
        if (!isOurMob(type)) return true;

        // BYPASS here as well
        if (reason == MobSpawnType.SPAWN_EGG
                || reason == MobSpawnType.COMMAND
                || reason == MobSpawnType.DISPENSER
                || reason == MobSpawnType.CONVERSION) {
            return true;
        }

        // Breeding allowed anywhere by default, unless the species opted-in to picky breeding
        if (reason == MobSpawnType.BREEDING
                && !RegionMobAllow.isBreedingRestricted(type)) {
            return true;
        }

        boolean aquatic = isAquatic(type);
        Region r = cachedRegion(level, pos.getX(), pos.getZ(), aquatic);

        if (r.ocean()) {
            return RegionMobAllow.isAllowedForSea(type, r.sea());
        } else {
            return RegionCompat.isAllowedForBiome(level.getBiome(pos), r)
                    && RegionMobAllow.isAllowedForLand(type, r.continent(), r.dir());
        }
    }

    // ----- cache helpers -----
    private static Region cachedRegion(ServerLevel level, int x, int z, boolean aquatic) {
        long now = level.getGameTime();
        long key = cacheKey(level, x, z, aquatic);   // pass block coords

        CacheEntry ce = CACHE.get(key);
        if (ce != null && now <= ce.expireAtTick) return ce.region;

        Region r = aquatic ? SAMPLER.seaRegion(level, x, z) : SAMPLER.landRegion(level, x, z);
        CACHE.put(key, new CacheEntry(now + CACHE_TTL_TICKS, r));
        if ((PUTS.incrementAndGet() & 0xFFF) == 0) sweep(now);
        return r;
    }

    /** For HUD/network sync to peek the cached decision. */
    public static Region peekRegion(ServerLevel level, int x, int z, boolean aquatic) {
        return cachedRegion(level, x, z, aquatic);
    }

    private static void sweep(long now) {
        CACHE.entrySet().removeIf(e -> e.getValue().expireAtTick < now);
    }

    private static long cacheKey(ServerLevel level, int x, int z, boolean aquatic) {
        long seed = level.getSeed();
        int dimHash = level.dimension().location().hashCode();

        // Region ownership is seed/X/Z deterministic. A biome lookup here
        // would only create duplicate cache entries and unnecessary world access.
        int chunkX = x >> 4, chunkZ = z >> 4;

        long k = seed ^ (long)dimHash * 0x9E3779B97F4A7C15L;
        k ^= (long)chunkX * 0xC2B2AE3D27D4EB4FL;
        k ^= (long)chunkZ * 0x165667B19E3779F9L;
        k ^= aquatic ? 0xA5A5A5A5A5A5A5A5L : 0x5A5A5A5A5A5A5A5AL;
        return k;
    }

    public static Region sampleNow(ServerLevel lvl, int x, int z, boolean aquatic) {
        return aquatic ? SAMPLER.seaRegion(lvl, x, z) : SAMPLER.landRegion(lvl, x, z);
    }

    // ----- type helpers -----
    private static boolean isAquatic(EntityType<?> type) {
        MobCategory c = type.getCategory();
        return c == MobCategory.WATER_AMBIENT
                || c == MobCategory.WATER_CREATURE
                || c == MobCategory.UNDERGROUND_WATER_CREATURE;
    }

    private static boolean isOurMob(EntityType<?> type) {
        ResourceLocation id = type.builtInRegistryHolder().key().location();
        return SemppisMythicalLegendsMod.MOD_ID.equals(id.getNamespace());
    }
}