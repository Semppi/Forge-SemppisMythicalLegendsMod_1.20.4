package net.semppi.semppis_mythical_legends_mod.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties COD_SOUP = new FoodProperties.Builder().nutrition(8).saturationMod(9.6f).build();

    public static final FoodProperties BAKED_CHEESY_FISH = new FoodProperties.Builder().nutrition(14).saturationMod(15.4f).build();

    public static final FoodProperties PORRIDGE = new FoodProperties.Builder().nutrition(6).saturationMod(6.3f).build();

    public static final FoodProperties HONEYED_PORRIDGE = new FoodProperties.Builder().nutrition(12).saturationMod(7.5f).build();

    public static final FoodProperties CHOCOLATE_PORRIDGE = new FoodProperties.Builder().nutrition(8).saturationMod(8.7f).build();

    public static final FoodProperties MANDRAKE_BERRIES = new FoodProperties.Builder().nutrition(2).saturationMod(0.4f)
            .effect(() -> new MobEffectInstance(MobEffects.POISON, 280), 0.15f).build();

    public static final FoodProperties EDIBLE_LEAF = new FoodProperties.Builder().nutrition(2).saturationMod(0.6f).build();

    public static final FoodProperties CUCUMBER = new FoodProperties.Builder().nutrition(3).saturationMod(1.1f).build();

    public static final FoodProperties RICOTTA_CHEESE = new FoodProperties.Builder().nutrition(2).saturationMod(1.0f).build();

    public static final FoodProperties FRIED_EGG = new FoodProperties.Builder().nutrition(2).saturationMod(1.0f).build();

    public static final FoodProperties BUTTER = new FoodProperties.Builder().nutrition(1).saturationMod(0.6f).build();

    public static final FoodProperties OIL = new FoodProperties.Builder().nutrition(1).saturationMod(0.4f).build();

    public static final FoodProperties SWEET_BERRY_JAM = new FoodProperties.Builder().nutrition(6).saturationMod(1.7f).build();

    public static final FoodProperties CHOCOLATE_BUTTER = new FoodProperties.Builder().nutrition(2).saturationMod(2.4f).build();

    public static final FoodProperties NOPALE_PASTE = new FoodProperties.Builder().nutrition(2).saturationMod(1.4f).build();

    public static final FoodProperties SWEET_BERRY_JAM_ON_BREAD = new FoodProperties.Builder().nutrition(6).saturationMod(3.9f).build();

    public static final FoodProperties CHOCOLATE_BUTTER_ON_BREAD = new FoodProperties.Builder().nutrition(4).saturationMod(4.2f).build();

    public static final FoodProperties NOPALE_PASTE_ON_BREAD = new FoodProperties.Builder().nutrition(5).saturationMod(4.4f).build();

    public static final FoodProperties SPANAKOPITA = new FoodProperties.Builder().nutrition(5).saturationMod(5.5f).build();

    public static final FoodProperties COOKED_MUSHROOM = new FoodProperties.Builder().nutrition(3).saturationMod(3.6f).build();

    public static final FoodProperties HONEYED_MEAT_PIE = new FoodProperties.Builder().nutrition(5).saturationMod(4.5f).build();

    public static final FoodProperties HONEYED_BERRY_TREAT = new FoodProperties.Builder().nutrition(14).saturationMod(2.8f).build();

    public static final FoodProperties FISHY_KELP_TREAT = new FoodProperties.Builder().nutrition(9).saturationMod(3.1f).build();

    public static final FoodProperties COOKED_FISHY_KELP_TREAT = new FoodProperties.Builder().nutrition(20).saturationMod(21.3f).build();

    public static final FoodProperties VEGGIE_KELP_TREAT = new FoodProperties.Builder().nutrition(7).saturationMod(6.2f).build();

    public static final FoodProperties COOKED_VEGGIE_KELP_TREAT = new FoodProperties.Builder().nutrition(19).saturationMod(21.0f).build();

    public static final FoodProperties RAW_BEEF_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.7f).meat().build();

    public static final FoodProperties COOKED_STEAK_PIECE = new FoodProperties.Builder().nutrition(2).saturationMod(3.2f).meat().build();

    public static final FoodProperties RAW_BEEF_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties COOKED_STEAK_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties RAW_PORKCHOP_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.7f).meat().build();

    public static final FoodProperties COOKED_PORKCHOP_PIECE = new FoodProperties.Builder().nutrition(2).saturationMod(3.2f).meat().build();

    public static final FoodProperties RAW_PORKCHOP_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties COOKED_PORKCHOP_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties RAW_MUTTON_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.7f).meat().build();

    public static final FoodProperties COOKED_MUTTON_PIECE = new FoodProperties.Builder().nutrition(2).saturationMod(3.2f).meat().build();

    public static final FoodProperties RAW_MUTTON_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties COOKED_MUTTON_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties RAW_AVIAN_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 160), 0.2f).build();

    public static final FoodProperties COOKED_AVIAN_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(1.8f).meat().build();

    public static final FoodProperties RAW_AVIAN_MEAT = new FoodProperties.Builder().nutrition(2).saturationMod(1.2f).meat()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.3f).build();

    public static final FoodProperties COOKED_AVIAN_MEAT = new FoodProperties.Builder().nutrition(6).saturationMod(7.2f).meat().build();

    public static final FoodProperties RAW_AVIAN_CHUNK = new FoodProperties.Builder().meat()
            .effect(() -> new MobEffectInstance(MobEffects.HUNGER, 600), 0.3f).build();

    public static final FoodProperties COOKED_AVIAN_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties RAW_BUSHMEAT_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat().build();

    public static final FoodProperties COOKED_BUSHMEAT_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(1.6f).meat().build();

    public static final FoodProperties RAW_BUSHMEAT = new FoodProperties.Builder().nutrition(2).saturationMod(0.9f).meat().build();

    public static final FoodProperties COOKED_BUSHMEAT = new FoodProperties.Builder().nutrition(6).saturationMod(6.0f).meat().build();

    public static final FoodProperties RAW_BUSHMEAT_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties COOKED_BUSHMEAT_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties RAW_FISH_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat().build();

    public static final FoodProperties COOKED_FISH_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(1.2f).meat().build();

    public static final FoodProperties RAW_FISH_MEAT = new FoodProperties.Builder().nutrition(2).saturationMod(0.4f).meat().build();

    public static final FoodProperties COOKED_FISH_MEAT = new FoodProperties.Builder().nutrition(5).saturationMod(6.0f).meat().build();

    public static final FoodProperties RAW_FISH_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties COOKED_FISH_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties RAW_UNGULATE_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.7f).meat().build();

    public static final FoodProperties COOKED_UNGULATE_PIECE = new FoodProperties.Builder().nutrition(2).saturationMod(3.2f).meat().build();

    public static final FoodProperties RAW_UNGULATE_MEAT = new FoodProperties.Builder().nutrition(3).saturationMod(1.8f).meat().build();

    public static final FoodProperties COOKED_UNGULATE_MEAT = new FoodProperties.Builder().nutrition(8).saturationMod(12.8f).meat().build();

    public static final FoodProperties RAW_UNGULATE_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties COOKED_UNGULATE_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties RAW_AMPHIBIAN_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.2f).meat().build();

    public static final FoodProperties COOKED_AMPHIBIAN_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(1.2f).meat().build();

    public static final FoodProperties RAW_AMPHIBIAN_MEAT = new FoodProperties.Builder().nutrition(2).saturationMod(0.4f).meat().build();

    public static final FoodProperties COOKED_AMPHIBIAN_MEAT = new FoodProperties.Builder().nutrition(5).saturationMod(6.0f).meat().build();

    public static final FoodProperties RAW_AMPHIBIAN_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties COOKED_AMPHIBIAN_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties HUMANOID_FLESH_PIECE = new FoodProperties.Builder().nutrition(1).saturationMod(0.7f).meat().build();

    public static final FoodProperties HUMANOID_STEAK_PIECE = new FoodProperties.Builder().nutrition(2).saturationMod(3.2f).meat().build();

    public static final FoodProperties HUMANOID_FLESH = new FoodProperties.Builder().nutrition(3).saturationMod(1.6f).meat().build();

    public static final FoodProperties HUMANOID_STEAK = new FoodProperties.Builder().nutrition(7).saturationMod(10.3f).meat().build();

    public static final FoodProperties HUMANOID_FLESH_CHUNK = new FoodProperties.Builder().meat().build();

    public static final FoodProperties HUMANOID_STEAK_CHUNK = new FoodProperties.Builder().meat().build();
}
