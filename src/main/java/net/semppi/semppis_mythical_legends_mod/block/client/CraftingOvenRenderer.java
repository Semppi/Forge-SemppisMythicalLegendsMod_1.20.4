package net.semppi.semppis_mythical_legends_mod.block.client;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.semppi.semppis_mythical_legends_mod.block.entity.CraftingOvenBlockEntity;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class CraftingOvenRenderer
        extends GeoBlockRenderer<CraftingOvenBlockEntity> {

    public CraftingOvenRenderer(
            BlockEntityRendererProvider.Context context
    ) {
        super(new CraftingOvenModel());
    }
}