package net.semppi.semppis_mythical_legends_mod.item.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.item.custom.PukisEggItem;
import net.semppi.semppis_mythical_legends_mod.item.custom.WendigoSkullItem;
import software.bernie.geckolib.model.GeoModel;

public class PukisEggItemModel extends GeoModel<PukisEggItem> {
    @Override
    public ResourceLocation getModelResource(PukisEggItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "geo/pukis_egg.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PukisEggItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/block/pukis_egg_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PukisEggItem animatable) {
        return null; // No animation file since it's not animated
    }
}