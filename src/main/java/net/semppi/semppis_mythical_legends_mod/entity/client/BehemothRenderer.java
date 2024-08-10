package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.BehemothEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.LovelandFrogmanEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BehemothRenderer extends GeoEntityRenderer<BehemothEntity> {
    public BehemothRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BehemothModel());
        this.shadowRadius = 5.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(BehemothEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/behemoth_brown.png");
    }

    @Override
    public void render(BehemothEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.5f, 1.5f, 1.5f);
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}