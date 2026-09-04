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
    private static final int INF = 1_000_000_000;

    private BoundedRegionPathRouter() {}

    enum RejectionReason {
        NONE,
        BRANCHED_OR_DISCONNECTED_RAW,
        INVALID_PORTAL,
        NO_BOUNDED_PATH,
        UNCHANGED_PATH,
        TOPOLOGY_REJECTED,
        NO_MEANINGFUL_CHANGE
    }

    record RouteResult(Region[] regions, RejectionReason rejectionReason) {
        private static RouteResult accepted(Region[] regions) {
            return new RouteResult(regions, RejectionReason.NONE);
        }

        private static RouteResult rejected(RejectionReason reason) {
            return new RouteResult(null, reason);
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
        List<Integer> path = shortestPath(
                start, end, rawDistance,
                raw, biomes, rivers
        );
        if (path == null) {
            return RouteResult.rejected(RejectionReason.NO_BOUNDED_PATH);
        }
        if (path.equals(boundary.orderedPath())) {
            return RouteResult.rejected(RejectionReason.UNCHANGED_PATH);
        }
        Region[] result = fillSides(
                raw, pathWalls(path), first, second
        );
        if (result == null) {
            return RouteResult.rejected(RejectionReason.TOPOLOGY_REJECTED);
        }
        return hasMeaningfulChange(raw, result)
                ? RouteResult.accepted(result)
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

    private static List<Integer> shortestPath(
            int start, int end, int[] rawDistance,
            Region[] raw, ResourceLocation[] biomes, boolean[] rivers
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
                        rawDistance[next], raw, biomes, rivers);
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
        return path;
    }

    private static int edgeCost(
            int from, int to, int rawDistance,
            Region[] raw, ResourceLocation[] biomes, boolean[] rivers
    ) {
        CellPair pair = separatedCells(from, to);
        if (pair == null) return INF;
        ResourceLocation a = biomes[pair.first()];
        ResourceLocation b = biomes[pair.second()];
        boolean natural = a != null && b != null && !a.equals(b);
        boolean river = rivers[pair.first()] || rivers[pair.second()];
        boolean rawEdge = !raw[pair.first()].equals(raw[pair.second()]);
        if (natural) {
            return Math.max(1, 2 + rawDistance / 8 - (river ? 1 : 0));
        }
        if (rawEdge) return 8;
        return 20 + rawDistance / 2;
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
    private static Region[] fillSides(
            Region[] raw, boolean[] walls, Region first, Region second
    ) {
        int[] component = new int[CELLS];
        Arrays.fill(component, -1);
        ArrayDeque<Integer> queue = new ArrayDeque<>();
        int components = 0;
        for (int seed = 0; seed < CELLS; seed++) {
            if (component[seed] >= 0) continue;
            if (components >= 2) return null;
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
        if (components != 2) return null;

        int[][] votes = new int[2][2];
        boolean[] touchesPerimeter = new boolean[2];
        for (int value = 0; value < CELLS; value++) {
            int owner = raw[value].equals(first) ? 0
                    : raw[value].equals(second) ? 1 : -1;
            if (owner < 0) return null;
            votes[component[value]][owner]++;
            int x = value % SIZE;
            int z = value / SIZE;
            if (x == 0 || z == 0 || x == SIZE - 1 || z == SIZE - 1) {
                touchesPerimeter[component[value]] = true;
            }
        }
        if (!touchesPerimeter[0] || !touchesPerimeter[1]) return null;
        int owner0 = majority(votes[0]);
        int owner1 = majority(votes[1]);
        if (owner0 < 0 || owner1 < 0 || owner0 == owner1) return null;

        Region[] result = new Region[CELLS];
        Region[] owners = {owner0 == 0 ? first : second,
                owner1 == 0 ? first : second};
        for (int value = 0; value < CELLS; value++) {
            result[value] = owners[component[value]];
        }
        return result;
    }

    private static int majority(int[] votes) {
        if (votes[0] == votes[1]) return -1;
        return votes[0] > votes[1] ? 0 : 1;
    }

    private static boolean hasMeaningfulChange(Region[] raw, Region[] result) {
        int changes = 0;
        for (int value = 0; value < CELLS; value++) {
            if (!raw[value].equals(result[value]) && ++changes >= 4) {
                return true;
            }
        }
        return false;
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
    private record Node(int vertex, int cost) implements Comparable<Node> {
        @Override
        public int compareTo(Node other) {
            int byCost = Integer.compare(cost, other.cost);
            return byCost != 0 ? byCost : Integer.compare(vertex, other.vertex);
        }
    }

    record Portal(int x, int z) {}

    @FunctionalInterface
    interface PortalResolver {
        Portal resolve(Portal rawPortal);
    }
}
