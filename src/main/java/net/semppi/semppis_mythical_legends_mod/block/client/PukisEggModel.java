package net.semppi.semppis_mythical_legends_mod.block.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.entity.PukisEggBlockEntity;
import net.semppi.semppis_mythical_legends_mod.block.entity.WendigoSkullBlockEntity;
import software.bernie.geckolib.model.GeoModel;

public class PukisEggModel extends GeoModel<PukisEggBlockEntity> {
    @Override
    public ResourceLocation getModelResource(PukisEggBlockEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/pukis_egg.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PukisEggBlockEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/block/pukis_egg_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PukisEggBlockEntity animatable) {
        return null; // No animation file since it's not animated
    }
}