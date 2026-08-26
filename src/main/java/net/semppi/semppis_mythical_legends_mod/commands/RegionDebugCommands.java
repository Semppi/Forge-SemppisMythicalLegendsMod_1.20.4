package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionGate;
import net.semppi.semppis_mythical_legends_mod.world.AuthoritativeRegionSampler;
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
                            if (sample.kind()
                                    == RegionSurfaceClassifier.SurfaceKind.LAND
                                    || sample.kind()
                                    == RegionSurfaceClassifier.SurfaceKind.RIVER) {
                                AuthoritativeRegionSampler.ChildAdjacency adjacency =
                                        AuthoritativeRegionSampler.childAdjacency(
                                                source.getLevel().getSeed(),
                                                pos.getX(), pos.getZ()
                                );
                                message += adjacencyText(adjacency);
                            }
                            String finalMessage = message;
                            source.sendSuccess(
                                    () -> Component.literal(finalMessage), false
                            );
                            return 1;
                        })
        );
    }

    private static String adjacencyText(
            AuthoritativeRegionSampler.ChildAdjacency adjacency) {
        StringBuilder text = new StringBuilder()
                .append(" | Geometry cluster: ")
                .append(Long.toUnsignedString(adjacency.parentKey(), 16))
                .append(" | Child: ")
                .append(adjacency.childIndex() + 1)
                .append('/')
                .append(adjacency.childCount())
                .append(" | Adjacent: ");

        for (int index = 0; index < adjacency.neighbors().size(); index++) {
            AuthoritativeRegionSampler.ChildNeighbor neighbor =
                    adjacency.neighbors().get(index);
            if (index > 0) {
                text.append(", ");
            }
            text.append(neighbor.childIndex() + 1)
                    .append(':')
                    .append(neighbor.direction());
        }
        return text.toString();
    }
}
