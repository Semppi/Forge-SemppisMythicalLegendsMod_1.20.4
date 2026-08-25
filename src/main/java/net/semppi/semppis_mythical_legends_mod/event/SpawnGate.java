package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SpawnGate {
    private SpawnGate() {}

    // Keep the safety nets on; we prefilter aggressively so cost is tiny.
    private static final boolean EXTRA_SAFETY_GATES = false;

    private static boolean isOurMob(EntityType<?> type) {
        var id = type.builtInRegistryHolder().key().location();
        return SemppisMythicalLegendsMod.MOD_ID.equals(id.getNamespace());
    }

    @SubscribeEvent
    public static void onSpawnPlacement(MobSpawnEvent.SpawnPlacementCheck e) {
        // Disable: our SpawnPlacements predicates already enforce the rules,
        // and doing extra work here during worldgen can cause sync loads/hitches.
        return;
    }

    @SubscribeEvent
    public static void onFinalize(MobSpawnEvent.FinalizeSpawn e) {
        if (!EXTRA_SAFETY_GATES) return;
        if (!(e.getLevel() instanceof ServerLevel sl)) return;
        if (!sl.getGameRules().getBoolean(
                net.semppi.semppis_mythical_legends_mod.rules.SMLRules.CONTINENTAL_SPAWNING)) return;
        // e.getEntity() is already a Mob here — no instanceof pattern needed.
        Mob mob = e.getEntity();
        if (!isOurMob(mob.getType())) return;

        if (!net.semppi.semppis_mythical_legends_mod.spawn.RegionGateCached.allows(
                sl, mob.getType(), mob.blockPosition(), e.getSpawnType())) {
            e.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent e) {
        if (!EXTRA_SAFETY_GATES) return;
        if (!(e.getLevel() instanceof ServerLevel sl)) return;
        if (!sl.getGameRules().getBoolean(
                net.semppi.semppis_mythical_legends_mod.rules.SMLRules.CONTINENTAL_SPAWNING)) return;
        if (!(e.getEntity() instanceof net.minecraft.world.entity.Mob mob)) return; // OK here: Join event is Entity
        if (!isOurMob(mob.getType())) return;
        if (mob.isBaby()
                && !net.semppi.semppis_mythical_legends_mod.spawn.RegionMobAllow.isBreedingRestricted(mob.getType()))
            return;

        if (!net.semppi.semppis_mythical_legends_mod.spawn.RegionGateCached.allows(
                sl, mob.getType(), mob.blockPosition())) {
            e.setCanceled(true);
        }
    }
}
