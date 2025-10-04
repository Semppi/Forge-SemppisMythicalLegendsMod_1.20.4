package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public final class RegionCompat {
    private RegionCompat() {}

    public static boolean isAllowedForBiome(Holder<Biome> biome, Region region) {
        // Start permissive; add stricter rules later if desired
        return true;
    }
}