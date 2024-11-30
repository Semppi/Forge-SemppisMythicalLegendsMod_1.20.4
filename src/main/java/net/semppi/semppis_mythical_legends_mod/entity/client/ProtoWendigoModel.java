package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.AlicantoEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.ProtoWendigoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class ProtoWendigoModel extends GeoModel<ProtoWendigoEntity> {
    @Override
    public ResourceLocation getModelResource(ProtoWendigoEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/proto_wendigo.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ProtoWendigoEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/proto_wendigo_gray.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ProtoWendigoEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "animations/model.animation.json");
    }

    @Override
    public void setCustomAnimations(ProtoWendigoEntity animatable, long instanceId, AnimationState<ProtoWendigoEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}