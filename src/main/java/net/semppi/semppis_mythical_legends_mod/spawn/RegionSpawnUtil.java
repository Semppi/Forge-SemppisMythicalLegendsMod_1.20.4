package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.Level;
import net.semppi.semppis_mythical_legends_mod.rules.SMLRules;
import net.semppi.semppis_mythical_legends_mod.world.*;

public final class RegionSpawnUtil {
    private RegionSpawnUtil() {}

    private static final RegionSampler SAMPLER = new RegionSampler();

    /** Returns true if added; false if denied by continental rules. */
    public static <T extends Mob> boolean tryAdd(ServerLevel sl, T entity) {
        BlockPos pos = entity.blockPosition();

        // Only enforce in Overworld and when gamerule is ON
        if (sl.dimension() == Level.OVERWORLD && sl.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) {
            boolean isWaterPlacement =
                    SpawnPlacements.getPlacementType(entity.getType()) == SpawnPlacements.Type.IN_WATER
                            || entity.isInWaterOrBubble();

            Region r = isWaterPlacement
                    ? SAMPLER.seaRegion(sl, pos.getX(), pos.getZ())   // biome-aware seas
                    : SAMPLER.landRegion(sl, pos.getX(), pos.getZ()); // biome-aware land

            boolean ok = r.ocean()
                    ? RegionMobAllow.isAllowedForSea(entity.getType(), r.sea())
                    : (RegionCompat.isAllowedForBiome(sl.getBiome(pos), r)
                    && RegionMobAllow.isAllowedForLand(entity.getType(), r.continent(), r.dir()));

            if (!ok) return false; // block the spawn
        }

        return sl.addFreshEntity(entity);
    }

    /** Convenience overload if you don’t have the entity instance yet. */
    public static <T extends Mob> boolean tryCreateAndAdd(
            ServerLevel sl, EntityType<T> type, BlockPos pos, MobSpawnType reason, RandomSource rnd
    ) {
        T e = type.create(sl);
        if (e == null) return false;
        e.moveTo(pos, sl.random.nextFloat() * 360F, 0F);
        return tryAdd(sl, e);
    }
}