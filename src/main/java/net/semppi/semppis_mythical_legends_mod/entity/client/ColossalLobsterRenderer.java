package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.ColossalLobsterEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.LovelandFrogmanEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ColossalLobsterRenderer extends GeoEntityRenderer<ColossalLobsterEntity> {
    public ColossalLobsterRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ColossalLobsterModel());
        this.shadowRadius = 1.0f;
    }

    @Override
    public ResourceLocation getTextureLocation(ColossalLobsterEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/lobster_orange_black.png");
    }

    @Override
    public void render(ColossalLobsterEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {
        if(entity.isBaby()) {
            poseStack.scale(0.4f, 0.4f, 0.4f);
        }

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}