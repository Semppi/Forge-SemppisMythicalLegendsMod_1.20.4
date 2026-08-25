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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;
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

    private static final EntityDataAccessor<Integer> GRABBED_VICTIM_ID =
            SynchedEntityData.defineId(
                    ColossalLobsterEntity.class,
                    EntityDataSerializers.INT
            );

    public ColossalLobsterEntity(EntityType<? extends WaterAnimal> entityType, Level level) {
        super(entityType, level);
        this.setMaxUpStep(2.0F);
        this.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, Integer.MAX_VALUE, 0, false, false));
    }

    @Override
    protected void defineSynchedData() {

        super.defineSynchedData();

        /*
         * -1 means the lobster is not holding anything.
         */
        this.entityData.define(
                GRABBED_VICTIM_ID,
                -1
        );
    }

    @Override
    protected boolean canRide(Entity vehicle) {
        if (vehicle instanceof Boat) {
            return false;
        }

        return super.canRide(vehicle);
    }

    @Override
    public void tick() {

        super.tick();

        /*
         * ------------------------------------------------
         * CLIENT-SIDE VISUAL GRAB
         * ------------------------------------------------
         *
         * Do NOT use teleport packets every tick.
         *
         * Instead, the client knows which entity the lobster
         * is holding and directly keeps its local copy at the
         * claw position.
         */
        if (this.level().isClientSide) {

            int grabbedId =
                    this.entityData.get(
                            GRABBED_VICTIM_ID
                    );

            if (grabbedId != -1) {

                Entity clientVictim =
                        this.level().getEntity(
                                grabbedId
                        );

                if (clientVictim != null) {

                    Vec3 holdPosition =
                            this.getGrabHoldPosition();

                    /*
                     * Move the visual/client copy directly.
                     *
                     * Most importantly, we do NOT touch YRot or XRot.
                     * The player remains free to look around and fight.
                     */
                    clientVictim.setPos(
                            holdPosition.x,
                            holdPosition.y,
                            holdPosition.z
                    );

                    /*
                     * Match the lobster's movement so interpolation
                     * does not try to pull the victim elsewhere.
                     */
                    clientVictim.setDeltaMovement(
                            this.getDeltaMovement()
                    );

                    clientVictim.fallDistance = 0.0F;
                }
            }

            return;
        }

        /*
         * Update land exposure for EVERY lobster,
         * not just grabbed ones.
         */
        if (this.isSafelyInWater()) {
            this.dryTicks = 0;
        } else {
            this.dryTicks++;

            if (this.dryTicks > 12000) {
                this.dryTicks = 12000;
            }
        }

        /*
         * Existing backup victim cleanup.
         */
        if (this.backupBoatVictim != null
                && this.tickCount > this.backupVictimExpireTick) {

            this.backupBoatVictim = null;
            this.backupVictimExpireTick = 0;
        }

        if (ColossalLobsterEntity.this.dryTicks
                >= WATER_URGENT_TICKS) {

            /*
             * At four minutes dry, survival outranks hunting.
             */
            ColossalLobsterEntity.this.setTarget(null);
        }

        /*
         * ------------------------------------------------
         * SERVER-SIDE GRAB
         * ------------------------------------------------
         */
        if (!this.isGrabbingVictim) {
            return;
        }

        /*
         * Validate the actual server-side victim.
         */
        if (this.grabbedVictim == null
                || !this.grabbedVictim.isAlive()
                || this.grabbedVictim.isRemoved()) {

            /*
             * Remember whether player 2 from the destroyed
             * boat is still eligible.
             */
            Player backup =
                    this.backupBoatVictim;

            boolean canRetrieveBackup =
                    backup != null
                            && backup.isAlive()
                            && !backup.isRemoved()
                            && this.tickCount
                            <= this.backupVictimExpireTick;

            this.releaseGrabbedVictim();

            if (canRetrieveBackup) {

                /*
                 * Player 1 died quickly enough.
                 *
                 * The lobster now returns to the seabed and gets
                 * seven seconds to retrieve player 2.
                 */
                this.backupBoatVictim = null;
                this.backupVictimExpireTick = 0;

                this.preparePlayerRetrieval(
                        backup
                );

            } else {

                this.backupBoatVictim = null;
                this.backupVictimExpireTick = 0;
            }

            return;
        }

        /*
         * Victim remains absolute prey priority.
         */
        this.setTarget(
                this.grabbedVictim
        );

        /*
         * Don't wander after other prey.
         */
        this.getNavigation().stop();

        /*
         * Calculate the same claw position used by the client.
         */
        Vec3 holdPosition =
                this.getGrabHoldPosition();

        /*
         * Server maintains the authoritative gameplay position.
         *
         * IMPORTANT:
         * This is setPos(), NOT connection.teleport().
         */
        this.grabbedVictim.setPos(
                holdPosition.x,
                holdPosition.y,
                holdPosition.z
        );

        /*
         * Carry the victim with the lobster's velocity.
         */
        this.grabbedVictim.setDeltaMovement(
                this.getDeltaMovement()
        );

        this.grabbedVictim.fallDistance =
                0.0F;

        /*
         * ------------------------------------------------
         * SEABED ATTACK
         * ------------------------------------------------
         */
        if (this.isNearSeabed()) {

            this.grabbedAttackTicks++;

            if (this.grabbedAttackTicks >= 30) {

                this.grabbedAttackTicks = 0;

                this.doHurtTarget(
                        this.grabbedVictim
                );
            }

        } else {

            this.grabbedAttackTicks = 0;
        }
    }

    private boolean grabVictim(LivingEntity victim) {

        if (victim == null || !victim.isAlive()) {
            return false;
        }

        if (this.isGrabbingVictim) {
            return false;
        }

        this.grabbedVictim = victim;
        this.isGrabbingVictim = true;
        this.grabbedAttackTicks = 0;

        /*
         * Synchronize the held entity to clients.
         */
        this.entityData.set(
                GRABBED_VICTIM_ID,
                victim.getId()
        );

        /*
         * The player may still technically be riding the
         * destroyed boat at this instant.
         */
        victim.stopRiding();

        return true;
    }

    public static boolean checkWaterMobSpawnRules(EntityType<ColossalLobsterEntity> entityType, LevelAccessor levelAccessor, MobSpawnType spawnType, BlockPos pos, RandomSource randomSource) {
        return levelAccessor.getBlockState(pos).is(Blocks.WATER) &&
                levelAccessor.getBlockState(pos.below()).isFaceSturdy(levelAccessor, pos.below(), Direction.UP);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 130D)
                .add(Attributes.ATTACK_DAMAGE, 10.0f)
                .add(Attributes.ATTACK_SPEED, 0.4f)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.ARMOR_TOUGHNESS, 0.30D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.60D)
                .build();
    }

    @Override
    public int getMaxAirSupply() {
        /*
         * Colossal lobsters can survive out of water
         * for approximately five minutes.
         *
         * 5 minutes * 60 seconds * 20 ticks = 6000.
         */
        return 6000;
    }

    @Override
    protected void handleAirSupply(int currentAir) {

        /*
         * While alive and out of water, consume the lobster's
         * stored moisture/air reserve.
         */
        if (this.isAlive() && !this.isInWaterOrBubble()) {

            this.setAirSupply(currentAir - 1);

            /*
             * Once the reserve is exhausted, use the same
             * repeating drowning rhythm as WaterAnimal.
             */
            if (this.getAirSupply() == -20) {

                this.setAirSupply(0);

                this.hurt(
                        this.damageSources().drown(),
                        2.0F
                );
            }

        } else {

            /*
             * Any proper water contact completely refreshes
             * the colossal lobster's five-minute reserve.
             */
            this.setAirSupply(
                    this.getMaxAirSupply()
            );
        }
    }

    @Override
    public void die(DamageSource damageSource) {

        this.releaseGrabbedVictim();

        super.die(damageSource);
    }

    private Vec3 getGrabHoldPosition() {

        /*
         * Starting values taken from the Blockbench location:
         *
         * X = 0
         * Y = 18
         * Z = -56
         *
         * Converted from model units to blocks:
         *
         * X = 0.0
         * Y = 1.125
         * Z = 3.5 forward
         */
        double forwardOffset = 3.5D;
        double sideOffset = 0.0D;
        double verticalOffset = 1.125D;

        double yawRadians =
                Math.toRadians(this.getYRot());

        /*
         * Minecraft-facing forward vector.
         */
        double forwardX =
                -Math.sin(yawRadians);

        double forwardZ =
                Math.cos(yawRadians);

        /*
         * Right-side vector.
         */
        double sideX =
                Math.cos(yawRadians);

        double sideZ =
                Math.sin(yawRadians);

        double holdX =
                this.getX()
                        + forwardX * forwardOffset
                        + sideX * sideOffset;

        double holdY =
                this.getY()
                        + verticalOffset;

        double holdZ =
                this.getZ()
                        + forwardZ * forwardOffset
                        + sideZ * sideOffset;

        return new Vec3(
                holdX,
                holdY,
                holdZ
        );
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isEffectiveAi() && this.isInWater()) {

            /*
             * LARGE BOAT LEAP
             */
            if (this.movementMode == LobsterMovementMode.BOAT_LEAP) {

                this.move(
                        MoverType.SELF,
                        this.getDeltaMovement()
                );

                Vec3 velocity = this.getDeltaMovement();

                this.setDeltaMovement(
                        velocity.x * 0.99D,
                        velocity.y * 0.99D - 0.02D,
                        velocity.z * 0.99D
                );

                return;
            }

            /*
             * LARGE PLAYER RETRIEVAL LEAP
             *
             * Uses the same basic ballistic movement as the boat leap.
             */
            if (this.movementMode == LobsterMovementMode.PLAYER_RETRIEVAL_LEAP) {

                this.move(
                        MoverType.SELF,
                        this.getDeltaMovement()
                );

                Vec3 velocity =
                        this.getDeltaMovement();

                this.setDeltaMovement(
                        velocity.x * 0.99D,
                        velocity.y * 0.99D - 0.02D,
                        velocity.z * 0.99D
                );

                return;
            }

            /*
             * SHORT PURSUIT BURST AFTER A MISSED BOAT LEAP
             *
             * This is one violent tail-powered correction.
             * It preserves momentum briefly, but is not sustained swimming.
             */
            if (this.movementMode == LobsterMovementMode.PURSUIT_BURST) {

                this.move(
                        MoverType.SELF,
                        this.getDeltaMovement()
                );

                Vec3 velocity =
                        this.getDeltaMovement();

                double horizontalRetention =
                        0.97D;

                /*
                 * Gradually arc downward.
                 */
                double newY =
                        velocity.y - 0.025D;

                /*
                 * Recovery burst is NEVER allowed
                 * to produce upward movement.
                 */
                newY =
                        Math.min(newY, 0.0D);

                newY =
                        Math.max(newY, -0.28D);

                this.setDeltaMovement(
                        velocity.x * horizontalRetention,
                        newY,
                        velocity.z * horizontalRetention
                );

                return;
            }

            /*
             * SMALL PREY LEAP
             */
            if (this.movementMode == LobsterMovementMode.LEAP) {

                this.move(
                        MoverType.SELF,
                        this.getDeltaMovement()
                );

                Vec3 velocity = this.getDeltaMovement();

                this.setDeltaMovement(
                        velocity.x * 0.96D,
                        velocity.y * 0.92D - 0.03D,
                        velocity.z * 0.96D
                );

                return;
            }

            /*
             * NORMAL SEABED MOVEMENT
             */
            float movementSpeed =
                    (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED);

            this.moveRelative(
                    movementSpeed * 0.22F,
                    travelVector
            );

            this.move(
                    MoverType.SELF,
                    this.getDeltaMovement()
            );

            Vec3 velocity = this.getDeltaMovement();

            double horizontalDrag = 0.91D;

            double newX = velocity.x * horizontalDrag;
            double newY = velocity.y;
            double newZ = velocity.z * horizontalDrag;

            /*
             * BOTTOM_WALK and SINK both naturally descend when unsupported.
             */
            if (!this.onGround()
                    && this.movementMode != LobsterMovementMode.PURSUIT_BURST
                    && this.movementMode != LobsterMovementMode.ESCAPE_BURST) {

                newY -= 0.08D;

                if (newY < -0.35D) {
                    newY = -0.35D;
                }
            }

            this.setDeltaMovement(
                    newX,
                    newY,
                    newZ
            );

            /*
             * Once a sinking lobster reaches the seabed,
             * return to ordinary walking mode.
             */
            if (this.movementMode == LobsterMovementMode.SINK) {
                boolean nearGround =
                        this.onGround()
                                || this.level()
                                .getBlockState(this.blockPosition().below())
                                .isFaceSturdy(
                                        this.level(),
                                        this.blockPosition().below(),
                                        Direction.UP
                                );

                if (nearGround) {

                    if (this.isGrabbingVictim) {

                        this.movementMode =
                                LobsterMovementMode.GRABBING;

                        this.getNavigation().stop();

                        Vec3 currentVelocity =
                                this.getDeltaMovement();

                        this.setDeltaMovement(
                                currentVelocity.x * 0.2D,
                                currentVelocity.y,
                                currentVelocity.z * 0.2D
                        );

                    } else if (this.retrievalPending) {

                        /*
                         * First arrival at the seabed after the boat broke.
                         */
                        this.beginPlayerRetrievalWindow();

                    } else if (this.retrievalActive) {

                        /*
                         * Returned to the seabed after one failed player leap.
                         *
                         * Keep the existing seven-second timer running and
                         * allow PlayerRetrievalGoal to make another attempt.
                         */
                        this.movementMode =
                                LobsterMovementMode.BOTTOM_WALK;

                        this.getNavigation().stop();

                    } else {

                        this.movementMode =
                                LobsterMovementMode.BOTTOM_WALK;
                    }
                }
            }

        } else {
            super.travel(travelVector);
        }
    }

    private enum LobsterMovementMode {
        BOTTOM_WALK,
        LEAP,
        BOAT_LEAP,
        PLAYER_RETRIEVAL_LEAP,
        PURSUIT_BURST,
        ESCAPE_BURST,
        SINK,
        GRABBING
    }

    private enum BoatAmbushPhase {
        WATCHING,
        APPROACHING,
        AIMING,
        LEAPING,
        BURST_AIMING,
        BURSTING
    }

    private boolean isSafelyInWater() {

        BlockPos bodyPos =
                this.blockPosition();

        /*
         * Require water around the body and one block above.
         *
         * This prevents a giant lobster from considering a
         * one-block-deep shoreline or puddle a satisfactory
         * return to the ocean.
         */
        return this.level()
                .getBlockState(bodyPos)
                .is(Blocks.WATER)
                && this.level()
                .getBlockState(bodyPos.above())
                .is(Blocks.WATER);
    }

    private LobsterMovementMode movementMode =
            LobsterMovementMode.BOTTOM_WALK;

    private int nextSmallLeapTick = 0;
    /*
     * Tracks how long the lobster has been meaningfully
     * out of water.
     *
     * This is separate from Minecraft's air supply so the
     * AI behavior can have its own thresholds.
     */
    private int dryTicks = 0;

    private static final int WATER_SEEK_START_TICKS = 3600;   // 3 minutes
    private static final int WATER_URGENT_TICKS = 4800;       // 4 minutes

    private LivingEntity grabbedVictim = null;
    private boolean isGrabbingVictim = false;
    private int grabbedAttackTicks = 0;
    /*
     * Post-boat player retrieval state.
     */
    private Player retrievalTarget = null;

    private boolean retrievalPending = false;
    private boolean retrievalActive = false;

    private int retrievalEndTick = 0;
    private int nextRetrievalLeapTick = 0;

    /*
     * If another player was aboard the destroyed boat,
     * remember one backup victim for a short time.
     */
    private Player backupBoatVictim = null;
    private int backupVictimExpireTick = 0;

    private void releaseGrabbedVictim() {

        this.grabbedVictim = null;
        this.isGrabbingVictim = false;
        this.grabbedAttackTicks = 0;

        /*
         * Tell clients that nothing is being held anymore.
         */
        this.entityData.set(
                GRABBED_VICTIM_ID,
                -1
        );

        if (this.movementMode == LobsterMovementMode.GRABBING) {

            this.movementMode =
                    LobsterMovementMode.BOTTOM_WALK;
        }
    }

    private void preparePlayerRetrieval(Player player) {

        if (player == null || !player.isAlive()) {
            return;
        }

        this.retrievalTarget = player;

        /*
         * Pending means:
         * "I want this player, but first I must get back
         * to the seabed."
         */
        this.retrievalPending = true;
        this.retrievalActive = false;

        this.getNavigation().stop();

        /*
         * Return to the bottom before beginning the
         * seven-second attack window.
         */
        this.movementMode =
                LobsterMovementMode.SINK;
    }

    private void beginPlayerRetrievalWindow() {

        if (this.retrievalTarget == null
                || !this.retrievalTarget.isAlive()) {

            this.endPlayerRetrieval();
            return;
        }

        this.retrievalPending = false;
        this.retrievalActive = true;

        /*
         * Seven seconds = 140 game ticks.
         */
        this.retrievalEndTick =
                this.tickCount + 140;

        /*
         * Permit the first leap immediately.
         */
        this.nextRetrievalLeapTick =
                this.tickCount;

        this.movementMode =
                LobsterMovementMode.BOTTOM_WALK;
    }

    private void endPlayerRetrieval() {

        this.retrievalTarget = null;

        this.retrievalPending = false;
        this.retrievalActive = false;

        this.retrievalEndTick = 0;
        this.nextRetrievalLeapTick = 0;

        if (!this.isGrabbingVictim) {
            this.movementMode =
                    LobsterMovementMode.BOTTOM_WALK;
        }
    }

    private boolean isNearSeabed() {

        BlockPos below =
                this.blockPosition().below();

        return this.onGround()
                || this.level()
                .getBlockState(below)
                .isFaceSturdy(
                        this.level(),
                        below,
                        Direction.UP
                );
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.setPathfindingMalus(BlockPathTypes.WATER, 0.0F);

        /*
         * Survival:
         * if stranded on land, find nearby water.
         */
        this.goalSelector.addGoal(
                0,
                new LobsterReturnToWaterGoal()
        );

        /*
         * Special combat behavior.
         */
        this.goalSelector.addGoal(1, new PlayerRetrievalGoal());
        this.goalSelector.addGoal(2, new BoatStalkGoal());
        this.goalSelector.addGoal(3, new SmallPreyLeapGoal());

        /*
         * Ordinary movement/combat.
         */
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractFish.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Squid.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Drowned.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Dolphin.class, true));
    }

    private class LobsterReturnToWaterGoal extends Goal {

        /*
         * How far around itself the lobster searches
         * for a route back to water.
         */
        private static final int HORIZONTAL_SEARCH_RANGE = 24;

        /*
         * We don't need to search wildly above/below.
         * This is mainly for beaches, slopes and shorelines.
         */
        private static final int VERTICAL_SEARCH_RANGE = 8;

        private static final double RETURN_SPEED = 1.05D;
        private static final double URGENT_RETURN_SPEED = 1.35D;

        private BlockPos waterTarget = null;

        private int recalculateTicks = 0;

        public LobsterReturnToWaterGoal() {

            this.setFlags(
                    EnumSet.of(
                            Goal.Flag.MOVE,
                            Goal.Flag.LOOK
                    )
            );
        }

        @Override
        public boolean canUse() {

            /*
             * Properly back in usable water.
             */
            if (ColossalLobsterEntity.this.isSafelyInWater()) {
                return false;
            }

            /*
             * For the first three minutes on land, there is
             * no emergency.
             *
             * The colossal lobster may wander, fight, cross
             * beaches, etc. normally.
             */
            if (ColossalLobsterEntity.this.dryTicks
                    < WATER_SEEK_START_TICKS) {

                return false;
            }

            /*
             * Don't interfere with the specialized
             * grab/boat-retrieval sequence.
             */
            if (ColossalLobsterEntity.this.isGrabbingVictim
                    || ColossalLobsterEntity.this.retrievalPending
                    || ColossalLobsterEntity.this.retrievalActive) {

                return false;
            }

            /*
             * Existing water search.
             */
            this.waterTarget =
                    findNearestReachableWater();

            return this.waterTarget != null;
        }

        @Override
        public boolean canContinueToUse() {

            /*
             * Keep going until the lobster is genuinely
             * immersed, not merely standing ankle-deep.
             */
            if (ColossalLobsterEntity.this.isSafelyInWater()) {
                return false;
            }

            if (this.waterTarget == null) {
                return false;
            }

            return !ColossalLobsterEntity.this.isGrabbingVictim
                    && !ColossalLobsterEntity.this.retrievalPending
                    && !ColossalLobsterEntity.this.retrievalActive;
        }

        @Override
        public void start() {

            this.recalculateTicks = 0;

            moveTowardWater();
        }

        @Override
        public void tick() {

            if (this.waterTarget == null) {
                return;
            }

            /*
             * At four minutes dry, survival outranks hunting.
             *
             * THIS is the line I previously meant when I said
             * to put the urgent behavior inside the return goal's tick().
             */
            if (ColossalLobsterEntity.this.dryTicks
                    >= WATER_URGENT_TICKS) {

                ColossalLobsterEntity.this
                        .setTarget(null);
            }

            /*
             * Face toward the chosen water destination.
             */
            ColossalLobsterEntity.this
                    .getLookControl()
                    .setLookAt(
                            this.waterTarget.getX() + 0.5D,
                            this.waterTarget.getY() + 0.5D,
                            this.waterTarget.getZ() + 0.5D,
                            30.0F,
                            30.0F
                    );

            this.recalculateTicks++;

            /*
             * Recalculate once per second or if the old path finished.
             */
            if (this.recalculateTicks >= 20
                    || ColossalLobsterEntity.this
                    .getNavigation()
                    .isDone()) {

                this.recalculateTicks = 0;

                BlockPos betterTarget =
                        findNearestReachableWater();

                if (betterTarget != null) {
                    this.waterTarget =
                            betterTarget;
                }

                moveTowardWater();
            }
        }

        private void moveTowardWater() {

            if (this.waterTarget == null) {
                return;
            }

            double speed =
                    ColossalLobsterEntity.this.dryTicks
                            >= WATER_URGENT_TICKS
                            ? URGENT_RETURN_SPEED
                            : RETURN_SPEED;

            ColossalLobsterEntity.this
                    .getNavigation()
                    .moveTo(
                            this.waterTarget.getX() + 0.5D,
                            this.waterTarget.getY(),
                            this.waterTarget.getZ() + 0.5D,
                            speed
                    );
        }

        @Override
        public void stop() {

            this.waterTarget = null;
            this.recalculateTicks = 0;

            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();
        }

        private BlockPos findNearestReachableWater() {

            BlockPos origin =
                    ColossalLobsterEntity.this.blockPosition();

            BlockPos bestPos = null;

            double bestDistanceSqr =
                    Double.MAX_VALUE;

            /*
             * Search outward around the lobster.
             *
             * We prefer the nearest usable water block.
             */
            for (int x = -HORIZONTAL_SEARCH_RANGE;
                 x <= HORIZONTAL_SEARCH_RANGE;
                 x++) {

                for (int z = -HORIZONTAL_SEARCH_RANGE;
                     z <= HORIZONTAL_SEARCH_RANGE;
                     z++) {

                    /*
                     * Skip positions outside the circular
                     * horizontal search radius.
                     */
                    if (x * x + z * z
                            > HORIZONTAL_SEARCH_RANGE
                            * HORIZONTAL_SEARCH_RANGE) {

                        continue;
                    }

                    for (int y = -VERTICAL_SEARCH_RANGE;
                         y <= VERTICAL_SEARCH_RANGE;
                         y++) {

                        BlockPos candidate =
                                origin.offset(
                                        x,
                                        y,
                                        z
                                );

                        /*
                         * We want an actual water block.
                         */
                        if (!ColossalLobsterEntity.this
                                .level()
                                .getBlockState(candidate)
                                .is(Blocks.WATER)) {

                            continue;
                        }

                        /*
                         * Prefer water with water above it as well.
                         *
                         * This prevents the lobster from considering
                         * every tiny one-block puddle a satisfactory
                         * ocean refuge.
                         */
                        boolean usefulDepth =
                                ColossalLobsterEntity.this
                                        .level()
                                        .getBlockState(candidate.above())
                                        .is(Blocks.WATER);

                        if (!usefulDepth) {
                            continue;
                        }

                        double distanceSqr =
                                origin.distSqr(candidate);

                        if (distanceSqr < bestDistanceSqr) {

                            bestDistanceSqr =
                                    distanceSqr;

                            bestPos =
                                    candidate;
                        }
                    }
                }
            }

            return bestPos;
        }
    }

    private class BoatStalkGoal extends Goal {

        /*
         * The lobster notices boats from very far away.
         */
        private static final double BOAT_SENSE_RANGE = 64.0D;

        /*
         * Once the boat gets this close horizontally,
         * begin moving toward it along the seabed.
         */
        private static final double BOAT_APPROACH_RANGE = 32.0D;

        /*
         * Once horizontally this close, stop moving
         * and begin preparing the large leap.
         */
        private static final double BOAT_AIM_RANGE = 16.0D;

        private static final double APPROACH_SPEED = 1.0D;

        private Boat targetBoat;

        private BoatAmbushPhase phase =
                BoatAmbushPhase.WATCHING;

        private Vec3 predictedBoatPosition =
                Vec3.ZERO;

        private int aimTicks = 0;
        private int boatLeapTicks = 0;
        private int pursuitBurstTicks = 0;
        private int burstAimTicks = 0;

        private boolean usedPursuitBurst = false;

        private Vec3 lastBoatPosition = Vec3.ZERO;
        private Vec3 measuredBoatVelocity = Vec3.ZERO;
        private boolean hasLastBoatPosition = false;

        /*
         * Remember who was recently aboard the boat.
         *
         * This prevents the lobster from forgetting its intended victim
         * if the player is ejected or dismounts during the exact tick
         * that the boat is struck.
         */
        private Player rememberedBoatVictim = null;
        private Player rememberedSecondBoatVictim = null;

        private int rememberedBoatVictimTick = 0;

        private void rememberBoatPassengers() {

            if (this.targetBoat == null
                    || !this.targetBoat.isAlive()
                    || this.targetBoat.isRemoved()) {
                return;
            }

            Player firstPlayer = null;
            Player secondPlayer = null;

            for (Entity passenger : this.targetBoat.getPassengers()) {

                if (!(passenger instanceof Player player)
                        || !player.isAlive()) {
                    continue;
                }

                if (firstPlayer == null) {
                    firstPlayer = player;
                } else if (secondPlayer == null) {
                    secondPlayer = player;
                    break;
                }
            }

            /*
             * Only overwrite our memory when there really
             * are players aboard.
             *
             * If the passenger disappears immediately before
             * impact, the previous memory survives.
             */
            if (firstPlayer != null) {

                this.rememberedBoatVictim =
                        firstPlayer;

                this.rememberedSecondBoatVictim =
                        secondPlayer;

                this.rememberedBoatVictimTick =
                        ColossalLobsterEntity.this.tickCount;
            }
        }

        private void destroyStruckBoat(Boat struckBoat) {

            Player chosenVictim = null;
            Player secondVictim = null;

            /*
             * First preference:
             * whoever Minecraft says is currently aboard
             * the boat on the exact strike tick.
             */
            for (Entity passenger : struckBoat.getPassengers()) {

                if (!(passenger instanceof Player player)
                        || !player.isAlive()) {
                    continue;
                }

                if (chosenVictim == null) {
                    chosenVictim = player;
                } else if (secondVictim == null) {
                    secondVictim = player;
                    break;
                }
            }

            /*
             * Fallback:
             *
             * The passenger may have been ejected/dismounted during
             * the impact timing. If that happens, use the player that
             * the lobster recently saw riding this boat.
             *
             * 40 ticks = 2 seconds of memory.
             */
            boolean rememberedPassengersAreFresh =
                    ColossalLobsterEntity.this.tickCount
                            - this.rememberedBoatVictimTick
                            <= 40;

            if (chosenVictim == null
                    && rememberedPassengersAreFresh
                    && this.rememberedBoatVictim != null
                    && this.rememberedBoatVictim.isAlive()
                    && !this.rememberedBoatVictim.isRemoved()) {

                chosenVictim =
                        this.rememberedBoatVictim;
            }

            if (secondVictim == null
                    && rememberedPassengersAreFresh
                    && this.rememberedSecondBoatVictim != null
                    && this.rememberedSecondBoatVictim.isAlive()
                    && !this.rememberedSecondBoatVictim.isRemoved()) {

                secondVictim =
                        this.rememberedSecondBoatVictim;
            }

            /*
             * Remember player 2 for ten seconds.
             *
             * We only use this if player 1 was successfully grabbed
             * and then dies shortly afterward.
             */
            if (secondVictim != null) {

                ColossalLobsterEntity.this.backupBoatVictim =
                        secondVictim;

                ColossalLobsterEntity.this.backupVictimExpireTick =
                        ColossalLobsterEntity.this.tickCount + 200;

            } else {

                ColossalLobsterEntity.this.backupBoatVictim =
                        null;

                ColossalLobsterEntity.this.backupVictimExpireTick =
                        0;
            }

            ColossalLobsterEntity.this.level().playSound(
                    null,
                    struckBoat.blockPosition(),
                    SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR,
                    SoundSource.HOSTILE,
                    1.5F,
                    0.9F
            );

            boolean immediateGrabSucceeded = false;

            if (chosenVictim != null) {

                boolean victimInGrabReach =
                        ColossalLobsterEntity.this
                                .getBoundingBox()
                                .inflate(2.0D)
                                .intersects(
                                        chosenVictim.getBoundingBox()
                                );

                if (victimInGrabReach) {

                    immediateGrabSucceeded =
                            ColossalLobsterEntity.this
                                    .grabVictim(chosenVictim);
                }
            }

            /*
             * Remove everyone from the doomed boat.
             */
            struckBoat.ejectPassengers();

            /*
             * Destroy without dropping the boat item.
             */
            struckBoat.discard();

            /*
             * The boat is gone but player 1 escaped the claw.
             *
             * Remember them and return to the seabed.
             * The seven-second retrieval clock begins once the
             * lobster actually reaches the bottom.
             */
            if (!immediateGrabSucceeded
                    && chosenVictim != null) {

                ColossalLobsterEntity.this
                        .preparePlayerRetrieval(chosenVictim);
            }
        }

        private boolean canAttemptBoatPursuitBurst() {

            if (this.targetBoat == null
                    || !this.targetBoat.isAlive()
                    || this.targetBoat.isRemoved()) {
                return false;
            }

            /*
             * Don't perform a ridiculous cross-ocean correction.
             *
             * The large leap should already have brought the lobster
             * fairly close to the boat.
             */
            double xDifference =
                    this.targetBoat.getX()
                            - ColossalLobsterEntity.this.getX();

            double zDifference =
                    this.targetBoat.getZ()
                            - ColossalLobsterEntity.this.getZ();

            double horizontalDistanceSqr =
                    xDifference * xDifference
                            + zDifference * zDifference;

            /*
             * 10-block recovery envelope.
             */
            return horizontalDistanceSqr <= 144.0D;
        }

        private void beginBoatPursuitBurst() {

            if (this.targetBoat == null) {
                return;
            }

            /*
             * This ambush has now spent its ONE recovery burst.
             */
            this.usedPursuitBurst = true;

            this.phase =
                    BoatAmbushPhase.BURSTING;

            ColossalLobsterEntity.this.movementMode =
                    LobsterMovementMode.PURSUIT_BURST;

            this.pursuitBurstTicks = 0;

            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();

            /*
             * Aim at the boat's CURRENT position.
             *
             * Unlike the main ambush leap, this is an emergency
             * close-range correction rather than a long predictive intercept.
             */
            Vec3 lobsterPosition =
                    ColossalLobsterEntity.this.position();

            /*
             * The burst lasts 16 ticks, but we don't want to
             * predict all 16 because the lobster only needs
             * to cross the boat's path during that interval.
             *
             * Lead approximately 6 ticks ahead.
             */
            double burstPredictionTicks =
                    6.0D;

            Vec3 boatPosition =
                    this.targetBoat.position().add(
                            this.measuredBoatVelocity.scale(
                                    burstPredictionTicks
                            )
                    );

            Vec3 horizontalDirection =
                    new Vec3(
                            boatPosition.x - lobsterPosition.x,
                            0.0D,
                            boatPosition.z - lobsterPosition.z
                    );

            if (horizontalDirection.lengthSqr() < 0.001D) {
                ColossalLobsterEntity.this.movementMode =
                        LobsterMovementMode.SINK;

                this.stop();
                return;
            }

            horizontalDirection =
                    horizontalDirection.normalize();

            /*
             * Strong but very brief correction.
             *
             * Horizontal thrust does most of the work.
             * Vertical correction is deliberately limited.
             */
            /*
             * Strong straight-line tail burst.
             *
             * More horizontal than before and intentionally
             * almost no upward component.
             */
            double horizontalStrength =
                    1.05D;

            /*
             * Begin nearly level.
             *
             * The travel() physics will make the lobster sink
             * progressively as the burst continues.
             */
            double initialVerticalVelocity =
                    -0.03D;

            Vec3 burstVelocity =
                    new Vec3(
                            horizontalDirection.x * horizontalStrength,
                            initialVerticalVelocity,
                            horizontalDirection.z * horizontalStrength
                    );

            ColossalLobsterEntity.this
                    .setDeltaMovement(
                            burstVelocity
                    );

            ColossalLobsterEntity.this.hasImpulse =
                    true;
        }

        public BoatStalkGoal() {
            this.setFlags(EnumSet.of(
                    Goal.Flag.MOVE,
                    Goal.Flag.LOOK
            ));
        }

        @Override
        public boolean canUse() {

            /*
             * Player retrieval and an existing grab outrank
             * interest in any other boat.
             */
            if (ColossalLobsterEntity.this.retrievalPending
                    || ColossalLobsterEntity.this.retrievalActive
                    || ColossalLobsterEntity.this.isGrabbingVictim) {

                return false;
            }

            /*
             * Only begin stalking while the lobster
             * is in its normal seabed state.
             */
            if (ColossalLobsterEntity.this.movementMode
                    != LobsterMovementMode.BOTTOM_WALK) {

                return false;
            }

            Boat nearestBoat =
                    findNearestBoat();

            if (nearestBoat == null) {
                return false;
            }

            this.targetBoat =
                    nearestBoat;

            return true;
        }

        @Override
        public boolean canContinueToUse() {

            if (this.targetBoat == null) {
                return false;
            }

            /*
             * Once committed to the leap, let the goal
             * continue until the leap logic ends it.
             */
            if (this.phase == BoatAmbushPhase.LEAPING
                    || this.phase == BoatAmbushPhase.BURST_AIMING
                    || this.phase == BoatAmbushPhase.BURSTING) {
                return true;
            }

            if (!this.targetBoat.isAlive()
                    || this.targetBoat.isRemoved()) {
                return false;
            }

            double senseRangeSqr =
                    BOAT_SENSE_RANGE * BOAT_SENSE_RANGE;

            return ColossalLobsterEntity.this
                    .distanceToSqr(this.targetBoat)
                    <= senseRangeSqr;
        }

        @Override
        public void start() {

            /*
             * The boat takes priority over fish and
             * other ordinary prey.
             */
            ColossalLobsterEntity.this
                    .setTarget(null);

            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();

            this.phase =
                    BoatAmbushPhase.WATCHING;

            this.predictedBoatPosition =
                    this.targetBoat.position();

            this.aimTicks = 0;
            this.boatLeapTicks = 0;
            this.pursuitBurstTicks = 0;
            this.burstAimTicks = 0;
            this.usedPursuitBurst = false;

            this.lastBoatPosition =
                    this.targetBoat.position();

            this.measuredBoatVelocity =
                    Vec3.ZERO;

            this.hasLastBoatPosition =
                    true;
        }

        @Override
        public void tick() {

            if (this.targetBoat == null) {
                return;
            }

            /*
             * Remember the boat's passengers BEFORE doing
             * any strike/intercept processing.
             */
            rememberBoatPassengers();

            /*
             * Measure how far the boat ACTUALLY moved since the
             * previous AI tick.
             */
            Vec3 currentBoatPosition =
                    this.targetBoat.position();

            if (this.hasLastBoatPosition) {

                Vec3 currentMeasuredVelocity =
                        currentBoatPosition.subtract(
                                this.lastBoatPosition
                        );

                /*
                 * Smooth the measurement slightly.
                 *
                 * This prevents tiny tick-to-tick fluctuations from
                 * throwing the predicted intercept around.
                 */
                this.measuredBoatVelocity =
                        this.measuredBoatVelocity
                                .scale(0.65D)
                                .add(
                                        currentMeasuredVelocity
                                                .scale(0.35D)
                                );
            }

            this.lastBoatPosition =
                    currentBoatPosition;

            this.hasLastBoatPosition =
                    true;

            /*
             * ------------------------------------------------
             * LARGE BOAT LEAP IN PROGRESS
             * ------------------------------------------------
             */
            if (this.phase == BoatAmbushPhase.LEAPING) {

                this.boatLeapTicks++;

                ColossalLobsterEntity.this
                        .getLookControl()
                        .setLookAt(
                                this.targetBoat,
                                30.0F,
                                30.0F
                        );

                boolean reachedBoat =
                        ColossalLobsterEntity.this
                                .getBoundingBox()
                                .inflate(1.0D)
                                .intersects(
                                        this.targetBoat.getBoundingBox()
                                );

                if (reachedBoat) {

                    destroyStruckBoat(this.targetBoat);

                    ColossalLobsterEntity.this.movementMode =
                            LobsterMovementMode.SINK;

                    this.stop();
                    return;
                }

                /*
                 * Main leap has clearly missed.
                 */
                /*
                 * The main leap has missed once the lobster
                 * has passed its apex and is clearly descending.
                 */
                if (this.boatLeapTicks > 10
                        && ColossalLobsterEntity.this
                        .getDeltaMovement().y <= -0.05D) {

                    /*
                     * If the lobster is still above the surface,
                     * DON'T freeze or begin the recovery burst yet.
                     *
                     * Let gravity carry it naturally back into the water.
                     */
                    if (!ColossalLobsterEntity.this.isInWater()) {
                        return;
                    }

                    /*
                     * Now that the lobster has re-entered the water,
                     * it may perform its one recovery burst.
                     */
                    if (!this.usedPursuitBurst
                            && canAttemptBoatPursuitBurst()) {

                        this.phase =
                                BoatAmbushPhase.BURST_AIMING;

                        this.burstAimTicks = 0;

                        /*
                         * Kill most of the old leap momentum only NOW,
                         * once the lobster is back in the water.
                         */
                        Vec3 velocity =
                                ColossalLobsterEntity.this
                                        .getDeltaMovement();

                        ColossalLobsterEntity.this
                                .setDeltaMovement(
                                        velocity.x * 0.15D,
                                        Math.min(
                                                velocity.y,
                                                0.0D
                                        ) * 0.15D,
                                        velocity.z * 0.15D
                                );

                    } else {

                        ColossalLobsterEntity.this.movementMode =
                                LobsterMovementMode.SINK;

                        this.stop();
                    }

                    return;
                }

                if (this.boatLeapTicks > 80) {

                    ColossalLobsterEntity.this.movementMode =
                            LobsterMovementMode.SINK;

                    this.stop();
                }

                return;
            }

            /*
             * ------------------------------------------------
             * BURST AIMING / BRACING
             * ------------------------------------------------
             */
            if (this.phase == BoatAmbushPhase.BURST_AIMING) {

                this.burstAimTicks++;

                ColossalLobsterEntity.this
                        .getNavigation()
                        .stop();

                ColossalLobsterEntity.this
                        .getLookControl()
                        .setLookAt(
                                this.targetBoat,
                                30.0F,
                                30.0F
                        );

                Vec3 velocity =
                        ColossalLobsterEntity.this
                                .getDeltaMovement();

                ColossalLobsterEntity.this
                        .setDeltaMovement(
                                velocity.x * 0.20D,
                                Math.max(
                                        Math.min(
                                                velocity.y,
                                                0.0D
                                        ) * 0.20D - 0.01D,
                                        -0.08D
                                ),
                                velocity.z * 0.20D
                        );

                if (this.burstAimTicks >= 4) {
                    beginBoatPursuitBurst();
                }

                return;
            }

            /*
             * ------------------------------------------------
             * ONE MISSED-LEAP PURSUIT BURST
             * ------------------------------------------------
             */
            if (this.phase == BoatAmbushPhase.BURSTING) {

                this.pursuitBurstTicks++;

                ColossalLobsterEntity.this
                        .getLookControl()
                        .setLookAt(
                                this.targetBoat,
                                30.0F,
                                30.0F
                        );

                boolean reachedBoat =
                        ColossalLobsterEntity.this
                                .getBoundingBox()
                                .inflate(1.0D)
                                .intersects(
                                        this.targetBoat.getBoundingBox()
                                );

                if (reachedBoat) {

                    destroyStruckBoat(this.targetBoat);

                    ColossalLobsterEntity.this.movementMode =
                            LobsterMovementMode.SINK;

                    this.stop();
                    return;
                }

                /*
                 * ONE burst only.
                 */
                if (this.pursuitBurstTicks >= 16) {

                    ColossalLobsterEntity.this.movementMode =
                            LobsterMovementMode.SINK;

                    this.stop();
                    return;
                }

                return;
            }

            /*
             * Keep ordinary prey suppressed while
             * the boat has the lobster's attention.
             */
            ColossalLobsterEntity.this
                    .setTarget(null);

            /*
             * Calculate horizontal distance only.
             *
             * A boat may be 16+ blocks vertically above the
             * lobster while almost directly overhead.
             */
            double xDifference =
                    this.targetBoat.getX()
                            - ColossalLobsterEntity.this.getX();

            double zDifference =
                    this.targetBoat.getZ()
                            - ColossalLobsterEntity.this.getZ();

            double horizontalDistanceSqr =
                    xDifference * xDifference
                            + zDifference * zDifference;

            double approachRangeSqr =
                    BOAT_APPROACH_RANGE
                            * BOAT_APPROACH_RANGE;

            double aimRangeSqr =
                    BOAT_AIM_RANGE
                            * BOAT_AIM_RANGE;

            /*
             * ------------------------------------------------
             * AIMING
             * ------------------------------------------------
             */
            if (horizontalDistanceSqr <= aimRangeSqr) {

                this.phase =
                        BoatAmbushPhase.AIMING;

                ColossalLobsterEntity.this
                        .getNavigation()
                        .stop();

                this.aimTicks++;

                double verticalDistance =
                        this.targetBoat.getY()
                                - ColossalLobsterEntity.this.getY();

                /*
                 * Calculate the same vertical launch strength
                 * that beginBoatLeap() will use.
                 *
                 * We need this here so we can accurately estimate
                 * how many ticks the lobster will spend rising.
                 */
                double estimatedVerticalStrength =
                        0.75D
                                + verticalDistance * 0.02D;

                estimatedVerticalStrength =
                        Math.max(
                                0.85D,
                                estimatedVerticalStrength
                        );

                estimatedVerticalStrength =
                        Math.min(
                                1.45D,
                                estimatedVerticalStrength
                        );

                /*
                 * Estimate the actual flight time using the
                 * lobster's BOAT_LEAP vertical physics.
                 */
                double estimatedFlightTicks =
                        calculateBoatLeapFlightTicks(
                                verticalDistance,
                                estimatedVerticalStrength
                        );

                /*
                 * Final prediction using the boat's measured motion
                 * immediately before launch.
                 */
                this.predictedBoatPosition =
                        this.targetBoat.position().add(
                                this.measuredBoatVelocity.scale(
                                        estimatedFlightTicks
                                )
                        );

                /*
                 * Look toward the predicted intercept point.
                 */
                ColossalLobsterEntity.this
                        .getLookControl()
                        .setLookAt(
                                this.predictedBoatPosition.x,
                                this.predictedBoatPosition.y,
                                this.predictedBoatPosition.z,
                                30.0F,
                                30.0F
                        );

                /*
                 * Aim for half a second before committing.
                 */
                if (this.aimTicks >= 6) {
                    beginBoatLeap();
                }

                return;
            }

            /*
             * ------------------------------------------------
             * APPROACHING
             * ------------------------------------------------
             */
            if (horizontalDistanceSqr <= approachRangeSqr) {

                this.aimTicks = 0;

                this.phase =
                        BoatAmbushPhase.APPROACHING;

                ColossalLobsterEntity.this
                        .getLookControl()
                        .setLookAt(
                                this.targetBoat,
                                30.0F,
                                30.0F
                        );

                ColossalLobsterEntity.this
                        .getNavigation()
                        .moveTo(
                                this.targetBoat,
                                APPROACH_SPEED
                        );

                return;
            }

            /*
             * ------------------------------------------------
             * WATCHING
             * ------------------------------------------------
             */
            this.aimTicks = 0;

            this.phase =
                    BoatAmbushPhase.WATCHING;

            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();

            ColossalLobsterEntity.this
                    .getLookControl()
                    .setLookAt(
                            this.targetBoat,
                            30.0F,
                            30.0F
                    );
        }

        /*
         * This MUST stay inside BoatStalkGoal because it uses
         * targetBoat, phase, predictedBoatPosition and boatLeapTicks.
         */
        private double calculateBoatLeapFlightTicks(
                double verticalDistance,
                double verticalStrength
        ) {

            double travelledY = 0.0D;
            double velocityY = verticalStrength;

            /*
             * Simulate the exact vertical physics used by
             * BOAT_LEAP in travel().
             */
            for (int tick = 1; tick <= 80; tick++) {

                travelledY += velocityY;

                if (travelledY >= verticalDistance) {
                    return tick;
                }

                velocityY =
                        velocityY * 0.99D - 0.02D;

                /*
                 * If it has already started falling before
                 * reaching the target height, this leap cannot
                 * naturally reach that Y level.
                 */
                if (velocityY <= 0.0D
                        && travelledY < verticalDistance) {
                    break;
                }
            }

            /*
             * Fallback. The vertical strength may need tuning
             * for exceptionally deep water.
             */
            return 40.0D;
        }

        private void beginBoatLeap() {

            if (this.targetBoat == null) {
                return;
            }

            this.phase =
                    BoatAmbushPhase.LEAPING;

            ColossalLobsterEntity.this.movementMode =
                    LobsterMovementMode.BOAT_LEAP;

            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();

            this.boatLeapTicks = 0;

            Vec3 lobsterPosition =
                    ColossalLobsterEntity.this.position();

            /*
             * ------------------------------------------------
             * CALCULATE VERTICAL LEAP
             * ------------------------------------------------
             */

            double verticalDistance =
                    this.targetBoat.getY()
                            - lobsterPosition.y;

            /*
             * Strong vertical launch that scales with depth.
             */
            double verticalStrength =
                    0.75D
                            + verticalDistance * 0.02D;

            verticalStrength =
                    Math.max(
                            0.85D,
                            verticalStrength
                    );

            verticalStrength =
                    Math.min(
                            1.45D,
                            verticalStrength
                    );

            double estimatedFlightTicks =
                    calculateBoatLeapFlightTicks(
                            verticalDistance,
                            verticalStrength
                    );

            /*
             * Lead the boat according to its ACTUAL measured
             * movement across recent ticks.
             */
            this.predictedBoatPosition =
                    this.targetBoat.position().add(
                            this.measuredBoatVelocity.scale(
                                    estimatedFlightTicks
                            )
                    );

            /*
             * ------------------------------------------------
             * CALCULATE HORIZONTAL INTERCEPT
             * ------------------------------------------------
             */

            Vec3 horizontalDirection =
                    new Vec3(
                            this.predictedBoatPosition.x
                                    - lobsterPosition.x,

                            0.0D,

                            this.predictedBoatPosition.z
                                    - lobsterPosition.z
                    );

            double horizontalDistance =
                    horizontalDirection.length();

            if (horizontalDistance > 0.001D) {
                horizontalDirection =
                        horizontalDirection.normalize();
            }

            /*
             * BOAT_LEAP horizontal movement keeps 99%
             * of its velocity every tick.
             *
             * Calculate how much total distance one unit
             * of initial velocity would travel during
             * the estimated flight.
             */
            double horizontalDrag =
                    0.99D;

            double horizontalTravelFactor =
                    (1.0D - Math.pow(
                            horizontalDrag,
                            estimatedFlightTicks
                    ))
                            / (1.0D - horizontalDrag);

            /*
             * Choose an initial horizontal speed intended
             * to reach the predicted intercept point.
             */
            double horizontalStrength =
                    horizontalDistance
                            / horizontalTravelFactor;

            /*
             * Safety cap for unusually extreme situations.
             */
            horizontalStrength =
                    Math.min(
                            horizontalStrength,
                            1.10D
                    );

            Vec3 launchVelocity =
                    new Vec3(
                            horizontalDirection.x
                                    * horizontalStrength,

                            verticalStrength,

                            horizontalDirection.z
                                    * horizontalStrength
                    );

            ColossalLobsterEntity.this
                    .setDeltaMovement(
                            launchVelocity
                    );

            ColossalLobsterEntity.this.hasImpulse =
                    true;
        }

        @Override
        public void stop() {

            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();

            this.targetBoat = null;

            this.phase =
                    BoatAmbushPhase.WATCHING;

            this.predictedBoatPosition =
                    Vec3.ZERO;

            this.aimTicks = 0;
            this.boatLeapTicks = 0;
            this.pursuitBurstTicks = 0;
            this.burstAimTicks = 0;
            this.usedPursuitBurst = false;

            this.lastBoatPosition =
                    Vec3.ZERO;

            this.measuredBoatVelocity =
                    Vec3.ZERO;

            this.hasLastBoatPosition =
                    false;
        }

        private Boat findNearestBoat() {

            double range =
                    BOAT_SENSE_RANGE;

            Boat nearestBoat =
                    null;

            double nearestDistanceSqr =
                    range * range;

            for (Boat boat :
                    ColossalLobsterEntity.this.level()
                            .getEntitiesOfClass(
                                    Boat.class,
                                    ColossalLobsterEntity.this
                                            .getBoundingBox()
                                            .inflate(range)
                            )) {

                if (!boat.isAlive()
                        || boat.isRemoved()) {
                    continue;
                }

                double distanceSqr =
                        ColossalLobsterEntity.this
                                .distanceToSqr(boat);

                if (distanceSqr
                        < nearestDistanceSqr) {

                    nearestDistanceSqr =
                            distanceSqr;

                    nearestBoat =
                            boat;
                }
            }

            return nearestBoat;
        }
    }

    private class PlayerRetrievalGoal extends Goal {

        private int leapTicks = 0;

        public PlayerRetrievalGoal() {

            this.setFlags(EnumSet.of(
                    Goal.Flag.MOVE,
                    Goal.Flag.LOOK
            ));
        }

        @Override
        public boolean canUse() {

            if (!ColossalLobsterEntity.this.retrievalActive) {
                return false;
            }

            Player target =
                    ColossalLobsterEntity.this.retrievalTarget;

            if (target == null || !target.isAlive()) {
                return false;
            }

            /*
             * During testing we intentionally DO NOT reject
             * creative players yet.
             */

            return true;
        }

        @Override
        public boolean canContinueToUse() {

            return ColossalLobsterEntity.this.retrievalActive
                    && ColossalLobsterEntity.this.retrievalTarget != null
                    && ColossalLobsterEntity.this.retrievalTarget.isAlive();
        }

        @Override
        public void tick() {

            Player target =
                    ColossalLobsterEntity.this.retrievalTarget;

            if (target == null || !target.isAlive()) {

                ColossalLobsterEntity.this
                        .endPlayerRetrieval();

                return;
            }

            /*
             * Seven-second window expired.
             */
            if (ColossalLobsterEntity.this.tickCount
                    >= ColossalLobsterEntity.this.retrievalEndTick) {

                ColossalLobsterEntity.this
                        .endPlayerRetrieval();

                return;
            }

            /*
             * Ignore EVERYTHING else.
             */
            ColossalLobsterEntity.this
                    .setTarget(target);

            ColossalLobsterEntity.this
                    .getLookControl()
                    .setLookAt(
                            target,
                            40.0F,
                            40.0F
                    );

            /*
             * ------------------------------------------------
             * PLAYER LEAP CURRENTLY IN PROGRESS
             * ------------------------------------------------
             */
            if (ColossalLobsterEntity.this.movementMode
                    == LobsterMovementMode.PLAYER_RETRIEVAL_LEAP) {

                this.leapTicks++;

                /*
                 * If our body/claw reaches the swimmer,
                 * convert directly into the established
                 * grab system.
                 */
                boolean reachedPlayer =
                        ColossalLobsterEntity.this
                                .getBoundingBox()
                                .inflate(2.0D)
                                .intersects(
                                        target.getBoundingBox()
                                );

                if (reachedPlayer) {

                    if (ColossalLobsterEntity.this
                            .grabVictim(target)) {

                        ColossalLobsterEntity.this
                                .endPlayerRetrieval();

                        ColossalLobsterEntity.this.movementMode =
                                LobsterMovementMode.SINK;

                        return;
                    }
                }

                /*
                 * The leap missed once it has passed its apex.
                 */
                if (this.leapTicks > 8
                        && ColossalLobsterEntity.this
                        .getDeltaMovement().y <= -0.05D) {

                    ColossalLobsterEntity.this.movementMode =
                            LobsterMovementMode.SINK;

                    /*
                     * Don't immediately launch again.
                     *
                     * The lobster must return to the bottom first.
                     */
                    ColossalLobsterEntity.this.nextRetrievalLeapTick =
                            ColossalLobsterEntity.this.tickCount + 8;

                    return;
                }

                return;
            }

            /*
             * If we're currently sinking after a failed retrieval
             * leap, wait for travel() to return us to BOTTOM_WALK.
             */
            if (ColossalLobsterEntity.this.movementMode
                    == LobsterMovementMode.SINK) {

                return;
            }

            /*
             * Don't launch another attempt until the small
             * post-miss delay has passed.
             */
            if (ColossalLobsterEntity.this.tickCount
                    < ColossalLobsterEntity.this.nextRetrievalLeapTick) {

                return;
            }

            /*
             * Must launch from the seabed.
             */
            if (!ColossalLobsterEntity.this.isNearSeabed()) {
                return;
            }

            beginRetrievalLeap(target);
        }

        private void beginRetrievalLeap(Player target) {

            this.leapTicks = 0;

            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();

            ColossalLobsterEntity.this.movementMode =
                    LobsterMovementMode.PLAYER_RETRIEVAL_LEAP;

            Vec3 lobsterPosition =
                    ColossalLobsterEntity.this.position();

            double verticalDistance =
                    target.getY()
                            - lobsterPosition.y;

            /*
             * Same vertical family as the successful
             * large boat leap.
             */
            double verticalStrength =
                    0.75D
                            + verticalDistance * 0.02D;

            verticalStrength =
                    Math.max(
                            0.85D,
                            verticalStrength
                    );

            verticalStrength =
                    Math.min(
                            1.45D,
                            verticalStrength
                    );

            /*
             * Estimate ascent duration using the same physics
             * as the boat leap.
             */
            double estimatedFlightTicks =
                    calculateRetrievalFlightTicks(
                            verticalDistance,
                            verticalStrength
                    );

            /*
             * Players are much more maneuverable than boats,
             * so don't lead them as aggressively.
             *
             * Their actual delta movement gives us a reasonable
             * one-shot intercept without creating homing behavior.
             */
            Vec3 targetVelocity =
                    target.getDeltaMovement();

            Vec3 predictedPosition =
                    target.position().add(
                            targetVelocity.scale(
                                    estimatedFlightTicks * 0.65D
                            )
                    );

            Vec3 horizontalDirection =
                    new Vec3(
                            predictedPosition.x
                                    - lobsterPosition.x,
                            0.0D,
                            predictedPosition.z
                                    - lobsterPosition.z
                    );

            double horizontalDistance =
                    horizontalDirection.length();

            if (horizontalDistance > 0.001D) {
                horizontalDirection =
                        horizontalDirection.normalize();
            }

            double horizontalDrag =
                    0.99D;

            double horizontalTravelFactor =
                    (1.0D - Math.pow(
                            horizontalDrag,
                            estimatedFlightTicks
                    ))
                            / (1.0D - horizontalDrag);

            double horizontalStrength =
                    horizontalDistance
                            / horizontalTravelFactor;

            /*
             * Slightly lower cap than the boat ambush.
             *
             * The stranded swimmer should still have some
             * ability to dodge individual leaps.
             */
            horizontalStrength =
                    Math.min(
                            horizontalStrength,
                            0.95D
                    );

            Vec3 launchVelocity =
                    new Vec3(
                            horizontalDirection.x
                                    * horizontalStrength,
                            verticalStrength,
                            horizontalDirection.z
                                    * horizontalStrength
                    );

            ColossalLobsterEntity.this
                    .setDeltaMovement(
                            launchVelocity
                    );

            ColossalLobsterEntity.this.hasImpulse =
                    true;
        }

        private double calculateRetrievalFlightTicks(
                double verticalDistance,
                double verticalStrength
        ) {

            double travelledY = 0.0D;
            double velocityY = verticalStrength;

            for (int tick = 1; tick <= 80; tick++) {

                travelledY += velocityY;

                if (travelledY >= verticalDistance) {
                    return tick;
                }

                velocityY =
                        velocityY * 0.99D - 0.02D;

                if (velocityY <= 0.0D
                        && travelledY < verticalDistance) {
                    break;
                }
            }

            return 40.0D;
        }

        @Override
        public void stop() {

            /*
             * Do NOT automatically cancel retrieval here.
             *
             * The entity-level retrieval state controls when
             * the seven-second hunt actually ends.
             */
            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();
        }
    }

    private class SmallPreyLeapGoal extends Goal {

        private LivingEntity target;
        private int leapTicks;
        private boolean attacked;

        public SmallPreyLeapGoal() {
            this.setFlags(EnumSet.of(
                    Goal.Flag.MOVE,
                    Goal.Flag.LOOK
            ));
        }

        @Override
        public boolean canUse() {

            /*
             * Only leap from normal seabed movement.
             */
            if (ColossalLobsterEntity.this.movementMode
                    != LobsterMovementMode.BOTTOM_WALK) {
                return false;
            }

            /*
             * Cooldown between leap attempts.
             */
            if (ColossalLobsterEntity.this.tickCount
                    < ColossalLobsterEntity.this.nextSmallLeapTick) {
                return false;
            }

            LivingEntity currentTarget =
                    ColossalLobsterEntity.this.getTarget();

            if (currentTarget == null || !currentTarget.isAlive()) {
                return false;
            }

            /*
             * This first leap is an underwater prey attack.
             */
            if (!currentTarget.isInWater()) {
                return false;
            }

            /*
             * For now, only use this small leap against aquatic prey.
             *
             * Boat attacks will receive their own much larger ambush goal later.
             */
            boolean validPrey =
                    currentTarget instanceof AbstractFish
                            || currentTarget instanceof Squid
                            || currentTarget instanceof Turtle
                            || currentTarget instanceof Dolphin;

            if (!validPrey) {
                return false;
            }

            /*
             * The lobster should launch from the seabed,
             * not repeatedly leap while already falling/swimming.
             */
            boolean nearGround =
                    ColossalLobsterEntity.this.onGround()
                            || ColossalLobsterEntity.this.level()
                            .getBlockState(
                                    ColossalLobsterEntity.this.blockPosition().below()
                            )
                            .isFaceSturdy(
                                    ColossalLobsterEntity.this.level(),
                                    ColossalLobsterEntity.this.blockPosition().below(),
                                    Direction.UP
                            );

            if (!nearGround) {
                return false;
            }

            double verticalDifference =
                    currentTarget.getY()
                            - ColossalLobsterEntity.this.getY();

            /*
             * Target must actually be above normal melee reach,
             * but not ridiculously high.
             */
            if (verticalDifference < 1.5D
                    || verticalDifference > 5.0D) {
                return false;
            }

            double xDifference =
                    currentTarget.getX()
                            - ColossalLobsterEntity.this.getX();

            double zDifference =
                    currentTarget.getZ()
                            - ColossalLobsterEntity.this.getZ();

            double horizontalDistanceSqr =
                    xDifference * xDifference
                            + zDifference * zDifference;

            /*
             * Don't leap at prey far across the ocean.
             *
             * 5 blocks horizontal range for this first version.
             */
            if (horizontalDistanceSqr > 25.0D) {
                return false;
            }

            /*
             * Don't launch through solid terrain.
             */
            if (!ColossalLobsterEntity.this
                    .getSensing()
                    .hasLineOfSight(currentTarget)) {
                return false;
            }

            this.target = currentTarget;

            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return this.target != null
                    && this.target.isAlive()
                    && ColossalLobsterEntity.this.movementMode
                    == LobsterMovementMode.LEAP
                    && this.leapTicks < 25;
        }

        @Override
        public void start() {

            this.leapTicks = 0;
            this.attacked = false;

            ColossalLobsterEntity.this
                    .getNavigation()
                    .stop();

            ColossalLobsterEntity.this.movementMode =
                    LobsterMovementMode.LEAP;

            Vec3 horizontalDirection =
                    new Vec3(
                            this.target.getX()
                                    - ColossalLobsterEntity.this.getX(),
                            0.0D,
                            this.target.getZ()
                                    - ColossalLobsterEntity.this.getZ()
                    );

            if (horizontalDirection.lengthSqr() > 0.001D) {
                horizontalDirection =
                        horizontalDirection.normalize();
            }

            double verticalDifference =
                    this.target.getY()
                            - ColossalLobsterEntity.this.getY();

            /*
             * Short prey leap:
             *
             * enough height for nearby fish,
             * but nowhere near the future boat ambush.
             */
            double verticalStrength =
                    0.50D + verticalDifference * 0.05D;

            verticalStrength =
                    Math.min(verticalStrength, 0.75D);

            Vec3 launchVelocity =
                    new Vec3(
                            horizontalDirection.x * 0.38D,
                            verticalStrength,
                            horizontalDirection.z * 0.38D
                    );

            ColossalLobsterEntity.this
                    .setDeltaMovement(launchVelocity);

            ColossalLobsterEntity.this.hasImpulse = true;
        }

        @Override
        public void tick() {

            this.leapTicks++;

            if (this.target == null) {
                return;
            }

            /*
             * Visually keep attention on the prey,
             * but do NOT home the actual trajectory toward it.
             */
            ColossalLobsterEntity.this
                    .getLookControl()
                    .setLookAt(
                            this.target,
                            30.0F,
                            30.0F
                    );

            /*
             * One attack attempt if the leap brings the lobster
             * into contact with the prey.
             */
            if (!this.attacked
                    && ColossalLobsterEntity.this
                    .getBoundingBox()
                    .inflate(0.5D)
                    .intersects(this.target.getBoundingBox())) {

                this.attacked = true;

                ColossalLobsterEntity.this
                        .doHurtTarget(this.target);

                ColossalLobsterEntity.this.movementMode =
                        LobsterMovementMode.SINK;

                return;
            }

            /*
             * Once the upward part of the leap is finished,
             * start sinking again.
             *
             * Give it a few ticks first so we don't terminate
             * immediately after launch.
             */
            if (this.leapTicks > 5
                    && ColossalLobsterEntity.this
                    .getDeltaMovement().y <= 0.0D) {

                ColossalLobsterEntity.this.movementMode =
                        LobsterMovementMode.SINK;
            }
        }

        @Override
        public void stop() {

            if (ColossalLobsterEntity.this.movementMode
                    == LobsterMovementMode.LEAP) {

                ColossalLobsterEntity.this.movementMode =
                        LobsterMovementMode.SINK;
            }

            /*
             * Three-second cooldown before another small leap.
             */
            ColossalLobsterEntity.this.nextSmallLeapTick =
                    ColossalLobsterEntity.this.tickCount + 30;

            this.target = null;
        }
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean didHurt = super.doHurtTarget(target);

        if (didHurt && !this.level().isClientSide) {
            this.triggerAnim("attackController", "attack");
        }

        return didHurt;
    }

