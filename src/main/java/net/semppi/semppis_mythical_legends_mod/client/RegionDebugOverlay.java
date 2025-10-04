package net.semppi.semppis_mythical_legends_mod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.world.Region;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class RegionDebugOverlay {
    private RegionDebugOverlay() {}

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

        // Only show when Overworld *and* current chunk is loaded
        if (mc.level.dimension() != Level.OVERWORLD) {
            left.add(insertAt, "Region: Unknown");
            return;
        }
        int cx = Mth.floor(mc.player.getX()) >> 4;
        int cz = Mth.floor(mc.player.getZ()) >> 4;
        if (!mc.level.getChunkSource().hasChunk(cx, cz)) {
            left.add(insertAt, "Region: Unknown");
            return;
        }

        Region r = net.semppi.semppis_mythical_legends_mod.client.ClientRegionState.get();
        left.add(insertAt, "Region: " + (r != null ? r.display() : "Unknown"));
    }
}