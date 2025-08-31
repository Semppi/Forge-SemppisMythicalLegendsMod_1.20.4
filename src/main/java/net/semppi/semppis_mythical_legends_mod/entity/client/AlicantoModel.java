package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.AlicantoEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.LovelandFrogmanEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class AlicantoModel extends GeoModel<AlicantoEntity> {
    @Override
    public ResourceLocation getModelResource(AlicantoEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "geo/alicanto.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(AlicantoEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/alicanto_gold.png");
    }

    @Override
    public ResourceLocation getAnimationResource(AlicantoEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "animations/alicanto.animation.json");
    }

    @Override
    public void setCustomAnimations(AlicantoEntity animatable, long instanceId, AnimationState<AlicantoEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}