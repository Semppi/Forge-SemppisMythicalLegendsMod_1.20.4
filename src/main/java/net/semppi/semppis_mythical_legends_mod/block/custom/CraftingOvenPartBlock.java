package net.semppi.semppis_mythical_legends_mod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CraftingOvenPartBlock extends Block {

    public static final DirectionProperty FACING =
            BlockStateProperties.HORIZONTAL_FACING;

    /*
     * false = side piece
     * true  = upper piece
     */
    public static final BooleanProperty UPPER =
            BooleanProperty.create("upper");

    public CraftingOvenPartBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(UPPER, false)
        );
    }

    /*
     * Selection shape for the SIDE helper block.
     *
     * The side/table section occupies a complete neighboring
     * block cell for interaction purposes.
     */
    private static final VoxelShape SIDE_SHAPE =
            Block.box(
                    0.0D, 0.0D, 0.0D,
                    16.0D, 16.0D, 16.0D
            );

    /*
     * Selection shape for the UPPER helper block.
     *
     * The main Crafting Oven model reaches from world Y16
     * to model Y29, so inside this upper helper block it
     * occupies 13 pixels of height.
     */
    private static final VoxelShape UPPER_SHAPE =
            Block.box(
                    0.0D, 0.0D, 0.0D,
                    16.0D, 13.0D, 16.0D
            );

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(
                FACING,
                UPPER
        );
    }

    /*
     * The visible/collision geometry is already provided
     * by the main CraftingOvenBlock's oversized VoxelShape.
     *
     * This block only reserves the occupied world cell.
     */
    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        /*
         * Find the actual Crafting Oven main block associated
         * with this invisible upper/side section.
         */
        BlockPos mainPos =
                getMainPos(
                        pos,
                        state
                );

        BlockState mainState =
                level.getBlockState(
                        mainPos
                );

        /*
         * If something has gone wrong and the main oven
         * no longer exists, don't leave a strange outline.
         */
        if (!mainState.is(
                net.semppi.semppis_mythical_legends_mod.block.ModBlocks
                        .CRAFTING_OVEN
                        .get()
        )) {
            return Shapes.empty();
        }

        /*
         * Get exactly the same complete outline used when
         * targeting the main Crafting Oven block.
         */
        VoxelShape fullOvenShape =
                CraftingOvenBlock.getShapeForFacing(
                        mainState
                );

        /*
         * VoxelShape coordinates are relative to the block
         * currently being targeted.
         *
         * The full oven shape is expressed relative to mainPos,
         * so translate it into this helper block's coordinate space.
         */
        double offsetX =
                mainPos.getX()
                        - pos.getX();

        double offsetY =
                mainPos.getY()
                        - pos.getY();

        double offsetZ =
                mainPos.getZ()
                        - pos.getZ();

        return fullOvenShape.move(
                offsetX,
                offsetY,
                offsetZ
        );
    }

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return Shapes.empty();
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        /*
         * Creative Pick Block on either invisible helper
         * section should give the actual Crafting Oven,
         * never the internal helper block.
         */
        return new ItemStack(
                ModBlocks.CRAFTING_OVEN.get()
        );
    }

    /*
     * Work out where the actual Crafting Oven main block is.
     */
    private BlockPos getMainPos(
            BlockPos partPos,
            BlockState state
    ) {
        if (state.getValue(UPPER)) {
            return partPos.below();
        }

        Direction facing =
                state.getValue(FACING);

        /*
         * SIDE was placed counter-clockwise from MAIN,
         * so MAIN is clockwise from SIDE.
         */
        return partPos.relative(
                facing.getClockWise()
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
        BlockPos mainPos =
                getMainPos(
                        pos,
                        state
                );

        BlockState mainState =
                level.getBlockState(
                        mainPos
                );

        if (mainState.is(
                net.semppi.semppis_mythical_legends_mod.block.ModBlocks
                        .CRAFTING_OVEN
                        .get()
        )) {

            /*
             * Directly call the actual Crafting Oven block's
             * interaction using its real world position.
             */
            return mainState.getBlock()
                    .use(
                            mainState,
                            level,
                            mainPos,
                            player,
                            hand,
                            hitResult
                    );
        }

        return InteractionResult.PASS;
    }

    /*
     * Breaking either invisible section breaks the real oven.
     */
    @Override
    public BlockState playerWillDestroy(
            Level level,
            BlockPos pos,
            BlockState state,
            Player player
    ) {
        if (!level.isClientSide) {

            BlockPos mainPos =
                    getMainPos(
                            pos,
                            state
                    );

            BlockState mainState =
                    level.getBlockState(
                            mainPos
                    );

            if (mainState.is(
                    net.semppi.semppis_mythical_legends_mod.block.ModBlocks.CRAFTING_OVEN.get()
            )) {

                level.destroyBlock(
                        mainPos,
                        !player.getAbilities().instabuild,
                        player
                );
            }
        }

        return super.playerWillDestroy(
                level,
                pos,
                state,
                player
        );
    }
}