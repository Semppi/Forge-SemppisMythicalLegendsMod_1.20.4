package net.semppi.semppis_mythical_legends_mod.item.client;

import net.semppi.semppis_mythical_legends_mod.item.custom.WrappedPukisItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WrappedPukisItemRenderer extends GeoItemRenderer<WrappedPukisItem> {
    public WrappedPukisItemRenderer() {
        super(new WrappedPukisItemModel());
    }
}