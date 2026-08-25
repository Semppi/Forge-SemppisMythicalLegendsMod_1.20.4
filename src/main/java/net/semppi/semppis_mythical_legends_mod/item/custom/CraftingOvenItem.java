package net.semppi.semppis_mythical_legends_mod.item.custom;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.item.client.GenericGeoBlockItemRenderer;

public class CraftingOvenItem
        extends GeoBlockItemBase {

    public CraftingOvenItem(
            Block block,
            Item.Properties properties
    ) {
        super(block, properties);
    }

    @Override
    protected BlockEntityWithoutLevelRenderer createItemRenderer() {

        return new GenericGeoBlockItemRenderer<>(
                new ResourceLocation(
                        SemppisMythicalLegendsMod.MOD_ID,
                        "geo/crafting_oven.geo.json"
                ),
                new ResourceLocation(
                        SemppisMythicalLegendsMod.MOD_ID,
                        "textures/block/crafting_oven.png"
                ),
                null
        );
    }
}