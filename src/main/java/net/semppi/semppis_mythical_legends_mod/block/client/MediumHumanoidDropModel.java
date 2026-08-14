package net.semppi.semppis_mythical_legends_mod.block.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.entity.MediumHumanoidDropBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class MediumHumanoidDropModel
        extends GeoModel<MediumHumanoidDropBlockEntity> {

    @Override
    public ResourceLocation getModelResource(
            MediumHumanoidDropBlockEntity animatable
    ) {
        return new ResourceLocation(
                SemppisMythicalLegendsMod.MOD_ID,
                "geo/medium_humanoid_drop.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(
            MediumHumanoidDropBlockEntity animatable
    ) {
        return new ResourceLocation(
                SemppisMythicalLegendsMod.MOD_ID,
                "textures/block/medium_humanoid_bones.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(
            MediumHumanoidDropBlockEntity animatable
    ) {
        return null;
    }
}