package net.semppi.semppis_mythical_legends_mod.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;

public final class ModDamageTypes {

    public static final ResourceKey<DamageType> SOUND =
            ResourceKey.create(
                    Registries.DAMAGE_TYPE,
                    new ResourceLocation(
                            SemppisMythicalLegendsMod.MOD_ID,
                            "sound"
                    )
            );

    private ModDamageTypes() {
    }
}