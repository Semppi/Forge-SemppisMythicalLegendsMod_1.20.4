package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.Heightmap;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.semppi.semppis_mythical_legends_mod.rules.SMLRules;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionGate;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionMobAllow;
import net.semppi.semppis_mythical_legends_mod.world.AuthoritativeRegionSampler;
import net.semppi.semppis_mythical_legends_mod.world.ClimateDirectionAssignment;
import net.semppi.semppis_mythical_legends_mod.world.Continent;
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

        dispatcher.register(
                Commands.literal("smlspawncheck")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument(
                                        "creature", StringArgumentType.word()
                                )
                                .executes(context -> runSpawnCheck(
                                        context.getSource(),
                                        StringArgumentType.getString(
                                                context, "creature"
                                        )
                                ))
                        )
        );
    }

    private static int runSpawnCheck(
            CommandSourceStack source, String creatureName) {
        if (source.getLevel().dimension() != Level.OVERWORLD) {
            source.sendFailure(Component.literal(
                    "Spawn check is unavailable outside the Overworld"
            ));
            return 0;
        }

        String normalized = creatureName.contains(":")
                ? creatureName
                : SemppisMythicalLegendsMod.MOD_ID + ":" + creatureName;
        ResourceLocation id = ResourceLocation.tryParse(normalized);
        EntityType<?> type = id == null
                ? null : ForgeRegistries.ENTITY_TYPES.getValue(id);
        if (type == null || id == null
                || !SemppisMythicalLegendsMod.MOD_ID.equals(
                        id.getNamespace()
                )) {
            source.sendFailure(Component.literal(
                    "Unknown SML creature: " + creatureName
            ));
            return 0;
        }

        BlockPos commandPos = BlockPos.containing(source.getPosition());
        int surfaceY = source.getLevel().getHeight(
                Heightmap.Types.WORLD_SURFACE,
                commandPos.getX(), commandPos.getZ()
        );
        BlockPos surfacePos = new BlockPos(
                commandPos.getX(), surfaceY, commandPos.getZ()
        );
        Holder<Biome> surfaceBiome = source.getLevel().getBiome(surfacePos);
        ResourceLocation biomeId = surfaceBiome
                .unwrapKey()
                .map(key -> key.location())
                .orElse(new ResourceLocation("sml", "unknown"));
        boolean biomeSpawnListed = surfaceBiome.value().getMobSettings()
                .getMobs(type.getCategory()).unwrap().stream()
                .anyMatch(entry -> entry.type == type);
        RegionSurfaceClassifier.Sample sample = RegionGate.resolve(
                source.getLevel(), surfacePos.getX(), surfacePos.getZ()
        );
        Region region = sample.region();
        boolean regionalMatch = region.ocean()
                ? RegionMobAllow.isAllowedForSea(type, region.sea())
                : RegionMobAllow.isAllowedForLand(
                        type, region.continent(), region.dir()
                );
        boolean gamerule = source.getLevel().getGameRules()
                .getBoolean(SMLRules.CONTINENTAL_SPAWNING);
        boolean effective = RegionGate.allows(
                source.getLevel(), type, surfacePos, MobSpawnType.NATURAL
        );

        source.sendSuccess(() -> Component.literal(
                "Spawn check: " + id
        ), false);
        source.sendSuccess(() -> Component.literal(
                "Biome: " + biomeId
                        + " | Surface: " + sample.kind()
                        + " | Region: " + region.display()
                        + " | X/Z: " + surfacePos.getX()
                        + "/" + surfacePos.getZ()
        ), false);
        source.sendSuccess(() -> Component.literal(
                "Biome spawn list: "
                        + (biomeSpawnListed ? "MATCH" : "NOT LISTED")
                        + " | Regional restriction: "
                        + yesNo(RegionMobAllow.hasRestriction(type))
                        + " | Region match: " + allowedBlocked(regionalMatch)
        ), false);
        source.sendSuccess(() -> Component.literal(
                "smlContinentalSpawning: " + gamerule
                        + " | Effective region gate: "
                        + allowedBlocked(effective)
                        + " | Biome/base spawn rules still apply"
        ), false);
        return 1;
    }

    private static String allowedBlocked(boolean value) {
        return value ? "ALLOWED" : "BLOCKED";
    }

    private static String yesNo(boolean value) {
        return value ? "YES" : "NO";
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
        Continent assignedContinent =
                ClimateDirectionAssignment.assignedContinent(
                        source.getLevel(), pos.getX(), pos.getZ()
                );
        ClimateDirectionAssignment.ContinentDecision continentDecision =
                ClimateDirectionAssignment.continentDecision(
                        source.getLevel(), pos.getX(), pos.getZ()
                );
        String continentText = assignedContinent == geometry.continent()
                ? continentName(assignedContinent)
                : continentName(assignedContinent) + "<-"
                + continentName(geometry.continent());
        source.sendSuccess(() -> Component.literal(
                "Climate cluster "
                        + Long.toUnsignedString(survey.parentKey(), 16)
                        + " | Continent: " + continentText
                        + " | Center: " + survey.parentCenterX() + "/"
                        + survey.parentCenterZ()
                        + " | Step: " + survey.sampleStep()
                        + " | Children sampled: " + survey.sampledChildren()
                        + "/" + survey.children().size()
                        + " | Rejection: "
                        + continentDecision.initialRejectedWeight()
                        + "->"
                        + continentDecision.assignedRejectedWeight()
                        + "/" + continentDecision.totalWeight()
        ), false);
        source.sendSuccess(() -> Component.literal(
                "Parent " + climateText(survey.parent())
        ), false);

        for (int index = 0; index < survey.children().size(); index++) {
            int childNumber = index + 1;
            MacroClimateSurvey.ClimateSummary child = survey.children().get(index);
            SubDir assigned = directions.get(index);
            SubDir initial = AuthoritativeRegionSampler.initialDirection(
                    survey.parentKey(), index, assignedContinent
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

    private static String continentName(Continent continent) {
        return continent == Continent.ANTARCTICA
                ? "FROZEN_POLE" : continent.toString();
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
