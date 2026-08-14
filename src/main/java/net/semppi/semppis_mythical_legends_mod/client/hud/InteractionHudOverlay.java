package net.semppi.semppis_mythical_legends_mod.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public final class InteractionHudOverlay {

    private static final WidgetSprites BUTTON_SPRITES =
            new WidgetSprites(
                    new ResourceLocation("widget/button"),
                    new ResourceLocation("widget/button_disabled"),
                    new ResourceLocation("widget/button_highlighted")
            );

    private static final ResourceLocation TEASING_TONE_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/tone_teasing.png"
            );

    private static final ResourceLocation SOFT_TONE_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/tone_soft.png"
            );

    private static final ResourceLocation NEUTRAL_TONE_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/tone_neutral.png"
            );

    private static final ResourceLocation RESPECTFUL_TONE_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/tone_respectful.png"
            );

    private static final ResourceLocation INTENSE_TONE_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/tone_intense.png"
            );

    private static final int TONE_ICON_SIZE = 28;

    private static final ResourceLocation CALL_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/call.png"
            );

    private static final ResourceLocation GREET_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/greet.png"
            );

    private static final ResourceLocation APPEASE_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/appease.png"
            );

    private static final ResourceLocation WARD_OFF_ICON =
            new ResourceLocation(
                    "semppis_mythical_legends_mod",
                    "textures/gui/interaction/ward_off.png"
            );

    private static final int ACTION_ICON_SIZE = 24;

    private static final int TARGET_BUTTON_SIZE = 36;

    private static final int TONE_BUTTON_SIZE = 32;
    private static final int TONE_BUTTON_SPACING = 4;
    private static final int TONE_BUTTON_COUNT = 5;

    private static final int TONE_GROUP_MARGIN = 5;

    private static final int ACTION_BUTTON_SIZE = 28;
    private static final int ACTION_BUTTON_SPACING = 4;

    private static int callIconX;
    private static int callIconY;

    private static int greetIconX;
    private static int greetIconY;

    private static int appeaseIconX;
    private static int appeaseIconY;

    private static int wardOffIconX;
    private static int wardOffIconY;

    private static final int ACTION_GROUP_MARGIN = 5;

    /*
     * Vertical empty space between the upper and lower action rows.
     * Same size as the buttons themselves.
     */
    private static final int ACTION_ROW_GAP = ACTION_BUTTON_SIZE;

    /*
     * Center opening between the left and right groups.
     * 28-pixel hypothetical button + 4 pixels spacing on both sides.
     */
    private static final int ACTION_CENTER_GAP =
            ACTION_BUTTON_SIZE * 3
                    + ACTION_BUTTON_SPACING * 2;

    public static final IGuiOverlay OVERLAY =
            (forgeGui, guiGraphics, partialTick, screenWidth, screenHeight) ->
                    render(
                            guiGraphics,
                            screenWidth,
                            screenHeight
                    );

    private InteractionHudOverlay() {
    }

    private static void render(
            GuiGraphics guiGraphics,
            int screenWidth,
            int screenHeight
    ) {
        if (!InteractionHudState.isOpen()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        /*
         * Hide our HUD while another Minecraft screen is open.
         *
         * The interaction mode itself remains open, so when the player
         * closes their inventory/pause menu, this HUD comes back.
         */
        if (minecraft.screen != null) {
            return;
        }

        /*
         * Background dim.
         */
        guiGraphics.fill(
                0,
                0,
                screenWidth,
                screenHeight,
                0x08500000
        );

        renderTargetDisplay(
                guiGraphics,
                screenWidth
        );

        renderToneRow(
                guiGraphics,
                screenWidth
        );

        renderActionButtons(
                guiGraphics,
                screenWidth,
                screenHeight
        );

        renderSelectedActionGroupNames(
                guiGraphics,
                screenWidth
        );
    }

    private static void renderTargetDisplay(
            GuiGraphics guiGraphics,
            int screenWidth
    ) {
        int x =
                (screenWidth - TARGET_BUTTON_SIZE) / 2;

        int y = 8;

        guiGraphics.blitSprite(
                BUTTON_SPRITES.get(true, false),
                x,
                y,
                TARGET_BUTTON_SIZE,
                TARGET_BUTTON_SIZE
        );

        /*
         * Temporary target-name display.
         */
        Component targetName =
                InteractionHudState.getTargetName();

        if (targetName != null) {
            Minecraft minecraft = Minecraft.getInstance();

            int textX =
                    x
                            + TARGET_BUTTON_SIZE
                            + 6;

            int textY =
                    y
                            + (
                            TARGET_BUTTON_SIZE
                                    - minecraft.font.lineHeight
                    ) / 2;

            guiGraphics.drawString(
                    minecraft.font,
                    targetName,
                    textX,
                    textY,
                    0xFFFFFFFF
            );
        }
    }

    private static void renderToneRow(
            GuiGraphics guiGraphics,
            int screenWidth
    ) {
        int totalWidth =
                TONE_BUTTON_COUNT * TONE_BUTTON_SIZE
                        + (TONE_BUTTON_COUNT - 1)
                        * TONE_BUTTON_SPACING;

        int rowStartX =
                (screenWidth - totalWidth) / 2;

        /*
         * Target display starts at Y 8 and is 36 pixels tall.
         * Same 6-pixel gap as the old Screen.
         */
        int rowY =
                8
                        + TARGET_BUTTON_SIZE
                        + 6;

        /*
         * Draw all five button backgrounds.
         */
        int selectedToneIndex =
                InteractionHudState
                        .getSelectedTone()
                        .ordinal();

        for (int index = 0;
             index < TONE_BUTTON_COUNT;
             index++) {

            int buttonX =
                    rowStartX
                            + index
                            * (
                            TONE_BUTTON_SIZE
                                    + TONE_BUTTON_SPACING
                    );

            boolean selected =
                    index == selectedToneIndex;

            renderToneButtonBackground(
                    guiGraphics,
                    buttonX,
                    rowY,
                    selected
            );
        }

        /*
         * Colored tone overlays.
         *
         * 0 = Teasing
         * 1 = Soft
         * 2 = Neutral
         * 3 = Respectful
         * 4 = Intense
         */
        renderToneTint(
                guiGraphics,
                rowStartX,
                rowY,
                0x66F080C0
        );

        renderToneTint(
                guiGraphics,
                rowStartX
                        + (TONE_BUTTON_SIZE
                        + TONE_BUTTON_SPACING),
                rowY,
                0x6665D47E
        );

        renderToneTint(
                guiGraphics,
                rowStartX
                        + 2 * (TONE_BUTTON_SIZE
                        + TONE_BUTTON_SPACING),
                rowY,
                0x665FA8FF
        );

        renderToneTint(
                guiGraphics,
                rowStartX
                        + 3 * (TONE_BUTTON_SIZE
                        + TONE_BUTTON_SPACING),
                rowY,
                0x66F2CE63
        );

        renderToneTint(
                guiGraphics,
                rowStartX
                        + 4 * (TONE_BUTTON_SIZE
                        + TONE_BUTTON_SPACING),
                rowY,
                0x66E85A5A
        );

        renderToneIcons(
                guiGraphics,
                rowStartX,
                rowY
        );

        /*
         * These only appear while actively selecting tones.
         */
        if (InteractionHudState.getSelectionMode()
                == InteractionHudState.SelectionMode.TONES) {

            renderToneSelectionFrame(
                    guiGraphics,
                    rowStartX,
                    rowY,
                    totalWidth
            );

            renderSelectedToneFrame(
                    guiGraphics,
                    rowStartX,
                    rowY
            );

            renderSelectedToneName(
                    guiGraphics,
                    screenWidth,
                    rowY
            );
        }
    }

    private static void renderToneButtonBackground(
            GuiGraphics guiGraphics,
            int x,
            int y,
            boolean selected
    ) {
        /*
         * Selected tone uses vanilla's disabled sprite.
         *
         * In our HUD this does not mean "unavailable";
         * visually it represents the tone being held down/selected.
         */
        guiGraphics.blitSprite(
                BUTTON_SPRITES.get(
                        !selected,
                        false
                ),
                x,
                y,
                TONE_BUTTON_SIZE,
                TONE_BUTTON_SIZE
        );
    }

    private static void renderToneTint(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int color
    ) {
        guiGraphics.fill(
                x + 1,
                y + 1,
                x + TONE_BUTTON_SIZE - 1,
                y + TONE_BUTTON_SIZE - 1,
                color
        );
    }

    private static void renderToneSelectionFrame(
            GuiGraphics guiGraphics,
            int rowStartX,
            int rowY,
            int totalWidth
    ) {
        int frameX =
                rowStartX - TONE_GROUP_MARGIN;

        int frameY =
                rowY - TONE_GROUP_MARGIN;

        int frameWidth =
                totalWidth
                        + TONE_GROUP_MARGIN * 2;

        int frameHeight =
                TONE_BUTTON_SIZE
                        + TONE_GROUP_MARGIN * 2;

        guiGraphics.renderOutline(
                frameX,
                frameY,
                frameWidth,
                frameHeight,
                0xFF101010
        );

        guiGraphics.renderOutline(
                frameX + 1,
                frameY + 1,
                frameWidth - 2,
                frameHeight - 2,
                0xFFF0FFF0
        );

        guiGraphics.renderOutline(
                frameX + 2,
                frameY + 2,
                frameWidth - 4,
                frameHeight - 4,
                0xFF9DAA98
        );
    }

    private static void renderActionButtons(
            GuiGraphics guiGraphics,
            int screenWidth,
            int screenHeight
    ) {
        /*
         * The upper row is vertically centered just like it was
         * in the old InteractionScreen.
         */
        int upperRowY =
                (screenHeight - ACTION_BUTTON_SIZE) / 2;

        /*
         * 28 pixels for the upper button row,
         * followed by a 28-pixel empty gap.
         */
        int lowerRowY =
                upperRowY
                        + ACTION_BUTTON_SIZE
                        + ACTION_ROW_GAP;

        int centerX = screenWidth / 2;

        /*
         * Keep a large 92-pixel opening between the
         * left and right action-button groups.
         */
        int leftGroupRightEdge =
                centerX - ACTION_CENTER_GAP / 2;

        int rightGroupStartX =
                centerX + ACTION_CENTER_GAP / 2;

        /*
         * Upper-left group: buttons 0–3.
         */
        for (int index = 1; index <= 4; index++) {
            int buttonX =
                    leftGroupRightEdge
                            - index * ACTION_BUTTON_SIZE
                            - (index - 1) * ACTION_BUTTON_SPACING;

            /*
             * Top-left action group:
             *
             * index 4 = Call
             * index 3 = Greet
             * index 2 = Appease
             * index 1 = Ward Off
             */
            switch (index) {
                case 4 -> {
                    callIconX = buttonX + 2;
                    callIconY = upperRowY + 2;
                }

                case 3 -> {
                    greetIconX = buttonX + 2;
                    greetIconY = upperRowY + 2;
                }

                case 2 -> {
                    appeaseIconX = buttonX + 2;
                    appeaseIconY = upperRowY + 2;
                }

                case 1 -> {
                    wardOffIconX = buttonX + 2;
                    wardOffIconY = upperRowY + 2;
                }
            }

            renderActionButtonBackground(
                    guiGraphics,
                    buttonX,
                    upperRowY
            );
        }

        /*
         * Upper-right group: buttons 4–7.
         */
        for (int index = 0; index < 4; index++) {
            int buttonX =
                    rightGroupStartX
                            + index
                            * (
                            ACTION_BUTTON_SIZE
                                    + ACTION_BUTTON_SPACING
                    );

            renderActionButtonBackground(
                     guiGraphics,
                    buttonX,
                    upperRowY
            );
        }

        /*
         * Lower-left group: buttons 8–11.
         */
        for (int index = 1; index <= 4; index++) {
            int buttonX =
                    leftGroupRightEdge
                            - index * ACTION_BUTTON_SIZE
                            - (index - 1) * ACTION_BUTTON_SPACING;

            renderActionButtonBackground(
                    guiGraphics,
                    buttonX,
                    lowerRowY
            );
        }

        /*
         * Lower-right group: buttons 12–15.
         */
        for (int index = 0; index < 4; index++) {
            int buttonX =
                    rightGroupStartX
                            + index
                            * (
                            ACTION_BUTTON_SIZE
                                    + ACTION_BUTTON_SPACING
                    );

            renderActionButtonBackground(
                    guiGraphics,
                    buttonX,
                    lowerRowY
            );
        }

        /*
         * Draw action icons after their button backgrounds.
         */
        renderTopLeftActionIcons(guiGraphics);

        renderActionSelectionFrame(
                guiGraphics,
                screenWidth,
                screenHeight
        );
    }

    private static void renderTopLeftActionIcons(
            GuiGraphics guiGraphics
    ) {
        /*
         * All four icons are white source textures,
         * so apply the currently selected tone color once.
         */
        setSelectedToneRenderColor(guiGraphics);

        renderActionIcon(
                guiGraphics,
                CALL_ICON,
                callIconX,
                callIconY
        );

        renderActionIcon(
                guiGraphics,
                GREET_ICON,
                greetIconX,
                greetIconY
        );

        renderActionIcon(
                guiGraphics,
                APPEASE_ICON,
                appeaseIconX,
                appeaseIconY
        );

        renderActionIcon(
                guiGraphics,
                WARD_OFF_ICON,
                wardOffIconX,
                wardOffIconY
        );

        /*
         * Restore normal rendering afterward.
         */
        guiGraphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }

    private static void renderActionIcon(
            GuiGraphics guiGraphics,
            ResourceLocation icon,
            int x,
            int y
    ) {
        guiGraphics.blit(
                icon,
                x,
                y,
                0.0F,
                0.0F,
                ACTION_ICON_SIZE,
                ACTION_ICON_SIZE,
                ACTION_ICON_SIZE,
                ACTION_ICON_SIZE
        );
    }

private static void setSelectedToneRenderColor(
        GuiGraphics guiGraphics
) {
    switch (InteractionHudState.getSelectedTone()) {
        case TEASING -> guiGraphics.setColor(
                240.0F / 255.0F,
                128.0F / 255.0F,
                192.0F / 255.0F,
                1.0F
        );

        case SOFT -> guiGraphics.setColor(
                101.0F / 255.0F,
                212.0F / 255.0F,
                126.0F / 255.0F,
                1.0F
        );

        case NEUTRAL -> guiGraphics.setColor(
                95.0F / 255.0F,
                168.0F / 255.0F,
                255.0F / 255.0F,
                1.0F
        );

        case RESPECTFUL -> guiGraphics.setColor(
                242.0F / 255.0F,
                206.0F / 255.0F,
                99.0F / 255.0F,
                1.0F
        );

        case INTENSE -> guiGraphics.setColor(
                232.0F / 255.0F,
                90.0F / 255.0F,
                90.0F / 255.0F,
                1.0F
        );
    }
}

    private static void renderActionButtonBackground(
            GuiGraphics guiGraphics,
            int x,
            int y
    ) {
        guiGraphics.blitSprite(
                BUTTON_SPRITES.get(true, false),
                x,
                y,
                ACTION_BUTTON_SIZE,
                ACTION_BUTTON_SIZE
        );
    }

    private static void renderToneIcons(
            GuiGraphics guiGraphics,
            int rowStartX,
            int rowY
    ) {
        /*
         * Tone order:
         *
         * 0 = Teasing
         * 1 = Soft
         * 2 = Neutral
         * 3 = Respectful
         * 4 = Intense
         */

        guiGraphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        renderToneIcon(
                guiGraphics,
                TEASING_TONE_ICON,
                rowStartX,
                rowY,
                0
        );

        renderToneIcon(
                guiGraphics,
                SOFT_TONE_ICON,
                rowStartX,
                rowY,
                1
        );

        renderToneIcon(
                guiGraphics,
                NEUTRAL_TONE_ICON,
                rowStartX,
                rowY,
                2
        );

        renderToneIcon(
                guiGraphics,
                RESPECTFUL_TONE_ICON,
                rowStartX,
                rowY,
                3
        );

        renderToneIcon(
                guiGraphics,
                INTENSE_TONE_ICON,
                rowStartX,
                rowY,
                4
        );
    }

    private static void renderToneIcon(
            GuiGraphics guiGraphics,
            ResourceLocation icon,
            int rowStartX,
            int rowY,
            int toneIndex
    ) {
        int buttonX =
                rowStartX
                        + toneIndex
                        * (
                        TONE_BUTTON_SIZE
                                + TONE_BUTTON_SPACING
                );

        int iconX = buttonX + 2;
        int iconY = rowY + 2;

        guiGraphics.blit(
                icon,
                iconX,
                iconY,
                0.0F,
                0.0F,
                TONE_ICON_SIZE,
                TONE_ICON_SIZE,
                TONE_ICON_SIZE,
                TONE_ICON_SIZE
        );
    }

    private static void renderSelectedToneName(
            GuiGraphics guiGraphics,
            int screenWidth,
            int rowY
    ) {
        /*
         * Only show the tone name while the player
         * is actively selecting tones.
         */
        if (InteractionHudState.getSelectionMode()
                != InteractionHudState.SelectionMode.TONES) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        Component toneName =
                switch (InteractionHudState.getSelectedTone()) {
                    case TEASING -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.tone.teasing"
                    );

                    case SOFT -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.tone.soft"
                    );

                    case NEUTRAL -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.tone.neutral"
                    );

                    case RESPECTFUL -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.tone.respectful"
                    );

                    case INTENSE -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.tone.intense"
                    );
                };

        /*
         * Tone buttons are 32 pixels tall.
         * Put the text 6 pixels below the row.
         */
        int textY =
                rowY
                        + TONE_BUTTON_SIZE
                        + 6;

        guiGraphics.drawCenteredString(
                minecraft.font,
                toneName,
                screenWidth / 2,
                textY,
                0xFFFFFFFF
        );
    }

    private static void renderSelectedActionGroupNames(
            GuiGraphics guiGraphics,
            int screenWidth
    ) {
        /*
         * Only show action names while the player
         * is actively selecting action groups.
         */
        if (InteractionHudState.getSelectionMode()
                != InteractionHudState.SelectionMode.ACTIONS) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();

        int selectedGroup =
                InteractionHudState.getSelectedActionGroup();

        Component actionNames =
                switch (selectedGroup) {
                    case 0 -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.actions.group_0"
                    );

                    case 1 -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.actions.group_1"
                    );

                    case 2 -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.actions.group_2"
                    );

                    case 3 -> Component.translatable(
                            "hud.semppis_mythical_legends_mod.actions.group_3"
                    );

                    default -> Component.empty();
                };

        /*
         * Use the exact same Y position as the tone name.
         *
         * Tone row starts at:
         * 8 + 36 + 6 = 50
         *
         * Tone row is 32 pixels tall.
         * Text sits 6 pixels underneath.
         */
        int toneRowY =
                8
                        + TARGET_BUTTON_SIZE
                        + 6;

        int textY =
                toneRowY
                        + TONE_BUTTON_SIZE
                        + 6;

        guiGraphics.drawCenteredString(
                minecraft.font,
                actionNames,
                screenWidth / 2,
                textY,
                0xFFFFFFFF
        );
    }

    private static void renderSelectedToneFrame(
            GuiGraphics guiGraphics,
            int rowStartX,
            int rowY
    ) {
        int selectedIndex =
                InteractionHudState
                        .getSelectedTone()
                        .ordinal();

        int selectedX =
                rowStartX
                        + selectedIndex
                        * (TONE_BUTTON_SIZE
                        + TONE_BUTTON_SPACING);

        guiGraphics.renderOutline(
                selectedX,
                rowY,
                TONE_BUTTON_SIZE,
                TONE_BUTTON_SIZE,
                0xFFFFFFFF
        );
    }

    private static void renderActionSelectionFrame(
            GuiGraphics guiGraphics,
            int screenWidth,
            int screenHeight
    ) {
        if (InteractionHudState.getSelectionMode()
                != InteractionHudState.SelectionMode.ACTIONS) {
            return;
        }

        int upperRowY =
                (screenHeight - ACTION_BUTTON_SIZE) / 2;

        int lowerRowY =
                upperRowY
                        + ACTION_BUTTON_SIZE
                        + ACTION_ROW_GAP;

        int centerX = screenWidth / 2;

        int leftGroupRightEdge =
                centerX - ACTION_CENTER_GAP / 2;

        int rightGroupStartX =
                centerX + ACTION_CENTER_GAP / 2;

        int groupWidth =
                ACTION_BUTTON_SIZE * 4
                        + ACTION_BUTTON_SPACING * 3;

        int groupHeight =
                ACTION_BUTTON_SIZE;

        int selectedGroup =
                InteractionHudState.getSelectedActionGroup();

        int groupX;
        int groupY;

        switch (selectedGroup) {
            case 0 -> {
                /*
                 * Upper-left.
                 */
                groupX =
                        leftGroupRightEdge
                                - groupWidth;

                groupY = upperRowY;
            }

            case 1 -> {
                /*
                 * Upper-right.
                 */
                groupX = rightGroupStartX;
                groupY = upperRowY;
            }

            case 2 -> {
                /*
                 * Lower-left.
                 */
                groupX =
                        leftGroupRightEdge
                                - groupWidth;

                groupY = lowerRowY;
            }

            case 3 -> {
                /*
                 * Lower-right.
                 */
                groupX = rightGroupStartX;
                groupY = lowerRowY;
            }

            default -> {
                return;
            }
        }

        int frameX =
                groupX - ACTION_GROUP_MARGIN;

        int frameY =
                groupY - ACTION_GROUP_MARGIN;

        int frameWidth =
                groupWidth
                        + ACTION_GROUP_MARGIN * 2;

        int frameHeight =
                groupHeight
                        + ACTION_GROUP_MARGIN * 2;

        guiGraphics.renderOutline(
                frameX,
                frameY,
                frameWidth,
                frameHeight,
                0xFF101010
        );

        guiGraphics.renderOutline(
                frameX + 1,
                frameY + 1,
                frameWidth - 2,
                frameHeight - 2,
                0xFFF0FFF0
        );

        guiGraphics.renderOutline(
                frameX + 2,
                frameY + 2,
                frameWidth - 4,
                frameHeight - 4,
                0xFF9DAA98
        );
    }
}