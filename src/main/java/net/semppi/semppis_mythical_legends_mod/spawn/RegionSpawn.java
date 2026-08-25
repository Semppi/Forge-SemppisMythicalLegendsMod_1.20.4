package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.ServerLevelAccessor;

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
            return RegionGate.allows(level, type, pos, reason)
                    && base.test(type, level, reason, pos, random);
        };
    }
}
