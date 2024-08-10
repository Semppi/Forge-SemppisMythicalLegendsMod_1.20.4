package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Arrays;

public class TransformCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("transform")
                .requires(source -> source.hasPermission(2))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(context.getSource().getServer().getPlayerNames(), builder))
                        .then(Commands.argument("entity", StringArgumentType.greedyString())
                                .suggests((context, builder) -> SharedSuggestionProvider.suggest(Arrays.asList(
                                        "semppis_mythical_legends_mod:satyr",
                                        "semppis_mythical_legends_mod:colossal_lobster",
                                        "semppis_mythical_legends_mod:behemoth",
                                        "semppis_mythical_legends_mod:pukis",
                                        "semppis_mythical_legends_mod:mandrake",
                                        "semppis_mythical_legends_mod:wendigo",
                                        "semppis_mythical_legends_mod:loveland_frogman",
                                        "semppis_mythical_legends_mod:kraken",
                                        "semppis_mythical_legends_mod:malphas"), builder))
                                .executes(context -> {
                                    String playerName = StringArgumentType.getString(context, "player");
                                    String entityName = StringArgumentType.getString(context, "entity");
                                    ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayerByName(playerName);
                                    if (player == null) {
                                        context.getSource().sendFailure(Component.literal("Player '" + playerName + "' not found."));
                                        return 0;
                                    }

                                    Entity transformedEntity = TransformHelper.transformPlayer(player, entityName);
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