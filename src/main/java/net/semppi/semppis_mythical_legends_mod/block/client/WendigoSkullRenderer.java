package net.semppi.semppis_mythical_legends_mod.block.client;

import net.semppi.semppis_mythical_legends_mod.block.entity.WendigoSkullBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class WendigoSkullRenderer extends GeoBlockRenderer<WendigoSkullBlockEntity> {
    public WendigoSkullRenderer(BlockEntityRendererProvider.Context context) {
        super(new WendigoSkullModel());
    }
}