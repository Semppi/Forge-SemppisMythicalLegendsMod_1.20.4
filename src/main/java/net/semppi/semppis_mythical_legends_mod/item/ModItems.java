package net.semppi.semppis_mythical_legends_mod.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.item.custom.PukisEggItem;
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

    public static final RegistryObject<Item> GHASTLY_TEETH = ITEMS.register("ghastly_teeth",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CARE_BRUSH = ITEMS.register("care_brush",
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
            () -> new ForgeSpawnEggItem(ModEntities.ALICANTO, 0xF2AE3E, 0x51BC6D, new Item.Properties()));

    public static final RegistryObject<Item> LESSER_BEHEMOTH_SPAWN_EGG = ITEMS.register("lesser_behemoth_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LESSER_BEHEMOTH, 0x645137, 0x433420, new Item.Properties()));

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

    public static final RegistryObject<Item> MANDRAKE_SPROUTLING_SPAWN_EGG = ITEMS.register("mandrake_sproutling_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MANDRAKE_SPROUTLING, 0x383226, 0x75713a, new Item.Properties()));

    public static final RegistryObject<Item> PEIKKO_SPAWN_EGG = ITEMS.register("peikko_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.LOVELAND_FROGMAN, 0x666666, 0x2b582a, new Item.Properties()));

    public static final RegistryObject<Item> PROTO_WENDIGO_SPAWN_EGG = ITEMS.register("proto_wendigo_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PROTO_WENDIGO, 0xababab, 0xa99393, new Item.Properties()));

    public static final RegistryObject<Item> PUKIS_SPAWN_EGG = ITEMS.register("pukis_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PUKIS, 0x017900, 0xB4E652, new Item.Properties()));

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

    public static final RegistryObject<Item> PUKIS_EGG_ITEM = ITEMS.register("pukis_egg",
            () -> new PukisEggItem(ModBlocks.PUKIS_EGG.get(), new Item.Properties()));

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

    public static final RegistryObject<Item> GRASS_BUNDLE = ITEMS.register("grass_bundle",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SEAGRASS_BUNDLE = ITEMS.register("seagrass_bundle",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> EDIBLE_LEAF = ITEMS.register("edible_leaf",
            () -> new Item(new Item.Properties().food(ModFoods.EDIBLE_LEAF)));

    public static final RegistryObject<Item> SPRUCE_TIPS = ITEMS.register("spruce_tips",
            () -> new Item(new Item.Properties().food(ModFoods.SPRUCE_TIPS)));

    public static final RegistryObject<Item> CUCUMBER = ITEMS.register("cucumber",
            () -> new Item(new Item.Properties().food(ModFoods.CUCUMBER)));

    public static final RegistryObject<Item> COOKED_MUSHROOM = ITEMS.register("cooked_mushroom",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_MUSHROOM)));

    public static final RegistryObject<Item> CHEESE = ITEMS.register("cheese",
            () -> new Item(new Item.Properties().food(ModFoods.CHEESE)));

    public static final RegistryObject<Item> FRIED_EGG = ITEMS.register("fried_egg",
            () -> new Item(new Item.Properties().food(ModFoods.FRIED_EGG)));

    public static final RegistryObject<Item> BUTTER = ITEMS.register("butter",
            () -> new Item(new Item.Properties().food(ModFoods.BUTTER)));

    public static final RegistryObject<Item> OIL = ITEMS.register("oil",
            () -> new CustomBottleFoodItem(new Item.Properties().food(ModFoods.OIL)));

    public static final RegistryObject<Item> SWEET_BERRY_JAM = ITEMS.register("sweet_berry_jam",
            () -> new Item(new Item.Properties().food(ModFoods.SWEET_BERRY_JAM)));

    public static final RegistryObject<Item> GLOW_BERRY_JAM = ITEMS.register("glow_berry_jam",
            () -> new Item(new Item.Properties().food(ModFoods.GLOW_BERRY_JAM)));

    public static final RegistryObject<Item> CHOCOLATE_BUTTER = ITEMS.register("chocolate_butter",
            () -> new Item(new Item.Properties().food(ModFoods.CHOCOLATE_BUTTER)));

    public static final RegistryObject<Item> NOPALE_PASTE = ITEMS.register("nopale_paste",
            () -> new Item(new Item.Properties().food(ModFoods.NOPALE_PASTE)));

    public static final RegistryObject<Item> SWEET_BERRY_JAM_ON_BREAD = ITEMS.register("sweet_berry_jam_on_bread",
            () -> new Item(new Item.Properties().food(ModFoods.SWEET_BERRY_JAM_ON_BREAD)));

    public static final RegistryObject<Item> GLOW_BERRY_JAM_ON_BREAD = ITEMS.register("glow_berry_jam_on_bread",
            () -> new Item(new Item.Properties().food(ModFoods.GLOW_BERRY_JAM_ON_BREAD)));

    public static final RegistryObject<Item> CHOCOLATE_BUTTER_ON_BREAD = ITEMS.register("chocolate_butter_on_bread",
            () -> new Item(new Item.Properties().food(ModFoods.CHOCOLATE_BUTTER_ON_BREAD)));

    public static final RegistryObject<Item> NOPALE_PASTE_ON_BREAD = ITEMS.register("nopale_paste_on_bread",
            () -> new Item(new Item.Properties().food(ModFoods.NOPALE_PASTE_ON_BREAD)));

    public static final RegistryObject<Item> SPANAKOPITA = ITEMS.register("spanakopita",
            () -> new Item(new Item.Properties().food(ModFoods.SPANAKOPITA)));

    public static final RegistryObject<Item> PORK_AND_RABBIT_PIE = ITEMS.register("pork_and_rabbit_pie",
            () -> new Item(new Item.Properties().food(ModFoods.PORK_AND_RABBIT_PIE)));

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

    public static final RegistryObject<Item> RAW_BEEF_PIECE = ITEMS.register("raw_beef_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_BEEF_PIECE)));

    public static final RegistryObject<Item> COOKED_STEAK_PIECE = ITEMS.register("cooked_steak_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_STEAK_PIECE)));

    public static final RegistryObject<Item> RAW_BEEF_CHUNK = ITEMS.register("raw_beef_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_BEEF_CHUNK)));

    public static final RegistryObject<Item> COOKED_STEAK_CHUNK = ITEMS.register("cooked_steak_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_STEAK_CHUNK)));

    public static final RegistryObject<Item> RAW_PORKCHOP_PIECE = ITEMS.register("raw_porkchop_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_PORKCHOP_PIECE)));

    public static final RegistryObject<Item> COOKED_PORKCHOP_PIECE = ITEMS.register("cooked_porkchop_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_PORKCHOP_PIECE)));

    public static final RegistryObject<Item> RAW_PORKCHOP_CHUNK = ITEMS.register("raw_porkchop_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_PORKCHOP_CHUNK)));

    public static final RegistryObject<Item> COOKED_PORKCHOP_CHUNK = ITEMS.register("cooked_porkchop_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_PORKCHOP_CHUNK)));

    public static final RegistryObject<Item> RAW_MUTTON_PIECE = ITEMS.register("raw_mutton_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_MUTTON_PIECE)));

    public static final RegistryObject<Item> COOKED_MUTTON_PIECE = ITEMS.register("cooked_mutton_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_MUTTON_PIECE)));

    public static final RegistryObject<Item> RAW_MUTTON_CHUNK = ITEMS.register("raw_mutton_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_MUTTON_CHUNK)));

    public static final RegistryObject<Item> COOKED_MUTTON_CHUNK = ITEMS.register("cooked_mutton_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_MUTTON_CHUNK)));

    public static final RegistryObject<Item> RAW_AVIAN_PIECE = ITEMS.register("raw_avian_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AVIAN_PIECE)));

    public static final RegistryObject<Item> COOKED_AVIAN_PIECE = ITEMS.register("cooked_avian_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AVIAN_PIECE)));

    public static final RegistryObject<Item> RAW_AVIAN_MEAT = ITEMS.register("raw_avian_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AVIAN_MEAT)));

    public static final RegistryObject<Item> COOKED_AVIAN_MEAT = ITEMS.register("cooked_avian_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AVIAN_MEAT)));

    public static final RegistryObject<Item> RAW_AVIAN_CHUNK = ITEMS.register("raw_avian_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AVIAN_CHUNK)));

    public static final RegistryObject<Item> COOKED_AVIAN_CHUNK = ITEMS.register("cooked_avian_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AVIAN_CHUNK)));

    public static final RegistryObject<Item> RAW_BUSHMEAT_PIECE = ITEMS.register("raw_bushmeat_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_BUSHMEAT_PIECE)));

    public static final RegistryObject<Item> COOKED_BUSHMEAT_PIECE = ITEMS.register("cooked_bushmeat_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_BUSHMEAT_PIECE)));

    public static final RegistryObject<Item> RAW_BUSHMEAT = ITEMS.register("raw_bushmeat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_BUSHMEAT)));

    public static final RegistryObject<Item> COOKED_BUSHMEAT = ITEMS.register("cooked_bushmeat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_BUSHMEAT)));

    public static final RegistryObject<Item> RAW_BUSHMEAT_CHUNK = ITEMS.register("raw_bushmeat_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_BUSHMEAT_CHUNK)));

    public static final RegistryObject<Item> COOKED_BUSHMEAT_CHUNK = ITEMS.register("cooked_bushmeat_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_BUSHMEAT_CHUNK)));

    public static final RegistryObject<Item> RAW_FISH_PIECE = ITEMS.register("raw_fish_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_FISH_PIECE)));

    public static final RegistryObject<Item> COOKED_FISH_PIECE = ITEMS.register("cooked_fish_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FISH_PIECE)));

    public static final RegistryObject<Item> RAW_FISH_MEAT = ITEMS.register("raw_fish_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_FISH_MEAT)));

    public static final RegistryObject<Item> COOKED_FISH_MEAT = ITEMS.register("cooked_fish_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FISH_MEAT)));

    public static final RegistryObject<Item> RAW_FISH_CHUNK = ITEMS.register("raw_fish_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_FISH_CHUNK)));

    public static final RegistryObject<Item> COOKED_FISH_CHUNK = ITEMS.register("cooked_fish_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FISH_CHUNK)));

    public static final RegistryObject<Item> RAW_UNGULATE_PIECE = ITEMS.register("raw_ungulate_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_UNGULATE_PIECE)));

    public static final RegistryObject<Item> COOKED_UNGULATE_PIECE = ITEMS.register("cooked_ungulate_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_UNGULATE_PIECE)));

    public static final RegistryObject<Item> RAW_UNGULATE_MEAT = ITEMS.register("raw_ungulate_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_UNGULATE_MEAT)));

    public static final RegistryObject<Item> COOKED_UNGULATE_MEAT = ITEMS.register("cooked_ungulate_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_UNGULATE_MEAT)));

    public static final RegistryObject<Item> RAW_UNGULATE_CHUNK = ITEMS.register("raw_ungulate_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_UNGULATE_CHUNK)));

    public static final RegistryObject<Item> COOKED_UNGULATE_CHUNK = ITEMS.register("cooked_ungulate_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_UNGULATE_CHUNK)));

    public static final RegistryObject<Item> RAW_CRUSTACEAN_PIECE = ITEMS.register("raw_crustacean_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_CRUSTACEAN_PIECE)));

    public static final RegistryObject<Item> COOKED_CRUSTACEAN_PIECE = ITEMS.register("cooked_crustacean_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_CRUSTACEAN_PIECE)));

    public static final RegistryObject<Item> RAW_CRUSTACEAN_MEAT = ITEMS.register("raw_crustacean_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_CRUSTACEAN_MEAT)));

    public static final RegistryObject<Item> COOKED_CRUSTACEAN_MEAT = ITEMS.register("cooked_crustacean_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_CRUSTACEAN_MEAT)));

    public static final RegistryObject<Item> RAW_CRUSTACEAN_CHUNK = ITEMS.register("raw_crustacean_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_CRUSTACEAN_CHUNK)));

    public static final RegistryObject<Item> COOKED_CRUSTACEAN_CHUNK = ITEMS.register("cooked_crustacean_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_CRUSTACEAN_CHUNK)));

    public static final RegistryObject<Item> RAW_MOLLUSC_PIECE = ITEMS.register("raw_mollusc_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_MOLLUSC_PIECE)));

    public static final RegistryObject<Item> COOKED_MOLLUSC_PIECE = ITEMS.register("cooked_mollusc_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_MOLLUSC_PIECE)));

    public static final RegistryObject<Item> RAW_MOLLUSC_MEAT = ITEMS.register("raw_mollusc_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_MOLLUSC_MEAT)));

    public static final RegistryObject<Item> COOKED_MOLLUSC_MEAT = ITEMS.register("cooked_mollusc_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_MOLLUSC_MEAT)));

    public static final RegistryObject<Item> RAW_MOLLUSC_CHUNK = ITEMS.register("raw_mollusc_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_MOLLUSC_CHUNK)));

    public static final RegistryObject<Item> COOKED_MOLLUSC_CHUNK = ITEMS.register("cooked_mollusc_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_MOLLUSC_CHUNK)));

    public static final RegistryObject<Item> RAW_AMPHIBIAN_PIECE = ITEMS.register("raw_amphibian_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AMPHIBIAN_PIECE)));

    public static final RegistryObject<Item> COOKED_AMPHIBIAN_PIECE = ITEMS.register("cooked_amphibian_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AMPHIBIAN_PIECE)));

    public static final RegistryObject<Item> RAW_AMPHIBIAN_MEAT = ITEMS.register("raw_amphibian_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AMPHIBIAN_MEAT)));

    public static final RegistryObject<Item> COOKED_AMPHIBIAN_MEAT = ITEMS.register("cooked_amphibian_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AMPHIBIAN_MEAT)));

    public static final RegistryObject<Item> RAW_AMPHIBIAN_CHUNK = ITEMS.register("raw_amphibian_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AMPHIBIAN_CHUNK)));

    public static final RegistryObject<Item> COOKED_AMPHIBIAN_CHUNK = ITEMS.register("cooked_amphibian_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AMPHIBIAN_CHUNK)));

    public static final RegistryObject<Item> RAW_REPTILIAN_PIECE = ITEMS.register("raw_reptilian_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_REPTILIAN_PIECE)));

    public static final RegistryObject<Item> COOKED_REPTILIAN_PIECE = ITEMS.register("cooked_reptilian_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_REPTILIAN_PIECE)));

    public static final RegistryObject<Item> RAW_REPTILIAN_MEAT = ITEMS.register("raw_reptilian_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_REPTILIAN_MEAT)));

    public static final RegistryObject<Item> COOKED_REPTILIAN_MEAT = ITEMS.register("cooked_reptilian_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_REPTILIAN_MEAT)));

    public static final RegistryObject<Item> RAW_REPTILIAN_CHUNK = ITEMS.register("raw_reptilian_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_REPTILIAN_CHUNK)));

    public static final RegistryObject<Item> COOKED_REPTILIAN_CHUNK = ITEMS.register("cooked_reptilian_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_REPTILIAN_CHUNK)));

    public static final RegistryObject<Item> RAW_AQUATIC_PIECE = ITEMS.register("raw_aquatic_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AQUATIC_PIECE)));

    public static final RegistryObject<Item> COOKED_AQUATIC_PIECE = ITEMS.register("cooked_aquatic_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AQUATIC_PIECE)));

    public static final RegistryObject<Item> RAW_AQUATIC_MEAT = ITEMS.register("raw_aquatic_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AQUATIC_MEAT)));

    public static final RegistryObject<Item> COOKED_AQUATIC_MEAT = ITEMS.register("cooked_aquatic_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AQUATIC_MEAT)));

    public static final RegistryObject<Item> RAW_AQUATIC_CHUNK = ITEMS.register("raw_aquatic_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_AQUATIC_CHUNK)));

    public static final RegistryObject<Item> COOKED_AQUATIC_CHUNK = ITEMS.register("cooked_aquatic_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_AQUATIC_CHUNK)));

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

    public static final RegistryObject<Item> ROTTEN_FLESH_PIECE = ITEMS.register("rotten_flesh_piece",
            () -> new Item(new Item.Properties().food(ModFoods.ROTTEN_FLESH_PIECE)));

    public static final RegistryObject<Item> ROTTEN_FLESH_CHUNK = ITEMS.register("rotten_flesh_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.ROTTEN_FLESH_CHUNK)));

    public static final RegistryObject<Item> RAW_SCUTTLE_PIECE = ITEMS.register("raw_scuttle_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_SCUTTLE_PIECE)));

    public static final RegistryObject<Item> COOKED_SCUTTLE_PIECE = ITEMS.register("cooked_scuttle_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_SCUTTLE_PIECE)));

    public static final RegistryObject<Item> RAW_SCUTTLE_MEAT = ITEMS.register("raw_scuttle_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_SCUTTLE_MEAT)));

    public static final RegistryObject<Item> COOKED_SCUTTLE_MEAT = ITEMS.register("cooked_scuttle_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_SCUTTLE_MEAT)));

    public static final RegistryObject<Item> RAW_SCUTTLE_CHUNK = ITEMS.register("raw_scuttle_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_SCUTTLE_CHUNK)));

    public static final RegistryObject<Item> COOKED_SCUTTLE_CHUNK = ITEMS.register("cooked_scuttle_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_SCUTTLE_CHUNK)));

    public static final RegistryObject<Item> RAW_BEAST_PIECE = ITEMS.register("raw_beast_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_BEAST_PIECE)));

    public static final RegistryObject<Item> COOKED_BEAST_PIECE = ITEMS.register("cooked_beast_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_BEAST_PIECE)));

    public static final RegistryObject<Item> RAW_BEAST_MEAT = ITEMS.register("raw_beast_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_BEAST_MEAT)));

    public static final RegistryObject<Item> COOKED_BEAST_MEAT = ITEMS.register("cooked_beast_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_BEAST_MEAT)));

    public static final RegistryObject<Item> RAW_BEAST_CHUNK = ITEMS.register("raw_beast_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_BEAST_CHUNK)));

    public static final RegistryObject<Item> COOKED_BEAST_CHUNK = ITEMS.register("cooked_beast_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_BEAST_CHUNK)));

    public static final RegistryObject<Item> RAW_FEY_PIECE = ITEMS.register("raw_fey_piece",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_FEY_PIECE)));

    public static final RegistryObject<Item> COOKED_FEY_PIECE = ITEMS.register("cooked_fey_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FEY_PIECE)));

    public static final RegistryObject<Item> RAW_FEY_MEAT = ITEMS.register("raw_fey_meat",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_FEY_MEAT)));

    public static final RegistryObject<Item> COOKED_FEY_MEAT = ITEMS.register("cooked_fey_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FEY_MEAT)));

    public static final RegistryObject<Item> RAW_FEY_CHUNK = ITEMS.register("raw_fey_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_FEY_CHUNK)));

    public static final RegistryObject<Item> COOKED_FEY_CHUNK = ITEMS.register("cooked_fey_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FEY_CHUNK)));

    public static final RegistryObject<Item> DRACONIC_FLESH_PIECE = ITEMS.register("draconic_flesh_piece",
            () -> new Item(new Item.Properties().food(ModFoods.DRACONIC_FLESH_PIECE)));

    public static final RegistryObject<Item> DRACONIC_STEAK_PIECE = ITEMS.register("draconic_steak_piece",
            () -> new Item(new Item.Properties().food(ModFoods.DRACONIC_STEAK_PIECE)));

    public static final RegistryObject<Item> DRACONIC_FLESH = ITEMS.register("draconic_flesh",
            () -> new Item(new Item.Properties().food(ModFoods.DRACONIC_FLESH)));

    public static final RegistryObject<Item> DRACONIC_STEAK = ITEMS.register("draconic_steak",
            () -> new Item(new Item.Properties().food(ModFoods.DRACONIC_STEAK)));

    public static final RegistryObject<Item> DRACONIC_FLESH_CHUNK = ITEMS.register("draconic_flesh_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.DRACONIC_FLESH_CHUNK)));

    public static final RegistryObject<Item> DRACONIC_STEAK_CHUNK = ITEMS.register("draconic_steak_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.DRACONIC_STEAK_CHUNK)));

    public static final RegistryObject<Item> ABERRANT_FLESH_PIECE = ITEMS.register("aberrant_flesh_piece",
            () -> new Item(new Item.Properties().food(ModFoods.ABERRANT_FLESH_PIECE)));

    public static final RegistryObject<Item> COOKED_ABERRANT_PIECE = ITEMS.register("cooked_aberrant_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_ABERRANT_PIECE)));

    public static final RegistryObject<Item> ABERRANT_FLESH = ITEMS.register("aberrant_flesh",
            () -> new Item(new Item.Properties().food(ModFoods.ABERRANT_FLESH)));

    public static final RegistryObject<Item> COOKED_ABERRANT_MEAT = ITEMS.register("cooked_aberrant_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_ABERRANT_MEAT)));

    public static final RegistryObject<Item> ABERRANT_FLESH_CHUNK = ITEMS.register("aberrant_flesh_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.ABERRANT_FLESH_CHUNK)));

    public static final RegistryObject<Item> COOKED_ABERRANT_CHUNK = ITEMS.register("cooked_aberrant_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_ABERRANT_CHUNK)));

    public static final RegistryObject<Item> FIEND_FLESH_PIECE = ITEMS.register("fiend_flesh_piece",
            () -> new Item(new Item.Properties().food(ModFoods.FIEND_FLESH_PIECE)));

    public static final RegistryObject<Item> COOKED_FIEND_PIECE = ITEMS.register("cooked_fiend_piece",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FIEND_PIECE)));

    public static final RegistryObject<Item> FIEND_FLESH = ITEMS.register("fiend_flesh",
            () -> new Item(new Item.Properties().food(ModFoods.FIEND_FLESH)));

    public static final RegistryObject<Item> COOKED_FIEND_MEAT = ITEMS.register("cooked_fiend_meat",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FIEND_MEAT)));

    public static final RegistryObject<Item> FIEND_FLESH_CHUNK = ITEMS.register("fiend_flesh_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.FIEND_FLESH_CHUNK)));

    public static final RegistryObject<Item> COOKED_FIEND_CHUNK = ITEMS.register("cooked_fiend_chunk",
            () -> new Item(new Item.Properties().food(ModFoods.COOKED_FIEND_CHUNK)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}