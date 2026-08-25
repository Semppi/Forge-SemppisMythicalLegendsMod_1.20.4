package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;

public final class RegionSpawnUtil {
    private RegionSpawnUtil() {}

    /** Returns true if added; false if denied by continental rules. */
    public static <T extends Mob> boolean tryAdd(ServerLevel level, T entity) {
        return tryAdd(level, entity, MobSpawnType.NATURAL);
    }

    public static <T extends Mob> boolean tryAdd(
            ServerLevel level, T entity, MobSpawnType reason) {
        BlockPos pos = entity.blockPosition();
        return RegionGate.allows(level, entity.getType(), pos, reason)
                && level.addFreshEntity(entity);
    }

    public static <T extends Mob> boolean tryCreateAndAdd(
            ServerLevel level, EntityType<T> type, BlockPos pos,
            MobSpawnType reason, RandomSource random) {
        T entity = type.create(level);
        if (entity == null) return false;

        entity.moveTo(pos, level.random.nextFloat() * 360F, 0F);
        return tryAdd(level, entity, reason);
    }
}
