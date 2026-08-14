package net.semppi.semppis_mythical_legends_mod.rules;

import net.minecraft.world.level.GameRules;

public final class SMLRules {
    public static final GameRules.Key<GameRules.BooleanValue> CONTINENTAL_SPAWNING =
            GameRules.register(
                    "smlContinentalSpawning",
                    GameRules.Category.SPAWNING,
                    GameRules.BooleanValue.create(false)
            );

    public static void init() {
    }
}