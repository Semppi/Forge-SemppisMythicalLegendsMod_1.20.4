package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public class CancelTransformCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("canceltransform")
                .requires(source -> source.hasPermission(2))
                .executes(context -> executeCancelTransform(context, null)) // Handle command without player name
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(context.getSource().getOnlinePlayerNames(), builder))
                        .executes(context -> executeCancelTransform(context, StringArgumentType.getString(context, "player")))
                )
        );
    }

    private static int executeCancelTransform(CommandContext<CommandSourceStack> context, @Nullable String playerName) throws CommandSyntaxException {
        ServerPlayer player;
        if (playerName == null) {
            // If no player name is provided, get the player issuing the command
            player = context.getSource().getPlayerOrException();
        } else {
            // Otherwise, get the specified player
            player = context.getSource().getServer().getPlayerList().getPlayerByName(playerName);
            if (player == null) {
                context.getSource().sendFailure(Component.literal("Player not found."));
                return 0; // Player not found
            }
        }

        if (TransformHelper.isPlayerTransformed(player.getUUID())) {
            TransformHelper.revertTransformation(player);
            context.getSource().sendSuccess(() -> Component.literal("Transformation for " + player.getScoreboardName() + " has been cancelled."), true);
            return 1; // Command was successful
        } else {
            context.getSource().sendFailure(Component.literal(player.getScoreboardName() + " is not currently transformed."));
            return 0; // Player is not transformed
        }
    }
}