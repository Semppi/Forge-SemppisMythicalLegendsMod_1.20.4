package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.PukisEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.SatyrEntity;
import net.semppi.semppis_mythical_legends_mod.entity.variant.PukisVariant;
import net.semppi.semppis_mythical_legends_mod.entity.variant.SatyrVariant;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SatyrModel extends GeoModel<SatyrEntity> {
    @Override
    public ResourceLocation getModelResource(SatyrEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/satyr.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SatyrEntity entity) {
        SatyrVariant variant = entity.getVariant();
        String variantName = variant.name().toLowerCase();
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/satyr_" + variantName + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(SatyrEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "animations/satyr.animation.json");
    }

    @Override
    public void setCustomAnimations(SatyrEntity animatable, long instanceId, AnimationState<SatyrEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
        // Accessing bones by name
        CoreGeoBone grownHorns = this.getAnimationProcessor().getBone("grown_horns");
        CoreGeoBone babyHorns = this.getAnimationProcessor().getBone("baby_horns");
        CoreGeoBone grownHair = this.getAnimationProcessor().getBone("grown_hair");

        // Determine if the entity is a baby
        boolean isBaby = animatable.isBaby();

        // Show or hide bones based on the entity's age
        if (grownHorns != null && babyHorns != null && grownHair != null) {
            grownHorns.setHidden(isBaby);  // Hide grown horns if baby
            babyHorns.setHidden(!isBaby);  // Hide baby horns if not baby
            grownHair.setHidden(isBaby);   // Hide grown hair if baby
        }
    }
}