package net.semppi.semppis_mythical_legends_mod.item.client;

import net.minecraft.resources.ResourceLocation;
import net.semppi.semppis_mythical_legends_mod.item.custom.GeoBlockItemBase;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class GenericGeoBlockItemRenderer<T extends GeoBlockItemBase>
        extends GeoItemRenderer<T> {

    public GenericGeoBlockItemRenderer(
            ResourceLocation model,
            ResourceLocation texture,
            ResourceLocation animation
    ) {
        super(
                new GenericGeoBlockItemModel<>(
                        model,
                        texture,
                        animation
                )
        );
    }
}