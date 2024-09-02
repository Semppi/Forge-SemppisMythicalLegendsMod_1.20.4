package net.semppi.semppis_mythical_legends_mod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.RenderUtils;

public class PukisEggBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    public PukisEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PUKIS_EGG_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object blockEntity) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        // No animation controllers needed for static block
    }
}