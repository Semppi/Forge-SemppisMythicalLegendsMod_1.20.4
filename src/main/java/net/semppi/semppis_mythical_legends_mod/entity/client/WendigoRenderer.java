package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.WendigoEntity;
import net.semppi.semppis_mythical_legends_mod.entity.variant.WendigoVariant;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WendigoRenderer extends GeoEntityRenderer<WendigoEntity> {
    public WendigoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WendigoModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public ResourceLocation getTextureLocation(WendigoEntity animatable) {
        WendigoVariant variant = animatable.getVariant();
        if (variant == WendigoVariant.BROWN) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/wendigo_brown.png");
        } else if (variant == WendigoVariant.GRAY) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/wendigo_gray.png");
        } else if (variant == WendigoVariant.WHITE) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/wendigo_white.png");
        } else if (variant == WendigoVariant.SOUL) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/wendigo_gray.png");
        }else {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/wendigo_gray.png");
        }
    }

    @Override
    public void render(WendigoEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}