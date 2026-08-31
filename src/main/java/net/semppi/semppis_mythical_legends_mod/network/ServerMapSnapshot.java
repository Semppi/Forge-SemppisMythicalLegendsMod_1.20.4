package net.semppi.semppis_mythical_legends_mod.network;

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
                        regionPixels,
                        player
                );
            }
        }

        return new MapSnapshotPayload(
                player.serverLevel().dimension().location(),
                originX,
                originZ,
                palette,
                pixels,
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
            byte[] regionPixels,
            ServerPlayer player
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
                if (player.serverLevel().dimension() == Level.OVERWORLD) {
                    regionPixels[pixel] = MapRegionCode.encode(
                            RegionGate.resolve(
                                    player.serverLevel(),
                                    worldX,
                                    worldZ
                            ).region()
                    );
                }
            }
        }
    }
}
