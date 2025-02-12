package net.semppi.semppis_mythical_legends_mod.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import net.semppi.semppis_mythical_legends_mod.loot.AddItemModifier;
import net.semppi.semppis_mythical_legends_mod.loot.AddMultipleItemsModifier;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, SemppisMythicalLegendsMod.MOD_ID);
    }

    @Override
    protected void start() {
        add("edible_leaf_from_oak_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.OAK_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_dark_oak_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.DARK_OAK_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_spruce_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SPRUCE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.07f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_birch_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BIRCH_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_jungle_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.JUNGLE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.16f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_acacia_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.ACACIA_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_mangrove_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.MANGROVE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.07f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_cherry_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.CHERRY_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("ghastly_teeth_from_ghast", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("entities/ghast")).build(),
                LootItemRandomChanceCondition.randomChance(0.7f).build() // 70% chance
        }, ModItems.GHASTLY_TEETH.get()));

        add("raw_bushmeat_piece_from_bat", new AddMultipleItemsModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/bat")).build()
                },
                ModItems.RAW_BUSHMEAT_PIECE.get(),
                0, // Minimum count
                2  // Maximum count
        ));

        add("raw_avian_piece_from_parrot", new AddMultipleItemsModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/parrot")).build()
                },
                ModItems.RAW_AVIAN_PIECE.get(),
                0, // Minimum count
                2  // Maximum count
        ));


        add("raw_bushmeat_from_wolf", new AddMultipleItemsModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/wolf")).build()
                },
                ModItems.RAW_BUSHMEAT.get(),
                0, // Minimum count
                2  // Maximum count
        ));

        add("raw_ungulate_meat_from_horse", new AddMultipleItemsModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/horse")).build()
                },
                ModItems.RAW_UNGULATE_MEAT.get(),
                1, // Minimum count
                3  // Maximum count
        ));

        add("raw_ungulate_meat_from_donkey", new AddMultipleItemsModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/donkey")).build()
                },
                ModItems.RAW_UNGULATE_MEAT.get(),
                1, // Minimum count
                3  // Maximum count
        ));

        add("raw_ungulate_meat_from_mule", new AddMultipleItemsModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/mule")).build()
                },
                ModItems.RAW_UNGULATE_MEAT.get(),
                1, // Minimum count
                3  // Maximum count
        ));

        add("raw_fish_chunk_from_elder_guardian", new AddMultipleItemsModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("entities/elder_guardian")).build()
                },
                ModItems.RAW_FISH_CHUNK.get(),
                3, // Minimum count
                11  // Maximum count
        ));

    }
}