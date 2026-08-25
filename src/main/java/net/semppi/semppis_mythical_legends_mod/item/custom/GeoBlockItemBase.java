package net.semppi.semppis_mythical_legends_mod.item.custom;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public abstract class GeoBlockItemBase
        extends BlockItem
        implements GeoItem {

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    public GeoBlockItemBase(
            Block block,
            Item.Properties properties
    ) {
        super(block, properties);
    }

    /*
     * Forge 1.20.4 custom item renderer hook.
     */
    @Override
    public void initializeClient(
            Consumer<IClientItemExtensions> consumer
    ) {
        consumer.accept(
                new IClientItemExtensions() {

                    private BlockEntityWithoutLevelRenderer renderer;

                    @Override
                    public BlockEntityWithoutLevelRenderer getCustomRenderer() {

                        if (this.renderer == null) {
                            this.renderer =
                                    GeoBlockItemBase.this.createItemRenderer();
                        }

                        return this.renderer;
                    }
                }
        );
    }

    /*
     * No item animations are needed yet.
     *
     * This method is still required because GeoItem extends
     * GeoAnimatable.
     */
    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
        // No animations yet.
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    /*
     * Individual Geo block items provide the renderer
     * they want to use.
     */
    protected abstract BlockEntityWithoutLevelRenderer createItemRenderer();
}