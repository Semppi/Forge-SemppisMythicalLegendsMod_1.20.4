package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.world.entity.EntityType;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.world.*;

public final class RegionMobAllow {
    private RegionMobAllow() {}

    /** Ocean gating */
    public static boolean isAllowedForSea(EntityType<?> type, Ocean sea) {
        if (type == ModEntities.COLOSSAL_LOBSTER.get()) {
            return sea == Ocean.NORTH_ATLANTIC || sea == Ocean.ARCTIC;
        }
        return true;
    }

    /** Land gating */
    public static boolean isAllowedForLand(EntityType<?> type, Continent c, SubDir d) {
        if (type == ModEntities.ALICANTO.get()) {
            return c == Continent.S_AMERICA && (d == SubDir.SOUTH);
        }
        if (type == ModEntities.LESSER_BEHEMOTH.get()) {
            return c == Continent.ASIA && (d == SubDir.WEST);
        }
        if (type == ModEntities.COLOSSAL_LOBSTER.get()) {
            return c == Continent.EUROPE && (d == SubDir.WEST || d == SubDir.NORTH);
        }
        if (type == ModEntities.LOVELAND_FROGMAN.get()) {
            return c == Continent.N_AMERICA && (d == SubDir.EAST);
        }
        if (type == ModEntities.MANDRAKE.get()) {
            return (c == Continent.EUROPE && d == SubDir.SOUTH)
                    || (c == Continent.AFRICA && d == SubDir.NORTH)
                    || (c == Continent.ASIA
                    && (d == SubDir.WEST
                    || d == SubDir.SOUTH
                    || d == SubDir.CENTRAL));
        }
        if (type == ModEntities.PROTO_WENDIGO.get()) {
            return c == Continent.N_AMERICA && (d == SubDir.NORTH || d == SubDir.CENTRAL || d == SubDir.EAST);
        }
        if (type == ModEntities.PUKIS.get()) {
            return c == Continent.EUROPE && (d == SubDir.CENTRAL || d == SubDir.EAST);
        }
        if (type == ModEntities.SATYR.get()) {
            return (c == Continent.EUROPE && d == SubDir.SOUTH) || (c == Continent.ASIA && d == SubDir.WEST);
        }
        if (type == ModEntities.WENDIGO.get()) {
            return c == Continent.N_AMERICA && (d == SubDir.NORTH || d == SubDir.EAST || d == SubDir.CENTRAL);
        }
        return true;
    }

    /** True when this creature has a continent/direction or ocean rule. */
    public static boolean hasRestriction(EntityType<?> type) {
        return type == ModEntities.ALICANTO.get()
                || type == ModEntities.LESSER_BEHEMOTH.get()
                || type == ModEntities.COLOSSAL_LOBSTER.get()
                || type == ModEntities.LOVELAND_FROGMAN.get()
                || type == ModEntities.MANDRAKE.get()
                || type == ModEntities.PROTO_WENDIGO.get()
                || type == ModEntities.PUKIS.get()
                || type == ModEntities.SATYR.get()
                || type == ModEntities.WENDIGO.get();
    }

    /** Breeding restriction (opt-in). Default: no one is picky. */
    public static boolean isBreedingRestricted(EntityType<?> type) {
        // Example to enable picky breeding for a species:
        // if (type == ModEntities.ALICANTO.get()) return true;
        return false;
    }
}
