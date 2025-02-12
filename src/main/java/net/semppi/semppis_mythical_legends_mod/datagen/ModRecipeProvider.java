package net.semppi.semppis_mythical_legends_mod.datagen;

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
        createCookingRecipe(recipeOutput, ModItems.RAW_BEEF_PIECE.get(), ModItems.COOKED_STEAK_PIECE.get(), 0.1f, 200, "cooked_steak_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_BEEF_CHUNK.get(), ModItems.COOKED_STEAK_CHUNK.get(), 0.25f, 200, "cooked_steak_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_PORKCHOP_PIECE.get(), ModItems.COOKED_PORKCHOP_PIECE.get(), 0.1f, 200, "cooked_porkchop_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_PORKCHOP_CHUNK.get(), ModItems.COOKED_PORKCHOP_CHUNK.get(), 0.25f, 200, "cooked_porkchop_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_MUTTON_PIECE.get(), ModItems.COOKED_MUTTON_PIECE.get(), 0.1f, 200, "cooked_mutton_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_MUTTON_CHUNK.get(), ModItems.COOKED_MUTTON_CHUNK.get(), 0.25f, 200, "cooked_mutton_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_AVIAN_PIECE.get(), ModItems.COOKED_AVIAN_PIECE.get(), 0.1f, 200, "cooked_avian_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_AVIAN_MEAT.get(), ModItems.COOKED_AVIAN_MEAT.get(), 0.25f, 200, "cooked_avian_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_AVIAN_CHUNK.get(), ModItems.COOKED_AVIAN_CHUNK.get(), 0.25f, 200, "cooked_avian_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_BUSHMEAT_PIECE.get(), ModItems.COOKED_BUSHMEAT_PIECE.get(), 0.1f, 200, "cooked_bushmeat_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_BUSHMEAT.get(), ModItems.COOKED_BUSHMEAT.get(), 0.25f, 200, "cooked_bushmeat");
        createCookingRecipe(recipeOutput, ModItems.RAW_BUSHMEAT_CHUNK.get(), ModItems.COOKED_BUSHMEAT_CHUNK.get(), 0.25f, 200, "cooked_bushmeat_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_FISH_PIECE.get(), ModItems.COOKED_FISH_PIECE.get(), 0.1f, 200, "cooked_fish_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_FISH_MEAT.get(), ModItems.COOKED_FISH_MEAT.get(), 0.25f, 200, "cooked_fish_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_FISH_CHUNK.get(), ModItems.COOKED_FISH_CHUNK.get(), 0.25f, 200, "cooked_fish_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_UNGULATE_PIECE.get(), ModItems.COOKED_UNGULATE_PIECE.get(), 0.1f, 200, "cooked_ungulate_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_UNGULATE_MEAT.get(), ModItems.COOKED_UNGULATE_MEAT.get(), 0.25f, 200, "cooked_ungulate_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_UNGULATE_CHUNK.get(), ModItems.COOKED_UNGULATE_CHUNK.get(), 0.25f, 200, "cooked_ungulate_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_AMPHIBIAN_PIECE.get(), ModItems.COOKED_AMPHIBIAN_PIECE.get(), 0.1f, 200, "cooked_amphibian_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_AMPHIBIAN_MEAT.get(), ModItems.COOKED_AMPHIBIAN_MEAT.get(), 0.25f, 200, "cooked_amphibian_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_AMPHIBIAN_CHUNK.get(), ModItems.COOKED_AMPHIBIAN_CHUNK.get(), 0.25f, 200, "cooked_amphibian_chunk");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH_PIECE.get(), ModItems.HUMANOID_STEAK_PIECE.get(), 0.1f, 200, "humanoid_steak_piece");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH.get(), ModItems.HUMANOID_STEAK.get(), 0.25f, 200, "humanoid_steak");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH_CHUNK.get(), ModItems.HUMANOID_STEAK_CHUNK.get(), 0.25f, 200, "humanoid_steak_chunk");
        createCookingRecipe(recipeOutput, Items.BROWN_MUSHROOM, ModItems.COOKED_MUSHROOM.get(), 0.25f, 200, "cooked_mushroom");
        createCookingRecipe(recipeOutput, Items.RED_MUSHROOM, ModItems.COOKED_MUSHROOM.get(), 0.25f, 200, "cooked_mushroom");
        createCookingRecipe(recipeOutput, Items.EGG, ModItems.FRIED_EGG.get(), 0.25f, 200, "fried_egg");
        createCookingRecipe(recipeOutput, Items.TURTLE_EGG, ModItems.FRIED_EGG.get(), 0.25f, 200, "fried_egg");
        createCookingRecipe(recipeOutput, ModItems.PUKIS_EGG_ITEM.get(), ModItems.FRIED_EGG.get(), 0.25f, 200, "fried_egg");
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

        // Shapeless recipe for Butter
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BUTTER.get(), 2)
                .requires(Items.MILK_BUCKET)
                .unlockedBy("has_milk", has(Items.MILK_BUCKET))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "butter"));

        // Shapeless recipe for Oil
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.OIL.get())
                .requires(Items.GLASS_BOTTLE)
                .requires(Items.SUNFLOWER)
                .requires(Items.SUNFLOWER)
                .unlockedBy("has_sunflower", has(Items.SUNFLOWER))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "oil"));

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

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Raw Beef to 4 Raw Beef Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BEEF_PIECE.get(), 4)
                .requires(Items.BEEF)
                .unlockedBy(getHasName(Items.BEEF), has(Items.BEEF))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_beef_to_piece"));

        // Shapeless recipe crafting a Cooked Steak to 4 Cooked Steak Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_STEAK_PIECE.get(), 4)
                .requires(Items.COOKED_BEEF)
                .unlockedBy(getHasName(Items.COOKED_BEEF), has(Items.COOKED_BEEF))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_steak_to_piece"));

        // Shapeless recipe crafting 4 Raw Beef Piece to a Raw Beef
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.BEEF)
                .requires(ModItems.RAW_BEEF_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_BEEF_PIECE.get()), has(ModItems.RAW_BEEF_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_beef"));

        // Shapeless recipe crafting 4 Cooked Steak Piece to a Cooked Steak
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_BEEF)
                .requires(ModItems.COOKED_STEAK_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_STEAK_PIECE.get()), has(ModItems.COOKED_STEAK_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_steak"));

        // Shapeless recipe crafting 4 Raw Beef to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BEEF_CHUNK.get())
                .requires(Items.BEEF, 4)
                .unlockedBy(getHasName(Items.BEEF), has(Items.BEEF))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_beef_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Steak to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_STEAK_CHUNK.get())
                .requires(Items.COOKED_BEEF, 4)
                .unlockedBy(getHasName(Items.COOKED_BEEF), has(Items.COOKED_BEEF))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_steak_to_chunk"));

        // Shapeless recipe crafting Raw Beef Chunk to 4 Raw Beef
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.BEEF, 4)
                .requires(ModItems.RAW_BEEF_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_BEEF_CHUNK.get()), has(ModItems.RAW_BEEF_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_beef"));

        // Shapeless recipe crafting Cooked Steak Chunk to 4 Cooked Steak
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_BEEF, 4)
                .requires(ModItems.COOKED_STEAK_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_STEAK_CHUNK.get()), has(ModItems.COOKED_STEAK_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_steak"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Raw Porkchop to 4 Raw Porkchop Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_PORKCHOP_PIECE.get(), 4)
                .requires(Items.PORKCHOP)
                .unlockedBy(getHasName(Items.PORKCHOP), has(Items.PORKCHOP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_porkchop_to_piece"));

        // Shapeless recipe crafting a Cooked Porkchop to 4 Cooked Porkchop Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_PORKCHOP_PIECE.get(), 4)
                .requires(Items.COOKED_PORKCHOP)
                .unlockedBy(getHasName(Items.COOKED_PORKCHOP), has(Items.COOKED_PORKCHOP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_porkchop_to_piece"));

        // Shapeless recipe crafting 4 Raw Porkchop Piece to a Raw Porkchop
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.PORKCHOP)
                .requires(ModItems.RAW_PORKCHOP_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_PORKCHOP_PIECE.get()), has(ModItems.RAW_PORKCHOP_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_porkchop"));

        // Shapeless recipe crafting 4 Cooked Porkchop Piece to a Cooked Porkchop
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_PORKCHOP)
                .requires(ModItems.COOKED_PORKCHOP_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_PORKCHOP_PIECE.get()), has(ModItems.COOKED_PORKCHOP_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_porkchop"));

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
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_porkchop"));

        // Shapeless recipe crafting a Cooked Porkchop Chunk to 4 Cooked Porkchop
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_PORKCHOP, 4)
                .requires(ModItems.COOKED_PORKCHOP_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_PORKCHOP_CHUNK.get()), has(ModItems.COOKED_PORKCHOP_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_porkchop"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Raw Mutton to 4 Raw Mutton Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_MUTTON_PIECE.get(), 4)
                .requires(Items.MUTTON)
                .unlockedBy(getHasName(Items.MUTTON), has(Items.MUTTON))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_mutton_to_piece"));

        // Shapeless recipe crafting a Cooked Mutton to 4 Cooked Mutton Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_MUTTON_PIECE.get(), 4)
                .requires(Items.COOKED_MUTTON)
                .unlockedBy(getHasName(Items.COOKED_MUTTON), has(Items.COOKED_MUTTON))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_mutton_to_piece"));

        // Shapeless recipe crafting 4 Raw Mutton Piece to a Raw Mutton
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.MUTTON)
                .requires(ModItems.RAW_MUTTON_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_MUTTON_PIECE.get()), has(ModItems.RAW_MUTTON_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_mutton"));

        // Shapeless recipe crafting 4 Cooked Mutton Piece to a Cooked Mutton
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_MUTTON)
                .requires(ModItems.COOKED_MUTTON_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_MUTTON_PIECE.get()), has(ModItems.COOKED_MUTTON_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_mutton"));

        // Shapeless recipe crafting 4 Raw Mutton to a Raw Mutton Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_MUTTON_CHUNK.get())
                .requires(Items.MUTTON, 4)
                .unlockedBy(getHasName(Items.MUTTON), has(Items.MUTTON))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_mutton_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Mutton to a Cooked Mutton Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_MUTTON_CHUNK.get())
                .requires(Items.COOKED_MUTTON, 4)
                .unlockedBy(getHasName(Items.COOKED_MUTTON), has(Items.COOKED_MUTTON))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_mutton_to_chunk"));

        // Shapeless recipe crafting a Raw Mutton Chunk to 4 Raw Mutton
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.MUTTON, 4)
                .requires(ModItems.RAW_MUTTON_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_MUTTON_CHUNK.get()), has(ModItems.RAW_MUTTON_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_mutton"));

        // Shapeless recipe crafting a Cooked Mutton Chunk to 4 Cooked Mutton
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.COOKED_MUTTON, 4)
                .requires(ModItems.COOKED_MUTTON_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_MUTTON_CHUNK.get()), has(ModItems.COOKED_MUTTON_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_mutton"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Raw Chicken to 4 Raw Avian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_PIECE.get(), 4)
                .requires(Items.CHICKEN)
                .unlockedBy(getHasName(Items.CHICKEN), has(Items.CHICKEN))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_chicken_to_piece"));

        // Shapeless recipe crafting a Cooked Chicken to 4 Cooked Avian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_PIECE.get(), 4)
                .requires(Items.COOKED_CHICKEN)
                .unlockedBy(getHasName(Items.COOKED_CHICKEN), has(Items.COOKED_CHICKEN))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_chicken_to_piece"));

        // Shapeless recipe crafting Raw Avian Meat to 4 Raw Avian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_PIECE.get(), 4)
                .requires(ModItems.RAW_AVIAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_AVIAN_MEAT.get()), has(ModItems.RAW_AVIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_avian_to_piece"));

        // Shapeless recipe crafting Cooked Avian Meat to 4 Cooked Avian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_PIECE.get(), 4)
                .requires(ModItems.COOKED_AVIAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_AVIAN_MEAT.get()), has(ModItems.COOKED_AVIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_avian_to_piece"));

        // Shapeless recipe crafting 4 Raw Avian Piece to a Raw Avian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_MEAT.get())
                .requires(ModItems.RAW_AVIAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_AVIAN_PIECE.get()), has(ModItems.RAW_AVIAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_avian_meat"));

        // Shapeless recipe crafting 4 Cooked Avian Piece to a Cooked Avian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_MEAT.get())
                .requires(ModItems.COOKED_AVIAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_AVIAN_PIECE.get()), has(ModItems.COOKED_AVIAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_avian_meat"));

        // Shapeless recipe crafting 4 Raw Chicken to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_CHUNK.get())
                .requires(Items.CHICKEN, 4)
                .unlockedBy(getHasName(Items.CHICKEN), has(Items.CHICKEN))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_chicken_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Chicken to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_CHUNK.get())
                .requires(Items.COOKED_CHICKEN, 4)
                .unlockedBy(getHasName(Items.COOKED_CHICKEN), has(Items.COOKED_CHICKEN))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_chicken_to_chunk"));

        // Shapeless recipe crafting 4 Raw Avian Meat to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_CHUNK.get())
                .requires(ModItems.RAW_AVIAN_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_AVIAN_MEAT.get()), has(ModItems.RAW_AVIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_avian_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Avian Meat to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_CHUNK.get())
                .requires(ModItems.COOKED_AVIAN_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_AVIAN_MEAT.get()), has(ModItems.COOKED_AVIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_avian_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Avian Chunk to 4 Raw Avian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AVIAN_MEAT.get(), 4)
                .requires(ModItems.RAW_AVIAN_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_AVIAN_CHUNK.get()), has(ModItems.RAW_AVIAN_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_avian_meat"));

        // Shapeless recipe crafting a Cooked Avian Chunk to 4 Cooked Avian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AVIAN_MEAT.get(), 4)
                .requires(ModItems.COOKED_AVIAN_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_AVIAN_CHUNK.get()), has(ModItems.COOKED_AVIAN_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_avian_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Raw Rabbit to 4 Raw Bushmeat Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BUSHMEAT_PIECE.get(), 4)
                .requires(Items.RABBIT)
                .unlockedBy(getHasName(Items.RABBIT), has(Items.RABBIT))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_rabbit_to_piece"));

        // Shapeless recipe crafting a Cooked Rabbit to 4 Cooked Bushmeat Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BUSHMEAT_PIECE.get(), 4)
                .requires(Items.COOKED_RABBIT)
                .unlockedBy(getHasName(Items.COOKED_RABBIT), has(Items.COOKED_RABBIT))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_rabbit_to_piece"));

        // Shapeless recipe crafting Raw Bushmeat to 4 Raw Bushmeat Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BUSHMEAT_PIECE.get(), 4)
                .requires(ModItems.RAW_BUSHMEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_BUSHMEAT.get()), has(ModItems.RAW_BUSHMEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_bushmeat_to_piece"));

        // Shapeless recipe crafting Cooked Bushmeat to 4 Cooked Bushmeat Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BUSHMEAT_PIECE.get(), 4)
                .requires(ModItems.COOKED_BUSHMEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_BUSHMEAT.get()), has(ModItems.COOKED_BUSHMEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_bushmeat_to_piece"));

        // Shapeless recipe crafting 4 Raw Bushmeat Piece to a Raw Bushmeat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BUSHMEAT.get())
                .requires(ModItems.RAW_BUSHMEAT_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_BUSHMEAT_PIECE.get()), has(ModItems.RAW_BUSHMEAT_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_bushmeat"));

        // Shapeless recipe crafting 4 Cooked Bushmeat Piece to a Cooked Bushmeat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BUSHMEAT.get())
                .requires(ModItems.COOKED_BUSHMEAT_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_BUSHMEAT_PIECE.get()), has(ModItems.COOKED_BUSHMEAT_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_bushmeat"));

        // Shapeless recipe crafting 4 Raw Rabbit to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BUSHMEAT_CHUNK.get())
                .requires(Items.RABBIT, 4)
                .unlockedBy(getHasName(Items.RABBIT), has(Items.RABBIT))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_rabbit_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Rabbit to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BUSHMEAT_CHUNK.get())
                .requires(Items.COOKED_RABBIT, 4)
                .unlockedBy(getHasName(Items.COOKED_RABBIT), has(Items.COOKED_RABBIT))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_rabbit_to_chunk"));

        // Shapeless recipe crafting 4 Raw Bushmeat to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BUSHMEAT_CHUNK.get())
                .requires(ModItems.RAW_BUSHMEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_BUSHMEAT.get()), has(ModItems.RAW_BUSHMEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_bushmeat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Bushmeat to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BUSHMEAT_CHUNK.get())
                .requires(ModItems.COOKED_BUSHMEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_BUSHMEAT.get()), has(ModItems.COOKED_BUSHMEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_bushmeat_to_chunk"));

        // Shapeless recipe crafting a Raw Bushmeat Chunk to 4 Raw Bushmeat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BUSHMEAT.get(), 4)
                .requires(ModItems.RAW_BUSHMEAT_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_BUSHMEAT_CHUNK.get()), has(ModItems.RAW_BUSHMEAT_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_bushmeat"));

        // Shapeless recipe crafting a Cooked Bushmeat Chunk to 4 Cooked Bushmeat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BUSHMEAT.get(), 4)
                .requires(ModItems.COOKED_BUSHMEAT_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_BUSHMEAT_CHUNK.get()), has(ModItems.COOKED_BUSHMEAT_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_bushmeat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Raw Generic Fish to 4 Raw Fish Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_PIECE.get(), 4)
                .requires(ModItemTagGenerator.RAW_FISH)
                .unlockedBy(getHasName(Items.COD), has(Items.COD))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_fish_to_piece"));

        // Shapeless recipe crafting Cooked Generic Fish to 4 Cooked Fish Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FISH_PIECE.get(), 4)
                .requires(ModItemTagGenerator.COOKED_FISH)
                .unlockedBy(getHasName(Items.COOKED_COD), has(Items.COOKED_COD))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_fish_to_piece"));

        // Shapeless recipe crafting 4 Raw Fish Piece to a Raw Fish Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_MEAT.get())
                .requires(ModItems.RAW_FISH_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_FISH_PIECE.get()), has(ModItems.RAW_FISH_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_fish_meat"));

        // Shapeless recipe crafting 4 Cooked Fish Piece to a Cooked Fish Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FISH_MEAT.get())
                .requires(ModItems.COOKED_FISH_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_FISH_PIECE.get()), has(ModItems.COOKED_FISH_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_fish_meat"));

        // Shapeless recipe crafting 4 Raw Generic Fish to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_CHUNK.get())
                .requires(Ingredient.of(ModItemTagGenerator.RAW_FISH), 4)
                .unlockedBy(getHasName(Items.COD), has(Items.COD))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_fish_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Generic Fish to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FISH_CHUNK.get())
                .requires(Ingredient.of(ModItemTagGenerator.COOKED_FISH), 4)
                .unlockedBy(getHasName(Items.COOKED_COD), has(Items.COOKED_COD))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_fish_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Fish Chunk to 4 Raw Fish Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_MEAT.get(), 4)
                .requires(ModItems.RAW_FISH_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_FISH_CHUNK.get()), has(ModItems.RAW_FISH_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_fish_meat"));

        // Shapeless recipe crafting a Cooked Fish Chunk to 4 Cooked Fish Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FISH_MEAT.get(), 4)
                .requires(ModItems.COOKED_FISH_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_FISH_CHUNK.get()), has(ModItems.COOKED_FISH_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_fish_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Raw Ungulate Meat to 4 Raw Ungulate Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_UNGULATE_PIECE.get(), 4)
                .requires(ModItems.RAW_UNGULATE_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_UNGULATE_MEAT.get()), has(ModItems.RAW_UNGULATE_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_ungulate_meat_to_piece"));

        // Shapeless recipe crafting Cooked Ungulate Meat to 4 Cooked Ungulate Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_UNGULATE_PIECE.get(), 4)
                .requires(ModItems.COOKED_UNGULATE_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_UNGULATE_MEAT.get()), has(ModItems.COOKED_UNGULATE_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_ungulate_meat_to_piece"));

        // Shapeless recipe crafting 4 Raw Ungulate Piece to a Raw Ungulate Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_UNGULATE_MEAT.get())
                .requires(ModItems.RAW_UNGULATE_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_UNGULATE_PIECE.get()), has(ModItems.RAW_UNGULATE_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_ungulate_meat"));

        // Shapeless recipe crafting 4 Cooked Ungulate Piece to a Cooked Ungulate Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_UNGULATE_MEAT.get())
                .requires(ModItems.COOKED_UNGULATE_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_UNGULATE_PIECE.get()), has(ModItems.COOKED_UNGULATE_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_ungulate_meat"));

        // Shapeless recipe crafting 4 Raw Ungulate Meat to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_UNGULATE_CHUNK.get())
                .requires(ModItems.RAW_UNGULATE_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_UNGULATE_MEAT.get()), has(ModItems.RAW_UNGULATE_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_ungulate_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Ungulate Meat to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_UNGULATE_CHUNK.get())
                .requires(ModItems.COOKED_UNGULATE_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_UNGULATE_MEAT.get()), has(ModItems.COOKED_UNGULATE_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_ungulate_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Ungulate Chunk to 4 Raw Ungulate Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_UNGULATE_MEAT.get(), 4)
                .requires(ModItems.RAW_UNGULATE_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_UNGULATE_CHUNK.get()), has(ModItems.RAW_UNGULATE_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_ungulate_meat"));

        // Shapeless recipe crafting a Cooked Ungulate Chunk to 4 Cooked Ungulate Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_UNGULATE_MEAT.get(), 4)
                .requires(ModItems.COOKED_UNGULATE_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_UNGULATE_CHUNK.get()), has(ModItems.COOKED_UNGULATE_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_ungulate_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Raw Amphibian Meat to 4 Raw Amphibian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AMPHIBIAN_PIECE.get(), 4)
                .requires(ModItems.RAW_AMPHIBIAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_AMPHIBIAN_MEAT.get()), has(ModItems.RAW_AMPHIBIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_amphibian_to_piece"));

        // Shapeless recipe crafting Cooked Amphibian Meat to 4 Cooked Amphibian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AMPHIBIAN_PIECE.get(), 4)
                .requires(ModItems.COOKED_AMPHIBIAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_AMPHIBIAN_MEAT.get()), has(ModItems.COOKED_AMPHIBIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_amphibian_to_piece"));

        // Shapeless recipe crafting 4 Raw Amphibian Piece to a Raw Amphibian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AMPHIBIAN_MEAT.get())
                .requires(ModItems.RAW_AMPHIBIAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_AMPHIBIAN_PIECE.get()), has(ModItems.RAW_AMPHIBIAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_amphibian_meat"));

        // Shapeless recipe crafting 4 Cooked Amphibian Piece to a Cooked Amphibian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AMPHIBIAN_MEAT.get())
                .requires(ModItems.COOKED_AMPHIBIAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_AMPHIBIAN_PIECE.get()), has(ModItems.COOKED_AMPHIBIAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_amphibian_meat"));

        // Shapeless recipe crafting 4 Raw Amphibian Meat to a Raw Amphibian Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AMPHIBIAN_CHUNK.get())
                .requires(ModItems.RAW_AMPHIBIAN_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_AMPHIBIAN_MEAT.get()), has(ModItems.RAW_AMPHIBIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_amphibian_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Amphibian Meat to a Cooked Amphibian Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AMPHIBIAN_CHUNK.get())
                .requires(ModItems.COOKED_AMPHIBIAN_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_AMPHIBIAN_MEAT.get()), has(ModItems.COOKED_AMPHIBIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_amphibian_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Amphibian Chunk to 4 Raw Amphibian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AMPHIBIAN_MEAT.get(), 4)
                .requires(ModItems.RAW_AMPHIBIAN_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_AMPHIBIAN_CHUNK.get()), has(ModItems.RAW_AMPHIBIAN_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_amphibian_meat"));

        // Shapeless recipe crafting a Cooked Amphibian Chunk to 4 Cooked Amphibian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AMPHIBIAN_MEAT.get(), 4)
                .requires(ModItems.COOKED_AMPHIBIAN_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_AMPHIBIAN_CHUNK.get()), has(ModItems.COOKED_AMPHIBIAN_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_amphibian_meat"));                                

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Humanoid Flesh to 4 Humanoid Flesh Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_FLESH_PIECE.get(), 4)
                .requires(ModItems.HUMANOID_FLESH.get())
                .unlockedBy(getHasName(ModItems.HUMANOID_FLESH.get()), has(ModItems.HUMANOID_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "humanoid_flesh_to_piece"));

        // Shapeless recipe crafting a Humanoid Steak to 4 Humanoid Steak Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_STEAK_PIECE.get(), 4)
                .requires(ModItems.HUMANOID_STEAK.get())
                .unlockedBy(getHasName(ModItems.HUMANOID_STEAK.get()), has(ModItems.HUMANOID_STEAK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "humanoid_steak_to_piece"));

        // Shapeless recipe crafting 4 Humanoid Flesh Piece to a Humanoid Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_FLESH.get())
                .requires(ModItems.HUMANOID_FLESH_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.HUMANOID_FLESH_PIECE.get()), has(ModItems.HUMANOID_FLESH_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_humanoid_flesh"));

        // Shapeless recipe crafting 4 Humanoid Steak Piece to a Humanoid Steak
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HUMANOID_STEAK.get())
                .requires(ModItems.HUMANOID_STEAK_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.HUMANOID_STEAK_PIECE.get()), has(ModItems.HUMANOID_STEAK_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_humanoid_steak"));

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