//    @Nullable
//    @Override
//    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageableMob) {
//        return ModEntities.COLOSSAL_LOBSTER.get().create(level);
//    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(
                new AnimationController<>(this, "controller", 10, this::predicate)
        );

        controllerRegistrar.add(
                new AnimationController<>(this, "attackController", 0, state -> PlayState.STOP)
                        .triggerableAnim(
                                "attack",
                                RawAnimation.begin()
                                        .then("animation.colossal_lobster.attack", Animation.LoopType.PLAY_ONCE)
                        )
        );
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (tAnimationState.isMoving()) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.colossal_lobster.walk", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.colossal_lobster.idle", Animation.LoopType.LOOP));
        return PlayState.CONTINUE;
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
    /// ToDo: Add the looting enchantment effect to lobster drop for crustacean meat if killed with the looting enchant
    /// ToDo: Add crustacean meat to drop cooked crustacean meat if entity is burning for a drop

    @Override
    protected void dropFromLootTable(DamageSource source, boolean causedByPlayer) {
        // Drop humanoid flesh
        int dropCount = this.random.nextInt(9) + 4; // Randomly generate a number between 4 and 9
        for (int i = 0; i < dropCount; i++) {
            this.spawnAtLocation(new ItemStack(ModItems.RAW_CRUSTACEAN_MEAT.get()));
        }

        super.dropFromLootTable(source, causedByPlayer);
    }

    public MobType getMobType() {
        return MobType.ARTHROPOD;
    }
}