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

        add("spruce_tips_from_spruce_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SPRUCE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.SPRUCE_TIPS.get()));

        add("edible_leaf_from_birch_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BIRCH_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_jungle_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.JUNGLE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_acacia_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.ACACIA_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.07f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_mangrove_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.MANGROVE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.07f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("edible_leaf_from_cherry_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.CHERRY_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.07f).build()}, ModItems.EDIBLE_LEAF.get()));

        add("tropical_leaf_from_jungle_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.JUNGLE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.TROPICAL_LEAF.get()));

        add("tropical_leaf_from_acacia_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.ACACIA_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.TROPICAL_LEAF.get()));

        add("tropical_leaf_from_mangrove_leaves", new AddItemModifier(new LootItemCondition[] {
                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.MANGROVE_LEAVES).build(),
                LootItemRandomChanceCondition.randomChance(0.11f).build()}, ModItems.TROPICAL_LEAF.get()));

        add("ghastly_teeth_from_ghast", new AddItemModifier(
                new LootItemCondition[] {
                        new LootTableIdCondition.Builder(new ResourceLocation("minecraft", "entities/ghast")).build(),
                        LootItemRandomChanceCondition.randomChance(0.7f).build()
                },
                ModItems.GHASTLY_TEETH.get()
        ));
    }
}