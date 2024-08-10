package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.ColossalLobsterEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.LovelandFrogmanEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ColossalLobsterModel extends GeoModel<ColossalLobsterEntity> {
    @Override
    public ResourceLocation getModelResource(ColossalLobsterEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/colossal_lobster.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ColossalLobsterEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/lobster_orange_black.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ColossalLobsterEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "animations/colossal_lobster.animation.json");
    }
}