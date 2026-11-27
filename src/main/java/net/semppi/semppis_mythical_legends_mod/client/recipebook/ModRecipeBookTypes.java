package net.semppi.semppis_mythical_legends_mod.client.recipebook;

import net.minecraft.client.RecipeBookCategories;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;

public final class ModRecipeBookTypes {

    /*
     * This book exists only for the Crafting Oven menu.
     */
    public static final RecipeBookType CRAFTING_OVEN =
            RecipeBookType.create(
                    "SEMPPIS_CRAFTING_OVEN"
            );

    /*
     * Dedicated category containing only the custom
     * Crafting Oven recipes.
     *
     * The tab icon can be changed later.
     */
    public static final RecipeBookCategories CRAFTING_OVEN_FOOD =
            RecipeBookCategories.create(
                    "SEMPPIS_CRAFTING_OVEN_FOOD",
                    new ItemStack(
                            ModItems.GENERIC_UNCOOKED_FOOD.get()
                    )
            );

    /*
     * Search tab used by the Crafting Oven book.
     */
    public static final RecipeBookCategories CRAFTING_OVEN_SEARCH =
            RecipeBookCategories.create(
                    "SEMPPIS_CRAFTING_OVEN_SEARCH",
                    new ItemStack(Items.COMPASS)
            );

    private ModRecipeBookTypes() {
    }
}