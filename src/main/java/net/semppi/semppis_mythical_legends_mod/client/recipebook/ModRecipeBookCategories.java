package net.semppi.semppis_mythical_legends_mod.client.recipebook;

import net.minecraft.client.RecipeBookCategories;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.semppi.semppis_mythical_legends_mod.recipe.ModRecipeTypes;

import java.util.List;

public final class ModRecipeBookCategories {

    private ModRecipeBookCategories() {
    }

    public static void registerRecipeBookCategories(
            RegisterRecipeBookCategoriesEvent event
    ) {

        /*
         * TEMPORARY DEBUG:
         *
         * If this appears in the console during game startup,
         * we know Forge actually reached this registration code.
         */
        System.out.println(
                "[SML RECIPE BOOK] Registering Crafting Oven categories"
        );

        /*
         * Tell Minecraft which categories this particular
         * recipe book contains.
         *
         * We retain all the normal crafting categories,
         * so vanilla and other mods' crafting recipes
         * continue working normally in the oven.
         */
        event.registerBookCategories(
                ModRecipeBookTypes.CRAFTING_OVEN,
                List.of(
                        ModRecipeBookTypes.CRAFTING_OVEN_SEARCH,
                        RecipeBookCategories.CRAFTING_EQUIPMENT,
                        RecipeBookCategories.CRAFTING_BUILDING_BLOCKS,
                        RecipeBookCategories.CRAFTING_MISC,
                        RecipeBookCategories.CRAFTING_REDSTONE,
                        ModRecipeBookTypes.CRAFTING_OVEN_FOOD
                )
        );

        /*
         * Any recipe using our custom Crafting Oven
         * recipe type belongs in the Crafting Oven Food category.
         */
        event.registerRecipeCategoryFinder(
                ModRecipeTypes.CRAFTING_OVEN_TYPE.get(),
                recipe ->
                        ModRecipeBookTypes.CRAFTING_OVEN_FOOD
        );

        /*
         * The search tab combines all crafting categories
         * plus our custom oven-food category.
         */
        event.registerAggregateCategory(
                ModRecipeBookTypes.CRAFTING_OVEN_SEARCH,
                List.of(
                        RecipeBookCategories.CRAFTING_EQUIPMENT,
                        RecipeBookCategories.CRAFTING_BUILDING_BLOCKS,
                        RecipeBookCategories.CRAFTING_MISC,
                        RecipeBookCategories.CRAFTING_REDSTONE,
                        ModRecipeBookTypes.CRAFTING_OVEN_FOOD
                )
        );

        /*
         * TEMPORARY DEBUG:
         *
         * Reaching this means all three registration calls
         * completed without throwing an error.
         */
        System.out.println(
                "[SML RECIPE BOOK] Crafting Oven categories registered"
        );
    }
}