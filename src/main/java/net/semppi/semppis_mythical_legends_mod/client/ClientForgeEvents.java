package net.semppi.semppis_mythical_legends_mod.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.client.hud.InteractionHudState;
import net.semppi.semppis_mythical_legends_mod.client.screen.TestMapScreen;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = SemppisMythicalLegendsMod.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE,
        value = Dist.CLIENT
)
public final class ClientForgeEvents {

    private ClientForgeEvents() {
    }

    private static final double NORMAL_INTERACTION_TARGET_RANGE = 25.0D;
    private static final double SPYGLASS_INTERACTION_TARGET_RANGE = 55.0D;

    private static int lockedHotbarSlot = -1;

    @SubscribeEvent
    public static void onClientTickBeforeVanillaKeys(
            TickEvent.ClientTickEvent.Pre event
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        while (ModKeyMappings.TEST_MAP.consumeClick()) {
            if (minecraft.screen == null
                    && minecraft.player != null
                    && minecraft.level != null) {

                /*
                 * P is also vanilla's default Social Interactions key.
                 * Drain that queued click when both mappings use P so only
                 * the SML test map opens. Players can rebind either action.
                 */
                if (minecraft.options.keySocialInteractions
                        .same(ModKeyMappings.TEST_MAP)) {
                    while (minecraft.options.keySocialInteractions
                            .consumeClick()) {
                        // Intentionally consumed by the test map.
                    }
                }

                InteractionHudState.close();
                lockedHotbarSlot = -1;
                minecraft.setScreen(new TestMapScreen());
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(
            TickEvent.ClientTickEvent.Post event
    ) {
        Minecraft minecraft = Minecraft.getInstance();

        while (ModKeyMappings.INTERACTION_SCREEN.consumeClick()) {
            if (minecraft.screen == null
                    && minecraft.player != null
                    && minecraft.level != null) {

                boolean wasOpen =
                        InteractionHudState.isOpen();

                InteractionHudState.toggle();

                if (!wasOpen && InteractionHudState.isOpen()) {
                    lockedHotbarSlot =
                            minecraft.player
                                    .getInventory()
                                    .selected;
                }

                if (!InteractionHudState.isOpen()) {
                    lockedHotbarSlot = -1;
                }
            }
        }

        /*
         * Safety guard:
         * vanilla must not change the selected hotbar slot
         * while the interaction HUD owns the number keys.
         */
        if (InteractionHudState.isOpen()
                && minecraft.player != null
                && minecraft.screen == null
                && lockedHotbarSlot >= 0) {

            minecraft.player
                    .getInventory()
                    .selected =
                    lockedHotbarSlot;
        }
    }

    @SubscribeEvent
    public static void onKeyboardInput(
            InputEvent.Key event
    ) {
        if (!InteractionHudState.isOpen()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.level == null
                || minecraft.screen != null) {
            return;
        }

        /*
         * Only react to the initial physical key press.
         */
        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }

        int key = event.getKey();

        /*
         * We only care about keyboard numbers 1–9.
         */
        if (key < GLFW.GLFW_KEY_1
                || key > GLFW.GLFW_KEY_9) {
            return;
        }

        int numberIndex =
                key - GLFW.GLFW_KEY_1;

        /*
         * Action mode:
         *
         * 1 = action slot 0
         * 2 = action slot 1
         * 3 = action slot 2
         * 4 = action slot 3
         */
        if (InteractionHudState.getSelectionMode()
                == InteractionHudState.SelectionMode.ACTIONS
                && numberIndex < 4) {

            /*
             * Clear Minecraft's hotbar KeyMapping before the
             * interaction closes the HUD.
             */
            minecraft.options
                    .keyHotbarSlots[numberIndex]
                    .setDown(false);

            while (minecraft.options
                    .keyHotbarSlots[numberIndex]
                    .consumeClick()) {
                // Remove queued vanilla hotbar presses.
            }

            activateInteraction(
                    minecraft,
                    numberIndex
            );

            return;
        }

        /*
         * Tone mode, or number keys 5–9:
         *
         * Reserve the key for the HUD but perform no action.
         */
        minecraft.options
                .keyHotbarSlots[numberIndex]
                .setDown(false);

        while (minecraft.options
                .keyHotbarSlots[numberIndex]
                .consumeClick()) {
            // Intentionally consumed.
        }
    }

    @SubscribeEvent
    public static void onScreenOpening(ScreenEvent.Opening event) {
        if (InteractionHudState.isOpen()
                && event.getNewScreen() != null) {

            InteractionHudState.close();
        }

    }

    @SubscribeEvent
    public static void onMouseScroll(
            InputEvent.MouseScrollingEvent event
    ) {
        if (!InteractionHudState.isOpen()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.screen != null
                || minecraft.player == null
                || minecraft.level == null) {
            return;
        }

        double scrollAmount = event.getDeltaY();

        if (scrollAmount == 0.0D) {
            return;
        }

        if (InteractionHudState.getSelectionMode()
                == InteractionHudState.SelectionMode.TONES) {

            if (scrollAmount > 0.0D) {
                InteractionHudState.selectPreviousTone();
            } else {
                InteractionHudState.selectNextTone();
            }
        }
        else {
            if (scrollAmount > 0.0D) {
                InteractionHudState.selectPreviousActionGroup();
            } else {
                InteractionHudState.selectNextActionGroup();
            }
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onMouseButton(
            InputEvent.MouseButton.Pre event
    ) {
        if (!InteractionHudState.isOpen()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null
                || minecraft.level == null
                || minecraft.screen != null) {
            return;
        }

        int mouseButton = event.getButton();

        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {

            /*
             * Only toggle once on the initial press.
             * We still cancel both press and release from vanilla.
             */
            if (event.getAction() == GLFW.GLFW_PRESS) {
                InteractionHudState.toggleSelectionMode();
            }

            event.setCanceled(true);
            return;
        }

        /*
         * Left and middle click remain reserved for the HUD.
         * We will give them jobs later.
         */
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_LEFT) {

            if (event.getAction() == GLFW.GLFW_PRESS) {

                EntityHitResult entityHitResult =
                        getInteractionTarget(minecraft);

                if (entityHitResult != null) {
                    InteractionHudState.setTargetName(
                            entityHitResult
                                    .getEntity()
                                    .getDisplayName()
                    );
                } else {
                    InteractionHudState.clearTarget();
                }
            }

            /*
             * The HUD owns left click, so never attack/break normally.
             */
            event.setCanceled(true);
            return;
        }
        if (mouseButton == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            event.setCanceled(true);
        }
    }

    private static EntityHitResult getInteractionTarget(
            Minecraft minecraft
    ) {
        if (minecraft.player == null || minecraft.level == null) {
            return null;
        }

        Entity cameraEntity = minecraft.getCameraEntity();

        if (cameraEntity == null) {
            return null;
        }

        /*
         * Holding a spyglass in the main hand greatly increases
         * interaction-HUD targeting distance.
         */
        boolean holdingSpyglass =
                minecraft.player
                        .getItemInHand(InteractionHand.MAIN_HAND)
                        .is(Items.SPYGLASS);

        double targetRange =
                holdingSpyglass
                        ? SPYGLASS_INTERACTION_TARGET_RANGE
                        : NORMAL_INTERACTION_TARGET_RANGE;

        Vec3 start =
                cameraEntity.getEyePosition();

        Vec3 lookDirection =
                cameraEntity.getViewVector(1.0F);

        Vec3 end =
                start.add(
                        lookDirection.scale(targetRange)
                );

        AABB searchBox =
                cameraEntity
                        .getBoundingBox()
                        .expandTowards(
                                lookDirection.scale(targetRange)
                        )
                        .inflate(1.0D);

        return ProjectileUtil.getEntityHitResult(
                cameraEntity,
                start,
                end,
                searchBox,
                entity ->
                        !entity.isSpectator()
                                && entity.isPickable(),
                targetRange * targetRange
        );
    }

    private static void activateInteraction(
            Minecraft minecraft,
            int actionSlot
    ) {
        InteractionHudState.Action action =
                InteractionHudState.getAction(
                        InteractionHudState.getSelectedActionGroup(),
                        actionSlot
                );

        if (action == null) {
            return;
        }

        InteractionHudState.Tone tone =
                InteractionHudState.getSelectedTone();

        /*
         * Actual creature interaction/networking will go here later.
         */

        playInteractionConfirmationSound(minecraft);

        releaseHotbarKeys(minecraft);

        InteractionHudState.close();
        lockedHotbarSlot = -1;
    }
    private static void playInteractionConfirmationSound(
            Minecraft minecraft
    ) {
        minecraft.getSoundManager().play(
                SimpleSoundInstance.forUI(
                        SoundEvents.NOTE_BLOCK_PLING,
                        1.2F
                )
        );
    }

    private static void releaseHotbarKeys(
            Minecraft minecraft
    ) {
        for (int index = 0; index < 9; index++) {
            minecraft.options
                    .keyHotbarSlots[index]
                    .setDown(false);

            while (minecraft.options
                    .keyHotbarSlots[index]
                    .consumeClick()) {
                // Clear queued presses.
            }
        }
    }
}
