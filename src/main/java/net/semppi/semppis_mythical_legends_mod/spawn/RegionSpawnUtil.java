package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.semppi.semppis_mythical_legends_mod.rules.SMLRules;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionCompat;
import net.semppi.semppis_mythical_legends_mod.world.RegionSurfaceClassifier;

public final class RegionSpawnUtil {
    private RegionSpawnUtil() {}

    /** Returns true if added; false if denied by continental rules. */
    public static <T extends Mob> boolean tryAdd(ServerLevel level, T entity) {
        BlockPos pos = entity.blockPosition();

        if (level.dimension() == Level.OVERWORLD
                && level.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) {
            Region region = RegionSurfaceClassifier
                    .sample(level, pos.getX(), pos.getZ())
                    .region();

            boolean allowed = region.ocean()
                    ? RegionMobAllow.isAllowedForSea(entity.getType(), region.sea())
                    : RegionCompat.isAllowedForBiome(level.getBiome(pos), region)
                    && RegionMobAllow.isAllowedForLand(
                            entity.getType(), region.continent(), region.dir()
                    );

            if (!allowed) return false;
        }

        return level.addFreshEntity(entity);
    }

    public static <T extends Mob> boolean tryCreateAndAdd(
            ServerLevel level, EntityType<T> type, BlockPos pos,
            MobSpawnType reason, RandomSource random) {
        T entity = type.create(level);
        if (entity == null) return false;

        entity.moveTo(pos, level.random.nextFloat() * 360F, 0F);
        return tryAdd(level, entity);
    }
}
