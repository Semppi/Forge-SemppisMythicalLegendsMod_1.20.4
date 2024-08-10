package net.semppi.semppis_mythical_legends_mod.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.item.custom.WendigoSkullItem;
import net.semppi.semppis_mythical_legends_mod.item.custom.WrappedPukisItem;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SemppisMythicalLegendsMod.MOD_ID);

    public static final RegistryObject<Item> MANDRAKE_ROOT = ITEMS.register("mandrake_root",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MANDRAKE_LEAF = ITEMS.register("mandrake_leaf",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SATYR_HORN = ITEMS.register("satyr_horn",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FROG_BUCKET = ITEMS.register("frog_bucket",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> BABY_TURTLE_BUCKET = ITEMS.register("baby_turtle_bucket",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LOBSTER_CRICKET_BUCKET = ITEMS.register("lobster_cricket_bucket",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FROGMAN_BUCKET = ITEMS.register("frogman_bucket",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MEDIUM_TADPOLE_BUCKET = ITEMS.register("medium_tadpole_bucket",
            () -> new Item(new Item.Properties()));


    public static final RegistryObject<Item> ALICANTO_SPAWN_EGG = ITEMS.register("alicanto_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LOVELAND_FROGMAN, 0xF2AE3E, 0x51BC6D, new Item.Properties()));

    public static final RegistryObject<Item> BEHEMOTH_SPAWN_EGG = ITEMS.register("behemoth_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BEHEMOTH, 0x645137, 0x433420, new Item.Properties()));

    public static final RegistryObject<Item> COLOSSAL_LOBSTER_SPAWN_EGG = ITEMS.register("colossal_lobster_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.COLOSSAL_LOBSTER, 0x223227, 0xf08632, new Item.Properties()));

    public static final RegistryObject<Item> GARGOYLE_SPAWN_EGG = ITEMS.register("gargoyle_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LOVELAND_FROGMAN, 0x505050, 0x433b96, new Item.Properties()));

    public static final RegistryObject<Item> KRAKEN_SPAWN_EGG = ITEMS.register("kraken_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.KRAKEN, 0xa12727, 0x3e3535, new Item.Properties()));

    public static final RegistryObject<Item> LOVELAND_FROGMAN_SPAWN_EGG = ITEMS.register("loveland_frogman_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LOVELAND_FROGMAN, 0x386c3f, 0xadbc6e, new Item.Properties()));

    public static final RegistryObject<Item> MANDRAKE_SPAWN_EGG = ITEMS.register("mandrake_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MANDRAKE, 0x383226, 0x75713a, new Item.Properties()));

    public static final RegistryObject<Item> PEIKKO_SPAWN_EGG = ITEMS.register("peikko_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LOVELAND_FROGMAN, 0x666666, 0x2b582a, new Item.Properties()));

    public static final RegistryObject<Item> PUKIS_SPAWN_EGG = ITEMS.register("pukis_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PUKIS, 0x3b3497, 0x2c1830, new Item.Properties()));

    public static final RegistryObject<Item> RAINBOW_SERPENT_SPAWN_EGG = ITEMS.register("rainbow_serpent_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LOVELAND_FROGMAN, 0x28C13F, 0x820d8e, new Item.Properties()));

    public static final RegistryObject<Item> SATYR_SPAWN_EGG = ITEMS.register("satyr_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SATYR, 0x95866d, 0x323232, new Item.Properties()));

    public static final RegistryObject<Item> THUNDERBIRD_SPAWN_EGG = ITEMS.register("thunderbird_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LOVELAND_FROGMAN, 0x232238, 0xd0c332, new Item.Properties()));

    public static final RegistryObject<Item> WENDIGO_SPAWN_EGG = ITEMS.register("wendigo_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WENDIGO, 0xb6b6b6, 0x4c4944, new Item.Properties()));

    public static final RegistryObject<Item> STAMP = ITEMS.register("stamp",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WRAPPED_PUKIS_ITEM = ITEMS.register("wrapped_pukis",
            () -> new WrappedPukisItem(new Item.Properties()));

    public static final RegistryObject<Item> WENDIGO_SKULL_ITEM = ITEMS.register("wendigo_skull_item",
            () -> new WendigoSkullItem(ModBlocks.WENDIGO_SKULL.get(), new Item.Properties()));


    public static final RegistryObject<Item> COD_SOUP = ITEMS.register("cod_soup",
            () -> new CustomBowlFoodItem(new Item.Properties().food(ModFoods.COD_SOUP)));

    public static final RegistryObject<Item> BAKED_CHEESY_FISH = ITEMS.register("baked_cheesy_fish",
            () -> new CustomBowlFoodItem(new Item.Properties().food(ModFoods.BAKED_CHEESY_FISH)));

    public static final RegistryObject<Item> PORRIDGE = ITEMS.register("porridge",
            () -> new CustomBowlFoodItem(new Item.Properties().food(ModFoods.PORRIDGE)));

    public static final RegistryObject<Item> HONEYED_PORRIDGE = ITEMS.register("honeyed_porridge",
            () -> new CustomBowlFoodItem(new Item.Properties().food(ModFoods.HONEYED_PORRIDGE)));

    public static final RegistryObject<Item> CHOCOLATE_PORRIDGE = ITEMS.register("chocolate_porridge",
            () -> new CustomBowlFoodItem(new Item.Properties().food(ModFoods.CHOCOLATE_PORRIDGE)));

    public static final RegistryObject<Item> MANDRAKE_BERRIES = ITEMS.register("mandrake_berries",
            () -> new Item(new Item.Properties().food(ModFoods.MANDRAKE_BERRIES)));

    public static final RegistryObject<Item> RICOTTA_CHEESE = ITEMS.register("ricotta_cheese",
            () -> new Item(new Item.Properties().food(ModFoods.RICOTTA_CHEESE)));

    public static final RegistryObject<Item> SWEET_BERRY_JAM = ITEMS.register("sweet_berry_jam",
            () -> new Item(new Item.Properties().food(ModFoods.SWEET_BERRY_JAM)));

    public static final RegistryObject<Item> CHOCOLATE_BUTTER = ITEMS.register("chocolate_butter",
            () -> new Item(new Item.Properties().food(ModFoods.CHOCOLATE_BUTTER)));

    public static final RegistryObject<Item> NOPALE_PASTE = ITEMS.register("nopale_paste",
            () -> new Item(new Item.Properties().food(ModFoods.NOPALE_PASTE)));

    public static final RegistryObject<Item> SWEET_BERRY_JAM_ON_BREAD = ITEMS.register("sweet_berry_jam_on_bread",
            () -> new Item(new Item.Properties().food(ModFoods.SWEET_BERRY_JAM_ON_BREAD)));

    public static final RegistryObject<Item> CHOCOLATE_BUTTER_ON_BREAD = ITEMS.register("chocolate_butter_on_bread",
            () -> new Item(new Item.Properties().food(ModFoods.CHOCOLATE_BUTTER_ON_BREAD)));

    public static final RegistryObject<Item> NOPALE_PASTE_ON_BREAD = ITEMS.register("nopale_paste_on_bread",
            () -> new Item(new Item.Properties().food(ModFoods.NOPALE_PASTE_ON_BREAD)));

    public static final RegistryObject<Item> SPANAKOPITA = ITEMS.register("spanakopita",
            () -> new Item(new Item.Properties().food(ModFoods.SPANAKOPITA)));

    public static final RegistryObject<Item> COOKED_MUSHROOM = ITEMS.register("cooked_mushroom",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_MUSHROOM)));

    public static final RegistryObject<Item> HONEYED_MEAT_PIE = ITEMS.register("honeyed_meat_pie",
            () -> new Item(new Item.Properties().food(ModFoods.HONEYED_MEAT_PIE)));

    public static final RegistryObject<Item> HONEYED_BERRY_TREAT = ITEMS.register("honeyed_berry_treat",
            () -> new Item(new Item.Properties().food(ModFoods.HONEYED_BERRY_TREAT)));

    public static final RegistryObject<Item> FISHY_KELP_TREAT = ITEMS.register("fishy_kelp_treat",
            () -> new Item(new Item.Properties().food(ModFoods.FISHY_KELP_TREAT)));

    public static final RegistryObject<Item> COOKED_FISHY_KELP_TREAT = ITEMS.register("cooked_fishy_kelp_treat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FISHY_KELP_TREAT)));

    public static final RegistryObject<Item> VEGGIE_KELP_TREAT = ITEMS.register("veggie_kelp_treat",
            () -> new Item(new Item.Properties().food(ModFoods.VEGGIE_KELP_TREAT)));

    public static final RegistryObject<Item> COOKED_VEGGIE_KELP_TREAT = ITEMS.register("cooked_veggie_kelp_treat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_VEGGIE_KELP_TREAT)));

    public static final RegistryObject<Item> RAW_PORKCHOP_PIECE = ITEMS.register("raw_porkchop_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_PORKCHOP_PIECE)));

    public static final RegistryObject<Item> COOKED_PORKCHOP_PIECE = ITEMS.register("cooked_porkchop_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_PORKCHOP_PIECE)));

    public static final RegistryObject<Item> HUMANOID_FLESH_PIECE = ITEMS.register("humanoid_flesh_piece",
            () -> new Item(new Item.Properties().food(ModFoods.HUMANOID_FLESH_PIECE)));

    public static final RegistryObject<Item> HUMANOID_STEAK_PIECE = ITEMS.register("humanoid_steak_piece",
            () -> new Item(new Item.Properties().food(ModFoods.HUMANOID_STEAK_PIECE)));

    public static final RegistryObject<Item> HUMANOID_FLESH = ITEMS.register("humanoid_flesh",
            () -> new Item(new Item.Properties().food(ModFoods.HUMANOID_FLESH)));

    public static final RegistryObject<Item> HUMANOID_STEAK = ITEMS.register("humanoid_steak",
            () -> new Item(new Item.Properties().food(ModFoods.HUMANOID_STEAK)));

    public static final RegistryObject<Item> HUMANOID_FLESH_CHUNK = ITEMS.register("humanoid_flesh_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.HUMANOID_FLESH_CHUNK)));

    public static final RegistryObject<Item> HUMANOID_STEAK_CHUNK = ITEMS.register("humanoid_steak_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.HUMANOID_STEAK_CHUNK)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}