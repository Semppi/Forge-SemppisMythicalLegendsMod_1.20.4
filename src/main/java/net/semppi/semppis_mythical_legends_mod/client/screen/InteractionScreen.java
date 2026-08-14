//package net.semppi.semppis_mythical_legends_mod.client.screen;
//
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.client.gui.components.Button;
//import net.minecraft.client.gui.screens.Screen;
//import net.minecraft.network.chat.CommonComponents;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.semppi.semppis_mythical_legends_mod.client.ModKeyMappings;
//import org.lwjgl.glfw.GLFW;
//
//public class InteractionScreen extends Screen {
//
//    private static final ResourceLocation NEUTRAL_TONE_ICON =
//            new ResourceLocation(
//                    "semppis_mythical_legends_mod",
//                    "textures/gui/interaction/tone_neutral.png"
//            );
//
//    private static final ResourceLocation CALL_ICON =
//            new ResourceLocation(
//                    "semppis_mythical_legends_mod",
//                    "textures/gui/interaction/call.png"
//            );
//
//    private static final int INTERACTION_ICON_SIZE = 24;
//
//    private int callIconX;
//    private int callIconY;
//
//    private int neutralToneIconX;
//    private int neutralToneIconY;
//
//    private int softToneButtonX;
//    private int softToneButtonY;
//
//    private int neutralToneButtonX;
//    private int neutralToneButtonY;
//
//    private int respectfulToneButtonX;
//    private int respectfulToneButtonY;
//
//    private int teasingToneButtonX;
//    private int teasingToneButtonY;
//
//    private int confrontingToneButtonX;
//    private int confrontingToneButtonY;
//
//    private static final int TONE_BUTTON_SIZE = 32;
//    private static final int TONE_ICON_SIZE = 28;
//
//    private static final int TONE_ROW_SPACING = 4;
//    private static final int TONE_ROW_BUTTON_COUNT = 5;
//    private static final int TONE_GROUP_MARGIN = 5;
//
//    public InteractionScreen() {
//        super(
//                Component.translatable(
//                        "screen.semppis_mythical_legends_mod.interaction"
//                )
//        );
//    }
//
//    private enum Tone {
//        TEASING,
//        SOFT,
//        NEUTRAL,
//        RESPECTFUL,
//        CONFRONTING
//    }
//
//    private Tone selectedTone = Tone.NEUTRAL;
//
//    private Button teasingToneButton;
//    private Button softToneButton;
//    private Button neutralToneButton;
//    private Button respectfulToneButton;
//    private Button confrontingToneButton;
//
//    @Override
//    public boolean mouseScrolled(
//            double mouseX,
//            double mouseY,
//            double scrollDeltaX,
//            double scrollDeltaY
//    ) {
//        if (scrollDeltaY > 0.0D) {
//            /*
//             * Scroll upward:
//             * move one tone to the left.
//             */
//            this.selectTone(this.getPreviousTone());
//            return true;
//        }
//
//        if (scrollDeltaY < 0.0D) {
//            /*
//             * Scroll downward:
//             * move one tone to the right.
//             */
//            this.selectTone(this.getNextTone());
//            return true;
//        }
//
//        return super.mouseScrolled(
//                mouseX,
//                mouseY,
//                scrollDeltaX,
//                scrollDeltaY
//        );
//    }
//
//    private Tone getPreviousTone() {
//        Tone[] tones = Tone.values();
//
//        int previousIndex = Math.floorMod(
//                this.selectedTone.ordinal() - 1,
//                tones.length
//        );
//
//        return tones[previousIndex];
//    }
//
//    private Tone getNextTone() {
//        Tone[] tones = Tone.values();
//
//        int nextIndex = Math.floorMod(
//                this.selectedTone.ordinal() + 1,
//                tones.length
//        );
//
//        return tones[nextIndex];
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//
//        /*
//         * Top-center button: 36 × 36
//         */
//        int topButtonSize = 36;
//        int topButtonX = (this.width - topButtonSize) / 2;
//        int topButtonY = 8;
//
//        this.addRenderableWidget(
//                Button.builder(
//                                CommonComponents.EMPTY,
//                                button -> {
//                                    // Top button
//                                }
//                        )
//                        .bounds(
//                                topButtonX,
//                                topButtonY,
//                                topButtonSize,
//                                topButtonSize
//                        )
//                        .build()
//        );
//
//        /*
//         * Five-button row: each button is 32 × 32.
//         */
//        int rowButtonSize = 32;
//        int rowButtonSpacing = 4;
//        int rowButtonCount = 5;
//
//        int totalRowWidth =
//                rowButtonCount * rowButtonSize
//                        + (rowButtonCount - 1) * rowButtonSpacing;
//
//        int rowStartX = (this.width - totalRowWidth) / 2;
//
//        int rowY = topButtonY + topButtonSize + 6;
//
//        for (int index = 0; index < rowButtonCount; index++) {
//            int buttonX =
//                    rowStartX
//                            + index * (rowButtonSize + rowButtonSpacing);
//
//            final Tone tone = Tone.values()[index];
//
//            Button toneButton = Button.builder(
//                            CommonComponents.EMPTY,
//                            button -> this.selectTone(tone)
//                    )
//                    .bounds(
//                            buttonX,
//                            rowY,
//                            rowButtonSize,
//                            rowButtonSize
//                    )
//                    .build();
//
//            switch (tone) {
//                case TEASING -> this.teasingToneButton = toneButton;
//                case SOFT -> this.softToneButton = toneButton;
//                case NEUTRAL -> this.neutralToneButton = toneButton;
//                case RESPECTFUL -> this.respectfulToneButton = toneButton;
//                case CONFRONTING -> this.confrontingToneButton = toneButton;
//            }
//
//            this.addRenderableWidget(toneButton);
//        }
//
//        /*
//         * Neutral starts selected, so disable Neutral and enable
//         * the other four tone buttons.
//         */
//        this.updateToneButtonStates();
//
//        /*
//         * The neutral tone is the center button, index 2.
//         *
//         * The button is 32 × 32 and the icon is 28 × 28,
//         * leaving two pixels of padding on each side.
//         */
//        /*
//         * Tone-button indexes:
//         *
//         * 0 = Teasing
//         * 1 = Soft
//         * 2 = Neutral
//         * 3 = Respectful
//         * 4 = Confronting
//         */
//
//        this.teasingToneButtonX = rowStartX;
//        this.teasingToneButtonY = rowY;
//
//        this.softToneButtonX =
//                rowStartX
//                        + (rowButtonSize + rowButtonSpacing);
//        this.softToneButtonY = rowY;
//
//        this.neutralToneButtonX =
//                rowStartX
//                        + 2 * (rowButtonSize + rowButtonSpacing);
//        this.neutralToneButtonY = rowY;
//
//        this.respectfulToneButtonX =
//                rowStartX
//                        + 3 * (rowButtonSize + rowButtonSpacing);
//        this.respectfulToneButtonY = rowY;
//
//        this.confrontingToneButtonX =
//                rowStartX
//                        + 4 * (rowButtonSize + rowButtonSpacing);
//        this.confrontingToneButtonY = rowY;
//
//        this.neutralToneIconX = this.neutralToneButtonX + 2;
//        this.neutralToneIconY = this.neutralToneButtonY + 2;
//
//        /*
//         * Small interaction buttons: 28 × 28
//         *
//         * Two rows:
//         * - four buttons on the left
//         * - four buttons on the right
//         *
//         * The center gap can fit one hypothetical 28 × 28 button.
//         * The vertical gap between rows is also 28 pixels.
//         */
//        int interactionButtonSize = 28;
//        int interactionButtonSpacing = 4;
//        int interactionRowGap = interactionButtonSize;
//
//        int upperInteractionY =
//                (this.height - interactionButtonSize) / 2;
//
//        int lowerInteractionY =
//                upperInteractionY
//                        + interactionButtonSize
//                        + interactionRowGap;
//
//        int centerX = this.width / 2;
//
//        int centerGap =
//                interactionButtonSize
//                        + interactionButtonSpacing * 2;
//
//        int leftGroupRightEdge =
//                centerX - centerGap / 2;
//
//        int rightGroupStartX =
//                centerX + centerGap / 2;
//
//        /*
//         * Upper row: four buttons on the left.
//         */
//        for (int index = 1; index <= 4; index++) {
//            int buttonX =
//                    leftGroupRightEdge
//                            - index * interactionButtonSize
//                            - (index - 1) * interactionButtonSpacing;
//
//            final int buttonNumber = 4 - index;
//
//            /*
//             * Button 0 is the far-left button of the upper row.
//             */
//            if (buttonNumber == 0) {
//                this.callIconX = buttonX + 2;
//                this.callIconY = upperInteractionY + 2;
//            }
//
//            this.addRenderableWidget(
//                    Button.builder(
//                                    CommonComponents.EMPTY,
//                                    button -> {
//                                        // Upper-left interaction button.
//                                        // Button 0 will eventually perform "Call".
//                                    }
//                            )
//                            .bounds(
//                                    buttonX,
//                                    upperInteractionY,
//                                    interactionButtonSize,
//                                    interactionButtonSize
//                            )
//                            .build()
//            );
//        }
//
//        /*
//         * Upper row: four buttons on the right.
//         */
//        for (int index = 0; index < 4; index++) {
//            int buttonX =
//                    rightGroupStartX
//                            + index * (
//                            interactionButtonSize
//                                    + interactionButtonSpacing
//                    );
//
//            final int buttonNumber = index + 4;
//
//            this.addRenderableWidget(
//                    Button.builder(
//                                    CommonComponents.EMPTY,
//                                    button -> {
//                                        // Upper-right interaction button.
//                                        // buttonNumber ranges from 4 to 7.
//                                    }
//                            )
//                            .bounds(
//                                    buttonX,
//                                    upperInteractionY,
//                                    interactionButtonSize,
//                                    interactionButtonSize
//                            )
//                            .build()
//            );
//        }
//
//        /*
//         * Lower row: four buttons on the left.
//         */
//        for (int index = 1; index <= 4; index++) {
//            int buttonX =
//                    leftGroupRightEdge
//                            - index * interactionButtonSize
//                            - (index - 1) * interactionButtonSpacing;
//
//            final int buttonNumber = 8 + (4 - index);
//
//            this.addRenderableWidget(
//                    Button.builder(
//                                    CommonComponents.EMPTY,
//                                    button -> {
//                                        // Lower-left interaction button.
//                                        // buttonNumber ranges from 8 to 11.
//                                    }
//                            )
//                            .bounds(
//                                    buttonX,
//                                    lowerInteractionY,
//                                    interactionButtonSize,
//                                    interactionButtonSize
//                            )
//                            .build()
//            );
//        }
//
//        /*
//         * Lower row: four buttons on the right.
//         */
//        for (int index = 0; index < 4; index++) {
//            int buttonX =
//                    rightGroupStartX
//                            + index * (
//                            interactionButtonSize
//                                    + interactionButtonSpacing
//                    );
//
//            final int buttonNumber = index + 12;
//
//            this.addRenderableWidget(
//                    Button.builder(
//                                    CommonComponents.EMPTY,
//                                    button -> {
//                                        // Lower-right interaction button.
//                                        // buttonNumber ranges from 12 to 15.
//                                    }
//                            )
//                            .bounds(
//                                    buttonX,
//                                    lowerInteractionY,
//                                    interactionButtonSize,
//                                    interactionButtonSize
//                            )
//                            .build()
//            );
//        }
//    }
//
//    private void selectTone(Tone newTone) {
//        if (newTone == this.selectedTone) {
//            return;
//        }
//
//        this.selectedTone = newTone;
//        this.updateToneButtonStates();
//    }
//
//    private void updateToneButtonStates() {
//        if (this.teasingToneButton != null) {
//            this.teasingToneButton.active =
//                    this.selectedTone != Tone.TEASING;
//        }
//
//        if (this.softToneButton != null) {
//            this.softToneButton.active =
//                    this.selectedTone != Tone.SOFT;
//        }
//
//        if (this.neutralToneButton != null) {
//            this.neutralToneButton.active =
//                    this.selectedTone != Tone.NEUTRAL;
//        }
//
//        if (this.respectfulToneButton != null) {
//            this.respectfulToneButton.active =
//                    this.selectedTone != Tone.RESPECTFUL;
//        }
//
//        if (this.confrontingToneButton != null) {
//            this.confrontingToneButton.active =
//                    this.selectedTone != Tone.CONFRONTING;
//        }
//    }
//
//    private int getSelectedToneButtonX() {
//        return switch (this.selectedTone) {
//            case TEASING -> this.teasingToneButtonX;
//            case SOFT -> this.softToneButtonX;
//            case NEUTRAL -> this.neutralToneButtonX;
//            case RESPECTFUL -> this.respectfulToneButtonX;
//            case CONFRONTING -> this.confrontingToneButtonX;
//        };
//    }
//
//    private int getSelectedToneButtonY() {
//        return switch (this.selectedTone) {
//            case TEASING -> this.teasingToneButtonY;
//            case SOFT -> this.softToneButtonY;
//            case NEUTRAL -> this.neutralToneButtonY;
//            case RESPECTFUL -> this.respectfulToneButtonY;
//            case CONFRONTING -> this.confrontingToneButtonY;
//        };
//    }
//
//    private void setSelectedToneRenderColor(GuiGraphics guiGraphics) {
//        switch (this.selectedTone) {
//            case TEASING -> guiGraphics.setColor(
//                    240.0F / 255.0F,
//                    128.0F / 255.0F,
//                    192.0F / 255.0F,
//                    1.0F
//            );
//
//            case SOFT -> guiGraphics.setColor(
//                    101.0F / 255.0F,
//                    212.0F / 255.0F,
//                    126.0F / 255.0F,
//                    1.0F
//            );
//
//            case NEUTRAL -> guiGraphics.setColor(
//                    95.0F / 255.0F,
//                    168.0F / 255.0F,
//                    255.0F / 255.0F,
//                    1.0F
//            );
//
//            case RESPECTFUL -> guiGraphics.setColor(
//                    242.0F / 255.0F,
//                    206.0F / 255.0F,
//                    99.0F / 255.0F,
//                    1.0F
//            );
//
//            case CONFRONTING -> guiGraphics.setColor(
//                    232.0F / 255.0F,
//                    90.0F / 255.0F,
//                    90.0F / 255.0F,
//                    1.0F
//            );
//        }
//    }
//
//    private void renderToneSelectionFrame(GuiGraphics guiGraphics) {
//        int toneRowWidth =
//                TONE_ROW_BUTTON_COUNT * TONE_BUTTON_SIZE
//                        + (TONE_ROW_BUTTON_COUNT - 1) * TONE_ROW_SPACING;
//
//        int frameX = this.teasingToneButtonX - TONE_GROUP_MARGIN;
//        int frameY = this.teasingToneButtonY - TONE_GROUP_MARGIN;
//
//        int frameWidth = toneRowWidth + TONE_GROUP_MARGIN * 2;
//        int frameHeight = TONE_BUTTON_SIZE + TONE_GROUP_MARGIN * 2;
//
//        /*
//         * Outer dark edge.
//         */
//        guiGraphics.renderOutline(
//                frameX,
//                frameY,
//                frameWidth,
//                frameHeight,
//                0xFF101010
//        );
//
//        /*
//         * Bright hotbar-style selection edge.
//         */
//        guiGraphics.renderOutline(
//                frameX + 1,
//                frameY + 1,
//                frameWidth - 2,
//                frameHeight - 2,
//                0xFFF0FFF0
//        );
//
//        /*
//         * Muted inner edge gives it a raised, framed appearance.
//         */
//        guiGraphics.renderOutline(
//                frameX + 2,
//                frameY + 2,
//                frameWidth - 4,
//                frameHeight - 4,
//                0xFF9DAA98
//        );
//    }
//
//    @Override
//    public void render(
//            GuiGraphics guiGraphics,
//            int mouseX,
//            int mouseY,
//            float partialTick
//    ) {
//        /*
//         * Slightly darken the world behind the interface.
//         */
//        guiGraphics.fill(
//                0,
//                0,
//                this.width,
//                this.height,
//                0x08500000
//        );
//
//        /*
//         * Draw every vanilla button exactly once.
//         */
//        super.render(guiGraphics, mouseX, mouseY, partialTick);
//
//        /*
//         * Draw all five tone tints over their vanilla buttons.
//         */
//        guiGraphics.fill(
//                this.teasingToneButtonX + 1,
//                this.teasingToneButtonY + 1,
//                this.teasingToneButtonX + TONE_BUTTON_SIZE - 1,
//                this.teasingToneButtonY + TONE_BUTTON_SIZE - 1,
//                0x66F080C0
//        );
//
//        guiGraphics.fill(
//                this.softToneButtonX + 1,
//                this.softToneButtonY + 1,
//                this.softToneButtonX + TONE_BUTTON_SIZE - 1,
//                this.softToneButtonY + TONE_BUTTON_SIZE - 1,
//                0x6665D47E
//        );
//
//        guiGraphics.fill(
//                this.neutralToneButtonX + 1,
//                this.neutralToneButtonY + 1,
//                this.neutralToneButtonX + TONE_BUTTON_SIZE - 1,
//                this.neutralToneButtonY + TONE_BUTTON_SIZE - 1,
//                0x665FA8FF
//        );
//
//        guiGraphics.fill(
//                this.respectfulToneButtonX + 1,
//                this.respectfulToneButtonY + 1,
//                this.respectfulToneButtonX + TONE_BUTTON_SIZE - 1,
//                this.respectfulToneButtonY + TONE_BUTTON_SIZE - 1,
//                0x66F2CE63
//        );
//
//        guiGraphics.fill(
//                this.confrontingToneButtonX + 1,
//                this.confrontingToneButtonY + 1,
//                this.confrontingToneButtonX + TONE_BUTTON_SIZE - 1,
//                this.confrontingToneButtonY + TONE_BUTTON_SIZE - 1,
//                0x66E85A5A
//        );
//
//        /*
//         * Draw the large frame around the complete tone row.
//         * This indicates that the scroll wheel currently controls tone selection.
//         */
//        this.renderToneSelectionFrame(guiGraphics);
//
//        /*
//         * Draw the white outline around the currently selected individual tone.
//         */
//        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
//
//        guiGraphics.renderOutline(
//                this.getSelectedToneButtonX(),
//                this.getSelectedToneButtonY(),
//                TONE_BUTTON_SIZE,
//                TONE_BUTTON_SIZE,
//                0xFFFFFFFF
//        );
//
//        /*
//         * Draw the Neutral icon in its original colors.
//         */
//        guiGraphics.blit(
//                NEUTRAL_TONE_ICON,
//                this.neutralToneIconX,
//                this.neutralToneIconY,
//                0.0F,
//                0.0F,
//                TONE_ICON_SIZE,
//                TONE_ICON_SIZE,
//                TONE_ICON_SIZE,
//                TONE_ICON_SIZE
//        );
//
//        /*
//         * Tint the white Call icon according to the selected tone.
//         */
//        this.setSelectedToneRenderColor(guiGraphics);
//
//        guiGraphics.blit(
//                CALL_ICON,
//                this.callIconX,
//                this.callIconY,
//                0.0F,
//                0.0F,
//                INTERACTION_ICON_SIZE,
//                INTERACTION_ICON_SIZE,
//                INTERACTION_ICON_SIZE,
//                INTERACTION_ICON_SIZE
//        );
//
//        /*
//         * Restore normal white rendering.
//         */
//        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
//    }
//
//    @Override
//    public boolean keyPressed(
//            int keyCode,
//            int scanCode,
//            int modifiers
//    ) {
//        if (ModKeyMappings.INTERACTION_SCREEN.matches(keyCode, scanCode)
//                || keyCode == GLFW.GLFW_KEY_E
//                || keyCode == GLFW.GLFW_KEY_ESCAPE) {
//
//            this.onClose();
//            return true;
//        }
//
//        return super.keyPressed(keyCode, scanCode, modifiers);
//    }
//
//    @Override
//    public boolean isPauseScreen() {
//        return false;
//    }
//}