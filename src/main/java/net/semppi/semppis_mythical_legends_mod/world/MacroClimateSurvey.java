package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Sparse, no-chunk-load climate survey of the final warped parent and child
 * candidate shapes. The result is descriptive only; label assignment remains
 * unchanged until the next overhaul goal.
 */
public final class MacroClimateSurvey {
    // 32 samples cross the 49,152-block survey square. Roughly half belong to
    // the selected Voronoi parent, providing dozens of samples per child
    // without turning first-time region discovery into a map-sized scan.
    private static final int SAMPLE_STEP = 1_536;
    private static final int SURVEY_RADIUS =
            AuthoritativeRegionSampler.CONTINENT_SCALE;
    private static final int WORLD_LIMIT = 29_999_984;

    private static final Map<Level, Map<Long, ClusterSurvey>> CACHE =
            new WeakHashMap<>();

    private MacroClimateSurvey() {}

    public static ClusterSurvey survey(ServerLevelAccessor level, int x, int z) {
        long seed = level.getLevel().getSeed();
        AuthoritativeRegionSampler.GeometrySample origin =
                AuthoritativeRegionSampler.geometrySample(seed, x, z);

        synchronized (CACHE) {
            ClusterSurvey cached = CACHE
                    .computeIfAbsent(level.getLevel(), ignored -> new LinkedHashMap<>())
                    .get(origin.parentKey());
            if (cached != null) {
                return cached;
            }
        }

        ClusterSurvey result = compute(level, seed, origin);
        synchronized (CACHE) {
            return CACHE
                    .computeIfAbsent(level.getLevel(), ignored -> new LinkedHashMap<>())
                    .computeIfAbsent(origin.parentKey(), ignored -> result);
        }
    }

    private static ClusterSurvey compute(
            ServerLevelAccessor level, long seed,
            AuthoritativeRegionSampler.GeometrySample origin) {
        Accumulator parent = new Accumulator();
        List<Accumulator> children = new ArrayList<>(origin.childCount());
        for (int child = 0; child < origin.childCount(); child++) {
            children.add(new Accumulator());
        }

        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var randomState = chunkSource.randomState();

        int minX = clampToWorld((long) origin.parentCenterX() - SURVEY_RADIUS);
        int maxX = clampToWorld((long) origin.parentCenterX() + SURVEY_RADIUS);
        int minZ = clampToWorld((long) origin.parentCenterZ() - SURVEY_RADIUS);
        int maxZ = clampToWorld((long) origin.parentCenterZ() + SURVEY_RADIUS);

        for (long sampleZLong = (long) minZ + SAMPLE_STEP / 2;
             sampleZLong < maxZ; sampleZLong += SAMPLE_STEP) {
            int sampleZ = (int) sampleZLong;
            for (long sampleXLong = (long) minX + SAMPLE_STEP / 2;
                 sampleXLong < maxX; sampleXLong += SAMPLE_STEP) {
                int sampleX = (int) sampleXLong;
                AuthoritativeRegionSampler.GeometrySample geometry =
                        AuthoritativeRegionSampler.geometrySample(
                                seed, sampleX, sampleZ
                );
                if (geometry.parentKey() != origin.parentKey()) {
                    continue;
                }

                int surfaceY = generator.getBaseHeight(
                        sampleX, sampleZ, Heightmap.Types.WORLD_SURFACE,
                        level, randomState
                );
                surfaceY = Math.max(
                        level.getMinBuildHeight(),
                        Math.min(surfaceY, level.getMaxBuildHeight() - 1)
                );
                Holder<Biome> biome = biomeSource.getNoiseBiome(
                        QuartPos.fromBlock(sampleX),
                        QuartPos.fromBlock(surfaceY),
                        QuartPos.fromBlock(sampleZ),
                        randomState.sampler()
                );
                ResourceLocation biomeId = biome.unwrapKey()
                        .map(key -> key.location())
                        .orElse(null);
                TagRules.BiomeClimateProfile profile = biomeId == null
                        ? TagRules.biomeProfile(
                                new ResourceLocation("sml", "unknown")
                        )
                        : TagRules.biomeProfile(biomeId);

                parent.add(profile);
                children.get(geometry.childIndex()).add(profile);
            }
        }

        List<ClimateSummary> childResults = children.stream()
                .map(Accumulator::finish)
                .toList();
        int sampledChildren = (int) childResults.stream()
                .filter(summary -> summary.totalSamples() > 0)
                .count();
        return new ClusterSurvey(
                origin.parentKey(), origin.parentCenterX(),
                origin.parentCenterZ(), parent.finish(),
                childResults, sampledChildren, SAMPLE_STEP
        );
    }

    private static int clampToWorld(long coordinate) {
        return (int) Math.max(-WORLD_LIMIT, Math.min(WORLD_LIMIT, coordinate));
    }

    public record ClusterSurvey(
            long parentKey,
            int parentCenterX,
            int parentCenterZ,
            ClimateSummary parent,
            List<ClimateSummary> children,
            int sampledChildren,
            int sampleStep
    ) {
        public ClusterSurvey {
            children = List.copyOf(children);
        }
    }

