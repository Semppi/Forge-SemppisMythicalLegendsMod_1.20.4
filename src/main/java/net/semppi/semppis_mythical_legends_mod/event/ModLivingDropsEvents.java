package net.semppi.semppis_mythical_legends_mod.event;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;

import java.util.Random;

@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModLivingDropsEvents {

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        // Check if the entity is a Ghast
        if (event.getEntity().getType() == EntityType.GHAST) {
            Random random = new Random();

            // 13% chance for Ghastly Teeth to drop
            if (random.nextDouble() < 0.13) {
                event.getDrops().add(new ItemEntity(
                        event.getEntity().level(),
                        event.getEntity().getX(),
                        event.getEntity().getY(),
                        event.getEntity().getZ(),
                        new ItemStack(ModItems.GHASTLY_TEETH.get())
                ));
            }
        }
    }
}