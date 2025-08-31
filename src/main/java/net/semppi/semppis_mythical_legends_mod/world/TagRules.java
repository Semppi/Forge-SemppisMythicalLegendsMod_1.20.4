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
                "minecraft:plains","minecraft:forest","minecraft:flower_forest","minecraft:birch_forest",
                "minecraft:swamp","minecraft:river","minecraft:beach","minecraft:stony_shore",
                "minecraft:windswept_hills","minecraft:taiga"
        );
        add(Continent.N_AMERICA, SubDir.NORTH,
                "minecraft:taiga","minecraft:snowy_taiga","minecraft:snowy_plains",
                "minecraft:river","minecraft:beach","minecraft:stony_shore"
        );
        add(Continent.N_AMERICA, SubDir.EAST,
                "minecraft:forest","minecraft:birch_forest","minecraft:old_growth_birch_forest",
                "minecraft:plains","minecraft:swamp","minecraft:river","minecraft:beach",
                "minecraft:stony_shore","minecraft:windswept_hills"
        );
        add(Continent.N_AMERICA, SubDir.SOUTH,
                "minecraft:forest","minecraft:birch_forest","minecraft:old_growth_birch_forest",
                "minecraft:plains","minecraft:swamp","minecraft:river","minecraft:beach",
                "minecraft:stony_shore","minecraft:windswept_hills"
        );
        add(Continent.N_AMERICA, SubDir.WEST,
                "minecraft:badlands","minecraft:eroded_badlands","minecraft:wooded_badlands",
                "minecraft:desert","minecraft:savanna",
                "minecraft:plains","minecraft:forest","minecraft:windswept_hills",
                "minecraft:river","minecraft:beach","minecraft:stony_shore"
        );

        // EUROPE
        add(Continent.EUROPE, SubDir.CENTRAL,
                "minecraft:plains", "minecraft:taiga", "minecraft:snowy_taiga",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:windswept_hills"
        );
        add(Continent.EUROPE, SubDir.NORTH,
                "minecraft:plains", "minecraft:taiga", "minecraft:snowy_taiga",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:windswept_hills"
        );
        add(Continent.EUROPE, SubDir.EAST,
                "minecraft:plains", "minecraft:taiga", "minecraft:snowy_taiga",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:windswept_hills"
        );
        add(Continent.EUROPE, SubDir.SOUTH,
                "minecraft:plains", "minecraft:taiga", "minecraft:snowy_taiga",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:windswept_hills"
        );
        add(Continent.EUROPE, SubDir.WEST,
                "minecraft:plains", "minecraft:forest", "minecraft:birch_forest",
                "minecraft:old_growth_birch_forest", "minecraft:flower_forest",
                "minecraft:swamp", "minecraft:river",
                "minecraft:beach", "minecraft:stony_shore",
                "minecraft:windswept_hills"
        );

        // ASIA
        add(Continent.ASIA, SubDir.CENTRAL,
                "minecraft:cherry_grove", "minecraft:birch_forest",
                "minecraft:plains", "minecraft:forest", "minecraft:river"
        );
        add(Continent.ASIA, SubDir.NORTH,
                "minecraft:cherry_grove", "minecraft:bamboo_jungle", "minecraft:jungle",
                "minecraft:forest", "minecraft:plains", "minecraft:river", "minecraft:beach"
        );
        add(Continent.ASIA, SubDir.EAST,
                "minecraft:cherry_grove", "minecraft:bamboo_jungle", "minecraft:jungle",
                "minecraft:forest", "minecraft:plains", "minecraft:river", "minecraft:beach"
        );
        add(Continent.ASIA, SubDir.SOUTH,
                "minecraft:desert", "minecraft:jungle", "minecraft:bamboo_jungle",
                "minecraft:sparse_jungle", "minecraft:swamp", "minecraft:river",
                "minecraft:beach", "minecraft:plains"
        );
        add(Continent.ASIA, SubDir.WEST,
                "minecraft:desert", "minecraft:jungle", "minecraft:bamboo_jungle",
                "minecraft:sparse_jungle", "minecraft:swamp", "minecraft:river",
                "minecraft:beach", "minecraft:plains"
        );

        // AFRICA
        add(Continent.AFRICA, SubDir.CENTRAL,
                "minecraft:savanna", "minecraft:plains", "minecraft:forest", "minecraft:meadow",
                "minecraft:river", "minecraft:beach", "minecraft:windswept_hills"
        );
        add(Continent.AFRICA, SubDir.NORTH,
                "minecraft:desert", "minecraft:savanna", "minecraft:savanna_plateau",
                "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest",
                "minecraft:stony_peaks", "minecraft:jagged_peaks",
                "minecraft:river", "minecraft:beach", "minecraft:stony_shore",
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean", "minecraft:warm_ocean",
                "minecraft:plains"
        );
        add(Continent.AFRICA, SubDir.EAST,
                "minecraft:savanna", "minecraft:plains", "minecraft:forest", "minecraft:meadow",
                "minecraft:river", "minecraft:beach", "minecraft:windswept_hills"
        );
        add(Continent.AFRICA, SubDir.SOUTH,
                "minecraft:savanna", "minecraft:plains", "minecraft:forest", "minecraft:meadow",
                "minecraft:river", "minecraft:beach", "minecraft:windswept_hills"
        );
        add(Continent.AFRICA, SubDir.WEST,
                "minecraft:savanna", "minecraft:plains", "minecraft:forest", "minecraft:meadow",
                "minecraft:river", "minecraft:beach", "minecraft:windswept_hills"
        );

        // SOUTH AMERICA
        add(Continent.S_AMERICA, SubDir.CENTRAL,
                "minecraft:jungle","minecraft:sparse_jungle","minecraft:bamboo_jungle",
                "minecraft:savanna","minecraft:plains","minecraft:forest",
                "minecraft:swamp","minecraft:river","minecraft:beach"
        );
        add(Continent.S_AMERICA, SubDir.NORTH,
                "minecraft:jungle","minecraft:sparse_jungle","minecraft:bamboo_jungle",
                "minecraft:river","minecraft:beach","minecraft:plains","minecraft:forest"
        );
        add(Continent.S_AMERICA, SubDir.EAST,
                "minecraft:jungle","minecraft:sparse_jungle","minecraft:bamboo_jungle",
                "minecraft:river","minecraft:beach","minecraft:plains","minecraft:forest"
        );
        add(Continent.S_AMERICA, SubDir.SOUTH,
                "minecraft:plains","minecraft:forest","minecraft:taiga",
                "minecraft:windswept_hills","minecraft:river","minecraft:beach","minecraft:stony_shore"
        );
        add(Continent.S_AMERICA, SubDir.WEST,
                "minecraft:badlands","minecraft:eroded_badlands","minecraft:stony_peaks",
                "minecraft:windswept_hills","minecraft:plains","minecraft:river",
                "minecraft:beach","minecraft:stony_shore"
        );

        // AUSTRALIA
        add(Continent.AUSTRALIA, SubDir.CENTRAL,
                "minecraft:desert", "minecraft:savanna", "minecraft:plains",
                "minecraft:badlands", "minecraft:eroded_badlands",
                "minecraft:river", "minecraft:beach"
        );
        add(Continent.AUSTRALIA, SubDir.NORTH,
                "minecraft:desert", "minecraft:savanna", "minecraft:plains",
                "minecraft:badlands", "minecraft:eroded_badlands",
                "minecraft:river", "minecraft:beach"
        );
        add(Continent.AUSTRALIA, SubDir.EAST,
                "minecraft:desert", "minecraft:savanna", "minecraft:plains",
                "minecraft:badlands", "minecraft:eroded_badlands",
                "minecraft:river", "minecraft:beach"
        );
        add(Continent.AUSTRALIA, SubDir.SOUTH,
                "minecraft:desert", "minecraft:savanna", "minecraft:plains",
                "minecraft:badlands", "minecraft:eroded_badlands",
                "minecraft:river", "minecraft:beach"
        );
        add(Continent.AUSTRALIA, SubDir.WEST,
                "minecraft:desert", "minecraft:savanna", "minecraft:plains",
                "minecraft:badlands", "minecraft:eroded_badlands",
                "minecraft:river", "minecraft:beach"
        );

        // ANTARCTICA
        add(Continent.ANTARCTICA, SubDir.CENTRAL,
                "minecraft:snowy_plains", "minecraft:snowy_taiga",
                "minecraft:ice_spikes", "minecraft:frozen_ocean", "minecraft:deep_frozen_ocean",
                "minecraft:river", "minecraft:stony_peaks"
        );

        // THE SEVEN SEAS
        putOcean(Ocean.NORTH_ATLANTIC,
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:river", "minecraft:stony_shore", "minecraft:beach"
        );
        putOcean(Ocean.SOUTH_ATLANTIC,
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean", "minecraft:river", "minecraft:beach"
        );
        putOcean(Ocean.NORTH_PACIFIC,
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:river", "minecraft:stony_shore", "minecraft:beach"
        );
        putOcean(Ocean.SOUTH_PACIFIC,
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
                "minecraft:river", "minecraft:stony_shore", "minecraft:beach"
        );
        putOcean(Ocean.INDIAN,
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean", "minecraft:river", "minecraft:beach"
        );
        putOcean(Ocean.ARCTIC,
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean", "minecraft:river", "minecraft:beach"
        );
        putOcean(Ocean.SOUTHERN,
                "minecraft:ocean", "minecraft:deep_ocean",
                "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
                "minecraft:warm_ocean", "minecraft:river", "minecraft:beach"
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