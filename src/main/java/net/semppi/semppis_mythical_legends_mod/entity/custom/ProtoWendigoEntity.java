package net.semppi.semppis_mythical_legends_mod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.semppi.semppis_mythical_legends_mod.entity.PlayerLinkedEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public class ProtoWendigoEntity extends Animal implements GeoEntity, PlayerRideableJumping, PlayerLinkedEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    private boolean isJumping;
    private float playerJumpPendingScale;
    protected boolean allowStandSliding;
    private boolean isTransformed = false;
    private boolean isDominant = false;
    private UUID linkedPlayerUUID = null;

    public ProtoWendigoEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.0F);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.ATTACK_DAMAGE, 2.0f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.JUMP_STRENGTH, 0.7f)
                .add(Attributes.MOVEMENT_SPEED, 0.27f)
                .build();
    }

    public void setDominant(boolean dominant) {
        this.isDominant = dominant;
        this.registerGoals();
    }

    public boolean isDominant() {
        return this.isDominant;
    }

    @Override
    protected void registerGoals() {
        // Clear all existing goals first
        clearGoals(this.goalSelector);
        clearGoals(this.targetSelector);

        if (!this.isTransformed) {
            // Normal AI for non-transformed Proto Wendigos
            addDefaultGoals();
        } else if (this.isDominant) {
            // Transformed and dominant: Reuse default AI goals
            addDefaultGoals();
        } else {
            // Transformed and player-controlled: No AI
        }
    }

    private void addDefaultGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));
        applyOpenDoorsAbility();

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && !isLinkedPlayer((Player) this.target);
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
    }

    private boolean isLinkedPlayer(Player player) {
        return player != null && this.linkedPlayerUUID != null && player.getUUID().equals(this.linkedPlayerUUID);
    }

    @Override
    public void setLinkedPlayer(Player player) {
        this.linkedPlayerUUID = player.getUUID();
    }

    @Override
    public Player getLinkedPlayer() {
        if (this.linkedPlayerUUID == null) return null;
        return this.level().getServer().getPlayerList().getPlayer(this.linkedPlayerUUID);
    }

    @Override
    public boolean isLinkedToPlayer(Player player) {
        return player != null && this.linkedPlayerUUID != null && player.getUUID().equals(this.linkedPlayerUUID);
    }

    private void applyOpenDoorsAbility() {
        if (GoalUtils.hasGroundPathNavigation(this)) {
            ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        }
    }

    private void clearGoals(GoalSelector goalSelector) {
        try {
            Field availableGoalsField = GoalSelector.class.getDeclaredField("availableGoals");
            availableGoalsField.setAccessible(true);
            Set<?> availableGoals = (Set<?>) availableGoalsField.get(goalSelector);
            availableGoals.clear();
        } catch (NoSuchFieldException | IllegalAccessException e) {
        }
    }

    public void setTransformed(boolean transformed, boolean isDominant, @Nullable Player linkedPlayer) {
        this.isTransformed = transformed;
        this.isDominant = isDominant;

        if (linkedPlayer instanceof ServerPlayer serverPlayer) {
            this.linkedPlayerUUID = serverPlayer.getUUID();
            this.setInvisible(true); // Make the player invisible
            this.setNoGravity(false); // Smooth movement

            // Make the player ride this entity
            serverPlayer.startRiding(this, true);
        } else {
            this.linkedPlayerUUID = null;
        }

        this.registerGoals(); // Update goals based on transformation
    }

    public void setTransformedState(ServerPlayer player, boolean transformed) {
        this.isTransformed = transformed;

        if (transformed) {
            // Link the player to this entity
            this.linkedPlayerUUID = player.getUUID();
            player.setInvisible(true); // Hide the player model
            this.setNoGravity(false);  // Smooth movement
            player.startRiding(this, true); // Ensure the player starts riding the entity
        } else {
            // Revert transformation
            player.stopRiding();
            player.setInvisible(false);
            this.linkedPlayerUUID = null; // Clear the link
            this.setNoGravity(false);    // Reset gravity
        }

        this.registerGoals(); // Update AI goals based on the state
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mate) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 10, this::predicate));
    }

    private <T extends GeoEntity> PlayState predicate(AnimationState<T> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected void playJumpSound() {
        this.playSound(SoundEvents.HORSE_JUMP, 0.4F, 1.0F);
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.HUSK_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.HUSK_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.HUSK_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.HUSK_DEATH;
    }

    protected float getSoundVolume() {
        return 0.2F;
    }

    public void setTransformed(boolean transformed) {
        this.isTransformed = transformed;
        if (!transformed) {
            this.linkedPlayerUUID = null;
        }
    }

    public boolean isTransformed() {
        return this.isTransformed;
    }

    @Override
    protected void tickRidden(Player rider, Vec3 travelVector) {
        super.tickRidden(rider, travelVector);

        // Head and body rotation handling
        float headRotationLimit = 70.0F; // Max head rotation in degrees before body starts turning
        float headRotationDifference = Mth.wrapDegrees(rider.getYRot() - this.yBodyRot);

        // Smooth body rotation when the head exceeds the limit
        if (Math.abs(headRotationDifference) > headRotationLimit) {
            this.yBodyRot += headRotationDifference * 0.1F; // Gradually rotate the body
            this.yBodyRot = Mth.wrapDegrees(this.yBodyRot); // Ensure the body rotation stays within bounds
            this.setYRot(this.yBodyRot);
        }

        // Apply smooth head rotation to avoid snapping
        this.yHeadRot = this.yBodyRot + Mth.clamp(headRotationDifference, -headRotationLimit, headRotationLimit);
        this.setXRot(rider.getXRot() * 0.5F);

        if (this.isControlledByLocalInstance()) {
            float speed = this.getRiddenSpeed(rider) * 0.2F;
            this.setSpeed(speed);
            this.travel(travelVector);

            if (this.onGround()) {
                this.setIsJumping(false);
                if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
                    this.executeRidersJump(this.playerJumpPendingScale, travelVector);
                }
                this.playerJumpPendingScale = 0.0F;
            }
        }
    }

    @Override
    public void stopRiding() {
        if (this.isTransformed) {
            return;
        }
        super.stopRiding();
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            Player player = (Player) this.getControllingPassenger();
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setRot(player.getYRot(), player.getXRot());

            this.setMaxUpStep(1.0F);

            float forward = player.zza;
            float strafe = player.xxa;

            if (this.isControlledByLocalInstance()) {
                float speedFactor = this.isTransformed() ? 0.5F : 0.3F;
                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedFactor);
                super.travel(new Vec3(strafe, travelVector.y, forward));
            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }

            this.calculateEntityAnimation(this.isSprinting());
        } else {
            super.travel(travelVector);
        }
    }

    @Override
    public void tick() {
        super.tick();

        // Skip non-transformed
        if (!this.isTransformed) return;

        // Keep the link—if they’re not already riding, force it
        Player linked = this.getLinkedPlayer();
        if (linked instanceof ServerPlayer sp && !sp.isPassenger()) {
            sp.startRiding(this, true);
        }

        // Movement & sync
        if (this.getControllingPassenger() instanceof Player player) {
            // copy rotation
            this.setYRot(player.getYRot());
            this.yRotO    = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);

            // travel
            float forward = player.zza, strafe = player.xxa;
            float speedFactor = 0.5F;
            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * speedFactor);
            super.travel(new Vec3(strafe, this.getDeltaMovement().y, forward));

            // **— GHOST FIX —**
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(this.getX(), this.getY(), this.getZ());
            }
        }
    }

    protected Vec2 getRiddenRotation(LivingEntity entity) {
        return new Vec2(entity.getXRot() * 0.5F, entity.getYRot());
    }

    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        if (this.onGround() && this.playerJumpPendingScale == 0.0F && !this.allowStandSliding) {
            return Vec3.ZERO;
        } else {
            float strafe = player.xxa * 0.5F;
            float forward = player.zza;
            if (forward <= 0.0F) {
                forward *= 0.25F;
            }
            return new Vec3(strafe, 0.0, forward);
        }
    }

    protected float getRiddenSpeed(Player player) {
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof Player ? (Player) this.getFirstPassenger() : null;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsTransformed", this.isTransformed);
        if (this.linkedPlayerUUID != null) {
            tag.putUUID("LinkedPlayer", this.linkedPlayerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.isTransformed = tag.getBoolean("IsTransformed");
        if (tag.hasUUID("LinkedPlayer")) {
            this.linkedPlayerUUID = tag.getUUID("LinkedPlayer");
        }
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isTransformed && this.getControllingPassenger() == passenger) {
            this.setYRot(passenger.getYRot());
            this.yHeadRot = passenger.getYRot();

            // Sync player position with entity immediately
            if (passenger instanceof ServerPlayer serverPlayer) {
                serverPlayer.teleportTo(this.getX(), this.getY(), this.getZ());
            }
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (passenger instanceof Player player && this.isTransformed) {
            if (this.isLinkedToPlayer(player)) {
                this.setTransformedState((ServerPlayer) player, false);
            }
        }
        super.removePassenger(passenger);
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty() && passenger instanceof Player;
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        this.playerJumpPendingScale = jumpPower >= 90 ? 1.0F : 0.4F + 0.4F * (float) jumpPower / 90.0F;
    }

    @Override
    public boolean canJump() {
        return true;
    }

    @Override
    public void handleStartJump(int jumpPower) {
        this.allowStandSliding = true;
        this.setIsJumping(true);
        this.playJumpSound();
    }

    @Override
    public void handleStopJump() {
        this.allowStandSliding = false;
    }

    protected void setIsJumping(boolean isJumping) {
        this.isJumping = isJumping;
    }

    protected boolean isJumping() {
        return this.isJumping;
    }

    protected void executeRidersJump(float playerJumpPendingScale, Vec3 travelVector) {
        double jumpStrength = this.getAttributeValue(Attributes.JUMP_STRENGTH);
        double jumpHeight = jumpStrength * (double) playerJumpPendingScale * (double) this.getBlockJumpFactor();
        double jumpBoost = jumpHeight + this.getJumpBoostPower();
        Vec3 deltaMovement = this.getDeltaMovement();
        this.setDeltaMovement(deltaMovement.x, jumpBoost, deltaMovement.z);
        this.setIsJumping(true);
        this.hasImpulse = true;
        ForgeHooks.onLivingJump(this);
        if (travelVector.z > 0.0) {
            float f = Mth.sin(this.getYRot() * 0.017453292F);
            float f1 = Mth.cos(this.getYRot() * 0.017453292F);
            this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f * playerJumpPendingScale), 0.0, (double) (0.4F * f1 * playerJumpPendingScale)));
        }
    }
}