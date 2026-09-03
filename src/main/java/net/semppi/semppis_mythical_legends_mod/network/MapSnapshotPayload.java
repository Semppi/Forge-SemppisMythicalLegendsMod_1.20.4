package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable biome snapshot for one fixed 256x256-block map page.
 * A biome pixel value of zero is unexplored; other values are palette index
 * + 1. Region pixels use zero for unexplored and {@link MapRegionCode} for
 * discovered positions.
 */
public record MapSnapshotPayload(
        ResourceLocation dimension,
        int originX,
        int originZ,
        List<ResourceLocation> biomePalette,
        int[] biomePixels,
        byte[] surfacePixels,
        byte[] rawRegionPixels,
        byte[] attractedRegionPixels,
        byte[] regionPixels
) {
    public static final int SIZE = 256;
    public static final int PIXEL_COUNT = SIZE * SIZE;
    public static final byte SURFACE_UNKNOWN = 0;
    public static final byte SURFACE_DRY = 1;
    public static final byte SURFACE_WET = 2;
    private static final int MAX_PALETTE_SIZE = 4_096;

    public MapSnapshotPayload {
        biomePalette = List.copyOf(biomePalette);
        biomePixels = biomePixels.clone();
        surfacePixels = surfacePixels.clone();
        rawRegionPixels = rawRegionPixels.clone();
        attractedRegionPixels = attractedRegionPixels.clone();
        regionPixels = regionPixels.clone();

        if (biomePalette.size() > MAX_PALETTE_SIZE) {
            throw new IllegalArgumentException("Map biome palette is too large");
        }
        if (biomePixels.length != PIXEL_COUNT) {
            throw new IllegalArgumentException(
                    "Map snapshot must contain exactly " + PIXEL_COUNT + " pixels"
            );
        }
        if (regionPixels.length != PIXEL_COUNT) {
            throw new IllegalArgumentException(
                    "Map snapshot must contain exactly " + PIXEL_COUNT
                            + " region pixels"
            );
        }
        if (rawRegionPixels.length != PIXEL_COUNT
                || attractedRegionPixels.length != PIXEL_COUNT) {
            throw new IllegalArgumentException(
                    "Map snapshot diagnostic regions must contain exactly "
                            + PIXEL_COUNT + " pixels"
            );
        }
        if (surfacePixels.length != PIXEL_COUNT) {
            throw new IllegalArgumentException(
                    "Map snapshot must contain exactly " + PIXEL_COUNT
                            + " surface pixels"
            );
        }
    }

    @Override
    public int[] biomePixels() {
        return biomePixels.clone();
    }

    @Override
    public byte[] regionPixels() {
        return regionPixels.clone();
    }

    @Override
    public byte[] rawRegionPixels() {
        return rawRegionPixels.clone();
    }

    @Override
    public byte[] attractedRegionPixels() {
        return attractedRegionPixels.clone();
    }

    @Override
    public byte[] surfacePixels() {
        return surfacePixels.clone();
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
        buffer.writeByteArray(surfacePixels);
        buffer.writeByteArray(rawRegionPixels);
        buffer.writeByteArray(attractedRegionPixels);
        buffer.writeByteArray(regionPixels);
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
                buffer.readVarIntArray(PIXEL_COUNT),
                buffer.readByteArray(PIXEL_COUNT),
                buffer.readByteArray(PIXEL_COUNT),
                buffer.readByteArray(PIXEL_COUNT),
                buffer.readByteArray(PIXEL_COUNT)
        );
    }
}
