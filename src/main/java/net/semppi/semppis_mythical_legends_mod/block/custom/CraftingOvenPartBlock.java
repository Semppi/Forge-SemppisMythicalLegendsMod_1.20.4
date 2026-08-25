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
        return Shapes.empty();
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