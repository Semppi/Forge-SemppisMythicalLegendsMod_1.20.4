package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

/** Client request for the fixed map page containing the player. */
public record MapSnapshotRequest() {

    public void write(FriendlyByteBuf buffer) {
        // The server derives the page and dimension from the sender.
    }

    public static MapSnapshotRequest decode(FriendlyByteBuf buffer) {
        return new MapSnapshotRequest();
    }

    public static void handle(
            MapSnapshotRequest request,
            CustomPayloadEvent.Context context
    ) {
        ServerPlayer player = context.getSender();
        if (player == null) {
            return;
        }

        SMLNetwork.sendTo(
                player,
                ServerMapSnapshot.create(player)
        );
    }
}
