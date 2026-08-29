package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable biome snapshot for one fixed 256x256-block map page.
 * A pixel value of zero is unexplored; other values are palette index + 1.
 */
public record MapSnapshotPayload(
        ResourceLocation dimension,
        int originX,
        int originZ,
        List<ResourceLocation> biomePalette,
        int[] biomePixels
) {
    public static final int SIZE = 256;
    public static final int PIXEL_COUNT = SIZE * SIZE;
    private static final int MAX_PALETTE_SIZE = 4_096;

    public MapSnapshotPayload {
        biomePalette = List.copyOf(biomePalette);
        biomePixels = biomePixels.clone();

        if (biomePalette.size() > MAX_PALETTE_SIZE) {
            throw new IllegalArgumentException("Map biome palette is too large");
        }
        if (biomePixels.length != PIXEL_COUNT) {
            throw new IllegalArgumentException(
                    "Map snapshot must contain exactly " + PIXEL_COUNT + " pixels"
            );
        }
    }

    @Override
    public int[] biomePixels() {
        return biomePixels.clone();
    }

    public void write(FriendlyByteBuf buffer) {
        buffer.writeResourceLocation(dimension);
        buffer.writeInt(originX);
        buffer.writeInt(originZ);
        buffer.writeVarInt(biomePalette.size());
        for (ResourceLocation biome : biomePalette) {
            buffer.writeResourceLocation(biome);
        }
        buffer.writeVarIntArray(biomePixels);
    }

    public static MapSnapshotPayload decode(FriendlyByteBuf buffer) {
        ResourceLocation dimension = buffer.readResourceLocation();
        int originX = buffer.readInt();
        int originZ = buffer.readInt();
        int paletteSize = buffer.readVarInt();
        if (paletteSize < 0 || paletteSize > MAX_PALETTE_SIZE) {
            throw new IllegalArgumentException("Invalid map biome palette size");
        }

        List<ResourceLocation> palette = new ArrayList<>(paletteSize);
        for (int index = 0; index < paletteSize; index++) {
            palette.add(buffer.readResourceLocation());
        }

        return new MapSnapshotPayload(
                dimension,
                originX,
                originZ,
                palette,
                buffer.readVarIntArray(PIXEL_COUNT)
        );
    }
}
