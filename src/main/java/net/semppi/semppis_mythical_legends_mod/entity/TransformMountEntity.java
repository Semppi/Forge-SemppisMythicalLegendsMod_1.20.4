package net.semppi.semppis_mythical_legends_mod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.UUID;

public class TransformMountEntity extends Mob implements PlayerLinkedEntity {
    private UUID linkedPlayerUUID;

    public TransformMountEntity(EntityType<? extends TransformMountEntity> type, Level world) {
        super(type, world);
        this.noPhysics = false;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // no extra fields
    }

    // --- PlayerLinkedEntity ---
    @Override
    public void setLinkedPlayer(Player p) {
        this.linkedPlayerUUID = p.getUUID();
        p.startRiding(this, true);
        p.setInvisible(true);
    }

    @Nullable
    @Override
    public Player getLinkedPlayer() {
        if (level() instanceof ServerLevel sl) {
            return sl.getPlayerByUUID(linkedPlayerUUID);
        }
        return null;
    }

    @Override
    public boolean isLinkedToPlayer(Player p) {
        return p.getUUID().equals(linkedPlayerUUID);
    }

    @Override
    public void tick() {
        super.tick();

        // 1) if owner gone, drop
        Player owner = getLinkedPlayer();
        if (owner == null || owner.isRemoved()) {
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        // 2) movement & control
        if (this.getControllingPassenger() instanceof Player rider) {
            // copy rotation
            this.setYRot(rider.getYRot());
            this.setXRot(rider.getXRot() * 0.5F);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();

            // forward & strafe
            float forward = rider.zza, strafe = rider.xxa;
            float baseSpeed = (float)this.getAttributeValue(Attributes.MOVEMENT_SPEED);
            this.setSpeed(baseSpeed * 0.5F);
            super.travel(new Vec3(strafe, this.getDeltaMovement().y, forward));

            // BUG-FIX: sync the rider’s server position so chunks & sounds follow
            if (rider instanceof ServerPlayer sp) {
                sp.teleportTo(getX(), getY(), getZ());
            }
        }
    }

    // prevent vanilla dismount
    @Override
    public void removePassenger(Entity passenger) {
        // no-op
    }

    @Override
    public void stopRiding() {
        // no-op
    }

    @Override
    public Vec3 getDismountLocationForPassenger(LivingEntity passenger) {
        return null; // block dismount
    }
}