package net.semppi.semppis_mythical_legends_mod.block.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.semppi.semppis_mythical_legends_mod.block.entity.PukisEggBlockEntity;
import net.semppi.semppis_mythical_legends_mod.block.entity.WendigoSkullBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PukisEggRenderer extends GeoBlockRenderer<PukisEggBlockEntity> {
    public PukisEggRenderer(BlockEntityRendererProvider.Context context) {
        super(new PukisEggModel());
    }
}