package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * Explicit 0-5 suitability ratings for vanilla surface biomes and SML's
 * continent directions. These values describe placement compatibility, not
 * creature spawn rates. Unlisted/modded biomes deliberately fall back to the
 * descriptive climate rules in {@link TagRules}.
 */
public final class BiomeRegionCompatibility {
    public static final int FORBIDDEN = 0;

    private static final Map<ResourceLocation, Map<Area, Integer>> RATINGS =
            new HashMap<>();

    private BiomeRegionCompatibility() {}

    static {
        allOrdinaryFive("forest", 0);
        allOrdinaryFive("flower_forest", 0);
        birch("birch_forest");
        birch("old_growth_birch_forest");
        taiga("taiga", 1);
        oldGrowthSpruceTaiga();
        allOrdinaryFive("dark_forest", 0);
        oldGrowthPineTaiga();
        taiga("snowy_taiga", 2);
        jungle("jungle");
        jungle("sparse_jungle");
        bambooJungle();
        allOrdinaryFive("swamp", 0);
        mangroveSwamp();
        allOrdinaryFive("plains", 0);
        allOrdinaryFive("sunflower_plains", 0);
        frozenBarren("snowy_plains");
        frozenBarren("ice_spikes");
        desert();
        hotDry("savanna");
        hotDry("savanna_plateau");
        hotDry("windswept_savanna");
        hotDry("badlands");
        hotDry("wooded_badlands");
        hotDry("eroded_badlands");
        allOrdinaryFive("meadow", 0);
        cherryGrove();
        frozenBarren("grove");
        allOrdinaryFive("windswept_hills", 1);
        allOrdinaryFive("windswept_gravelly_hills", 1);
        allOrdinaryFive("windswept_forest", 0);
        allOrdinaryFive("mushroom_fields", 3);
        validateCompleteTable();
    }

    public static OptionalInt rating(
            Continent continent, SubDir direction,
            ResourceLocation biomeId) {
        Map<Area, Integer> values = RATINGS.get(biomeId);
        if (values == null) return OptionalInt.empty();
        Area area = Area.of(continent, direction);
        Integer value = values.get(area);
        return value == null ? OptionalInt.empty()
                : OptionalInt.of(value);
    }

    /**
     * Converts the authored 0-5 scale into assignment utility. Poor ratings
     * are deliberately more costly than equally distant good ratings are
     * rewarding, matching the user's "please try something else" semantics.
     */
    public static int utility(int rating) {
        return switch (rating) {
            case 5 -> 6;
            case 4 -> 3;
            case 3 -> 1;
            case 2 -> -2;
            case 1 -> -6;
            case 0 -> -12;
            default -> throw new IllegalArgumentException(
                    "Compatibility rating must be between 0 and 5"
            );
        };
    }

    private static void allOrdinaryFive(String biome, int pole) {
        put(biome, 5, Area.ordinary());
        put(biome, pole, Area.POLE);
    }

    private static void birch(String biome) {
        put(biome, 5,
                Area.EUR_N, Area.EUR_E, Area.EUR_S, Area.EUR_W, Area.EUR_C,
                Area.ASI_N, Area.ASI_E, Area.ASI_S, Area.ASI_W, Area.ASI_C,
                Area.AFR_N,
                Area.NAM_N, Area.NAM_E, Area.NAM_W, Area.NAM_C);
        put(biome, 4,
                Area.AFR_E, Area.AFR_S, Area.AFR_W, Area.AFR_C,
                Area.AUS_N, Area.AUS_E, Area.AUS_S, Area.AUS_W, Area.AUS_C,
                Area.NAM_S,
                Area.SAM_N, Area.SAM_E, Area.SAM_S, Area.SAM_W, Area.SAM_C);
        put(biome, 0, Area.POLE);
    }

    private static void taiga(String biome, int pole) {
        put(biome, 5,
                Area.EUR_N, Area.EUR_E, Area.ASI_N,
                Area.NAM_N, Area.NAM_E, Area.NAM_C);
        put(biome, 4,
                Area.EUR_W, Area.EUR_C, Area.ASI_E, Area.ASI_C);
        put(biome, 3,
                Area.EUR_S, Area.ASI_S, Area.ASI_W, Area.AFR_N,
                Area.AUS_E, Area.AUS_S, Area.AUS_C, Area.SAM_S);
        put(biome, 2,
                Area.AFR_E, Area.AFR_W, Area.NAM_S, Area.NAM_W);
        put(biome, 1,
                Area.AFR_S, Area.AFR_C, Area.AUS_N, Area.AUS_W,
                Area.SAM_N, Area.SAM_E, Area.SAM_W, Area.SAM_C);
        put(biome, pole, Area.POLE);
    }

