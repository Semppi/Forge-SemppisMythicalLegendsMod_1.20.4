package net.semppi.semppis_mythical_legends_mod.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class CustomBowlFoodItem extends Item {

    public CustomBowlFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        // Apply the food properties to the player
        if (entity instanceof Player) {
            Player player = (Player) entity;
            player.getFoodData().eat(this, stack);

            // Play the burp sound
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_BURP, player.getSoundSource(), 1.0F, 1.0F);

            // Reduce the stack size by 1
            stack.shrink(1);

            // Create a new ItemStack for the bowl
            ItemStack bowlStack = new ItemStack(Items.BOWL);

            // Add the bowl to the player's inventory or drop it if the inventory is full
            if (!player.getAbilities().instabuild) {
                if (!player.getInventory().add(bowlStack)) {
                    player.drop(bowlStack, false);
                }
            }

            // If the stack is now empty, return ItemStack.EMPTY
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
        }

        return super.finishUsingItem(stack, world, entity);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false; // Return true if you want this item to always have an enchantment glint.
    }
}