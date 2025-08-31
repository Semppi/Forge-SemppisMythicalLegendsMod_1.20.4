package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.LovelandFrogmanEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.PukisEntity;
import net.semppi.semppis_mythical_legends_mod.entity.variant.PukisVariant;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PukisRenderer extends GeoEntityRenderer<PukisEntity> {
    public PukisRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new PukisModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public ResourceLocation getTextureLocation(PukisEntity animatable) {
        PukisVariant variant = animatable.getVariant();
        if (variant == PukisVariant.GREEN) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/pukis_dark.png");
        } else if (variant == PukisVariant.BLUE) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/pukis_dark.png");
        } else if (variant == PukisVariant.RED) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/pukis_dark.png");
        } else if (variant == PukisVariant.GOLD) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/pukis_dark.png");
        } else if (variant == PukisVariant.DARK) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/pukis_dark.png");
        } else if (variant == PukisVariant.SILVER) {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/pukis_silver.png");
        }else {
            return ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/pukis_dark.png");
        }
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