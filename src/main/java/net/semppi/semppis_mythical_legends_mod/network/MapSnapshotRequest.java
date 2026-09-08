package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Client request for the fixed map page containing the player. */
public record MapSnapshotRequest() {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<UUID> ACTIVE_REQUESTS =
            ConcurrentHashMap.newKeySet();
    /**
     * One low-priority worker prevents diagnostic routing from taking the
     * integrated server thread away from chunk loading and entity ticks.
     * A single worker also keeps memory and CPU use bounded when P is pressed
     * repeatedly.
     */
    private static final ExecutorService MAP_WORKER =
            Executors.newSingleThreadExecutor(task -> {
                Thread thread = new Thread(task, "sml-test-map");
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            });

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
        UUID playerId = player.getUUID();
        if (!ACTIVE_REQUESTS.add(playerId)) {
            return;
        }
        MAP_WORKER.execute(() -> {
            MapSnapshotPayload snapshot;
            try {
                snapshot = ServerMapSnapshot.create(player);
            } catch (RuntimeException exception) {
                LOGGER.error("Failed to prepare SML test map", exception);
                ACTIVE_REQUESTS.remove(playerId);
                return;
            }
            var server = player.getServer();
            if (server == null) {
                ACTIVE_REQUESTS.remove(playerId);
                return;
            }
            server.execute(() -> {
                try {
                    if (!player.isRemoved()) {
                        SMLNetwork.sendTo(
                                player,
                                ServerMapDiscoveryState.merge(
                                        player, snapshot
                                )
                        );
                    }
                } finally {
                    ACTIVE_REQUESTS.remove(playerId);
                }
            });
        });
    }
}
