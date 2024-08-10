package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.WendigoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WendigoModel extends GeoModel<WendigoEntity> {
    @Override
    public ResourceLocation getModelResource(WendigoEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/wendigo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WendigoEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/wendigo_brown.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WendigoEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "animations/wendigo.animation.json");
    }

    @Override
    public void setCustomAnimations(WendigoEntity animatable, long instanceId, AnimationState<WendigoEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}