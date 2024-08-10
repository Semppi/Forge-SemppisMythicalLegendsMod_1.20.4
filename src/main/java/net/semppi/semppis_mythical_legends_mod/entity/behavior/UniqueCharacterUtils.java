package net.semppi.semppis_mythical_legends_mod.entity.behavior;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.semppi.semppis_mythical_legends_mod.entity.custom.MalphasEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UniqueCharacterUtils {
    private static final Map<Integer, MalphasEntity> malphasMap = new HashMap<>();
    private static int nextValue = 1;
    private static final Random RANDOM = new Random();

    public static void manageMalphasSpawning(MalphasEntity newMalphas, ServerLevel world) {
        // Check if there's any Malphas entity in the world and remove it
        for (MalphasEntity malphas : malphasMap.values()) {
            if (malphas != null && malphas.isAlive()) {
                malphas.remove(Entity.RemovalReason.DISCARDED);
            }
        }

        int value = nextValue;
        nextValue = (nextValue == 10) ? 0 : nextValue + 1;

        MalphasEntity existingMalphas = malphasMap.get(value);
        if (existingMalphas != null && existingMalphas.isAlive()) {
            // Coin flip to decide which Malphas to remove if there's already one with the same value
            if (RANDOM.nextBoolean()) {
                existingMalphas.remove(Entity.RemovalReason.DISCARDED);
            } else {
                newMalphas.remove(Entity.RemovalReason.DISCARDED);
                return; // Do not proceed with adding the new Malphas since it was removed
            }
        }

        malphasMap.put(value, newMalphas);
        newMalphas.setValue(value);
    }

    public static void removeMalphas(int value) {
        malphasMap.remove(value);
    }

    public static MalphasEntity getMalphas(int value) {
        return malphasMap.get(value);
    }
}