package net.semppi.semppis_mythical_legends_mod.client;

import net.semppi.semppis_mythical_legends_mod.world.Region;

public final class ClientRegionState {
    private static volatile Region last; // updated from network thread safely

    private ClientRegionState(){}

    public static void set(Region r) { last = r; }
    public static Region get() { return last; }
}