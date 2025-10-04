package net.semppi.semppis_mythical_legends_mod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.behavior.UniqueCharacterUtils;
import net.semppi.semppis_mythical_legends_mod.entity.custom.MalphasEntity;

@Mod.EventBusSubscriber(
        modid = SemppisMythicalLegendsMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class EntitySpawnHandler {
    private EntitySpawnHandler() {}

    @SubscribeEvent
    public static void onEntitySpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof MalphasEntity malphas)) return;

        UniqueCharacterUtils.manageMalphasSpawning(malphas, (ServerLevel) event.getLevel());
    }
}