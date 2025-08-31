package net.semppi.semppis_mythical_legends_mod.spawn;

import net.minecraft.world.entity.EntityType;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.world.*;

public final class RegionMobAllow {
    private RegionMobAllow() {}

    /** Ocean gating */
    public static boolean isAllowedForSea(EntityType<?> type, Ocean sea) {
        // Colossal Lobster: only in North Atlantic or Arctic
        if (type == ModEntities.COLOSSAL_LOBSTER.get()) {
            return sea == Ocean.NORTH_ATLANTIC || sea == Ocean.ARCTIC;
        }

        // other sea mobs: allow until you add rules
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

        if (type == ModEntities.PROTO_WENDIGO.get()) {
            return c == Continent.N_AMERICA && (d == SubDir.NORTH || d == SubDir.CENTRAL || d == SubDir.EAST);
        }

        if (type == ModEntities.PUKIS.get()) {
            return c == Continent.EUROPE && (d == SubDir.CENTRAL || d == SubDir.EAST);
        }

        if (type == ModEntities.SATYR.get()) {
            return (c == Continent.EUROPE && d == SubDir.SOUTH)
                    || (c == Continent.ASIA   && d == SubDir.WEST);
        }

        if (type == ModEntities.WENDIGO.get()) {
            return c == Continent.N_AMERICA && (d == SubDir.NORTH || d == SubDir.CENTRAL || d == SubDir.EAST);
        }



        // other land mobs: allow until you add rules
        return true;
    }
}