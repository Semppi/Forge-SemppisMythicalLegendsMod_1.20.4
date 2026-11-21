package net.semppi.semppis_mythical_legends_mod.client;

import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SMLFieldGuideAnimatable implements GeoAnimatable {

    /*
     * The complete 3D opening sequence.
     *
     * 1. Play the woosh once.
     * 2. Play the opening animation.
     * 3. Hold the final open-book pose.
     */
    private static final RawAnimation BOOK_SEQUENCE =
            RawAnimation.begin()
                    .then(
                            "book_woosh",
                            Animation.LoopType.PLAY_ONCE
                    )
                    .then(
                            "book_open",
                            Animation.LoopType.HOLD_ON_LAST_FRAME
                    );

    private final AnimatableInstanceCache cache =
            GeckoLibUtil.createInstanceCache(this);

    @Override
    public void registerControllers(
            AnimatableManager.ControllerRegistrar controllers
    ) {
        controllers.add(
                new AnimationController<>(
                        this,
                        "field_guide_controller",
                        0,
                        animationState -> {

                            animationState.getController()
                                    .setAnimation(BOOK_SEQUENCE);

                            return PlayState.CONTINUE;
                        }
                )
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object relatedObject) {
        return System.nanoTime() / 50_000_000.0D;
    }
}