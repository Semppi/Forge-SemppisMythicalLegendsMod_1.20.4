package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.commands.TransformHelper;
import net.semppi.semppis_mythical_legends_mod.entity.TransformPlayerMount;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DismountEventHandler {

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();
        Player player = event.getEntity();

        // Check if the player is transformed and interacting with a mountable entity
        if (TransformHelper.isPlayerTransformed(player.getUUID())) {
            Entity transformedEntity = TransformHelper.getTransformedEntity(player.getUUID());

            if (entity instanceof Boat || entity instanceof Minecart || isMountableEntity(entity)) {
                if (transformedEntity != null) {
                    // Cancel the player's interaction to prevent them from mounting
                    event.setCanceled(true);

                    // Transform entity mounts instead
                    transformedEntity.startRiding(entity, true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDismount(EntityMountEvent event) {
        if (event.isDismounting() && event.getEntityMounting() instanceof Player player) {
            if (TransformHelper.isPlayerTransformed(player.getUUID())) {
                // Get the transformed entity
                Entity transformedEntity = TransformHelper.getTransformedEntity(player.getUUID());

                // If the player is trying to dismount their transformation, cancel the event
                if (event.getEntityBeingMounted() == transformedEntity) {
                    event.setCanceled(true);
                } else if (transformedEntity != null && player.isShiftKeyDown()) {
                    // If the player is holding shift, allow the transformation to dismount its current mount
                    transformedEntity.stopRiding();
                }
            }
        }
    }

    // Helper method to check if an entity is mountable (extend this with more mountable entities as needed)
    private static boolean isMountableEntity(Entity entity) {
        return entity instanceof Boat || entity instanceof Minecart;
        // Add more entity checks if necessary, for example:
        // return entity instanceof Boat || entity instanceof Minecart || entity instanceof Horse;
    }
}