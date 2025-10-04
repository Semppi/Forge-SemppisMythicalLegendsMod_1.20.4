package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.semppi.semppis_mythical_legends_mod.world.*;

public record RegionSyncPayload(boolean ocean, int c, int d, int s) {
    /** Encoder (payload -> bytes) */
    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(ocean);
        buf.writeByte(c);
        buf.writeByte(d);
        buf.writeByte(s);
    }

    /** Decoder (bytes -> payload) */
    public static RegionSyncPayload decode(FriendlyByteBuf buf) {
        return new RegionSyncPayload(
                buf.readBoolean(),
                buf.readUnsignedByte(),
                buf.readUnsignedByte(),
                buf.readUnsignedByte()
        );
    }

    /** Convenience for the client handler */
    public Region toRegion() {
        return ocean
                ? Region.sea(Ocean.values()[s])
                : Region.land(Continent.values()[c], SubDir.values()[d]);
    }

    public static RegionSyncPayload from(Region r) {
        return r.ocean()
                ? new RegionSyncPayload(true, 0, 0, r.sea().ordinal())
                : new RegionSyncPayload(false, r.continent().ordinal(), r.dir().ordinal(), 0);
    }
}