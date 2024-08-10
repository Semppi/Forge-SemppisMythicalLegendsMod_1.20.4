package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.entity.custom.*;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvents {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BEHEMOTH.get(), BehemothEntity.setAttributes());
        event.put(ModEntities.COLOSSAL_LOBSTER.get(), ColossalLobsterEntity.setAttributes());
        event.put(ModEntities.KRAKEN.get(), KrakenEntity.setAttributes());
        event.put(ModEntities.LOVELAND_FROGMAN.get(), LovelandFrogmanEntity.setAttributes());
        event.put(ModEntities.MALPHAS.get(), MalphasEntity.setAttributes());
        event.put(ModEntities.MANDRAKE.get(), MandrakeEntity.setAttributes());
        event.put(ModEntities.PUKIS.get(), PukisEntity.setAttributes());
        event.put(ModEntities.SATYR.get(), SatyrEntity.setAttributes());
        event.put(ModEntities.WENDIGO.get(), WendigoEntity.setAttributes());
    }
}