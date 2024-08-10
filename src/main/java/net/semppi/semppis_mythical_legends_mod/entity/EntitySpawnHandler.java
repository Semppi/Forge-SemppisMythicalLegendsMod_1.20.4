package net.semppi.semppis_mythical_legends_mod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.behavior.UniqueCharacterUtils;
import net.semppi.semppis_mythical_legends_mod.entity.custom.MalphasEntity;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class EntitySpawnHandler {

    private EntitySpawnHandler() {
        // Private constructor to prevent instantiation
    }

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof MalphasEntity) {
            MalphasEntity malphas = (MalphasEntity) event.getEntity();
            UniqueCharacterUtils.manageMalphasSpawning(malphas, (ServerLevel) event.getLevel());
        }
    }
}