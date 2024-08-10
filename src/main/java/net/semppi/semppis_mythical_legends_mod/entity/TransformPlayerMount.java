package net.semppi.semppis_mythical_legends_mod.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class TransformPlayerMount extends Mob {
    private static final EntityDataAccessor<Boolean> IS_CROUCHING = SynchedEntityData.defineId(TransformPlayerMount.class, EntityDataSerializers.BOOLEAN);

    public TransformPlayerMount(EntityType<? extends Mob> entityType, Level world) {
        super(entityType, world);
        this.noPhysics = false; // Enable physics processing
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_CROUCHING, false);
    }

    public interface ICrouchable {
        void setCrouching(boolean crouching);
    }

    // Call this method when the shift key is pressed
    public void toggleCrouch() {
        boolean isCrouching = this.entityData.get(IS_CROUCHING);
        this.entityData.set(IS_CROUCHING, !isCrouching);

        // Notify the entity it's riding about the crouch state change
        if (!this.getPassengers().isEmpty()) {
            Entity passenger = this.getPassengers().get(0);
            if (passenger instanceof ICrouchable) {
                ((ICrouchable) passenger).setCrouching(isCrouching);
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            this.controlEntity((Player) this.getControllingPassenger());
        }
    }

    @Override
    public boolean shouldRiderSit() {
        return true; // This ensures the rider is rendered sitting
    }

    @Override
    public void removePassenger(Entity passenger) {
        // Prevent dismounting
    }

    @Override
    public void stopRiding() {
        // Prevent dismounting
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        // Prevent dismounting by not providing a valid dismount location
        return null;
    }

    public void controlEntity(Player player) {
        this.setYRot(player.getYRot());
        this.yRotO = this.getYRot();
        this.setXRot(player.getXRot() * 0.5F);
        this.setRot(this.getYRot(), this.getXRot());
        this.yBodyRot = this.getYRot();
        this.yHeadRot = this.getYRot();

        float strafe = player.zza * 0.5F;
        float forward = player.xxa;

        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
        Vec3 movementVec = new Vec3(strafe, 0.0D, forward);
        this.travel(movementVec);

        // Handle jump
        if (Minecraft.getInstance().options.keyJump.isDown()) {
            this.setJumping(true);
        } else {
            this.setJumping(false);
        }

        // Handle crouch
        if (player.isCrouching()) {
            this.toggleCrouch();
        }
    }
}