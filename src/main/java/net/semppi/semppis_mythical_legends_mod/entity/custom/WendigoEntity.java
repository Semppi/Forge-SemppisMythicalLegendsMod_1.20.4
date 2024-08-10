package net.semppi.semppis_mythical_legends_mod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.ForgeEventFactory;
import net.semppi.semppis_mythical_legends_mod.entity.variant.PukisVariant;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import software.bernie.geckolib.core.animation.RawAnimation;

public class WendigoEntity extends TamableAnimal implements GeoEntity, PlayerRideableJumping {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT =
            SynchedEntityData.defineId(WendigoEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(WendigoEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean isJumping;
    private float playerJumpPendingScale;
    protected boolean allowStandSliding;
    private boolean isTransformed = false; // Indicates if Wendigo is in transformation mode

    public WendigoEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(2.0F);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 100D)
                .add(Attributes.ATTACK_DAMAGE, 9.0f)
                .add(Attributes.ATTACK_SPEED, 0.6f)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.30D)
                .add(Attributes.JUMP_STRENGTH, 0.7D)
                .build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        applyOpenDoorsAbility();

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, AbstractIllager.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, SatyrEntity.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Pig.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Cow.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Sheep.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Chicken.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Rabbit.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Fox.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Wolf.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, AbstractHorse.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Zombie.class, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, WendigoEntity.class, true));
    }

    private void applyOpenDoorsAbility() {
        if (GoalUtils.hasGroundPathNavigation(this)) {
            ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        }
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        // Prevent fall damage entirely
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mate) {
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "sitController", 0, this::sitPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackController", 0, this::attackingPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.wendigo.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.wendigo.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sitPredicate(AnimationState<T> tAnimationState) {
        if (this.isSitting()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.wendigo.sit", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState attackingPredicate(AnimationState<T> tAnimationState) {
        if (this.swinging) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.wendigo.attack", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.GOAT_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.POLAR_BEAR_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.DONKEY_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.HORSE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 1.0F;
    }

    @Override
    public MobType getMobType() {
        return MobType.UNDEAD;
    }

    // Setter for transformation mode
    public void setTransformed(boolean transformed) {
        this.isTransformed = transformed;
    }

    // Getter for transformation mode
    public boolean isTransformed() {
        return this.isTransformed;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        Item item = itemstack.getItem();

        Item itemForTaming = ModItems.HUMANOID_FLESH_CHUNK.get();

        if (item == itemForTaming && !isTame()) {
            if (this.level().isClientSide) {
                return InteractionResult.CONSUME;
            } else {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (!ForgeEventFactory.onAnimalTame(this, player)) {
                    if (!this.level().isClientSide) {
                        super.tame(player);
                        this.navigation.recomputePath();
                        this.setTarget(null);
                        this.level().broadcastEntityEvent(this, (byte)7);
                        setSitting(true);
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        if (isTame() && !this.level().isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (player.isShiftKeyDown()) {
                // Shift + right-click to sit/stand
                setSitting(!isSitting());
                return InteractionResult.SUCCESS;
            } else {
                // Right-click to ride or transform
                if (this.isTransformed()) {
                    // If transformed, disallow normal mounting interactions
                    return InteractionResult.PASS;
                }
                if (this.isSitting()) {
                    setSitting(false); // Stand up when mounted
                }
                player.startRiding(this);
                return InteractionResult.SUCCESS;
            }
        }

        if (itemstack.getItem() == itemForTaming) {
            return InteractionResult.PASS;
        }

        return super.mobInteract(player, hand);
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

            // Ensure the entity remains standing while being ridden
            if (this.isSitting()) {
                this.setSitting(false);
            }
        }
    }

    @Override
    public void stopRiding() {
        if (this.isTransformed()) {
            return;
        }
        super.stopRiding();
        this.setSitting(false);
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
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            Player player = (Player) this.getControllingPassenger();
            this.setYRot(player.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.225F);

            if (this.onGround()) {
                this.setIsJumping(false);
                if (this.playerJumpPendingScale > 0.0F && !this.isJumping()) {
                    this.executeRidersJump(this.playerJumpPendingScale, player.getDeltaMovement());
                }

                this.playerJumpPendingScale = 0.0F;
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
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setSitting(tag.getBoolean("isSitting"));
        this.entityData.set(DATA_ID_TYPE_VARIANT, tag.getInt("Variant"));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("isSitting", this.isSitting());
        tag.putInt("Variant", this.getTypeVariant());
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, false);
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
    }

    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.getControllingPassenger() == passenger) {
            this.setYRot(passenger.getYRot());
            this.yHeadRot = passenger.getYRot();
        }
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
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

    protected void playJumpSound() {
        this.playSound(SoundEvents.HORSE_JUMP, 0.4F, 1.0F);
    }

    /* VARIANTS */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
                                        MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag compoundTag) {
        PukisVariant variant = PukisVariant.getRandomVariant();
        setVariant(variant);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public PukisVariant getVariant() {
        return PukisVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(PukisVariant variant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    @Override
    @Nullable
    public PlayerTeam getTeam() {
        return (PlayerTeam) super.getTeam();
    }
}