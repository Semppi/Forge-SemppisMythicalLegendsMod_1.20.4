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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RegionSyncServer {
    private static final Logger LOG = LogManager.getLogger(RegionSyncServer.class);
    private static final Map<UUID, net.minecraft.resources.ResourceLocation> LAST_BIOME = new HashMap<>();
    private static final Map<UUID, Region> LAST_REGION = new HashMap<>();
    private static final Map<UUID, Integer> COOLDOWN = new HashMap<>();
    private static final int INTERVAL = 40; // ~2s; lower if you want snappier HUD

    private RegionSyncServer() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) sendIfChanged(sp, true);
    }

    @SubscribeEvent @SuppressWarnings("removal")
    public static void onPlayerTick(TickEvent.PlayerTickEvent e) {
        if (!(e.player instanceof ServerPlayer sp)) return;
        if (e.phase != TickEvent.Phase.END) return;

        int cd = COOLDOWN.getOrDefault(sp.getUUID(), 0);
        if (cd > 0) { COOLDOWN.put(sp.getUUID(), cd - 1); return; }
        COOLDOWN.put(sp.getUUID(), INTERVAL);

        sendIfChanged(sp, false);
    }

    private static void sendIfChanged(ServerPlayer sp, boolean force) {
        ServerLevel level = sp.serverLevel();
        if (level.dimension() != Level.OVERWORLD) return;

        var pos = sp.blockPosition();

        // Don’t force chunk loads for HUD updates
        int cx = pos.getX() >> 4, cz = pos.getZ() >> 4;
        if (level.getChunkSource().getChunkNow(cx, cz) == null) return;

        // Track current biome under the player (debug/useful)
        var biomeKey = level.getBiome(pos).unwrapKey().map(k -> k.location()).orElse(null);
        LAST_BIOME.put(sp.getUUID(), biomeKey);

        // Decide aquatic strictly by biome tags at the player position
        var biome = level.getBiome(pos);
        boolean isOcean = biome.is(net.minecraft.tags.BiomeTags.IS_OCEAN) || biome.is(net.minecraft.tags.BiomeTags.IS_DEEP_OCEAN);
        boolean isBeach = biome.is(net.minecraft.tags.BiomeTags.IS_BEACH);
        boolean isRiver = biome.is(net.minecraft.tags.BiomeTags.IS_RIVER);
        boolean aquatic = isOcean && !isBeach && !isRiver;

        int x = pos.getX(), z = pos.getZ();

        // Uncached, consensus-aware sample (accurate to the actual region now)
        Region region = RegionGate.sampleNow(level, x, z, aquatic);

        Region prev = LAST_REGION.get(sp.getUUID());
        if (!force && same(prev, region)) return;

        LAST_REGION.put(sp.getUUID(), region);
        sendPacket(sp, region);
    }

    private static void sendPacket(ServerPlayer sp, Region region) {
        net.semppi.semppis_mythical_legends_mod.network.SMLNetwork
                .sendTo(sp, net.semppi.semppis_mythical_legends_mod.network.RegionSyncPayload.from(region));
    }

    private static boolean same(Region a, Region b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.ocean() != b.ocean()) return false;
        return a.ocean() ? a.sea() == b.sea()
                : a.continent() == b.continent() && a.dir() == b.dir();
    }
}