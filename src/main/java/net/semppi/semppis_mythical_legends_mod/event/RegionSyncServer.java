package net.semppi.semppis_mythical_legends_mod.event;

import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.world.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RegionSyncServer {
    private static final RegionSampler SAMPLER = new RegionSampler();

    private static final Map<UUID, Region> LAST = new HashMap<>();
    private static final int INTERVAL = 10;
    private static final Map<UUID, Integer> COOLDOWN = new HashMap<>();

    /** Channel id used by both server and client */
    public static final ResourceLocation REGION_SYNC_ID =
            ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, "region_sync");

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
        BlockPos pos = sp.blockPosition();

        // BIOME-AWARE land tag
        Region land = SAMPLER.landRegion(level, pos.getX(), pos.getZ());


        land = snapToBiomeEdge(level, pos.getX(), pos.getZ(), land);

        // Seas can stay seed-only
        Region sea  = SAMPLER.seaRegion(level.getSeed(),  pos.getX(), pos.getZ());
        Region region = WaterMask.isWaterDominant(level, pos) ? sea : land;

        Region prev = LAST.get(sp.getUUID());
        if (!force && same(prev, region)) return;

        LAST.put(sp.getUUID(), region);
        sendPacket(sp, region);
    }

    private static Region snapToBiomeEdge(ServerLevel level, int x, int z, Region base) {
        // Quick out if we're clearly inside the same region
        Region n = SAMPLER.landRegion(level, x, z - 8);
        Region s = SAMPLER.landRegion(level, x, z + 8);
        Region w = SAMPLER.landRegion(level, x - 8, z);
        Region e = SAMPLER.landRegion(level, x + 8, z);
        int diff = 0;
        if (!same(n, base)) diff++;
        if (!same(s, base)) diff++;
        if (!same(w, base)) diff++;
        if (!same(e, base)) diff++;
        if (diff == 0) return base;

        // Near a boundary: look along the *same biome* directions and follow it
        Holder<Biome> here = level.getBiome(new BlockPos(x, level.getMinBuildHeight() + 64, z));
        int votesBase = 0, votesOther = 0;
        Region other = base;
        final int STEP = 12;

        for (net.minecraft.core.Direction d : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            int sx = x + d.getStepX() * STEP;
            int sz = z + d.getStepZ() * STEP;

            if (level.getBiome(new BlockPos(sx, level.getMinBuildHeight() + 64, sz)).equals(here)) {
                Region r = SAMPLER.landRegion(level, sx, sz);
                if (same(r, base)) votesBase++;
                else { votesOther++; other = r; }
            }
        }
        return (votesOther > votesBase) ? other : base;
    }

    /** Send a simple 4-byte payload: [ocean:boolean][c][d][s] */
    private static void sendPacket(ServerPlayer sp, Region region) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(4));
        buf.writeBoolean(region.ocean());
        if (region.ocean()) {
            buf.writeByte(0); // continent unused
            buf.writeByte(0); // subdir    unused
            buf.writeByte(region.sea().ordinal());
        } else {
            buf.writeByte(region.continent().ordinal());
            buf.writeByte(region.dir().ordinal());
            buf.writeByte(0); // sea unused
        }

        // Wrap in a CustomPacketPayload (this is what 1.20.4 expects)
        sp.connection.send(new ClientboundCustomPayloadPacket(new RawBufPayload(REGION_SYNC_ID, buf)));
    }

    private static boolean same(Region a, Region b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.ocean() != b.ocean()) return false;
        if (a.ocean()) return a.sea() == b.sea();
        return a.continent() == b.continent() && a.dir() == b.dir();
    }

    /** Minimal wrapper used to send a raw FriendlyByteBuf as a typed payload */
    private static final class RawBufPayload implements CustomPacketPayload {
        private final ResourceLocation id;
        private final FriendlyByteBuf data;

        RawBufPayload(ResourceLocation id, FriendlyByteBuf data) {
            this.id = id;
            this.data = data;
        }

        @Override public void write(FriendlyByteBuf out) {
            // write a copy so the original buffer can be GC’d safely
            out.writeBytes(data.copy());
        }

        @Override public ResourceLocation id() {
            return id;
        }
    }
}