package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TransformCommand {
    private static final Map<String, String> ENTITY_NAME_MAP = new HashMap<>();

    static {
        ENTITY_NAME_MAP.put("alicanto", "semppis_mythical_legends_mod:alicanto");
        ENTITY_NAME_MAP.put("behemoth", "semppis_mythical_legends_mod:behemoth");
        ENTITY_NAME_MAP.put("colossal_lobster", "semppis_mythical_legends_mod:colossal_lobster");
        ENTITY_NAME_MAP.put("kraken", "semppis_mythical_legends_mod:kraken");
        ENTITY_NAME_MAP.put("loveland_frogman", "semppis_mythical_legends_mod:loveland_frogman");
        ENTITY_NAME_MAP.put("malphas", "semppis_mythical_legends_mod:malphas");
        ENTITY_NAME_MAP.put("mandrake", "semppis_mythical_legends_mod:mandrake");
        ENTITY_NAME_MAP.put("pukis", "semppis_mythical_legends_mod:pukis");
        ENTITY_NAME_MAP.put("satyr", "semppis_mythical_legends_mod:satyr");
        ENTITY_NAME_MAP.put("wendigo", "semppis_mythical_legends_mod:wendigo");

        ENTITY_NAME_MAP.put("allay", "minecraft:allay");
        //ENTITY_NAME_MAP.put("armadillo", "minecraft:armadillo");
        ENTITY_NAME_MAP.put("axolotl", "minecraft:axolotl");
        ENTITY_NAME_MAP.put("bat", "minecraft:bat");
        ENTITY_NAME_MAP.put("bee", "minecraft:bee");
        ENTITY_NAME_MAP.put("blaze", "minecraft:blaze");
        //ENTITY_NAME_MAP.put("bogged", "minecraft:bogged");
        //ENTITY_NAME_MAP.put("breeze", "minecraft:breeze");
        ENTITY_NAME_MAP.put("camel", "minecraft:camel");
        ENTITY_NAME_MAP.put("cat", "minecraft:cat");
        ENTITY_NAME_MAP.put("cave_spider", "minecraft:cave_spider");
        ENTITY_NAME_MAP.put("chicken", "minecraft:chicken");
        ENTITY_NAME_MAP.put("cod", "minecraft:cod");
        ENTITY_NAME_MAP.put("cow", "minecraft:cow");
        ENTITY_NAME_MAP.put("creeper", "minecraft:creeper");
        ENTITY_NAME_MAP.put("dolphin", "minecraft:dolphin");
        ENTITY_NAME_MAP.put("donkey", "minecraft:donkey");
        ENTITY_NAME_MAP.put("drowned", "minecraft:drowned");
        ENTITY_NAME_MAP.put("elder_guardian", "minecraft:elder_guardian");
        ENTITY_NAME_MAP.put("ender_dragon", "minecraft:ender_dragon");
        ENTITY_NAME_MAP.put("enderman", "minecraft:enderman");
        ENTITY_NAME_MAP.put("endermite", "minecraft:endermite");
        ENTITY_NAME_MAP.put("evoker", "minecraft:evoker");
        ENTITY_NAME_MAP.put("fox", "minecraft:fox");
        ENTITY_NAME_MAP.put("frog", "minecraft:frog");
        ENTITY_NAME_MAP.put("ghast", "minecraft:ghast");
        ENTITY_NAME_MAP.put("glow_squid", "minecraft:glow_squid");
        ENTITY_NAME_MAP.put("goat", "minecraft:goat");
        ENTITY_NAME_MAP.put("guardian", "minecraft:guardian");
        ENTITY_NAME_MAP.put("hoglin", "minecraft:hoglin");
        ENTITY_NAME_MAP.put("horse", "minecraft:horse");
        ENTITY_NAME_MAP.put("husk", "minecraft:husk");
        ENTITY_NAME_MAP.put("iron_golem", "minecraft:iron_golem");
        ENTITY_NAME_MAP.put("llama", "minecraft:llama");
        ENTITY_NAME_MAP.put("magma_cube", "minecraft:magma_cube");
        ENTITY_NAME_MAP.put("mooshroom", "minecraft:mooshroom");
        ENTITY_NAME_MAP.put("mule", "minecraft:mule");
        ENTITY_NAME_MAP.put("ocelot", "minecraft:ocelot");
        ENTITY_NAME_MAP.put("panda", "minecraft:panda");
        ENTITY_NAME_MAP.put("parrot", "minecraft:parrot");
        ENTITY_NAME_MAP.put("phantom", "minecraft:phantom");
        ENTITY_NAME_MAP.put("pig", "minecraft:pig");
        ENTITY_NAME_MAP.put("piglin", "minecraft:piglin");
        ENTITY_NAME_MAP.put("piglin_brute", "minecraft:piglin_brute");
        ENTITY_NAME_MAP.put("pillager", "minecraft:pillager");
        ENTITY_NAME_MAP.put("polar_bear", "minecraft:polar_bear");
        ENTITY_NAME_MAP.put("pufferfish", "minecraft:pufferfish");
        ENTITY_NAME_MAP.put("rabbit", "minecraft:rabbit");
        ENTITY_NAME_MAP.put("ravager", "minecraft:ravager");
        ENTITY_NAME_MAP.put("salmon", "minecraft:salmon");
        ENTITY_NAME_MAP.put("sheep", "minecraft:sheep");
        ENTITY_NAME_MAP.put("shulker", "minecraft:shulker");
        ENTITY_NAME_MAP.put("silverfish", "minecraft:silverfish");
        ENTITY_NAME_MAP.put("skeleton", "minecraft:skeleton");
        ENTITY_NAME_MAP.put("skeleton_horse", "minecraft:skeleton_horse");
        ENTITY_NAME_MAP.put("slime", "minecraft:slime");
        ENTITY_NAME_MAP.put("sniffer", "minecraft:sniffer");
        ENTITY_NAME_MAP.put("snow_golem", "minecraft:snow_golem");
        ENTITY_NAME_MAP.put("spider", "minecraft:spider");
        ENTITY_NAME_MAP.put("squid", "minecraft:squid");
        ENTITY_NAME_MAP.put("stray", "minecraft:stray");
        ENTITY_NAME_MAP.put("strider", "minecraft:strider");
        ENTITY_NAME_MAP.put("tadpole", "minecraft:tadpole");
        ENTITY_NAME_MAP.put("trader_llama", "minecraft:trader_llama");
        ENTITY_NAME_MAP.put("tropical_fish", "minecraft:tropical_fish");
        ENTITY_NAME_MAP.put("turtle", "minecraft:turtle");
        ENTITY_NAME_MAP.put("vex", "minecraft:vex");
        ENTITY_NAME_MAP.put("villager", "minecraft:villager");
        ENTITY_NAME_MAP.put("vindicator", "minecraft:vindicator");
        ENTITY_NAME_MAP.put("wandering_trader", "minecraft:wandering_trader");
        ENTITY_NAME_MAP.put("warden", "minecraft:warden");
        ENTITY_NAME_MAP.put("witch", "minecraft:witch");
        ENTITY_NAME_MAP.put("wither", "minecraft:wither");
        ENTITY_NAME_MAP.put("wither_skeleton", "minecraft:wither_skeleton");
        ENTITY_NAME_MAP.put("wolf", "minecraft:wolf");
        ENTITY_NAME_MAP.put("zoglin", "minecraft:zoglin");
        ENTITY_NAME_MAP.put("zombie", "minecraft:zombie");
        ENTITY_NAME_MAP.put("zombie_horse", "minecraft:zombie_horse");
        ENTITY_NAME_MAP.put("zombie_villager", "minecraft:zombie_villager");
        ENTITY_NAME_MAP.put("zombified_piglin", "minecraft:zombified_piglin");
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("transform")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(context.getSource().getServer().getPlayerNames(), builder))
                        .then(Commands.argument("entity", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    String input = builder.getRemaining().toLowerCase();
                                    return SharedSuggestionProvider.suggest(
                                            ENTITY_NAME_MAP.keySet().stream()
                                                    .filter(name -> name.startsWith(input))
                                                    .collect(Collectors.toList()),
                                            builder
                                    );
                                })
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String entityName = StringArgumentType.getString(context, "entity").toLowerCase();

                                    ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayerByName(playerName);
                                    if (player == null) {
                                        context.getSource().sendFailure(Component.literal("Player '" + playerName + "' not found."));
                                        return 0;
                                    }

                                    String fullEntityName = ENTITY_NAME_MAP.getOrDefault(entityName, entityName);

                                    Entity transformedEntity = TransformHelper.transformPlayer(player, fullEntityName);
                                    if (transformedEntity != null) {
                                        return 1; // Success
                                    } else {
                                        context.getSource().sendFailure(Component.literal("Failed to transform."));
                                        return 0; // Failure
                                    }
                                })
                        )
                )
        );
    }
}