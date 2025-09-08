package net.semppi.semppis_mythical_legends_mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
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

        var left = event.getLeft();
        int insertAt = -1;
        for (int i = 0; i < left.size(); i++) {
            if (left.get(i).startsWith("Local Difficulty")) { insertAt = i; break; }
        }
        if (insertAt == -1) return;

        // Non-Overworld dimensions (including ALL modded ones) -> Unknown
        if (mc.level.dimension() != Level.OVERWORLD) {
            left.add(insertAt, "Region: Unknown");
            return;
        }

        Region region = null;

        if (mc.getSingleplayerServer() != null) {
            var sl = mc.getSingleplayerServer().getLevel(mc.level.dimension());
            if (sl == null) return;
            var pos = mc.player.blockPosition();
            var land = SAMPLER.landRegion(sl, pos.getX(), pos.getZ());
            var sea  = SAMPLER.seaRegion(sl, pos.getX(), pos.getZ());
            region = WaterMask.isWaterDominant(mc.level, pos) ? sea : land;
        } else {
            // multiplayer: read last server-sent value; if nothing yet, show Unknown
            region = net.semppi.semppis_mythical_legends_mod.client.ClientRegionState.get();
            if (region == null) {
                left.add(insertAt, "Region: Unknown");
                return;
            }
        }

        left.add(insertAt, "Region: " + region.display());
    }
}