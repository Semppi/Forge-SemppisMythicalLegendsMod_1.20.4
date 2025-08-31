package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.rules.SMLRules;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionMobAllow;
import net.semppi.semppis_mythical_legends_mod.world.*;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpawnGate {
    private static final RegionSampler SAMPLER = new RegionSampler();

    private SpawnGate() {}

    @SubscribeEvent
    public static void onSpawnPlacement(MobSpawnEvent.SpawnPlacementCheck e) {
        // Only gate natural/chunk-generation spawns
        var type = e.getSpawnType();
        if (type != MobSpawnType.NATURAL && type != MobSpawnType.CHUNK_GENERATION) return;

        // Only handle real server levels
        if (!(e.getLevel() instanceof ServerLevel level)) return;

        // If gamerule is OFF, skip all continental checks
        if (!level.getGameRules().getBoolean(SMLRules.CONTINENTAL_SPAWNING)) return;

        BlockPos pos = e.getPos();

        // Land (biome-aware) + sea (seed-only), then choose by local water dominance
        Region land = SAMPLER.landRegion(level, pos.getX(), pos.getZ());
        Region sea  = SAMPLER.seaRegion(level.getSeed(), pos.getX(), pos.getZ());
        Region region = WaterMask.isWaterDominant(level, pos) ? sea : land;

        var biome = level.getBiome(pos);      // Holder<Biome>
        var entityType = e.getEntityType();   // EntityType<?>

        if (region.ocean()) {
            if (!RegionMobAllow.isAllowedForSea(entityType, region.sea())) {
                e.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.DENY);
            }
        } else {
            if (!RegionCompat.isAllowedForBiome(biome, region)
                    || !RegionMobAllow.isAllowedForLand(entityType, region.continent(), region.dir())) {
                e.setResult(MobSpawnEvent.SpawnPlacementCheck.Result.DENY);
            }
        }
    }
}