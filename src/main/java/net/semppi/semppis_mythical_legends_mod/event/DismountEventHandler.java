package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.commands.TransformHelper;
import net.semppi.semppis_mythical_legends_mod.entity.TransformMountEntity;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DismountEventHandler {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        // only ServerPlayer can be transformed
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (TransformHelper.isPlayerTransformed(player)) {
            TransformMountEntity mount = TransformHelper.getPlayerMount(player);
            if (mount != null && (event.getTarget() instanceof Boat || event.getTarget() instanceof Minecart)) {
                event.setCanceled(true);
                mount.startRiding(event.getTarget(), true);
            }
        }
    }

    @SubscribeEvent
    public static void onDismount(EntityMountEvent event) {
        if (!event.isDismounting()) return;
        if (!(event.getEntityMounting() instanceof ServerPlayer player)) return;

        if (TransformHelper.isPlayerTransformed(player)) {
            TransformMountEntity mount = TransformHelper.getPlayerMount(player);
            if (mount == null) return;

            // prevent dismounting your own transform
            if (event.getEntityBeingMounted() == mount) {
                event.setCanceled(true);
            }
            // but allow shift-dismount of whatever the transform is riding
            else if (player.isShiftKeyDown()) {
                mount.stopRiding();
            }
        }
    }
}