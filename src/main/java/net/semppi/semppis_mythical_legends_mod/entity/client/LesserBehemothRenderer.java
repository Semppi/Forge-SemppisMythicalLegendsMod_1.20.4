package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.LesserBehemothEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class LesserBehemothRenderer extends GeoEntityRenderer<LesserBehemothEntity> {
    public LesserBehemothRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new LesserBehemothModel());
        this.shadowRadius = 5.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(LesserBehemothEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/behemoth_brown.png");
    }

    @Override
    public void render(LesserBehemothEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.5f, 1.5f, 1.5f);
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}