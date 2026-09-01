package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionGate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Builds map snapshots without loading or generating any additional chunks. */
public final class ServerMapSnapshot {
    private static final int REGION_TILE_SIZE = 32;
    private static final int MIN_REGION_TILE_SIZE = 4;

    private ServerMapSnapshot() {}

    public static MapSnapshotPayload create(ServerPlayer player) {
        int originX = Math.floorDiv(
                player.blockPosition().getX(),
                MapSnapshotPayload.SIZE
        ) * MapSnapshotPayload.SIZE;
        int originZ = Math.floorDiv(
                player.blockPosition().getZ(),
                MapSnapshotPayload.SIZE
        ) * MapSnapshotPayload.SIZE;

        int[] pixels = new int[MapSnapshotPayload.PIXEL_COUNT];
        byte[] surfacePixels = new byte[MapSnapshotPayload.PIXEL_COUNT];
        byte[] regionPixels = new byte[MapSnapshotPayload.PIXEL_COUNT];
        List<ResourceLocation> palette = new ArrayList<>();
        Map<ResourceLocation, Integer> paletteIndices = new LinkedHashMap<>();

        int firstChunkX = Math.floorDiv(originX, 16);
        int firstChunkZ = Math.floorDiv(originZ, 16);

        for (int chunkOffsetZ = 0; chunkOffsetZ < 16; chunkOffsetZ++) {
            for (int chunkOffsetX = 0; chunkOffsetX < 16; chunkOffsetX++) {
                LevelChunk chunk = player.serverLevel().getChunkSource().getChunkNow(
                        firstChunkX + chunkOffsetX,
                        firstChunkZ + chunkOffsetZ
                );
                if (chunk == null) {
                    continue;
                }

                sampleChunk(
                        chunk,
                        originX,
                        originZ,
                        chunkOffsetX * 16,
                        chunkOffsetZ * 16,
                        palette,
                        paletteIndices,
                        pixels,
                        surfacePixels
                );
            }
        }

        if (player.serverLevel().dimension() == Level.OVERWORLD) {
            sampleRegionTiles(
                    player,
                    originX,
                    originZ,
                    pixels,
                    regionPixels
            );
        }

        return new MapSnapshotPayload(
                player.serverLevel().dimension().location(),
                originX,
                originZ,
                palette,
                pixels,
                surfacePixels,
                regionPixels
        );
    }

    private static void sampleChunk(
            LevelChunk chunk,
            int originX,
            int originZ,
            int pixelStartX,
            int pixelStartZ,
            List<ResourceLocation> palette,
            Map<ResourceLocation, Integer> paletteIndices,
            int[] pixels,
            byte[] surfacePixels
    ) {
        for (int localZ = 0; localZ < 16; localZ++) {
            int pixelZ = pixelStartZ + localZ;
            int worldZ = originZ + pixelZ;

            for (int localX = 0; localX < 16; localX++) {
                int pixelX = pixelStartX + localX;
                int worldX = originX + pixelX;
                int surfaceY = chunk.getHeight(
                        Heightmap.Types.WORLD_SURFACE,
                        localX,
                        localZ
                );
                Holder<Biome> biome = chunk.getNoiseBiome(
                        QuartPos.fromBlock(worldX),
                        QuartPos.fromBlock(surfaceY),
                        QuartPos.fromBlock(worldZ)
                );
                ResourceLocation biomeId = biome.unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(null);
                if (biomeId == null) {
                    continue;
                }

                int paletteIndex = paletteIndices.computeIfAbsent(
                        biomeId,
                        ignored -> {
                            palette.add(biomeId);
                            return palette.size() - 1;
                        }
                );
                int pixel = pixelZ * MapSnapshotPayload.SIZE + pixelX;
                pixels[pixel] = paletteIndex + 1;
                BlockPos surface = new BlockPos(
                        worldX,
                        Math.max(chunk.getMinBuildHeight(), surfaceY - 1),
                        worldZ
                );
                surfacePixels[pixel] = chunk.getFluidState(surface).isEmpty()
                        ? MapSnapshotPayload.SURFACE_DRY
                        : MapSnapshotPayload.SURFACE_WET;
            }
        }
    }

