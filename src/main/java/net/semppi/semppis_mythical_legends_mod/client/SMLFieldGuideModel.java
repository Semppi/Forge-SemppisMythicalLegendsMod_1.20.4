package net.semppi.semppis_mythical_legends_mod.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;

import software.bernie.geckolib.model.GeoModel;

public class SMLFieldGuideModel
        extends GeoModel<SMLFieldGuideAnimatable> {

    @Override
    public ResourceLocation getModelResource(
            SMLFieldGuideAnimatable animatable
    ) {
        return new ResourceLocation(
                SemppisMythicalLegendsMod.MOD_ID,
                "geo/sml_field_guide.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(
            SMLFieldGuideAnimatable animatable
    ) {
        return new ResourceLocation(
                SemppisMythicalLegendsMod.MOD_ID,
                "textures/gui/field_guide/sml_field_guide.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(
            SMLFieldGuideAnimatable animatable
    ) {
        return new ResourceLocation(
                SemppisMythicalLegendsMod.MOD_ID,
                "animations/sml_field_guide.animation.json"
        );
    }
}