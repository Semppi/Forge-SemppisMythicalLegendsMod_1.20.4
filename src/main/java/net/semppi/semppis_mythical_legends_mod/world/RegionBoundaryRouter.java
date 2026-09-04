package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Prepares short, deterministic Raw-border segments without placing a search
 * in the runtime lookup path. A successful tile contains exactly two owners
 * and one non-branching seam. Its replacement remains connected to Raw at
 * both ends and is published atomically; every other case publishes Raw.
 */
public final class RegionBoundaryRouter {
    private static final int TILE_QUARTS = 64;
    private static final int TILE_CELLS = TILE_QUARTS * TILE_QUARTS;
    private static final int MAX_SHIFT_QUARTS = 24;
    private static final int ENDPOINT_ROWS = 1;
    private static final int MAX_STEP_PER_ROW = 3;
    private static final int RAW_FALLBACK_COST = 24;
    private static final int TURN_COST = 3;
    private static final int RIVER_BONUS = 8;
    private static final int MIN_EDGE_PERCENT = 55;
    private static final int MAX_EDGE_GAP = 3;
    private static final int EDGE_GAP_COST = 32;
    private static final int INF = 1_000_000_000;
    private static final int MAX_CACHE_ENTRIES = 131_072;
    private static final int MAX_PREPARED_TILES = 8_192;

    private static final Map<ServerLevel, RouteCache> WORLD_CACHES =
            new WeakHashMap<>();

    private RegionBoundaryRouter() {}

    /** Runtime-safe: one bounded cache lookup, otherwise Raw. */
    public static Region resolvePreparedOrRaw(
            ServerLevelAccessor level, int x, int z,
            Holder<Biome> biome, Region rawOwner
    ) {
        Region prepared = cacheFor(level.getLevel()).get(
                cellKey(QuartPos.fromBlock(x), QuartPos.fromBlock(z))
        );
        return prepared == null ? rawOwner : prepared;
    }

    /**
     * Explicit preparation entry point used by diagnostics and, later, a
     * budgeted segment worker. It never runs through RegionGate.
     */
    public static Region prepareAndResolve(
            ServerLevelAccessor level, long seed, int x, int z,
            Holder<Biome> biome, Region rawOwner
    ) {
        int quartX = QuartPos.fromBlock(x);
        int quartZ = QuartPos.fromBlock(z);
        int tileX = Math.floorDiv(quartX, TILE_QUARTS);
        int tileZ = Math.floorDiv(quartZ, TILE_QUARTS);
        RouteCache cache = cacheFor(level.getLevel());
        long tileKey = cellKey(tileX, tileZ);
        long queryKey = cellKey(quartX, quartZ);

        if (!cache.isPrepared(tileKey) || cache.get(queryKey) == null) {
            PreparedTile prepared = prepareTile(level, tileX, tileZ);
            cache.publish(tileKey, prepared);
        }

        Region prepared = cache.get(queryKey);
        return prepared == null ? rawOwner : prepared;
    }

