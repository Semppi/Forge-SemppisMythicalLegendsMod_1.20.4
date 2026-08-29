package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.network.FriendlyByteBuf;

/** Tells the client to discard its currently displayed test-map page. */
public record MapSnapshotClearPayload() {

    public void write(FriendlyByteBuf buffer) {
        // No fields are required.
    }

    public static MapSnapshotClearPayload decode(FriendlyByteBuf buffer) {
        return new MapSnapshotClearPayload();
    }
}
