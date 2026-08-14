package net.semppi.semppis_mythical_legends_mod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MediumHumanoidDropBlockEntity
        extends BlockEntity
        implements GeoBlockEntity {

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    public MediumHumanoidDropBlockEntity(
            BlockPos blockPos,
            BlockState blockState
    ) {
        super(
                ModBlockEntities.MEDIUM_HUMANOID_DROP_BLOCK_ENTITY.get(),
                blockPos,
                blockState
        );
    }

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
        // No animations yet.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}