    private static PreparedTile prepareTile(
            ServerLevelAccessor level, int tileX, int tileZ
    ) {
        int originX = tileX * TILE_QUARTS;
        int originZ = tileZ * TILE_QUARTS;
        Region[] raw = new Region[TILE_CELLS];
        ResourceLocation[] biomes = new ResourceLocation[TILE_CELLS];
        boolean[] rivers = new boolean[TILE_CELLS];

        var chunkSource = level.getLevel().getChunkSource();
        var generator = chunkSource.getGenerator();
        var biomeSource = generator.getBiomeSource();
        var climateSampler = chunkSource.randomState().sampler();
        int quartY = QuartPos.fromBlock(generator.getSeaLevel());

        Region first = null;
        Region second = null;
        for (int localZ = 0; localZ < TILE_QUARTS; localZ++) {
            for (int localX = 0; localX < TILE_QUARTS; localX++) {
                int index = index(localX, localZ);
                int quartX = originX + localX;
                int quartZ = originZ + localZ;
                Region owner = ClimateDirectionAssignment.landRegion(
                        level, QuartPos.toBlock(quartX) + 2,
                        QuartPos.toBlock(quartZ) + 2
                );
                raw[index] = owner;
                if (!owner.ocean()) {
                    if (first == null) first = owner;
                    else if (!owner.equals(first) && second == null) {
                        second = owner;
                    } else if (!owner.equals(first)
                            && !owner.equals(second)) {
                        return PreparedTile.raw(originX, originZ, raw);
                    }
                }

                Holder<Biome> sample = biomeSource.getNoiseBiome(
                        quartX, quartY, quartZ, climateSampler
                );
                biomes[index] = sample.unwrapKey()
                        .map(key -> key.location()).orElse(null);
                rivers[index] = sample.is(BiomeTags.IS_RIVER);
            }
        }

        if (first == null || second == null) {
            return PreparedTile.raw(originX, originZ, raw);
        }

        Seam vertical = findVerticalSeam(raw, first, second);
        Seam horizontal = findHorizontalSeam(raw, first, second);
        if ((vertical == null) == (horizontal == null)) {
            return PreparedTile.raw(originX, originZ, raw);
        }

        Seam seam = vertical != null ? vertical : horizontal;
        EdgeIdentity edge = selectEdgeIdentity(seam, biomes);
        if (edge == null) {
            return PreparedTile.raw(originX, originZ, raw);
        }
        int startAnchor;
        int endAnchor;
        if (seam.vertical()) {
            startAnchor = sharedAnchor(
                    level, true, originX, originZ,
                    first, second, seam.raw()[0], edge, biomeSource,
                    climateSampler, quartY
            );
            endAnchor = sharedAnchor(
                    level, true, originX, originZ + TILE_QUARTS,
                    first, second, seam.raw()[TILE_QUARTS - 1], edge,
                    biomeSource, climateSampler, quartY
            );
        } else {
            startAnchor = sharedAnchor(
                    level, false, originZ, originX,
                    first, second, seam.raw()[0], edge, biomeSource,
                    climateSampler, quartY
            );
            endAnchor = sharedAnchor(
                    level, false, originZ, originX + TILE_QUARTS,
                    first, second, seam.raw()[TILE_QUARTS - 1], edge,
                    biomeSource, climateSampler, quartY
            );
        }
        seam = new Seam(
                seam.vertical(), seam.raw(), startAnchor, endAnchor
        );
        int[] route = route(seam, edge, biomes, rivers);
        if (route == null) {
            return PreparedTile.raw(originX, originZ, raw);
        }

        Region[] result = Arrays.copyOf(raw, raw.length);
        if (seam.vertical()) {
            for (int z = 0; z < TILE_QUARTS; z++) {
                Region left = raw[index(0, z)];
                Region right = raw[index(TILE_QUARTS - 1, z)];
                for (int x = 0; x < TILE_QUARTS; x++) {
                    Region owner = x <= route[z] ? left : right;
                    if (raw[index(x, z)].equals(first)
                            || raw[index(x, z)].equals(second)) {
                        result[index(x, z)] = owner;
                    }
                }
            }
        } else {
            for (int x = 0; x < TILE_QUARTS; x++) {
                Region top = raw[index(x, 0)];
                Region bottom = raw[index(x, TILE_QUARTS - 1)];
                for (int z = 0; z < TILE_QUARTS; z++) {
                    Region owner = z <= route[x] ? top : bottom;
                    if (raw[index(x, z)].equals(first)
                            || raw[index(x, z)].equals(second)) {
                        result[index(x, z)] = owner;
                    }
                }
            }
        }
        return new PreparedTile(originX, originZ, result);
    }

    private static Seam findVerticalSeam(
            Region[] raw, Region first, Region second
    ) {
        int[] positions = new int[TILE_QUARTS];
        for (int z = 0; z < TILE_QUARTS; z++) {
            Region left = raw[index(0, z)];
            Region right = raw[index(TILE_QUARTS - 1, z)];
            if (left.ocean() || right.ocean() || left.equals(right)
                    || !isPair(left, first, second)
                    || !isPair(right, first, second)) return null;

            int found = -1;
            for (int x = 0; x < TILE_QUARTS - 1; x++) {
                Region a = raw[index(x, z)];
                Region b = raw[index(x + 1, z)];
                if (!a.equals(b)) {
                    if (!isPair(a, first, second)
                            || !isPair(b, first, second)
                            || found >= 0) return null;
                    found = x;
                }
            }
            if (found < 0) return null;
            positions[z] = found;
        }
        return new Seam(true, positions);
    }

