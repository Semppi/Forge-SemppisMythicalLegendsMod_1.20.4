package net.semppi.semppis_mythical_legends_mod.spawn;

import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

/**
 * Very small per-tick cache around RegionGate.allows(...)
 * Keyed by (dimension, entity type, chunkX, chunkZ, spawn reason).
 * Clears itself each server tick.
 */
public final class RegionGateCached {
    private RegionGateCached() {}

    private static long lastTick = Long.MIN_VALUE;
    private static final Object2BooleanOpenHashMap<Key> CACHE = new Object2BooleanOpenHashMap<>();

    public static boolean allows(ServerLevel level, EntityType<?> type, BlockPos pos, MobSpawnType reason) {
        final long tick = level.getGameTime();
        if (tick != lastTick) {
            // new tick, flush
            CACHE.clear();
            lastTick = tick;
        }

        final int cx = pos.getX() >> 4;
        final int cz = pos.getZ() >> 4;
        final Key key = new Key(level.dimension(), type, cx, cz, reason);

        if (CACHE.containsKey(key)) {
            return CACHE.getBoolean(key);
        }

        boolean result = RegionGate.allows(level, type, pos, reason);
        CACHE.put(key, result);
        return result;
    }

    public static boolean allows(ServerLevel level, EntityType<?> type, BlockPos pos) {
        // Join events don't carry a spawn reason; treat as "natural" for caching purposes.
        return allows(level, type, pos, MobSpawnType.NATURAL);
    }

    private record Key(ResourceKey<Level> dim, EntityType<?> type, int cx, int cz, MobSpawnType reason) {
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key k)) return false;
            return cx == k.cx && cz == k.cz && Objects.equals(dim, k.dim) && type == k.type && reason == k.reason;
        }
        @Override public int hashCode() {
            int h = dim.hashCode();
            h = 31*h + System.identityHashCode(type);
            h = 31*h + cx;
            h = 31*h + cz;
            h = 31*h + reason.hashCode();
            return h;
        }
    }
}