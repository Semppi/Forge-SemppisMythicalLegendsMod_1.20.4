package net.semppi.semppis_mythical_legends_mod.entity.variant;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public enum WendigoVariant {
    BROWN(0),
    GRAY(1),
    WHITE(2),
    SOUL(3),
    BLACK(4);

    private static final Random RANDOM = new Random();

    public static WendigoVariant getRandomVariant() {
        double chance = RANDOM.nextDouble();
        if (chance < 0.04) { // 4% chance for SOUL
            return SOUL;
        } else if (chance < 0.08) { // additional 4% chance for BLACK
            return BLACK;
        } else { // Remaining 92% chance split among BROWN, GRAY, WHITE
            return BY_ID[RANDOM.nextInt(3)]; // Randomly pick between BROWN (0), GRAY (1), WHITE (2)
        }
    }

    private static final WendigoVariant[] BY_ID = Arrays.stream(values())
            .sorted(Comparator.comparingInt(WendigoVariant::getId))
            .toArray(WendigoVariant[]::new);
    private final int id;

    WendigoVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static WendigoVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}