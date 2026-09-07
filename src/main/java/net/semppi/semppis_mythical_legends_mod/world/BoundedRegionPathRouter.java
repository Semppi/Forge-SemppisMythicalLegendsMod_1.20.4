package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/** Routes a border on one fixed 65x65 corner lattice. */
final class BoundedRegionPathRouter {
    private static final int SIZE = RegionBoundaryRouter.TILE_QUARTS;
    private static final int VERTEX_SIZE = SIZE + 1;
    private static final int VERTICES = VERTEX_SIZE * VERTEX_SIZE;
    private static final int CELLS = SIZE * SIZE;
    private static final int MAX_DISTANCE_FROM_RAW = 40;
    // Four active runs across three handoff layers keep the state bound below
    // the previous sixteen independent path searches.
    private static final int MAX_PREFERRED_EDGE_RUNS = 4;
    private static final int MIN_PREFERRED_RUN_EDGES = 4;
    private static final int MAX_EDGE_HANDOFFS = 2;
    private static final int EDGE_ACQUIRE_COST = 4;
    private static final int EDGE_HANDOFF_COST = 24;
    private static final int EXIT_IDENTITY_MISMATCH_COST = 48;
    private static final int INF = 1_000_000_000;

    private BoundedRegionPathRouter() {}

    enum RejectionReason {
        NONE,
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

    record RouteResult(
            Region[] regions, RejectionReason rejectionReason,
            int changedCells, int preferredEdgeSteps,
            int controlledHandoffs, int roundedBridgeSteps,
            EdgeRunDiagnostics edgeRunDiagnostics
    ) {
        private static RouteResult rejected(RejectionReason reason) {
            return new RouteResult(
                    null, reason, 0, 0, 0, 0,
                    EdgeRunDiagnostics.EMPTY
            );
        }
    }

    record EdgeRunDiagnostics(
            int totalRuns, int eligibleRuns, int selectedRuns,
            int rejectedShort, int rejectedBeyondReach,
            int nearestBeyondReach
    ) {
        static final EdgeRunDiagnostics EMPTY =
                new EdgeRunDiagnostics(0, 0, 0, 0, 0, -1);
    }

    static RouteResult route(
            Region[] raw, ResourceLocation[] biomes, boolean[] rivers,
            Region first, Region second, PortalResolver portalResolver
    ) {
        RawBoundary boundary = traceRawBoundary(raw, first, second);
        if (boundary == null) {
            return RouteResult.rejected(
                    RejectionReason.BRANCHED_OR_DISCONNECTED_RAW
            );
        }
        Portal rawStart = portal(boundary.start());
        Portal rawEnd = portal(boundary.end());
        Portal startPortal = portalResolver.resolve(rawStart);
        Portal endPortal = portalResolver.resolve(rawEnd);
        if (!validPortal(rawStart, startPortal)
                || !validPortal(rawEnd, endPortal)
                || startPortal.equals(endPortal)) {
            return RouteResult.rejected(RejectionReason.INVALID_PORTAL);
        }
        int start = vertex(startPortal.x(), startPortal.z());
        int end = vertex(endPortal.x(), endPortal.z());
        int[] rawDistance = distanceFromRaw(boundary.vertices());
        NaturalRuns naturalRuns = naturalRuns(
                biomes, rawDistance,
                startPortal.edgeIdentity(), endPortal.edgeIdentity()
        );
        PathSearch selected = shortestPath(
                start, end, rawDistance, raw, biomes, rivers,
                naturalRuns, startPortal.edgeIdentity(),
                endPortal.edgeIdentity()
        );
        List<Integer> path = selected == null ? null : selected.path();
        if (path == null) {
            return RouteResult.rejected(RejectionReason.NO_BOUNDED_PATH);
        }
        if (path.equals(boundary.orderedPath())) {
            return RouteResult.rejected(RejectionReason.UNCHANGED_PATH);
        }
        List<Integer> rawPath = boundary.orderedPath();
        SideIdentity startIdentity = sideIdentity(
                rawPath.get(0), rawPath.get(1), raw
        );
        SideIdentity endIdentity = sideIdentity(
                rawPath.get(rawPath.size() - 2),
                rawPath.get(rawPath.size() - 1), raw
        );
        DirectedSides routedStart = directedSides(path.get(0), path.get(1));
        DirectedSides routedEnd = directedSides(
                path.get(path.size() - 2), path.get(path.size() - 1)
        );
        if (startIdentity == null || endIdentity == null
                || routedStart == null || routedEnd == null) {
            return RouteResult.rejected(RejectionReason.INVALID_SIDE_ANCHOR);
        }
        RoundedPath rounded = roundUnsupportedBridges(
                path, rawDistance, raw, biomes
        );
        List<Integer> finalPath = rounded.path();
        DirectedSides roundedStart = directedSides(
                finalPath.get(0), finalPath.get(1)
        );
        DirectedSides roundedEnd = directedSides(
                finalPath.get(finalPath.size() - 2),
                finalPath.get(finalPath.size() - 1)
        );
        FillResult fill = fillSides(
                raw, pathWalls(finalPath), first, second,
                startIdentity, endIdentity, roundedStart, roundedEnd
        );
        int roundedSteps = rounded.changedSteps();
        int changes = fill.regions() == null
                ? 0 : countChanges(raw, fill.regions());
        // Rounding is presentation polish. Never discard a valid bounded route
        // if a diagonalized bridge happens to create invalid local topology.
        if ((fill.regions() == null || changes < 4) && roundedSteps > 0) {
            roundedSteps = 0;
            fill = fillSides(
                    raw, pathWalls(path), first, second,
                    startIdentity, endIdentity, routedStart, routedEnd
            );
            changes = fill.regions() == null
                    ? 0 : countChanges(raw, fill.regions());
        }
        if (fill.regions() == null) {
            return RouteResult.rejected(fill.rejectionReason());
        }
        return changes >= 4
                ? new RouteResult(
                        fill.regions(), RejectionReason.NONE, changes,
                        selected.preferredEdges(), selected.handoffs(),
                        roundedSteps, naturalRuns.diagnostics()
                )
                : RouteResult.rejected(RejectionReason.NO_MEANINGFUL_CHANGE);
    }

