package net.semppi.semppis_mythical_legends_mod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.entity.custom.*;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SemppisMythicalLegendsMod.MOD_ID);

    public static final RegistryObject<EntityType<SatyrEntity>> SATYR =
            ENTITY_TYPES.register("satyr", () -> EntityType.Builder.of(SatyrEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.7f).build("satyr"));

    public static final RegistryObject<EntityType<ColossalLobsterEntity>> COLOSSAL_LOBSTER =
            ENTITY_TYPES.register("colossal_lobster", () -> EntityType.Builder.of(ColossalLobsterEntity::new, MobCategory.WATER_CREATURE)
                    .sized(1.7f, 2.3f).build("colossal_lobster"));

    public static final RegistryObject<EntityType<BehemothEntity>> BEHEMOTH =
            ENTITY_TYPES.register("behemoth", () -> EntityType.Builder.of(BehemothEntity::new, MobCategory.CREATURE)
                    .sized(6.2f, 6.4f).build("behemoth"));

    public static final RegistryObject<EntityType<PukisEntity>> PUKIS =
            ENTITY_TYPES.register("pukis", () -> EntityType.Builder.of(PukisEntity::new, MobCategory.CREATURE)
                    .sized(0.7f, 1.4f).build("pukis"));

    public static final RegistryObject<EntityType<MandrakeEntity>> MANDRAKE =
            ENTITY_TYPES.register("mandrake", () -> EntityType.Builder.of(MandrakeEntity::new, MobCategory.CREATURE)
                    .sized(0.4f, 0.6f).build("mandrake"));

    public static final RegistryObject<EntityType<WendigoEntity>> WENDIGO =
            ENTITY_TYPES.register("wendigo", () -> EntityType.Builder.of(WendigoEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 2.9f).build("wendigo"));

    public static final RegistryObject<EntityType<LovelandFrogmanEntity>> LOVELAND_FROGMAN =
            ENTITY_TYPES.register("loveland_frogman", () -> EntityType.Builder.of(LovelandFrogmanEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.0f).build("loveland_frogman"));

    public static final RegistryObject<EntityType<MalphasEntity>> MALPHAS =
            ENTITY_TYPES.register("malphas", () -> EntityType.Builder.of(MalphasEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 2.9f).build("malphas"));

    public static final RegistryObject<EntityType<KrakenEntity>> KRAKEN =
            ENTITY_TYPES.register("kraken", () -> EntityType.Builder.of(KrakenEntity::new, MobCategory.CREATURE)
                    .sized(3.2f, 3.0f).build("kraken"));

    public static final RegistryObject<EntityType<AlicantoEntity>> ALICANTO =
            ENTITY_TYPES.register("alicanto", () -> EntityType.Builder.of(AlicantoEntity::new, MobCategory.CREATURE)
                    .sized(0.5f, 1.8f).build("alicanto"));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
