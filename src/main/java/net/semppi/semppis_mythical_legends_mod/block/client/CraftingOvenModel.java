package net.semppi.semppis_mythical_legends_mod.block.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.entity.CraftingOvenBlockEntity;
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
}