package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.LovelandFrogmanEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.PukisEntity;
import net.semppi.semppis_mythical_legends_mod.entity.variant.PukisVariant;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PukisModel extends GeoModel<PukisEntity> {
    @Override
    public ResourceLocation getModelResource(PukisEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/pukis.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PukisEntity entity) {
        PukisVariant variant = entity.getVariant();
        String variantName = variant.name().toLowerCase();
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/pukis_" + variantName + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(PukisEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "animations/pukis.animation.json");
    }

    @Override
    public void setCustomAnimations(PukisEntity animatable, long instanceId, AnimationState<PukisEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}