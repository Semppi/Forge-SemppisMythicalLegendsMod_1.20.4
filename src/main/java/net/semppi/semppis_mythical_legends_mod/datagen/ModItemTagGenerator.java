package net.semppi.semppis_mythical_legends_mod.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerator extends ItemTagsProvider {

    public static final TagKey<Item> TREE_LEAVES = createTag("tree_leaves");
    public static final TagKey<Item> RAW_FISH = createTag("raw_fish");
    public static final TagKey<Item> COOKED_FISH = createTag("cooked_fish");

    public ModItemTagGenerator(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pLookupProvider,
                               CompletableFuture<TagLookup<Block>> pBlockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pLookupProvider, pBlockTags, SemppisMythicalLegendsMod.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(TREE_LEAVES).add(
                net.semppi.semppis_mythical_legends_mod.item.ModItems.EDIBLE_LEAF.get(),
                net.minecraft.world.item.Items.OAK_LEAVES,
                net.minecraft.world.item.Items.BIRCH_LEAVES,
                net.minecraft.world.item.Items.SPRUCE_LEAVES,
                net.minecraft.world.item.Items.DARK_OAK_LEAVES,
                net.minecraft.world.item.Items.ACACIA_LEAVES,
                net.minecraft.world.item.Items.JUNGLE_LEAVES,
                net.minecraft.world.item.Items.MANGROVE_LEAVES,
                net.minecraft.world.item.Items.AZALEA_LEAVES
        );
        tag(RAW_FISH).add(
                net.minecraft.world.item.Items.COD,
                net.minecraft.world.item.Items.SALMON,
                net.minecraft.world.item.Items.TROPICAL_FISH,
                ModItems.RAW_FISH_MEAT.get()
        );
        tag(COOKED_FISH).add(
                net.minecraft.world.item.Items.COOKED_COD,
                net.minecraft.world.item.Items.COOKED_SALMON,
                ModItems.COOKED_FISH_MEAT.get()
        );
    }

    private static TagKey<Item> createTag(String name) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(SemppisMythicalLegendsMod.MOD_ID, name));
    }
}