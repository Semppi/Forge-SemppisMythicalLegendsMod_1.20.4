package net.semppi.semppis_mythical_legends_mod.block.client;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.entity.CraftingOvenBlockEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CraftingOvenModel
        extends GeoModel<CraftingOvenBlockEntity> {

    @Override
    public ResourceLocation getModelResource(
            CraftingOvenBlockEntity animatable
    ) {
        return new ResourceLocation(
                SemppisMythicalLegendsMod.MOD_ID,
                "geo/crafting_oven.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(
            CraftingOvenBlockEntity animatable
    ) {
        return new ResourceLocation(
                SemppisMythicalLegendsMod.MOD_ID,
                "textures/block/crafting_oven.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(
            CraftingOvenBlockEntity animatable
    ) {
        return null;
    }

    @Override
    public void setCustomAnimations(
            CraftingOvenBlockEntity animatable,
            long instanceId,
            AnimationState<CraftingOvenBlockEntity> animationState
    ) {
        super.setCustomAnimations(
                animatable,
                instanceId,
                animationState
        );

        /*
         * Always begin hidden.
         *
         * This prevents a previous world render from leaving
         * the connector visible when the same model is later
         * rendered as an item.
         */
        setBoneHidden(
                "oven_connected",
                true
        );

        Level level =
                animatable.getLevel();

        /*
         * Item/inventory rendering does not have a meaningful
         * world level for this oven.
         *
         * Leave the connector hidden.
         */
        if (level == null) {
            return;
        }

        BlockPos ovenPos =
                animatable.getBlockPos();

        /*
         * Extra safety:
         * only use chimney logic if this block entity still
         * corresponds to a real Crafting Oven in the world.
         */
        BlockState state =
                level.getBlockState(
                        ovenPos
                );

        if (!state.is(
                ModBlocks.CRAFTING_OVEN.get()
        )) {
            return;
        }

        BlockPos wallPos =
                ovenPos.above(2);

        boolean wallAbove =
                level.getBlockState(
                        wallPos
                ).is(BlockTags.WALLS);

        setBoneHidden(
                "oven_connected",
                !wallAbove
        );
    }

    private void setBoneHidden(
            String boneName,
            boolean hidden
    ) {
        CoreGeoBone bone =
                getAnimationProcessor()
                        .getBone(
                                boneName
                        );

        if (bone != null) {
            bone.setHidden(
                    hidden
            );
        }
    }
}