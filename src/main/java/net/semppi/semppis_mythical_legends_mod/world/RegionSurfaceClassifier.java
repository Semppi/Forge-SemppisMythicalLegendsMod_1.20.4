package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

/**
 * Classifies the Overworld surface before choosing a continental or ocean
 * label. Player height and underground cave biomes never affect the result.
 */
public final class RegionSurfaceClassifier {
    private static final RegionSampler SAMPLER = new RegionSampler();

    // Coast searches are deliberately sparse and bounded. They query the
    // generator's biome source, not neighboring chunks, so exploration order
    // and which chunks happen to be loaded cannot alter a result.
    private static final int[] COAST_STEPS = {32, 64, 96, 128, 160, 192};
    private static final int[][] COAST_DIRECTIONS = {
            { 1,  0}, { 1,  1}, { 0,  1}, {-1,  1},
            {-1,  0}, {-1, -1}, { 0, -1}, { 1, -1}
    };

    private RegionSurfaceClassifier() {}

    public enum SurfaceKind {
        LAND,
        RIVER,
        COAST,
        OCEAN
    }

    public record Sample(SurfaceKind kind, Region region) {}

    private record CoastMatch(Region region) {}

    public static Sample sample(ServerLevelAccessor level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
        y = Math.max(level.getMinBuildHeight(), Math.min(y, level.getMaxBuildHeight() - 1));

        Holder<Biome> biome = level.getBiome(new BlockPos(x, y, z));
        return sampleBiome(level, x, z, biome);
    }

    /**
     * Diagnostic-map lookup of the pristine generated surface. This uses the
     * chunk generator and biome source directly, so mapping distant positions
     * does not load or generate those chunks.
     */
    public static Sample sampleGenerated(ServerLevelAccessor level, int x, int z) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var randomState = chunkSource.randomState();
        int y = generator.getBaseHeight(
                x, z, Heightmap.Types.WORLD_SURFACE,
                level, randomState
        );
        y = Math.max(level.getMinBuildHeight(), Math.min(y, level.getMaxBuildHeight() - 1));

        Holder<Biome> biome = generator.getBiomeSource().getNoiseBiome(
                QuartPos.fromBlock(x), QuartPos.fromBlock(y),
                QuartPos.fromBlock(z), randomState.sampler()
        );
        return sampleBiome(level, x, z, biome);
    }

    private static Sample sampleBiome(ServerLevelAccessor level, int x, int z,
                                      Holder<Biome> biome) {
        SurfaceKind kind = classify(biome);
        long seed = level.getLevel().getSeed();

        if (kind == SurfaceKind.OCEAN) {
            // Deep-ocean biomes anchor the true ocean basins. Only ordinary
            // ocean may become a coastal sea belonging to nearby land.
            if (!biome.is(BiomeTags.IS_DEEP_OCEAN)) {
                CoastMatch coast = findCoast(level, seed, x, z);
                if (coast != null) {
                    return new Sample(SurfaceKind.COAST, coast.region());
                }
            }
            return new Sample(SurfaceKind.OCEAN, SAMPLER.seaRegion(seed, x, z));
        }

        // Rivers use the unmodified local land overlay under their own
        // coordinates. A surface land biome may nudge a nearby existing
        // boundary, but it can never select a region from scratch.
        Region landRegion = SAMPLER.landRegion(seed, x, z);
        if (kind == SurfaceKind.LAND) {
            landRegion = BoundedBiomeBorderAttractor.attract(
                    level, seed, x, z, biome, landRegion
            );
        }
        return new Sample(kind, landRegion);
    }

    public static SurfaceKind classify(Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_RIVER)) {
            return SurfaceKind.RIVER;
        }
        if (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_DEEP_OCEAN)) {
            return SurfaceKind.OCEAN;
        }

        // Beaches, snowy beaches, stony shores, mushroom islands and unknown
        // modded biomes remain land unless their biome tags say otherwise.
        return SurfaceKind.LAND;
    }

    private static CoastMatch findCoast(ServerLevelAccessor level, long seed, int waterX, int waterZ) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        // Rings are examined nearest first. A coastal tag therefore comes from
        // the nearest sampled land, preventing one continent from projecting a
        // coast across another continent's shore.
        for (int step : COAST_STEPS) {
            for (int[] direction : COAST_DIRECTIONS) {
                int landX = waterX + direction[0] * step;
                int landZ = waterZ + direction[1] * step;
                Holder<Biome> candidate = biomeSource.getNoiseBiome(
                        QuartPos.fromBlock(landX),
                        quartY,
                        QuartPos.fromBlock(landZ),
                        climateSampler
                );

                if (!isLandCandidate(candidate)) {
                    continue;
                }

                Region landRegion = SAMPLER.landRegion(seed, landX, landZ);
                int reach = coastReach(seed, landX, landZ);
                long dx = (long) landX - waterX;
                long dz = (long) landZ - waterZ;
                if (dx * dx + dz * dz <= (long) reach * reach) {
                    return new CoastMatch(landRegion);
                }
            }
        }
        return null;
    }

    private static boolean isLandCandidate(Holder<Biome> biome) {
        return !biome.is(BiomeTags.IS_RIVER)
                && !biome.is(BiomeTags.IS_OCEAN)
                && !biome.is(BiomeTags.IS_DEEP_OCEAN);
    }

    /**
     * Stable small/medium/large coast widths. The 256-block anchor prevents
     * noisy per-block width changes while still allowing different shores to
     * have different buffers. Maximum reach stays below 200 blocks.
     */
    private static int coastReach(long seed, int landX, int landZ) {
        long hash = seed
                ^ ((long) (landX >> 8) * 0x9E3779B97F4A7C15L)
                ^ ((long) (landZ >> 8) * 0xC2B2AE3D27D4EB4FL);
        hash = mix64(hash);
        return switch ((int) Math.floorMod(hash, 3L)) {
            case 0 -> 96;
            case 1 -> 144;
            default -> 192;
        };
    }

    private static long mix64(long value) {
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
}
