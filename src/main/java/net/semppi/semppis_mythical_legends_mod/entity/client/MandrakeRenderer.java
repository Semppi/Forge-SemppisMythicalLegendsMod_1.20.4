package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.semppi.semppis_mythical_legends_mod.entity.custom.MandrakeEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MandrakeRenderer extends GeoEntityRenderer<MandrakeEntity> {
    public MandrakeRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MandrakeModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(MandrakeEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/mandrake_brown.png");
    }

    @Override
    public void render(
            MandrakeEntity entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight
    ) {
        if (entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        int mandrakeLight = packedLight;

        if (entity.isRooted()) {
            BlockPos lightSamplePos =
                    entity.blockPosition().above(2);

            mandrakeLight = LevelRenderer.getLightColor(
                    entity.level(),
                    lightSamplePos
            );

            poseStack.translate(0.0D, 0.0625D, 0.0D);
        }

        super.render(
                entity,
                entityYaw,
                partialTick,
                poseStack,
                bufferSource,
                mandrakeLight
        );
    }
}