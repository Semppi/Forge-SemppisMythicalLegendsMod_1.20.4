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
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.entity.variant.SatyrVariant;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class SatyrEntity extends TamableAnimal implements GeoEntity {
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT =
            SynchedEntityData.defineId(SatyrEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> SITTING =
            SynchedEntityData.defineId(SatyrEntity.class, EntityDataSerializers.BOOLEAN);

    public SatyrEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(1.5F);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16D)
                .add(Attributes.ATTACK_DAMAGE, 2.0f)
                .add(Attributes.ATTACK_SPEED, 1.2f)
                .add(Attributes.MOVEMENT_SPEED, 0.4f)
                .build();
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource source) {
        // Call the super method and store the result if necessary. Adjust damageMultiplier to reduce fall damage.
        boolean result = super.causeFallDamage(distance, damageMultiplier * 0.6F, source);
        return result;
    }
    /// ToDo: Tamed satyrs attack other tames if attacked

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(5, new OpenDoorGoal(this,true));
        applyOpenDoorsAbility();

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Rabbit.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
    }
    private void applyOpenDoorsAbility() {
        if (GoalUtils.hasGroundPathNavigation(this)) {
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        }

    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob mob) {
        SatyrEntity baby = ModEntities.SATYR.get().create(serverLevel);
        if (random.nextFloat() < 0.05) {
            // 5% chance to get a completely random variant
            SatyrVariant variant = SatyrVariant.getRandomVariant();
            baby.setVariant(variant);
        } else {
            // 95% chance to inherit the variant from one of the parents
            if (mob instanceof SatyrEntity) {
                SatyrVariant parentVariant = ((SatyrEntity) mob).getVariant();
                baby.setVariant(parentVariant);
            } else {
                // Fallback to random if something goes wrong
                SatyrVariant variant = SatyrVariant.getRandomVariant();
                baby.setVariant(variant);
            }
        }
        return baby;
    }

    @Override
    public boolean isFood(ItemStack pStack) {
        return pStack.getItem() == ModItems.CHEESE.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 10, this::predicate));
        controllerRegistrar.add(new AnimationController<>(this, "sitController", 0, this::sitPredicate));
        controllerRegistrar.add(new AnimationController<>(this, "attackController", 0, this::attackingPredicate));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.satyr.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.satyr.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sitPredicate(AnimationState<T> tAnimationState) {
        if (this.isSitting()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.satyr.sit", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private <T extends GeoAnimatable> PlayState attackingPredicate(AnimationState<T> tAnimationState) {
        if (this.swinging) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.satyr.attack", Animation.LoopType.PLAY_ONCE));
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.PANDA_AGGRESSIVE_AMBIENT;
    }



    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.LLAMA_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.LLAMA_DEATH;
    }

    protected float getSoundVolume() {
        return 0.2F;
    }
    /// ToDo: Add the looting enchantment effect to satyr drop for raw fey meat if killed with the looting enchant
    /// ToDo: Add raw fey meat to drop cooked fey meat if entity is burning

    @Override
    protected void dropFromLootTable(DamageSource source, boolean causedByPlayer) {
        // Drop fey meat
        int dropCount = this.random.nextInt(2) + 1; // Randomly generate a number between 1 and 2
        for (int i = 0; i < dropCount; i++) {
            this.spawnAtLocation(new ItemStack(ModItems.RAW_FEY_MEAT.get()));
        }

        super.dropFromLootTable(source, causedByPlayer);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        Item item = itemStack.getItem();

        // Handling taming with a specific item
        if (item == ModItems.HONEYED_BERRY_TREAT.get() && !this.isTame()) {
            if (!this.level().isClientSide) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                }
                this.tame(player);
                this.navigation.recomputePath();
                this.setTarget(null);
                this.level().broadcastEntityEvent(this, (byte)7);
                this.setSitting(false); // Ensure the satyr is not sitting after being tamed
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        // Check if the entity is tamed before allowing armor placement or sitting/standing interaction
        if (this.isTame()) {
            // Handling armor equipping with red carpet only if tamed
            if (item == Items.RED_CARPET && !this.hasArmor()) {
                if (!this.level().isClientSide) {
                    this.setHasArmor(true);
                    itemStack.shrink(1);
                    return InteractionResult.SUCCESS;
                }
            }
            // Handling armor unequipping by shift-clicking only if tamed
            else if (player.isCrouching() && this.hasArmor()) {
                if (!this.level().isClientSide) {
                    this.setHasArmor(false);
                    this.spawnAtLocation(Items.RED_CARPET);
                    return InteractionResult.SUCCESS;
                }
            }
            // Toggle sitting/standing state only if tamed and no item interaction occurs
            else if (hand == InteractionHand.MAIN_HAND) {
                setSitting(!this.isSitting());
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }

        // Fallback to parent interaction if none of the above conditions are met
        return super.mobInteract(player, hand);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setSitting(tag.getBoolean("isSitting"));
        this.entityData.set(DATA_ID_TYPE_VARIANT, tag.getInt("Variant"));
        this.setHasArmor(tag.getBoolean("HasArmor")); // Make sure to save the armor state
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("isSitting", this.isSitting());
        tag.putInt("Variant", this.getTypeVariant());
        tag.putBoolean("HasArmor", this.hasArmor()); // Save the armor state
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(SITTING, false);
        this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
        this.entityData.define(HAS_ARMOR, false);
    }

    public boolean hasArmor() {return this.entityData.get(HAS_ARMOR);}

    public void setHasArmor(boolean hasArmor) {
        this.entityData.set(HAS_ARMOR, hasArmor);
    }

    // Ensure the sitting logic is correctly handled
    public void setSitting(boolean sitting) {
        this.entityData.set(SITTING, sitting);
        this.setOrderedToSit(sitting);
    }

    public boolean isSitting() {
        return this.entityData.get(SITTING);
    }

    /* VARIANTS */
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance,
                                        MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData,
                                        @Nullable CompoundTag compoundTag) {
        SatyrVariant variant = SatyrVariant.getRandomVariant();
        setVariant(variant);
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    public SatyrVariant getVariant() {
        return SatyrVariant.byId(this.getTypeVariant() & 255);
    }

    private int getTypeVariant() {
        return this.entityData.get(DATA_ID_TYPE_VARIANT);
    }

    private void setVariant(SatyrVariant variant) {
        this.entityData.set(DATA_ID_TYPE_VARIANT, variant.getId() & 255);
    }

    private static final EntityDataAccessor<Boolean> HAS_ARMOR = SynchedEntityData.defineId(SatyrEntity.class, EntityDataSerializers.BOOLEAN);
}