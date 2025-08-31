package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.KrakenEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.SatyrEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.WendigoEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class KrakenModel extends GeoModel<KrakenEntity> {
    @Override
    public ResourceLocation getModelResource(KrakenEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "geo/kraken.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(KrakenEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/kraken_red.png");
    }

    @Override
    public ResourceLocation getAnimationResource(KrakenEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "animations/kraken.animation.json");
    }

    @Override
    public void setCustomAnimations(KrakenEntity animatable, long instanceId, AnimationState<KrakenEntity> animationState) {
        // Accessing bones by name
        CoreGeoBone grownFins = this.getAnimationProcessor().getBone("grown_midd_fins");
        CoreGeoBone youngFrontFins = this.getAnimationProcessor().getBone("young_front_fins");
        CoreGeoBone youngMiddFins = this.getAnimationProcessor().getBone("young_midd_fins");

        // Determine if the entity is a baby
        boolean isBaby = animatable.isBaby();

        // Show or hide bones based on the entity's age
        if (grownFins != null && youngFrontFins != null && youngMiddFins != null) {
            grownFins.setHidden(isBaby);  // Hide grown fins if baby
            youngFrontFins.setHidden(!isBaby);  // Hide young front fins if not baby
            youngMiddFins.setHidden(!isBaby);   // Hide young midd fins if not baby
        }
    }
}