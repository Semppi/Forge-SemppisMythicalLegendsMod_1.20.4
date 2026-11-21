package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.RawheadAndBloodyBonesEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RawheadAndBloodyBonesModel extends GeoModel<RawheadAndBloodyBonesEntity> {
    @Override
    public ResourceLocation getModelResource(RawheadAndBloodyBonesEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/rawhead_and_bloody_bones.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RawheadAndBloodyBonesEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/rawhead_and_bloody_bones.png");
    }

    @Override
    public ResourceLocation getAnimationResource(RawheadAndBloodyBonesEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "animations/rawhead.animation.json");
    }

    @Override
    public void setCustomAnimations(
            RawheadAndBloodyBonesEntity animatable,
            long instanceId,
            AnimationState<RawheadAndBloodyBonesEntity> animationState
    ) {

        // ====================================================
        // HEAD ROTATION
        // ====================================================

        CoreGeoBone head =
                getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData =
                    animationState.getData(
                            DataTickets.ENTITY_MODEL_DATA
                    );

            if (entityData != null) {
                head.setRotX(
                        entityData.headPitch() * Mth.DEG_TO_RAD
                );

                head.setRotY(
                        entityData.netHeadYaw() * Mth.DEG_TO_RAD
                );
            }
        }

        // ====================================================
        // CHAINS
        // Both groups are enabled or disabled together.
        // ====================================================

        boolean showChains = animatable.hasChains();

        setBoneVisibility("chain_top", showChains);
        setBoneVisibility("chain_lower", showChains);

        // ====================================================
        // RAGS
        // Both groups are enabled or disabled together.
        // ====================================================

        boolean showRags = animatable.hasRags();

        setBoneVisibility("rag_top", showRags);
        setBoneVisibility("rag_lower", showRags);

        // ====================================================
        // HAIR
        // 0 = bald
        // 1 = hair_1
        // 2 = hair_2
        // ====================================================

        int hairVariant = animatable.getHairVariant();

        setBoneVisibility("hair_1", hairVariant == 1);
        setBoneVisibility("hair_2", hairVariant == 2);
    }

    // ========================================================
    // BONE VISIBILITY
    // ========================================================

    private void setBoneVisibility(
            String boneName,
            boolean visible
    ) {
        CoreGeoBone bone =
                getAnimationProcessor().getBone(boneName);

        if (bone != null) {
            bone.setHidden(!visible);
        }
    }
}