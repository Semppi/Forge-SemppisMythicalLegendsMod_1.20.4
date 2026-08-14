package net.semppi.semppis_mythical_legends_mod.client.hud;

import net.minecraft.network.chat.Component;

public final class InteractionHudState {

    public enum Tone {
        TEASING,
        SOFT,
        NEUTRAL,
        RESPECTFUL,
        INTENSE
    }

    public enum Action {
        CALL,
        GREET,
        APPEASE,
        WARD_OFF,

        BECKON,
        SIGNAL,
        YIELD,
        BOW,

        COMPLIMENT,
        INSULT,
        THREATEN,
        LOOK_BIGGER,

        COMFORT,
        RECONCILE,
        PERFORM,
        MIMIC
    }

    private static boolean open = false;

    private static Tone selectedTone = Tone.NEUTRAL;

    private static SelectionMode selectionMode = SelectionMode.TONES;

    private static int selectedActionGroup = 0;

    private static Component targetName = null;

    public static SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public static int getSelectedActionGroup() {
        return selectedActionGroup;
    }

    public static void toggleSelectionMode() {
        if (selectionMode == SelectionMode.TONES) {
            selectionMode = SelectionMode.ACTIONS;
        } else {
            selectionMode = SelectionMode.TONES;
        }
    }

    public static void selectNextActionGroup() {
        selectedActionGroup =
                Math.floorMod(
                        selectedActionGroup + 1,
                        4
                );
    }

    public static void selectPreviousActionGroup() {
        selectedActionGroup =
                Math.floorMod(
                        selectedActionGroup - 1,
                        4
                );
    }

    public static Action getAction(
            int group,
            int slot
    ) {
        return switch (group) {
            case 0 -> switch (slot) {
                case 0 -> Action.CALL;
                case 1 -> Action.GREET;
                case 2 -> Action.APPEASE;
                case 3 -> Action.WARD_OFF;
                default -> null;
            };

            case 1 -> switch (slot) {
                case 0 -> Action.BECKON;
                case 1 -> Action.SIGNAL;
                case 2 -> Action.YIELD;
                case 3 -> Action.BOW;
                default -> null;
            };

            case 2 -> switch (slot) {
                case 0 -> Action.COMPLIMENT;
                case 1 -> Action.INSULT;
                case 2 -> Action.THREATEN;
                case 3 -> Action.LOOK_BIGGER;
                default -> null;
            };

            case 3 -> switch (slot) {
                case 0 -> Action.COMFORT;
                case 1 -> Action.RECONCILE;
                case 2 -> Action.PERFORM;
                case 3 -> Action.MIMIC;
                default -> null;
            };

            default -> null;
        };
    }


    private InteractionHudState() {
    }

    public static boolean isOpen() {
        return open;
    }

    public static void open() {
        open = true;
        selectionMode = SelectionMode.TONES;
    }

    public static void close() {
        open = false;
        targetName = null;
    }

    public static void toggle() {
        open = !open;

        if (open) {
            selectionMode = SelectionMode.TONES;
        } else {
            targetName = null;
        }
    }

    public static Tone getSelectedTone() {
        return selectedTone;
    }

    public static void setSelectedTone(Tone tone) {
        selectedTone = tone;
    }

    public enum SelectionMode {
        TONES,
        ACTIONS
    }

    public static void selectNextTone() {
        Tone[] tones = Tone.values();

        int nextIndex = Math.floorMod(
                selectedTone.ordinal() + 1,
                tones.length
        );

        selectedTone = tones[nextIndex];
    }

    public static void selectPreviousTone() {
        Tone[] tones = Tone.values();

        int previousIndex = Math.floorMod(
                selectedTone.ordinal() - 1,
                tones.length
        );

        selectedTone = tones[previousIndex];
    }

    public static Component getTargetName() {
        return targetName;
    }

    public static void setTargetName(Component name) {
        targetName = name;
    }

    public static void clearTarget() {
        targetName = null;
    }
}