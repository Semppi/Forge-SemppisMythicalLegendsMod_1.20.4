package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionGate;
import net.semppi.semppis_mythical_legends_mod.world.AuthoritativeRegionSampler;
import net.semppi.semppis_mythical_legends_mod.world.ClimateDirectionAssignment;
import net.semppi.semppis_mythical_legends_mod.world.MacroClimateSurvey;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionSurfaceClassifier;
import net.semppi.semppis_mythical_legends_mod.world.SubDir;

import java.util.List;

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
                                        ClimateDirectionAssignment.childAdjacency(
                                                source.getLevel(),
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

        dispatcher.register(
                Commands.literal("smlclimatesurvey")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> runClimateSurvey(context.getSource()))
        );
    }

    private static int runClimateSurvey(CommandSourceStack source) {
        if (source.getLevel().dimension() != Level.OVERWORLD) {
            source.sendFailure(Component.literal(
                    "Climate survey is unavailable outside the Overworld"
            ));
            return 0;
        }

        BlockPos pos = BlockPos.containing(source.getPosition());
        MacroClimateSurvey.ClusterSurvey survey = MacroClimateSurvey.survey(
                source.getLevel(), pos.getX(), pos.getZ()
        );
        AuthoritativeRegionSampler.GeometrySample geometry =
                AuthoritativeRegionSampler.geometrySample(
                        source.getLevel().getSeed(), pos.getX(), pos.getZ()
                );
        List<SubDir> directions =
                ClimateDirectionAssignment.assignedDirections(
                        source.getLevel(), pos.getX(), pos.getZ()
                );
        source.sendSuccess(() -> Component.literal(
                "Climate cluster "
                        + Long.toUnsignedString(survey.parentKey(), 16)
                        + " | Continent: " + geometry.continent()
                        + " | Center: " + survey.parentCenterX() + "/"
                        + survey.parentCenterZ()
                        + " | Step: " + survey.sampleStep()
                        + " | Children sampled: " + survey.sampledChildren()
                        + "/" + survey.children().size()
        ), false);
        source.sendSuccess(() -> Component.literal(
                "Parent " + climateText(survey.parent())
        ), false);

        for (int index = 0; index < survey.children().size(); index++) {
            int childNumber = index + 1;
            MacroClimateSurvey.ClimateSummary child = survey.children().get(index);
            SubDir assigned = directions.get(index);
            SubDir initial = AuthoritativeRegionSampler.initialDirection(
                    survey.parentKey(), index, geometry.continent()
            );
            String directionText = assigned == initial
                    ? assigned.toString()
                    : assigned + "<-" + initial;
            source.sendSuccess(() -> Component.literal(
                    "Child " + childNumber + " "
                            + directionText + " "
                            + climateText(child)
            ), false);
        }
        return 1;
    }

    private static String climateText(
            MacroClimateSurvey.ClimateSummary summary) {
        return "samples=" + summary.totalSamples()
                + ", votes=" + summary.votingSamples()
                + ", weight=" + summary.placementWeight()
                + ", climate=" + summary.dominantTemperature()
                + "/" + summary.dominantMoisture()
                + ", terrain=" + summary.dominantRole()
                + ", frozen-barren="
                + Math.round(summary.frozenBarrenShare() * 100.0) + "%"
                + ", forest/open=" + summary.forestSamples()
                + "/" + summary.openLowlandSamples()
                + ", context/mountain=" + summary.contextualSamples()
                + "/" + summary.mountainSamples();
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
