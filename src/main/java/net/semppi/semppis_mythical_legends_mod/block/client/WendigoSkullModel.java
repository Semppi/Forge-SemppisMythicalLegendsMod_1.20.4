package net.semppi.semppis_mythical_legends_mod.block.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.entity.WendigoSkullBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class WendigoSkullModel extends GeoModel<WendigoSkullBlockEntity> {
    @Override
    public ResourceLocation getModelResource(WendigoSkullBlockEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/wendigo_skull.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WendigoSkullBlockEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/block/wendigo_skull.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WendigoSkullBlockEntity animatable) {
        return null; // No animation file since it's not animated
    }
}