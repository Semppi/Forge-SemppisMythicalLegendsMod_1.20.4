package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.commands.TransformHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TransformationEventHandler {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceKey<DamageType> OUT_OF_WORLD_KEY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation("minecraft", "out_of_world"));

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) entity;
            UUID playerUUID = player.getUUID();
            if (TransformHelper.isPlayerTransformed(playerUUID)) {
                Entity transformedEntity = TransformHelper.getTransformedEntity(playerUUID);
                if (transformedEntity != null) {
                    transformedEntity.remove(Entity.RemovalReason.DISCARDED);
                }
                TransformHelper.revertTransformation(player);
                player.displayClientMessage(Component.literal("Your transformation has been reverted due to death."), true);
            }
        } else {
            UUID ownerUUID = findOwnerOfTransformedEntity(entity);
            if (ownerUUID != null) {
                ServerPlayer owner = entity.getServer().getPlayerList().getPlayer(ownerUUID);
                if (owner != null && TransformHelper.isPlayerTransformed(ownerUUID)) {
                    DamageSource transformationDeathSource = new DamageSource(owner.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(OUT_OF_WORLD_KEY));
                    owner.hurt(transformationDeathSource, Float.MAX_VALUE);
                    TransformHelper.revertTransformation(owner);
                    owner.displayClientMessage(Component.literal("Your transformation entity has died, and you have been reverted."), true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LOGGER.info("Player logged in: {}", player.getName().getString());
            TransformHelper.loadTransformationState(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LOGGER.info("Player logged out: {}", player.getName().getString());
            TransformHelper.saveTransformationState(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerCancelledTransformation(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID playerUUID = player.getUUID();
            if (TransformHelper.isPlayerTransformed(playerUUID)) {
                Entity transformedEntity = TransformHelper.getTransformedEntity(playerUUID);
                if (transformedEntity != null && !transformedEntity.isRemoved()) {
                    transformedEntity.remove(Entity.RemovalReason.DISCARDED);
                }
                TransformHelper.revertTransformation(player);
            }
        }
    }

    private UUID findOwnerOfTransformedEntity(Entity transformedEntity) {
        for (Map.Entry<UUID, Entity> entry : TransformHelper.getTransformedEntities().entrySet()) {
            if (entry.getValue().equals(transformedEntity)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onPlayerDismount(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            UUID playerUUID = player.getUUID();
            if (TransformHelper.isPlayerTransformed(playerUUID)) {
                Entity transformedEntity = TransformHelper.getTransformedEntity(playerUUID);
                if (transformedEntity != null && !player.isPassenger()) {
                    LOGGER.info("Player dismounted from transformation entity, remounting...");
                    player.startRiding(transformedEntity, true);
                }
            }
        }
    }
}