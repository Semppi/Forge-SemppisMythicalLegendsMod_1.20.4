package net.semppi.semppis_mythical_legends_mod.client;

import net.semppi.semppis_mythical_legends_mod.world.Continent;
import net.semppi.semppis_mythical_legends_mod.world.Ocean;
import net.semppi.semppis_mythical_legends_mod.world.Region;

public final class RegionText {
    private RegionText() {}

    public static String format(Region region) {
        if (region == null) return "";

        if (region.ocean()) {
            return prettyOcean(region.sea());
        }

        // The internal Antarctica identity represents either frozen pole.
        if (region.continent() == Continent.ANTARCTICA) {
            return "Frozen Pole";
        }

        String dir = switch (region.dir()) {
            case NORTH -> "Northern";
            case SOUTH -> "Southern";
            case EAST  -> "Eastern";
            case WEST  -> "Western";
            case CENTRAL -> "Central";
        };

        return dir + " " + prettyContinent(region.continent());
    }

    private static String prettyContinent(Continent c) {
        // turn EUROPE or SOUTH_AMERICA into "Europe" / "South America"
        String s = c.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String prettyOcean(Ocean o) {
        // e.g. NORTH_ATLANTIC -> "North Atlantic"
        String s = o.name().toLowerCase().replace('_', ' ');
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