    private static Seam findHorizontalSeam(
            Region[] raw, Region first, Region second
    ) {
        int[] positions = new int[TILE_QUARTS];
        for (int x = 0; x < TILE_QUARTS; x++) {
            Region top = raw[index(x, 0)];
            Region bottom = raw[index(x, TILE_QUARTS - 1)];
            if (top.ocean() || bottom.ocean() || top.equals(bottom)
                    || !isPair(top, first, second)
                    || !isPair(bottom, first, second)) return null;

            int found = -1;
            for (int z = 0; z < TILE_QUARTS - 1; z++) {
                Region a = raw[index(x, z)];
                Region b = raw[index(x, z + 1)];
                if (!a.equals(b)) {
                    if (!isPair(a, first, second)
                            || !isPair(b, first, second)
                            || found >= 0) return null;
                    found = z;
                }
            }
            if (found < 0) return null;
            positions[x] = found;
        }
        return new Seam(false, positions);
    }

    /**
     * Chooses an endpoint from one canonical world-coordinate line. The tile
     * on either side of that line therefore receives exactly the same anchor,
     * independent of preparation order or which player opened a map first.
     */
    private static int sharedAnchor(
            ServerLevelAccessor level,
            boolean vertical,
            int varyingOrigin,
            int fixedCoordinate,
            Region first,
            Region second,
            int fallback,
            EdgeIdentity edge,
            net.minecraft.world.level.biome.BiomeSource biomeSource,
            net.minecraft.world.level.biome.Climate.Sampler climateSampler,
            int quartY
    ) {
        Region[] owners = new Region[TILE_QUARTS];
        ResourceLocation[] biomeIds = new ResourceLocation[TILE_QUARTS];
        boolean[] river = new boolean[TILE_QUARTS];
        for (int position = 0; position < TILE_QUARTS; position++) {
            int quartX = vertical
                    ? varyingOrigin + position : fixedCoordinate;
            int quartZ = vertical
                    ? fixedCoordinate : varyingOrigin + position;
            owners[position] = ClimateDirectionAssignment.landRegion(
                    level, QuartPos.toBlock(quartX) + 2,
                    QuartPos.toBlock(quartZ) + 2
            );
            Holder<Biome> sample = biomeSource.getNoiseBiome(
                    quartX, quartY, quartZ, climateSampler
            );
            biomeIds[position] = sample.unwrapKey()
                    .map(key -> key.location()).orElse(null);
            river[position] = sample.is(BiomeTags.IS_RIVER);
        }

        int rawTransition = -1;
        for (int position = 0; position < TILE_QUARTS - 1; position++) {
            Region a = owners[position];
            Region b = owners[position + 1];
            if (!a.equals(b)) {
                if (!isPair(a, first, second)
                        || !isPair(b, first, second)
                        || rawTransition >= 0) return fallback;
                rawTransition = position;
            }
        }
        if (rawTransition < 0) return fallback;

        int best = rawTransition;
        int bestCost = RAW_FALLBACK_COST;
        int minimum = Math.max(0, rawTransition - MAX_SHIFT_QUARTS);
        int maximum = Math.min(
                TILE_QUARTS - 2,
                rawTransition + MAX_SHIFT_QUARTS
        );
        for (int position = minimum; position <= maximum; position++) {
            ResourceLocation a = biomeIds[position];
            ResourceLocation b = biomeIds[position + 1];
            if (!edge.matches(a, b)) continue;

            int cost = Math.abs(position - rawTransition);
            if (river[position] || river[position + 1]) {
                cost = Math.max(0, cost - RIVER_BONUS);
            }
            if (cost < bestCost
                    || (cost == bestCost && position < best)) {
                best = position;
                bestCost = cost;
            }
        }
        return best;
    }

    private static EdgeIdentity selectEdgeIdentity(
            Seam seam, ResourceLocation[] biomes
    ) {
        Map<EdgeIdentity, EdgeScore> scores = new HashMap<>();
        for (int line = 0; line < TILE_QUARTS; line++) {
            int rawPosition = seam.raw()[line];
            int minimum = Math.max(0, rawPosition - MAX_SHIFT_QUARTS);
            int maximum = Math.min(
                    TILE_QUARTS - 2, rawPosition + MAX_SHIFT_QUARTS
            );
            Map<EdgeIdentity, Integer> closestOnLine = new HashMap<>();
            for (int position = minimum; position <= maximum; position++) {
                EdgeIdentity identity = edgeAt(
                        seam, line, position, biomes
                );
                if (identity == null) continue;
                int distance = Math.abs(position - rawPosition);
                closestOnLine.merge(identity, distance, Math::min);
            }
            for (Map.Entry<EdgeIdentity, Integer> candidate
                    : closestOnLine.entrySet()) {
                scores.computeIfAbsent(
                        candidate.getKey(), ignored -> new EdgeScore()
                ).add(candidate.getValue());
            }
        }

        EdgeIdentity best = null;
        EdgeScore bestScore = null;
        for (Map.Entry<EdgeIdentity, EdgeScore> candidate
                : scores.entrySet()) {
            if (bestScore == null
                    || candidate.getValue().betterThan(
                            bestScore, candidate.getKey(), best
                    )) {
                best = candidate.getKey();
                bestScore = candidate.getValue();
            }
        }
        return bestScore != null
                && bestScore.lines * 100
                >= TILE_QUARTS * MIN_EDGE_PERCENT ? best : null;
    }

