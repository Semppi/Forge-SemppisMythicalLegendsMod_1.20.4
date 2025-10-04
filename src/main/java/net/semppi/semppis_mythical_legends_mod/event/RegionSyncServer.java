package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionSampler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RegionSyncServer {
    private static final Logger LOG = LogManager.getLogger(RegionSyncServer.class);

    private static final RegionSampler SAMPLER = new RegionSampler();
    private static final Map<UUID, Region> LAST_REGION = new HashMap<>();
    private static final Map<UUID, Long> LAST_CHUNK_KEY = new HashMap<>();
    private static final Map<UUID, Integer> COOLDOWN = new HashMap<>();

    // how often we *allow* a recalculation at most (ticks)
    private static final int INTERVAL = 40; // ~2s

    private RegionSyncServer() {}

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            // force an initial sync on login
            sendIfChanged(sp, true);
        }
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

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            UUID id = sp.getUUID();
            LAST_REGION.remove(id);
            LAST_CHUNK_KEY.remove(id);
            COOLDOWN.remove(id);
        }
    }

    private static long chunkKey(int cx, int cz) {
        return (((long) cx) << 32) ^ (cz & 0xffffffffL);
    }

    private static void sendIfChanged(ServerPlayer sp, boolean force) {
        ServerLevel level = sp.serverLevel();
        if (level.dimension() != Level.OVERWORLD) return;

        BlockPos pos = sp.blockPosition();
        int cx = pos.getX() >> 4, cz = pos.getZ() >> 4;
        long key = chunkKey(cx, cz);

        // Only recompute when entering a new chunk (unless forced)
        if (!force) {
            Long prevKey = LAST_CHUNK_KEY.get(sp.getUUID());
            if (prevKey != null && prevKey == key) return;
        }
        LAST_CHUNK_KEY.put(sp.getUUID(), key);

        // Never force a chunk load: only proceed if this chunk is already present
        if (level.getChunkSource().getChunkNow(cx, cz) == null) return;

        boolean aquatic =
                         level.getFluidState(pos).is(FluidTags.WATER)
                              || level.getFluidState(pos.below()).is(FluidTags.WATER)
                              || net.semppi.semppis_mythical_legends_mod.world.WaterMask.isWaterDominant(level, pos)
                              || net.semppi.semppis_mythical_legends_mod.world.WaterMask.isWaterBiome(
                                     level.getBiome(pos));

        // Sample at the chunk center with noise-only sampler (stable & cheap)
        int sx = (cx << 4) + 8;
        int sz = (cz << 4) + 8;
        Region region = aquatic
                ? SAMPLER.seaRegion(level, sx, sz)
                : SAMPLER.landRegion(level, sx, sz);

        Region prev = LAST_REGION.get(sp.getUUID());
        if (!force && same(prev, region)) return;

        LAST_REGION.put(sp.getUUID(), region);
        sendPacket(sp, region);

        // Debug logging is nice but can be noisy; keep it off by default
        // LOG.debug("[SML] Sync region to {}: {}", sp.getGameProfile().getName(), region.display());
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