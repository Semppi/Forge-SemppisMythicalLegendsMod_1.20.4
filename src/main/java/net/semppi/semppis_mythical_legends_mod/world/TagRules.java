package net.semppi.semppis_mythical_legends_mod.world;

import java.util.*;
import net.minecraft.resources.ResourceLocation;

public final class TagRules {
    private TagRules() {}

    /**
     * Soft biome guidance for continental selection.
     *
     * <p>Only {@link #STRONGLY_UNSUITABLE} is a hard rejection. Every other
     * value is evidence for the future cluster sampler, not permission for an
     * individual biome to replace a continent at one coordinate.</p>
     */
    public enum Affinity {
        EXCELLENT_MATCH(3),
        GOOD_MATCH(1),
        NEUTRAL(0),
        UNUSUAL(-1),
        STRONGLY_UNSUITABLE(-4);

        private final int score;

        Affinity(int score) {
            this.score = score;
        }

        public int score() {
            return score;
        }
    }

    private enum ClimateFamily {
        POLAR,
        BOREAL,
        TEMPERATE,
        ARID,
        SAVANNA,
        TROPICAL,
        WETLAND,
        COLD_MOUNTAIN,
        TEMPERATE_MOUNTAIN,
        SPECIAL,
        UNKNOWN
    }

    // Researched per-(continent, direction) excellent matches for vanilla biomes.
    private static final Map<Continent, Map<SubDir, Set<ResourceLocation>>> BY_DIR = new EnumMap<>(Continent.class);
    // Merged per-continent sets (for cluster scoring)
    private static final Map<Continent, Set<ResourceLocation>> MERGED = new EnumMap<>(Continent.class);
    // Existing ocean lists are retained as positive evidence.
    private static final Map<Ocean, Set<ResourceLocation>> OCEAN_ALLOW = new EnumMap<>(Ocean.class);

    // Small override tables hold only especially strong evidence.
    private static final Map<Continent, Map<ResourceLocation, Affinity>> CONTINENT_OVERRIDES =
            new EnumMap<>(Continent.class);
    private static final Map<Ocean, Map<ResourceLocation, Affinity>> OCEAN_OVERRIDES =
            new EnumMap<>(Ocean.class);

