package net.semppi.semppis_mythical_legends_mod.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.semppi.semppis_mythical_legends_mod.entity.custom.ProtoWendigoEntity;

public class PlayerInputHandler {
    private final LivingEntity entity;

    public PlayerInputHandler(LivingEntity entity) {
        this.entity = entity;
    }

//    @SubscribeEvent
//    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
//        Player player = event.player;
//        if (player.getVehicle() instanceof ProtoWendigoEntity) {
//            ProtoWendigoEntity entity = (ProtoWendigoEntity) player.getVehicle();
//            entity.inputHandler.handleTravel(player);
//        }
//    }

    public void handleTravel(Player player) {
        // Update entity's rotation to match the player's
        entity.setYRot(player.getYRot());
        entity.yRotO = entity.getYRot();
        entity.setXRot(player.getXRot() * 0.5F);

        // Retrieve player movement inputs
        float forward = player.zza; // Forward/backward movement
        float strafe = player.xxa;  // Left/right movement

        if (entity.isControlledByLocalInstance()) {
            // Calculate movement direction based on entity's current rotation
            double movementSpeed = entity.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
            Vec3 moveVector = new Vec3(strafe, 0, forward).normalize().scale(movementSpeed);
            moveVector = moveVector.yRot(-(float) Math.toRadians(entity.getYRot()));

            // Apply movement vector to entity
            entity.setDeltaMovement(moveVector);

            // Handle ground friction
            if (entity.onGround()) {
                float friction = 0.91F * entity.level().getBlockState(entity.blockPosition()).getFriction(entity.level(), entity.blockPosition(), entity);
                Vec3 velocity = entity.getDeltaMovement().multiply(friction, 1.0, friction);
                entity.setDeltaMovement(velocity);
            }
        }
    }
}