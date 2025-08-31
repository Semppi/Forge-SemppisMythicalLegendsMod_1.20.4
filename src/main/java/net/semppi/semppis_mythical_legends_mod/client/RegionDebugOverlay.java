package net.semppi.semppis_mythical_legends_mod.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionSampler;
import net.semppi.semppis_mythical_legends_mod.world.WaterMask;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RegionDebugOverlay {
    private static final RegionSampler SAMPLER = new RegionSampler();

    @SubscribeEvent
    public static void onDebugText(net.minecraftforge.client.event.CustomizeGuiOverlayEvent.DebugText event) {
        var mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        // Decide the region source
        Region region = null;

        if (mc.getSingleplayerServer() != null) {
            // singleplayer: compute locally
            var sl = mc.getSingleplayerServer().getLevel(mc.level.dimension());
            if (sl == null) return;
            long seed = sl.getSeed();
            var pos = mc.player.blockPosition();
            var land = SAMPLER.landRegion(seed, pos.getX(), pos.getZ());
            var sea  = SAMPLER.seaRegion(seed,  pos.getX(), pos.getZ());
            region = WaterMask.isWaterDominant(mc.level, pos) ? sea : land;
        } else {
            // multiplayer: read last server-sent value
            region = net.semppi.semppis_mythical_legends_mod.client.ClientRegionState.get();
            if (region == null) return; // nothing received yet
        }

        var left = event.getLeft();
        int insertAt = -1;
        for (int i = 0; i < left.size(); i++) {
            if (left.get(i).startsWith("Local Difficulty")) { insertAt = i; break; }
        }
        if (insertAt == -1) return; // not the F3 list
        left.add(insertAt, "Region: " + region.display());
    }
}