    /**
     * Uniform continental interiors are cheap, while tiles containing an
     * actual boundary recursively retain the resolver's four-block detail.
     */
    private static void sampleRegionTiles(
            ServerPlayer player,
            int originX,
            int originZ,
            int[] biomePixels,
            byte[] regionPixels
    ) {
        for (int z = 0; z < MapSnapshotPayload.SIZE; z += REGION_TILE_SIZE) {
            for (int x = 0; x < MapSnapshotPayload.SIZE; x += REGION_TILE_SIZE) {
                sampleRegionTile(
                        player,
                        originX,
                        originZ,
                        biomePixels,
                        regionPixels,
                        x,
                        z,
                        REGION_TILE_SIZE
                );
            }
        }
    }

    private static void sampleRegionTile(
            ServerPlayer player,
            int originX,
            int originZ,
            int[] biomePixels,
            byte[] regionPixels,
            int startX,
            int startZ,
            int size
    ) {
        Discovery coverage = discoveryCoverage(
                biomePixels,
                startX,
                startZ,
                size
        );
        if (coverage == Discovery.NONE) {
            return;
        }

        if (coverage == Discovery.FULL) {
            byte topLeft = resolveRegionCode(
                    player, originX + startX, originZ + startZ
            );
            byte topRight = resolveRegionCode(
                    player,
                    originX + startX + size - 1,
                    originZ + startZ
            );
            byte bottomLeft = resolveRegionCode(
                    player,
                    originX + startX,
                    originZ + startZ + size - 1
            );
            byte bottomRight = resolveRegionCode(
                    player,
                    originX + startX + size - 1,
                    originZ + startZ + size - 1
            );
            byte center = resolveRegionCode(
                    player,
                    originX + startX + size / 2,
                    originZ + startZ + size / 2
            );

            int overlayIdentity = MapRegionCode.overlayIdentity(topLeft);
            if (overlayIdentity == MapRegionCode.overlayIdentity(topRight)
                    && overlayIdentity == MapRegionCode.overlayIdentity(
                            bottomLeft
                    )
                    && overlayIdentity == MapRegionCode.overlayIdentity(
                            bottomRight
                    )
                    && overlayIdentity == MapRegionCode.overlayIdentity(center)) {
                fillRegionTile(
                        regionPixels, startX, startZ, size, topLeft
                );
                return;
            }
        }

        if (size <= MIN_REGION_TILE_SIZE) {
            for (int z = startZ; z < startZ + size; z++) {
                for (int x = startX; x < startX + size; x++) {
                    int pixel = z * MapSnapshotPayload.SIZE + x;
                    if (biomePixels[pixel] != 0) {
                        regionPixels[pixel] = resolveRegionCode(
                                player, originX + x, originZ + z
                        );
                    }
                }
            }
            return;
        }

        int half = size / 2;
        sampleRegionTile(
                player, originX, originZ, biomePixels, regionPixels,
                startX, startZ, half
        );
        sampleRegionTile(
                player, originX, originZ, biomePixels, regionPixels,
                startX + half, startZ, half
        );
        sampleRegionTile(
                player, originX, originZ, biomePixels, regionPixels,
                startX, startZ + half, half
        );
        sampleRegionTile(
                player, originX, originZ, biomePixels, regionPixels,
                startX + half, startZ + half, half
        );
    }

    private static byte resolveRegionCode(
            ServerPlayer player, int worldX, int worldZ
    ) {
        return MapRegionCode.encode(
                RegionGate.resolve(
                        player.serverLevel(), worldX, worldZ
                ).region()
        );
    }

    private static Discovery discoveryCoverage(
            int[] biomePixels, int startX, int startZ, int size
    ) {
        boolean discovered = false;
        boolean unexplored = false;
        for (int z = startZ; z < startZ + size; z++) {
            int row = z * MapSnapshotPayload.SIZE;
            for (int x = startX; x < startX + size; x++) {
                if (biomePixels[row + x] == 0) {
                    unexplored = true;
                } else {
                    discovered = true;
                }
                if (discovered && unexplored) {
                    return Discovery.PARTIAL;
                }
            }
        }
        return discovered ? Discovery.FULL : Discovery.NONE;
    }

    private static void fillRegionTile(
            byte[] regionPixels,
            int startX,
            int startZ,
            int size,
            byte region
    ) {
        for (int z = startZ; z < startZ + size; z++) {
            int row = z * MapSnapshotPayload.SIZE;
            for (int x = startX; x < startX + size; x++) {
                regionPixels[row + x] = region;
            }
        }
    }

    private enum Discovery {
        NONE,
        PARTIAL,
        FULL
    }
}
