package net.semppi.semppis_mythical_legends_mod.block.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.semppi.semppis_mythical_legends_mod.block.entity.MediumHumanoidDropBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MediumHumanoidDropRenderer
        extends GeoBlockRenderer<MediumHumanoidDropBlockEntity> {

    public MediumHumanoidDropRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        super(new MediumHumanoidDropModel());
    }
}