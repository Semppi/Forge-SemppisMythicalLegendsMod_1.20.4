package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Session discovery memory for the early test map.
 *
 * <p>It is deliberately server-side and isolated by server instance. A later
 * map-persistence goal can replace this with world SavedData without changing
 * the request, payload or renderer contracts.</p>
 */
public final class ServerMapDiscoveryState {
    private static final Map<
            MinecraftServer,
            Map<UUID, Map<PageKey, StoredPage>>
    > SERVERS = new WeakHashMap<>();

    private ServerMapDiscoveryState() {}

    public static MapSnapshotPayload merge(
            ServerPlayer player,
            MapSnapshotPayload current
    ) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return current;
        }

        Map<PageKey, StoredPage> pages = SERVERS
                .computeIfAbsent(server, ignored -> new HashMap<>())
                .computeIfAbsent(player.getUUID(), ignored -> new HashMap<>());
        PageKey key = new PageKey(
                current.dimension(),
                current.originX(),
                current.originZ()
        );
        StoredPage page = pages.computeIfAbsent(
                key,
                ignored -> new StoredPage()
        );
        page.merge(current);
        return page.snapshot(key);
    }

    /** Returns the number of accumulated pages removed for this player. */
    public static int clear(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return 0;
        }

        Map<UUID, Map<PageKey, StoredPage>> players = SERVERS.get(server);
        if (players == null) {
            return 0;
        }

        Map<PageKey, StoredPage> removed = players.remove(player.getUUID());
        if (players.isEmpty()) {
            SERVERS.remove(server);
        }
        return removed == null ? 0 : removed.size();
    }

    private record PageKey(
            ResourceLocation dimension,
            int originX,
            int originZ
    ) {}

    private static final class StoredPage {
        private final List<ResourceLocation> palette = new ArrayList<>();
        private final Map<ResourceLocation, Integer> paletteIndices =
                new LinkedHashMap<>();
        private final int[] pixels =
                new int[MapSnapshotPayload.PIXEL_COUNT];
        private final byte[] regionPixels =
                new byte[MapSnapshotPayload.PIXEL_COUNT];

        private void merge(MapSnapshotPayload current) {
            List<ResourceLocation> currentPalette = current.biomePalette();
            int[] currentPixels = current.biomePixels();
            byte[] currentRegionPixels = current.regionPixels();

            for (int pixel = 0; pixel < currentPixels.length; pixel++) {
                int encodedBiome = currentPixels[pixel];
                if (encodedBiome == 0) {
                    continue;
                }

                ResourceLocation biome = currentPalette.get(encodedBiome - 1);
                int storedIndex = paletteIndices.computeIfAbsent(
                        biome,
                        ignored -> {
                            palette.add(biome);
                            return palette.size() - 1;
                        }
                );
                pixels[pixel] = storedIndex + 1;
                regionPixels[pixel] = currentRegionPixels[pixel];
            }
        }

        private MapSnapshotPayload snapshot(PageKey key) {
            return new MapSnapshotPayload(
                    key.dimension(),
                    key.originX(),
                    key.originZ(),
                    palette,
                    pixels,
                    regionPixels
            );
        }
    }
}
