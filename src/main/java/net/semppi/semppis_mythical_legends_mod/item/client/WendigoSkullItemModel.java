package net.semppi.semppis_mythical_legends_mod.item.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.item.custom.WendigoSkullItem;
import software.bernie.geckolib.model.GeoModel;

public class WendigoSkullItemModel extends GeoModel<WendigoSkullItem> {
    @Override
    public ResourceLocation getModelResource(WendigoSkullItem animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/wendigo_skull.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WendigoSkullItem animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/block/wendigo_skull.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WendigoSkullItem animatable) {
        return null; // No animation file since it's not animated
    }
}