    private static void oldGrowthSpruceTaiga() {
        String biome = "old_growth_spruce_taiga";
        put(biome, 5,
                Area.EUR_N, Area.EUR_E, Area.EUR_S, Area.EUR_W, Area.EUR_C,
                Area.ASI_N, Area.ASI_E, Area.ASI_W, Area.ASI_C,
                Area.NAM_N, Area.NAM_E, Area.NAM_S, Area.NAM_W, Area.NAM_C);
        put(biome, 4, Area.ASI_S, Area.SAM_N);
        put(biome, 3,
                Area.AFR_N, Area.AFR_E, Area.AFR_S, Area.AFR_W, Area.AFR_C,
                Area.AUS_S, Area.SAM_S, Area.SAM_W);
        put(biome, 2,
                Area.AUS_N, Area.AUS_E, Area.AUS_W, Area.AUS_C,
                Area.SAM_E, Area.SAM_C);
        put(biome, 0, Area.POLE);
    }

    private static void oldGrowthPineTaiga() {
        String biome = "old_growth_pine_taiga";
        put(biome, 5,
                Area.EUR_N, Area.EUR_E, Area.EUR_S, Area.EUR_W, Area.EUR_C,
                Area.ASI_N, Area.ASI_E, Area.ASI_S, Area.ASI_W, Area.ASI_C,
                Area.AFR_N, Area.AFR_W,
                Area.NAM_N, Area.NAM_E, Area.NAM_S, Area.NAM_W, Area.NAM_C,
                Area.SAM_N, Area.SAM_S);
        put(biome, 4,
                Area.AFR_E, Area.AFR_C,
                Area.SAM_E, Area.SAM_W, Area.SAM_C);
        put(biome, 3,
                Area.AFR_S,
                Area.AUS_N, Area.AUS_E, Area.AUS_S, Area.AUS_W, Area.AUS_C);
        put(biome, 0, Area.POLE);
    }

    private static void jungle(String biome) {
        put(biome, 5,
                Area.EUR_E,
                Area.ASI_E, Area.ASI_S, Area.ASI_W, Area.ASI_C,
                Area.AFR_E, Area.AFR_S, Area.AFR_W, Area.AFR_C,
                Area.AUS_N, Area.AUS_E, Area.AUS_S, Area.AUS_W, Area.AUS_C,
                Area.NAM_N, Area.NAM_E, Area.NAM_S, Area.NAM_W,
                Area.SAM_N, Area.SAM_E, Area.SAM_S, Area.SAM_W, Area.SAM_C);
        put(biome, 4, Area.EUR_S, Area.AFR_N, Area.NAM_C);
        put(biome, 0,
                Area.EUR_N, Area.EUR_W, Area.EUR_C, Area.ASI_N, Area.POLE);
    }

    private static void bambooJungle() {
        String biome = "bamboo_jungle";
        put(biome, 5,
                Area.ASI_E, Area.ASI_S, Area.ASI_C,
                Area.AFR_E, Area.AFR_S, Area.AFR_W, Area.AFR_C,
                Area.AUS_N, Area.AUS_E, Area.AUS_C,
                Area.NAM_E, Area.NAM_S, Area.NAM_W, Area.NAM_C,
                Area.SAM_N, Area.SAM_E, Area.SAM_S, Area.SAM_W, Area.SAM_C);
        put(biome, 4, Area.ASI_W);
        put(biome, 3, Area.ASI_N, Area.AUS_S, Area.AUS_W);
        put(biome, 2,
                Area.EUR_S, Area.EUR_W, Area.EUR_C, Area.AFR_N);
        put(biome, 1, Area.EUR_E);
        put(biome, 0, Area.EUR_N, Area.NAM_N, Area.POLE);
    }

