package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.semppi.semppis_mythical_legends_mod.world.RegionBoundaryRouter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Client request for the fixed map page containing the player. */
public record MapSnapshotRequest() {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Set<UUID> ACTIVE_REQUESTS =
            ConcurrentHashMap.newKeySet();
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
        try {
            RegionBoundaryRouter.prepareForMap(
                    player.serverLevel(),
                    player.blockPosition().getX(),
                    player.blockPosition().getZ(),
                    () -> finishRequest(player, playerId)
            );
        } catch (RuntimeException exception) {
            ACTIVE_REQUESTS.remove(playerId);
            LOGGER.error("Failed to prepare SML test map", exception);
        }
    }

    private static void finishRequest(ServerPlayer player, UUID playerId) {
        long started = System.nanoTime();
        try {
            if (!player.isRemoved()) {
                SMLNetwork.sendTo(
                        player,
                        ServerMapDiscoveryState.merge(
                                player, ServerMapSnapshot.create(player)
                        )
                );
            }
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to prepare SML test map", exception);
        } finally {
            LOGGER.info(
                    "SML test map snapshot finished in {} ms after border preparation",
                    (System.nanoTime() - started) / 1_000_000L
            );
            ACTIVE_REQUESTS.remove(playerId);
        }
    }
}
