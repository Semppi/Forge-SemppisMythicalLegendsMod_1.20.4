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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class RawheadAndBloodyBonesEntity extends Animal implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // Chains: on or off
    private static final EntityDataAccessor<Boolean> HAS_CHAINS =
            SynchedEntityData.defineId(
                    RawheadAndBloodyBonesEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    // Rags: on or off
    private static final EntityDataAccessor<Boolean> HAS_RAGS =
            SynchedEntityData.defineId(
                    RawheadAndBloodyBonesEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    // Hair: 0 = bald, 1 = hair_1, 2 = hair_2
    private static final EntityDataAccessor<Integer> HAIR_VARIANT =
            SynchedEntityData.defineId(
                    RawheadAndBloodyBonesEntity.class,
                    EntityDataSerializers.INT
            );

    public RawheadAndBloodyBonesEntity(
            EntityType<? extends Animal> entityType,
            Level level
    ) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(HAS_CHAINS, false);
        this.entityData.define(HAS_RAGS, false);
        this.entityData.define(HAIR_VARIANT, 0);
    }

    // Getters used by the GeckoLib model

    public boolean hasChains() {
        return this.entityData.get(HAS_CHAINS);
    }

    public boolean hasRags() {
        return this.entityData.get(HAS_RAGS);
    }

    public int getHairVariant() {
        return this.entityData.get(HAIR_VARIANT);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnData,
            @Nullable CompoundTag spawnTag
    ) {
        SpawnGroupData result = super.finalizeSpawn(
                level,
                difficulty,
                spawnType,
                spawnData,
                spawnTag
        );

        // Select appearance only on the server.
        if (!level.isClientSide()) {

            // 50% chance of chains
            this.entityData.set(
                    HAS_CHAINS,
                    this.random.nextBoolean()
            );

            // 50% chance of rags
            this.entityData.set(
                    HAS_RAGS,
                    this.random.nextBoolean()
            );

            // 0 = bald, 1 = hair_1, 2 = hair_2
            this.entityData.set(
                    HAIR_VARIANT,
                    this.random.nextInt(3)
            );
        }

        return result;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putBoolean("RawheadHasChains", this.hasChains());
        tag.putBoolean("RawheadHasRags", this.hasRags());
        tag.putInt("RawheadHairVariant", this.getHairVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("RawheadHasChains")) {
            this.entityData.set(
                    HAS_CHAINS,
                    tag.getBoolean("RawheadHasChains")
            );
        }

        if (tag.contains("RawheadHasRags")) {
            this.entityData.set(
                    HAS_RAGS,
                    tag.getBoolean("RawheadHasRags")
            );
        }

        if (tag.contains("RawheadHairVariant")) {
            int variant = tag.getInt("RawheadHairVariant");

            this.entityData.set(
                    HAIR_VARIANT,
                    Math.max(0, Math.min(2, variant))
            );
        }
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30D)
                .add(Attributes.ATTACK_DAMAGE, 5.0f)
                .add(Attributes.ATTACK_SPEED, 0.8f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.25D));

        this.goalSelector.addGoal(
                3,
                new AvoidEntityGoal<>(
                        this,
                        Player.class,
                        10.0F,
                        1.6D,
                        1.4D
                )
        );

        this.goalSelector.addGoal(
                3,
                new LookAtPlayerGoal(this, Player.class, 8.0F)
        );

        this.goalSelector.addGoal(
                4,
                new WaterAvoidingRandomStrollGoal(this, 1.0D)
        );

        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(
                6,
                new HurtByTargetGoal(this).setAlertOthers()
        );
    }

    @Override
    public boolean causeFallDamage(
            float fallDistance,
            float damageMultiplier,
            DamageSource source
    ) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(
            ServerLevel level,
            AgeableMob ageableMob
    ) {
        return ModEntities.RAWHEAD_AND_BLOODY_BONES.get().create(level);
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllerRegistrar
    ) {
        controllerRegistrar.add(
                new AnimationController<>(
                        this,
                        "controller",
                        0,
                        this::predicate
                )
        );
    }

    private <T extends GeoAnimatable> PlayState predicate(
            AnimationState<T> animationState
    ) {
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(
                    RawAnimation.begin().then(
                            "walk",
                            Animation.LoopType.LOOP
                    )
            );

            return PlayState.CONTINUE;
        }

        animationState.getController().setAnimation(
                RawAnimation.begin().then(
                        "idle",
                        Animation.LoopType.LOOP
                )
        );

        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.DROWNED_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.DROWNED_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.SLIME_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ZOMBIE_DEATH;
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    // TODO: Add Looting enchantment scaling to humanoid flesh.
    // TODO: Add cooked humanoid steak when killed while burning.

    @Override
    protected void dropFromLootTable(
            DamageSource source,
            boolean causedByPlayer
    ) {
        int dropCount = this.random.nextInt(2) + 1;

        for (int i = 0; i < dropCount; i++) {
            this.spawnAtLocation(
                    new ItemStack(ModItems.HUMANOID_FLESH.get())
            );
        }

        super.dropFromLootTable(source, causedByPlayer);
    }
}
