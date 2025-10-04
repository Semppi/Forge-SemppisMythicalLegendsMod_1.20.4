package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.semppi.semppis_mythical_legends_mod.rules.SMLRules;
import net.semppi.semppis_mythical_legends_mod.world.*;

public final class RegionSpawn {
    private RegionSpawn() {}

    private static final RegionSampler SAMPLER = new RegionSampler();

    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> gatedLand(SpawnPlacements.SpawnPredicate<T> base) {
        return (EntityType<T> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource rnd) -> {
            // vanilla-bypass reasons (keep as-is)
            if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND
                    || reason == MobSpawnType.DISPENSER || reason == MobSpawnType.CONVERSION
                    || reason == MobSpawnType.SPAWNER
                    || (reason == MobSpawnType.BREEDING && !RegionMobAllow.isBreedingRestricted(type))) {
                return base.test(type, level, reason, pos, rnd);
            }

            final ServerLevel sl = level.getLevel();
            if (sl.dimension() != Level.OVERWORLD || !sl.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) {
                return base.test(type, level, reason, pos, rnd);
            }

            // Align to chunk center to be stable & cheap
            final int sx = ((pos.getX() >> 4) << 4) + 8;
            final int sz = ((pos.getZ() >> 4) << 4) + 8;

            boolean allowed;
            if (level instanceof net.minecraft.world.level.WorldGenLevel) {
                // WORLDGEN LANE: seed/noise only — never loads neighbors
                final Region r = SAMPLER.landRegion(sl.getSeed(), sx, sz);
                allowed = !r.ocean() && RegionMobAllow.isAllowedForLand(type, r.continent(), r.dir());
                // no RegionCompat here — keep worldgen path minimal & safe
            } else {
                // RUNTIME LANE: cached & biome-aware, but amortized
                allowed = net.semppi.semppis_mythical_legends_mod.spawn.RegionGateCached
                        .allows(sl, type, pos, reason);
            }

            return allowed && base.test(type, level, reason, pos, rnd);
        };
    }

    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> gatedSea(SpawnPlacements.SpawnPredicate<T> base) {
        return (EntityType<T> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource rnd) -> {
            if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND
                    || reason == MobSpawnType.DISPENSER || reason == MobSpawnType.CONVERSION
                    || reason == MobSpawnType.SPAWNER
                    || (reason == MobSpawnType.BREEDING && !RegionMobAllow.isBreedingRestricted(type))) {
                return base.test(type, level, reason, pos, rnd);
            }

            final ServerLevel sl = level.getLevel();
            if (sl.dimension() != Level.OVERWORLD || !sl.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) {
                return base.test(type, level, reason, pos, rnd);
            }

            final int sx = ((pos.getX() >> 4) << 4) + 8;
            final int sz = ((pos.getZ() >> 4) << 4) + 8;

            boolean allowed;
            if (level instanceof net.minecraft.world.level.WorldGenLevel) {
                // WORLDGEN LANE: seed/noise only
                final Region r = SAMPLER.seaRegion(sl.getSeed(), sx, sz);
                allowed = RegionMobAllow.isAllowedForSea(type, r.sea());
            } else {
                // RUNTIME LANE: cached
                allowed = net.semppi.semppis_mythical_legends_mod.spawn.RegionGateCached
                        .allows(sl, type, pos, reason);
            }

            return allowed && base.test(type, level, reason, pos, rnd);
        };
    }
}