    /**
     * Replaces only unsupported bridge spans with a balanced cardinal
     * staircase. The wall lattice stays four-connected, but long rectangular
     * legs become deterministic map-scale diagonals with gradual approaches.
     */
    private static RoundedPath roundUnsupportedBridges(
            List<Integer> path, int[] rawDistance, Region[] raw,
            ResourceLocation[] biomes
    ) {
        if (path.size() < 4) return new RoundedPath(path, 0);
        List<Integer> result = new ArrayList<>(path.size());
        result.add(path.get(0));
        int changedSteps = 0;
        int edge = 0;
        while (edge < path.size() - 1) {
            if (!unsupportedBridgeEdge(
                    path.get(edge), path.get(edge + 1), raw, biomes
            )) {
                appendIfDifferent(result, path.get(edge + 1));
                edge++;
                continue;
            }
            int startEdge = edge;
            while (edge < path.size() - 1 && unsupportedBridgeEdge(
                    path.get(edge), path.get(edge + 1), raw, biomes
            )) {
                edge++;
            }
            int from = path.get(startEdge);
            int to = path.get(edge);
            List<Integer> rounded = balancedCardinalPath(from, to);
            if (validRoundedSpan(rounded, rawDistance)) {
                for (int index = 1; index < rounded.size(); index++) {
                    appendIfDifferent(result, rounded.get(index));
                }
                if (!rounded.equals(path.subList(startEdge, edge + 1))) {
                    changedSteps += rounded.size() - 1;
                }
            } else {
                for (int index = startEdge + 1; index <= edge; index++) {
                    appendIfDifferent(result, path.get(index));
                }
            }
        }
        return new RoundedPath(result, changedSteps);
    }

    private static boolean unsupportedBridgeEdge(
            int from, int to, Region[] raw, ResourceLocation[] biomes
    ) {
        CellPair pair = separatedCells(from, to);
        if (pair == null) return false;
        ResourceLocation first = biomes[pair.first()];
        ResourceLocation second = biomes[pair.second()];
        boolean natural = first != null && second != null
                && !first.equals(second);
        boolean rawEdge = !raw[pair.first()].equals(raw[pair.second()]);
        return !natural && !rawEdge;
    }

    private static List<Integer> balancedCardinalPath(int from, int to) {
        int x = vertexX(from);
        int z = vertexZ(from);
        int targetX = vertexX(to);
        int targetZ = vertexZ(to);
        int totalX = Math.abs(targetX - x);
        int totalZ = Math.abs(targetZ - z);
        int stepX = Integer.compare(targetX, x);
        int stepZ = Integer.compare(targetZ, z);
        int movedX = 0;
        int movedZ = 0;
        List<Integer> result = new ArrayList<>(totalX + totalZ + 1);
        result.add(from);
        while (movedX < totalX || movedZ < totalZ) {
            boolean moveX;
            if (movedX >= totalX) moveX = false;
            else if (movedZ >= totalZ) moveX = true;
            else {
                long xProgress = (long) (movedX + 1) * totalZ;
                long zProgress = (long) (movedZ + 1) * totalX;
                moveX = xProgress <= zProgress;
            }
            if (moveX) {
                x += stepX;
                movedX++;
            } else {
                z += stepZ;
                movedZ++;
            }
            result.add(vertex(x, z));
        }
        return result;
    }

    private static boolean validRoundedSpan(
            List<Integer> span, int[] rawDistance
    ) {
        for (int index = 1; index < span.size() - 1; index++) {
            int value = span.get(index);
            if (onPerimeter(value)
                    || rawDistance[value] > MAX_DISTANCE_FROM_RAW) {
                return false;
            }
        }
        return true;
    }

