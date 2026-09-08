package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Prepares bounded deterministic border routes without searching from the
 * runtime lookup path. A tile publishes either one complete route or Raw.
 */
public final class RegionBoundaryRouter {
    private static final Logger LOGGER = LogManager.getLogger();
    static final int TILE_QUARTS = 64;
    private static final int TILE_CELLS = TILE_QUARTS * TILE_QUARTS;
    private static final int PORTAL_REACH_QUARTS = 40;
    private static final int PORTAL_RIVER_BONUS = 8;
    private static final int PORTAL_LOOKAHEAD_DEPTH = 4;
    private static final int PORTAL_LOOKAHEAD_SEARCH = 4;
    private static final int PORTAL_LOOKAHEAD_WEIGHT = 8;
    private static final int MAX_CACHE_ENTRIES = 131_072;
    private static final int MAX_PREPARED_TILES = 8_192;
    private static final long CAPTURE_BUDGET_NANOS = 1_000_000L;
    private static final int MAX_CAPTURE_SAMPLES_PER_TICK = 32;
    private static final Map<ServerLevel, RouteCache> WORLD_CACHES =
            new WeakHashMap<>();
    private static final Map<MinecraftServer, ArrayDeque<CaptureJob>>
            CAPTURE_QUEUES = new WeakHashMap<>();
    private static final ExecutorService ROUTE_WORKER =
            Executors.newSingleThreadExecutor(task -> {
                Thread thread = new Thread(task, "sml-border-router");
                thread.setDaemon(true);
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            });

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

