package net.semppi.semppis_mythical_legends_mod.world;

public record Region(boolean ocean, Continent continent, SubDir dir, Ocean sea) {
    public static Region land(Continent c, SubDir d) {
        return new Region(false, c, d, null);
    }
    public static Region sea(Ocean s) {
        return new Region(true, null, SubDir.CENTRAL, s);
    }

    public String display() {
        if (ocean) {
            return switch (sea) {
                case ARCTIC -> "Arctic Ocean";
                case NORTH_ATLANTIC -> "North Atlantic";
                case SOUTH_ATLANTIC -> "South Atlantic";
                case INDIAN -> "Indian Ocean";
                case NORTH_PACIFIC -> "North Pacific";
                case SOUTH_PACIFIC -> "South Pacific";
                case SOUTHERN -> "Southern Ocean";
            };
        }

        // Antarctica has no directions in the UI
        if (continent == Continent.ANTARCTICA) {
            return "Antarctica";
        }

        String cName = switch (continent) {
            case AFRICA -> "Africa";
            case ANTARCTICA -> "Antarctica";
            case ASIA -> "Asia";
            case EUROPE -> "Europe";
            case N_AMERICA -> "North America";
            case S_AMERICA -> "South America";
            case AUSTRALIA -> "Australia";
        };
        String dName = switch (dir) {
            case NORTH -> "Northern";
            case SOUTH -> "Southern";
            case EAST -> "Eastern";
            case WEST -> "Western";
            case CENTRAL -> "Central";
        };
        return dName + " " + cName;
    }
}