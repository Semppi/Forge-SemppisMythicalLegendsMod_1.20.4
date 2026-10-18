package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.entity.custom.PukisEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PukisRenderer extends GeoEntityRenderer<PukisEntity> {
    public PukisRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PukisModel());
        this.shadowRadius = 0.3f;
    }

    @Override
    public ResourceLocation getTextureLocation(PukisEntity animatable) {
        return PukisTextureManager.getTexture(animatable);
    }

    @Override
    public void render(PukisEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}