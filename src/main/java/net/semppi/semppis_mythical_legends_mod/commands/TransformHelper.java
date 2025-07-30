package net.semppi.semppis_mythical_legends_mod.commands;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.minecraft.world.entity.player.Player;
import net.semppi.semppis_mythical_legends_mod.entity.TransformMountEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.ProtoWendigoEntity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.WendigoEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TransformHelper {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NBT_TRANSFORMED = "SML_IsTransformed";
    private static final String NBT_MOUNT_UUID = "SML_MountUUID";

    private static final Set<EntityType<?>> ALLOWED = Set.of(
            ModEntities.ALICANTO.get(),
            ModEntities.LESSER_BEHEMOTH.get(),
            ModEntities.COLOSSAL_LOBSTER.get(),
            ModEntities.KRAKEN.get(),
            ModEntities.LOVELAND_FROGMAN.get(),
            ModEntities.MALPHAS.get(),
            ModEntities.MANDRAKE_SPROUTLING.get(),
            ModEntities.PROTO_WENDIGO.get(),
            ModEntities.PUKIS.get(),
            ModEntities.SATYR.get(),
            ModEntities.WENDIGO.get(),

            // vanilla examples
            EntityType.ALLAY,
            EntityType.AXOLOTL,
            EntityType.BAT,
            EntityType.BEE,
            EntityType.BLAZE,
            EntityType.CAMEL,
            EntityType.CAT,
            EntityType.CAVE_SPIDER,
            EntityType.CHICKEN,
            EntityType.COD,
            EntityType.COW,
            EntityType.CREEPER,
            EntityType.DOLPHIN,
            EntityType.DONKEY,
            EntityType.DROWNED,
            EntityType.ELDER_GUARDIAN,
            EntityType.ENDERMAN,
            EntityType.ENDERMITE,
            EntityType.ENDER_DRAGON,
            EntityType.EVOKER,
            EntityType.FOX,
            EntityType.FROG,
            EntityType.GHAST,
            EntityType.GLOW_SQUID,
            EntityType.GOAT,
            EntityType.GUARDIAN,
            EntityType.HOGLIN,
            EntityType.HORSE,
            EntityType.HUSK,
            EntityType.IRON_GOLEM,
            EntityType.LLAMA,
            EntityType.MAGMA_CUBE,
            EntityType.MOOSHROOM,
            EntityType.MULE,
            EntityType.OCELOT,
            EntityType.PANDA,
            EntityType.PARROT,
            EntityType.PHANTOM,
            EntityType.PIG,
            EntityType.PIGLIN,
            EntityType.PIGLIN_BRUTE,
            EntityType.PILLAGER,
            EntityType.POLAR_BEAR,
            EntityType.PUFFERFISH,
            EntityType.RABBIT,
            EntityType.RAVAGER,
            EntityType.SALMON,
            EntityType.SHEEP,
            EntityType.SHULKER,
            EntityType.SILVERFISH,
            EntityType.SKELETON,
            EntityType.SKELETON_HORSE,
            EntityType.SLIME,
            EntityType.SNIFFER,
            EntityType.SNOW_GOLEM,
            EntityType.SPIDER,
            EntityType.SQUID,
            EntityType.STRAY,
            EntityType.STRIDER,
            EntityType.TADPOLE,
            EntityType.TRADER_LLAMA,
            EntityType.TROPICAL_FISH,
            EntityType.TURTLE,
            EntityType.VEX,
            EntityType.VILLAGER,
            EntityType.VINDICATOR,
            EntityType.WANDERING_TRADER,
            EntityType.WARDEN,
            EntityType.WITCH,
            EntityType.WITHER,
            EntityType.WITHER_SKELETON,
            EntityType.WOLF,
            EntityType.ZOGLIN,
            EntityType.ZOMBIE,
            EntityType.ZOMBIE_HORSE,
            EntityType.ZOMBIE_VILLAGER,
            EntityType.ZOMBIFIED_PIGLIN
    );

    private static final Map<UUID, UUID> playerToMount = new ConcurrentHashMap<>();

    /**
     * @return the active TransformMountEntity or null
     */
    public static TransformMountEntity getPlayerMount(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(NBT_TRANSFORMED)) return null;
        UUID mountId = data.getUUID(NBT_MOUNT_UUID);
        ServerLevel lvl = (ServerLevel) player.level();
        Entity e = lvl.getEntity(mountId);
        return (e instanceof TransformMountEntity) ? (TransformMountEntity)e : null;
    }

    public static Entity transformPlayer(ServerPlayer player, String entityTypeName) {
        CompoundTag data = player.getPersistentData();
        UUID playerId = player.getUUID();

        // 1) revert if already transformed
        if (data.getBoolean(NBT_TRANSFORMED)) {
            revertTransformation(player);
            return null;
        }

        // 2) parse & validate
        EntityType<?> rawType = EntityType.byString(entityTypeName).orElse(null);
        if (rawType == null || !ALLOWED.contains(rawType)) {
            player.displayClientMessage(Component.literal("Cannot transform into '" + entityTypeName + "'"), false);
            return null;
        }

        // ** pull these out so every branch can see them **
        double x    = player.getX();
        double y    = player.getY() + 0.5;  // nudge up
        double z    = player.getZ();
        float  yRot = player.getYRot();
        float  xRot = player.getXRot();

        Entity result;

        // 3a) Proto Wendigo
        if (rawType == ModEntities.PROTO_WENDIGO.get()) {
            ProtoWendigoEntity proto = new ProtoWendigoEntity(ModEntities.PROTO_WENDIGO.get(), player.level());
            proto.moveTo(x, y, z, yRot, xRot);
            player.level().addFreshEntity(proto);

            proto.absMoveTo(x, y, z, yRot, xRot);
            proto.setPos(x, y, z);
            player.teleportTo(x, y, z);

            player.startRiding(proto, true);
            player.setInvisible(true);
            player.setInvulnerable(true);

            result = proto;

            // 3b) Wendigo — use the *exact* EntityType from your registry, not rawType
        } else if (rawType == ModEntities.WENDIGO.get()) {
            WendigoEntity w = new WendigoEntity(ModEntities.WENDIGO.get(), player.level());
            w.moveTo(x, y, z, yRot, xRot);
            player.level().addFreshEntity(w);

            w.absMoveTo(x, y, z, yRot, xRot);
            w.setPos(x, y, z);
            player.teleportTo(x, y, z);

            player.startRiding(w, true);
            player.setInvisible(true);
            player.setInvulnerable(true);
            w.setTransformed(true);

            result = w;

            // 3c) fallback: generic TransformMountEntity
        } else {
            TransformMountEntity mount = new TransformMountEntity(ModEntities.TRANSFORM_MOUNT.get(), player.level());
            mount.moveTo(x, y, z, yRot, xRot);
            player.level().addFreshEntity(mount);
            mount.setLinkedPlayer(player);
            result = mount;
        }

        // 4) persist state
        playerToMount.put(playerId, result.getUUID());
        data.putBoolean(NBT_TRANSFORMED, true);
        data.putUUID(NBT_MOUNT_UUID, result.getUUID());

        player.displayClientMessage(Component.literal("You have transformed into " + entityTypeName + "!"), true);
        return result;
    }

    public static void loadTransformationState(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(NBT_TRANSFORMED)) return;

        UUID mountId = data.getUUID(NBT_MOUNT_UUID);
        ServerLevel lvl = (ServerLevel) player.level();
        Entity e = lvl.getEntity(mountId);
        if (e instanceof TransformMountEntity mount) {
            mount.setLinkedPlayer(player);
            LOGGER.info("Restored transformation for {}", player.getName().getString());
        } else {
            LOGGER.warn("Failed to restore mount {} for {}", mountId, player.getName().getString());
            data.putBoolean(NBT_TRANSFORMED, false);
            data.remove(NBT_MOUNT_UUID);
        }
    }

    /**
     * Nothing to do on logout—NBT is already written—but log it anyway.
     */
    public static void saveTransformationState(ServerPlayer player) {
        if (isPlayerTransformed(player)) {
            LOGGER.info("Saving transformation for {}", player.getName().getString());
        }
    }

    public static void revertTransformation(ServerPlayer player) {
        CompoundTag data = player.getPersistentData();
        if (!data.getBoolean(NBT_TRANSFORMED)) return;

        UUID mountId = data.getUUID(NBT_MOUNT_UUID);
        ServerLevel lvl = (ServerLevel)player.level();
        Entity e = lvl.getEntity(mountId);
        if (e != null) {
            // 1) force the player off any mount
            if (player.isPassenger()) {
                player.stopRiding();
            }
            // 2) then discard the mount entity (whether Proto/Wendigo or custom mount)
            e.remove(Entity.RemovalReason.DISCARDED);
        }

        playerToMount.remove(player.getUUID());
        data.putBoolean(NBT_TRANSFORMED, false);
        data.remove(NBT_MOUNT_UUID);

        // restore the player
        player.setInvisible(false);
        player.setInvulnerable(false);
        player.displayClientMessage(
                Component.literal("Your transformation has been reverted."), true
        );
    }

    // …
    /** @return true if the player is in a transformed state */
    public static boolean isPlayerTransformed(Player player) {
        return player.getPersistentData().getBoolean(NBT_TRANSFORMED);
    }
}