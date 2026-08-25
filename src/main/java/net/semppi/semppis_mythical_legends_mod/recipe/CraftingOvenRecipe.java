package net.semppi.semppis_mythical_legends_mod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class CraftingOvenRecipe
        implements Recipe<CraftingContainer> {

    private final NonNullList<Ingredient> ingredients;
    private final ItemStack result;
    private final float experience;
    private final int cookingTime;

    public CraftingOvenRecipe(
            NonNullList<Ingredient> ingredients,
            ItemStack result,
            float experience,
            int cookingTime
    ) {
        this.ingredients = ingredients;
        this.result = result;
        this.experience = experience;
        this.cookingTime = cookingTime;
    }

    /*
     * ------------------------------------------------
     * RECIPE MATCHING
     * ------------------------------------------------
     *
     * This first real Crafting Oven recipe system is
     * shapeless.
     *
     * Ingredients may be anywhere in the 3 x 3 grid.
     */
    @Override
    public boolean matches(
            CraftingContainer container,
            Level level
    ) {

        List<ItemStack> presentItems =
                new ArrayList<>();

        for (int slot = 0;
             slot < container.getContainerSize();
             slot++) {

            ItemStack stack =
                    container.getItem(slot);

            if (!stack.isEmpty()) {
                presentItems.add(stack);
            }
        }

        /*
         * If the number of occupied slots is different
         * from the number of recipe ingredients,
         * this recipe cannot match.
         */
        if (presentItems.size()
                != this.ingredients.size()) {

            return false;
        }

        /*
         * Track which actual item stacks have already
         * been matched to ingredients.
         */
        boolean[] used =
                new boolean[presentItems.size()];

        return matchesIngredient(
                0,
                presentItems,
                used
        );
    }

    /*
     * Recursive matcher.
     *
     * This lets the recipe correctly support:
     *
     * - duplicate ingredients
     * - tags
     * - ingredients that can accept several items
     */
    private boolean matchesIngredient(
            int ingredientIndex,
            List<ItemStack> presentItems,
            boolean[] used
    ) {

        if (ingredientIndex
                >= this.ingredients.size()) {

            return true;
        }

        Ingredient ingredient =
                this.ingredients.get(
                        ingredientIndex
                );

        for (int itemIndex = 0;
             itemIndex < presentItems.size();
             itemIndex++) {

            if (used[itemIndex]) {
                continue;
            }

            if (!ingredient.test(
                    presentItems.get(itemIndex)
            )) {
                continue;
            }

            used[itemIndex] = true;

            if (matchesIngredient(
                    ingredientIndex + 1,
                    presentItems,
                    used
            )) {
                return true;
            }

            used[itemIndex] = false;
        }

        return false;
    }

    /*
     * The Crafting Oven does NOT directly place this
     * result into the normal crafting output.
     *
     * This result tells our menu what the Generic
     * Uncooked Food should eventually become.
     */
    @Override
    public ItemStack assemble(
            CraftingContainer container,
            RegistryAccess registryAccess
    ) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(
            int width,
            int height
    ) {
        return width * height
                >= this.ingredients.size();
    }

    @Override
    public ItemStack getResultItem(
            RegistryAccess registryAccess
    ) {
        return this.result.copy();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ItemStack getFinalResult() {
        return this.result.copy();
    }

    public float getExperience() {
        return this.experience;
    }

    public int getCookingTime() {
        return this.cookingTime;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CRAFTING_OVEN_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CRAFTING_OVEN_TYPE.get();
    }

    /*
     * ------------------------------------------------
     * SERIALIZER
     * ------------------------------------------------
     *
     * The serializer teaches Minecraft how this recipe
     * is represented in JSON and sent over the network.
     */
    public static class Serializer
            implements RecipeSerializer<CraftingOvenRecipe> {

        private static final Codec<NonNullList<Ingredient>>
                INGREDIENTS_CODEC =
                Ingredient.CODEC
                        .listOf()
                        .xmap(
                                list -> {

                                    NonNullList<Ingredient>
                                            ingredients =
                                            NonNullList.create();

                                    ingredients.addAll(list);

                                    return ingredients;
                                },

                                ingredients ->
                                        List.copyOf(
                                                ingredients
                                        )
                        );

        private static final Codec<CraftingOvenRecipe>
                CODEC =
                RecordCodecBuilder.create(
                        instance ->
                                instance.group(

                                        INGREDIENTS_CODEC
                                                .fieldOf(
                                                        "ingredients"
                                                )
                                                .forGetter(
                                                        recipe ->
                                                                recipe.ingredients
                                                ),

                                        ItemStack.CODEC
                                                .fieldOf(
                                                        "result"
                                                )
                                                .forGetter(
                                                        recipe ->
                                                                recipe.result
                                                ),

                                        Codec.FLOAT
                                                .optionalFieldOf(
                                                        "experience",
                                                        0.0F
                                                )
                                                .forGetter(
                                                        recipe ->
                                                                recipe.experience
                                                ),

                                        Codec.INT
                                                .optionalFieldOf(
                                                        "cookingtime",
                                                        100
                                                )
                                                .forGetter(
                                                        recipe ->
                                                                recipe.cookingTime
                                                )

                                ).apply(
                                        instance,
                                        CraftingOvenRecipe::new
                                )
                );

        @Override
        public Codec<CraftingOvenRecipe> codec() {
            return CODEC;
        }

        @Override
        public CraftingOvenRecipe fromNetwork(
                FriendlyByteBuf buffer
        ) {

            int ingredientCount =
                    buffer.readVarInt();

            NonNullList<Ingredient> ingredients =
                    NonNullList.withSize(
                            ingredientCount,
                            Ingredient.EMPTY
                    );

            for (int i = 0;
                 i < ingredientCount;
                 i++) {

                ingredients.set(
                        i,
                        Ingredient.fromNetwork(
                                buffer
                        )
                );
            }

            ItemStack result =
                    buffer.readItem();

            float experience =
                    buffer.readFloat();

            int cookingTime =
                    buffer.readVarInt();

            return new CraftingOvenRecipe(
                    ingredients,
                    result,
                    experience,
                    cookingTime
            );
        }

        @Override
        public void toNetwork(
                FriendlyByteBuf buffer,
                CraftingOvenRecipe recipe
        ) {

            buffer.writeVarInt(
                    recipe.ingredients.size()
            );

            for (Ingredient ingredient
                    : recipe.ingredients) {

                ingredient.toNetwork(
                        buffer
                );
            }

            buffer.writeItem(
                    recipe.result
            );

            buffer.writeFloat(
                    recipe.experience
            );

            buffer.writeVarInt(
                    recipe.cookingTime
            );
        }
    }
}