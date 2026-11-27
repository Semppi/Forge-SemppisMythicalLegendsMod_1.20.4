package net.semppi.semppis_mythical_legends_mod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.semppi.semppis_mythical_legends_mod.block.entity.ModBlockEntities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;
import net.semppi.semppis_mythical_legends_mod.block.entity.CraftingOvenBlockEntity;
import org.jetbrains.annotations.Nullable;

public class CraftingOvenBlock
        extends BaseEntityBlock
        implements SimpleWaterloggedBlock {

    public static final MapCodec<CraftingOvenBlock> CODEC =
            simpleCodec(CraftingOvenBlock::new);

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;

    public static final BooleanProperty LIT =
            BlockStateProperties.LIT;

    private static final int CHIMNEY_HORIZONTAL_RANGE = 3;

    /*
     * Number of wall blocks allowed vertically,
     * counting the first wall directly above the oven.
     */
    private static final int CHIMNEY_MAX_HEIGHT = 12;

    /*
     * MAIN BODY:
     *
     * lower main block = 16 × 16 × 16
     * upper main block = 13 pixels tall, ending at Y 29
     *
     * The side extension rotates around the main block.
     */

    private static final VoxelShape NORTH_SHAPE = Shapes.or(

            // Main lower body
            Block.box(
                    0.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 16.0D
            ),

            // Extra lower section toward -X / west
            Block.box(
                    -16.0D, 0.0D, 0.0D,
                    0.0D, 16.0D, 16.0D
            ),

            // Upper body
            Block.box(
                    0.0D, 16.0D, 0.0D,
                    16.0D, 29.0D, 16.0D
            )
    );

    private static final VoxelShape EAST_SHAPE = Shapes.or(

            // Main lower body
            Block.box(
                    0.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 16.0D
            ),

            // Rotated extension toward -Z / north
            Block.box(
                    0.0D, 0.0D, -16.0D,
                    16.0D, 16.0D, 0.0D
            ),

            // Upper body
            Block.box(
                    0.0D, 16.0D, 0.0D,
                    16.0D, 29.0D, 16.0D
            )
    );

    private static final VoxelShape SOUTH_SHAPE = Shapes.or(

            // Main lower body
            Block.box(
                    0.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 16.0D
            ),

            // Rotated extension toward +X / east
            Block.box(
                    16.0D, 0.0D, 0.0D,
                    32.0D, 16.0D, 16.0D
            ),

            // Upper body
            Block.box(
                    0.0D, 16.0D, 0.0D,
                    16.0D, 29.0D, 16.0D
            )
    );

    private static final VoxelShape WEST_SHAPE = Shapes.or(

            // Main lower body
            Block.box(
                    0.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 16.0D
            ),

            // Rotated extension toward +Z / south
            Block.box(
                    0.0D, 0.0D, 16.0D,
                    16.0D, 16.0D, 32.0D
            ),

            // Upper body
            Block.box(
                    0.0D, 16.0D, 0.0D,
                    16.0D, 29.0D, 16.0D
            )
    );

    public static VoxelShape getShapeForFacing(BlockState state) {

        return switch (state.getValue(FACING)) {

            case NORTH -> NORTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;

            default -> NORTH_SHAPE;
        };
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return getShapeForFacing(state);
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return getShapeForFacing(state);
    }

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    public CraftingOvenBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(WATERLOGGED, false)
                        .setValue(FACING, Direction.NORTH)
                        .setValue(LIT, false)
        );
    }

    private BlockPos getSidePos(
            BlockPos mainPos,
            Direction facing
    ) {
        return mainPos.relative(
                facing.getCounterClockWise()
        );
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(
                FACING,
                WATERLOGGED,
                LIT
        );
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(
            BlockPlaceContext context
    ) {
        FluidState fluidState =
                context.getLevel()
                        .getFluidState(
                                context.getClickedPos()
                        );

        Direction facing =
                context.getHorizontalDirection()
                        .getOpposite();

        BlockPos mainPos =
                context.getClickedPos();

        BlockPos sidePos =
                getSidePos(
                        mainPos,
                        facing
                );

        BlockPos upperPos =
                mainPos.above();

        /*
         * The oven needs all three world cells available.
         */
        if (!context.getLevel()
                .getBlockState(sidePos)
                .canBeReplaced(context)) {

            return null;
        }

        if (!context.getLevel()
                .getBlockState(upperPos)
                .canBeReplaced(context)) {

            return null;
        }

        return this.defaultBlockState()
                .setValue(
                        WATERLOGGED,
                        fluidState.getType() == Fluids.WATER
                )
                .setValue(
                        LIT,
                        false
                )

                .setValue(
                        FACING,
                        facing
                );
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {

        if (level.isClientSide) {
            return null;
        }

        return createTickerHelper(
                type,
                ModBlockEntities.CRAFTING_OVEN_BLOCK_ENTITY.get(),
                CraftingOvenBlockEntity::serverTick
        );
    }

    @Override
    public void animateTick(
            BlockState state,
            Level level,
            BlockPos pos,
            RandomSource random
    ) {

        /*
         * Only make cooking particles while the oven is lit.
         */
        if (!state.getValue(LIT)) {
            return;
        }

        Direction facing =
                state.getValue(FACING);

        /*
         * =================================================
         * FRONT FIRE / SMOKE PARTICLES
         * =================================================
         *
         * These appear at the front of the MAIN oven block,
         * similar to a vanilla furnace.
         */

        double centerX =
                pos.getX() + 0.5D;

        double centerY =
                pos.getY() + 0.5D;

        double centerZ =
                pos.getZ() + 0.5D;

        /*
         * Move the particle position toward the front face.
         */
        double frontDistance =
                0.52D;

        double frontX =
                centerX
                        + facing.getStepX()
                        * frontDistance;

        double frontZ =
                centerZ
                        + facing.getStepZ()
                        * frontDistance;

        /*
         * Give the front particles a tiny random sideways spread
         * so they don't all appear at exactly one pixel-perfect point.
         */
        double sidewaysSpread =
                (random.nextDouble() - 0.5D)
                        * 0.35D;

        if (facing.getAxis()
                == Direction.Axis.X) {

            frontZ +=
                    sidewaysSpread;

        } else {

            frontX +=
                    sidewaysSpread;
        }

        /*
         * Vanilla-like furnace smoke.
         */
        level.addParticle(
                ParticleTypes.SMOKE,
                frontX,
                centerY,
                frontZ,
                0.0D,
                0.0D,
                0.0D
        );

        /*
         * Small fire spark/flame at the opening.
         */
        level.addParticle(
                ParticleTypes.FLAME,
                frontX,
                centerY,
                frontZ,
                0.0D,
                0.0D,
                0.0D
        );

        /*
         * =================================================
         * TOP SMOKE / CHIMNEY PARTICLES
         * =================================================
         *
         * The oven occupies:
         *
         * pos            = main oven block
         * pos.above()    = invisible upper oven part
         * pos.above(2)   = first possible chimney wall
         * pos.above(3)   = second possible chimney wall
         *
         * For this first version we support either:
         *
         * - one wall
         * - two stacked walls
         */
        BlockPos firstWallPos =
                pos.above(2);

        BlockPos secondWallPos =
                pos.above(3);

        boolean firstWall =
                level.getBlockState(
                        firstWallPos
                ).is(BlockTags.WALLS);

        boolean secondWall =
                firstWall
                        && level.getBlockState(
                        secondWallPos
                ).is(BlockTags.WALLS);

        /*
         * =================================================
         * CHIMNEY MODE
         * =================================================
         *
         * If at least one wall is attached to the oven,
         * suppress the ordinary little oven-roof smoke.
         */
        BlockPos chimneyOutlet =
                findChimneyOutlet(
                        level,
                        pos,
                        facing
                );

        if (chimneyOutlet != null) {

            double smokeX =
                    chimneyOutlet.getX()
                            + 0.5D
                            + (random.nextDouble() - 0.5D)
                            * 0.12D;

            double smokeY =
                    chimneyOutlet.getY()
                            + 1.05D;

            double smokeZ =
                    chimneyOutlet.getZ()
                            + 0.5D
                            + (random.nextDouble() - 0.5D)
                            * 0.12D;

            if (random.nextDouble() < 0.35D) {

                level.addParticle(
                        ParticleTypes.CAMPFIRE_COSY_SMOKE,
                        smokeX,
                        smokeY,
                        smokeZ,
                        0.0D,
                        0.07D,
                        0.0D
                );
            }
        }
        else {

            /*
             * No acceptable chimney path.
             *
             * Use the ordinary Crafting Oven roof smoke.
             */
            double roofY =
                    pos.getY()
                            + 29.0D / 16.0D;

            if (random.nextDouble() < 0.20D) {

                double topX =
                        pos.getX()
                                + 0.5D
                                + (random.nextDouble() - 0.5D)
                                * 0.15D;

                double topZ =
                        pos.getZ()
                                + 0.5D
                                + (random.nextDouble() - 0.5D)
                                * 0.15D;

                level.addParticle(
                        ParticleTypes.SMOKE,
                        topX,
                        roofY + 0.02D,
                        topZ,
                        0.0D,
                        0.03D,
                        0.0D
                );
            }
        }
    }

    @Nullable
    private static BlockPos findChimneyOutlet(
            Level level,
            BlockPos ovenPos,
            Direction ovenFacing
    ) {
        /*
         * The oven occupies:
         *
         * ovenPos          = lower/main block
         * ovenPos.above()  = invisible upper oven section
         * ovenPos.above(2) = first possible chimney wall
         */
        BlockPos start =
                ovenPos.above(2);

        if (!level.getBlockState(start)
                .is(net.minecraft.tags.BlockTags.WALLS)) {

            return null;
        }

        Queue<BlockPos> open =
                new ArrayDeque<>();

        Set<BlockPos> visited =
                new HashSet<>();

        open.add(start);

        List<BlockPos> outlets =
                new ArrayList<>();

        while (!open.isEmpty()) {

            BlockPos current =
                    open.remove();

            if (!visited.add(current)) {
                continue;
            }

            int dx =
                    current.getX()
                            - start.getX();

            int dz =
                    current.getZ()
                            - start.getZ();

            int height =
                    current.getY()
                            - start.getY();

            /*
             * Outside our accepted chimney area.
             */
            if (Math.abs(dx)
                    > CHIMNEY_HORIZONTAL_RANGE
                    || Math.abs(dz)
                    > CHIMNEY_HORIZONTAL_RANGE
                    || height < 0
                    || height >= CHIMNEY_MAX_HEIGHT) {

                continue;
            }

            if (!level.getBlockState(current)
                    .is(net.minecraft.tags.BlockTags.WALLS)) {

                continue;
            }

            BlockPos above =
                    current.above();

            boolean wallAbove =
                    level.getBlockState(above)
                            .is(net.minecraft.tags.BlockTags.WALLS);

            /*
             * An exposed top is a possible smoke outlet.
             *
             * However, if another wall continues immediately
             * above the maximum permitted height, this branch
             * is too tall and is not accepted as an outlet.
             */
            if (!wallAbove) {

                /*
                 * This wall has open air directly above it,
                 * so smoke could escape from here.
                 */
                outlets.add(
                        current.immutable()
                );
            }

            /*
             * Explore all six connected directions.
             *
             * Walls can therefore rise, bend sideways,
             * and form more elaborate chimney shapes.
             */
            for (Direction direction
                    : Direction.values()) {

                BlockPos next =
                        current.relative(
                                direction
                        );

                if (visited.contains(next)) {
                    continue;
                }

                int nextDx =
                        next.getX()
                                - start.getX();

                int nextDz =
                        next.getZ()
                                - start.getZ();

                int nextHeight =
                        next.getY()
                                - start.getY();

                if (Math.abs(nextDx)
                        > CHIMNEY_HORIZONTAL_RANGE
                        || Math.abs(nextDz)
                        > CHIMNEY_HORIZONTAL_RANGE
                        || nextHeight < 0
                        || nextHeight >= CHIMNEY_MAX_HEIGHT) {

                    continue;
                }

                if (level.getBlockState(next)
                        .is(net.minecraft.tags.BlockTags.WALLS)) {

                    open.add(next);
                }
            }
        }

        return chooseBestChimneyOutlet(
                outlets,
                start,
                ovenFacing
        );
    }

    @Nullable
    private static BlockPos chooseBestChimneyOutlet(
            List<BlockPos> outlets,
            BlockPos start,
            Direction ovenFacing
    ) {
        if (outlets.isEmpty()) {
            return null;
        }

        /*
         * First find the absolute highest possible exit.
         */
        int highestY =
                outlets.stream()
                        .mapToInt(BlockPos::getY)
                        .max()
                        .orElse(start.getY());

        BlockPos best = null;

        for (BlockPos candidate : outlets) {

            /*
             * PATH-OF-LEAST-RESISTANCE RULE:
             *
             * Allow an outlet one block below the absolute
             * maximum to compete with the highest outlets.
             *
             * This prevents smoke from travelling a long
             * horizontal pipe merely to rise one extra block.
             */
            if (candidate.getY()
                    < highestY - 1) {

                continue;
            }

            if (best == null) {
                best = candidate;
                continue;
            }

            int candidateDistance =
                    horizontalDistanceSquared(
                            candidate,
                            start
                    );

            int bestDistance =
                    horizontalDistanceSquared(
                            best,
                            start
                    );

            /*
             * Prefer the outlet closest to the chimney's
             * original vertical centerline.
             */
            if (candidateDistance
                    < bestDistance) {

                best = candidate;
                continue;
            }

            if (candidateDistance
                    > bestDistance) {

                continue;
            }

            /*
             * Equal distance:
             * prefer the oven's right side.
             */
            Direction right =
                    ovenFacing.getClockWise();

            int candidateRight =
                    horizontalSideScore(
                            candidate,
                            start,
                            right
                    );

            int bestRight =
                    horizontalSideScore(
                            best,
                            start,
                            right
                    );

            if (candidateRight
                    > bestRight) {

                best = candidate;
                continue;
            }

            /*
             * Final tie:
             * if everything else is identical,
             * retain the higher outlet.
             */
            if (candidateRight == bestRight
                    && candidate.getY()
                    > best.getY()) {

                best = candidate;
            }
        }

        return best;
    }

    private static int horizontalDistanceSquared(
            BlockPos pos,
            BlockPos start
    ) {
        int dx =
                pos.getX()
                        - start.getX();

        int dz =
                pos.getZ()
                        - start.getZ();

        return dx * dx
                + dz * dz;
    }

    private static int horizontalSideScore(
            BlockPos pos,
            BlockPos start,
            Direction direction
    ) {
        int dx =
                pos.getX()
                        - start.getX();

        int dz =
                pos.getZ()
                        - start.getZ();

        return dx * direction.getStepX()
                + dz * direction.getStepZ();
    }

    @Override
    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED)
                ? Fluids.WATER.getSource(false)
                : super.getFluidState(blockState);
    }

    @Override
    public BlockState updateShape(
            BlockState blockState,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos neighborPos
    ) {
        if (blockState.getValue(WATERLOGGED)) {
            level.scheduleTick(
                    currentPos,
                    Fluids.WATER,
                    Fluids.WATER.getTickDelay(level)
            );
        }

        return super.updateShape(
                blockState,
                direction,
                neighborState,
                level,
                currentPos,
                neighborPos
        );
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        return new ItemStack(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(
            BlockPos blockPos,
            BlockState blockState
    ) {
        return new CraftingOvenBlockEntity(blockPos, blockState);
    }

    @Override
    public void onRemove(
            BlockState oldState,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {

        /*
         * Only do the destruction work when the actual
         * Crafting Oven is being removed.
         *
         * Changing LIT from false to true or true to false
         * must NOT count as breaking the oven.
         */
        if (!oldState.is(newState.getBlock())) {

            if (!level.isClientSide) {

                /*
                 * ------------------------------------------------
                 * DROP THE SMOKER INVENTORY
                 * ------------------------------------------------
                 *
                 * 0 = smoker input
                 * 1 = fuel
                 * 2 = smoker output
                 */
                BlockEntity blockEntity =
                        level.getBlockEntity(pos);

                if (blockEntity instanceof CraftingOvenBlockEntity oven) {

                    for (int slot = 0;
                         slot < CraftingOvenBlockEntity.TOTAL_SLOT_COUNT;
                         slot++) {

                        ItemStack stack =
                                oven.getItemHandler()
                                        .getStackInSlot(slot);

                        if (!stack.isEmpty()) {

                            Block.popResource(
                                    level,
                                    pos,
                                    stack.copy()
                            );

                            /*
                             * Empty the stored copy so the same
                             * item cannot be dropped twice.
                             */
                            oven.getItemHandler()
                                    .setStackInSlot(
                                            slot,
                                            ItemStack.EMPTY
                                    );
                        }
                    }
                }

                /*
                 * ------------------------------------------------
                 * REMOVE THE INVISIBLE SIDE AND UPPER PARTS
                 * ------------------------------------------------
                 */
                Direction facing =
                        oldState.getValue(FACING);

                BlockPos sidePos =
                        getSidePos(
                                pos,
                                facing
                        );

                BlockPos upperPos =
                        pos.above();

                if (level.getBlockState(sidePos)
                        .is(ModBlocks.CRAFTING_OVEN_PART.get())) {

                    level.removeBlock(
                            sidePos,
                            false
                    );
                }

                if (level.getBlockState(upperPos)
                        .is(ModBlocks.CRAFTING_OVEN_PART.get())) {

                    level.removeBlock(
                            upperPos,
                            false
                    );
                }
            }
        }

        /*
         * Let Minecraft finish the normal block-removal work.
         *
         * This should happen exactly once.
         */
        super.onRemove(
                oldState,
                level,
                pos,
                newState,
                isMoving
        );
    }

    @Override
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {

        if (!level.isClientSide) {

            BlockEntity blockEntity =
                    level.getBlockEntity(pos);

            if (blockEntity instanceof CraftingOvenBlockEntity oven
                    && player instanceof ServerPlayer serverPlayer) {

                serverPlayer.openMenu(
                        oven,
                        buffer -> buffer.writeBlockPos(pos)
                );
            }
        }

        return InteractionResult.sidedSuccess(
                level.isClientSide
        );
    }

    @Override
    public void setPlacedBy(
            net.minecraft.world.level.Level level,
            BlockPos pos,
            BlockState state,
            @Nullable net.minecraft.world.entity.LivingEntity placer,
            ItemStack stack
    ) {
        super.setPlacedBy(
                level,
                pos,
                state,
                placer,
                stack
        );

        if (level.isClientSide) {
            return;
        }

        Direction facing =
                state.getValue(FACING);

        BlockPos sidePos =
                getSidePos(
                        pos,
                        facing
                );

        BlockPos upperPos =
                pos.above();

        BlockState sideState =
                ModBlocks.CRAFTING_OVEN_PART.get()
                        .defaultBlockState()
                        .setValue(
                                CraftingOvenPartBlock.FACING,
                                facing
                        )
                        .setValue(
                                CraftingOvenPartBlock.UPPER,
                                false
                        );

        BlockState upperState =
                ModBlocks.CRAFTING_OVEN_PART.get()
                        .defaultBlockState()
                        .setValue(
                                CraftingOvenPartBlock.FACING,
                                facing
                        )
                        .setValue(
                                CraftingOvenPartBlock.UPPER,
                                true
                        );

        level.setBlock(
                sidePos,
                sideState,
                Block.UPDATE_ALL
        );

        level.setBlock(
                upperPos,
                upperState,
                Block.UPDATE_ALL
        );
    }
}