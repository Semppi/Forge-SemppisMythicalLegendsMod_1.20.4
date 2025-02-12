package net.semppi.semppis_mythical_legends_mod.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.semppi.semppis_mythical_legends_mod.util.EntityAttributesRegistry;

public class TransformMountEntity extends Mob {

    private Player owner;

    public TransformMountEntity(EntityType<? extends TransformMountEntity> type, Level level) {
        super(type, level);
    }

    public TransformMountEntity(EntityType<? extends Mob> entityType, Level level, Player owner) {
        super(entityType, level);
        this.owner = owner;

        // Fetch and apply attributes from the registry
        EntityAttributesRegistry.Attributes attributes = EntityAttributesRegistry.getAttributes(entityType);
        if (attributes != null) {
            this.setBoundingBox(new AABB(
                    this.getX() - attributes.width / 2, this.getY(), this.getZ() - attributes.width / 2,
                    this.getX() + attributes.width / 2, this.getY() + attributes.height, this.getZ() + attributes.width / 2
            ));
        }
    }

    @Override
    public boolean isPushable() {
        return false; // Prevent dismount due to collisions
    }

    @Override
    public void tick() {
        super.tick();

        if (owner != null && !owner.isRemoved()) {
            // Synchronize with the owner's position and rotation
            this.setPos(owner.getX(), owner.getY(), owner.getZ());
            this.setYRot(owner.getYRot());
            this.setXRot(owner.getXRot());

            // Ensure the owner remains a passenger
            if (!owner.isPassenger()) {
                owner.startRiding(this, true);
            }
        } else {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean canRide(Entity vehicle) {
        return vehicle instanceof Boat; // Allow riding boats or other vehicles
    }

    public void setOwner(Player owner) {
        if (owner != null) {
            this.owner = owner;
        }
    }

    public Player getOwner() {
        return owner;
    }
}