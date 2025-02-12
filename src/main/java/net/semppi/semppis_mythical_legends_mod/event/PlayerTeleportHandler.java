package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.entity.TransformMountEntity;

@Mod.EventBusSubscriber
public class PlayerTeleportHandler {

    @SubscribeEvent
    public static void onPlayerTeleport(EntityTravelToDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            Entity mount = player.getVehicle();
            if (mount instanceof TransformMountEntity) {
                // Sync the mount's position with the player
                mount.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            }
        }
    }
}