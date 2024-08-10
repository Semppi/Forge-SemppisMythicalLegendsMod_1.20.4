package net.semppi.semppis_mythical_legends_mod.entity.variant;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public enum PukisVariant {
    GREEN(0),
    BLUE(1),
    RED(2),
    GOLD(3),
    DARK(4),
    SILVER(5),
    BLACK(6);

    private static final Random RANDOM = new Random();

    public static PukisVariant getRandomVariant() {
        double chance = RANDOM.nextDouble();
        if (chance < 0.00012) { // 0.012% for BLACK
            return BLACK;
        } else if (chance < 0.04012) { // additional 4% for SILVER
            return SILVER;
        } else if (chance < 0.08012) { // additional 4% for DARK
            return DARK;
        } else if (chance < 0.12024) { // additional 4% for GOLD
            return GOLD;
        } else { // Remaining 95.91976% split among GREEN, BLUE, RED
            return BY_ID[RANDOM.nextInt(3)]; // Randomly pick between GREEN (0), BLUE (1), RED (2)
        }
    }

    private static final PukisVariant[] BY_ID = Arrays.stream(values()).sorted(Comparator.
            comparingInt(PukisVariant::getId)).toArray(PukisVariant[]::new);
    private final int id;

    PukisVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static PukisVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}