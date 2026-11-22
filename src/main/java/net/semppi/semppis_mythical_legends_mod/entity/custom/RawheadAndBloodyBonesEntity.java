package net.semppi.semppis_mythical_legends_mod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import net.semppi.semppis_mythical_legends_mod.territory.CreatureTerritories;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.semppi.semppis_mythical_legends_mod.entity.personality.CreaturePersonality;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;

public class RawheadAndBloodyBonesEntity extends Animal implements GeoEntity {
    public static final ResourceLocation ANIMATION_RESOURCE =
            new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "animations/rawhead.animation.json");

    private static final EntityDataAccessor<Integer> WATER_POSE =
            SynchedEntityData.defineId(RawheadAndBloodyBonesEntity.class, EntityDataSerializers.INT);
    private static final int LAND = 0;
    private static final int SWIM_IDLE = 1;
    private static final int SWIM = 2;
    private static final int FLOOR = 3;
    private static final int FLOAT = 4;

    private Vec3 swimVelocity = Vec3.ZERO;

    private static final int BACKYARD_RADIUS = 80;
    private static final int PATROL_RADIUS = 160;
    private static final int OZARK_SPAWN_CHANCE = 4;

    private static final String SHY = "shy";
    private static final String CURIOUS = "curious";
    private static final String HOSTILE = "hostile";
    private static final String[] TEST_TRAITS = {SHY, CURIOUS, HOSTILE};
    private static final Set<String> CORE_TRAITS = Set.of();

    private CreaturePersonality personality = new CreaturePersonality(CORE_TRAITS, Set.of());
    @Nullable
    private CreatureTerritories.Territory territory;
    private boolean claimTerritoryOnAdd;
    private AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final EntityDataAccessor<Boolean> IS_OZARK =
            SynchedEntityData.defineId(RawheadAndBloodyBonesEntity.class, EntityDataSerializers.BOOLEAN);

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

        this.entityData.define(WATER_POSE, LAND);
        this.entityData.define(IS_OZARK, false);
        this.entityData.define(HAS_CHAINS, false);
        this.entityData.define(HAS_RAGS, false);
        this.entityData.define(HAIR_VARIANT, 0);
    }

    // Getters used by the GeckoLib model

    public boolean isOzark() {
        return this.entityData.get(IS_OZARK);
    }

    public boolean hasChains() {
        return !this.isOzark() && this.entityData.get(HAS_CHAINS);
    }

    public boolean hasRags() {
        return !this.isOzark() && this.entityData.get(HAS_RAGS);
    }

    public int getHairVariant() {
        return this.isOzark() ? 0 : this.entityData.get(HAIR_VARIANT);
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

        if (!level.isClientSide()) {
            boolean ozark = this.random.nextInt(OZARK_SPAWN_CHANCE) == 0;
            this.entityData.set(IS_OZARK, ozark);
            this.entityData.set(HAS_CHAINS, !ozark && this.random.nextBoolean());
            this.entityData.set(HAS_RAGS, !ozark && this.random.nextBoolean());
            this.entityData.set(HAIR_VARIANT, ozark ? 0 : this.random.nextInt(3));

            if (spawnType == MobSpawnType.SPAWN_EGG) {
                this.claimTerritoryOnAdd = true;
                this.setPersistenceRequired();
            }
        }

        return result;
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }

        if (this.personality.individualTraits().isEmpty()) {
            String trait = TEST_TRAITS[this.random.nextInt(TEST_TRAITS.length)];
            this.personality = new CreaturePersonality(CORE_TRAITS, Set.of(trait));
        }

        CreatureTerritories territories = CreatureTerritories.get(level);
        CompoundTag ownerData = new CompoundTag();
        this.saveAppearance(ownerData);
        this.personality.save(ownerData);
        if (this.claimTerritoryOnAdd) {
            territories.claim(this.getUUID(), level, this.blockPosition(),
                    BACKYARD_RADIUS, PATROL_RADIUS, ownerData);
            this.claimTerritoryOnAdd = false;
        }
        territories.updateOwnerData(this.getUUID(), ownerData);
        this.territory = territories.getTerritory(this.getUUID());
    }

    @Override
    public void remove(RemovalReason reason) {
        if (reason.shouldDestroy() && this.level() instanceof ServerLevel level) {
            CreatureTerritories.get(level).remove(this.getUUID());
        }
        super.remove(reason);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        this.saveAppearance(tag);
        this.personality.save(tag);
    }

    private void saveAppearance(CompoundTag tag) {
        tag.putBoolean("RawheadIsOzark", this.isOzark());
        tag.putBoolean("RawheadHasChains", this.hasChains());
        tag.putBoolean("RawheadHasRags", this.hasRags());
        tag.putInt("RawheadHairVariant", this.getHairVariant());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.personality = CreaturePersonality.load(tag, CORE_TRAITS);
        this.entityData.set(IS_OZARK, tag.getBoolean("RawheadIsOzark"));

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
        this.goalSelector.addGoal(1, new WaterActivityGoal());
        this.goalSelector.addGoal(1, new FloatGoal(this) {
            @Override
            public boolean canUse() {
                return (isOzark() || !isInWater()) && super.canUse();
            }
        });
        this.goalSelector.addGoal(2, new PersonalityGoal());
        this.goalSelector.addGoal(3, new SeekWaterGoal());
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            protected @Nullable Vec3 getPosition() {
                if (!allowsPosition(blockPosition()) && territory != null) {
                    Vec3 home = Vec3.atBottomCenterOf(territory.center());
                    return DefaultRandomPos.getPosTowards(RawheadAndBloodyBonesEntity.this,
                            10, 7, home, Math.PI / 2);
                }
                Vec3 position = super.getPosition();
                return position != null && allowsPosition(BlockPos.containing(position))
                        ? position : null;
            }
        });
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    private boolean allowsPosition(BlockPos pos) {
        if (this.territory == null || !(this.level() instanceof ServerLevel level)
                || !this.territory.dimension().equals(level.dimension().location())) {
            return true;
        }
        CreatureTerritories.Zone zone = this.territory.zoneAt(level, pos);
        return zone == CreatureTerritories.Zone.BACKYARD
                || (zone == CreatureTerritories.Zone.PATROL && hasTrait(HOSTILE));
    }

    private boolean hasTrait(String trait) {
        return this.personality.individualTraits().contains(trait);
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        if (type == MoverType.SELF && this.territory != null
                && allowsPosition(this.blockPosition())
                && !allowsPosition(BlockPos.containing(this.position().add(movement)))) {
            this.getNavigation().stop();
            movement = new Vec3(0, movement.y, 0);
        }
        super.move(type, movement);
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return this.isOzark() ? super.decreaseAirSupply(air) : this.getMaxAirSupply();
    }

    @Override
    public void travel(Vec3 input) {
        if (!this.isOzark() && this.isInWater()) {
            if (!this.level().isClientSide()) {
                this.setDeltaMovement(this.swimVelocity);
                this.move(MoverType.SELF, this.swimVelocity);
                this.setDeltaMovement(Vec3.ZERO);
            }
            return;
        }
        super.travel(input);
    }

    private boolean canSwimTo(BlockPos pos) {
        return allowsPosition(pos) && this.level().hasChunkAt(pos)
                && this.level().getFluidState(pos).is(FluidTags.WATER)
                && this.level().noCollision(this,
                        this.getBoundingBox().move(Vec3.atBottomCenterOf(pos).subtract(this.position())));
    }

    private class SeekWaterGoal extends Goal {
        private static final float SEEK_CHANCE = 0.65F;
        private Path path;
        private BlockPos water;
        private int nextAttempt;

        SeekWaterGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (isOzark() || isInWater() || tickCount < this.nextAttempt) {
                return false;
            }
            this.nextAttempt = tickCount + 100 + random.nextInt(100);
            if (random.nextFloat() >= SEEK_CHANCE) {
                return false;
            }

            BlockPos origin = blockPosition();
            int pathsTried = 0;
            for (BlockPos horizontal : BlockPos.spiralAround(origin, 10,
                    Direction.EAST, Direction.SOUTH)) {
                for (int dy = 1; dy >= -2; dy--) {
                    BlockPos candidate = horizontal.offset(0, dy, 0);
                    if (!canSwimTo(candidate)) {
                        continue;
                    }
                    Path candidatePath = getNavigation().createPath(candidate, 0);
                    if (candidatePath != null && candidatePath.canReach()) {
                        this.path = candidatePath;
                        this.water = candidate.immutable();
                        return true;
                    }
                    if (++pathsTried >= 4) {
                        return false;
                    }
                }
            }
            return false;
        }

        @Override
        public void start() {
            getNavigation().moveTo(this.path, 1.05);
        }

        @Override
        public boolean canContinueToUse() {
            return !isOzark() && !isInWater() && !getNavigation().isDone() && canSwimTo(this.water);
        }

        @Override
        public void stop() {
            getNavigation().stop();
        }
    }

    private class WaterActivityGoal extends Goal {
        private Vec3 destination;
        private Player player;
        private int restPose;
        private int nextActivity;
        private int nextObservation;

        WaterActivityGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return !isOzark() && isInWater();
        }

        @Override
        public void start() {
            getNavigation().stop();
            this.nextObservation = tickCount;
            this.nextActivity = tickCount;
        }

        @Override
        public void stop() {
            this.player = null;
            this.destination = null;
            swimVelocity = Vec3.ZERO;
            entityData.set(WATER_POSE, LAND);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (tickCount >= this.nextObservation) {
                this.nextObservation = tickCount + 10;
                Player previous = this.player;
                this.player = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(10),
                                candidate -> candidate.isAlive() && !candidate.isCreative()
                                        && !candidate.isSpectator() && distanceToSqr(candidate) <= 100
                                        && allowsPosition(candidate.blockPosition()) && hasLineOfSight(candidate))
                        .stream().min(Comparator.comparingDouble(
                                RawheadAndBloodyBonesEntity.this::distanceToSqr)).orElse(null);
                if (this.player != null) {
                    this.restPose = SWIM_IDLE;
                    this.destination = reactionDestination(this.player);
                } else if (previous != null || tickCount >= this.nextActivity) {
                    chooseActivity();
                }
            }

            swimVelocity = Vec3.ZERO;
            if (this.destination != null) {
                Vec3 offset = this.destination.subtract(position());
                if (offset.lengthSqr() > 0.0025) {
                    double speed = hasTrait(SHY) && this.player != null ? 0.17 : 0.11;
                    Vec3 step = offset.scale(Math.min(1, speed / offset.length()));
                    BlockPos next = BlockPos.containing(position().add(step));
                    if (allowsPosition(next) && level().hasChunkAt(next)
                            && level().getFluidState(next).is(FluidTags.WATER)
                            && level().noCollision(RawheadAndBloodyBonesEntity.this,
                                    getBoundingBox().move(step))) {
                        swimVelocity = step;
                    } else {
                        this.destination = null;
                        this.restPose = SWIM_IDLE;
                    }
                }
            }

            if (this.player != null && !hasTrait(SHY)) {
                getLookControl().setLookAt(this.player);
            }
            if (swimVelocity.horizontalDistanceSqr() > 0.0001) {
                float yaw = (float) Math.toDegrees(Math.atan2(-swimVelocity.x, swimVelocity.z));
                setYRot(yaw);
                setYBodyRot(yaw);
            }
            entityData.set(WATER_POSE, swimVelocity.lengthSqr() > 0 ? SWIM : this.restPose);
        }

        private void chooseActivity() {
            this.nextActivity = tickCount + 100 + random.nextInt(101);
            this.restPose = SWIM_IDLE;
            this.destination = null;
            int choice = random.nextInt(3);
            if (choice == 0) {
                this.destination = findWaterDirection(null, false);
                return;
            }

            BlockPos current = blockPosition();
            BlockPos bottom = current;
            BlockPos top = current;
            for (int i = 0; i < 12 && canSwimTo(bottom.below()); i++) {
                bottom = bottom.below();
            }
            for (int i = 0; i < 12 && canSwimTo(top.above()); i++) {
                top = top.above();
            }
            if (choice == 1 && canSwimTo(bottom)
                    && level().loadedAndEntityCanStandOn(bottom.below(), RawheadAndBloodyBonesEntity.this)) {
                this.destination = Vec3.atBottomCenterOf(bottom);
                this.restPose = FLOOR;
            } else if (top.getY() - bottom.getY() >= 2) {
                BlockPos midpoint = bottom.above((top.getY() - bottom.getY()) / 2);
                this.destination = Vec3.atBottomCenterOf(midpoint);
                this.restPose = FLOAT;
            }
        }

        private @Nullable Vec3 reactionDestination(Player target) {
            boolean flee = hasTrait(SHY);
            if (!flee && distanceToSqr(target) <= 16) {
                return null;
            }
            return findWaterDirection(target, flee);
        }

        private @Nullable Vec3 findWaterDirection(@Nullable Player target, boolean flee) {
            Vec3 best = null;
            double score = target == null ? 0 : distanceToSqr(target);
            for (int i = 0; i < 16; i++) {
                BlockPos candidate = blockPosition().offset(
                        random.nextInt(9) - 4, random.nextInt(5) - 2, random.nextInt(9) - 4);
                if (candidate.equals(blockPosition()) || !canSwimTo(candidate)) {
                    continue;
                }
                Vec3 destination = Vec3.atBottomCenterOf(candidate);
                if (target == null) {
                    return destination;
                }
                double distance = destination.distanceToSqr(target.position());
                if (flee ? distance > score : distance < score && distance >= 9) {
                    best = destination;
                    score = distance;
                }
            }
            return best;
        }
    }

    private class PersonalityGoal extends Goal {
        private static final double CURIOUS_DISTANCE_SQUARED = 36;
        private static final double CLOSE_DISTANCE_SQUARED = 16;
        private static final int PATH_INTERVAL = 10;

        private Player player;
        private int nextSearchTick;
        private int nextPathTick;
        private int nextAttackTick;

        PersonalityGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (tickCount < this.nextSearchTick || !allowsPosition(blockPosition())) {
                return false;
            }
            this.nextSearchTick = tickCount + PATH_INTERVAL;

            double range = hasTrait(SHY) ? 10 : 16;
            this.player = level().getEntitiesOfClass(Player.class, getBoundingBox().inflate(range),
                            candidate -> canReactTo(candidate, range * range) && hasLineOfSight(candidate))
                    .stream()
                    .min(Comparator.comparingDouble(RawheadAndBloodyBonesEntity.this::distanceToSqr))
                    .orElse(null);
            return this.player != null;
        }

        @Override
        public boolean canContinueToUse() {
            double range = hasTrait(SHY) ? 13 : 20;
            return this.player != null && allowsPosition(blockPosition())
                    && canReactTo(this.player, range * range);
        }

        private boolean canReactTo(Player candidate, double rangeSquared) {
            return candidate.isAlive() && !candidate.isCreative() && !candidate.isSpectator()
                    && allowsPosition(candidate.blockPosition()) && distanceToSqr(candidate) <= rangeSquared;
        }

        @Override
        public void start() {
            this.nextPathTick = tickCount;
        }

        @Override
        public void stop() {
            this.player = null;
            getNavigation().stop();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            if (!canContinueToUse()) {
                getNavigation().stop();
                return;
            }

            getLookControl().setLookAt(this.player);
            double distanceSquared = distanceToSqr(this.player);
            boolean hostile = hasTrait(HOSTILE);
            boolean curious = hasTrait(CURIOUS);

            if (hostile && tickCount >= this.nextAttackTick && hasLineOfSight(this.player)
                    && isWithinMeleeAttackRange(this.player)) {
                this.nextAttackTick = tickCount + 20;
                swing(InteractionHand.MAIN_HAND);
                doHurtTarget(this.player);
            }

            if (curious && distanceSquared >= CLOSE_DISTANCE_SQUARED
                    && distanceSquared <= CURIOUS_DISTANCE_SQUARED) {
                getNavigation().stop();
                return;
            }

            if (tickCount < this.nextPathTick) {
                return;
            }
            this.nextPathTick = tickCount + PATH_INTERVAL;

            boolean retreat = hasTrait(SHY) || (curious && distanceSquared < CLOSE_DISTANCE_SQUARED);
            Vec3 destination = retreat
                    ? DefaultRandomPos.getPosAway(RawheadAndBloodyBonesEntity.this, 12, 5, this.player.position())
                    : this.player.position();
            if (destination == null || !allowsPosition(BlockPos.containing(destination))) {
                getNavigation().stop();
                return;
            }

            Path path = getNavigation().createPath(BlockPos.containing(destination), 0);
            if (path != null) {
                getNavigation().moveTo(path, hostile ? 1.35 : retreat ? 1.4 : 1.05);
            }
        }
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
        String animation = animationState.isMoving() ? "walk" : "idle";
        String waterAnimation = switch (this.entityData.get(WATER_POSE)) {
            case SWIM_IDLE -> "swim_idle";
            case SWIM -> "swim";
            case FLOOR -> "water_floor_1";
            case FLOAT -> "water_float_1";
            default -> null;
        };
        if (waterAnimation != null) {
            BakedAnimations animations = GeckoLibCache.getBakedAnimations().get(ANIMATION_RESOURCE);
            if (animations != null && animations.getAnimation(waterAnimation) != null) {
                animation = waterAnimation;
            }
        }
        animationState.getController().setAnimation(RawAnimation.begin().thenLoop(animation));
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
