package net.semppi.semppis_mythical_legends_mod.entity.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import java.util.EnumSet;
import net.minecraft.world.level.block.state.BlockState;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundSource;
import net.semppi.semppis_mythical_legends_mod.damage.ModDamageTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import org.jetbrains.annotations.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.core.animation.*;
import net.semppi.semppis_mythical_legends_mod.sound.ModSounds;
import software.bernie.geckolib.core.object.PlayState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.SpawnGroupData;

public class MandrakeEntity extends Animal implements GeoEntity {
    private AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    @Nullable
    private BlockPos rootPos;

    private boolean pendingInitialRoot = false;

    private int rootingCooldown = 600;

    private int screamCooldown = 0;

    private int screamAnimationTicks = 0;

    private int upsetSoundCooldown = 0;

    private boolean upperRootBlockWasPresent = true;
    private boolean lowerRootBlockWasPresent = true;

    public MandrakeEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 16D)
                .add(Attributes.ATTACK_DAMAGE, 2.0f)
                .add(Attributes.ATTACK_SPEED, 0.8f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f).build();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new PanicGoal(this, 1.25D));


        this.goalSelector.addGoal(3, new MandrakeRootGoal(this, 1.0D));

        this.goalSelector.addGoal(4, new MandrakeLookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new MandrakeRandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new OpenDoorGoal(this, true));

        this.targetSelector.addGoal(
                6,
                (new HurtByTargetGoal(this)).setAlertOthers()
        );

        applyOpenDoorsAbility();
    }
    private void applyOpenDoorsAbility() {
        if (GoalUtils.hasGroundPathNavigation(this)) {
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
        }

    }

    private static class MandrakeLookAtPlayerGoal extends LookAtPlayerGoal {
        private final MandrakeEntity mandrake;

        public MandrakeLookAtPlayerGoal(
                MandrakeEntity mandrake,
                Class<? extends net.minecraft.world.entity.LivingEntity> lookAtType,
                float lookDistance
        ) {
            super(mandrake, lookAtType, lookDistance);
            this.mandrake = mandrake;
        }

        @Override
        public boolean canUse() {
            return !mandrake.isRooted() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !mandrake.isRooted() && super.canContinueToUse();
        }
    }

    private static class MandrakeRandomLookAroundGoal extends RandomLookAroundGoal {
        private final MandrakeEntity mandrake;

        public MandrakeRandomLookAroundGoal(MandrakeEntity mandrake) {
            super(mandrake);
            this.mandrake = mandrake;
        }

        @Override
        public boolean canUse() {
            return !mandrake.isRooted() && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !mandrake.isRooted() && super.canContinueToUse();
        }
    }

    private static class MandrakeRootGoal extends Goal {

        private final MandrakeEntity mandrake;
        private final double speedModifier;

        @Nullable
        private BlockPos targetSurfacePos;

        private int searchDelay;

        public MandrakeRootGoal(
                MandrakeEntity mandrake,
                double speedModifier
        ) {
            this.mandrake = mandrake;
            this.speedModifier = speedModifier;

            this.setFlags(
                    EnumSet.of(Goal.Flag.MOVE)
            );
        }

        @Override
        public boolean canUse() {
            /*
             * Already planted.
             */
            if (mandrake.isRooted()) {
                return false;
            }

            /*
             * Still enjoying its wandering period.
             */
            if (mandrake.rootingCooldown > 0) {
                return false;
            }

            /*
             * Don't perform an expensive area search every tick.
             */
            if (searchDelay > 0) {
                searchDelay--;
                return false;
            }

            searchDelay = 40;

            this.targetSurfacePos = findRootingPosition();

            return this.targetSurfacePos != null;
        }

        @Override
        public boolean canContinueToUse() {
            if (mandrake.isRooted()) {
                return false;
            }

            if (this.targetSurfacePos == null) {
                return false;
            }

            return !mandrake.getNavigation().isDone();
        }

        @Override
        public void start() {
            if (this.targetSurfacePos == null) {
                return;
            }

            mandrake.getNavigation().moveTo(
                    this.targetSurfacePos.getX() + 0.5D,
                    this.targetSurfacePos.getY(),
                    this.targetSurfacePos.getZ() + 0.5D,
                    this.speedModifier
            );
        }

        @Override
        public void tick() {
            if (this.targetSurfacePos == null) {
                return;
            }

            /*
             * Once the Mandrake is close enough to the chosen
             * surface block, try to plant it.
             */
            double targetX =
                    this.targetSurfacePos.getX() + 0.5D;

            double targetY =
                    this.targetSurfacePos.getY();

            double targetZ =
                    this.targetSurfacePos.getZ() + 0.5D;

            double distanceSquared =
                    mandrake.distanceToSqr(
                            targetX,
                            targetY,
                            targetZ
                    );

            if (distanceSquared <= 2.25D) {

                if (mandrake.rootAtPosition(this.targetSurfacePos)) {
                    this.targetSurfacePos = null;
                }
            }
        }

        @Override
        public void stop() {
            this.targetSurfacePos = null;
        }

        @Nullable
        private BlockPos findRootingPosition() {
            BlockPos origin =
                    mandrake.blockPosition();

            /*
             * Try a number of random nearby positions instead of
             * scanning every single block in a huge cube.
             */
            for (int attempt = 0; attempt < 24; attempt++) {

                int offsetX =
                        mandrake.getRandom().nextInt(25) - 12;

                int offsetZ =
                        mandrake.getRandom().nextInt(25) - 12;

                /*
                 * Search a few blocks vertically because terrain
                 * isn't perfectly flat.
                 */
                int offsetY =
                        mandrake.getRandom().nextInt(7) - 3;

                BlockPos candidate =
                        origin.offset(
                                offsetX,
                                offsetY,
                                offsetZ
                        );

                if (mandrake.canRootAt(candidate)) {
                    return candidate.immutable();
                }
            }

            return null;
        }
    }


    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
        return ModEntities.MANDRAKE_SPROUTLING.get().create(level);
    }

    public static boolean canMandrakeSpawn(
            EntityType<MandrakeEntity> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        BlockPos upperSoilPos = pos.below();
        BlockPos lowerSoilPos = pos.below(2);

        if (!isValidMandrakeSoil(level.getBlockState(upperSoilPos))) {
            return false;
        }

        if (!isValidMandrakeSoil(level.getBlockState(lowerSoilPos))) {
            return false;
        }

        if (!level.getBlockState(pos).isAir()) {
            return false;
        }

        if (!level.getBlockState(pos.above()).isAir()) {
            return false;
        }

        return true;
    }

    private static boolean isValidMandrakeSoil(BlockState state) {
        return state.is(Blocks.DIRT)
                || state.is(Blocks.GRASS_BLOCK)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.MUD)
                || state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND);
    }

    private boolean canRootAt(BlockPos surfacePos) {
        BlockPos upperSoilPos = surfacePos.below();
        BlockPos lowerSoilPos = surfacePos.below(2);

        /*
         * Both underground blocks must be valid Mandrake soil.
         */
        if (!isValidMandrakeSoil(this.level().getBlockState(upperSoilPos))) {
            return false;
        }

        if (!isValidMandrakeSoil(this.level().getBlockState(lowerSoilPos))) {
            return false;
        }

        /*
         * The Mandrake needs two free blocks above the soil,
         * just like during natural spawning.
         */
        if (!this.level().getBlockState(surfacePos).isAir()) {
            return false;
        }

        if (!this.level().getBlockState(surfacePos.above()).isAir()) {
            return false;
        }

        return true;
    }

    @Override
    public SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType spawnType,
            @Nullable SpawnGroupData spawnData,
            @Nullable CompoundTag dataTag
    ) {
        SpawnGroupData result = super.finalizeSpawn(
                level,
                difficulty,
                spawnType,
                spawnData,
                dataTag
        );

        /*
         * 50/50 visual sex assignment.
         */
        this.setFemale(this.random.nextBoolean());

        /*
         * Ordinary natural spawning is already happening during normal
         * world operation, so it is safe to root immediately.
         */
        if (spawnType == MobSpawnType.NATURAL) {
            rootAtSpawnPosition();
        }

        /*
         * Do NOT move/root the Mandrake while Minecraft is generating chunks.
         * We discovered that doing so can freeze fresh-world generation.
         *
         * Just remember that it needs to root later.
         */
        else if (spawnType == MobSpawnType.CHUNK_GENERATION) {
            this.pendingInitialRoot = true;
        }

        return result;
    }

    private boolean rootAtPosition(BlockPos surfacePos) {
        /*
         * Make sure nothing changed while the Mandrake
         * was walking toward the location.
         */
        if (!canRootAt(surfacePos)) {
            return false;
        }

        BlockPos lowerSoilPos = surfacePos.below(2);

        this.setRootPos(lowerSoilPos);

        this.setRootState(RootState.FULLY_ROOTED);

        this.setPos(
                lowerSoilPos.getX() + 0.5D,
                lowerSoilPos.getY() + 0.05D,
                lowerSoilPos.getZ() + 0.5D
        );

        this.getNavigation().stop();
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);

        this.rootingCooldown = 0;

        return true;
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
        /*
         * IMPORTANT:
         * Get the exact Mandrake instance this animation controller
         * is currently evaluating.
         */
        MandrakeEntity mandrake =
                (MandrakeEntity) animationState.getAnimatable();

        /*
         * Highest priority: this individual Mandrake's scream state.
         */
        if (mandrake.getScreamState() == ScreamState.ROOTED) {
            animationState.getController().setAnimation(
                    RawAnimation.begin().then(
                            "rooted_scream",
                            Animation.LoopType.PLAY_ONCE
                    )
            );

            return PlayState.CONTINUE;
        }

        if (mandrake.getScreamState() == ScreamState.UPROOTED) {
            animationState.getController().setAnimation(
                    RawAnimation.begin().then(
                            "uprooted_scream",
                            Animation.LoopType.PLAY_ONCE
                    )
            );

            return PlayState.CONTINUE;
        }

        /*
         * This individual Mandrake is rooted but not screaming.
         */
        if (mandrake.isRooted()) {
            animationState.getController().setAnimation(
                    RawAnimation.begin().then(
                            "rooted",
                            Animation.LoopType.LOOP
                    )
            );

            return PlayState.CONTINUE;
        }

        /*
         * This individual Mandrake is walking.
         */
        if (animationState.isMoving()) {
            animationState.getController().setAnimation(
                    RawAnimation.begin().then(
                            "walk",
                            Animation.LoopType.LOOP
                    )
            );

            return PlayState.CONTINUE;
        }

        /*
         * Normal standing idle.
         */
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

    protected void playStepSound(BlockPos pos, BlockState blockIn) {
        this.playSound(SoundEvents.HANGING_ROOTS_PLACE, 0.15F, 1.0F);
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        if (isRooted()) {
            return null;
        }

        return SoundEvents.STRIDER_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundEvents.STRIDER_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.GHAST_HURT;
    }

    protected float getSoundVolume() {
        return 0.1F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean wasRooted = this.isRooted();

        boolean damaged = super.hurt(source, amount);

        /*
         * Only rooted / partially rooted Mandrakes use the scream system.
         */
        if (damaged
                && wasRooted
                && !this.level().isClientSide) {

            /*
             * Real scream is available.
             */
            if (this.screamCooldown <= 0) {

                this.setScreamState(
                        ScreamState.ROOTED
                );

                this.screamAnimationTicks = 50;

                this.playRandomMandrakeScream();

                /*
                 * Damage EVERY player inside the scream zones.
                 */
                if (this.rootPos != null) {
                    this.applyMandrakeScreamDamage(
                            this.rootPos
                    );
                }

                /*
                 * Half a Minecraft day 12000.
                 */
                this.screamCooldown = 12000;
            }

            /*
             * Real scream is on cooldown.
             * Harmless upset sound instead.
             */
            else {
                this.playMandrakeUpsetSound();
            }
        }

        return damaged;
    }

    private void playRandomMandrakeScream() {
        SoundEvent screamSound;

        switch (this.random.nextInt(3)) {
            case 0:
                screamSound = ModSounds.MANDRAKE_SCREAM1.get();
                break;

            case 1:
                screamSound = ModSounds.MANDRAKE_SCREAM2.get();
                break;

            default:
                screamSound = ModSounds.MANDRAKE_SCREAM3.get();
                break;
        }

        float screamPitch =
                1.20F + this.random.nextFloat() * 0.15F;

        this.playSound(
                screamSound,
                1.0F,
                screamPitch
        );
    }

    private void applyMandrakeScreamDamage(BlockPos screamRootPos) {

        /*
         * The rooted block itself is our origin.
         *
         * We expand this one-block box outward in X, Y and Z,
         * creating the same nested zones as the test area.
         */
        AABB rootBlockBox = new AABB(
                screamRootPos.getX(),
                screamRootPos.getY(),
                screamRootPos.getZ(),
                screamRootPos.getX() + 1.0D,
                screamRootPos.getY() + 1.0D,
                screamRootPos.getZ() + 1.0D
        );

        /*
         * Cumulative zone sizes:
         *
         * black  = 3
         * red    = 3 + 5 = 8
         * orange = 8 + 5 = 13
         * yellow = 13 + 3 = 16
         */
        AABB blackZone =
                rootBlockBox.inflate(3.0D);

        AABB redZone =
                rootBlockBox.inflate(8.0D);

        AABB orangeZone =
                rootBlockBox.inflate(12.0D);

        AABB yellowZone =
                rootBlockBox.inflate(15.0D);

        /*
         * We only need to search as far as the outer yellow boundary.
         */
        for (Player player :
                this.level().getEntitiesOfClass(
                        Player.class,
                        yellowZone
                )) {

            /*
             * Spectators aren't physically participating in gameplay.
             */
            if (player.isSpectator()) {
                continue;
            }

            float damage;

            /*
             * Check strongest/closest zone first.
             *
             * We use the player's bounding box rather than only their feet,
             * so if any part of the player enters the zone, that zone counts.
             */
            if (player.getBoundingBox().intersects(blackZone)) {

                damage = 1060.0F;

            } else if (player.getBoundingBox().intersects(redZone)) {

                damage = 600.0F;

            } else if (player.getBoundingBox().intersects(orangeZone)) {

                damage = 250.0F;

            } else if (player.getBoundingBox().intersects(yellowZone)) {

                damage = 18.0F;

            } else {

                /*
                 * Green / safe zone.
                 */
                continue;
            }

            DamageSource screamSource =
                    new DamageSource(
                            this.level()
                                    .registryAccess()
                                    .registryOrThrow(Registries.DAMAGE_TYPE)
                                    .getHolderOrThrow(ModDamageTypes.SOUND)
                    );

            boolean hurtPlayer = player.hurt(
                    screamSource,
                    damage
            );

            if (hurtPlayer) {
                player.addEffect(
                        new MobEffectInstance(
                                MobEffects.CONFUSION,
                                8 * 20,
                                2
                        )
                );
            }
        }
    }

    private void playMandrakeUpsetSound() {
        if (this.upsetSoundCooldown > 0) {
            return;
        }

        this.playSound(
                ModSounds.MANDRAKE_UPSET.get(),
                1.0F,
                1.0F
        );

        /*
         * Prevent repeated hits from stacking multiple upset sounds.
         *
         * Start with 60 ticks = 3 seconds.
         * Adjust this to roughly match the actual length of mandrake_upset.ogg.
         */
        this.upsetSoundCooldown = 60;
    }

    /// ToDo: Add the looting enchantment effect to mandrakes drop for roots if killed with the looting enchant

    @Override
    protected void dropFromLootTable(DamageSource source, boolean causedByPlayer) {

        int dropCount = this.random.nextInt(2) + 1; // Randomly generate a number between 1 and 2
        for (int i = 0; i < dropCount; i++) {
            this.spawnAtLocation(new ItemStack(ModItems.MANDRAKE_ROOT.get()));
        }

        super.dropFromLootTable(source, causedByPlayer);
    }

    @Override
    public InteractionResult mobInteract(
            Player player,
            InteractionHand hand
    ) {
        if (this.isRooted() && this.hasBerries()) {

            if (!this.level().isClientSide) {

                // Drop the harvested berries beside the Mandrake.
                this.spawnAtLocation(
                        new ItemStack(
                                ModItems.MANDRAKE_BERRIES.get(),
                                1
                        )
                );

                this.setHasBerries(false);

                this.level().playSound(
                        null,
                        this.getX(),
                        this.getY(),
                        this.getZ(),
                        SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                        SoundSource.NEUTRAL,
                        1.0F,
                        1.0F
                );
            }

            return InteractionResult.sidedSuccess(
                    this.level().isClientSide
            );
        }

        return super.mobInteract(player, hand);
    }

    public boolean hasBerries() {
        return this.entityData.get(HAS_BERRIES);
    }

    public void setHasBerries(boolean hasBerries) {
        this.entityData.set(HAS_BERRIES, hasBerries);
    }

    private static final EntityDataAccessor<Boolean> FEMALE =
            SynchedEntityData.defineId(
                    MandrakeEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putByte(
                "RootState",
                (byte) getRootState().ordinal()
        );

        if (this.rootPos != null) {
            tag.putLong(
                    "RootPos",
                    this.rootPos.asLong()
            );
        }

        tag.putBoolean(
                "HasBerries",
                this.hasBerries()
        );

        tag.putBoolean("Female", this.isFemale());

        tag.putBoolean(
                "PendingInitialRoot",
                this.pendingInitialRoot
        );

        tag.putInt(
                "ScreamCooldown",
                this.screamCooldown
        );
    }

    public boolean isFemale() {
        return this.entityData.get(FEMALE);
    }

    public boolean isMale() {
        return !this.entityData.get(FEMALE);
    }

    public void setFemale(boolean female) {
        this.entityData.set(FEMALE, female);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains("PendingInitialRoot")) {
            this.pendingInitialRoot =
                    tag.getBoolean("PendingInitialRoot");
        }

        if (tag.contains("Female")) {
            this.setFemale(tag.getBoolean("Female"));
        }

        if (tag.contains("RootState")) {
            int stateIndex = tag.getByte("RootState");

            RootState[] states = RootState.values();

            if (stateIndex >= 0 && stateIndex < states.length) {
                setRootState(states[stateIndex]);
            }
        }

        if (tag.contains("RootPos")) {
            this.rootPos = BlockPos.of(
                    tag.getLong("RootPos")
            );
        } else {
            this.rootPos = null;
        }
        if (this.rootPos != null) {
            this.upperRootBlockWasPresent =
                    !this.level().getBlockState(this.rootPos.above()).isAir();

            this.lowerRootBlockWasPresent =
                    !this.level().getBlockState(this.rootPos).isAir();
        }
        if (tag.contains("HasBerries")) {
            this.setHasBerries(
                    tag.getBoolean("HasBerries")
            );
        }

        if (tag.contains("ScreamCooldown")) {
            this.screamCooldown =
                    tag.getInt("ScreamCooldown");
        }

        applyRootState();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();

        this.entityData.define(
                ROOT_STATE,
                (byte) RootState.UPROOTED.ordinal()
        );

        this.entityData.define(
                HAS_BERRIES,
                true
        );

        this.entityData.define(
                FEMALE,
                false
        );

        this.entityData.define(
                SCREAM_STATE,
                (byte) ScreamState.NONE.ordinal()
        );
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);

        if (ROOT_STATE.equals(dataAccessor)) {
            this.refreshDimensions();
        }
    }

    private static final EntityDataAccessor<Byte> ROOT_STATE =
            SynchedEntityData.defineId(
                    MandrakeEntity.class,
                    EntityDataSerializers.BYTE
            );
    private static final EntityDataAccessor<Boolean> HAS_BERRIES =
            SynchedEntityData.defineId(
                    MandrakeEntity.class,
                    EntityDataSerializers.BOOLEAN
            );

    private static final EntityDataAccessor<Byte> SCREAM_STATE =
            SynchedEntityData.defineId(
                    MandrakeEntity.class,
                    EntityDataSerializers.BYTE
            );

    public enum ScreamState {
        NONE,
        ROOTED,
        UPROOTED
    }

    public ScreamState getScreamState() {
        int index = this.entityData.get(SCREAM_STATE);

        ScreamState[] states = ScreamState.values();

        if (index < 0 || index >= states.length) {
            return ScreamState.NONE;
        }

        return states[index];
    }

    public void setScreamState(ScreamState state) {
        this.entityData.set(
                SCREAM_STATE,
                (byte) state.ordinal()
        );
    }

    public boolean isScreaming() {
        return getScreamState() != ScreamState.NONE;
    }

    private void rootAtSpawnPosition() {
        BlockPos surfacePos = this.blockPosition();

        BlockPos upperSoilPos = surfacePos.below();
        BlockPos lowerSoilPos = surfacePos.below(2);

        if (!isValidMandrakeSoil(this.level().getBlockState(upperSoilPos))
                || !isValidMandrakeSoil(this.level().getBlockState(lowerSoilPos))) {

            this.pendingInitialRoot = false;
            return;
        }

        this.setRootPos(lowerSoilPos);
        this.setRootState(RootState.FULLY_ROOTED);

        this.setPos(
                lowerSoilPos.getX() + 0.5D,
                lowerSoilPos.getY() + 0.05D,
                lowerSoilPos.getZ() + 0.5D
        );

        this.pendingInitialRoot = false;
    }

    public enum RootState {
        FULLY_ROOTED,
        PARTIALLY_EXPOSED,
        UPROOTED
    }

    public RootState getRootState() {
        int index = this.entityData.get(ROOT_STATE);

        RootState[] states = RootState.values();

        if (index < 0 || index >= states.length) {
            return RootState.UPROOTED;
        }

        return states[index];
    }

    public void setRootState(RootState state) {
        this.entityData.set(
                ROOT_STATE,
                (byte) state.ordinal()
        );

        applyRootState();

        /*
         * Rooted and uprooted Mandrakes use different hitbox heights.
         */
        this.refreshDimensions();
    }

    public boolean isRooted() {
        return getRootState() != RootState.UPROOTED;
    }

    public boolean isFullyRooted() {
        return getRootState() == RootState.FULLY_ROOTED;
    }

    public boolean isPartiallyExposed() {
        return getRootState() == RootState.PARTIALLY_EXPOSED;
    }

    public boolean isUprooted() {
        return getRootState() == RootState.UPROOTED;
    }

    @Nullable
    public BlockPos getRootPos() {
        return this.rootPos;
    }

    public void setRootPos(@Nullable BlockPos rootPos) {
        this.rootPos = rootPos;
    }

    private void applyRootState() {
        if (isRooted()) {
            this.setNoGravity(true);
            this.noPhysics = true;

            /*
             * A rooted Mandrake occupies its soil column visually and
             * interactably, but must not prevent the player from replacing
             * blocks in its two root slots.
             */
            this.blocksBuilding = false;

            this.getNavigation().stop();
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);

            this.setYHeadRot(this.getYRot());
            this.setYBodyRot(this.getYRot());
        } else {
            this.setNoGravity(false);
            this.noPhysics = false;

            /*
             * Once walking normally again, restore normal entity
             * obstruction behavior.
             */
            this.blocksBuilding = true;
        }
    }

    private void updateRootStateFromBlocks() {
        if (this.rootPos == null) {
            return;
        }

        BlockState lowerBlockState =
                this.level().getBlockState(this.rootPos);

        BlockState upperBlockState =
                this.level().getBlockState(this.rootPos.above());

        boolean lowerBlockPresent =
                !lowerBlockState.isAir();

        boolean upperBlockPresent =
                !upperBlockState.isAir();

        /*
         * Detect a block being restored to either of the Mandrake's
         * two rooted positions.
         */
        if (!this.upperRootBlockWasPresent && upperBlockPresent) {
            playRootBlockPlacementSound(
                    this.rootPos.above(),
                    upperBlockState
            );
        }

        if (!this.lowerRootBlockWasPresent && lowerBlockPresent) {
            playRootBlockPlacementSound(
                    this.rootPos,
                    lowerBlockState
            );
        }

        /*
         * Remember the current block state for the next check.
         */
        this.upperRootBlockWasPresent = upperBlockPresent;
        this.lowerRootBlockWasPresent = lowerBlockPresent;

        /*
         * Both spaces uncovered = actually uprooted.
         */
        if (!lowerBlockPresent && !upperBlockPresent) {
            uproot();
            return;
        }

        /*
         * Both positions contain blocks = fully buried.
         */
        if (lowerBlockPresent && upperBlockPresent) {
            if (!isFullyRooted()) {
                setRootState(RootState.FULLY_ROOTED);
            }

            return;
        }

        /*
         * Exactly one block is missing.
         */
        if (!isPartiallyExposed()) {
            setRootState(RootState.PARTIALLY_EXPOSED);
        }
    }

    private void playRootBlockPlacementSound(
            BlockPos pos,
            BlockState state
    ) {
        this.level().playSound(
                null,
                pos,
                state.getSoundType().getPlaceSound(),
                SoundSource.BLOCKS,
                (state.getSoundType().getVolume() + 1.0F) / 2.0F,
                state.getSoundType().getPitch() * 0.8F
        );
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (isRooted()) {
            EntityDimensions normal = super.getDimensions(pose);

            return EntityDimensions.scalable(
                    normal.width,
                    2.25F
            );
        }

        return super.getDimensions(pose);
    }

    @Override
    public void knockback(double strength, double x, double z) {
        if (this.isRooted()) {
            return;
        }

        super.knockback(strength, x, z);
    }

    @Override
    public boolean isPushable() {
        return !this.isRooted() && super.isPushable();
    }

    private void uproot() {
        if (isUprooted()) {
            return;
        }

        /*
         * Remember the original root block before clearing rootPos.
         * The scream originates from here.
         */
        BlockPos oldRootPos = this.rootPos;

        setRootState(RootState.UPROOTED);

        this.rootPos = null;

        if (!this.level().isClientSide) {

            /*
             * Real scream is available.
             */
            if (this.screamCooldown <= 0) {

                this.setScreamState(
                        ScreamState.UPROOTED
                );

                this.screamAnimationTicks = 50;

                this.playRandomMandrakeScream();

                /*
                 * Apply damage around the block from which the
                 * Mandrake was just uprooted.
                 */
                if (oldRootPos != null) {
                    this.applyMandrakeScreamDamage(
                            oldRootPos
                    );
                }

                /*
                 * Half a Minecraft day.
                 */
                this.screamCooldown = 12000;
            }

            /*
             * It wanted to scream, but its real scream is on cooldown.
             */
            else {
                this.playMandrakeUpsetSound();
            }
        }

        /*
         * Wait 30–60 seconds before searching for a new rooting site.
         */
        this.rootingCooldown =
                600 + this.random.nextInt(601);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {

            /*
             * Mandrakes created during chunk generation root themselves
             * once normal server ticking has begun.
             */
            if (this.pendingInitialRoot) {
                rootAtSpawnPosition();
            }

            /*
             * Count down until an uprooted Mandrake is ready
             * to search for a new rooting location.
             */
            if (this.isUprooted() && this.rootingCooldown > 0) {
                this.rootingCooldown--;
            }

            if (this.screamCooldown > 0) {
                this.screamCooldown--;
            }

            if (this.upsetSoundCooldown > 0) {
                this.upsetSoundCooldown--;
            }

            if (this.screamAnimationTicks > 0) {
                this.screamAnimationTicks--;

                if (this.screamAnimationTicks == 0) {
                    this.setScreamState(
                            ScreamState.NONE
                    );
                }
            }

            /*
             * Existing rooted behavior.
             */
            if (this.isRooted()) {
                this.getNavigation().stop();
                this.setDeltaMovement(0.0D, 0.0D, 0.0D);

                if (this.rootPos != null) {
                    double rootX = this.rootPos.getX() + 0.5D;
                    double rootY = this.rootPos.getY() + 0.05D;
                    double rootZ = this.rootPos.getZ() + 0.5D;

                    if (this.distanceToSqr(rootX, rootY, rootZ) > 0.001D) {
                        this.setPos(rootX, rootY, rootZ);
                    }
                }

                if (this.tickCount % 5 == 0) {
                    updateRootStateFromBlocks();
                }
            }
        }
    }
}