package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.MandrakeSproutlingEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MandrakeSproutlingRenderer extends GeoEntityRenderer<MandrakeSproutlingEntity> {
    public MandrakeSproutlingRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MandrakeSproutlingModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public ResourceLocation getTextureLocation(MandrakeSproutlingEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/mandrake_sproutling_brown.png");
    }

    @Override
    public void render(MandrakeSproutlingEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}