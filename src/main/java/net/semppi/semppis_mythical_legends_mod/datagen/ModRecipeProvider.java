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

@SuppressWarnings({ "deprecation", "removal" })
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
        createCookingRecipe(recipeOutput, ModItems.RAW_CRUSTACEAN_PIECE.get(), ModItems.COOKED_CRUSTACEAN_PIECE.get(), 0.1f, 200, "cooked_crustacean_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_CRUSTACEAN_MEAT.get(), ModItems.COOKED_CRUSTACEAN_MEAT.get(), 0.25f, 200, "cooked_crustacean_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_CRUSTACEAN_CHUNK.get(), ModItems.COOKED_CRUSTACEAN_CHUNK.get(), 0.25f, 200, "cooked_crustacean_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_MOLLUSC_PIECE.get(), ModItems.COOKED_MOLLUSC_PIECE.get(), 0.1f, 200, "cooked_mollusc_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_MOLLUSC_MEAT.get(), ModItems.COOKED_MOLLUSC_MEAT.get(), 0.25f, 200, "cooked_mollusc_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_MOLLUSC_CHUNK.get(), ModItems.COOKED_MOLLUSC_CHUNK.get(), 0.25f, 200, "cooked_mollusc_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_AMPHIBIAN_PIECE.get(), ModItems.COOKED_AMPHIBIAN_PIECE.get(), 0.1f, 200, "cooked_amphibian_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_AMPHIBIAN_MEAT.get(), ModItems.COOKED_AMPHIBIAN_MEAT.get(), 0.25f, 200, "cooked_amphibian_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_AMPHIBIAN_CHUNK.get(), ModItems.COOKED_AMPHIBIAN_CHUNK.get(), 0.25f, 200, "cooked_amphibian_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_REPTILIAN_PIECE.get(), ModItems.COOKED_REPTILIAN_PIECE.get(), 0.1f, 200, "cooked_reptilian_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_REPTILIAN_MEAT.get(), ModItems.COOKED_REPTILIAN_MEAT.get(), 0.25f, 200, "cooked_reptilian_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_REPTILIAN_CHUNK.get(), ModItems.COOKED_REPTILIAN_CHUNK.get(), 0.25f, 200, "cooked_reptilian_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_AQUATIC_PIECE.get(), ModItems.COOKED_AQUATIC_PIECE.get(), 0.1f, 200, "cooked_aquatic_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_AQUATIC_MEAT.get(), ModItems.COOKED_AQUATIC_MEAT.get(), 0.25f, 200, "cooked_aquatic_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_AQUATIC_CHUNK.get(), ModItems.COOKED_AQUATIC_CHUNK.get(), 0.25f, 200, "cooked_aquatic_chunk");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH_PIECE.get(), ModItems.HUMANOID_STEAK_PIECE.get(), 0.1f, 200, "humanoid_steak_piece");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH.get(), ModItems.HUMANOID_STEAK.get(), 0.25f, 200, "humanoid_steak");
        createCookingRecipe(recipeOutput, ModItems.HUMANOID_FLESH_CHUNK.get(), ModItems.HUMANOID_STEAK_CHUNK.get(), 0.25f, 200, "humanoid_steak_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_SCUTTLE_PIECE.get(), ModItems.COOKED_SCUTTLE_PIECE.get(), 0.1f, 200, "cooked_scuttle_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_SCUTTLE_MEAT.get(), ModItems.COOKED_SCUTTLE_MEAT.get(), 0.25f, 200, "cooked_scuttle_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_SCUTTLE_CHUNK.get(), ModItems.COOKED_SCUTTLE_CHUNK.get(), 0.25f, 200, "cooked_scuttle_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_BEAST_PIECE.get(), ModItems.COOKED_BEAST_PIECE.get(), 0.1f, 200, "cooked_beast_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_BEAST_MEAT.get(), ModItems.COOKED_BEAST_MEAT.get(), 0.25f, 200, "cooked_beast_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_BEAST_CHUNK.get(), ModItems.COOKED_BEAST_CHUNK.get(), 0.25f, 200, "cooked_beast_chunk");
        createCookingRecipe(recipeOutput, ModItems.RAW_FEY_PIECE.get(), ModItems.COOKED_FEY_PIECE.get(), 0.1f, 200, "cooked_fey_piece");
        createCookingRecipe(recipeOutput, ModItems.RAW_FEY_MEAT.get(), ModItems.COOKED_FEY_MEAT.get(), 0.25f, 200, "cooked_fey_meat");
        createCookingRecipe(recipeOutput, ModItems.RAW_FEY_CHUNK.get(), ModItems.COOKED_FEY_CHUNK.get(), 0.25f, 200, "cooked_fey_chunk");
        createCookingRecipe(recipeOutput, ModItems.DRACONIC_FLESH_PIECE.get(), ModItems.DRACONIC_STEAK_PIECE.get(), 0.1f, 200, "draconic_steak_piece");
        createCookingRecipe(recipeOutput, ModItems.DRACONIC_FLESH.get(), ModItems.DRACONIC_STEAK.get(), 0.25f, 200, "draconic_steak");
        createCookingRecipe(recipeOutput, ModItems.DRACONIC_FLESH_CHUNK.get(), ModItems.DRACONIC_STEAK_CHUNK.get(), 0.25f, 200, "draconic_steak_chunk");
        createCookingRecipe(recipeOutput, ModItems.ABERRANT_FLESH_PIECE.get(), ModItems.COOKED_ABERRANT_PIECE.get(), 0.1f, 200, "cooked_aberrant_piece");
        createCookingRecipe(recipeOutput, ModItems.ABERRANT_FLESH.get(), ModItems.COOKED_ABERRANT_MEAT.get(), 0.25f, 200, "cooked_aberrant_meat");
        createCookingRecipe(recipeOutput, ModItems.ABERRANT_FLESH_CHUNK.get(), ModItems.COOKED_ABERRANT_CHUNK.get(), 0.25f, 200, "cooked_aberrant_chunk");
        createCookingRecipe(recipeOutput, ModItems.FIEND_FLESH_PIECE.get(), ModItems.COOKED_FIEND_PIECE.get(), 0.1f, 200, "cooked_fiend_piece");
        createCookingRecipe(recipeOutput, ModItems.FIEND_FLESH.get(), ModItems.COOKED_FIEND_MEAT.get(), 0.25f, 200, "cooked_fiend_meat");
        createCookingRecipe(recipeOutput, ModItems.FIEND_FLESH_CHUNK.get(), ModItems.COOKED_FIEND_CHUNK.get(), 0.25f, 200, "cooked_fiend_chunk");

        createCookingRecipe(recipeOutput, Items.BROWN_MUSHROOM, ModItems.COOKED_MUSHROOM.get(), 0.25f, 200, "cooked_mushroom");
        createCookingRecipe(recipeOutput, Items.RED_MUSHROOM, ModItems.COOKED_MUSHROOM.get(), 0.25f, 200, "cooked_mushroom");
        createCookingRecipe(recipeOutput, Items.EGG, ModItems.FRIED_EGG.get(), 0.25f, 200, "fried_egg");
        createCookingRecipe(recipeOutput, Items.TURTLE_EGG, ModItems.FRIED_EGG.get(), 0.25f, 200, "fried_egg");
        createCookingRecipe(recipeOutput, ModItems.PUKIS_EGG_ITEM.get(), ModItems.FRIED_EGG.get(), 0.25f, 200, "fried_egg");
        createCookingRecipe(recipeOutput, ModItems.FISHY_KELP_TREAT.get(), ModItems.COOKED_FISHY_KELP_TREAT.get(), 0.25f, 200, "cooked_fishy_kelp_treat");
        createCookingRecipe(recipeOutput, ModItems.VEGGIE_KELP_TREAT.get(), ModItems.COOKED_VEGGIE_KELP_TREAT.get(), 0.25f, 200, "cooked_veggie_kelp_treat");

        // Shaped recipe for Care Brush
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CARE_BRUSH.get())
                .pattern("r")
                .pattern("c")
                .pattern("s")
                .define('r', Items.RABBIT_HIDE)
                .define('c', Items.COPPER_INGOT)
                .define('s', Items.STICK)
                .unlockedBy(getHasName(Items.RABBIT_HIDE), has(Items.RABBIT_HIDE))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "care_brush"));

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
                .requires(Items.COOKED_COD)
                .requires(Items.BOWL)
                .requires(Items.SEAGRASS)
                .requires(Items.BAKED_POTATO)
                .unlockedBy("has_cod", has(Items.COD))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cod_soup"));

        // Shapeless recipe for Baked Cheesy Fish
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BAKED_CHEESY_FISH.get())
                .requires(Items.COOKED_COD)
                .requires(Items.BOWL)
                .requires(ModItems.CHEESE.get())
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

        // Shapeless recipe for Grass Bundle
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GRASS_BUNDLE.get())
                .requires(Items.SHORT_GRASS)
                .requires(Items.SHORT_GRASS)
                .requires(Items.SHORT_GRASS)
                .requires(Items.SHORT_GRASS)
                .requires(Items.SHORT_GRASS)
                .requires(Items.SHORT_GRASS)
                .requires(Items.SHORT_GRASS)
                .requires(Items.SHORT_GRASS)
                .requires(Items.SHORT_GRASS)
                .unlockedBy("has_short_grass", has(Items.SHORT_GRASS))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "grass_bundle"));

        // Shapeless recipe for Seagrass Bundle
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.SEAGRASS_BUNDLE.get())
                .requires(Items.SEAGRASS)
                .requires(Items.SEAGRASS)
                .requires(Items.SEAGRASS)
                .requires(Items.SEAGRASS)
                .requires(Items.SEAGRASS)
                .requires(Items.SEAGRASS)
                .requires(Items.SEAGRASS)
                .requires(Items.SEAGRASS)
                .requires(Items.SEAGRASS)
                .unlockedBy("has_seagrass", has(Items.SEAGRASS))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "seagrass_bundle"));

        // Shapeless recipe for Ricotta Cheese
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHEESE.get(), 6)
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

        // Shapeless recipe for Glow Berry Jam
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GLOW_BERRY_JAM.get())
                .requires(Items.GLOW_BERRIES)
                .requires(Items.GLOW_BERRIES)
                .requires(Items.GLOW_BERRIES)
                .requires(Items.SUGAR)
                .unlockedBy("has_glow_berries", has(Items.GLOW_BERRIES))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "glow_berry_jam"));

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
                .requires(ModItems.CHEESE.get())
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

        // Shapeless recipe for Glow Berry Jam on Bread
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GLOW_BERRY_JAM_ON_BREAD.get(), 2)
                .requires(ModItems.GLOW_BERRY_JAM.get())
                .requires(Items.BREAD)
                .unlockedBy("has_glow_berry_jam", has(ModItems.GLOW_BERRY_JAM.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "glow_berry_jam_on_bread"));

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
                .requires(ModItems.CHEESE.get())
                .requires(Items.WHEAT_SEEDS)
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .unlockedBy("has_ricotta_cheese", has(ModItems.CHEESE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "spanakopita"));

        // Shapeless recipe for Pork and Rabbit Pie
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.PORK_AND_RABBIT_PIE.get())
                .requires(Items.WATER_BUCKET)
                .requires(Items.WHEAT)
                .requires(Items.WHEAT)
                .requires(Items.EGG)
                .requires(ModItems.BUTTER.get())
                .requires(ModItems.EDIBLE_LEAF.get())
                .requires(Items.COOKED_PORKCHOP)
                .requires(Items.COOKED_RABBIT)
                .unlockedBy("has_cooked_porkchop", has(Items.COOKED_PORKCHOP))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "pork_and_rabbit_pie"));

        // Shapeless recipe for Honeyed Meat Pie
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.HONEYED_MEAT_PIE.get(), 4)
                .requires(Items.WATER_BUCKET)
                .requires(Items.COOKED_BEEF)
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
                .requires(ModItems.CHEESE.get())
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
                .requires(ModItems.CHEESE.get())
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
                .requires(ModItemTagGenerator.RAW_COMMON_FISH)
                .unlockedBy(getHasName(Items.COD), has(Items.COD))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_fish_to_piece"));

        // Shapeless recipe crafting Tropical Fish to 1 Raw Fish Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_PIECE.get(), 1)
                .requires(Items.TROPICAL_FISH)
                .unlockedBy(getHasName(Items.TROPICAL_FISH), has(Items.TROPICAL_FISH))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "tropical_fish_to_piece"));

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

        // Shapeless recipe crafting 4 Raw Tropical Fish to a Raw Fish Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_MEAT.get())
                .requires(Items.TROPICAL_FISH, 4)
                .unlockedBy(getHasName(Items.TROPICAL_FISH), has(Items.TROPICAL_FISH))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "tropical_fish_to_raw_fish_meat"));

        // Shapeless recipe crafting 4 Raw Generic Fish to a Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FISH_CHUNK.get())
                .requires(Ingredient.of(ModItemTagGenerator.RAW_COMMON_FISH), 4)
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

        // Shapeless recipe crafting Raw Crustacean Meat to 4 Raw Crustacean Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_CRUSTACEAN_PIECE.get(), 4)
                .requires(ModItems.RAW_CRUSTACEAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_CRUSTACEAN_MEAT.get()), has(ModItems.RAW_CRUSTACEAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_crustacean_to_piece"));

        // Shapeless recipe crafting Cooked Crustacean Meat to 4 Cooked Crustacean Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_CRUSTACEAN_PIECE.get(), 4)
                .requires(ModItems.COOKED_CRUSTACEAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_CRUSTACEAN_MEAT.get()), has(ModItems.COOKED_CRUSTACEAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_crustacean_to_piece"));

        // Shapeless recipe crafting 4 Raw Crustacean Piece to a Raw Crustacean Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_CRUSTACEAN_MEAT.get())
                .requires(ModItems.RAW_CRUSTACEAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_CRUSTACEAN_PIECE.get()), has(ModItems.RAW_CRUSTACEAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_crustacean_meat"));

        // Shapeless recipe crafting 4 Cooked Crustacean Piece to a Cooked Crustacean Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_CRUSTACEAN_MEAT.get())
                .requires(ModItems.COOKED_CRUSTACEAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_CRUSTACEAN_PIECE.get()), has(ModItems.COOKED_CRUSTACEAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_crustacean_meat"));

        // Shapeless recipe crafting 4 Raw Crustacean Meat to a Raw Crustacean Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_CRUSTACEAN_CHUNK.get())
                .requires(ModItems.RAW_CRUSTACEAN_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_CRUSTACEAN_MEAT.get()), has(ModItems.RAW_CRUSTACEAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_crustacean_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Crustacean Meat to a Cooked Crustacean Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_CRUSTACEAN_CHUNK.get())
                .requires(ModItems.COOKED_CRUSTACEAN_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_CRUSTACEAN_MEAT.get()), has(ModItems.COOKED_CRUSTACEAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_crustacean_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Crustacean Chunk to 4 Raw Crustacean Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_CRUSTACEAN_MEAT.get(), 4)
                .requires(ModItems.RAW_CRUSTACEAN_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_CRUSTACEAN_CHUNK.get()), has(ModItems.RAW_CRUSTACEAN_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_crustacean_meat"));

        // Shapeless recipe crafting a Cooked Crustacean Chunk to 4 Cooked Crustacean Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_CRUSTACEAN_MEAT.get(), 4)
                .requires(ModItems.COOKED_CRUSTACEAN_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_CRUSTACEAN_CHUNK.get()), has(ModItems.COOKED_CRUSTACEAN_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_crustacean_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Raw Mollusc Meat to 4 Raw Mollusc Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_MOLLUSC_PIECE.get(), 4)
                .requires(ModItems.RAW_MOLLUSC_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_MOLLUSC_MEAT.get()), has(ModItems.RAW_MOLLUSC_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_mollusc_to_piece"));

        // Shapeless recipe crafting Cooked Mollusc Meat to 4 Cooked Mollusc Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_MOLLUSC_PIECE.get(), 4)
                .requires(ModItems.COOKED_MOLLUSC_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_MOLLUSC_MEAT.get()), has(ModItems.COOKED_MOLLUSC_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_mollusc_to_piece"));

        // Shapeless recipe crafting 4 Raw Mollusc Piece to a Raw Mollusc Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_MOLLUSC_MEAT.get())
                .requires(ModItems.RAW_MOLLUSC_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_MOLLUSC_PIECE.get()), has(ModItems.RAW_MOLLUSC_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_mollusc_meat"));

        // Shapeless recipe crafting 4 Cooked Mollusc Piece to a Cooked Mollusc Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_MOLLUSC_MEAT.get())
                .requires(ModItems.COOKED_MOLLUSC_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_MOLLUSC_PIECE.get()), has(ModItems.COOKED_MOLLUSC_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_mollusc_meat"));

        // Shapeless recipe crafting 4 Raw Mollusc Meat to a Raw Mollusc Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_MOLLUSC_CHUNK.get())
                .requires(ModItems.RAW_MOLLUSC_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_MOLLUSC_MEAT.get()), has(ModItems.RAW_MOLLUSC_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_mollusc_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Mollusc Meat to a Cooked Mollusc Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_MOLLUSC_CHUNK.get())
                .requires(ModItems.COOKED_MOLLUSC_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_MOLLUSC_MEAT.get()), has(ModItems.COOKED_MOLLUSC_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_mollusc_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Mollusc Chunk to 4 Raw Mollusc Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_MOLLUSC_MEAT.get(), 4)
                .requires(ModItems.RAW_MOLLUSC_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_MOLLUSC_CHUNK.get()), has(ModItems.RAW_MOLLUSC_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_mollusc_meat"));

        // Shapeless recipe crafting a Cooked Mollusc Chunk to 4 Cooked Mollusc Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_MOLLUSC_MEAT.get(), 4)
                .requires(ModItems.COOKED_MOLLUSC_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_MOLLUSC_CHUNK.get()), has(ModItems.COOKED_MOLLUSC_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_mollusc_meat"));

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

        // Shapeless recipe crafting Raw Reptilian Meat to 4 Raw Reptilian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_REPTILIAN_PIECE.get(), 4)
                .requires(ModItems.RAW_REPTILIAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_REPTILIAN_MEAT.get()), has(ModItems.RAW_REPTILIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_reptilian_to_piece"));

        // Shapeless recipe crafting Cooked Reptilian Meat to 4 Cooked Reptilian Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_REPTILIAN_PIECE.get(), 4)
                .requires(ModItems.COOKED_REPTILIAN_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_REPTILIAN_MEAT.get()), has(ModItems.COOKED_REPTILIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_reptilian_to_piece"));

        // Shapeless recipe crafting 4 Raw Reptilian Piece to a Raw Reptilian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_REPTILIAN_MEAT.get())
                .requires(ModItems.RAW_REPTILIAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_REPTILIAN_PIECE.get()), has(ModItems.RAW_REPTILIAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_reptilian_meat"));

        // Shapeless recipe crafting 4 Cooked Reptilian Piece to a Cooked Reptilian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_REPTILIAN_MEAT.get())
                .requires(ModItems.COOKED_REPTILIAN_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_REPTILIAN_PIECE.get()), has(ModItems.COOKED_REPTILIAN_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_reptilian_meat"));

        // Shapeless recipe crafting 4 Raw Reptilian Meat to a Raw Reptilian Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_REPTILIAN_CHUNK.get())
                .requires(ModItems.RAW_REPTILIAN_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_REPTILIAN_MEAT.get()), has(ModItems.RAW_REPTILIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_reptilian_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Reptilian Meat to a Cooked Reptilian Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_REPTILIAN_CHUNK.get())
                .requires(ModItems.COOKED_REPTILIAN_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_REPTILIAN_MEAT.get()), has(ModItems.COOKED_REPTILIAN_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_reptilian_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Reptilian Chunk to 4 Raw Reptilian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_REPTILIAN_MEAT.get(), 4)
                .requires(ModItems.RAW_REPTILIAN_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_REPTILIAN_CHUNK.get()), has(ModItems.RAW_REPTILIAN_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_reptilian_meat"));

        // Shapeless recipe crafting a Cooked Reptilian Chunk to 4 Cooked Reptilian Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_REPTILIAN_MEAT.get(), 4)
                .requires(ModItems.COOKED_REPTILIAN_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_REPTILIAN_CHUNK.get()), has(ModItems.COOKED_REPTILIAN_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_reptilian_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Raw Aquatic Meat to 4 Raw Aquatic Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AQUATIC_PIECE.get(), 4)
                .requires(ModItems.RAW_AQUATIC_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_AQUATIC_MEAT.get()), has(ModItems.RAW_AQUATIC_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_aquatic_meat_to_piece"));

        // Shapeless recipe crafting Cooked Aquatic Meat to 4 Cooked Aquatic Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AQUATIC_PIECE.get(), 4)
                .requires(ModItems.COOKED_AQUATIC_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_AQUATIC_MEAT.get()), has(ModItems.COOKED_AQUATIC_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_aquatic_meat_to_piece"));

        // Shapeless recipe crafting 4 Raw Aquatic Piece to a Raw Aquatic Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AQUATIC_MEAT.get())
                .requires(ModItems.RAW_AQUATIC_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_AQUATIC_PIECE.get()), has(ModItems.RAW_AQUATIC_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_aquatic_meat"));

        // Shapeless recipe crafting 4 Cooked Aquatic Piece to a Cooked Aquatic Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AQUATIC_MEAT.get())
                .requires(ModItems.COOKED_AQUATIC_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_AQUATIC_PIECE.get()), has(ModItems.COOKED_AQUATIC_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_aquatic_meat"));

        // Shapeless recipe crafting 4 Raw Aquatic Meat to a Cooked Aquatic Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AQUATIC_CHUNK.get())
                .requires(ModItems.RAW_AQUATIC_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_AQUATIC_MEAT.get()), has(ModItems.RAW_AQUATIC_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_aquatic_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Aquatic Meat to a Cooked Aquatic Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AQUATIC_CHUNK.get())
                .requires(ModItems.COOKED_AQUATIC_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_AQUATIC_MEAT.get()), has(ModItems.COOKED_AQUATIC_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_aquatic_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Aquatic Chunk to 4 Raw Aquatic Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_AQUATIC_MEAT.get(), 4)
                .requires(ModItems.RAW_AQUATIC_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_AQUATIC_CHUNK.get()), has(ModItems.RAW_AQUATIC_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_aquatic_meat"));

        // Shapeless recipe crafting a Cooked Aquatic Chunk to 4 Cooked Aquatic Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_AQUATIC_MEAT.get(), 4)
                .requires(ModItems.COOKED_AQUATIC_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_AQUATIC_CHUNK.get()), has(ModItems.COOKED_AQUATIC_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_aquatic_meat"));

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

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Rotten Flesh to 4 Rotten Flesh Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.ROTTEN_FLESH_PIECE.get(), 4)
                .requires(Items.ROTTEN_FLESH)
                .unlockedBy(getHasName(Items.ROTTEN_FLESH), has(Items.ROTTEN_FLESH))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "rotten_flesh_to_piece"));

        // Shapeless recipe crafting 4 Rotten Flesh Piece to Rotten Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.ROTTEN_FLESH)
                .requires(ModItems.ROTTEN_FLESH_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.ROTTEN_FLESH_PIECE.get()), has(ModItems.ROTTEN_FLESH_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_rotten_flesh"));

        // Shapeless recipe crafting 4 Rotten Flesh to a Rotten Flesh Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.ROTTEN_FLESH_CHUNK.get())
                .requires(Items.ROTTEN_FLESH, 4)
                .unlockedBy(getHasName(Items.ROTTEN_FLESH), has(Items.ROTTEN_FLESH))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "rotten_flesh_to_chunk"));

        // Shapeless recipe crafting a Rotten Flesh Chunk to 4 Rotten Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, Items.ROTTEN_FLESH, 4)
                .requires(ModItems.ROTTEN_FLESH_CHUNK.get())
                .unlockedBy(getHasName(ModItems.ROTTEN_FLESH_CHUNK.get()), has(ModItems.ROTTEN_FLESH_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_rotten_flesh"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Raw Scuttle Meat to 4 Raw Scuttle Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_SCUTTLE_PIECE.get(), 4)
                .requires(ModItems.RAW_SCUTTLE_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_SCUTTLE_MEAT.get()), has(ModItems.RAW_SCUTTLE_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_scuttle_to_piece"));

        // Shapeless recipe crafting Cooked Scuttle Meat to 4 Cooked Scuttle Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_SCUTTLE_PIECE.get(), 4)
                .requires(ModItems.COOKED_SCUTTLE_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_SCUTTLE_MEAT.get()), has(ModItems.COOKED_SCUTTLE_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_scuttle_to_piece"));

        // Shapeless recipe crafting 4 Raw Scuttle Piece to a Raw Scuttle Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_SCUTTLE_MEAT.get())
                .requires(ModItems.RAW_SCUTTLE_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_SCUTTLE_PIECE.get()), has(ModItems.RAW_SCUTTLE_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_scuttle_meat"));

        // Shapeless recipe crafting 4 Cooked Scuttle Piece to a Cooked Scuttle Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_SCUTTLE_MEAT.get())
                .requires(ModItems.COOKED_SCUTTLE_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_SCUTTLE_PIECE.get()), has(ModItems.COOKED_SCUTTLE_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_scuttle_meat"));

        // Shapeless recipe crafting 4 Raw Scuttle Meat to a Raw Scuttle Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_SCUTTLE_CHUNK.get())
                .requires(ModItems.RAW_SCUTTLE_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_SCUTTLE_MEAT.get()), has(ModItems.RAW_SCUTTLE_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_scuttle_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Scuttle Meat to a Cooked Scuttle Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_SCUTTLE_CHUNK.get())
                .requires(ModItems.COOKED_SCUTTLE_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_SCUTTLE_MEAT.get()), has(ModItems.COOKED_SCUTTLE_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_scuttle_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Scuttle Chunk to 4 Raw Scuttle Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_SCUTTLE_MEAT.get(), 4)
                .requires(ModItems.RAW_SCUTTLE_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_SCUTTLE_CHUNK.get()), has(ModItems.RAW_SCUTTLE_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_scuttle_meat"));

        // Shapeless recipe crafting a Cooked Scuttle Chunk to 4 Cooked Scuttle Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_SCUTTLE_MEAT.get(), 4)
                .requires(ModItems.COOKED_SCUTTLE_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_SCUTTLE_CHUNK.get()), has(ModItems.COOKED_SCUTTLE_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_scuttle_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Raw Beast Meat to 4 Raw Beast Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BEAST_PIECE.get(), 4)
                .requires(ModItems.RAW_BEAST_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_BEAST_MEAT.get()), has(ModItems.RAW_BEAST_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_beast_to_piece"));

        // Shapeless recipe crafting Cooked Beast Meat to 4 Cooked Beast Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BEAST_PIECE.get(), 4)
                .requires(ModItems.COOKED_BEAST_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_BEAST_MEAT.get()), has(ModItems.COOKED_BEAST_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_beast_to_piece"));

        // Shapeless recipe crafting 4 Raw Beast Piece to a Raw Beast Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BEAST_MEAT.get())
                .requires(ModItems.RAW_BEAST_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_BEAST_PIECE.get()), has(ModItems.RAW_BEAST_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_beast_meat"));

        // Shapeless recipe crafting 4 Cooked Beast Piece to a Cooked Beast Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BEAST_MEAT.get())
                .requires(ModItems.COOKED_BEAST_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_BEAST_PIECE.get()), has(ModItems.COOKED_BEAST_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_beast_meat"));

        // Shapeless recipe crafting 4 Raw Beast Meat to a Raw Beast Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BEAST_CHUNK.get())
                .requires(ModItems.RAW_BEAST_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_BEAST_MEAT.get()), has(ModItems.RAW_BEAST_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_beast_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Beast Meat to a Cooked Beast Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BEAST_CHUNK.get())
                .requires(ModItems.COOKED_BEAST_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_BEAST_MEAT.get()), has(ModItems.COOKED_BEAST_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_beast_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Beast Chunk to 4 Raw Beast Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_BEAST_MEAT.get(), 4)
                .requires(ModItems.RAW_BEAST_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_BEAST_CHUNK.get()), has(ModItems.RAW_BEAST_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_beast_meat"));

        // Shapeless recipe crafting a Cooked Beast Chunk to 4 Cooked Beast Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_BEAST_MEAT.get(), 4)
                .requires(ModItems.COOKED_BEAST_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_BEAST_CHUNK.get()), has(ModItems.COOKED_BEAST_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_beast_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Raw Fey Meat to 4 Raw Fey Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FEY_PIECE.get(), 4)
                .requires(ModItems.RAW_FEY_MEAT.get())
                .unlockedBy(getHasName(ModItems.RAW_FEY_MEAT.get()), has(ModItems.RAW_FEY_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_fey_to_piece"));

        // Shapeless recipe crafting Cooked Fey Meat to 4 Cooked Fey Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FEY_PIECE.get(), 4)
                .requires(ModItems.COOKED_FEY_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_FEY_MEAT.get()), has(ModItems.COOKED_FEY_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_fey_to_piece"));

        // Shapeless recipe crafting 4 Raw Fey Piece to a Raw Fey Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FEY_MEAT.get())
                .requires(ModItems.RAW_FEY_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_FEY_PIECE.get()), has(ModItems.RAW_FEY_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_raw_fey_meat"));

        // Shapeless recipe crafting 4 Cooked Fey Piece to a Cooked Fey Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FEY_MEAT.get())
                .requires(ModItems.COOKED_FEY_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_FEY_PIECE.get()), has(ModItems.COOKED_FEY_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_fey_meat"));

        // Shapeless recipe crafting 4 Raw Fey Meat to a Raw Fey Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FEY_CHUNK.get())
                .requires(ModItems.RAW_FEY_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.RAW_FEY_MEAT.get()), has(ModItems.RAW_FEY_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "raw_fey_meat_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Fey Meat to a Cooked Fey Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FEY_CHUNK.get())
                .requires(ModItems.COOKED_FEY_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_FEY_MEAT.get()), has(ModItems.COOKED_FEY_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_fey_meat_to_chunk"));

        // Shapeless recipe crafting a Raw Fey Chunk to 4 Raw Fey Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.RAW_FEY_MEAT.get(), 4)
                .requires(ModItems.RAW_FEY_CHUNK.get())
                .unlockedBy(getHasName(ModItems.RAW_FEY_CHUNK.get()), has(ModItems.RAW_FEY_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_raw_fey_meat"));

        // Shapeless recipe crafting a Cooked Fey Chunk to 4 Cooked Fey Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FEY_MEAT.get(), 4)
                .requires(ModItems.COOKED_FEY_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_FEY_CHUNK.get()), has(ModItems.COOKED_FEY_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_fey_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting a Draconic Flesh to 4 Draconic Flesh Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.DRACONIC_FLESH_PIECE.get(), 4)
                .requires(ModItems.DRACONIC_FLESH.get())
                .unlockedBy(getHasName(ModItems.DRACONIC_FLESH.get()), has(ModItems.DRACONIC_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "draconic_flesh_to_piece"));

        // Shapeless recipe crafting a Draconic Steak to 4 Draconic Steak Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.DRACONIC_STEAK_PIECE.get(), 4)
                .requires(ModItems.DRACONIC_STEAK.get())
                .unlockedBy(getHasName(ModItems.DRACONIC_STEAK.get()), has(ModItems.DRACONIC_STEAK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "draconic_steak_to_piece"));

        // Shapeless recipe crafting 4 Draconic Flesh Piece to a Draconic Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.DRACONIC_FLESH.get())
                .requires(ModItems.DRACONIC_FLESH_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.DRACONIC_FLESH_PIECE.get()), has(ModItems.DRACONIC_FLESH_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_draconic_flesh"));

        // Shapeless recipe crafting 4 Draconic Steak Piece to a Draconic Steak
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.DRACONIC_STEAK.get())
                .requires(ModItems.DRACONIC_STEAK_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.DRACONIC_STEAK_PIECE.get()), has(ModItems.DRACONIC_STEAK_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_draconic_steak"));

        // Shapeless recipe crafting 4 Draconic Flesh to a Draconic Flesh Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.DRACONIC_FLESH_CHUNK.get())
                .requires(ModItems.DRACONIC_FLESH.get(), 4)
                .unlockedBy(getHasName(ModItems.DRACONIC_FLESH.get()), has(ModItems.DRACONIC_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "draconic_flesh_to_chunk"));

        // Shapeless recipe crafting 4 Draconic Steak to a Draconic Steak Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.DRACONIC_STEAK_CHUNK.get())
                .requires(ModItems.DRACONIC_STEAK.get(), 4)
                .unlockedBy(getHasName(ModItems.DRACONIC_STEAK.get()), has(ModItems.DRACONIC_STEAK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "draconic_steak_to_chunk"));

        // Shapeless recipe crafting a Draconic Flesh Chunk to 4 Draconic Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.DRACONIC_FLESH.get(), 4)
                .requires(ModItems.DRACONIC_FLESH_CHUNK.get())
                .unlockedBy(getHasName(ModItems.DRACONIC_FLESH_CHUNK.get()), has(ModItems.DRACONIC_FLESH_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_draconic_flesh"));

        // Shapeless recipe crafting a Draconic Steak Chunk to 4 Draconic Steak
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.DRACONIC_STEAK.get(), 4)
                .requires(ModItems.DRACONIC_STEAK_CHUNK.get())
                .unlockedBy(getHasName(ModItems.DRACONIC_STEAK_CHUNK.get()), has(ModItems.DRACONIC_STEAK_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_draconic_steak"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Aberrant Flesh to 4 Aberrant Flesh Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.ABERRANT_FLESH_PIECE.get(), 4)
                .requires(ModItems.ABERRANT_FLESH.get())
                .unlockedBy(getHasName(ModItems.ABERRANT_FLESH.get()), has(ModItems.ABERRANT_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "aberrant_flesh_to_piece"));

        // Shapeless recipe crafting Cooked Aberrant Meat to 4 Cooked Aberrant Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_ABERRANT_PIECE.get(), 4)
                .requires(ModItems.COOKED_ABERRANT_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_ABERRANT_MEAT.get()), has(ModItems.COOKED_ABERRANT_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_aberrant_meat_to_piece"));

        // Shapeless recipe crafting 4 Aberrant Flesh Piece to Aberrant Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.ABERRANT_FLESH.get())
                .requires(ModItems.ABERRANT_FLESH_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.ABERRANT_FLESH_PIECE.get()), has(ModItems.ABERRANT_FLESH_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_aberrant_flesh"));

        // Shapeless recipe crafting 4 Cooked Aberrant Piece to Cooked Aberrant Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_ABERRANT_MEAT.get())
                .requires(ModItems.COOKED_ABERRANT_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_ABERRANT_PIECE.get()), has(ModItems.COOKED_ABERRANT_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_aberrant_meat"));

        // Shapeless recipe crafting 4 Aberrant Flesh to Aberrant Flesh Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.ABERRANT_FLESH_CHUNK.get())
                .requires(ModItems.ABERRANT_FLESH.get(), 4)
                .unlockedBy(getHasName(ModItems.ABERRANT_FLESH.get()), has(ModItems.ABERRANT_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "aberrant_flesh_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Aberrant Meat to Cooked Aberrant Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_ABERRANT_CHUNK.get())
                .requires(ModItems.COOKED_ABERRANT_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_ABERRANT_MEAT.get()), has(ModItems.COOKED_ABERRANT_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_aberrant_meat_to_chunk"));

        // Shapeless recipe crafting Aberrant Flesh Chunk to 4 Aberrant Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.ABERRANT_FLESH.get(), 4)
                .requires(ModItems.ABERRANT_FLESH_CHUNK.get())
                .unlockedBy(getHasName(ModItems.ABERRANT_FLESH_CHUNK.get()), has(ModItems.ABERRANT_FLESH_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_aberrant_flesh"));

        // Shapeless recipe crafting Cooked Aberrant Chunk to 4 Cooked Aberrant Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_ABERRANT_MEAT.get(), 4)
                .requires(ModItems.COOKED_ABERRANT_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_ABERRANT_CHUNK.get()), has(ModItems.COOKED_ABERRANT_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_aberrant_meat"));

        /////////////////////////////////////////////////////////////////////////////////

        // Shapeless recipe crafting Fiend Flesh to 4 Fiend Flesh Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FIEND_FLESH_PIECE.get(), 4)
                .requires(ModItems.FIEND_FLESH.get())
                .unlockedBy(getHasName(ModItems.FIEND_FLESH.get()), has(ModItems.FIEND_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "fiend_flesh_to_piece"));

        // Shapeless recipe crafting Cooked Fiend Meat to 4 Cooked Fiend Piece
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FIEND_PIECE.get(), 4)
                .requires(ModItems.COOKED_FIEND_MEAT.get())
                .unlockedBy(getHasName(ModItems.COOKED_FIEND_MEAT.get()), has(ModItems.COOKED_FIEND_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_fiend_meat_to_piece"));

        // Shapeless recipe crafting 4 Fiend Flesh Piece to Fiend Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FIEND_FLESH.get())
                .requires(ModItems.FIEND_FLESH_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.FIEND_FLESH_PIECE.get()), has(ModItems.FIEND_FLESH_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_fiend_flesh"));

        // Shapeless recipe crafting 4 Cooked Fiend Piece to Cooked Fiend Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FIEND_MEAT.get())
                .requires(ModItems.COOKED_FIEND_PIECE.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_FIEND_PIECE.get()), has(ModItems.COOKED_FIEND_PIECE.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "piece_to_cooked_fiend_meat"));

        // Shapeless recipe crafting 4 Fiend Flesh to Fiend Flesh Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FIEND_FLESH_CHUNK.get())
                .requires(ModItems.FIEND_FLESH.get(), 4)
                .unlockedBy(getHasName(ModItems.FIEND_FLESH.get()), has(ModItems.FIEND_FLESH.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "fiend_flesh_to_chunk"));

        // Shapeless recipe crafting 4 Cooked Fiend Meat to Cooked Fiend Chunk
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FIEND_CHUNK.get())
                .requires(ModItems.COOKED_FIEND_MEAT.get(), 4)
                .unlockedBy(getHasName(ModItems.COOKED_FIEND_MEAT.get()), has(ModItems.COOKED_FIEND_MEAT.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "cooked_fiend_meat_to_chunk"));

        // Shapeless recipe crafting Fiend Flesh Chunk to 4 Fiend Flesh
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.FIEND_FLESH.get(), 4)
                .requires(ModItems.FIEND_FLESH_CHUNK.get())
                .unlockedBy(getHasName(ModItems.FIEND_FLESH_CHUNK.get()), has(ModItems.FIEND_FLESH_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_fiend_flesh"));

        // Shapeless recipe crafting Cooked Fiend Chunk to 4 Cooked Fiend Meat
        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COOKED_FIEND_MEAT.get(), 4)
                .requires(ModItems.COOKED_FIEND_CHUNK.get())
                .unlockedBy(getHasName(ModItems.COOKED_FIEND_CHUNK.get()), has(ModItems.COOKED_FIEND_CHUNK.get()))
                .save(recipeOutput, new ResourceLocation(SemppisMythicalLegendsMod.MOD_ID, "chunk_to_cooked_fiend_meat"));

        /////////////////////////////////////////////////////////////////////////////////

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