package net.semppi.semppis_mythical_legends_mod.item.client;

import net.semppi.semppis_mythical_legends_mod.item.custom.PukisEggItem;
import net.semppi.semppis_mythical_legends_mod.item.custom.WendigoSkullItem;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PukisEggItemRenderer extends GeoItemRenderer<PukisEggItem> {
    public PukisEggItemRenderer() {
        super(new PukisEggItemModel());
    }
}