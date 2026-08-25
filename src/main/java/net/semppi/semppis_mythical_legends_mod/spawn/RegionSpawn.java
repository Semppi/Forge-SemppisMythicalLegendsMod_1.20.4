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
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionSurfaceClassifier;

public final class RegionSpawn {
    private RegionSpawn() {}

    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> gatedLand(
            SpawnPlacements.SpawnPredicate<T> base) {
        return gated(base);
    }

    public static <T extends Mob> SpawnPlacements.SpawnPredicate<T> gatedSea(
            SpawnPlacements.SpawnPredicate<T> base) {
        return gated(base);
    }

    /**
     * Land and sea placement predicates intentionally share this path. The
     * surface biome classifier—not entity category or wrapper name—decides
     * whether the coordinate belongs to land, a river, or an ocean.
     */
    private static <T extends Mob> SpawnPlacements.SpawnPredicate<T> gated(
            SpawnPlacements.SpawnPredicate<T> base) {
        return (EntityType<T> type, ServerLevelAccessor level,
                MobSpawnType reason, BlockPos pos, RandomSource random) -> {
            if (bypassesRegionGate(type, reason)) {
                return base.test(type, level, reason, pos, random);
            }

            ServerLevel serverLevel = level.getLevel();
            if (serverLevel.dimension() != Level.OVERWORLD
                    || !serverLevel.getGameRules().getBoolean(
                            SMLRules.CONTINENTAL_SPAWNING)) {
                return base.test(type, level, reason, pos, random);
            }

            boolean allowed;
            if (level instanceof net.minecraft.world.level.WorldGenLevel) {
                Region region = RegionSurfaceClassifier
                        .sample(level, pos.getX(), pos.getZ())
                        .region();
                allowed = allowedInRegion(type, region);
            } else {
                allowed = RegionGateCached.allows(
                        serverLevel, type, pos, reason
                );
            }

            return allowed && base.test(type, level, reason, pos, random);
        };
    }

    private static boolean allowedInRegion(EntityType<?> type, Region region) {
        return region.ocean()
                ? RegionMobAllow.isAllowedForSea(type, region.sea())
                : RegionMobAllow.isAllowedForLand(
                        type, region.continent(), region.dir()
                );
    }

    private static boolean bypassesRegionGate(EntityType<?> type,
                                              MobSpawnType reason) {
        return reason == MobSpawnType.SPAWN_EGG
                || reason == MobSpawnType.COMMAND
                || reason == MobSpawnType.DISPENSER
                || reason == MobSpawnType.CONVERSION
                || reason == MobSpawnType.SPAWNER
                || (reason == MobSpawnType.BREEDING
                && !RegionMobAllow.isBreedingRestricted(type));
    }
}