    private static void appendIfDifferent(
            List<Integer> path, int value
    ) {
        if (path.get(path.size() - 1) != value) path.add(value);
    }

    private static RawBoundary traceRawBoundary(
            Region[] raw, Region first, Region second
    ) {
        @SuppressWarnings("unchecked")
        List<Integer>[] graph = new List[VERTICES];
        boolean[] used = new boolean[VERTICES];
        for (int z = 0; z < SIZE; z++) {
            for (int x = 1; x < SIZE; x++) {
                Region a = raw[cell(x - 1, z)];
                Region b = raw[cell(x, z)];
                if (differentPair(a, b, first, second)) {
                    addEdge(graph, used, vertex(x, z), vertex(x, z + 1));
                }
            }
        }
        for (int z = 1; z < SIZE; z++) {
            for (int x = 0; x < SIZE; x++) {
                Region a = raw[cell(x, z - 1)];
                Region b = raw[cell(x, z)];
                if (differentPair(a, b, first, second)) {
                    addEdge(graph, used, vertex(x, z), vertex(x + 1, z));
                }
            }
        }

        List<Integer> endpoints = new ArrayList<>(2);
        int usedCount = 0;
        for (int value = 0; value < VERTICES; value++) {
            if (!used[value]) continue;
            usedCount++;
            int degree = graph[value].size();
            if (degree > 2 || degree == 0) return null;
            if (degree == 1) {
                if (!onPerimeter(value)) return null;
                endpoints.add(value);
            }
        }
        if (usedCount == 0 || endpoints.size() != 2) return null;

        List<Integer> ordered = new ArrayList<>(usedCount);
        boolean[] visited = new boolean[VERTICES];
        int previous = -1;
        int current = endpoints.get(0);
        while (true) {
            ordered.add(current);
            visited[current] = true;
            int next = -1;
            for (int candidate : graph[current]) {
                if (candidate != previous) {
                    if (visited[candidate]) return null;
                    next = candidate;
                    break;
                }
            }
            if (next < 0) break;
            previous = current;
            current = next;
        }
        if (current != endpoints.get(1) || ordered.size() != usedCount) {
            return null;
        }
        return new RawBoundary(
                endpoints.get(0), endpoints.get(1), used, ordered
        );
    }

    private static int[] distanceFromRaw(boolean[] rawVertices) {
        int[] distance = new int[VERTICES];
        Arrays.fill(distance, INF);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        for (int value = 0; value < VERTICES; value++) {
            if (rawVertices[value]) {
                distance[value] = 0;
                queue.add(value);
            }
        }
        while (!queue.isEmpty()) {
            int current = queue.removeFirst();
            for (int next : neighbors(current)) {
                if (distance[next] > distance[current] + 1) {
                    distance[next] = distance[current] + 1;
                    queue.addLast(next);
                }
            }
        }
        return distance;
    }

