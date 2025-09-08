package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.network.RegionSyncPayload;
import net.semppi.semppis_mythical_legends_mod.world.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RegionSyncServer {
    private static final RegionSampler SAMPLER = new RegionSampler();

    private static final Map<UUID, Region> LAST = new HashMap<>();
    private static final int INTERVAL = 40; // was 10 -> check every ~2s
    private static final Map<UUID, Integer> COOLDOWN = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) sendIfChanged(sp, true);
    }

    @SuppressWarnings("removal")
    @SubscribeEvent
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

        // NEW: integrated (not dedicated) servers don't need the packet at all.
        if (!sp.getServer().isDedicatedServer()) return;

        if (!level.getGameRules().getBoolean(net.semppi.semppis_mythical_legends_mod.rules.SMLRules.CONTINENTAL_SPAWNING))
            return;

        BlockPos pos = sp.blockPosition();
        boolean aquatic = WaterMask.isWaterDominant(level, pos);

        Region region;
        int cx = pos.getX() >> 4, cz = pos.getZ() >> 4;
        if (level.getChunkSource().hasChunk(cx, cz)) {
            // loaded -> use cached biome-aware result
            region = net.semppi.semppis_mythical_legends_mod.spawn.RegionGate.peekRegion(
                    level, pos.getX(), pos.getZ(), aquatic
            );
        } else {
            // NEW: avoid any loads in new territory; use noise-only
            region = aquatic
                    ? SAMPLER.seaRegion(level.getSeed(), pos.getX(), pos.getZ())
                    : SAMPLER.landRegion(level.getSeed(), pos.getX(), pos.getZ());
        }

        Region prev = LAST.get(sp.getUUID());
        if (!force && same(prev, region)) return;

        LAST.put(sp.getUUID(), region);
        sendPacket(sp, region);
    }

    private static void sendPacket(ServerPlayer sp, Region region) {
        FriendlyByteBuf buf = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());

        // 1) Prefix our channel id so the client can tell whose packet this is
        buf.writeResourceLocation(RegionSyncPayload.ID);

        // 2) Then write the actual 4 bytes (your RegionSyncPayload)
        RegionSyncPayload.from(region).write(buf);

        // 3) Send as an "opaque" payload so Forge fires CustomPayloadEvent on the client
        sp.connection.send(new net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket(
                new RawBufPayload(RegionSyncPayload.ID, buf)
        ));
    }

    /** Minimal wrapper so we can send raw bytes under our own channel id */
    private static final class RawBufPayload implements net.minecraft.network.protocol.common.custom.CustomPacketPayload {
        private final net.minecraft.resources.ResourceLocation id;
        private final net.minecraft.network.FriendlyByteBuf data;

        RawBufPayload(net.minecraft.resources.ResourceLocation id, net.minecraft.network.FriendlyByteBuf data) {
            this.id = id;
            this.data = data;
        }
        @Override public void write(net.minecraft.network.FriendlyByteBuf out) { out.writeBytes(data.copy()); }
        @Override public net.minecraft.resources.ResourceLocation id() { return id; }
    }

    private static Region snapToBiomeEdge(ServerLevel level, int x, int z, Region base) {
        final int STEP = 16;
        int diff = 0;
        for (int oz = -1; oz <= 1; oz++) for (int ox = -1; ox <= 1; ox++) {
            if (ox == 0 && oz == 0) continue;
            Region r = SAMPLER.landRegion(level, x + ox * STEP, z + oz * STEP);
            if (!same(r, base)) diff++;
        }
        if (diff == 0) return base;

        int y0 = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        Holder<Biome> here = level.getBiome(new BlockPos(x, y0, z));

        int votesBase = 0, votesOther = 0;
        Region other = base;

        for (int oz = -1; oz <= 1; oz++) for (int ox = -1; ox <= 1; ox++) {
            if (ox == 0 && oz == 0) continue;
            int sx = x + ox * STEP, sz = z + oz * STEP;
            int sy = level.getHeight(Heightmap.Types.MOTION_BLOCKING, sx, sz);

            if (level.getBiome(new BlockPos(sx, sy, sz)).equals(here)) {
                Region r = SAMPLER.landRegion(level, sx, sz);
                if (same(r, base)) votesBase++; else { votesOther++; other = r; }
            }
        }
        return (votesOther > votesBase) ? other : base;
    }

    private static boolean same(Region a, Region b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.ocean() != b.ocean()) return false;
        return a.ocean() ? a.sea() == b.sea()
                : a.continent() == b.continent() && a.dir() == b.dir();
    }
}