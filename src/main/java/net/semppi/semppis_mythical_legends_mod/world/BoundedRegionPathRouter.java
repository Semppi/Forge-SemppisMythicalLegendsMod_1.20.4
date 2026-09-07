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
    private static final int MAX_PREFERRED_EDGE_RUNS = 16;
    private static final int MIN_PREFERRED_RUN_EDGES = 4;
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
            int changedCells, int preferredEdgeSteps
    ) {
        private static RouteResult rejected(RejectionReason reason) {
            return new RouteResult(null, reason, 0, 0);
        }
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
        NaturalRuns naturalRuns = naturalRuns(biomes, rawDistance);
        PathSearch selected = null;
        for (EdgeRunCandidate candidate : naturalRuns.candidates()) {
            PathSearch current = shortestPath(
                    start, end, rawDistance, raw, biomes, rivers,
                    naturalRuns.runByEdge(), candidate.runId()
            );
            if (current == null
                    || current.preferredEdges() < MIN_PREFERRED_RUN_EDGES) {
                continue;
            }
            if (selected == null || current.cost() < selected.cost()
                    || current.cost() == selected.cost()
                    && current.preferredEdges() > selected.preferredEdges()
                    || current.cost() == selected.cost()
                    && current.preferredEdges() == selected.preferredEdges()
                    && candidate.runId() < selected.preferredRun()) {
                selected = current;
            }
        }
        if (selected == null) {
            selected = shortestPath(
                    start, end, rawDistance, raw, biomes, rivers,
                    naturalRuns.runByEdge(), -1
            );
        }
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
        FillResult fill = fillSides(
                raw, pathWalls(path), first, second,
                startIdentity, endIdentity, routedStart, routedEnd
        );
        if (fill.regions() == null) {
            return RouteResult.rejected(fill.rejectionReason());
        }
        int changes = countChanges(raw, fill.regions());
        return changes >= 4
                ? new RouteResult(
                        fill.regions(), RejectionReason.NONE, changes,
                        selected.preferredEdges()
                )
                : RouteResult.rejected(RejectionReason.NO_MEANINGFUL_CHANGE);
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
            int[] runByEdge, int preferredRun
    ) {
        int[] cost = new int[VERTICES];
        int[] previous = new int[VERTICES];
        Arrays.fill(cost, INF);
        Arrays.fill(previous, -1);
        PriorityQueue<Node> open = new PriorityQueue<>();
        cost[start] = 0;
        open.add(new Node(start, 0));
        while (!open.isEmpty()) {
            Node node = open.remove();
            if (node.cost() != cost[node.vertex()]) continue;
            if (node.vertex() == end) break;
            for (int next : neighbors(node.vertex())) {
                if (next != end && onPerimeter(next)) continue;
                if (rawDistance[next] > MAX_DISTANCE_FROM_RAW) continue;
                int step = edgeCost(node.vertex(), next,
                        rawDistance[next], raw, biomes, rivers,
                        runByEdge, preferredRun);
                if (step >= INF) continue;
                int candidate = node.cost() + step;
                if (candidate < cost[next]
                        || candidate == cost[next]
                        && node.vertex() < previous[next]) {
                    cost[next] = candidate;
                    previous[next] = node.vertex();
                    open.add(new Node(next, candidate));
                }
            }
        }
        if (cost[end] >= INF) return null;
        List<Integer> path = new ArrayList<>();
        for (int current = end; current >= 0;
             current = previous[current]) {
            path.add(current);
            if (current == start) break;
        }
        if (path.get(path.size() - 1) != start) return null;
        Collections.reverse(path);
        int preferredEdges = 0;
        if (preferredRun >= 0) {
            for (int index = 1; index < path.size(); index++) {
                int slot = edgeSlot(path.get(index - 1), path.get(index));
                if (slot >= 0 && runByEdge[slot] == preferredRun) {
                    preferredEdges++;
                }
            }
        }
        return new PathSearch(
                path, cost[end], preferredRun, preferredEdges
        );
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
            ResourceLocation[] biomes, int[] rawDistance
    ) {
        int edgeSlots = VERTICES * 2;
        BiomePair[] pairs = new BiomePair[edgeSlots];
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
        }
        List<EdgeRunCandidate> candidates = new ArrayList<>();
        for (int run = 0; run < runCount; run++) {
            if (edgeCount[run] >= MIN_PREFERRED_RUN_EDGES
                    && minimumDistance[run] <= MAX_DISTANCE_FROM_RAW) {
                candidates.add(new EdgeRunCandidate(
                        run, minimumDistance[run], edgeCount[run]
                ));
            }
        }
        candidates.sort((left, right) -> {
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
        if (candidates.size() > MAX_PREFERRED_EDGE_RUNS) {
            candidates = new ArrayList<>(candidates.subList(
                    0, MAX_PREFERRED_EDGE_RUNS
            ));
        }
        return new NaturalRuns(runByEdge, candidates);
    }

    private static void registerNaturalEdge(
            int from, int to, ResourceLocation[] biomes,
            BiomePair[] pairs, UnionFind union
    ) {
        CellPair cells = separatedCells(from, to);
        if (cells == null) return;
        ResourceLocation first = biomes[cells.first()];
        ResourceLocation second = biomes[cells.second()];
        if (first == null || second == null || first.equals(second)) return;
        int slot = edgeSlot(from, to);
        pairs[slot] = BiomePair.of(first, second);
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
    private record BiomePair(
            ResourceLocation first, ResourceLocation second
    ) {
        private static BiomePair of(
                ResourceLocation first, ResourceLocation second
        ) {
            return first.toString().compareTo(second.toString()) <= 0
                    ? new BiomePair(first, second)
                    : new BiomePair(second, first);
        }
    }
    private record EdgeRunCandidate(
            int runId, int rawDistance, int edgeCount
    ) {}
    private record NaturalRuns(
            int[] runByEdge, List<EdgeRunCandidate> candidates
    ) {}
    private record PathSearch(
            List<Integer> path, int cost, int preferredRun,
            int preferredEdges
    ) {}
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
    private record Node(int vertex, int cost) implements Comparable<Node> {
        @Override
        public int compareTo(Node other) {
            int byCost = Integer.compare(cost, other.cost);
            return byCost != 0 ? byCost : Integer.compare(vertex, other.vertex);
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

    record Portal(int x, int z) {}

    @FunctionalInterface
    interface PortalResolver {
        Portal resolve(Portal rawPortal);
    }
}
