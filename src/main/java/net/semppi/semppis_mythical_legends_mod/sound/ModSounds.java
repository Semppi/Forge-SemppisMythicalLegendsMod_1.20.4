package net.semppi.semppis_mythical_legends_mod.sound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SemppisMythicalLegendsMod.MOD_ID);

    public static final RegistryObject<SoundEvent> LARGE_CROW = registerSoundEvent("large_crow");
    public static final RegistryObject<SoundEvent> LARGE_CROW_HURT = registerSoundEvent("large_crow_hurt");
    public static final RegistryObject<SoundEvent> MANDRAKE_SCREAM1 = registerSoundEvent("mandrake_scream1");
    public static final RegistryObject<SoundEvent> MANDRAKE_SCREAM2 = registerSoundEvent("mandrake_scream2");
    public static final RegistryObject<SoundEvent> MANDRAKE_SCREAM3 = registerSoundEvent("mandrake_scream3");
    public static final RegistryObject<SoundEvent> MANDRAKE_UPSET = registerSoundEvent("mandrake_upset");

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}