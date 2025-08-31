package net.semppi.semppis_mythical_legends_mod.item.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.item.custom.WrappedPukisItem;
import software.bernie.geckolib.model.GeoModel;

public class WrappedPukisItemModel extends GeoModel<WrappedPukisItem> {
    @Override
    public ResourceLocation getModelResource(WrappedPukisItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "geo/wrapped_pukis.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WrappedPukisItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/item/pukis_dark_wrapped.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WrappedPukisItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "animations/wrapped_pukis.animation.json");
    }
}