    public record ClimateSummary(
            int totalSamples,
            int votingSamples,
            int placementWeight,
            Map<TagRules.TemperatureBand, Integer> temperatureEvidence,
            Map<TagRules.MoistureBand, Integer> moistureEvidence,
            Map<TagRules.BiomeRole, Integer> roleSamples,
            Map<TagRules.Portability, Integer> portabilitySamples,
            int frozenBarrenSamples,
            int forestSamples,
            int openLowlandSamples,
            int contextualSamples,
            int mountainSamples
    ) {
        public ClimateSummary {
            temperatureEvidence = Map.copyOf(temperatureEvidence);
            moistureEvidence = Map.copyOf(moistureEvidence);
            roleSamples = Map.copyOf(roleSamples);
            portabilitySamples = Map.copyOf(portabilitySamples);
        }

        public TagRules.TemperatureBand dominantTemperature() {
            return dominant(temperatureEvidence, TagRules.TemperatureBand.NEUTRAL);
        }

        public TagRules.MoistureBand dominantMoisture() {
            return dominant(moistureEvidence, TagRules.MoistureBand.NEUTRAL);
        }

        public TagRules.BiomeRole dominantRole() {
            TagRules.BiomeRole best = TagRules.BiomeRole.UNKNOWN;
            int bestValue = 0;
            for (Map.Entry<TagRules.BiomeRole, Integer> entry
                    : roleSamples.entrySet()) {
                TagRules.BiomeRole role = entry.getKey();
                if (role == TagRules.BiomeRole.MOUNTAIN
                        || role == TagRules.BiomeRole.RIVER_CONTEXT
                        || role == TagRules.BiomeRole.SHORE_CONTEXT
                        || role == TagRules.BiomeRole.OCEAN_CONTEXT
                        || role == TagRules.BiomeRole.CAVE_CONTEXT
                        || role == TagRules.BiomeRole.SPECIAL_NEUTRAL
                        || role == TagRules.BiomeRole.UNKNOWN) {
                    continue;
                }
                if (entry.getValue() > bestValue) {
                    best = role;
                    bestValue = entry.getValue();
                }
            }
            return best;
        }

        public double frozenBarrenShare() {
            int lowlandSamples = totalSamples
                    - contextualSamples - mountainSamples;
            return lowlandSamples <= 0 ? 0.0
                    : (double) frozenBarrenSamples / lowlandSamples;
        }

        private static <E extends Enum<E>> E dominant(
                Map<E, Integer> values, E fallback) {
            E best = fallback;
            int bestValue = 0;
            for (Map.Entry<E, Integer> entry : values.entrySet()) {
                if (entry.getValue() > bestValue) {
                    best = entry.getKey();
                    bestValue = entry.getValue();
                }
            }
            return best;
        }
    }

    private static final class Accumulator {
        private int totalSamples;
        private int votingSamples;
        private int placementWeight;
        private int frozenBarrenSamples;
        private int forestSamples;
        private int openLowlandSamples;
        private int contextualSamples;
        private int mountainSamples;
        private final EnumMap<TagRules.TemperatureBand, Integer> temperatures =
                new EnumMap<>(TagRules.TemperatureBand.class);
        private final EnumMap<TagRules.MoistureBand, Integer> moisture =
                new EnumMap<>(TagRules.MoistureBand.class);
        private final EnumMap<TagRules.BiomeRole, Integer> roles =
                new EnumMap<>(TagRules.BiomeRole.class);
        private final EnumMap<TagRules.Portability, Integer> portability =
                new EnumMap<>(TagRules.Portability.class);

        private void add(TagRules.BiomeClimateProfile profile) {
            totalSamples++;
            roles.merge(profile.role(), 1, Integer::sum);
            portability.merge(profile.portability(), 1, Integer::sum);

            if (profile.isPlacementContext()) contextualSamples++;
            if (profile.isMountain()) mountainSamples++;
            if (profile.role() == TagRules.BiomeRole.FOREST) forestSamples++;
            if (profile.role() == TagRules.BiomeRole.OPEN_LOWLAND) {
                openLowlandSamples++;
            }
            if (profile.role() == TagRules.BiomeRole.BARREN_LOWLAND
                    && profile.temperature()
                    == TagRules.TemperatureBand.FROZEN) {
                frozenBarrenSamples++;
            }

            int weight = profile.placementWeight();
            if (weight <= 0) return;
            votingSamples++;
            placementWeight += weight;
            temperatures.merge(profile.temperature(), weight, Integer::sum);
            moisture.merge(profile.moisture(), weight, Integer::sum);
        }

        private ClimateSummary finish() {
            return new ClimateSummary(
                    totalSamples, votingSamples, placementWeight,
                    temperatures, moisture, roles, portability,
                    frozenBarrenSamples, forestSamples, openLowlandSamples,
                    contextualSamples, mountainSamples
            );
        }
    }
}
