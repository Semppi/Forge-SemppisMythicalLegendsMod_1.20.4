package net.semppi.semppis_mythical_legends_mod.network;

import net.semppi.semppis_mythical_legends_mod.world.Continent;
import net.semppi.semppis_mythical_legends_mod.world.Ocean;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.SubDir;

/** Compact, stable identities for resolved map regions. Zero is unexplored. */
public final class MapRegionCode {
    private static final int LAND_COUNT =
            Continent.values().length * SubDir.values().length;

    private MapRegionCode() {}

    public static byte encode(Region region) {
        int code;
        if (region.ocean()) {
            code = 1 + LAND_COUNT + region.sea().ordinal();
        } else {
            code = 1
                    + region.continent().ordinal() * SubDir.values().length
                    + region.dir().ordinal();
        }
        return (byte) code;
    }

    public static boolean isOcean(byte encodedRegion) {
        return Byte.toUnsignedInt(encodedRegion) > LAND_COUNT;
    }

    public static Continent continent(byte encodedRegion) {
        int code = Byte.toUnsignedInt(encodedRegion);
        if (code == 0 || code > LAND_COUNT) {
            throw new IllegalArgumentException("Region code is not land");
        }
        int continentIndex = (code - 1) / SubDir.values().length;
        return Continent.values()[continentIndex];
    }

    public static Ocean ocean(byte encodedRegion) {
        int code = Byte.toUnsignedInt(encodedRegion);
        int oceanIndex = code - 1 - LAND_COUNT;
        if (oceanIndex < 0 || oceanIndex >= Ocean.values().length) {
            throw new IllegalArgumentException("Region code is not ocean");
        }
        return Ocean.values()[oceanIndex];
    }

    /** Identity actually drawn by the current continental overlay. */
    public static int overlayIdentity(byte encodedRegion) {
        if (isOcean(encodedRegion)) {
            return Continent.values().length
                    + ocean(encodedRegion).ordinal();
        }
        return continent(encodedRegion).ordinal();
    }
}
