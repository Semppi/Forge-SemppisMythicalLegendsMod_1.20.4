package net.semppi.semppis_mythical_legends_mod.block;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.custom.*;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, SemppisMythicalLegendsMod.MOD_ID);

    public static final RegistryObject<Block> WENDIGO_SKULL = BLOCKS.register("wendigo_skull",
            () -> new WendigoSkull(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(1.0F, 4.0F)
                    .sound(SoundType.STONE)
                    .noOcclusion()));

    public static final RegistryObject<Block> PUKIS_EGG = BLOCKS.register("pukis_egg",
            () -> new PukisEgg(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(0.6F, 2.5F)
                    .sound(SoundType.STONE)
                    .noOcclusion()));

    public static final RegistryObject<Block> MEDIUM_HUMANOID_DROP =
            registerBlock(
                    "medium_humanoid_drop",
                    () -> new MediumHumanoidDropBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                                    .strength(0.8F)
                                    .sound(SoundType.BONE_BLOCK)
                                    .noOcclusion()
                    )
            );

    public static final RegistryObject<Block> CRAFTING_OVEN =
            BLOCKS.register(
                    "crafting_oven",
                    () -> new CraftingOvenBlock(
                            BlockBehaviour.Properties.of()
                                    .mapColor(MapColor.COLOR_RED)
                                    .strength(3.5F)
                                    .sound(SoundType.STONE)
                                    .requiresCorrectToolForDrops()
                                    .lightLevel(
                                            state -> state.getValue(CraftingOvenBlock.LIT)
                                                    ? 13
                                                    : 0
                                    )
                                    .noOcclusion()
                    )
            );
    public static final RegistryObject<Block> CRAFTING_OVEN_PART =
            BLOCKS.register(
                    "crafting_oven_part",
                    () -> new CraftingOvenPartBlock(
                            BlockBehaviour.Properties.of()
                                    .strength(3.5F)
                                    .noOcclusion()
                                    .noLootTable()
                    )
            );

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}