    private static int[] route(
            Seam seam, EdgeIdentity edge,
            ResourceLocation[] biomes, boolean[] rivers
    ) {
        int positions = TILE_QUARTS - 1;
        int states = positions * (MAX_EDGE_GAP + 1);
        int[][] costs = new int[TILE_QUARTS][states];
        int[][] previous = new int[TILE_QUARTS][states];
        for (int[] row : costs) Arrays.fill(row, INF);
        for (int[] row : previous) Arrays.fill(row, -1);

        int start = seam.startAnchor();
        int startGap = edge.matchesAt(seam, 0, start, biomes) ? 0 : 1;
        costs[0][state(start, startGap)] = 0;
        for (int line = 1; line < TILE_QUARTS; line++) {
            int rawPosition = seam.raw()[line];
            boolean endpoint = line == TILE_QUARTS - 1;
            for (int position = 0; position < positions; position++) {
                if (Math.abs(position - rawPosition)
                        > MAX_SHIFT_QUARTS) continue;
                boolean matchingEdge = edge.matchesAt(
                        seam, line, position, biomes
                );
                if (endpoint && position != seam.endAnchor()) continue;

                int fromStart = Math.max(0, position - MAX_STEP_PER_ROW);
                int fromEnd = Math.min(
                        TILE_QUARTS - 2,
                        position + MAX_STEP_PER_ROW
                );
                for (int from = fromStart; from <= fromEnd; from++) {
                    for (int oldGap = 0; oldGap <= MAX_EDGE_GAP;
                         oldGap++) {
                        int fromState = state(from, oldGap);
                        if (costs[line - 1][fromState] >= INF) continue;
                        int gap = matchingEdge ? 0 : oldGap + 1;
                        if (gap > MAX_EDGE_GAP) {
                            continue;
                        }
                        int localCost = matchingEdge
                                ? edgeCost(seam, line, position,
                                        rawPosition, rivers)
                                : EDGE_GAP_COST
                                        + Math.abs(position - rawPosition);
                        int cost = costs[line - 1][fromState] + localCost
                                + Math.abs(position - from) * TURN_COST;
                        int targetState = state(position, gap);
                        if (cost < costs[line][targetState]) {
                            costs[line][targetState] = cost;
                            previous[line][targetState] = fromState;
                        }
                    }
                }
            }
        }

        int end = seam.endAnchor();
        int endState = -1;
        int endCost = INF;
        for (int gap = 0; gap <= MAX_EDGE_GAP; gap++) {
            int candidate = state(end, gap);
            if (costs[TILE_QUARTS - 1][candidate] < endCost) {
                endState = candidate;
                endCost = costs[TILE_QUARTS - 1][candidate];
            }
        }
        if (endState < 0) return null;
        int[] route = new int[TILE_QUARTS];
        route[TILE_QUARTS - 1] = end;
        int cursor = endState;
        for (int line = TILE_QUARTS - 1; line > 0; line--) {
            cursor = previous[line][cursor];
            if (cursor < 0) return null;
            route[line - 1] = cursor / (MAX_EDGE_GAP + 1);
        }

        int naturalEdges = 0;
        for (int line = ENDPOINT_ROWS;
             line < TILE_QUARTS - ENDPOINT_ROWS; line++) {
            if (edge.matchesAt(seam, line, route[line], biomes)) {
                naturalEdges++;
            }
        }
        int interior = TILE_QUARTS - ENDPOINT_ROWS * 2;
        return naturalEdges * 100 >= interior * MIN_EDGE_PERCENT
                ? route : null;
    }

