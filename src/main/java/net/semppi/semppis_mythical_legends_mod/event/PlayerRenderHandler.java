package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.commands.TransformHelper;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerRenderHandler {

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<LivingEntity, ?> event) {
        if (!(event.getEntity() instanceof AbstractClientPlayer player)) return;

        // now takes Player, so this compiles
        if (TransformHelper.isPlayerTransformed((Player)player)) {
            event.setCanceled(true);
        }
    }
}