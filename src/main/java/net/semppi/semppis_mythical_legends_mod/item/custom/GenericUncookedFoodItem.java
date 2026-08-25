package net.semppi.semppis_mythical_legends_mod.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GenericUncookedFoodItem extends Item {

    /*
     * These are the names of the pieces of data stored
     * inside an individual Generic Uncooked Food stack.
     */
    private static final String RESULT_ITEM_TAG =
            "CraftingOvenResult";

    private static final String RESULT_COUNT_TAG =
            "CraftingOvenResultCount";

    private static final String COOK_TIME_TAG =
            "CraftingOvenCookTime";

    private static final String EXPERIENCE_TAG =
            "CraftingOvenExperience";

    public GenericUncookedFoodItem(
            Properties properties
    ) {
        super(properties);
    }

    /*
     * Creates one Generic Uncooked Food stack and writes
     * information into it describing what it should become.
     */
    public static ItemStack create(
            ItemStack finalFood,
            int cookTime,
            float experience
    ) {

        ItemStack genericStack =
                new ItemStack(
                        net.semppi.semppis_mythical_legends_mod.item.ModItems
                                .GENERIC_UNCOOKED_FOOD
                                .get()
                );

        ResourceLocation resultId =
                BuiltInRegistries.ITEM.getKey(
                        finalFood.getItem()
                );

        genericStack.getOrCreateTag()
                .putString(
                        RESULT_ITEM_TAG,
                        resultId.toString()
                );

        genericStack.getOrCreateTag()
                .putInt(
                        RESULT_COUNT_TAG,
                        finalFood.getCount()
                );

        genericStack.getOrCreateTag()
                .putInt(
                        COOK_TIME_TAG,
                        cookTime
                );

        genericStack.getOrCreateTag()
                .putFloat(
                        EXPERIENCE_TAG,
                        experience
                );

        return genericStack;
    }

    /*
     * Reads the final food back out of the stored data.
     */
    public static ItemStack getCookingResult(
            ItemStack genericStack
    ) {

        if (!genericStack.hasTag()) {
            return ItemStack.EMPTY;
        }

        String resultString =
                genericStack.getTag()
                        .getString(
                                RESULT_ITEM_TAG
                        );

        if (resultString.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ResourceLocation resultId =
                new ResourceLocation(
                        resultString
                );

        Item resultItem =
                BuiltInRegistries.ITEM.get(
                        resultId
                );

        int resultCount =
                Math.max(
                        1,
                        genericStack.getTag()
                                .getInt(
                                        RESULT_COUNT_TAG
                                )
                );

        return new ItemStack(
                resultItem,
                resultCount
        );
    }

    public static int getCookingTime(
            ItemStack genericStack
    ) {

        if (!genericStack.hasTag()) {
            return 100;
        }

        return Math.max(
                1,
                genericStack.getTag()
                        .getInt(
                                COOK_TIME_TAG
                        )
        );
    }

    public static float getCookingExperience(
            ItemStack genericStack
    ) {

        if (!genericStack.hasTag()) {
            return 0.0F;
        }

        return genericStack.getTag()
                .getFloat(
                        EXPERIENCE_TAG
                );
    }

    /*
     * Adds the useful hover text.
     *
     * Example:
     *
     * Generic Uncooked Food
     * Cooks into: Pepes Telur Kodok
     */
    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag flag
    ) {

        super.appendHoverText(
                stack,
                level,
                tooltip,
                flag
        );

        ItemStack result =
                getCookingResult(
                        stack
                );

        if (!result.isEmpty()) {

            tooltip.add(
                    Component.translatable(
                            "tooltip.semppis_mythical_legends_mod.cooks_into",
                            result.getHoverName()
                    ).withStyle(
                            ChatFormatting.GRAY
                    )
            );
        }
    }
}