    private static int edgeCost(
            Seam seam, int line, int position, int rawPosition,
            boolean[] rivers
    ) {
        int first = seam.vertical()
                ? index(position, line) : index(line, position);
        int second = seam.vertical()
                ? index(position + 1, line) : index(line, position + 1);
        int cost = Math.abs(position - rawPosition);
        if (rivers[first] || rivers[second]) {
            cost = Math.max(0, cost - RIVER_BONUS);
        }
        return cost;
    }

    private static EdgeIdentity edgeAt(
            Seam seam, int line, int position, ResourceLocation[] biomes
    ) {
        int first = seam.vertical()
                ? index(position, line) : index(line, position);
        int second = seam.vertical()
                ? index(position + 1, line) : index(line, position + 1);
        ResourceLocation a = biomes[first];
        ResourceLocation b = biomes[second];
        return EdgeIdentity.of(a, b);
    }

    private static int state(int position, int gap) {
        return position * (MAX_EDGE_GAP + 1) + gap;
    }

    private static boolean isPair(
            Region value, Region first, Region second
    ) {
        return value.equals(first) || value.equals(second);
    }

    private static int index(int localX, int localZ) {
        return localZ * TILE_QUARTS + localX;
    }

    private static RouteCache cacheFor(ServerLevel level) {
        synchronized (WORLD_CACHES) {
            return WORLD_CACHES.computeIfAbsent(
                    level, ignored -> new RouteCache()
            );
        }
    }

    private static long cellKey(int quartX, int quartZ) {
        return ((long) quartX << 32) ^ (quartZ & 0xFFFFFFFFL);
    }

    private record Seam(
            boolean vertical,
            int[] raw,
            int startAnchor,
            int endAnchor
    ) {
        private Seam(boolean vertical, int[] raw) {
            this(vertical, raw, raw[0], raw[raw.length - 1]);
        }
    }

    /** Unordered biome pair: direction changes do not change edge identity. */
    private record EdgeIdentity(ResourceLocation first,
                                ResourceLocation second) {
        private static EdgeIdentity of(
                ResourceLocation first, ResourceLocation second
        ) {
            if (first == null || second == null || first.equals(second)) {
                return null;
            }
            return first.toString().compareTo(second.toString()) <= 0
                    ? new EdgeIdentity(first, second)
                    : new EdgeIdentity(second, first);
        }

        private boolean matches(ResourceLocation a, ResourceLocation b) {
            return equals(of(a, b));
        }

        private boolean matchesAt(
                Seam seam, int line, int position,
                ResourceLocation[] biomes
        ) {
            return equals(edgeAt(seam, line, position, biomes));
        }

        private String stableKey() {
            return first + "|" + second;
        }
    }

    private static final class EdgeScore {
        private int lines;
        private int distance;

        private void add(int candidateDistance) {
            lines++;
            distance += candidateDistance;
        }

        private boolean betterThan(
                EdgeScore other, EdgeIdentity identity,
                EdgeIdentity otherIdentity
        ) {
            if (lines != other.lines) return lines > other.lines;
            if (distance != other.distance) return distance < other.distance;
            return identity.stableKey().compareTo(
                    otherIdentity.stableKey()
            ) < 0;
        }
    }

    private record PreparedTile(int originX, int originZ, Region[] regions) {
        private static PreparedTile raw(
                int originX, int originZ, Region[] raw
        ) {
            return new PreparedTile(
                    originX, originZ, Arrays.copyOf(raw, raw.length)
            );
        }
    }

    private static final class RouteCache {
        private final Map<Long, Region> cells =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, Region> eldest
                    ) {
                        return size() > MAX_CACHE_ENTRIES;
                    }
                };
        private final Map<Long, Boolean> preparedTiles =
                new LinkedHashMap<>(128, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, Boolean> eldest
                    ) {
                        return size() > MAX_PREPARED_TILES;
                    }
                };

        private synchronized Region get(long key) {
            return cells.get(key);
        }

        private synchronized boolean isPrepared(long tileKey) {
            return preparedTiles.containsKey(tileKey);
        }

        private synchronized void publish(
                long tileKey, PreparedTile prepared
        ) {
            for (int z = 0; z < TILE_QUARTS; z++) {
                for (int x = 0; x < TILE_QUARTS; x++) {
                    cells.put(
                            cellKey(prepared.originX() + x,
                                    prepared.originZ() + z),
                            prepared.regions()[index(x, z)]
                    );
                }
            }
            preparedTiles.put(tileKey, Boolean.TRUE);
        }
    }
}
