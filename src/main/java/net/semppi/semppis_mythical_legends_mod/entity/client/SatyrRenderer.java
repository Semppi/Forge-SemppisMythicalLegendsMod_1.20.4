package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.PukisEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.SatyrEntity;
import net.semppi.semppis_mythical_legends_mod.entity.variant.PukisVariant;
import net.semppi.semppis_mythical_legends_mod.entity.variant.SatyrVariant;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SatyrRenderer extends GeoEntityRenderer<SatyrEntity> {
    public SatyrRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SatyrModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public ResourceLocation getTextureLocation(SatyrEntity animatable) {
        SatyrVariant variant = animatable.getVariant();
        if (variant == SatyrVariant.BLACK) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/satyr_black.png");
        } else if (variant == SatyrVariant.BROWN) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/satyr_brown.png");
        } else if (variant == SatyrVariant.CARAMEL) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/satyr_caramel.png");
        } else if (variant == SatyrVariant.BLONDE) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/satyr_blonde.png");
        } else if (variant == SatyrVariant.COPPER_RED) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/satyr_copper_red.png");
        }else {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/satyr_albino.png");
        }
    }

    @Override
    public void render(SatyrEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}