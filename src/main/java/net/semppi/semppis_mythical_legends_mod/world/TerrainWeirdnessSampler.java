package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.DensityFunction;

/**
 * Measures sustained extreme terrain weirdness without loading chunks. The
 * resulting 0-2 points are diagnostic data only and do not affect spawning.
 */
public final class TerrainWeirdnessSampler {
    private static final int SAMPLE_OFFSET = 192;
    private static final double HIGH_THRESHOLD = 0.90;
    private static final double EXTREME_THRESHOLD = 0.97;
    private static final int REQUIRED_SAMPLES = 5;

    private TerrainWeirdnessSampler() {}

    public static WeirdnessSample sample(
            ServerLevelAccessor level, int x, int z) {
        DensityFunction weirdness = level.getLevel().getChunkSource()
                .randomState().router().ridges();
        double center = compute(weirdness, x, z);
        double absoluteTotal = 0.0;
        int highSamples = 0;
        int extremeSamples = 0;

        for (int offsetZ = -SAMPLE_OFFSET;
             offsetZ <= SAMPLE_OFFSET; offsetZ += SAMPLE_OFFSET) {
            for (int offsetX = -SAMPLE_OFFSET;
                 offsetX <= SAMPLE_OFFSET; offsetX += SAMPLE_OFFSET) {
                double value = compute(
                        weirdness, x + offsetX, z + offsetZ
                );
                double absolute = Math.abs(value);
                absoluteTotal += absolute;
                if (absolute >= HIGH_THRESHOLD) highSamples++;
                if (absolute >= EXTREME_THRESHOLD) extremeSamples++;
            }
        }

        int points = extremeSamples >= REQUIRED_SAMPLES ? 2
                : highSamples >= REQUIRED_SAMPLES ? 1 : 0;
        return new WeirdnessSample(
                center, absoluteTotal / 9.0,
                highSamples, extremeSamples, points
        );
    }

    private static double compute(
            DensityFunction weirdness, int x, int z) {
        return weirdness.compute(
                new DensityFunction.SinglePointContext(x, 0, z)
        );
    }

    public record WeirdnessSample(
            double center,
            double meanAbsolute,
            int highSamples,
            int extremeSamples,
            int points
    ) {}
}
