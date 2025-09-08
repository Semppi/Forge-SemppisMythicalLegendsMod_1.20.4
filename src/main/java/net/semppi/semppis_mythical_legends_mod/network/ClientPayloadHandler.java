package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.common.Mod;

import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.client.ClientRegionState;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientPayloadHandler {
    private ClientPayloadHandler() {}

    @SubscribeEvent
    public static void onClientPayload(CustomPayloadEvent event) {
        FriendlyByteBuf buf = event.getPayload();
        if (buf == null || !buf.isReadable()) return;

        // Work on a copy so other listeners aren’t disturbed
        FriendlyByteBuf copy = new FriendlyByteBuf(buf.copy());

        ResourceLocation id;
        try {
            id = copy.readResourceLocation(); // first value we wrote on the server
        } catch (Exception ex) {
            return; // not our payload (or malformed)
        }
        if (!RegionSyncPayload.ID.equals(id)) return;

        RegionSyncPayload msg = RegionSyncPayload.decode(copy);
        Minecraft.getInstance().execute(() ->
                ClientRegionState.set(msg.toRegion())
        );
    }
}