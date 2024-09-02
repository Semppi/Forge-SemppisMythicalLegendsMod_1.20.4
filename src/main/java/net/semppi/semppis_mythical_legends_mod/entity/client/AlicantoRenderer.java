package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.AlicantoEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.LovelandFrogmanEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class AlicantoRenderer extends GeoEntityRenderer<AlicantoEntity> {
    public AlicantoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AlicantoModel());
        this.shadowRadius = 0.5f;
    }

    @Override
    public ResourceLocation getTextureLocation(AlicantoEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/alicanto_gold.png");
    }

    @Override
    public void render(AlicantoEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}