    static {
        // --- the big tables (same as you pasted) ---
        // N_AMERICA
        add(Continent.N_AMERICA, SubDir.CENTRAL,
                "minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow",
                "minecraft:forest", "minecraft:flower_forest", "minecraft:dark_forest",
                "minecraft:birch_forest", "minecraft:old_growth_birch_forest", "minecraft:swamp",
                "minecraft:grove", "minecraft:jagged_peaks", "minecraft:stony_peaks",
                "minecraft:frozen_peaks", "minecraft:snowy_slopes", "minecraft:windswept_hills",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.N_AMERICA, SubDir.NORTH,
                "minecraft:taiga", "minecraft:old_growth_pine_taiga", "minecraft:old_growth_spruce_taiga",
                "minecraft:birch_forest", "minecraft:old_growth_birch_forest", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:plains",
                "minecraft:swamp", "minecraft:snowy_taiga", "minecraft:snowy_plains",
                "minecraft:ice_spikes", "minecraft:jagged_peaks", "minecraft:frozen_peaks",
                "minecraft:stony_peaks", "minecraft:meadow", "minecraft:grove",
                "minecraft:snowy_slopes", "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills",
                "minecraft:windswept_forest",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.N_AMERICA, SubDir.EAST,
                "minecraft:swamp", "minecraft:forest", "minecraft:flower_forest",
                "minecraft:dark_forest", "minecraft:birch_forest", "minecraft:old_growth_birch_forest",
                "minecraft:taiga", "minecraft:plains", "minecraft:sunflower_plains",
                "minecraft:meadow", "minecraft:stony_peaks", "minecraft:windswept_hills",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.N_AMERICA, SubDir.SOUTH,
                "minecraft:badlands", "minecraft:wooded_badlands", "minecraft:eroded_badlands",
                "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:windswept_savanna",
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:desert",
                "minecraft:plains", "minecraft:sunflower_plains", "minecraft:swamp",
                "minecraft:mangrove_swamp", "minecraft:meadow", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:mushroom_fields",
                "minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:windswept_hills",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.N_AMERICA, SubDir.WEST,
                "minecraft:taiga", "minecraft:old_growth_pine_taiga", "minecraft:old_growth_spruce_taiga",
                "minecraft:birch_forest", "minecraft:old_growth_birch_forest", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:plains",
                "minecraft:sunflower_plains", "minecraft:desert", "minecraft:jungle",
                "minecraft:sparse_jungle", "minecraft:mushroom_fields", "minecraft:meadow",
                "minecraft:swamp", "minecraft:grove", "minecraft:jagged_peaks",
                "minecraft:stony_peaks", "minecraft:frozen_peaks", "minecraft:snowy_slopes",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );

        // EUROPE
        add(Continent.EUROPE, SubDir.CENTRAL,
                "minecraft:forest", "minecraft:flower_forest", "minecraft:birch_forest",
                "minecraft:old_growth_birch_forest", "minecraft:dark_forest", "minecraft:plains",
                "minecraft:sunflower_plains", "minecraft:swamp", "minecraft:meadow",
                "minecraft:grove", "minecraft:jagged_peaks", "minecraft:stony_peaks",
                "minecraft:snowy_slopes", "minecraft:frozen_peaks", "minecraft:windswept_hills",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.EUROPE, SubDir.NORTH,
                "minecraft:taiga", "minecraft:old_growth_pine_taiga", "minecraft:old_growth_spruce_taiga",
                "minecraft:birch_forest", "minecraft:old_growth_birch_forest", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:plains",
                "minecraft:swamp", "minecraft:snowy_taiga", "minecraft:snowy_plains",
                "minecraft:ice_spikes", "minecraft:jagged_peaks", "minecraft:frozen_peaks",
                "minecraft:stony_peaks", "minecraft:meadow", "minecraft:grove",
                "minecraft:snowy_slopes", "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills",
                "minecraft:windswept_forest", "minecraft:mushroom_fields",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.EUROPE, SubDir.EAST,
                "minecraft:taiga", "minecraft:old_growth_pine_taiga", "minecraft:old_growth_spruce_taiga",
                "minecraft:birch_forest", "minecraft:old_growth_birch_forest", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:plains",
                "minecraft:sunflower_plains", "minecraft:swamp", "minecraft:snowy_taiga",
                "minecraft:snowy_plains", "minecraft:ice_spikes", "minecraft:jagged_peaks",
                "minecraft:frozen_peaks", "minecraft:stony_peaks", "minecraft:meadow",
                "minecraft:grove", "minecraft:snowy_slopes", "minecraft:windswept_hills",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest", "minecraft:mushroom_fields",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.EUROPE, SubDir.SOUTH,
                "minecraft:plains", "minecraft:forest", "minecraft:flower_forest",
                "minecraft:birch_forest", "minecraft:old_growth_birch_forest", "minecraft:dark_forest",
                "minecraft:swamp", "minecraft:meadow", "minecraft:grove",
                "minecraft:jagged_peaks", "minecraft:stony_peaks", "minecraft:snowy_slopes",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.EUROPE, SubDir.WEST,
                "minecraft:plains", "minecraft:sunflower_plains", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:birch_forest", "minecraft:old_growth_birch_forest",
                "minecraft:dark_forest", "minecraft:meadow", "minecraft:swamp",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );

        // ASIA
        add(Continent.ASIA, SubDir.CENTRAL,
                "minecraft:plains", "minecraft:forest", "minecraft:flower_forest",
                "minecraft:birch_forest", "minecraft:old_growth_birch_forest", "minecraft:swamp",
                "minecraft:mangrove_swamp", "minecraft:cherry_grove", "minecraft:desert",
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:bamboo_jungle",
                "minecraft:snowy_slopes", "minecraft:grove", "minecraft:stony_peaks",
                "minecraft:jagged_peaks", "minecraft:frozen_peaks", "minecraft:meadow",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"

        );
        add(Continent.ASIA, SubDir.NORTH,
                "minecraft:snowy_plains", "minecraft:snowy_taiga", "minecraft:taiga",
                "minecraft:forest", "minecraft:flower_forest", "minecraft:birch_forest",
                "minecraft:old_growth_birch_forest", "minecraft:dark_forest", "minecraft:old_growth_pine_taiga",
                "minecraft:old_growth_spruce_taiga", "minecraft:plains", "minecraft:meadow",
                "minecraft:swamp", "minecraft:snowy_slopes", "minecraft:grove",
                "minecraft:stony_peaks", "minecraft:jagged_peaks", "minecraft:frozen_peaks",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:ice_spikes", "minecraft:mushroom_fields",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.ASIA, SubDir.EAST,
                "minecraft:forest", "minecraft:plains", "minecraft:cherry_grove",
                "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:meadow",
                "minecraft:stony_peaks", "minecraft:jagged_peaks", "minecraft:snowy_slopes",
                "minecraft:grove", "minecraft:swamp", "minecraft:mangrove_swamp",
                "minecraft:mushroom_fields", "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills",
                "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.ASIA, SubDir.SOUTH,
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:bamboo_jungle",
                "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:windswept_savanna",
                "minecraft:desert", "minecraft:swamp", "minecraft:mangrove_swamp",
                "minecraft:forest", "minecraft:dark_forest", "minecraft:plains",
                "minecraft:meadow", "minecraft:grove", "minecraft:jagged_peaks",
                "minecraft:frozen_peaks", "minecraft:stony_peaks", "minecraft:snowy_slopes",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.ASIA, SubDir.WEST,
                "minecraft:desert", "minecraft:plains", "minecraft:swamp",
                "minecraft:forest", "minecraft:flower_forest", "minecraft:meadow",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );

        // AFRICA
        add(Continent.AFRICA, SubDir.CENTRAL,
                "minecraft:desert", "minecraft:savanna", "minecraft:savanna_plateau",
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:plains",
                "minecraft:forest", "minecraft:flower_forest", "minecraft:dark_forest",
                "minecraft:swamp",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.AFRICA, SubDir.NORTH,
                "minecraft:plains", "minecraft:desert", "minecraft:savanna",
                "minecraft:savanna_plateau", "minecraft:windswept_savanna", "minecraft:forest",
                "minecraft:stony_peaks", "minecraft:jagged_peaks", "minecraft:windswept_gravelly_hills",
                "minecraft:windswept_forest", "minecraft:meadow",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.AFRICA, SubDir.EAST,
                "minecraft:desert", "minecraft:savanna", "minecraft:savanna_plateau",
                "minecraft:windswept_savanna", "minecraft:mushroom_fields", "minecraft:swamp",
                "minecraft:mangrove_swamp", "minecraft:plains", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:stony_peaks", "minecraft:meadow",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.AFRICA, SubDir.SOUTH,
                "minecraft:desert", "minecraft:badlands", "minecraft:wooded_badlands",
                "minecraft:eroded_badlands", "minecraft:savanna", "minecraft:savanna_plateau",
                "minecraft:windswept_savanna", "minecraft:jungle", "minecraft:sparse_jungle",
                "minecraft:plains", "minecraft:forest", "minecraft:swamp",
                "minecraft:mangrove_swamp", "minecraft:meadow", "minecraft:stony_peaks",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.AFRICA, SubDir.WEST,
                "minecraft:desert", "minecraft:savanna", "minecraft:savanna_plateau",
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:plains",
                "minecraft:forest", "minecraft:flower_forest", "minecraft:swamp",
                "minecraft:mangrove_swamp", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );

        // SOUTH AMERICA
        add(Continent.S_AMERICA, SubDir.CENTRAL,
                "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:windswept_savanna",
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:swamp",
                "minecraft:plains", "minecraft:meadow", "minecraft:stony_peaks",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.S_AMERICA, SubDir.NORTH,
                "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:windswept_savanna",
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:plains",
                "minecraft:swamp", "minecraft:mangrove_swamp", "minecraft:mushroom_fields",
                "minecraft:dark_forest", "minecraft:meadow", "minecraft:stony_peaks",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.S_AMERICA, SubDir.EAST,
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:savanna",
                "minecraft:savanna_plateau", "minecraft:windswept_savanna", "minecraft:plains",
                "minecraft:swamp", "minecraft:mangrove_swamp", "minecraft:forest",
                "minecraft:flower_forest", "minecraft:dark_forest", "minecraft:meadow",
                "minecraft:stony_peaks", "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills",
                "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.S_AMERICA, SubDir.SOUTH,
                "minecraft:desert", "minecraft:savanna", "minecraft:savanna_plateau",
                "minecraft:windswept_savanna", "minecraft:mushroom_fields", "minecraft:plains",
                "minecraft:forest", "minecraft:flower_forest", "minecraft:dark_forest",
                "minecraft:meadow", "minecraft:stony_peaks", "minecraft:windswept_hills",
                "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.S_AMERICA, SubDir.WEST,
                "minecraft:desert", "minecraft:savanna", "minecraft:savanna_plateau",
                "minecraft:windswept_savanna", "minecraft:jungle", "minecraft:sparse_jungle",
                "minecraft:mushroom_fields", "minecraft:plains", "minecraft:swamp",
                "minecraft:meadow", "minecraft:stony_peaks", "minecraft:windswept_gravelly_hills",
                "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );

        // AUSTRALIA
        add(Continent.AUSTRALIA, SubDir.CENTRAL,
                "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:desert",
                "minecraft:badlands", "minecraft:wooded_badlands", "minecraft:eroded_badlands",
                "minecraft:plains", "minecraft:forest", "minecraft:flower_forest",
                "minecraft:swamp", "minecraft:mangrove_swamp",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.AUSTRALIA, SubDir.NORTH,
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:plains",
                "minecraft:forest", "minecraft:mushroom_fields", "minecraft:swamp",
                "minecraft:mangrove_swamp", "minecraft:stony_peaks", "minecraft:windswept_gravelly_hills",
                "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.AUSTRALIA, SubDir.EAST,
                "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:windswept_savanna",
                "minecraft:desert", "minecraft:plains", "minecraft:swamp",
                "minecraft:mangrove_swamp", "minecraft:stony_peaks", "minecraft:jagged_peaks",
                "minecraft:snowy_slopes", "minecraft:grove", "minecraft:windswept_gravelly_hills",
                "minecraft:windswept_forest", "minecraft:forest", "minecraft:dark_forest",
                "minecraft:jungle", "minecraft:sparse_jungle", "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.AUSTRALIA, SubDir.SOUTH,
                "minecraft:plains", "minecraft:forest", "minecraft:flower_forest",
                "minecraft:dark_forest", "minecraft:swamp", "minecraft:mangrove_swamp",
                "minecraft:stony_peaks", "minecraft:jagged_peaks", "minecraft:snowy_slopes",
                "minecraft:grove", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        add(Continent.AUSTRALIA, SubDir.WEST,
                "minecraft:savanna", "minecraft:savanna_plateau", "minecraft:desert",
                "minecraft:badlands", "minecraft:wooded_badlands", "minecraft:eroded_badlands",
                "minecraft:plains", "minecraft:forest", "minecraft:flower_forest",
                "minecraft:swamp", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );

        // ANTARCTICA
        add(Continent.ANTARCTICA, SubDir.CENTRAL,
                "minecraft:snowy_plains", "minecraft:ice_spikes", "minecraft:frozen_peaks",
                "minecraft:grove", "minecraft:snowy_slopes", "minecraft:snowy_taiga",
                "minecraft:mushroom_fields",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean", "minecraft:cold_ocean", "minecraft:deep_cold_ocean", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );

        // THE SEVEN SEAS
        putOcean(Ocean.NORTH_ATLANTIC,
                "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        putOcean(Ocean.SOUTH_ATLANTIC,
                "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        putOcean(Ocean.NORTH_PACIFIC,
                "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        putOcean(Ocean.SOUTH_PACIFIC,
                "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        putOcean(Ocean.INDIAN,
                "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        putOcean(Ocean.ARCTIC,
                "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );
        putOcean(Ocean.SOUTHERN,
                "minecraft:mushroom_fields",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:frozen_river", "minecraft:snowy_beach",
                "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:deep_dark", "minecraft:dripstone_caves", "minecraft:lush_caves"
        );

        rebuildMerged();
        registerAffinityOverrides();
    }

    private static void registerAffinityOverrides() {
        // Regional maps remain the source of every excellent match. Overrides
        // are reserved for exceptional clarifications that broad climate alone
        // cannot express.
        affinity(Continent.AFRICA, Affinity.NEUTRAL,
                "minecraft:cherry_grove");

        // Ocean temperature is allowed to be flexible, except at the poles.
        oceanAffinity(Ocean.ARCTIC, Affinity.STRONGLY_UNSUITABLE,
                "minecraft:warm_ocean");
        oceanAffinity(Ocean.SOUTHERN, Affinity.STRONGLY_UNSUITABLE,
                "minecraft:warm_ocean");
    }

    // ---- helpers for building ----
    private static void add(Continent c, SubDir d, String... ids) {
        Map<SubDir, Set<ResourceLocation>> perDir = BY_DIR.computeIfAbsent(c, k -> new EnumMap<>(SubDir.class));
        Set<ResourceLocation> set = perDir.computeIfAbsent(d, k -> new HashSet<>());
        for (String s : ids) set.add(new ResourceLocation(s));
    }

    private static void rebuildMerged() {
        MERGED.clear();
        for (Map.Entry<Continent, Map<SubDir, Set<ResourceLocation>>> e : BY_DIR.entrySet()) {
            Set<ResourceLocation> all = new HashSet<>();
            for (Set<ResourceLocation> s : e.getValue().values()) all.addAll(s);
            MERGED.put(e.getKey(), all);
        }
    }

    private static void putOcean(Ocean o, String... ids) {
        Set<ResourceLocation> set = OCEAN_ALLOW.computeIfAbsent(o, k -> new HashSet<>());
        for (String s : ids) set.add(new ResourceLocation(s));
    }

    private static void affinity(Continent continent, Affinity value, String... ids) {
        Map<ResourceLocation, Affinity> values =
                CONTINENT_OVERRIDES.computeIfAbsent(continent, ignored -> new HashMap<>());
        for (String id : ids) values.put(new ResourceLocation(id), value);
    }

    private static void oceanAffinity(Ocean ocean, Affinity value, String... ids) {
        Map<ResourceLocation, Affinity> values =
                OCEAN_OVERRIDES.computeIfAbsent(ocean, ignored -> new HashMap<>());
        for (String id : ids) values.put(new ResourceLocation(id), value);
    }

    private static boolean isSurfaceIndependent(ResourceLocation biomeId) {
        if (!"minecraft".equals(biomeId.getNamespace())) return true;

        String path = biomeId.getPath();
        return path.equals("deep_dark")
                || path.equals("dripstone_caves")
                || path.equals("lush_caves")
                || path.equals("river")
                || path.equals("frozen_river")
                || path.equals("beach")
                || path.equals("snowy_beach")
                || path.equals("stony_shore")
                || path.equals("ocean")
                || path.equals("deep_ocean")
                || path.equals("warm_ocean")
                || path.equals("lukewarm_ocean")
                || path.equals("deep_lukewarm_ocean")
                || path.equals("cold_ocean")
                || path.equals("deep_cold_ocean")
                || path.equals("frozen_ocean")
                || path.equals("deep_frozen_ocean");
    }

    // ---- weighted API used by the current and future samplers ----

    public static Affinity directionAffinity(Continent continent, SubDir direction,
                                             ResourceLocation biomeId) {
        Affinity override = override(CONTINENT_OVERRIDES, continent, biomeId);
        if (override != null) return override;

        // Caves, coasts, rivers and oceans are classified by a later layer.
        // They must not influence a land continent or direction.
        if (isSurfaceIndependent(biomeId)) return Affinity.NEUTRAL;

        Map<SubDir, Set<ResourceLocation>> perDirection = BY_DIR.get(continent);
        if (perDirection == null) return Affinity.UNUSUAL;

        // The original researched regional maps define excellent matches.
        Set<ResourceLocation> researchedForDirection = perDirection.get(direction);
        if (researchedForDirection != null && researchedForDirection.contains(biomeId)) {
            return Affinity.EXCELLENT_MATCH;
        }

        // A biome researched for another subregion of the same continent is
        // still a good match. Direction labels are cultural regions, not an
        // obligation to follow Minecraft's compass.
        Set<ResourceLocation> researchedForContinent = MERGED.get(continent);
        if (researchedForContinent != null && researchedForContinent.contains(biomeId)) {
            return Affinity.GOOD_MATCH;
        }

        return climateAffinity(continent, climateFamily(biomeId));
    }

    public static Affinity continentAffinity(Continent continent, ResourceLocation biomeId) {
        Affinity override = override(CONTINENT_OVERRIDES, continent, biomeId);
        if (override != null) return override;
        if (isSurfaceIndependent(biomeId)) return Affinity.NEUTRAL;

        // At continent scale every entry from the researched regional maps is
        // excellent evidence, regardless of its subregion.
        Set<ResourceLocation> researched = MERGED.get(continent);
        if (researched != null && researched.contains(biomeId)) {
            return Affinity.EXCELLENT_MATCH;
        }

        return climateAffinity(continent, climateFamily(biomeId));
    }

    public static Affinity oceanAffinity(Ocean ocean, ResourceLocation biomeId) {
        Affinity override = override(OCEAN_OVERRIDES, ocean, biomeId);
        if (override != null) return override;

        Set<ResourceLocation> researched = OCEAN_ALLOW.get(ocean);
        if (researched != null && researched.contains(biomeId)) {
            return Affinity.EXCELLENT_MATCH;
        }

        return oceanClimateAffinity(ocean, biomeId);
    }

    private static ClimateFamily climateFamily(ResourceLocation biomeId) {
        if (!"minecraft".equals(biomeId.getNamespace())) return ClimateFamily.UNKNOWN;

        return switch (biomeId.getPath()) {
            case "snowy_plains", "ice_spikes" -> ClimateFamily.POLAR;
            case "taiga", "snowy_taiga", "old_growth_pine_taiga",
                    "old_growth_spruce_taiga" -> ClimateFamily.BOREAL;
            case "plains", "sunflower_plains", "forest", "flower_forest",
                    "birch_forest", "old_growth_birch_forest", "dark_forest",
                    "meadow", "cherry_grove" -> ClimateFamily.TEMPERATE;
            case "desert", "badlands", "wooded_badlands",
                    "eroded_badlands" -> ClimateFamily.ARID;
            case "savanna", "savanna_plateau",
                    "windswept_savanna" -> ClimateFamily.SAVANNA;
            case "jungle", "sparse_jungle",
                    "bamboo_jungle" -> ClimateFamily.TROPICAL;
            case "swamp", "mangrove_swamp" -> ClimateFamily.WETLAND;
            case "frozen_peaks", "jagged_peaks", "snowy_slopes",
                    "grove" -> ClimateFamily.COLD_MOUNTAIN;
            case "stony_peaks", "windswept_hills", "windswept_gravelly_hills",
                    "windswept_forest" -> ClimateFamily.TEMPERATE_MOUNTAIN;
            case "mushroom_fields" -> ClimateFamily.SPECIAL;
            default -> ClimateFamily.UNKNOWN;
        };
    }

    private static Affinity climateAffinity(Continent continent, ClimateFamily climate) {
        if (climate == ClimateFamily.UNKNOWN || climate == ClimateFamily.SPECIAL) {
            return Affinity.NEUTRAL;
        }

        return switch (continent) {
            case AFRICA -> switch (climate) {
                case ARID, SAVANNA, TROPICAL, WETLAND -> Affinity.GOOD_MATCH;
                case TEMPERATE, TEMPERATE_MOUNTAIN -> Affinity.NEUTRAL;
                case COLD_MOUNTAIN -> Affinity.UNUSUAL;
                case BOREAL, POLAR -> Affinity.STRONGLY_UNSUITABLE;
                default -> Affinity.NEUTRAL;
            };
            case ANTARCTICA -> switch (climate) {
                case POLAR -> Affinity.EXCELLENT_MATCH;
                case BOREAL, COLD_MOUNTAIN -> Affinity.GOOD_MATCH;
                default -> Affinity.STRONGLY_UNSUITABLE;
            };
            case ASIA -> switch (climate) {
                case POLAR, BOREAL, TEMPERATE, ARID, SAVANNA, TROPICAL, WETLAND,
                        COLD_MOUNTAIN, TEMPERATE_MOUNTAIN -> Affinity.GOOD_MATCH;
                default -> Affinity.NEUTRAL;
            };
            case EUROPE -> switch (climate) {
                case BOREAL, TEMPERATE, WETLAND, COLD_MOUNTAIN,
                        TEMPERATE_MOUNTAIN -> Affinity.GOOD_MATCH;
                case POLAR -> Affinity.NEUTRAL;
                case ARID, SAVANNA -> Affinity.UNUSUAL;
                case TROPICAL -> Affinity.UNUSUAL;
                default -> Affinity.NEUTRAL;
            };
            case N_AMERICA -> switch (climate) {
                case POLAR, BOREAL, TEMPERATE, ARID, SAVANNA, TROPICAL, WETLAND,
                        COLD_MOUNTAIN, TEMPERATE_MOUNTAIN -> Affinity.GOOD_MATCH;
                default -> Affinity.NEUTRAL;
            };
            case S_AMERICA -> switch (climate) {
                case TEMPERATE, ARID, SAVANNA, TROPICAL, WETLAND,
                        COLD_MOUNTAIN, TEMPERATE_MOUNTAIN -> Affinity.GOOD_MATCH;
                case BOREAL, POLAR -> Affinity.UNUSUAL;
                default -> Affinity.NEUTRAL;
            };
            case AUSTRALIA -> switch (climate) {
                case TEMPERATE, ARID, SAVANNA, TROPICAL, WETLAND,
                        TEMPERATE_MOUNTAIN -> Affinity.GOOD_MATCH;
                case COLD_MOUNTAIN, BOREAL -> Affinity.UNUSUAL;
                case POLAR -> Affinity.STRONGLY_UNSUITABLE;
                default -> Affinity.NEUTRAL;
            };
        };
    }

    private static Affinity oceanClimateAffinity(Ocean ocean, ResourceLocation biomeId) {
        if (!"minecraft".equals(biomeId.getNamespace())) return Affinity.NEUTRAL;

        String path = biomeId.getPath();
        boolean frozen = path.equals("frozen_ocean") || path.equals("deep_frozen_ocean");
        boolean cold = path.equals("cold_ocean") || path.equals("deep_cold_ocean");
        boolean mild = path.equals("ocean") || path.equals("deep_ocean")
                || path.equals("lukewarm_ocean") || path.equals("deep_lukewarm_ocean");
        boolean warm = path.equals("warm_ocean");

        if (!frozen && !cold && !mild && !warm) return Affinity.NEUTRAL;

        return switch (ocean) {
            case ARCTIC, SOUTHERN -> frozen
                    ? Affinity.EXCELLENT_MATCH
                    : cold ? Affinity.GOOD_MATCH
                    : mild ? Affinity.UNUSUAL
                    : Affinity.STRONGLY_UNSUITABLE;
            case NORTH_ATLANTIC, NORTH_PACIFIC -> cold || mild
                    ? Affinity.GOOD_MATCH
                    : Affinity.NEUTRAL;
            case SOUTH_ATLANTIC, SOUTH_PACIFIC, INDIAN -> warm || mild
                    ? Affinity.GOOD_MATCH
                    : frozen ? Affinity.UNUSUAL
                    : Affinity.NEUTRAL;
        };
    }

    private static <K extends Enum<K>> Affinity override(
            Map<K, Map<ResourceLocation, Affinity>> table, K key, ResourceLocation biomeId) {
        Map<ResourceLocation, Affinity> values = table.get(key);
        return values == null ? null : values.get(biomeId);
    }

    /**
     * Compatibility wrapper for the present sampler. A normal mismatch is now
     * accepted; only a deliberate strong contradiction triggers fallback.
     */
    public static boolean allows(Continent continent, SubDir direction, ResourceLocation biomeId) {
        return directionAffinity(continent, direction, biomeId) != Affinity.STRONGLY_UNSUITABLE;
    }

    /**
     * Compatibility wrapper for cluster scoring. Positive affinities count as
     * supporting evidence; neutral and unusual biomes do not.
     */
    public static boolean continentAllows(Continent continent, ResourceLocation biomeId) {
        return continentAffinity(continent, biomeId).score() > 0;
    }

    public static boolean oceanAllows(Ocean ocean, ResourceLocation biomeId) {
        return oceanAffinity(ocean, biomeId) != Affinity.STRONGLY_UNSUITABLE;
    }
}