    private static void mangroveSwamp() {
        String biome = "mangrove_swamp";
        put(biome, 5,
                Area.ASI_E, Area.ASI_S, Area.ASI_W, Area.ASI_C,
                Area.AFR_N, Area.AFR_E, Area.AFR_S, Area.AFR_W, Area.AFR_C,
                Area.AUS_N, Area.AUS_E, Area.AUS_S, Area.AUS_W, Area.AUS_C,
                Area.NAM_S,
                Area.SAM_N, Area.SAM_E, Area.SAM_W);
        put(biome, 3, Area.NAM_W, Area.NAM_E);
        put(biome, 2, Area.EUR_S, Area.SAM_S, Area.SAM_C);
        put(biome, 1, Area.EUR_W, Area.EUR_C, Area.NAM_C);
        put(biome, 0,
                Area.EUR_N, Area.EUR_E, Area.ASI_N, Area.NAM_N, Area.POLE);
    }

    private static void frozenBarren(String biome) {
        put(biome, 5,
                Area.EUR_N, Area.EUR_E, Area.EUR_W, Area.EUR_C,
                Area.ASI_N, Area.ASI_E,
                Area.NAM_N, Area.NAM_E, Area.POLE);
        put(biome, 4,
                Area.EUR_S, Area.ASI_C, Area.AUS_S, Area.SAM_S);
        put(biome, 3, Area.AUS_C, Area.NAM_C);
        put(biome, 2,
                Area.ASI_S, Area.ASI_W, Area.AUS_E, Area.SAM_C);
        put(biome, 1, Area.NAM_W, Area.SAM_W);
        put(biome, 0,
                Area.AFR_N, Area.AFR_E, Area.AFR_S, Area.AFR_W, Area.AFR_C,
                Area.AUS_N, Area.AUS_W, Area.NAM_S,
                Area.SAM_N, Area.SAM_E);
    }

    private static void desert() {
        String biome = "desert";
        put(biome, 5,
                Area.ASI_S, Area.ASI_W, Area.ASI_C,
                Area.AFR_N, Area.AFR_E, Area.AFR_S, Area.AFR_W, Area.AFR_C,
                Area.AUS_E, Area.AUS_W, Area.AUS_C,
                Area.NAM_S, Area.NAM_W, Area.NAM_C,
                Area.SAM_N, Area.SAM_E, Area.SAM_S, Area.SAM_W);
        put(biome, 4, Area.EUR_E);
        put(biome, 3, Area.EUR_S, Area.ASI_E, Area.NAM_E, Area.SAM_C);
        put(biome, 2, Area.AUS_N, Area.NAM_N);
        put(biome, 1, Area.EUR_W, Area.ASI_N, Area.AUS_S);
        put(biome, 0, Area.EUR_N, Area.EUR_C, Area.POLE);
    }

    private static void hotDry(String biome) {
        put(biome, 5,
                Area.ASI_S, Area.ASI_W,
                Area.AFR_E, Area.AFR_S, Area.AFR_W, Area.AFR_C,
                Area.AUS_N, Area.AUS_E, Area.AUS_C,
                Area.NAM_S,
                Area.SAM_N, Area.SAM_E, Area.SAM_S, Area.SAM_W, Area.SAM_C);
        put(biome, 4, Area.ASI_C, Area.AFR_N, Area.NAM_W);
        put(biome, 3,
                Area.EUR_E, Area.ASI_E, Area.AUS_W,
                Area.NAM_E, Area.NAM_C);
        put(biome, 2, Area.EUR_S, Area.AUS_S);
        put(biome, 1, Area.EUR_W);
        put(biome, 0,
                Area.EUR_N, Area.EUR_C, Area.ASI_N, Area.NAM_N, Area.POLE);
    }

    private static void cherryGrove() {
        String biome = "cherry_grove";
        put(biome, 5,
                Area.ASI_E, Area.ASI_S, Area.ASI_C,
                Area.NAM_E, Area.NAM_C);
        put(biome, 4,
                Area.EUR_E, Area.EUR_S, Area.ASI_W,
                Area.AFR_W, Area.AFR_C,
                Area.AUS_E, Area.AUS_S,
                Area.NAM_N, Area.NAM_W);
        put(biome, 3,
                Area.EUR_N, Area.EUR_W, Area.EUR_C, Area.ASI_N,
                Area.AFR_E, Area.AFR_S,
                Area.AUS_N, Area.AUS_W, Area.AUS_C,
                Area.NAM_S,
                Area.SAM_N, Area.SAM_E, Area.SAM_C);
        put(biome, 2, Area.AFR_N, Area.SAM_S, Area.SAM_W);
        put(biome, 0, Area.POLE);
    }

