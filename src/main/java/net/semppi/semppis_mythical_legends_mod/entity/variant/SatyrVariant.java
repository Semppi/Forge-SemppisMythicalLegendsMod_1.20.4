package net.semppi.semppis_mythical_legends_mod.entity.variant;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public enum SatyrVariant {
    BLACK(0),
    BROWN(1),
    CARAMEL(2),
    COPPER_RED(3),
    BLONDE(4),
    ALBINO(5);

    private static final Random RANDOM = new Random();

    public static SatyrVariant getRandomVariant() {
        double chance = RANDOM.nextDouble();
        if (chance < 0.00012) { // 0.012% for ALBINO
            return ALBINO;
        } else if (chance < 0.04012) { // additional 4% for BLONDE
            return BLONDE;
        } else if (chance < 0.08024) { // additional 4% for COPPER_RED
            return COPPER_RED;
        } else { // Remaining 95.91988% split among BLACK, BROWN, CARAMEL
            return BY_ID[RANDOM.nextInt(3)]; // Randomly pick between BLACK (0), BROWN (1), CARAMEL (2)
        }
    }

    private static final SatyrVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(SatyrVariant::getId)).toArray(SatyrVariant[]::new);
    private final int id;

    SatyrVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static SatyrVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}