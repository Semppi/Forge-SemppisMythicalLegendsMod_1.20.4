package net.semppi.semppis_mythical_legends_mod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.AlicantoEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.ProtoWendigoEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.WendigoEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class ProtoWendigoRenderer extends GeoEntityRenderer<ProtoWendigoEntity> {
    public ProtoWendigoRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new ProtoWendigoModel());
        this.shadowRadius = 0.4f;
    }

    @Override
    public ResourceLocation getTextureLocation(ProtoWendigoEntity animatable) {
        return new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "textures/entity/proto_wendigo_gray.png");
    }

    @Override
    public void render(ProtoWendigoEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}