    /** Explicit preparation used by diagnostics and a future budgeted worker. */
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
            cache.publish(tileKey, prepareTile(level, tileX, tileZ));
        }
        Region prepared = cache.get(queryKey);
        return prepared == null ? rawOwner : prepared;
    }

    /**
     * Captures world inputs on the server thread, runs only the detached graph
     * search on one low-priority worker, and atomically publishes back on the
     * server thread. Players requesting the same tile share one preparation.
     */
    public static void prepareForMap(
            ServerLevel level, int x, int z, Runnable completion
    ) {
        int tileX = Math.floorDiv(
                QuartPos.fromBlock(x), TILE_QUARTS
        );
        int tileZ = Math.floorDiv(
                QuartPos.fromBlock(z), TILE_QUARTS
        );
        long tileKey = cellKey(tileX, tileZ);
        RouteCache cache = cacheFor(level);
        if (cache.isPrepared(tileKey)) {
            completion.run();
            return;
        }
        if (!cache.beginPreparation(tileKey, completion)) {
            return;
        }

        synchronized (CAPTURE_QUEUES) {
            CAPTURE_QUEUES.computeIfAbsent(
                    level.getServer(), ignored -> new ArrayDeque<>()
            ).addLast(new CaptureJob(
                    level, tileX, tileZ, tileKey, cache
            ));
        }
    }

    /** Runs a strictly budgeted slice of diagnostic world capture each tick. */
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        CaptureJob job;
        synchronized (CAPTURE_QUEUES) {
            ArrayDeque<CaptureJob> queue = CAPTURE_QUEUES.get(
                    event.getServer()
            );
            job = queue == null ? null : queue.peekFirst();
        }
        if (job == null) return;

        long deadline = System.nanoTime() + CAPTURE_BUDGET_NANOS;
        CapturedTile captured;
        try {
            captured = job.captureSlice(
                    deadline, MAX_CAPTURE_SAMPLES_PER_TICK
            );
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to capture SML border tile", exception);
            job.cache().failPreparation(job.tileKey());
            removeCaptureJob(event.getServer(), job);
            return;
        }
        if (captured == null) return;
        removeCaptureJob(event.getServer(), job);
        routeDetached(job, captured);
    }

    private static void removeCaptureJob(
            MinecraftServer server, CaptureJob job
    ) {
        synchronized (CAPTURE_QUEUES) {
            ArrayDeque<CaptureJob> queue = CAPTURE_QUEUES.get(server);
            if (queue == null) return;
            queue.remove(job);
            if (queue.isEmpty()) CAPTURE_QUEUES.remove(server);
        }
    }

    private static void routeDetached(
            CaptureJob job, CapturedTile captured
    ) {
        ROUTE_WORKER.execute(() -> {
            PreparedTile prepared;
            long routeStarted = System.nanoTime();
            try {
                prepared = routeCaptured(captured);
            } catch (RuntimeException exception) {
                LOGGER.error(
                        "Failed to route captured SML border tile",
                        exception
                );
                job.level().getServer().execute(() ->
                        job.cache().failPreparation(job.tileKey())
                );
                return;
            }
            long routeMillis = elapsedMillis(routeStarted);
            job.level().getServer().execute(() -> {
                job.cache().publish(job.tileKey(), prepared);
                LOGGER.info(
                        "Final border preparation timing: {} ms capture, "
                                + "{} ms detached route",
                        job.elapsedMillis(), routeMillis
                );
                job.cache().completePreparation(job.tileKey());
            });
        });
    }

    private static PreparedTile prepareTile(
            ServerLevelAccessor level, int tileX, int tileZ
    ) {
        return routeCaptured(captureTile(level, tileX, tileZ));
    }

    private static CapturedTile captureTile(
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
        boolean tooManyRegions = false;
        for (int z = 0; z < TILE_QUARTS; z++) {
            for (int x = 0; x < TILE_QUARTS; x++) {
                int index = index(x, z);
                int quartX = originX + x;
                int quartZ = originZ + z;
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
                        tooManyRegions = true;
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
        if (tooManyRegions) {
            return CapturedTile.raw(
                    originX, originZ, raw, biomes, rivers,
                    TileResult.TOO_MANY_REGIONS
            );
        }
        if (first == null || second == null) {
            return CapturedTile.raw(
                    originX, originZ, raw, biomes, rivers,
                    TileResult.NO_RAW_BOUNDARY
            );
        }
        BoundedRegionPathRouter.RawPortals rawPortals =
                BoundedRegionPathRouter.rawPortals(raw, first, second);
        if (rawPortals == null) {
            return CapturedTile.raw(
                    originX, originZ, raw, biomes, rivers,
                    TileResult.BRANCHED_OR_DISCONNECTED_RAW
            );
        }
        BoundedRegionPathRouter.Portal startPortal = sharedPortal(
                level, originX, originZ, rawPortals.start(),
                first, second, biomeSource, climateSampler, quartY
        );
        BoundedRegionPathRouter.Portal endPortal = sharedPortal(
                level, originX, originZ, rawPortals.end(),
                first, second, biomeSource, climateSampler, quartY
        );
        return new CapturedTile(
                originX, originZ, raw, biomes, rivers, first, second,
                rawPortals.start(), rawPortals.end(),
                startPortal, endPortal, null
        );
    }

    /** Pure CPU work over inputs detached from Minecraft's world state. */
    private static PreparedTile routeCaptured(CapturedTile captured) {
        if (captured.earlyResult() != null) {
            return PreparedTile.raw(
                    captured.originX(), captured.originZ(), captured.raw(),
                    captured.earlyResult()
            );
        }
        BoundedRegionPathRouter.RouteResult route =
                BoundedRegionPathRouter.route(
                captured.raw(), captured.biomes(), captured.rivers(),
                captured.first(), captured.second(),
                rawPortal -> rawPortal.equals(captured.rawStart())
                        ? captured.startPortal()
                        : rawPortal.equals(captured.rawEnd())
                                ? captured.endPortal() : null
        );
        if (route.regions() == null) {
            return PreparedTile.raw(
                    captured.originX(), captured.originZ(), captured.raw(),
                    TileResult.valueOf(route.rejectionReason().name())
            );
        }
        return new PreparedTile(
                captured.originX(), captured.originZ(),
                route.regions(), TileResult.ROUTED,
                route.changedCells(), route.preferredEdgeSteps(),
                route.controlledHandoffs(), route.roundedBridgeSteps(),
                route.edgeRunDiagnostics()
        );
    }

    /**
     * Resolves a portal from samples on the exact shared world-coordinate
     * edge. Both neighboring tiles therefore select the same result without
     * depending on preparation order or a mutable cross-tile cache.
     */
    private static BoundedRegionPathRouter.Portal sharedPortal(
            ServerLevelAccessor level, int originX, int originZ,
            BoundedRegionPathRouter.Portal rawPortal,
            Region first, Region second,
            net.minecraft.world.level.biome.BiomeSource biomeSource,
            net.minecraft.world.level.biome.Climate.Sampler climateSampler,
            int quartY
    ) {
        int rawX = rawPortal.x();
        int rawZ = rawPortal.z();
        if ((rawX == 0 || rawX == TILE_QUARTS)
                && (rawZ == 0 || rawZ == TILE_QUARTS)) {
            return rawPortal;
        }
        boolean horizontal = rawZ == 0 || rawZ == TILE_QUARTS;
        Region[] owners = new Region[TILE_QUARTS];
        ResourceLocation[] biomeIds = new ResourceLocation[TILE_QUARTS];
        boolean[] river = new boolean[TILE_QUARTS];
        for (int position = 0; position < TILE_QUARTS; position++) {
            int quartX = horizontal ? originX + position : originX + rawX;
            int quartZ = horizontal ? originZ + rawZ : originZ + position;
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

        return resolveSharedPortal(
                rawPortal, first, second, owners, biomeIds, river
        );
    }

    private static BoundedRegionPathRouter.Portal resolveSharedPortal(
            BoundedRegionPathRouter.Portal rawPortal,
            Region first, Region second, Region[] owners,
            ResourceLocation[] biomeIds, boolean[] river
    ) {
        int rawX = rawPortal.x();
        int rawZ = rawPortal.z();
        if ((rawX == 0 || rawX == TILE_QUARTS)
                && (rawZ == 0 || rawZ == TILE_QUARTS)) {
            return rawPortal;
        }
        boolean horizontal = rawZ == 0 || rawZ == TILE_QUARTS;

        int rawTransition = -1;
        for (int position = 0; position < TILE_QUARTS - 1; position++) {
            Region a = owners[position];
            Region b = owners[position + 1];
            if (!a.equals(b)) {
                if (!isPair(a, first, second)
                        || !isPair(b, first, second)
                        || rawTransition >= 0) return null;
                rawTransition = position + 1;
            }
        }
        if (rawTransition < 0) return null;

        int best = rawTransition;
        int bestCost = PORTAL_REACH_QUARTS + 1;
        int minimum = Math.max(1, rawTransition - PORTAL_REACH_QUARTS);
        int maximum = Math.min(
                TILE_QUARTS - 1,
                rawTransition + PORTAL_REACH_QUARTS
        );
        for (int portal = minimum; portal <= maximum; portal++) {
            ResourceLocation a = biomeIds[portal - 1];
            ResourceLocation b = biomeIds[portal];
            if (a == null || b == null || a.equals(b)) continue;
            int cost = Math.abs(portal - rawTransition);
            if (river[portal - 1] || river[portal]) {
                cost = Math.max(0, cost - PORTAL_RIVER_BONUS);
            }
            if (cost < bestCost || cost == bestCost && portal < best) {
                best = portal;
                bestCost = cost;
            }
        }
        BoundedRegionPathRouter.EdgeIdentity identity =
                BoundedRegionPathRouter.EdgeIdentity.of(
                        biomeIds[best - 1], biomeIds[best]
                );
        int continuation = portalContinuation(
                best, identity, biomeIds
        );
        return horizontal
                ? new BoundedRegionPathRouter.Portal(
                        best, rawZ, identity, continuation
                )
                : new BoundedRegionPathRouter.Portal(
                        rawX, best, identity, continuation
                );
    }

    /**
     * Measures the deterministic connected biome-edge span visible on the
     * shared tile edge. Both adjacent tiles capture the same samples, so this
     * lightweight continuation hint crosses portals without shared mutation.
     */
    private static int portalContinuation(
            int portal, BoundedRegionPathRouter.EdgeIdentity identity,
            ResourceLocation[] biomeIds
    ) {
        if (identity == null) return 0;
        int length = 1;
        for (int position = portal - 1; position > 0; position--) {
            if (!identity.equals(BoundedRegionPathRouter.EdgeIdentity.of(
                    biomeIds[position - 1], biomeIds[position]
            ))) break;
            length++;
        }
        for (int position = portal + 1;
             position < TILE_QUARTS; position++) {
            if (!identity.equals(BoundedRegionPathRouter.EdgeIdentity.of(
                    biomeIds[position - 1], biomeIds[position]
            ))) break;
            length++;
        }
        return length;
    }

    private static boolean isPair(
            Region value, Region first, Region second
    ) {
        return value.equals(first) || value.equals(second);
    }

    static int index(int x, int z) {
        return z * TILE_QUARTS + x;
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

    private static long elapsedMillis(long started) {
        return (System.nanoTime() - started) / 1_000_000L;
    }

    private enum TileResult {
        ROUTED,
        NO_RAW_BOUNDARY,
        TOO_MANY_REGIONS,
        BRANCHED_OR_DISCONNECTED_RAW,
        INVALID_PORTAL,
        NO_BOUNDED_PATH,
        UNCHANGED_PATH,
        TOO_MANY_COMPONENTS,
        NOT_TWO_COMPONENTS,
        UNKNOWN_RAW_OWNER,
        ENCLOSED_COMPONENT,
        INVALID_SIDE_ANCHOR,
        PORTAL_SIDE_CONFLICT,
        UNANCHORED_COMPONENT,
        NO_MEANINGFUL_CHANGE
    }

    private static final class CaptureJob {
        private final ServerLevel level;
        private final int originX;
        private final int originZ;
        private final long tileKey;
        private final RouteCache cache;
        private final Region[] raw = new Region[TILE_CELLS];
        private final ResourceLocation[] biomes =
                new ResourceLocation[TILE_CELLS];
        private final boolean[] rivers = new boolean[TILE_CELLS];
        private final net.minecraft.world.level.biome.BiomeSource biomeSource;
        private final net.minecraft.world.level.biome.Climate.Sampler
                climateSampler;
        private final int quartY;
        private final long started = System.nanoTime();
        private int cellIndex;
        private Region first;
        private Region second;
        private boolean tooManyRegions;
        private boolean boundaryInitialized;
        private BoundedRegionPathRouter.RawPortals rawPortals;
        private PortalCapture startCapture;
        private PortalCapture endCapture;

        private CaptureJob(
                ServerLevel level, int tileX, int tileZ,
                long tileKey, RouteCache cache
        ) {
            this.level = level;
            this.originX = tileX * TILE_QUARTS;
            this.originZ = tileZ * TILE_QUARTS;
            this.tileKey = tileKey;
            this.cache = cache;
            var chunkSource = level.getChunkSource();
            var generator = chunkSource.getGenerator();
            this.biomeSource = generator.getBiomeSource();
            this.climateSampler = chunkSource.randomState().sampler();
            this.quartY = QuartPos.fromBlock(generator.getSeaLevel());
        }

        private CapturedTile captureSlice(long deadline, int maxSamples) {
            int samples = 0;
            while (samples < maxSamples && System.nanoTime() < deadline) {
                if (cellIndex < TILE_CELLS) {
                    sampleCell(cellIndex++);
                    samples++;
                    continue;
                }
                if (!boundaryInitialized) {
                    boundaryInitialized = true;
                    if (tooManyRegions) {
                        return early(TileResult.TOO_MANY_REGIONS);
                    }
                    if (first == null || second == null) {
                        return early(TileResult.NO_RAW_BOUNDARY);
                    }
                    rawPortals = BoundedRegionPathRouter.rawPortals(
                            raw, first, second
                    );
                    if (rawPortals == null) {
                        return early(
                                TileResult.BRANCHED_OR_DISCONNECTED_RAW
                        );
                    }
                    startCapture = new PortalCapture(rawPortals.start());
                    endCapture = new PortalCapture(rawPortals.end());
                }
                if (!startCapture.complete()) {
                    startCapture.sampleNext(this, first, second);
                    samples++;
                    continue;
                }
                if (!endCapture.complete()) {
                    endCapture.sampleNext(this, first, second);
                    samples++;
                    continue;
                }
                return new CapturedTile(
                        originX, originZ, raw, biomes, rivers,
                        first, second,
                        rawPortals.start(), rawPortals.end(),
                        startCapture.resolve(first, second),
                        endCapture.resolve(first, second), null
                );
            }
            return null;
        }

        private void sampleCell(int index) {
            int x = index % TILE_QUARTS;
            int z = index / TILE_QUARTS;
            int quartX = originX + x;
            int quartZ = originZ + z;
            Region owner = ClimateDirectionAssignment.landRegion(
                    level, QuartPos.toBlock(quartX) + 2,
                    QuartPos.toBlock(quartZ) + 2
            );
            raw[index] = owner;
            if (!owner.ocean()) {
                if (first == null) first = owner;
                else if (!owner.equals(first) && second == null) {
                    second = owner;
                } else if (!owner.equals(first) && !owner.equals(second)) {
                    tooManyRegions = true;
                }
            }
            Holder<Biome> sample = biomeSource.getNoiseBiome(
                    quartX, quartY, quartZ, climateSampler
            );
            biomes[index] = sample.unwrapKey()
                    .map(key -> key.location()).orElse(null);
            rivers[index] = sample.is(BiomeTags.IS_RIVER);
        }

        private CapturedTile early(TileResult result) {
            return CapturedTile.raw(
                    originX, originZ, raw, biomes, rivers, result
            );
        }

        private long elapsedMillis() { return RegionBoundaryRouter.elapsedMillis(started); }
        private ServerLevel level() { return level; }
        private long tileKey() { return tileKey; }
        private RouteCache cache() { return cache; }
    }

    private static final class PortalCapture {
        private final BoundedRegionPathRouter.Portal rawPortal;
        private final Region[] owners = new Region[TILE_QUARTS];
        private final ResourceLocation[] biomes =
                new ResourceLocation[TILE_QUARTS];
        private final boolean[] rivers = new boolean[TILE_QUARTS];
        private final ResourceLocation[] lookaheadBiomes =
                new ResourceLocation[
                        TILE_QUARTS * PORTAL_LOOKAHEAD_DEPTH
                ];
        private int position;
        private int lookaheadPosition;
        private boolean portalResolved;
        private BoundedRegionPathRouter.Portal resolvedPortal;

        private PortalCapture(BoundedRegionPathRouter.Portal rawPortal) {
            this.rawPortal = rawPortal;
        }

        private void sampleNext(
                CaptureJob job, Region first, Region second
        ) {
            if (position >= TILE_QUARTS) {
                if (!portalResolved) {
                    portalResolved = true;
                    resolvedPortal = resolveSharedPortal(
                            rawPortal, first, second,
                            owners, biomes, rivers
                    );
                    if (resolvedPortal == null
                            || resolvedPortal.edgeIdentity() == null) {
                        lookaheadPosition = lookaheadBiomes.length;
                        return;
                    }
                }
                sampleLookahead(job);
                return;
            }
            boolean horizontal = rawPortal.z() == 0
                    || rawPortal.z() == TILE_QUARTS;
            int quartX = horizontal
                    ? job.originX + position
                    : job.originX + rawPortal.x();
            int quartZ = horizontal
                    ? job.originZ + rawPortal.z()
                    : job.originZ + position;
            owners[position] = ClimateDirectionAssignment.landRegion(
                    job.level, QuartPos.toBlock(quartX) + 2,
                    QuartPos.toBlock(quartZ) + 2
            );
            Holder<Biome> sample = job.biomeSource.getNoiseBiome(
                    quartX, job.quartY, quartZ, job.climateSampler
            );
            biomes[position] = sample.unwrapKey()
                    .map(key -> key.location()).orElse(null);
            rivers[position] = sample.is(BiomeTags.IS_RIVER);
            position++;
        }

        private void sampleLookahead(CaptureJob job) {
            int depth = lookaheadPosition / TILE_QUARTS;
            int along = lookaheadPosition % TILE_QUARTS;
            boolean horizontal = rawPortal.z() == 0
                    || rawPortal.z() == TILE_QUARTS;
            int quartX;
            int quartZ;
            if (horizontal) {
                quartX = job.originX + along;
                quartZ = rawPortal.z() == 0
                        ? job.originZ - 1 - depth
                        : job.originZ + TILE_QUARTS + depth;
            } else {
                quartX = rawPortal.x() == 0
                        ? job.originX - 1 - depth
                        : job.originX + TILE_QUARTS + depth;
                quartZ = job.originZ + along;
            }
            Holder<Biome> sample = job.biomeSource.getNoiseBiome(
                    quartX, job.quartY, quartZ, job.climateSampler
            );
            lookaheadBiomes[lookaheadPosition] = sample.unwrapKey()
                    .map(key -> key.location()).orElse(null);
            lookaheadPosition++;
        }

        private boolean complete() {
            return portalResolved && (resolvedPortal == null
                    || lookaheadPosition >= lookaheadBiomes.length);
        }

        private BoundedRegionPathRouter.Portal resolve(
                Region first, Region second
        ) {
            if (resolvedPortal == null) return null;
            int continuation = resolvedPortal.continuationSteps()
                    + forwardContinuation(resolvedPortal);
            return new BoundedRegionPathRouter.Portal(
                    resolvedPortal.x(), resolvedPortal.z(),
                    resolvedPortal.edgeIdentity(), continuation
            );
        }

        private int forwardContinuation(
                BoundedRegionPathRouter.Portal portal
        ) {
            BoundedRegionPathRouter.EdgeIdentity identity =
                    portal.edgeIdentity();
            if (identity == null) return 0;
            boolean horizontal = rawPortal.z() == 0
                    || rawPortal.z() == TILE_QUARTS;
            int previous = horizontal ? portal.x() : portal.z();
            int followedDepth = 0;
            for (int depth = 0; depth < PORTAL_LOOKAHEAD_DEPTH; depth++) {
                int best = -1;
                int bestDistance = PORTAL_LOOKAHEAD_SEARCH + 1;
                int minimum = Math.max(
                        1, previous - PORTAL_LOOKAHEAD_SEARCH
                );
                int maximum = Math.min(
                        TILE_QUARTS - 1,
                        previous + PORTAL_LOOKAHEAD_SEARCH
                );
                int row = depth * TILE_QUARTS;
                for (int candidate = minimum;
                     candidate <= maximum; candidate++) {
                    BoundedRegionPathRouter.EdgeIdentity candidateIdentity =
                            BoundedRegionPathRouter.EdgeIdentity.of(
                                    lookaheadBiomes[row + candidate - 1],
                                    lookaheadBiomes[row + candidate]
                            );
                    if (!identity.equals(candidateIdentity)) continue;
                    int distance = Math.abs(candidate - previous);
                    if (distance < bestDistance
                            || distance == bestDistance
                            && candidate < best) {
                        best = candidate;
                        bestDistance = distance;
                    }
                }
                if (best < 0) break;
                followedDepth++;
                previous = best;
            }
            return followedDepth * PORTAL_LOOKAHEAD_WEIGHT;
        }
    }

    private record CapturedTile(
            int originX, int originZ, Region[] raw,
            ResourceLocation[] biomes, boolean[] rivers,
            Region first, Region second,
            BoundedRegionPathRouter.Portal rawStart,
            BoundedRegionPathRouter.Portal rawEnd,
            BoundedRegionPathRouter.Portal startPortal,
            BoundedRegionPathRouter.Portal endPortal,
            TileResult earlyResult
    ) {
        private static CapturedTile raw(
                int originX, int originZ, Region[] raw,
                ResourceLocation[] biomes, boolean[] rivers,
                TileResult result
        ) {
            return new CapturedTile(
                    originX, originZ, raw, biomes, rivers,
                    null, null, null, null, null, null, result
            );
        }
    }

    private record PreparedTile(
            int originX, int originZ, Region[] regions, TileResult result,
            int changedCells, int preferredEdgeSteps,
            int controlledHandoffs, int roundedBridgeSteps,
            BoundedRegionPathRouter.EdgeRunDiagnostics edgeRunDiagnostics
    ) {
        private static PreparedTile raw(
                int x, int z, Region[] raw, TileResult result
        ) {
            return new PreparedTile(
                    x, z, Arrays.copyOf(raw, raw.length), result,
                    0, 0, 0, 0,
                    BoundedRegionPathRouter.EdgeRunDiagnostics.EMPTY
            );
        }
    }

    private static final class RouteCache {
        private final Map<Long, Region> cells =
                new LinkedHashMap<>(256, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, Region> eldest
                    ) { return size() > MAX_CACHE_ENTRIES; }
                };
        private final Map<Long, Boolean> preparedTiles =
                new LinkedHashMap<>(128, 0.75F, true) {
                    @Override
                    protected boolean removeEldestEntry(
                            Map.Entry<Long, Boolean> eldest
                    ) { return size() > MAX_PREPARED_TILES; }
                };
        private final Map<Long, List<Runnable>> preparationWaiters =
                new LinkedHashMap<>();

        private synchronized Region get(long key) { return cells.get(key); }
        private synchronized boolean isPrepared(long key) {
            return preparedTiles.containsKey(key);
        }
        private synchronized boolean beginPreparation(
                long key, Runnable completion
        ) {
            List<Runnable> waiters = preparationWaiters.get(key);
            if (waiters != null) {
                waiters.add(completion);
                return false;
            }
            waiters = new ArrayList<>();
            waiters.add(completion);
            preparationWaiters.put(key, waiters);
            return true;
        }

        private void completePreparation(long key) {
            List<Runnable> waiters;
            synchronized (this) {
                waiters = preparationWaiters.remove(key);
            }
            if (waiters != null) {
                for (Runnable waiter : waiters) waiter.run();
            }
        }

        private synchronized void failPreparation(long key) {
            preparationWaiters.remove(key);
        }
        private synchronized void publish(long key, PreparedTile prepared) {
            for (int z = 0; z < TILE_QUARTS; z++) {
                for (int x = 0; x < TILE_QUARTS; x++) {
                    cells.put(cellKey(prepared.originX() + x,
                            prepared.originZ() + z),
                            prepared.regions()[index(x, z)]);
                }
            }
            preparedTiles.put(key, Boolean.TRUE);
            int minBlockX = QuartPos.toBlock(prepared.originX());
            int minBlockZ = QuartPos.toBlock(prepared.originZ());
            if (prepared.result() == TileResult.ROUTED) {
                LOGGER.info(
                        "Final border routed tile X {}..{}, Z {}..{}: "
                                + "ROUTED ({} quart cells changed, "
                                + "{} preferred edge steps, "
                                + "{} controlled handoffs, "
                                + "{} rounded bridge steps; edge runs "
                                + "{} selected/{} eligible/{} total, "
                                + "{} short, {} beyond reach, "
                                + "nearest beyond {} quart; "
                                + "{} selected dead ends, longest continuation "
                                + "{} quart, portal continuation {}/{}; "
                                + "candidates [{}])",
                        minBlockX, minBlockX + TILE_QUARTS * 4 - 1,
                        minBlockZ, minBlockZ + TILE_QUARTS * 4 - 1,
                        prepared.changedCells(),
                        prepared.preferredEdgeSteps(),
                        prepared.controlledHandoffs(),
                        prepared.roundedBridgeSteps(),
                        prepared.edgeRunDiagnostics().selectedRuns(),
                        prepared.edgeRunDiagnostics().eligibleRuns(),
                        prepared.edgeRunDiagnostics().totalRuns(),
                        prepared.edgeRunDiagnostics().rejectedShort(),
                        prepared.edgeRunDiagnostics().rejectedBeyondReach(),
                        prepared.edgeRunDiagnostics().nearestBeyondReach(),
                        prepared.edgeRunDiagnostics().selectedDeadEnds(),
                        prepared.edgeRunDiagnostics()
                                .longestSelectedContinuation(),
                        prepared.edgeRunDiagnostics()
                                .startPortalContinuation(),
                        prepared.edgeRunDiagnostics()
                                .endPortalContinuation(),
                        prepared.edgeRunDiagnostics().selectedCandidates()
                );
            } else {
                LOGGER.info(
                        "Final border kept Raw for tile X {}..{}, Z {}..{}: {}",
                        minBlockX, minBlockX + TILE_QUARTS * 4 - 1,
                        minBlockZ, minBlockZ + TILE_QUARTS * 4 - 1,
                        prepared.result()
                );
            }
        }
    }
}
