package net.semppi.semppis_mythical_legends_mod.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SemppisMythicalLegendsMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SML_FOOD = CREATIVE_MODE_TABS.register("smlfoodtab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.COD_SOUP.get()))
                    .title(Component.translatable("creativetab.smlfoodtab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.COD_SOUP.get());
                        pOutput.accept(ModItems.BAKED_CHEESY_FISH.get());
                        pOutput.accept(ModItems.PORRIDGE.get());
                        pOutput.accept(ModItems.HONEYED_PORRIDGE.get());
                        pOutput.accept(ModItems.CHOCOLATE_PORRIDGE.get());
                        pOutput.accept(ModItems.MANDRAKE_BERRIES.get());
                        pOutput.accept(ModItems.RICOTTA_CHEESE.get());
                        pOutput.accept(ModItems.SWEET_BERRY_JAM.get());
                        pOutput.accept(ModItems.CHOCOLATE_BUTTER.get());
                        pOutput.accept(ModItems.NOPALE_PASTE.get());
                        pOutput.accept(ModItems.SWEET_BERRY_JAM_ON_BREAD.get());
                        pOutput.accept(ModItems.CHOCOLATE_BUTTER_ON_BREAD.get());
                        pOutput.accept(ModItems.NOPALE_PASTE_ON_BREAD.get());
                        pOutput.accept(ModItems.SPANAKOPITA.get());
                        pOutput.accept(ModItems.COOKED_MUSHROOM.get());
                        pOutput.accept(ModItems.HONEYED_MEAT_PIE.get());
                        pOutput.accept(ModItems.HONEYED_BERRY_TREAT.get());
                        pOutput.accept(ModItems.FISHY_KELP_TREAT.get());
                        pOutput.accept(ModItems.COOKED_FISHY_KELP_TREAT.get());
                        pOutput.accept(ModItems.VEGGIE_KELP_TREAT.get());
                        pOutput.accept(ModItems.COOKED_VEGGIE_KELP_TREAT.get());
                        pOutput.accept(ModItems.RAW_PORKCHOP_PIECE.get());
                        pOutput.accept(ModItems.COOKED_PORKCHOP_PIECE.get());
                        pOutput.accept(ModItems.HUMANOID_FLESH_PIECE.get());
                        pOutput.accept(ModItems.HUMANOID_STEAK_PIECE.get());
                        pOutput.accept(ModItems.HUMANOID_FLESH.get());
                        pOutput.accept(ModItems.HUMANOID_STEAK.get());
                        pOutput.accept(ModItems.HUMANOID_FLESH_CHUNK.get());
                        pOutput.accept(ModItems.HUMANOID_STEAK_CHUNK.get());
                    })
                    .build());

    public static final RegistryObject<CreativeModeTab> SML_ITEM = CREATIVE_MODE_TABS.register("smlitemtab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SATYR_HORN.get()))
                    .title(Component.translatable("creativetab.smlitemtab"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModItems.MANDRAKE_ROOT.get());
                        pOutput.accept(ModItems.MANDRAKE_LEAF.get());
                        pOutput.accept(ModItems.SATYR_HORN.get());
                        pOutput.accept(ModItems.FROG_BUCKET.get());
                        pOutput.accept(ModItems.BABY_TURTLE_BUCKET.get());
                        pOutput.accept(ModItems.LOBSTER_CRICKET_BUCKET.get());
                        pOutput.accept(ModItems.FROGMAN_BUCKET.get());
                        pOutput.accept(ModItems.MEDIUM_TADPOLE_BUCKET.get());
                        pOutput.accept(ModItems.ALICANTO_SPAWN_EGG.get());
                        pOutput.accept(ModItems.BEHEMOTH_SPAWN_EGG.get());
                        pOutput.accept(ModItems.COLOSSAL_LOBSTER_SPAWN_EGG.get());
                        pOutput.accept(ModItems.GARGOYLE_SPAWN_EGG.get());
                        pOutput.accept(ModItems.KRAKEN_SPAWN_EGG.get());
                        pOutput.accept(ModItems.LOVELAND_FROGMAN_SPAWN_EGG.get());
                        pOutput.accept(ModItems.MANDRAKE_SPAWN_EGG.get());
                        pOutput.accept(ModItems.PEIKKO_SPAWN_EGG.get());
                        pOutput.accept(ModItems.PUKIS_SPAWN_EGG.get());
                        pOutput.accept(ModItems.RAINBOW_SERPENT_SPAWN_EGG.get());
                        pOutput.accept(ModItems.SATYR_SPAWN_EGG.get());
                        pOutput.accept(ModItems.THUNDERBIRD_SPAWN_EGG.get());
                        pOutput.accept(ModItems.WENDIGO_SPAWN_EGG.get());
                        pOutput.accept(ModItems.STAMP.get());
                        pOutput.accept(ModItems.WRAPPED_PUKIS_ITEM.get());
                        pOutput.accept(ModBlocks.WENDIGO_SKULL.get());
                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}