    private static PathSearch shortestPath(
            int start, int end, int[] rawDistance,
            Region[] raw, ResourceLocation[] biomes, boolean[] rivers,
            NaturalRuns naturalRuns, EdgeIdentity startIdentity,
            EdgeIdentity endIdentity
    ) {
        int activeStates = naturalRuns.candidates().size() + 1;
        int stateCount = VERTICES * activeStates * (MAX_EDGE_HANDOFFS + 1);
        int[] cost = new int[stateCount];
        int[] previous = new int[stateCount];
        Arrays.fill(cost, INF);
        Arrays.fill(previous, -1);
        PriorityQueue<Node> open = new PriorityQueue<>();
        int startActive = portalCandidate(
                start, startIdentity, naturalRuns
        );
        int startState = state(start, startActive, 0, activeStates);
        cost[startState] = 0;
        open.add(new Node(startState, 0));
        while (!open.isEmpty()) {
            Node node = open.remove();
            if (node.cost() != cost[node.state()]) continue;
            int currentVertex = stateVertex(node.state());
            int active = stateActive(node.state(), activeStates);
            int handoffs = stateHandoffs(node.state(), activeStates);
            for (int next : neighbors(currentVertex)) {
                if (next != end && onPerimeter(next)) continue;
                if (rawDistance[next] > MAX_DISTANCE_FROM_RAW) continue;
                int slot = edgeSlot(currentVertex, next);
                int edgeRun = slot < 0 ? -1
                        : naturalRuns.runByEdge()[slot];
                int edgeCandidate = edgeRun < 0 ? -1
                        : naturalRuns.candidateByRun()[edgeRun];

                int retainedCost = edgeCost(
                        currentVertex, next, rawDistance[next], raw,
                        biomes, rivers, naturalRuns.runByEdge(),
                        active == 0 ? -1
                                : naturalRuns.candidates()
                                        .get(active - 1).runId()
                );
                if (active != 0 || edgeCandidate < 0) {
                    relax(
                            node.state(), next, active, handoffs,
                            retainedCost, activeStates, cost, previous, open
                    );
                }

                if (edgeCandidate >= 0 && edgeCandidate + 1 != active) {
                    int nextActive = edgeCandidate + 1;
                    int nextHandoffs = active == 0
                            ? handoffs : handoffs + 1;
                    if (nextHandoffs <= MAX_EDGE_HANDOFFS) {
                        int switchCost = edgeCost(
                                currentVertex, next, rawDistance[next], raw,
                                biomes, rivers, naturalRuns.runByEdge(),
                                edgeRun
                        ) + (active == 0
                                ? EDGE_ACQUIRE_COST : EDGE_HANDOFF_COST);
                        relax(
                                node.state(), next, nextActive,
                                nextHandoffs, switchCost, activeStates,
                                cost, previous, open
                        );
                    }
                }
            }
        }

        int endCandidate = portalCandidate(end, endIdentity, naturalRuns);
        int bestState = -1;
        int bestCost = INF;
        for (int handoffs = 0; handoffs <= MAX_EDGE_HANDOFFS; handoffs++) {
            for (int active = 0; active < activeStates; active++) {
                int candidateState = state(
                        end, active, handoffs, activeStates
                );
                if (cost[candidateState] >= INF) continue;
                int candidateCost = cost[candidateState];
                if (endCandidate > 0 && active != endCandidate) {
                    candidateCost += EXIT_IDENTITY_MISMATCH_COST;
                }
                if (candidateCost < bestCost
                        || candidateCost == bestCost
                        && candidateState < bestState) {
                    bestCost = candidateCost;
                    bestState = candidateState;
                }
            }
        }
        if (bestState < 0) return null;

        List<Integer> path = new ArrayList<>();
        List<Integer> states = new ArrayList<>();
        for (int current = bestState; current >= 0;
             current = previous[current]) {
            states.add(current);
            path.add(stateVertex(current));
            if (current == startState) break;
        }
        if (states.get(states.size() - 1) != startState) return null;
        Collections.reverse(path);
        Collections.reverse(states);
        int preferredEdges = 0;
        for (int index = 1; index < path.size(); index++) {
            int slot = edgeSlot(path.get(index - 1), path.get(index));
            int active = stateActive(states.get(index), activeStates);
            if (slot >= 0 && active > 0
                    && naturalRuns.runByEdge()[slot]
                    == naturalRuns.candidates().get(active - 1).runId()) {
                preferredEdges++;
            }
        }
        return new PathSearch(
                path, bestCost, preferredEdges,
                stateHandoffs(bestState, activeStates)
        );
    }

    private static void relax(
            int fromState, int nextVertex, int nextActive,
            int nextHandoffs, int stepCost, int activeStates,
            int[] cost, int[] previous, PriorityQueue<Node> open
    ) {
        if (stepCost >= INF) return;
        int nextState = state(
                nextVertex, nextActive, nextHandoffs, activeStates
        );
        int candidate = cost[fromState] + stepCost;
        if (candidate < cost[nextState]
                || candidate == cost[nextState]
                && fromState < previous[nextState]) {
            cost[nextState] = candidate;
            previous[nextState] = fromState;
            open.add(new Node(nextState, candidate));
        }
    }

    private static int state(
            int vertex, int active, int handoffs, int activeStates
    ) {
        return (handoffs * activeStates + active) * VERTICES + vertex;
    }

    private static int stateVertex(int state) { return state % VERTICES; }

    private static int stateActive(int state, int activeStates) {
        return state / VERTICES % activeStates;
    }

    private static int stateHandoffs(int state, int activeStates) {
        return state / VERTICES / activeStates;
    }

    private static int portalCandidate(
            int portalVertex, EdgeIdentity identity, NaturalRuns naturalRuns
    ) {
        if (identity == null) return 0;
        int best = 0;
        for (int slot : incidentEdgeSlots(portalVertex)) {
            int run = naturalRuns.runByEdge()[slot];
            if (run < 0 || !identity.equals(
                    naturalRuns.identityByRun()[run])) continue;
            int candidate = naturalRuns.candidateByRun()[run];
            if (candidate >= 0 && (best == 0 || candidate + 1 < best)) {
                best = candidate + 1;
            }
        }
        return best;
    }

    private static int edgeCost(
            int from, int to, int rawDistance,
            Region[] raw, ResourceLocation[] biomes, boolean[] rivers,
            int[] runByEdge, int preferredRun
    ) {
        CellPair pair = separatedCells(from, to);
        if (pair == null) return INF;
        ResourceLocation a = biomes[pair.first()];
        ResourceLocation b = biomes[pair.second()];
        boolean natural = a != null && b != null && !a.equals(b);
        boolean river = rivers[pair.first()] || rivers[pair.second()];
        boolean rawEdge = !raw[pair.first()].equals(raw[pair.second()]);
        if (natural) {
            int slot = edgeSlot(from, to);
            if (preferredRun >= 0 && slot >= 0) {
                if (runByEdge[slot] == preferredRun) {
                    return Math.max(
                            1, 2 + rawDistance / 10 - (river ? 1 : 0)
                    );
                }
                return Math.max(
                        5, 9 + rawDistance / 8 - (river ? 1 : 0)
                );
            }
            return Math.max(1, 2 + rawDistance / 8 - (river ? 1 : 0));
        }
        if (rawEdge) return 8;
        return 20 + rawDistance / 2;
    }

