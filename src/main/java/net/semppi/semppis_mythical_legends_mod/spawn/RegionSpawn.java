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
            if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND) {
                return base.test(type, level, reason, pos, rnd);
            }

            final ServerLevel sl = level.getLevel();

            if (sl.dimension() == Level.OVERWORLD &&
                    sl.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) {

                final Region land =
                        (level instanceof net.minecraft.world.level.WorldGenLevel)
                                ? SAMPLER.landRegion(sl.getSeed(), pos.getX(), pos.getZ())
                                : SAMPLER.landRegion(sl, pos.getX(), pos.getZ());

                if (land.ocean()) return false;

                if (!RegionMobAllow.isAllowedForLand(type, land.continent(), land.dir())) return false;

                if (!(level instanceof net.minecraft.world.level.WorldGenLevel)) {
                    if (!RegionCompat.isAllowedForBiome(level.getBiome(pos), land)) return false;
                }
            }

            return base.test(type, level, reason, pos, rnd);
        };
    }

    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> gatedSea(SpawnPlacements.SpawnPredicate<T> base) {
        return (EntityType<T> type, ServerLevelAccessor level, MobSpawnType reason, BlockPos pos, RandomSource rnd) -> {
            if (reason == MobSpawnType.SPAWN_EGG || reason == MobSpawnType.COMMAND) {
                return base.test(type, level, reason, pos, rnd);
            }

            final ServerLevel sl = level.getLevel();

            if (sl.dimension() == Level.OVERWORLD &&
                    sl.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) {

                final Region seaR =
                        (level instanceof net.minecraft.world.level.WorldGenLevel)
                                ? SAMPLER.seaRegion(sl.getSeed(), pos.getX(), pos.getZ())
                                : SAMPLER.seaRegion(sl, pos.getX(), pos.getZ());

                if (!seaR.ocean()) return false;
                if (!RegionMobAllow.isAllowedForSea(type, seaR.sea())) return false;
            }

            return base.test(type, level, reason, pos, rnd);
        };
    }
}