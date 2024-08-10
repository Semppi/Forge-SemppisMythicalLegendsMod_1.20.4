package net.semppi.semppis_mythical_legends_mod.item.client;

import net.semppi.semppis_mythical_legends_mod.item.custom.WendigoSkullItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class WendigoSkullItemRenderer extends GeoItemRenderer<WendigoSkullItem> {
    public WendigoSkullItemRenderer() {
        super(new WendigoSkullItemModel());
    }
}