    /**
     * Gives each connected run of one unordered biome pair a stable local id.
     * Only a small deterministic set nearest Raw is evaluated by the router.
     */
    private static NaturalRuns naturalRuns(
            ResourceLocation[] biomes, int[] rawDistance,
            EdgeIdentity startIdentity, EdgeIdentity endIdentity
    ) {
        int edgeSlots = VERTICES * 2;
        EdgeIdentity[] pairs = new EdgeIdentity[edgeSlots];
        UnionFind union = new UnionFind(edgeSlots);
        for (int value = 0; value < VERTICES; value++) {
            int x = vertexX(value);
            int z = vertexZ(value);
            if (x < SIZE) {
                registerNaturalEdge(
                        value, vertex(x + 1, z), biomes, pairs, union
                );
            }
            if (z < SIZE) {
                registerNaturalEdge(
                        value, vertex(x, z + 1), biomes, pairs, union
                );
            }
        }
        for (int value = 0; value < VERTICES; value++) {
            int[] incident = incidentEdgeSlots(value);
            for (int first = 0; first < incident.length; first++) {
                int firstSlot = incident[first];
                if (pairs[firstSlot] == null) continue;
                for (int second = first + 1;
                     second < incident.length; second++) {
                    int secondSlot = incident[second];
                    if (pairs[firstSlot].equals(pairs[secondSlot])) {
                        union.union(firstSlot, secondSlot);
                    }
                }
            }
        }

        int[] rootIds = new int[edgeSlots];
        int[] runByEdge = new int[edgeSlots];
        Arrays.fill(rootIds, -1);
        Arrays.fill(runByEdge, -1);
        int runCount = 0;
        for (int slot = 0; slot < edgeSlots; slot++) {
            if (pairs[slot] == null) continue;
            int root = union.find(slot);
            if (rootIds[root] < 0) rootIds[root] = runCount++;
            runByEdge[slot] = rootIds[root];
        }

        int[] minimumDistance = new int[runCount];
        int[] edgeCount = new int[runCount];
        EdgeIdentity[] identityByRun = new EdgeIdentity[runCount];
        Arrays.fill(minimumDistance, INF);
        for (int slot = 0; slot < edgeSlots; slot++) {
            int run = runByEdge[slot];
            if (run < 0) continue;
            int from = edgeFrom(slot);
            int to = edgeTo(slot);
            minimumDistance[run] = Math.min(
                    minimumDistance[run],
                    Math.min(rawDistance[from], rawDistance[to])
            );
            edgeCount[run]++;
            identityByRun[run] = pairs[slot];
        }
        List<EdgeRunCandidate> candidates = new ArrayList<>();
        int rejectedShort = 0;
        int rejectedBeyondReach = 0;
        int nearestBeyondReach = INF;
        for (int run = 0; run < runCount; run++) {
            if (edgeCount[run] >= MIN_PREFERRED_RUN_EDGES
                    && minimumDistance[run] <= MAX_DISTANCE_FROM_RAW) {
                candidates.add(new EdgeRunCandidate(
                        run, minimumDistance[run], edgeCount[run],
                        identityByRun[run]
                ));
            } else if (edgeCount[run] < MIN_PREFERRED_RUN_EDGES) {
                rejectedShort++;
            } else {
                rejectedBeyondReach++;
                nearestBeyondReach = Math.min(
                        nearestBeyondReach, minimumDistance[run]
                );
            }
        }
        candidates.sort((left, right) -> {
            int leftPortalMatch = portalMatch(
                    left.identity(), startIdentity, endIdentity
            );
            int rightPortalMatch = portalMatch(
                    right.identity(), startIdentity, endIdentity
            );
            int byPortal = Integer.compare(
                    rightPortalMatch, leftPortalMatch
            );
            if (byPortal != 0) return byPortal;
            int byDistance = Integer.compare(
                    left.rawDistance(), right.rawDistance()
            );
            if (byDistance != 0) return byDistance;
            int byLength = Integer.compare(
                    right.edgeCount(), left.edgeCount()
            );
            return byLength != 0 ? byLength
                    : Integer.compare(left.runId(), right.runId());
        });
        int eligibleRuns = candidates.size();
        if (candidates.size() > MAX_PREFERRED_EDGE_RUNS) {
            candidates = new ArrayList<>(candidates.subList(
                    0, MAX_PREFERRED_EDGE_RUNS
            ));
        }
        int[] candidateByRun = new int[runCount];
        Arrays.fill(candidateByRun, -1);
        for (int index = 0; index < candidates.size(); index++) {
            candidateByRun[candidates.get(index).runId()] = index;
        }
        return new NaturalRuns(
                runByEdge, identityByRun, candidateByRun, candidates,
                new EdgeRunDiagnostics(
                        runCount, eligibleRuns, candidates.size(),
                        rejectedShort, rejectedBeyondReach,
                        nearestBeyondReach >= INF ? -1 : nearestBeyondReach
                )
        );
    }

