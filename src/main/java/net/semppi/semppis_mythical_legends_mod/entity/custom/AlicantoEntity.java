package net.semppi.semppis_mythical_legends_mod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

public class AlicantoEntity extends Animal implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public AlicantoEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static boolean canAlicantoSpawn(EntityType<AlicantoEntity> type, LevelAccessor world, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        BlockState blockState = world.getBlockState(pos.below());
        return (blockState.is(Blocks.SAND) || blockState.is(Blocks.RED_SAND)) && world.getMaxLocalRawBrightness(pos) > 8;
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20D)
                .add(Attributes.ATTACK_DAMAGE, 1.0f)
                .add(Attributes.ATTACK_SPEED, 0.8f)
                .add(Attributes.MOVEMENT_SPEED, 0.4f).build();
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.25D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, 10.0F, 1.6D, 1.4D));
        this.goalSelector.addGoal(3, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(6, (new HurtByTargetGoal(this)).setAlertOthers());
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource source) {
        // Prevent fall damage entirely
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
        return ModEntities.ALICANTO.get().create(level);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if(tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.alicanto.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.alicanto.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.STRIDER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.FROG_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.FROG_DEATH;
    }

    protected float getSoundVolume() {
        return 0.4F;
    }
    /// ToDo: Add the looting enchantment effect to alicantos drop for avian meat if killed with the looting enchant
    /// ToDo: Add raw avian meat to drop cooked avian meat if entity is burning for a drop

    @Override
    protected void dropFromLootTable(DamageSource source, boolean causedByPlayer) {
        // Drop raw avian meat
        int dropCount = this.random.nextInt(2) + 1; // Randomly generate a number between 1 and 2
        for (int i = 0; i < dropCount; i++) {
            this.spawnAtLocation(new ItemStack(ModItems.RAW_AVIAN_MEAT.get()));
        }

        super.dropFromLootTable(source, causedByPlayer);
    }
}
