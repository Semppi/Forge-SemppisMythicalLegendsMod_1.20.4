package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.UUID;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.commands.TransformHelper;
import net.semppi.semppis_mythical_legends_mod.entity.TransformMountEntity;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TransformationEventHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceKey<DamageType> OUT_OF_WORLD_KEY =
            ResourceKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE,
                    new ResourceLocation("minecraft","out_of_world"));
    private static final UUID MOUNT_MINING_MODIFIER_ID =
            UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @SubscribeEvent
    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (TransformHelper.isPlayerTransformed(player)) {
            // your existing effect logic...
            player.removeEffect(MobEffects.DIG_SLOWDOWN);
            AttributeInstance at = player.getAttribute(Attributes.ATTACK_SPEED);
            if (at != null) {
                at.removeModifier(MOUNT_MINING_MODIFIER_ID);
                at.addTransientModifier(new AttributeModifier(
                        MOUNT_MINING_MODIFIER_ID,
                        "Negate Mount Mining Penalty",
                        4.0,
                        AttributeModifier.Operation.MULTIPLY_BASE
                ));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(BreakSpeed event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (TransformHelper.isPlayerTransformed(player) && player.isPassenger()) {
            float adj = event.getOriginalSpeed() * 5.0f;
            event.setNewSpeed(adj);
            LOGGER.info("Mining speed adjusted: {} → {}", event.getOriginalSpeed(), adj);
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        Entity e = event.getEntity();

        // Player died
        if (e instanceof ServerPlayer player &&
                TransformHelper.isPlayerTransformed(player)) {

            TransformMountEntity mount = TransformHelper.getPlayerMount(player);
            if (mount != null && !mount.isRemoved()) {
                mount.remove(Entity.RemovalReason.DISCARDED);
            }
            TransformHelper.revertTransformation(player);
            player.displayClientMessage(
                    Component.literal("Your transformation has been reverted due to death."),
                    true
            );

            // Mount died
        } else if (e instanceof TransformMountEntity mount) {
            Player owner = mount.getLinkedPlayer();
            if (owner instanceof ServerPlayer p && TransformHelper.isPlayerTransformed(p)) {
                DamageSource ds = new DamageSource(
                        p.level().registryAccess()
                                .registryOrThrow(net.minecraft.core.registries.Registries.DAMAGE_TYPE)
                                .getHolderOrThrow(OUT_OF_WORLD_KEY)
                );
                p.hurt(ds, Float.MAX_VALUE);
                TransformHelper.revertTransformation(p);
                p.displayClientMessage(
                        Component.literal("Your transformation entity died; you've been reverted."),
                        true
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent ev) {
        if (ev.getEntity() instanceof ServerPlayer p) {
            LOGGER.info("Login: {}", p.getName().getString());
            TransformHelper.loadTransformationState(p);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent ev) {
        if (ev.getEntity() instanceof ServerPlayer p) {
            LOGGER.info("Logout: {}", p.getName().getString());
            TransformHelper.saveTransformationState(p);
        }
    }

    @SubscribeEvent
    public static void onPlayerDismount(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer p)) return;
        if (!TransformHelper.isPlayerTransformed(p)) return;

        TransformMountEntity mount = TransformHelper.getPlayerMount(p);
        if (mount != null && !p.isPassenger() && !p.level().isClientSide) {
            LOGGER.info("Remounting player to their transformation.");
            p.startRiding(mount, true);
        }
    }
}