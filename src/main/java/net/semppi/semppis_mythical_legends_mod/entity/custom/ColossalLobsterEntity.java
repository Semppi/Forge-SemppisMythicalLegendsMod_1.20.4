package net.semppi.semppis_mythical_legends_mod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;

public class ColossalLobsterEntity extends WaterAnimal implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public ColossalLobsterEntity(EntityType<? extends WaterAnimal> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(2.0F);
        this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
    }

    public static boolean checkWaterMobSpawnRules(EntityType<ColossalLobsterEntity> entityType, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos).is(Blocks.WATER) &&
                levelAccessor.getBlockState(pos.below()).isFaceSturdy(levelAccessor, pos.below(), Direction.UP);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 130D)
                .add(Attributes.ATTACK_DAMAGE, 8.0f)
                .add(Attributes.ATTACK_SPEED, 0.4f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.ARMOR_TOUGHNESS, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.60D)
                .build();
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.setPathfindingMalus(BlockPathTypes.WATER, 0.9F);

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractFish.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Squid.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Drowned.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Dolphin.class, true));
    }

//    @Nullable
//    @Override
//    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
//        return ModEntities.COLOSSAL_LOBSTER.get().create(level);
//    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackController", 0, this::attackingPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.colossal_lobster.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.colossal_lobster.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState attackingPredicate(AnimationState<T> tAnimationState) {
        if (this.swinging) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.colossal_lobster.attack", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.DRIPSTONE_BLOCK_FALL, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ELDER_GUARDIAN_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SQUID_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.SQUID_DEATH;
    }

    protected float getSoundVolume() {
        return 0.9F;
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
}