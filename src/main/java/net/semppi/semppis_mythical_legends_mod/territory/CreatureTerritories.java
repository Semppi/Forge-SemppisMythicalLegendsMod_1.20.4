package net.semppi.semppis_mythical_legends_mod.territory;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** World records for persistent creatures, including owners whose chunks are unloaded. */
public final class CreatureTerritories extends SavedData {
    private static final String DATA_NAME = "sml_creature_territories";

    private final Map<UUID, Territory> territories = new HashMap<>();

    public static CreatureTerritories get(ServerLevel level) {
        return level.getServer().overworld().getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(CreatureTerritories::new, CreatureTerritories::load, null),
                DATA_NAME
        );
    }

    public void claim(UUID owner, ServerLevel level, BlockPos center,
                      int backyardRadius, int patrolRadius, CompoundTag ownerData) {
        Territory territory = new Territory(level.dimension().location(), center.immutable(),
                backyardRadius, patrolRadius, ownerData.copy());
        if (territories.putIfAbsent(owner, territory) == null) {
            setDirty();
        }
    }

    @Nullable
    public Territory getTerritory(UUID owner) {
        return territories.get(owner);
    }

    public void updateOwnerData(UUID owner, CompoundTag ownerData) {
        Territory territory = territories.get(owner);
        if (territory == null) {
            return;
        }
        CompoundTag merged = territory.ownerData();
        merged.merge(ownerData.copy());
        if (!merged.equals(territory.ownerData())) {
            territories.put(owner, new Territory(territory.dimension(), territory.center(),
                    territory.backyardRadius(), territory.patrolRadius(), merged));
            setDirty();
        }
    }

    public void remove(UUID owner) {
        if (territories.remove(owner) != null) {
            setDirty();
        }
    }

    private static CreatureTerritories load(CompoundTag tag) {
        CreatureTerritories result = new CreatureTerritories();
        ListTag entries = tag.getList("Territories", Tag.TAG_COMPOUND);
        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            result.territories.put(entry.getUUID("Owner"), new Territory(
                    new ResourceLocation(entry.getString("Dimension")),
                    BlockPos.of(entry.getLong("Center")),
                    entry.getInt("BackyardRadius"),
                    entry.getInt("PatrolRadius"),
                    entry.getCompound("OwnerData")
            ));
        }
        return result;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag entries = new ListTag();
        territories.forEach((owner, territory) -> {
            CompoundTag entry = new CompoundTag();
            entry.putUUID("Owner", owner);
            entry.putString("Dimension", territory.dimension().toString());
            entry.putLong("Center", territory.center().asLong());
            entry.putInt("BackyardRadius", territory.backyardRadius());
            entry.putInt("PatrolRadius", territory.patrolRadius());
            entry.put("OwnerData", territory.ownerData());
            entries.add(entry);
        });
        tag.put("Territories", entries);
        return tag;
    }

    public enum Zone {
        BACKYARD, PATROL, OUTSIDE
    }

    public record Territory(ResourceLocation dimension, BlockPos center, int backyardRadius,
                            int patrolRadius, CompoundTag ownerData) {
        @Override
        public CompoundTag ownerData() {
            return ownerData.copy();
        }

        public Zone zoneAt(ServerLevel level, BlockPos pos) {
            if (!dimension.equals(level.dimension().location())) {
                return Zone.OUTSIDE;
            }

            long dx = (long) pos.getX() - center.getX();
            long dz = (long) pos.getZ() - center.getZ();
            long distanceSquared = dx * dx + dz * dz;
            if (distanceSquared <= (long) backyardRadius * backyardRadius) {
                return Zone.BACKYARD;
            }
            if (distanceSquared <= (long) patrolRadius * patrolRadius) {
                return Zone.PATROL;
            }
            return Zone.OUTSIDE;
        }
    }
}
