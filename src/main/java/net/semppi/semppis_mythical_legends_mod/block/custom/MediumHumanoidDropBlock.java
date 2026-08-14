package net.semppi.semppis_mythical_legends_mod.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.shapes.Shapes;
import net.semppi.semppis_mythical_legends_mod.block.entity.MediumHumanoidDropBlockEntity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class MediumHumanoidDropBlock
        extends BaseEntityBlock
        implements SimpleWaterloggedBlock {

    public static final MapCodec<MediumHumanoidDropBlock> CODEC =
            simpleCodec(MediumHumanoidDropBlock::new);

    public static final BooleanProperty WATERLOGGED =
            BlockStateProperties.WATERLOGGED;

    private static final VoxelShape SHAPE =
            box(1.0D, 0.0D, 1.0D, 15.0D, 10.0D, 15.0D);

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
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

    public MediumHumanoidDropBlock(Properties properties) {
        super(properties);

        this.registerDefaultState(
                this.stateDefinition.any()
                        .setValue(WATERLOGGED, false)
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
        builder.add(WATERLOGGED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState =
                context.getLevel().getFluidState(context.getClickedPos());

        return this.defaultBlockState()
                .setValue(
                        WATERLOGGED,
                        fluidState.getType() == Fluids.WATER
                );
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
        return new ItemStack(Items.BONE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(
            BlockPos blockPos,
            BlockState blockState
    ) {
        return new MediumHumanoidDropBlockEntity(blockPos, blockState);
    }
}