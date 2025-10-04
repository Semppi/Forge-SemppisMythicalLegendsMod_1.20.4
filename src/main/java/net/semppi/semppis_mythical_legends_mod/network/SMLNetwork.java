package net.semppi.semppis_mythical_legends_mod.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;

public final class SMLNetwork {
    private SMLNetwork() {}

    public static final int PROTOCOL = 1; // int protocol version
    public static final ResourceLocation CHANNEL_ID =
            new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "main");

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(CHANNEL_ID)
            .networkProtocolVersion(PROTOCOL)                    // int
            .clientAcceptedVersions((status, ver) -> ver == PROTOCOL)
            .serverAcceptedVersions((status, ver) -> ver == PROTOCOL)
            .simpleChannel();

    public static void init() {
        int id = 0;

        CHANNEL.messageBuilder(RegionSyncPayload.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RegionSyncPayload::write)
                .decoder(RegionSyncPayload::decode)
                .consumerMainThread((msg, ctx) -> {
                    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                            net.semppi.semppis_mythical_legends_mod.client.ClientRegionState.set(msg.toRegion())
                    );
                })
                .add();
    }

    // Preferred path on Forge 49.x
    public static void sendTo(ServerPlayer player, RegionSyncPayload msg) {
        CHANNEL.send(msg, PacketDistributor.PLAYER.with(player)); // (message, target)
    }

    // If the above doesn't exist in your workspace, comment it out and use this instead:
//    public static void sendTo(ServerPlayer player, RegionSyncPayload msg) {
//        CHANNEL.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
//    }
}