package net.semppi.semppis_mythical_legends_mod.world;

import java.util.*;
import net.minecraft.resources.ResourceLocation;

public final class TagRules {
    private TagRules() {}

    // Per-(continent, direction) allow-lists for VANILLA biomes (namespace "minecraft").
    // Modded biomes are allowed by default.
    private static final Map<Continent, Map<SubDir, Set<ResourceLocation>>> BY_DIR =
            new EnumMap<>(Continent.class);

    // Continent-merged allow sets (used by cluster scoring fallback)
    private static final Map<Continent, Set<ResourceLocation>> MERGED =
            new EnumMap<>(Continent.class);

    // Optional: per-ocean allow-lists (not currently used by sampler, but ready to use)
    private static final Map<Ocean, Set<ResourceLocation>> OCEAN_ALLOW =
            new EnumMap<>(Ocean.class);

    static {

        // NORTH AMERICA
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
    }

    // ---- helpers for building the tables ----
    private static void add(Continent c, SubDir d, String... ids) {
        Map<SubDir, Set<ResourceLocation>> perDir =
                BY_DIR.computeIfAbsent(c, k -> new EnumMap<>(SubDir.class));
        Set<ResourceLocation> set =
                perDir.computeIfAbsent(d, k -> new HashSet<>());
        for (String s : ids) set.add(ResourceLocation.parse(s));
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
        for (String s : ids) set.add(ResourceLocation.parse(s));
    }

    // ---- API used by RegionSampler ----
    /** Direction-aware check. If per-dir list missing, falls back to continent aggregate. */
    public static boolean allows(Continent c, SubDir d, ResourceLocation biomeId) {
        if (!"minecraft".equals(biomeId.getNamespace())) return true; // mod biomes allowed
        Map<SubDir, Set<ResourceLocation>> perDir = BY_DIR.get(c);
        if (perDir == null) return false;
        Set<ResourceLocation> set = perDir.get(d);
        if (set != null) return set.contains(biomeId);
        Set<ResourceLocation> merged = MERGED.get(c);
        return merged != null && merged.contains(biomeId);
    }

    /** Backwards-compatible continent-level check (used by cluster scoring). */
    public static boolean continentAllows(Continent c, ResourceLocation biomeId) {
        if (!"minecraft".equals(biomeId.getNamespace())) return true;
        Set<ResourceLocation> merged = MERGED.get(c);
        return merged != null && merged.contains(biomeId);
    }

    /** Optional ocean rule check (not currently used by sampler). */
    public static boolean oceanAllows(Ocean o, ResourceLocation biomeId) {
        if (!"minecraft".equals(biomeId.getNamespace())) return true;
        Set<ResourceLocation> set = OCEAN_ALLOW.get(o);
        return set != null && set.contains(biomeId);
    }
}