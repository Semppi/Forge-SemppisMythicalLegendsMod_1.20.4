package net.semppi.semppis_mythical_legends_mod.entity.personality;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;

/** Species defaults come from the creature class. Only individual traits belong to the save. */
public record CreaturePersonality(Set<String> coreTraits, Set<String> individualTraits) {
    private static final String SAVE_KEY = "SmlPersonality";

    public CreaturePersonality {
        coreTraits = Set.copyOf(coreTraits);
        individualTraits = Set.copyOf(individualTraits);
    }

    public void save(CompoundTag tag) {
        ListTag traits = new ListTag();
        individualTraits.stream().sorted().map(StringTag::valueOf).forEach(traits::add);
        tag.put(SAVE_KEY, traits);
    }

    public static CreaturePersonality load(CompoundTag tag, Set<String> coreTraits) {
        ListTag traits = tag.getList(SAVE_KEY, Tag.TAG_STRING);
        Set<String> individualTraits = new HashSet<>();
        for (int i = 0; i < traits.size(); i++) {
            individualTraits.add(traits.getString(i));
        }
        return new CreaturePersonality(coreTraits, individualTraits);
    }
}
