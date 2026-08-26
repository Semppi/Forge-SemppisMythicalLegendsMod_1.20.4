package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.LinkedHashMap;
import java.util.Map;

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

    // Vanilla shores are normally narrow, so this deliberately stays much
    // tighter than the sea-coast search. Nearest rings vote first, allowing a
    // long beach to change territory when the adjacent inland region changes.
    private static final int[] SHORE_STEPS = {16, 32, 48, 64, 80, 96};
    private static final double[][] INHERITANCE_DIRECTIONS = {
            { 1.0,  0.0}, { 0.70710678118,  0.70710678118},
            { 0.0,  1.0}, {-0.70710678118,  0.70710678118},
            {-1.0,  0.0}, {-0.70710678118, -0.70710678118},
            { 0.0, -1.0}, { 0.70710678118, -0.70710678118}
    };

    private RegionSurfaceClassifier() {}

    public enum SurfaceKind {
        LAND,
        RIVER,
        SHORE,
        COAST,
        OCEAN
    }

    public record Sample(SurfaceKind kind, Region region) {}

    private record CoastMatch(Region region) {}

    private record InheritanceVote(int score, int support) {
        private InheritanceVote add(int weight) {
            return new InheritanceVote(score + weight, support + 1);
        }
    }

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

        if (kind == SurfaceKind.SHORE) {
            Region inherited = findShoreLand(level, seed, x, z);
            if (inherited != null) {
                return new Sample(SurfaceKind.SHORE, inherited);
            }

            // Defensive fallback for unusually wide modded beaches. It keeps
            // every coordinate deterministic without letting the shore itself
            // pull a continental boundary.
            return new Sample(
                    SurfaceKind.SHORE, SAMPLER.landRegion(seed, x, z)
            );
        }

        // Rivers use the unmodified local land overlay under their own
        // coordinates. A surface land biome may nudge a nearby existing
        // boundary, but it can never select a region from scratch.
        Region landRegion = SAMPLER.landRegion(seed, x, z);
        if (kind == SurfaceKind.LAND) {
            landRegion = BoundedBiomeBorderAttractor.attract(
                    level, seed, x, z, biome, landRegion
            );
            landRegion = BoundedBiomeComponentResolver.resolve(
                    level, seed, x, z, biome, landRegion
            );
        }
        return new Sample(kind, landRegion);
    }

    public static SurfaceKind classify(Holder<Biome> biome) {
        if (biome.is(BiomeTags.IS_RIVER)) {
            return SurfaceKind.RIVER;
        }
        if (biome.is(BiomeTags.IS_BEACH)) {
            return SurfaceKind.SHORE;
        }
        if (biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_DEEP_OCEAN)) {
            return SurfaceKind.OCEAN;
        }

        // Mushroom islands and unknown modded biomes remain land unless their
        // biome tags say otherwise.
        return SurfaceKind.LAND;
    }

    /**
     * Inherits a shore from a bounded nearest band of genuine inland samples.
     * A region needs support from at least three probes, so one changing ray can
     * no longer create a thin bite or isolated shore pixel.
     */
    private static Region findShoreLand(ServerLevelAccessor level, long seed,
                                        int shoreX, int shoreZ) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        Map<Region, InheritanceVote> votes = new LinkedHashMap<>();
        for (int ring = 0; ring < SHORE_STEPS.length; ring++) {
            int step = SHORE_STEPS[ring];
            int weight = SHORE_STEPS.length - ring;

            for (double[] direction : INHERITANCE_DIRECTIONS) {
                int landX = offset(shoreX, direction[0], step);
                int landZ = offset(shoreZ, direction[1], step);
                Holder<Biome> candidate = biomeSource.getNoiseBiome(
                        QuartPos.fromBlock(landX), quartY,
                        QuartPos.fromBlock(landZ), climateSampler
                );
                if (!isLandCandidate(candidate)) {
                    continue;
                }

                Region landRegion = resolveLandRegion(
                        level, seed, landX, landZ, candidate
                );
                addVote(votes, landRegion, weight);
            }

            Region winner = selectSupportedWinner(votes, 3);
            if (winner != null) {
                return winner;
            }
        }
        // A very small island may never expose three inland probes. Two
        // agreeing samples are still safer than falling back to an unrelated
        // raw boundary beneath the beach.
        return selectSupportedWinner(votes, 2);
    }

    private static CoastMatch findCoast(ServerLevelAccessor level, long seed, int waterX, int waterZ) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        // Nearer rings carry more voting weight, while three agreeing probes are
        // required. This keeps a coast attached to nearby land without the old
        // first-hit ray producing stripes, freckles or one-pixel islands.
        Map<Region, InheritanceVote> votes = new LinkedHashMap<>();
        for (int ring = 0; ring < COAST_STEPS.length; ring++) {
            int step = COAST_STEPS[ring];
            int weight = COAST_STEPS.length - ring;

            for (double[] direction : INHERITANCE_DIRECTIONS) {
                int landX = offset(waterX, direction[0], step);
                int landZ = offset(waterZ, direction[1], step);
                Holder<Biome> candidate = biomeSource.getNoiseBiome(
                        QuartPos.fromBlock(landX),
                        quartY,
                        QuartPos.fromBlock(landZ),
                        climateSampler
                );

                if (!isLandCandidate(candidate)) {
                    continue;
                }

                Region landRegion = resolveLandRegion(
                        level, seed, landX, landZ, candidate
                );
                int reach = coastReach(seed, landRegion);
                long dx = (long) landX - waterX;
                long dz = (long) landZ - waterZ;
                if (dx * dx + dz * dz <= (long) reach * reach) {
                    addVote(votes, landRegion, weight);
                }
            }

            Region winner = selectSupportedWinner(votes, 3);
            if (winner != null) {
                return new CoastMatch(winner);
            }
        }
        return null;
    }

    private static Region resolveLandRegion(
            ServerLevelAccessor level, long seed, int x, int z,
            Holder<Biome> biome) {
        Region region = SAMPLER.landRegion(seed, x, z);
        region = BoundedBiomeBorderAttractor.attract(
                level, seed, x, z, biome, region
        );
        return BoundedBiomeComponentResolver.resolve(
                level, seed, x, z, biome, region
        );
    }

    private static void addVote(
            Map<Region, InheritanceVote> votes, Region region, int weight) {
        votes.compute(
                region,
                (ignored, vote) -> vote == null
                        ? new InheritanceVote(weight, 1)
                        : vote.add(weight)
        );
    }

    /** Stable insertion order resolves an exact score/support tie. */
    private static Region selectSupportedWinner(
            Map<Region, InheritanceVote> votes, int minimumSupport) {
        Region winner = null;
        InheritanceVote best = null;

        for (Map.Entry<Region, InheritanceVote> entry : votes.entrySet()) {
            InheritanceVote vote = entry.getValue();
            if (vote.support() < minimumSupport) {
                continue;
            }
            if (best == null
                    || vote.score() > best.score()
                    || (vote.score() == best.score()
                    && vote.support() > best.support())) {
                winner = entry.getKey();
                best = vote;
            }
        }
        return winner;
    }

    private static boolean isLandCandidate(Holder<Biome> biome) {
        return !biome.is(BiomeTags.IS_RIVER)
                && !biome.is(BiomeTags.IS_BEACH)
                && !biome.is(BiomeTags.IS_OCEAN)
                && !biome.is(BiomeTags.IS_DEEP_OCEAN);
    }

    private static int offset(int origin, double direction, int distance) {
        return origin + (int) Math.round(direction * distance);
    }

    /**
     * Stable small/medium/large coast widths for each regional identity. A
     * nearby probe can no longer change width merely by crossing an unrelated
     * 256-block hash cell. Maximum reach stays below 200 blocks.
     */
    private static int coastReach(long seed, Region region) {
        long hash = seed
                ^ ((long) region.continent().ordinal() * 0x9E3779B97F4A7C15L)
                ^ ((long) region.dir().ordinal() * 0xC2B2AE3D27D4EB4FL);
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
