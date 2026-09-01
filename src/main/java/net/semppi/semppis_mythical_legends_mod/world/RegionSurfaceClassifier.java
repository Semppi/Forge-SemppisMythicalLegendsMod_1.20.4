package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

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
    private static final int COAST_NEIGHBOR_OFFSET = 4;
    private static final int MIN_COAST_NEIGHBORHOOD_SUPPORT = 3;
    private static final int MIN_COAST_REGION_SUPPORT = 2;
    private static final int MIN_POTENTIAL_COAST_SUPPORT = 2;
    private static final int MIN_DETACHED_COAST_COMPONENT_SIZE = 12;
    private static final int MAX_ENCLOSED_OCEAN_HOLE_SIZE = 8;
    private static final int MIN_OCEAN_HOLE_COAST_SUPPORT = 2;
    private static final int MAX_COAST_COMPONENT_CACHE_ENTRIES = 65_536;
    private static final int MAX_RAW_COAST_CACHE_ENTRIES = 65_536;
    private static final int[][] COAST_NEIGHBORS = {
            {0, 0},
            {COAST_NEIGHBOR_OFFSET, 0},
            {-COAST_NEIGHBOR_OFFSET, 0},
            {0, COAST_NEIGHBOR_OFFSET},
            {0, -COAST_NEIGHBOR_OFFSET}
    };
    private static final Map<ServerLevel, RawCoastCache> RAW_COAST_CACHES =
            new WeakHashMap<>();
    private static final Map<ServerLevel, CoastComponentCache>
            COAST_COMPONENT_CACHES = new WeakHashMap<>();

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

    private record RawCoastResult(
            CoastMatch match,
            int potentialSupport
    ) {}

    private record CoastCell(int quartX, int quartZ) {}

    private record InheritanceVote(
            int score,
            int support,
            int directionMask
    ) {
        private InheritanceVote add(int weight, int directionIndex) {
            return new InheritanceVote(
                    score + weight,
                    support + 1,
                    directionMask | 1 << directionIndex
            );
        }

        private int directionSupport() {
            return Integer.bitCount(directionMask);
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
                    SurfaceKind.SHORE, SAMPLER.landRegion(level, x, z)
            );
        }

        // Rivers use the unmodified local land overlay under their own
        // coordinates. A surface land biome may nudge a nearby existing
        // boundary, but it can never select a region from scratch.
        Region landRegion = SAMPLER.landRegion(level, x, z);
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
     * A region needs support from at least three probes, reducing thin bites
     * and isolated shore pixels.
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

            for (int directionIndex = 0;
                 directionIndex < INHERITANCE_DIRECTIONS.length;
                 directionIndex++) {
                double[] direction = INHERITANCE_DIRECTIONS[directionIndex];
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
                addVote(votes, landRegion, weight, directionIndex);
            }

            Region winner = selectSupportedWinner(votes, 3, 1);
            if (winner != null) {
                return winner;
            }
        }
        // A very small island may never expose three inland probes. Two
        // agreeing samples are still safer than falling back to an unrelated
        // raw boundary beneath the beach.
        return selectSupportedWinner(votes, 2, 1);
    }

    private static CoastMatch findCoast(
            ServerLevelAccessor level,
            long seed,
            int waterX,
            int waterZ
    ) {
        int quartX = QuartPos.fromBlock(waterX);
        int quartZ = QuartPos.fromBlock(waterZ);
        long key = cellKey(quartX, quartZ);
        CoastComponentCache cache = coastComponentCache(level.getLevel());
        CoastMatch cached = cache.get(key);
        if (cached != CoastComponentCache.NOT_CACHED) {
            return cached;
        }

        CoastMatch candidate = findSmoothedCoast(
                level, seed, quartX * 4 + 2, quartZ * 4 + 2
        );
        if (candidate == null) {
            return fillEnclosedOceanHole(
                    level, seed, new CoastCell(quartX, quartZ), cache
            );
        }

        return retainConnectedCoastComponent(
                level, seed, new CoastCell(quartX, quartZ), candidate, cache
        ) ? candidate : null;
    }

    /**
     * Applies a small cross-shaped majority filter to the raw coast field.
     * This removes isolated coast cells and fills isolated ocean holes without
     * moving an ordinary continuous boundary by more than one biome cell.
     */
    private static CoastMatch findSmoothedCoast(
            ServerLevelAccessor level,
            long seed,
            int waterX,
            int waterZ
    ) {
        RawCoastResult center = rawCoast(
                level, seed, waterX, waterZ
        );
        if (center.match() == null
                && center.potentialSupport()
                < MIN_POTENTIAL_COAST_SUPPORT) {
            return null;
        }

        int coastSupport = 0;
        Map<Region, Integer> regionSupport = new LinkedHashMap<>();
        for (int[] offset : COAST_NEIGHBORS) {
            if ((offset[0] != 0 || offset[1] != 0)
                    && !isGeneratedOrdinaryOcean(
                            level,
                            waterX + offset[0],
                            waterZ + offset[1]
                    )) {
                continue;
            }
            RawCoastResult neighbor = offset[0] == 0 && offset[1] == 0
                    ? center
                    : rawCoast(
                            level,
                            seed,
                            waterX + offset[0],
                            waterZ + offset[1]
                    );
            if (neighbor.match() == null) {
                continue;
            }
            coastSupport++;
            regionSupport.merge(
                    neighbor.match().region(), 1, Integer::sum
            );
        }

        if (coastSupport < MIN_COAST_NEIGHBORHOOD_SUPPORT) {
            return null;
        }
        if (center.match() != null) {
            return center.match();
        }

        Region winner = null;
        int winnerSupport = 0;
        for (Map.Entry<Region, Integer> entry : regionSupport.entrySet()) {
            if (entry.getValue() > winnerSupport) {
                winner = entry.getKey();
                winnerSupport = entry.getValue();
            }
        }
        return winner == null || winnerSupport < MIN_COAST_REGION_SUPPORT
                ? null
                : new CoastMatch(winner);
    }

    /**
     * Rejects a small coast-colored component floating in open water. A
     * component is retained immediately when it reaches genuine land/shore,
     * or when it is large enough that it is no longer a map-scale freckle.
     * The bounded search prevents this validation from walking an entire
     * coastline when opening the diagnostic map.
     */
    private static boolean retainConnectedCoastComponent(
            ServerLevelAccessor level,
            long seed,
            CoastCell origin,
            CoastMatch originMatch,
            CoastComponentCache cache
    ) {
        ArrayDeque<CoastCell> pending = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        pending.add(origin);

        boolean retain = false;
        while (!pending.isEmpty()) {
            CoastCell cell = pending.removeFirst();
            long key = cellKey(cell.quartX(), cell.quartZ());
            if (!visited.add(key)) {
                continue;
            }

            int x = cell.quartX() * 4 + 2;
            int z = cell.quartZ() * 4 + 2;
            if (touchesGeneratedLandOrShore(level, x, z)
                    || visited.size() >= MIN_DETACHED_COAST_COMPONENT_SIZE) {
                retain = true;
                break;
            }

            for (int direction = 1; direction < COAST_NEIGHBORS.length;
                 direction++) {
                int neighborX = x + COAST_NEIGHBORS[direction][0];
                int neighborZ = z + COAST_NEIGHBORS[direction][1];
                long neighborKey = cellKey(
                        QuartPos.fromBlock(neighborX),
                        QuartPos.fromBlock(neighborZ)
                );
                if (visited.contains(neighborKey)
                        || !isGeneratedOrdinaryOcean(
                                level, neighborX, neighborZ
                        )) {
                    continue;
                }
                CoastMatch neighbor = findSmoothedCoast(
                        level, seed, neighborX, neighborZ
                );
                if (neighbor != null
                        && neighbor.region().equals(originMatch.region())) {
                    pending.addLast(new CoastCell(
                            QuartPos.fromBlock(neighborX),
                            QuartPos.fromBlock(neighborZ)
                    ));
                }
            }
        }

        for (long key : visited) {
            cache.put(key, retain ? originMatch : null);
        }
        return retain;
    }

    /**
     * Fills only a small, fully bounded pocket in the smoothed coast field.
     * Reaching deep ocean, more than eight cells, or two different coastal
     * regions proves that the pocket is an inlet/boundary rather than an
     * isolated ocean freckle and leaves it untouched.
     */
    private static CoastMatch fillEnclosedOceanHole(
            ServerLevelAccessor level,
            long seed,
            CoastCell origin,
            CoastComponentCache cache
    ) {
        ArrayDeque<CoastCell> pending = new ArrayDeque<>();
        Set<Long> visited = new HashSet<>();
        pending.add(origin);

        Region surroundingRegion = null;
        int coastSupport = 0;
        boolean enclosed = true;

        while (!pending.isEmpty() && enclosed) {
            CoastCell cell = pending.removeFirst();
            long key = cellKey(cell.quartX(), cell.quartZ());
            if (!visited.add(key)) {
                continue;
            }
            if (visited.size() > MAX_ENCLOSED_OCEAN_HOLE_SIZE) {
                enclosed = false;
                break;
            }

            int x = cell.quartX() * 4 + 2;
            int z = cell.quartZ() * 4 + 2;
            for (int direction = 1; direction < COAST_NEIGHBORS.length;
                 direction++) {
                int neighborX = x + COAST_NEIGHBORS[direction][0];
                int neighborZ = z + COAST_NEIGHBORS[direction][1];
                long neighborKey = cellKey(
                        QuartPos.fromBlock(neighborX),
                        QuartPos.fromBlock(neighborZ)
                );
                if (visited.contains(neighborKey)) {
                    continue;
                }

                Holder<Biome> biome = generatedSeaLevelBiome(
                        level, neighborX, neighborZ
                );
                if (biome.is(BiomeTags.IS_DEEP_OCEAN)) {
                    enclosed = false;
                    break;
                }
                if (!biome.is(BiomeTags.IS_OCEAN)) {
                    SurfaceKind kind = classify(biome);
                    if (isGeneratedSubmerged(level, neighborX, neighborZ)
                            && (kind == SurfaceKind.SHORE
                            || kind == SurfaceKind.RIVER)) {
                        Region shallowWaterRegion = kind == SurfaceKind.SHORE
                                ? findShoreLand(
                                        level, seed, neighborX, neighborZ
                                )
                                : SAMPLER.landRegion(
                                        level, neighborX, neighborZ
                                );
                        if (shallowWaterRegion != null) {
                            coastSupport++;
                            if (surroundingRegion == null) {
                                surroundingRegion = shallowWaterRegion;
                            } else if (!surroundingRegion.equals(
                                    shallowWaterRegion
                            )) {
                                enclosed = false;
                                break;
                            }
                        }
                    }
                    // Dry land closes the pocket. Submerged beach and river
                    // also close it, but contribute their continental region
                    // so a gulf cannot manufacture an ocean-tag island.
                    continue;
                }

                CoastMatch neighbor = findSmoothedCoast(
                        level, seed, neighborX, neighborZ
                );
                if (neighbor == null) {
                    pending.addLast(new CoastCell(
                            QuartPos.fromBlock(neighborX),
                            QuartPos.fromBlock(neighborZ)
                    ));
                    continue;
                }

                coastSupport++;
                if (surroundingRegion == null) {
                    surroundingRegion = neighbor.region();
                } else if (!surroundingRegion.equals(neighbor.region())) {
                    enclosed = false;
                    break;
                }
            }
        }

        CoastMatch result = enclosed
                && surroundingRegion != null
                && coastSupport >= MIN_OCEAN_HOLE_COAST_SUPPORT
                ? new CoastMatch(surroundingRegion)
                : null;
        for (long key : visited) {
            cache.put(key, result);
        }
        return result;
    }

    private static boolean touchesGeneratedLandOrShore(
            ServerLevelAccessor level,
            int x,
            int z
    ) {
        for (int direction = 1; direction < COAST_NEIGHBORS.length;
             direction++) {
            Holder<Biome> biome = generatedSeaLevelBiome(
                    level,
                    x + COAST_NEIGHBORS[direction][0],
                    z + COAST_NEIGHBORS[direction][1]
            );
            SurfaceKind kind = classify(biome);
            if ((kind == SurfaceKind.LAND || kind == SurfaceKind.SHORE)
                    && !isGeneratedSubmerged(
                            level,
                            x + COAST_NEIGHBORS[direction][0],
                            z + COAST_NEIGHBORS[direction][1]
                    )) {
                return true;
            }
        }
        return false;
    }

    private static boolean isGeneratedSubmerged(
            ServerLevelAccessor level,
            int x,
            int z
    ) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        int oceanFloor = generator.getBaseHeight(
                x,
                z,
                Heightmap.Types.OCEAN_FLOOR_WG,
                level,
                chunkSource.randomState()
        );
        return oceanFloor < generator.getSeaLevel();
    }

    private static boolean isGeneratedOrdinaryOcean(
            ServerLevelAccessor level,
            int x,
            int z
    ) {
        Holder<Biome> biome = generatedSeaLevelBiome(level, x, z);
        return biome.is(BiomeTags.IS_OCEAN)
                && !biome.is(BiomeTags.IS_DEEP_OCEAN);
    }

    private static Holder<Biome> generatedSeaLevelBiome(
            ServerLevelAccessor level,
            int x,
            int z
    ) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        return generator.getBiomeSource().getNoiseBiome(
                QuartPos.fromBlock(x),
                QuartPos.fromBlock(generator.getSeaLevel()),
                QuartPos.fromBlock(z),
                chunkSource.randomState().sampler()
        );
    }

    private static RawCoastResult rawCoast(
            ServerLevelAccessor level,
            long seed,
            int waterX,
            int waterZ
    ) {
        int quartX = QuartPos.fromBlock(waterX);
        int quartZ = QuartPos.fromBlock(waterZ);
        long key = cellKey(quartX, quartZ);
        RawCoastCache cache = rawCoastCache(level.getLevel());
        RawCoastResult cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        // Canonical cell centers prevent lookup order from changing which
        // coordinate represents a four-block biome cell.
        int canonicalX = quartX * 4 + 2;
        int canonicalZ = quartZ * 4 + 2;
        RawCoastResult computed = calculateRawCoast(
                level, seed, canonicalX, canonicalZ
        );
        return cache.putIfAbsent(key, computed);
    }

    private static RawCoastResult calculateRawCoast(
            ServerLevelAccessor level,
            long seed,
            int waterX,
            int waterZ
    ) {
        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        // Nearer rings carry more voting weight. A coast needs three agreeing
        // probes spread across at least two directions, preventing one ray
        // from producing stripes, freckles or one-pixel islands.
        Map<Region, InheritanceVote> votes = new LinkedHashMap<>();
        for (int ring = 0; ring < COAST_STEPS.length; ring++) {
            int step = COAST_STEPS[ring];
            int weight = COAST_STEPS.length - ring;

            for (int directionIndex = 0;
                 directionIndex < INHERITANCE_DIRECTIONS.length;
                 directionIndex++) {
                double[] direction = INHERITANCE_DIRECTIONS[directionIndex];
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
                    addVote(
                            votes,
                            landRegion,
                            weight,
                            directionIndex
                    );
                }
            }

            // Repeated hits along one ray no longer manufacture a narrow
            // coastal finger or isolated freckle in otherwise open water.
            Region winner = selectSupportedWinner(votes, 3, 2);
            if (winner != null) {
                return new RawCoastResult(
                        new CoastMatch(winner),
                        maximumPotentialSupport(votes)
                );
            }
        }
        return new RawCoastResult(
                null, maximumPotentialSupport(votes)
        );
    }

    private static int maximumPotentialSupport(
            Map<Region, InheritanceVote> votes
    ) {
        int support = 0;
        for (InheritanceVote vote : votes.values()) {
            support = Math.max(support, vote.support());
        }
        return support;
    }

    private static RawCoastCache rawCoastCache(ServerLevel level) {
        synchronized (RAW_COAST_CACHES) {
            return RAW_COAST_CACHES.computeIfAbsent(
                    level, ignored -> new RawCoastCache()
            );
        }
    }

    private static CoastComponentCache coastComponentCache(ServerLevel level) {
        synchronized (COAST_COMPONENT_CACHES) {
            return COAST_COMPONENT_CACHES.computeIfAbsent(
                    level, ignored -> new CoastComponentCache()
            );
        }
    }

    private static long cellKey(int quartX, int quartZ) {
        return ((long) quartX << 32) ^ (quartZ & 0xFFFFFFFFL);
    }

    private static Region resolveLandRegion(
            ServerLevelAccessor level, long seed, int x, int z,
            Holder<Biome> biome) {
        Region region = SAMPLER.landRegion(level, x, z);
        region = BoundedBiomeBorderAttractor.attract(
                level, seed, x, z, biome, region
        );
        return BoundedBiomeComponentResolver.resolve(
                level, seed, x, z, biome, region
        );
    }

    private static void addVote(
            Map<Region, InheritanceVote> votes,
            Region region,
            int weight,
            int directionIndex
    ) {
        votes.compute(
                region,
                (ignored, vote) -> vote == null
                        ? new InheritanceVote(
                                weight,
                                1,
                                1 << directionIndex
                        )
                        : vote.add(weight, directionIndex)
        );
    }

    /** Stable insertion order resolves an exact score/support tie. */
    private static Region selectSupportedWinner(
            Map<Region, InheritanceVote> votes,
            int minimumSupport,
            int minimumDirectionSupport
    ) {
        Region winner = null;
        InheritanceVote best = null;

        for (Map.Entry<Region, InheritanceVote> entry : votes.entrySet()) {
            InheritanceVote vote = entry.getValue();
            if (vote.support() < minimumSupport
                    || vote.directionSupport() < minimumDirectionSupport) {
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

    private static final class RawCoastCache {
        private final Map<Long, RawCoastResult> values =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, RawCoastResult> eldest
                    ) {
                        return size() > MAX_RAW_COAST_CACHE_ENTRIES;
                    }
                };

        private synchronized RawCoastResult get(long key) {
            return values.get(key);
        }

        private synchronized RawCoastResult putIfAbsent(
                long key, RawCoastResult value
        ) {
            RawCoastResult existing = values.get(key);
            if (existing != null) {
                return existing;
            }
            values.put(key, value);
            return value;
        }
    }

    private static final class CoastComponentCache {
        private static final CoastMatch NOT_CACHED = new CoastMatch(null);
        private final Map<Long, CoastMatch> values =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, CoastMatch> eldest
                    ) {
                        return size() > MAX_COAST_COMPONENT_CACHE_ENTRIES;
                    }
                };

        private synchronized CoastMatch get(long key) {
            return values.containsKey(key) ? values.get(key) : NOT_CACHED;
        }

        private synchronized void put(long key, CoastMatch value) {
            values.put(key, value);
        }
    }
}
