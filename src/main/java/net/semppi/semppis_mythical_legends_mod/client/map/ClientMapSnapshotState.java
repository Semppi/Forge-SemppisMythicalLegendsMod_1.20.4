package net.semppi.semppis_mythical_legends_mod.client.map;

import net.semppi.semppis_mythical_legends_mod.network.MapSnapshotPayload;

/** Latest complete map page received from the logical server. */
public final class ClientMapSnapshotState {
    private static volatile MapSnapshotPayload snapshot;

    private ClientMapSnapshotState() {}

    public static void set(MapSnapshotPayload value) {
        snapshot = value;
    }

    public static MapSnapshotPayload get() {
        return snapshot;
    }

    public static void clear() {
        snapshot = null;
    }
}
