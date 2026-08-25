package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionGate;
import net.semppi.semppis_mythical_legends_mod.world.Region;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(
        modid = SemppisMythicalLegendsMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class RegionSyncServer {
    private static final Map<UUID, Region> LAST_REGION = new HashMap<>();
    private static final Map<UUID, Integer> COOLDOWN = new HashMap<>();
    private static final int INTERVAL = 40;

    private RegionSyncServer() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            sendIfChanged(player, true);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("removal")
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!(event.player instanceof ServerPlayer player)) return;
        if (event.phase != TickEvent.Phase.END) return;

        int cooldown = COOLDOWN.getOrDefault(player.getUUID(), 0);
        if (cooldown > 0) {
            COOLDOWN.put(player.getUUID(), cooldown - 1);
            return;
        }

        COOLDOWN.put(player.getUUID(), INTERVAL);
        sendIfChanged(player, false);
    }

    private static void sendIfChanged(ServerPlayer player, boolean force) {
        ServerLevel level = player.serverLevel();
        if (level.dimension() != Level.OVERWORLD) return;

        var pos = player.blockPosition();
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        if (level.getChunkSource().getChunkNow(chunkX, chunkZ) == null) return;

        // The classifier samples the surface biome. A player standing in a
        // cave therefore sees the same region as the surface above.
        Region region = RegionGate.sampleNow(
                level, pos.getX(), pos.getZ()
        );

        Region previous = LAST_REGION.get(player.getUUID());
        if (!force && same(previous, region)) return;

        LAST_REGION.put(player.getUUID(), region);
        net.semppi.semppis_mythical_legends_mod.network.SMLNetwork.sendTo(
                player,
                net.semppi.semppis_mythical_legends_mod.network.RegionSyncPayload
                        .from(region)
        );
    }

    private static boolean same(Region first, Region second) {
        if (first == second) return true;
        if (first == null || second == null) return false;
        if (first.ocean() != second.ocean()) return false;

        return first.ocean()
                ? first.sea() == second.sea()
                : first.continent() == second.continent()
                && first.dir() == second.dir();
    }
}
