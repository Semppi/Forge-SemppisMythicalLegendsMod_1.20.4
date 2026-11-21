package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.RawheadAndBloodyBonesEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class RawheadAndBloodyBonesRenderer extends GeoEntityRenderer<RawheadAndBloodyBonesEntity> {
    public RawheadAndBloodyBonesRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new RawheadAndBloodyBonesModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public ResourceLocation getTextureLocation(RawheadAndBloodyBonesEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/rawhead_and_bloody_bones.png");
    }

    @Override
    public void render(RawheadAndBloodyBonesEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}