package net.semppi.semppis_mythical_legends_mod.block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SemppisMythicalLegendsMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<WendigoSkullBlockEntity>> WENDIGO_SKULL_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("wendigo_skull_block_entity",
                    () -> BlockEntityType.Builder.of(WendigoSkullBlockEntity::new, ModBlocks.WENDIGO_SKULL.get()).build(null));

    public static final RegistryObject<BlockEntityType<PukisEggBlockEntity>> PUKIS_EGG_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("pukis_egg_block_entity",
                    () -> BlockEntityType.Builder.of(PukisEggBlockEntity::new, ModBlocks.PUKIS_EGG.get()).build(null));

    public static final RegistryObject<BlockEntityType<MediumHumanoidDropBlockEntity>>
            MEDIUM_HUMANOID_DROP_BLOCK_ENTITY =
            BLOCK_ENTITIES.register(
                    "medium_humanoid_drop_block_entity",
                    () -> BlockEntityType.Builder.of(
                            MediumHumanoidDropBlockEntity::new,
                            ModBlocks.MEDIUM_HUMANOID_DROP.get()
                    ).build(null)
            );

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}