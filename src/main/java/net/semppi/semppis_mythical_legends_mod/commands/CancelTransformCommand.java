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

public class CancelTransformCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("canceltransform")
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> executeCancelTransform(ctx, null))
                .then(Commands.argument("player", StringArgumentType.word())
                        .suggests((ctx, b) -> SharedSuggestionProvider.suggest(
                                ctx.getSource().getServer().getPlayerNames(), b))
                        .executes(ctx -> executeCancelTransform(
                                ctx, StringArgumentType.getString(ctx, "player"))
                        )
                )
        );
    }

    private static int executeCancelTransform(
            CommandContext<CommandSourceStack> context,
            String playerName
    ) throws CommandSyntaxException {
        ServerPlayer player;
        if (playerName == null) {
            player = context.getSource().getPlayerOrException();
        } else {
            player = context.getSource()
                    .getServer()
                    .getPlayerList()
                    .getPlayerByName(playerName);
            if (player == null) {
                context.getSource().sendFailure(
                        Component.literal("Player not found."));
                return 0;
            }
        }

        if (TransformHelper.isPlayerTransformed(player)) {
            TransformHelper.revertTransformation(player);
            context.getSource().sendSuccess(
                    () -> Component.literal(
                            "Transformation for " + player.getScoreboardName() +
                                    " has been cancelled."),
                    true
            );
            return 1;
        } else {
            context.getSource().sendFailure(
                    Component.literal(player.getScoreboardName() +
                            " is not currently transformed."));
            return 0;
        }
    }
}