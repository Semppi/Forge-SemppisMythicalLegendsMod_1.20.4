package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.semppi.semppis_mythical_legends_mod.network.MapSnapshotClearPayload;
import net.semppi.semppis_mythical_legends_mod.network.SMLNetwork;
import net.semppi.semppis_mythical_legends_mod.network.ServerMapDiscoveryState;

/** Test and maintenance commands for the early SML map. */
public final class TestMapCommand {

    private TestMapCommand() {}

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher
    ) {
        dispatcher.register(
                Commands.literal("smlmap")
                        .then(Commands.literal("clear")
                                .executes(context -> clear(
                                        context.getSource()
                                ))
                        )
        );
    }

    private static int clear(CommandSourceStack source)
            throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        int removedPages = ServerMapDiscoveryState.clear(player);
        SMLNetwork.sendTo(player, new MapSnapshotClearPayload());

        source.sendSuccess(
                () -> Component.literal(
                        "Cleared SML test map data ("
                                + removedPages
                                + (removedPages == 1 ? " page)." : " pages).")
                ),
                false
        );
        return 1;
    }
}
