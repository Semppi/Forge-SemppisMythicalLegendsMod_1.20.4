package net.semppi.semppis_mythical_legends_mod.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.behavior.UniqueCharacterUtils;
import net.semppi.semppis_mythical_legends_mod.entity.custom.MalphasEntity;

@Mod.EventBusSubscriber(
        modid = SemppisMythicalLegendsMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.DEDICATED_SERVER
)
public final class EntitySpawnHandler {

    private EntitySpawnHandler() {
        // no instances
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        if (event.getEntity() instanceof MalphasEntity malphas) {
            UniqueCharacterUtils.manageMalphasSpawning(malphas, (ServerLevel) event.getLevel());
        }
    }

    /** If you already created the mob instance. Returns true if it was added. */
    public static boolean tryAddRespectingRegion(ServerLevel level, Mob mob) {
        return net.semppi.semppis_mythical_legends_mod.spawn.RegionSpawnUtil.tryAdd(level, mob);
    }

    /** If you just have the type + position. Returns true if it was created & added. */
    public static <T extends Mob> boolean tryCreateAndAddRespectingRegion(
            ServerLevel level,
            EntityType<T> type,
            BlockPos pos,
            MobSpawnType reason
    ) {
        return net.semppi.semppis_mythical_legends_mod.spawn.RegionSpawnUtil.tryCreateAndAdd(
                level, type, pos, reason, level.getRandom()
        );
    }
}