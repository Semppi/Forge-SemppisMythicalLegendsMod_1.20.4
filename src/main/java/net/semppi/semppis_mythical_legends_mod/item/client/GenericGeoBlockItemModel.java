package net.semppi.semppis_mythical_legends_mod.item.client;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import net.semppi.semppis_mythical_legends_mod.item.custom.GeoBlockItemBase;

public class GenericGeoBlockItemModel<T extends GeoBlockItemBase>
        extends GeoModel<T> {

    private final ResourceLocation model;
    private final ResourceLocation texture;
    private final ResourceLocation animation;

    public GenericGeoBlockItemModel(
            ResourceLocation model,
            ResourceLocation texture,
            ResourceLocation animation
    ) {
        this.model = model;
        this.texture = texture;
        this.animation = animation;
    }

    @Override
    public ResourceLocation getModelResource(T animatable) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return this.animation;
    }
}