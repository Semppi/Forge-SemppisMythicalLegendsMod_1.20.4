package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionGate;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionSurfaceClassifier;

/** Reports the exact same resolved sample used by spawning and F3 sync. */
public final class RegionDebugCommands {
    private RegionDebugCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("smlwhere")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();
                            if (source.getLevel().dimension() != Level.OVERWORLD) {
                                source.sendSuccess(
                                        () -> Component.literal(
                                                "Region: unavailable outside the Overworld"
                                        ),
                                        false
                                );
                                return 1;
                            }

                            BlockPos pos = BlockPos.containing(source.getPosition());
                            RegionSurfaceClassifier.Sample sample = RegionGate.resolve(
                                    source.getLevel(), pos.getX(), pos.getZ()
                            );
                            Region region = sample.region();

                            String message = "Region: " + region.display()
                                    + " | Surface: " + sample.kind()
                                    + " | X/Z: " + pos.getX() + "/" + pos.getZ();
                            source.sendSuccess(() -> Component.literal(message), false);
                            return 1;
                        })
        );
    }
}