    private static void put(String biome, int rating, Area... areas) {
        ResourceLocation id = new ResourceLocation("minecraft", biome);
        Map<Area, Integer> values = RATINGS.computeIfAbsent(
                id, ignored -> new EnumMap<>(Area.class)
        );
        for (Area area : areas) {
            Integer previous = values.put(area, rating);
            if (previous != null && previous != rating) {
                throw new IllegalStateException(
                        biome + " has duplicate rating for " + area
                );
            }
        }
    }

    private static void validateCompleteTable() {
        int expectedAreas = Area.values().length;
        for (Map.Entry<ResourceLocation, Map<Area, Integer>> entry
                : RATINGS.entrySet()) {
            if (entry.getValue().size() != expectedAreas) {
                throw new IllegalStateException(
                        entry.getKey() + " has " + entry.getValue().size()
                                + "/" + expectedAreas
                                + " compatibility ratings"
                );
            }
            for (int rating : entry.getValue().values()) {
                if (rating < 0 || rating > 5) {
                    throw new IllegalStateException(
                            entry.getKey() + " has invalid rating " + rating
                    );
                }
            }
        }
    }

    private enum Area {
        EUR_N(Continent.EUROPE, SubDir.NORTH),
        EUR_E(Continent.EUROPE, SubDir.EAST),
        EUR_S(Continent.EUROPE, SubDir.SOUTH),
        EUR_W(Continent.EUROPE, SubDir.WEST),
        EUR_C(Continent.EUROPE, SubDir.CENTRAL),
        ASI_N(Continent.ASIA, SubDir.NORTH),
        ASI_E(Continent.ASIA, SubDir.EAST),
        ASI_S(Continent.ASIA, SubDir.SOUTH),
        ASI_W(Continent.ASIA, SubDir.WEST),
        ASI_C(Continent.ASIA, SubDir.CENTRAL),
        AFR_N(Continent.AFRICA, SubDir.NORTH),
        AFR_E(Continent.AFRICA, SubDir.EAST),
        AFR_S(Continent.AFRICA, SubDir.SOUTH),
        AFR_W(Continent.AFRICA, SubDir.WEST),
        AFR_C(Continent.AFRICA, SubDir.CENTRAL),
        AUS_N(Continent.AUSTRALIA, SubDir.NORTH),
        AUS_E(Continent.AUSTRALIA, SubDir.EAST),
        AUS_S(Continent.AUSTRALIA, SubDir.SOUTH),
        AUS_W(Continent.AUSTRALIA, SubDir.WEST),
        AUS_C(Continent.AUSTRALIA, SubDir.CENTRAL),
        NAM_N(Continent.N_AMERICA, SubDir.NORTH),
        NAM_E(Continent.N_AMERICA, SubDir.EAST),
        NAM_S(Continent.N_AMERICA, SubDir.SOUTH),
        NAM_W(Continent.N_AMERICA, SubDir.WEST),
        NAM_C(Continent.N_AMERICA, SubDir.CENTRAL),
        SAM_N(Continent.S_AMERICA, SubDir.NORTH),
        SAM_E(Continent.S_AMERICA, SubDir.EAST),
        SAM_S(Continent.S_AMERICA, SubDir.SOUTH),
        SAM_W(Continent.S_AMERICA, SubDir.WEST),
        SAM_C(Continent.S_AMERICA, SubDir.CENTRAL),
        POLE(Continent.ANTARCTICA, SubDir.CENTRAL);

        private final Continent continent;
        private final SubDir direction;

        Area(Continent continent, SubDir direction) {
            this.continent = continent;
            this.direction = direction;
        }

        private static Area of(Continent continent, SubDir direction) {
            if (continent == Continent.ANTARCTICA) return POLE;
            for (Area area : values()) {
                if (area.continent == continent
                        && area.direction == direction) {
                    return area;
                }
            }
            throw new IllegalArgumentException(
                    "No compatibility area for " + continent + "/" + direction
            );
        }

        private static Area[] ordinary() {
            Area[] values = values();
            Area[] ordinary = new Area[values.length - 1];
            System.arraycopy(values, 0, ordinary, 0, ordinary.length);
            return ordinary;
        }
    }
}