    private static int portalMatch(
            EdgeIdentity candidate, EdgeIdentity start,
            EdgeIdentity end
    ) {
        int matches = 0;
        if (candidate.equals(start)) matches++;
        if (candidate.equals(end)) matches++;
        return matches;
    }

    private static void registerNaturalEdge(
            int from, int to, ResourceLocation[] biomes,
            EdgeIdentity[] pairs, UnionFind union
    ) {
        CellPair cells = separatedCells(from, to);
        if (cells == null) return;
        ResourceLocation first = biomes[cells.first()];
        ResourceLocation second = biomes[cells.second()];
        if (first == null || second == null || first.equals(second)) return;
        int slot = edgeSlot(from, to);
        pairs[slot] = EdgeIdentity.of(first, second);
        union.activate(slot);
    }

    private static int[] incidentEdgeSlots(int value) {
        int x = vertexX(value);
        int z = vertexZ(value);
        int[] slots = new int[4];
        int count = 0;
        if (x < SIZE) slots[count++] = edgeSlot(value, vertex(x + 1, z));
        if (z < SIZE) slots[count++] = edgeSlot(value, vertex(x, z + 1));
        if (x > 0) slots[count++] = edgeSlot(vertex(x - 1, z), value);
        if (z > 0) slots[count++] = edgeSlot(vertex(x, z - 1), value);
        return Arrays.copyOf(slots, count);
    }

    private static int edgeSlot(int first, int second) {
        int firstX = vertexX(first);
        int firstZ = vertexZ(first);
        int secondX = vertexX(second);
        int secondZ = vertexZ(second);
        if (firstZ == secondZ && Math.abs(firstX - secondX) == 1) {
            return Math.min(first, second) * 2;
        }
        if (firstX == secondX && Math.abs(firstZ - secondZ) == 1) {
            return Math.min(first, second) * 2 + 1;
        }
        return -1;
    }

    private static int edgeFrom(int slot) { return slot / 2; }

    private static int edgeTo(int slot) {
        return edgeFrom(slot) + (slot % 2 == 0 ? 1 : VERTEX_SIZE);
    }

    private static boolean[] pathWalls(List<Integer> path) {
        boolean[] walls = new boolean[CELLS * 4];
        for (int i = 1; i < path.size(); i++) {
            CellPair pair = separatedCells(path.get(i - 1), path.get(i));
            if (pair != null) block(walls, pair.first(), pair.second());
        }
        return walls;
    }

