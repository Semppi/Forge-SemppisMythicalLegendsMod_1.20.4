package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.network.CustomPayloadEvent;

import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.client.ClientRegionState;
import net.semppi.semppis_mythical_legends_mod.world.*;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientPayloadHandler {
    private ClientPayloadHandler() {}

    @SubscribeEvent
    public static void onCustomPayload(CustomPayloadEvent event) {
        // Forge will give us a FriendlyByteBuf for unknown/foreign custom payloads.
        FriendlyByteBuf buf = event.getPayload();
        if (buf == null) return;                 // not a raw-buf payload -> ignore
        if (buf.readableBytes() != 4) return;    // not our 4-byte message -> ignore

        boolean ocean = buf.readBoolean();
        int b1 = buf.readByte() & 0xFF;
        int b2 = buf.readByte() & 0xFF;
        int b3 = buf.readByte() & 0xFF;

        event.getSource().enqueueWork(() -> {
            if (ocean) {
                if (b3 < Ocean.values().length) {
                    ClientRegionState.set(Region.sea(Ocean.values()[b3]));
                }
            } else {
                if (b1 < Continent.values().length && b2 < SubDir.values().length) {
                    ClientRegionState.set(Region.land(
                            Continent.values()[b1],
                            SubDir.values()[b2]
                    ));
                }
            }
        });
    }
}