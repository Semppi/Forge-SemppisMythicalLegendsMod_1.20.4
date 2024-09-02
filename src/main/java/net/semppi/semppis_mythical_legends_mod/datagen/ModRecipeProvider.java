package net.semppi.semppis_mythical_legends_mod.datagen;

import net.minecraft.client.Minecraft;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;

import java.util.Iterator;
import java.util.List;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    private static final List<ItemLike> FOOD_COOKING = List.of(ModItems.HUMANOID_FLESH.get());

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        System.out.println("Generating recipes...");

        // Cooking recipes
        createCookingRecipe(recipeOutput, ModItems.RAW_PORKCHOP_PIECE.get(), ModItems.COOKED_PORKCHOP_PIECE.get(), 0.1f, 200, "cooked_porkchop_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_PORKCHOP_CHUNK.get(), ModItems.COOKED_PORKCHOP_CHUNK.get(), 0.25f, 200, "cooked_porkchop_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_AVIAN_PIECE.get(), ModItems.COOKED_AVIAN_PIECE.get(), 0.1f, 200, "cooked_avian_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_AVIAN_MEAT.get(), ModItems.COOKED_AVIAN_MEAT.get(), 0.25f, 200, "cooked_avian_meat");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH.get(), ModItems.HUMANOID_STEAK.get(), 0.25f, 200, "humanoid_steak");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH_PIECE.get(), ModItems.HUMANOID_STEAK_PIECE.get(), 0.1f, 200, "humanoid_steak_piece");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH_CHUNK.get(), ModItems.HUMANOID_STEAK_CHUNK.get(), 0.25f, 200, "humanoid_steak_chunk");
        createCookingRecipe(recipeOutput, Items.BROWN_MUSHROOM, ModItems.COOKED_MUSHROOM.get(), 0.25f, 200, "cooked_mushroom");
        createCookingRecipe(recipeOutput, Items.RED_MUSHROOM, ModItems.COOKED_MUSHROOM.get(), 0.25f, 200, "cooked_mushroom");
        createCookingRecipe(recipeOutput, ModItems.FISHY_KELP_TREAT.get(), ModItems.COOKED_FISHY_KELP_TREAT.get(), 0.25f, 200, "cooked_fishy_kelp_treat");
        createCookingRecipe(recipeOutput, ModItems.VEGGIE_KELP_TREAT.get(), ModItems.COOKED_VEGGIE_KELP_TREAT.get(), 0.25f, 200, "cooked_veggie_kelp_treat");

        // Shaped recipe for Stamp
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STAMP.get(), 6)
                .pattern("S")
                .pattern("W")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('W', ItemTags.WOOL)
                .unlockedBy(getHasName(Items.OAK_SLAB), has(Items.OAK_SLAB))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "stamp"));

        // Shapeless recipe for Cod Soup
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COD_SOUP.get())
                .requires(Items.COD)
                .requires(Items.BOWL)
                .requires(Items.SEAGRASS)
                .requires(Items.POTATO)
                .unlockedBy("has_cod", has(Items.COD))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cod_soup"));

        // Shapeless recipe for Baked Cheesy Fish
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BAKED_CHEESY_FISH.get())
                .requires(Items.COOKED_COD)
                .requires(Items.BOWL)
                .requires(ModItems.RICOTTA_CHEESE.get())
                .requires(Items.BEETROOT)
                .requires(Items.BEETROOT)
                .requires(Items.BAKED_POTATO)
                .unlockedBy("has_cooked_cod", has(Items.COOKED_COD))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "baked_cheesy_fish"));

        // Shapeless recipe for Porridge
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.PORRIDGE.get())
                .requires(Items.WATER_BUCKET)
                .requires(Items.WHEAT)
                .requires(Items.WHEAT)
                .requires(Items.SUGAR)
                .requires(Items.BOWL)
                .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "porridge"));

        // Shapeless recipe for Honeyed Porridge
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HONEYED_PORRIDGE.get())
                .requires(ModItems.PORRIDGE.get())
                .requires(Items.HONEY_BOTTLE)
                .unlockedBy("has_porridge", has(ModItems.PORRIDGE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "honeyed_porridge"));

        // Shapeless recipe for Chocolate Porridge
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_PORRIDGE.get())
                .requires(ModItems.PORRIDGE.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .requires(Items.SUGAR)
                .unlockedBy("has_porridge", has(ModItems.PORRIDGE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chocolate_porridge"));

        // Shapeless recipe for Ricotta Cheese
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RICOTTA_CHEESE.get(), 6)
                .requires(Items.MILK_BUCKET)
                .requires(Items.MILK_BUCKET)
                .requires(Items.WHEAT)
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .unlockedBy("has_milk_bucket", has(Items.MILK_BUCKET))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "ricotta_cheese"));

        // Shapeless recipe for Sweet Berry Jam
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SWEET_BERRY_JAM.get())
                .requires(Items.SWEET_BERRIES)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.SUGAR)
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "sweet_berry_jam"));

        // Shapeless recipe for Chocolate Butter
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_BUTTER.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.COCOA_BEANS)
                .requires(Items.COCOA_BEANS)
                .requires(Items.SUGAR)
                .unlockedBy("has_cocoa_beans", has(Items.COCOA_BEANS))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chocolate_butter"));

        // Shapeless recipe for Nopale Paste
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.NOPALE_PASTE.get(), 8)
                .requires(Items.WATER_BUCKET)
                .requires(Items.CACTUS)
                .requires(Items.CACTUS)
                .requires(ModItems.RICOTTA_CHEESE.get())
                .requires(Items.COOKED_CHICKEN)
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .unlockedBy("has_cactus", has(Items.CACTUS))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "nopale_paste"));

        // Shapeless recipe for Sweet Berry Jam on Bread
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SWEET_BERRY_JAM_ON_BREAD.get(), 2)
                .requires(ModItems.SWEET_BERRY_JAM.get())
                .requires(Items.BREAD)
                .unlockedBy("has_sweet_berry_jam", has(ModItems.SWEET_BERRY_JAM.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "sweet_berry_jam_on_bread"));

        // Shapeless recipe for Chocolate Butter on Bread
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_BUTTER_ON_BREAD.get(), 2)
                .requires(ModItems.CHOCOLATE_BUTTER.get())
                .requires(Items.BREAD)
                .unlockedBy("has_chocolate_butter", has(ModItems.CHOCOLATE_BUTTER.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chocolate_butter_on_bread"));

        // Shapeless recipe for Nopale Paste on Bread
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.NOPALE_PASTE_ON_BREAD.get(), 2)
                .requires(ModItems.NOPALE_PASTE.get())
                .requires(ModItems.NOPALE_PASTE.get())
                .requires(Items.BREAD)
                .unlockedBy("has_nopale_paste", has(ModItems.NOPALE_PASTE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "nopale_paste_on_bread"));

        // Shapeless recipe for Spanakopita
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SPANAKOPITA.get(), 2)
                .requires(Items.WATER_BUCKET)
                .requires(Items.MILK_BUCKET)
                .requires(ModItemTagGenerator.TREE_LEAVES)
                .requires(ModItems.RICOTTA_CHEESE.get())
                .requires(Items.WHEAT_SEEDS)
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .unlockedBy("has_ricotta_cheese", has(ModItems.RICOTTA_CHEESE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "spanakopita"));

        // Shapeless recipe for Honeyed Meat Pie
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HONEYED_MEAT_PIE.get(), 4)
                .requires(Items.WATER_BUCKET)
                .requires(Items.BEEF)
                .requires(Items.WHEAT)
                .requires(Items.WHEAT)
                .requires(Items.HONEY_BOTTLE)
                .unlockedBy("has_honey_bottle", has(Items.HONEY_BOTTLE))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "honeyed_meat_pie"));

        // Shapeless recipe for Honeyed Berry Treat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HONEYED_BERRY_TREAT.get())
                .requires(Items.HONEYCOMB)
                .requires(Items.HONEYCOMB)
                .requires(Items.HONEY_BOTTLE)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.SWEET_BERRIES)
                .requires(Items.GLOW_BERRIES)
                .requires(Items.GLOW_BERRIES)
                .unlockedBy("has_honeycomb", has(Items.HONEYCOMB))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "honeyed_berry_treat"));

        // Shapeless recipe for Fishy Kelp Treat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FISHY_KELP_TREAT.get())
                .requires(ModItemTagGenerator.RAW_FISH)
                .requires(ModItemTagGenerator.RAW_FISH)
                .requires(ModItemTagGenerator.RAW_FISH)
                .requires(ModItems.RICOTTA_CHEESE.get())
                .requires(Items.KELP)
                .requires(Items.KELP)
                .requires(Items.KELP)
                .unlockedBy("has_kelp", has(Items.KELP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "fishy_kelp_treat"));

        // Shapeless recipe for Cooked Fishy Kelp Treat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FISHY_KELP_TREAT.get())
                .requires(ModItemTagGenerator.COOKED_FISH)
                .requires(ModItemTagGenerator.COOKED_FISH)
                .requires(ModItemTagGenerator.COOKED_FISH)
                .requires(ModItems.RICOTTA_CHEESE.get())
                .requires(Items.DRIED_KELP)
                .requires(Items.DRIED_KELP)
                .requires(Items.DRIED_KELP)
                .unlockedBy("has_dried_kelp", has(Items.DRIED_KELP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_fishy_kelp_treat"));

        // Shapeless recipe for Veggie Kelp Treat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.VEGGIE_KELP_TREAT.get())
                .requires(Items.POTATO)
                .requires(Items.POTATO)
                .requires(Items.CARROT)
                .requires(Items.BROWN_MUSHROOM)
                .requires(Items.KELP)
                .requires(Items.KELP)
                .requires(Items.KELP)
                .unlockedBy("has_kelp", has(Items.KELP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "veggie_kelp_treat"));

        // Shapeless recipe for Cooked Veggie Kelp Treat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_VEGGIE_KELP_TREAT.get())
                .requires(Items.BAKED_POTATO)
                .requires(Items.BAKED_POTATO)
                .requires(Items.CARROT)
                .requires(ModItems.COOKED_MUSHROOM.get())
                .requires(Items.DRIED_KELP)
                .requires(Items.DRIED_KELP)
                .requires(Items.DRIED_KELP)
                .unlockedBy("has_dried_kelp", has(Items.DRIED_KELP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_veggie_kelp_treat"));


        // Shapeless recipe crafting a Raw Porkchop to 4 Raw Porkchop Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_PORKCHOP_PIECE.get(), 4)
                .requires(Items.PORKCHOP)
                .unlockedBy(getHasName(Items.PORKCHOP), has(Items.PORKCHOP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_porkchop_piece"));

        // Shapeless recipe crafting a Cooked Porkchop to 4 Cooked Porkchop Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_PORKCHOP_PIECE.get(), 4)
                .requires(Items.COOKED_PORKCHOP)
                .unlockedBy(getHasName(Items.COOKED_PORKCHOP), has(Items.COOKED_PORKCHOP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_porkchop_piece"));

        // Shapeless recipe crafting 4 Raw Porkchop Piece to a Raw Porkchop
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.PORKCHOP)
                .requires(ModItems.RAW_PORKCHOP_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_PORKCHOP_PIECE.get()), has(ModItems.RAW_PORKCHOP_PIECE.get()))
                .save(recipeOutput, new ResourceLocation("semppis_mythical_legends_mod", "porkchop"));

        // Shapeless recipe crafting 4 Cooked Porkchop Piece to a Cooked Porkchop
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_PORKCHOP)
                .requires(ModItems.COOKED_PORKCHOP_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_PORKCHOP_PIECE.get()), has(ModItems.COOKED_PORKCHOP_PIECE.get()))
                .save(recipeOutput, new ResourceLocation("semppis_mythical_legends_mod", "cooked_porkchop"));

        // Shapeless recipe crafting 4 Raw Porkchop to a Raw Porkchop Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_PORKCHOP_CHUNK.get())
                .requires(Items.PORKCHOP, 4)
                .unlockedBy(getHasName(Items.PORKCHOP), has(Items.PORKCHOP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_porkchop_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Porkchop to a Cooked Porkchop Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_PORKCHOP_CHUNK.get())
                .requires(Items.COOKED_PORKCHOP, 4)
                .unlockedBy(getHasName(Items.COOKED_PORKCHOP), has(Items.COOKED_PORKCHOP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_porkchop_to_chunk"));

        // Shapeless recipe crafting a Raw Porkchop Chunk to 4 Raw Porkchop
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.PORKCHOP, 4)
                .requires(ModItems.RAW_PORKCHOP_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_PORKCHOP_CHUNK.get()), has(ModItems.RAW_PORKCHOP_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_porkchop_chunk_to_porkchop"));

        // Shapeless recipe crafting a Cooked Porkchop Chunk to 4 Cooked Porkchop
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 4)
                .requires(ModItems.COOKED_PORKCHOP_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_PORKCHOP_CHUNK.get()), has(ModItems.COOKED_PORKCHOP_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_porkchop_chunk_to_cooked_porkchop"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Raw Chicken to 4 Raw Avian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_PIECE.get(), 4)
                .requires(Items.CHICKEN)
                .unlockedBy(getHasName(Items.CHICKEN), has(Items.CHICKEN))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_chicken_to_pieces"));

        // Shapeless recipe crafting a Cooked Chicken to 4 Cooked Avian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_PIECE.get(), 4)
                .requires(Items.COOKED_CHICKEN)
                .unlockedBy(getHasName(Items.COOKED_CHICKEN), has(Items.COOKED_CHICKEN))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_chicken_to_pieces"));

        // Shapeless recipe crafting Raw Avian Meat to 4 Raw Avian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_PIECE.get(), 4)
                .requires(ModItems.RAW_AVIAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_AVIAN_MEAT.get()), has(ModItems.RAW_AVIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_avian_to_pieces"));

        // Shapeless recipe crafting Cooked Avian Meat to 4 Cooked Avian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_PIECE.get(), 4)
                .requires(ModItems.COOKED_AVIAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_AVIAN_MEAT.get()), has(ModItems.COOKED_AVIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_avian_to_pieces"));

        // Shapeless recipe crafting 4 Raw Avian Piece to a Raw Avian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_MEAT.get())
                .requires(ModItems.RAW_AVIAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_AVIAN_PIECE.get()), has(ModItems.RAW_AVIAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "pieces_to_raw_avian_meat"));

        // Shapeless recipe crafting 4 Cooked Avian Piece to a Cooked Avian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_MEAT.get())
                .requires(ModItems.COOKED_AVIAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_AVIAN_PIECE.get()), has(ModItems.COOKED_AVIAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "pieces_to_cooked_avian_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Humanoid Flesh to 4 Humanoid Flesh Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_FLESH_PIECE.get(), 4)
                .requires(ModItems.HUMANOID_FLESH.get())
                .unlockedBy(getHasName(ModItems.HUMANOID_FLESH.get()), has(ModItems.HUMANOID_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "humanoid_flesh_to_pieces"));

        // Shapeless recipe crafting a Humanoid Steak to 4 Humanoid Steak Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_STEAK_PIECE.get(), 4)
                .requires(ModItems.HUMANOID_STEAK.get())
                .unlockedBy(getHasName(ModItems.HUMANOID_STEAK.get()), has(ModItems.HUMANOID_STEAK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "humanoid_steak_to_pieces"));

        // Shapeless recipe crafting 4 Humanoid Flesh Piece to a Humanoid Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_FLESH.get())
                .requires(ModItems.HUMANOID_FLESH_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.HUMANOID_FLESH_PIECE.get()), has(ModItems.HUMANOID_FLESH_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "pieces_to_humanoid_flesh"));

        // Shapeless recipe crafting 4 Humanoid Steak Piece to a Humanoid Steak
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_STEAK.get())
                .requires(ModItems.HUMANOID_STEAK_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.HUMANOID_STEAK_PIECE.get()), has(ModItems.HUMANOID_STEAK_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "pieces_to_humanoid_steak"));

        // Shapeless recipe crafting 4 Humanoid Flesh to a Humanoid Flesh Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_FLESH_CHUNK.get())
                .requires(ModItems.HUMANOID_FLESH.get(), 4)
                .unlockedBy(getHasName(ModItems.HUMANOID_FLESH.get()), has(ModItems.HUMANOID_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "humanoid_flesh_to_chunk"));

        // Shapeless recipe crafting 4 Humanoid Steak to a Humanoid Steak Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_STEAK_CHUNK.get())
                .requires(ModItems.HUMANOID_STEAK.get(), 4)
                .unlockedBy(getHasName(ModItems.HUMANOID_STEAK.get()), has(ModItems.HUMANOID_STEAK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "humanoid_steak_to_chunk"));

        // Shapeless recipe crafting a Humanoid Flesh Chunk to 4 Humanoid Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_FLESH.get(), 4)
                .requires(ModItems.HUMANOID_FLESH_CHUNK.get())
                .unlockedBy(getHasName(ModItems.HUMANOID_FLESH_CHUNK.get()), has(ModItems.HUMANOID_FLESH_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_humanoid_flesh"));

        // Shapeless recipe crafting a Humanoid Steak Chunk to 4 Humanoid Steak
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_STEAK.get(), 4)
                .requires(ModItems.HUMANOID_STEAK_CHUNK.get())
                .unlockedBy(getHasName(ModItems.HUMANOID_STEAK_CHUNK.get()), has(ModItems.HUMANOID_STEAK_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_humanoid_steak"));

    }

    private void createCookingRecipe(RecipeOutput recipeOutput, ItemLike input, ItemLike output, float experience, int cookingTime, String group) {
        // Smelting
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(input), RecipeCategory.FOOD, output, experience, cookingTime)
                .group(group)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, getItemName(input) + "_smelting"));

//        // Blasting
//        SimpleCookingRecipeBuilder.blasting(Ingredient.of(input), RecipeCategory.FOOD, output, experience, cookingTime / 2)
//                .group(group)
//                .unlockedBy(getHasName(input), has(input))
//                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, getItemName(input) + "_blasting"));

        // Smoking
        SimpleCookingRecipeBuilder.smoking(Ingredient.of(input), RecipeCategory.FOOD, output, experience, cookingTime / 2)
                .group(group)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, getItemName(input) + "_smoking"));

        // Campfire Cooking
        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(input), RecipeCategory.FOOD, output, experience, cookingTime * 3)
                .group(group)
                .unlockedBy(getHasName(input), has(input))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, getItemName(input) + "_campfire_cooking"));
    }

    protected static void oreSmelting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pRecipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
    }

//    protected static void oreBlasting(RecipeOutput pRecipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
//        oreCooking(pRecipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup, "_from_blasting");
//    }

    private static <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput pRecipeOutput, RecipeSerializer<T> pSerializer, AbstractCookingRecipe.Factory<T> pRecipeFactory, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pSuffix) {
        for (ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pSerializer, pRecipeFactory)
                    .group(pGroup)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(pRecipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, getItemName(itemlike) + pSuffix));
        }
    }
}