    /** A valid simple border must produce exactly two connected components. */
    private static FillResult fillSides(
            Region[] raw, boolean[] walls, Region first, Region second,
            SideIdentity startIdentity, SideIdentity endIdentity,
            DirectedSides routedStart, DirectedSides routedEnd
    ) {
        int[] component = new int[CELLS];
        Arrays.fill(component, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        int components = 0;
        for (int seed = 0; seed < CELLS; seed++) {
            if (component[seed] >= 0) continue;
            if (components >= 2) {
                return FillResult.rejected(
                        RejectionReason.TOO_MANY_COMPONENTS
                );
            }
            component[seed] = components;
            queue.add(seed);
            while (!queue.isEmpty()) {
                int current = queue.removeFirst();
                for (int direction = 0; direction < 4; direction++) {
                    int next = cellNeighbor(current, direction);
                    if (next < 0 || walls[current * 4 + direction]
                            || component[next] >= 0) continue;
                    component[next] = components;
                    queue.addLast(next);
                }
            }
            components++;
        }
        if (components != 2) {
            return FillResult.rejected(RejectionReason.NOT_TWO_COMPONENTS);
        }

        boolean[] touchesPerimeter = new boolean[2];
        for (int value = 0; value < CELLS; value++) {
            int owner = raw[value].equals(first) ? 0
                    : raw[value].equals(second) ? 1 : -1;
            if (owner < 0) {
                return FillResult.rejected(
                        RejectionReason.UNKNOWN_RAW_OWNER
                );
            }
            int x = value % SIZE;
            int z = value / SIZE;
            if (x == 0 || z == 0 || x == SIZE - 1 || z == SIZE - 1) {
                touchesPerimeter[component[value]] = true;
            }
        }
        if (!touchesPerimeter[0] || !touchesPerimeter[1]) {
            return FillResult.rejected(
                    RejectionReason.ENCLOSED_COMPONENT
            );
        }
        Region[] owners = new Region[2];
        if (!anchor(owners, component[routedStart.leftCell()],
                startIdentity.leftOwner())
                || !anchor(owners, component[routedStart.rightCell()],
                startIdentity.rightOwner())
                || !anchor(owners, component[routedEnd.leftCell()],
                endIdentity.leftOwner())
                || !anchor(owners, component[routedEnd.rightCell()],
                endIdentity.rightOwner())) {
            return FillResult.rejected(
                    RejectionReason.PORTAL_SIDE_CONFLICT
            );
        }
        if (owners[0] == null || owners[1] == null) {
            return FillResult.rejected(
                    RejectionReason.UNANCHORED_COMPONENT
            );
        }
        if (owners[0].equals(owners[1])) {
            return FillResult.rejected(
                    RejectionReason.PORTAL_SIDE_CONFLICT
            );
        }

        Region[] result = new Region[CELLS];
        for (int value = 0; value < CELLS; value++) {
            result[value] = owners[component[value]];
        }
        return FillResult.accepted(result);
    }

    private static boolean anchor(
            Region[] owners, int component, Region owner
    ) {
        if (component < 0 || component >= owners.length || owner == null) {
            return false;
        }
        if (owners[component] == null) {
            owners[component] = owner;
            return true;
        }
        return owners[component].equals(owner);
    }

    private static SideIdentity sideIdentity(
            int from, int to, Region[] raw
    ) {
        DirectedSides sides = directedSides(from, to);
        return sides == null ? null : new SideIdentity(
                raw[sides.leftCell()], raw[sides.rightCell()]
        );
    }

    /** Cells on the geometric left and right of one directed border edge. */
    private static DirectedSides directedSides(int from, int to) {
        int fromX = vertexX(from);
        int fromZ = vertexZ(from);
        int toX = vertexX(to);
        int toZ = vertexZ(to);
        int dx = toX - fromX;
        int dz = toZ - fromZ;
        if (dx == 1 && dz == 0 && fromZ > 0 && fromZ < SIZE) {
            return new DirectedSides(
                    cell(fromX, fromZ - 1), cell(fromX, fromZ)
            );
        }
        if (dx == -1 && dz == 0 && fromZ > 0 && fromZ < SIZE) {
            return new DirectedSides(
                    cell(toX, fromZ), cell(toX, fromZ - 1)
            );
        }
        if (dz == 1 && dx == 0 && fromX > 0 && fromX < SIZE) {
            return new DirectedSides(
                    cell(fromX, fromZ), cell(fromX - 1, fromZ)
            );
        }
        if (dz == -1 && dx == 0 && fromX > 0 && fromX < SIZE) {
            return new DirectedSides(
                    cell(fromX - 1, toZ), cell(fromX, toZ)
            );
        }
        return null;
    }

    private static int countChanges(Region[] raw, Region[] result) {
        int changes = 0;
        for (int value = 0; value < CELLS; value++) {
            if (!raw[value].equals(result[value])) changes++;
        }
        return changes;
    }

    private static CellPair separatedCells(int from, int to) {
        int fromX = vertexX(from);
        int fromZ = vertexZ(from);
        int toX = vertexX(to);
        int toZ = vertexZ(to);
        if (fromX == toX && Math.abs(fromZ - toZ) == 1) {
            int z = Math.min(fromZ, toZ);
            return fromX > 0 && fromX < SIZE
                    ? new CellPair(cell(fromX - 1, z), cell(fromX, z))
                    : null;
        }
        if (fromZ == toZ && Math.abs(fromX - toX) == 1) {
            int x = Math.min(fromX, toX);
            return fromZ > 0 && fromZ < SIZE
                    ? new CellPair(cell(x, fromZ - 1), cell(x, fromZ))
                    : null;
        }
        return null;
    }

    private static void block(boolean[] walls, int first, int second) {
        int difference = second - first;
        int firstDirection;
        int secondDirection;
        if (difference == 1) {
            firstDirection = 1; secondDirection = 3;
        } else if (difference == -1) {
            firstDirection = 3; secondDirection = 1;
        } else if (difference == SIZE) {
            firstDirection = 2; secondDirection = 0;
        } else {
            firstDirection = 0; secondDirection = 2;
        }
        walls[first * 4 + firstDirection] = true;
        walls[second * 4 + secondDirection] = true;
    }

    private static int cellNeighbor(int value, int direction) {
        int x = value % SIZE;
        int z = value / SIZE;
        return switch (direction) {
            case 0 -> z > 0 ? value - SIZE : -1;
            case 1 -> x < SIZE - 1 ? value + 1 : -1;
            case 2 -> z < SIZE - 1 ? value + SIZE : -1;
            default -> x > 0 ? value - 1 : -1;
        };
    }

    private static int[] neighbors(int value) {
        int x = vertexX(value);
        int z = vertexZ(value);
        int[] values = new int[4];
        int count = 0;
        if (z > 0) values[count++] = vertex(x, z - 1);
        if (x < SIZE) values[count++] = vertex(x + 1, z);
        if (z < SIZE) values[count++] = vertex(x, z + 1);
        if (x > 0) values[count++] = vertex(x - 1, z);
        return Arrays.copyOf(values, count);
    }

    private static void addEdge(
            List<Integer>[] graph, boolean[] used, int first, int second
    ) {
        if (graph[first] == null) graph[first] = new ArrayList<>(2);
        if (graph[second] == null) graph[second] = new ArrayList<>(2);
        graph[first].add(second);
        graph[second].add(first);
        used[first] = true;
        used[second] = true;
    }

    private static boolean differentPair(
            Region a, Region b, Region first, Region second
    ) {
        return !a.equals(b) && isPair(a, first, second)
                && isPair(b, first, second);
    }

    private static boolean isPair(Region value, Region first, Region second) {
        return value.equals(first) || value.equals(second);
    }

    private static boolean onPerimeter(int value) {
        int x = vertexX(value);
        int z = vertexZ(value);
        return x == 0 || z == 0 || x == SIZE || z == SIZE;
    }

    private static Portal portal(int value) {
        return new Portal(vertexX(value), vertexZ(value));
    }

    private static boolean validPortal(Portal raw, Portal candidate) {
        if (candidate == null || candidate.x() < 0 || candidate.x() > SIZE
                || candidate.z() < 0 || candidate.z() > SIZE) return false;
        if (raw.x() == 0) return candidate.x() == 0;
        if (raw.x() == SIZE) return candidate.x() == SIZE;
        if (raw.z() == 0) return candidate.z() == 0;
        if (raw.z() == SIZE) return candidate.z() == SIZE;
        return false;
    }

    private static int cell(int x, int z) {
        return RegionBoundaryRouter.index(x, z);
    }

    private static int vertex(int x, int z) {
        return z * VERTEX_SIZE + x;
    }

    private static int vertexX(int value) { return value % VERTEX_SIZE; }
    private static int vertexZ(int value) { return value / VERTEX_SIZE; }

    private record RawBoundary(
            int start, int end, boolean[] vertices,
            List<Integer> orderedPath
    ) {}
    private record CellPair(int first, int second) {}
    record EdgeIdentity(
            ResourceLocation first, ResourceLocation second
    ) {
        static EdgeIdentity of(
                ResourceLocation first, ResourceLocation second
        ) {
            if (first == null || second == null || first.equals(second)) {
                return null;
            }
            return first.toString().compareTo(second.toString()) <= 0
                    ? new EdgeIdentity(first, second)
                    : new EdgeIdentity(second, first);
        }
    }
    private record EdgeRunCandidate(
            int runId, int rawDistance, int edgeCount,
            EdgeIdentity identity
    ) {}
    private record NaturalRuns(
            int[] runByEdge, EdgeIdentity[] identityByRun,
            int[] candidateByRun, List<EdgeRunCandidate> candidates,
            EdgeRunDiagnostics diagnostics
    ) {}
    private record PathSearch(
            List<Integer> path, int cost, int preferredEdges,
            int handoffs
    ) {}
    private record RoundedPath(List<Integer> path, int changedSteps) {}
    private record DirectedSides(int leftCell, int rightCell) {}
    private record SideIdentity(Region leftOwner, Region rightOwner) {}
    private record FillResult(
            Region[] regions, RejectionReason rejectionReason
    ) {
        private static FillResult accepted(Region[] regions) {
            return new FillResult(regions, RejectionReason.NONE);
        }

        private static FillResult rejected(RejectionReason reason) {
            return new FillResult(null, reason);
        }
    }
    private record Node(int state, int cost) implements Comparable<Node> {
        @Override
        public int compareTo(Node other) {
            int byCost = Integer.compare(cost, other.cost);
            return byCost != 0 ? byCost : Integer.compare(state, other.state);
        }
    }

    private static final class UnionFind {
        private final int[] parent;
        private final byte[] rank;

        private UnionFind(int size) {
            parent = new int[size];
            rank = new byte[size];
            Arrays.fill(parent, -1);
        }

        private void activate(int value) { parent[value] = value; }

        private int find(int value) {
            int root = value;
            while (parent[root] != root) root = parent[root];
            while (value != root) {
                int next = parent[value];
                parent[value] = root;
                value = next;
            }
            return root;
        }

        private void union(int first, int second) {
            int firstRoot = find(first);
            int secondRoot = find(second);
            if (firstRoot == secondRoot) return;
            if (rank[firstRoot] < rank[secondRoot]) {
                parent[firstRoot] = secondRoot;
            } else {
                parent[secondRoot] = firstRoot;
                if (rank[firstRoot] == rank[secondRoot]) rank[firstRoot]++;
            }
        }
    }

    record Portal(int x, int z, EdgeIdentity edgeIdentity) {
        Portal(int x, int z) { this(x, z, null); }
    }

    @FunctionalInterface
    interface PortalResolver {
        Portal resolve(Portal rawPortal);
    }
}
