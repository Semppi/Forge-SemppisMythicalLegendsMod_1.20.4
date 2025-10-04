package net.semppi.semppis_mythical_legends_mod.client;

import net.semppi.semppis_mythical_legends_mod.world.Region;

public final class ClientRegionState {
    // updated from network thread safely
    private static volatile Region last;

    private ClientRegionState() {}

    public static void set(Region r) {
        last = r;
    }

    public static Region get() {
        return last;
    }
}