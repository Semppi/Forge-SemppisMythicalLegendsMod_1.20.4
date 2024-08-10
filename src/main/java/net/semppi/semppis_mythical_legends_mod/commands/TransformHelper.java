package net.semppi.semppis_mythical_legends_mod.commands;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.entity.TransformPlayerMount;
import net.semppi.semppis_mythical_legends_mod.entity.custom.WendigoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransformHelper {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, Boolean> transformedPlayers = new ConcurrentHashMap<>();
    private static final Map<UUID, Entity> transformedEntities = new ConcurrentHashMap<>();
    private static final String TRANSFORMATION_KEY = "IsTransformed";
    private static final String TRANSFORMED_ENTITY_KEY = "TransformedEntity";
    private static final Set<EntityType<? extends Mob>> allowedTransformations = new HashSet<>();

    static {
        addAllowedTransformations();
    }

    private static void addAllowedTransformations() {
        allowedTransformations.add(EntityType.ZOMBIE);
        allowedTransformations.add(EntityType.COW);
        allowedTransformations.add(EntityType.SHEEP);
        allowedTransformations.add(ModEntities.SATYR.get());
        allowedTransformations.add(ModEntities.COLOSSAL_LOBSTER.get());
        allowedTransformations.add(ModEntities.BEHEMOTH.get());
        allowedTransformations.add(ModEntities.PUKIS.get());
        allowedTransformations.add(ModEntities.MANDRAKE.get());
        allowedTransformations.add(ModEntities.WENDIGO.get());
        allowedTransformations.add(ModEntities.LOVELAND_FROGMAN.get());
        allowedTransformations.add(ModEntities.MALPHAS.get());
        allowedTransformations.add(ModEntities.KRAKEN.get());
    }

    public static Map<UUID, Entity> getTransformedEntities() {
        return new HashMap<>(transformedEntities);  // Return a copy to prevent external modification
    }

    public static void saveTransformationState(ServerPlayer player) {
        LOGGER.info("Saving transformation state for player: {}", player.getName().getString());
        CompoundTag playerData = player.getPersistentData();
        boolean isTransformed = isPlayerTransformed(player.getUUID());
        playerData.putBoolean(TRANSFORMATION_KEY, isTransformed);
        if (isTransformed) {
            Entity transformedEntity = getTransformedEntity(player.getUUID());
            if (transformedEntity != null) {
                String entityKey = EntityType.getKey(transformedEntity.getType()).toString();
                playerData.putString(TRANSFORMED_ENTITY_KEY, entityKey);
                LOGGER.info("Saved transformation entity type: {}", entityKey);
            } else {
                LOGGER.warn("No transformed entity found to save.");
            }
        } else {
            LOGGER.info("Player is not transformed.");
        }
    }

    public static void loadTransformationState(ServerPlayer player) {
        LOGGER.info("Loading transformation state for player: {}", player.getName().getString());
        CompoundTag playerData = player.getPersistentData();
        boolean wasTransformed = playerData.getBoolean(TRANSFORMATION_KEY);
        if (wasTransformed) {
            String entityTypeName = playerData.getString(TRANSFORMED_ENTITY_KEY);
            if (!entityTypeName.isEmpty()) {
                Entity transformedEntity = transformPlayer(player, entityTypeName);
                if (transformedEntity != null) {
                    LOGGER.info("Loaded transformation entity type: {}", entityTypeName);
                } else {
                    LOGGER.warn("Failed to load transformation entity.");
                }
            } else {
                LOGGER.warn("No entity type found in saved data.");
            }
        } else {
            LOGGER.info("Player was not transformed.");
        }
    }

    public static void initializeAllowedTransformations() {
        addAllowedTransformations();  // Ensure it's called on mod setup if needed
    }

    public static Entity transformPlayer(ServerPlayer player, String entityTypeName) {
        UUID playerId = player.getUUID();
        if (transformedPlayers.getOrDefault(playerId, false)) {
            revertTransformation(player);
            return null;
        }

        EntityType<?> rawType = EntityType.byString(entityTypeName).orElse(null);
        if (rawType == null) {
            player.displayClientMessage(Component.literal("Invalid entity type for transformation."), false);
            return null;
        }

        if (!allowedTransformations.contains(rawType)) {
            player.displayClientMessage(Component.literal("This entity type cannot be transformed into."), false);
            return null;
        }

        if (rawType == ModEntities.WENDIGO.get()) {
            WendigoEntity wendigoEntity = new WendigoEntity((EntityType<? extends TamableAnimal>) rawType, player.level());
            wendigoEntity.setTransformed(true);
            wendigoEntity.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            player.level().addFreshEntity(wendigoEntity);
            player.startRiding(wendigoEntity, true);
            player.setInvisible(true);
            player.setInvulnerable(true);

            transformedPlayers.put(playerId, true);
            transformedEntities.put(playerId, wendigoEntity);
            return wendigoEntity;
        }

        TransformPlayerMount entity = new TransformPlayerMount((EntityType<? extends Mob>) rawType, player.level());
        entity.moveTo(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
        player.level().addFreshEntity(entity);
        player.startRiding(entity, true);
        player.setInvisible(true);
        player.setInvulnerable(true);

        transformedPlayers.put(playerId, true);
        transformedEntities.put(playerId, entity);

        return entity;
    }

    public static boolean isPlayerTransformed(UUID playerId) {
        return transformedPlayers.getOrDefault(playerId, false);
    }

    public static Entity getTransformedEntity(UUID playerId) {
        return transformedEntities.get(playerId);
    }

    public static void revertTransformation(ServerPlayer player) {
        UUID playerId = player.getUUID();
        if (transformedPlayers.getOrDefault(playerId, false)) {
            player.stopRiding();
            player.setInvisible(false);
            player.setInvulnerable(false);
            player.displayClientMessage(Component.literal("Transformation reverted."), true);
            Entity entity = transformedEntities.remove(playerId);
            if (entity != null) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
            transformedPlayers.remove(playerId);
        } else {
            player.displayClientMessage(Component.literal("No transformation to revert."), false);
        }
    }
}