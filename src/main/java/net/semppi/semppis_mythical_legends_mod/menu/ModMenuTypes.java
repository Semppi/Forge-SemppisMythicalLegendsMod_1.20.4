package net.semppi.semppis_mythical_legends_mod.menu;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(
                    ForgeRegistries.MENU_TYPES,
                    SemppisMythicalLegendsMod.MOD_ID
            );

    public static final RegistryObject<MenuType<CraftingOvenMenu>>
            CRAFTING_OVEN_MENU =
            MENUS.register(
                    "crafting_oven_menu",
                    () -> IForgeMenuType.create(
                            CraftingOvenMenu::new
                    )
            );

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}