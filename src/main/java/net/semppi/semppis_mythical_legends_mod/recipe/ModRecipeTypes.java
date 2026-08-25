package net.semppi.semppis_mythical_legends_mod.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>>
            RECIPE_SERIALIZERS =
            DeferredRegister.create(
                    ForgeRegistries.RECIPE_SERIALIZERS,
                    SemppisMythicalLegendsMod.MOD_ID
            );

    public static final DeferredRegister<RecipeType<?>>
            RECIPE_TYPES =
            DeferredRegister.create(
                    ForgeRegistries.RECIPE_TYPES,
                    SemppisMythicalLegendsMod.MOD_ID
            );

    public static final RegistryObject<
            RecipeSerializer<CraftingOvenRecipe>>
            CRAFTING_OVEN_SERIALIZER =
            RECIPE_SERIALIZERS.register(
                    "crafting_oven",
                    CraftingOvenRecipe.Serializer::new
            );

    public static final RegistryObject<
            RecipeType<CraftingOvenRecipe>>
            CRAFTING_OVEN_TYPE =
            RECIPE_TYPES.register(
                    "crafting_oven",
                    () -> new RecipeType<>() {

                        @Override
                        public String toString() {
                            return SemppisMythicalLegendsMod.MOD_ID
                                    + ":crafting_oven";
                        }
                    }
            );

    public static void register(
            IEventBus eventBus
    ) {

        RECIPE_SERIALIZERS.register(
                eventBus
        );

        RECIPE_TYPES.register(
                eventBus
        );
    }
}