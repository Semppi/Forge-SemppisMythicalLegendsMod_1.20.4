package net.semppi.semppis_mythical_legends_mod.entity.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.MandrakeEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MandrakeModel extends GeoModel<MandrakeEntity> {
    @Override
    public ResourceLocation getModelResource(MandrakeEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "geo/mandrake.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MandrakeEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/mandrake_brown.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MandrakeEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "animations/mandrake.animation.json");
    }

    @Override
    public void setCustomAnimations(
            MandrakeEntity animatable,
            long instanceId,
            AnimationState<MandrakeEntity> animationState
    ) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData =
                    animationState.getData(DataTickets.ENTITY_MODEL_DATA);

            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }

        /*
         * FLOWERS
         *
         * Flowers are hidden for the current default reproductive stage.
         * Later we can make these visible during spring/flowering.
         */
        setBoneHidden("l_stem_flowers", true);
        setBoneHidden("l_stem_flowers2", true);
        setBoneHidden("flower_1", true);
        setBoneHidden("flower_2", true);

        setBoneHidden("r_stem_flowers", true);
        setBoneHidden("r_stem_flowers2", true);
        setBoneHidden("flower_3", true);
        setBoneHidden("flower_4", true);

        /*
         * BERRIES
         *
         * Visible while the Mandrake has berries available.
         * Hidden after the player harvests them.
         */
        boolean hideBerries = !animatable.hasBerries();

        setBoneHidden("l_stem_berries", hideBerries);
        setBoneHidden("l_stem_berries2", hideBerries);
        setBoneHidden("berrie_1", hideBerries);
        setBoneHidden("berrie_2", hideBerries);

        setBoneHidden("r_stem_berries", hideBerries);
        setBoneHidden("r_stem_berries2", hideBerries);
        setBoneHidden("berrie_3", hideBerries);
        setBoneHidden("berrie_4", hideBerries);

        boolean female = animatable.isFemale();

        setBoneHidden("hair_male", female);
        setBoneHidden("hair_female", !female);
        setBoneHidden("chest", !female);
    }
    private void setBoneHidden(String boneName, boolean hidden) {
        CoreGeoBone bone =
                getAnimationProcessor().getBone(boneName);

        if (bone != null) {
            bone.setHidden(hidden);
        }
    }
}