package net.semppi.semppis_mythical_legends_mod.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class CustomBottleFoodItem extends Item {

    public CustomBottleFoodItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        if (entity instanceof Player player) {
            if (!world.isClientSide) {
                // Play the honey bottle drinking sound
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.HONEY_DRINK, player.getSoundSource(), 1.0F, 1.0F);
            }

            // Shrink the oil item stack by 1
            stack.shrink(1);

            // Create a new ItemStack for the glass bottle
            ItemStack bottleStack = new ItemStack(Items.GLASS_BOTTLE);

            // Add the glass bottle to the player's inventory or drop it if the inventory is full
            if (!player.getAbilities().instabuild) {
                if (!player.getInventory().add(bottleStack)) {
                    player.drop(bottleStack, false);
                }
            }

            // If the stack is now empty, return ItemStack.EMPTY
            return stack.isEmpty() ? ItemStack.EMPTY : stack;
        }

        return super.finishUsingItem(stack, world, entity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        // Set the use animation to DRINK, like a honey bottle
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        // Set the use duration to match the honey bottle (32 ticks)
        return 32;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        // Return the honey drinking sound for the entire drinking process
        return SoundEvents.HONEY_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        // Return null to prevent the eating sound from playing
        return null;
    }
}