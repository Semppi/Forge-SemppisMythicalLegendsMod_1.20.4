package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.MandrakeSproutlingEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MandrakeSproutlingModel extends GeoModel<MandrakeSproutlingEntity> {
    @Override
    public ResourceLocation getModelResource(MandrakeSproutlingEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "geo/mandrake_sproutling.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MandrakeSproutlingEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/mandrake_sproutling_brown.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MandrakeSproutlingEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "animations/mandrake_sproutling.animation.json");
    }

    @Override
    public void setCustomAnimations(MandrakeSproutlingEntity animatable, long instanceId, AnimationState<MandrakeSproutlingEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}