package net.semppi.semppis_mythical_legends_mod.world;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Assigns directions to one already-selected continent using its cached macro
 * climate survey. Geometry and continent identity remain unchanged.
 */
public final class ClimateDirectionAssignment {
    private static final SubDir[] DIRECTIONS = SubDir.values();
    private static final Map<ServerLevel, Map<Long, Assignment>> CACHE =
            new WeakHashMap<>();

    private ClimateDirectionAssignment() {}

    public static Region landRegion(ServerLevelAccessor level, int x, int z) {
        long seed = level.getLevel().getSeed();
        AuthoritativeRegionSampler.GeometrySample geometry =
                AuthoritativeRegionSampler.geometrySample(seed, x, z);
        Assignment assignment = assignment(level, geometry, x, z);
        return Region.land(
                geometry.continent(),
                assignment.directions().get(geometry.childIndex())
        );
    }

    public static AuthoritativeRegionSampler.ChildAdjacency childAdjacency(
            ServerLevelAccessor level, int x, int z) {
        long seed = level.getLevel().getSeed();
        AuthoritativeRegionSampler.GeometrySample geometry =
                AuthoritativeRegionSampler.geometrySample(seed, x, z);
        Assignment assignment = assignment(level, geometry, x, z);
        int selected = geometry.childIndex();
        List<AuthoritativeRegionSampler.ChildNeighbor> neighbors =
                new ArrayList<>();
        for (int neighbor : neighborIndices(selected, geometry.childCount())) {
            neighbors.add(new AuthoritativeRegionSampler.ChildNeighbor(
                    neighbor, assignment.directions().get(neighbor)
            ));
        }
        return new AuthoritativeRegionSampler.ChildAdjacency(
                geometry.parentKey(), selected, geometry.childCount(),
                Region.land(
                        geometry.continent(),
                        assignment.directions().get(selected)
                ),
                neighbors
        );
    }

    public static List<SubDir> assignedDirections(
            ServerLevelAccessor level, int x, int z) {
        long seed = level.getLevel().getSeed();
        AuthoritativeRegionSampler.GeometrySample geometry =
                AuthoritativeRegionSampler.geometrySample(seed, x, z);
        return assignment(level, geometry, x, z).directions();
    }

    private static Assignment assignment(
            ServerLevelAccessor level,
            AuthoritativeRegionSampler.GeometrySample geometry,
            int x, int z) {
        synchronized (CACHE) {
            Assignment cached = CACHE
                    .computeIfAbsent(
                            level.getLevel(), ignored -> new LinkedHashMap<>()
                    )
                    .get(geometry.parentKey());
            if (cached != null) return cached;
        }

        MacroClimateSurvey.ClusterSurvey survey =
                MacroClimateSurvey.survey(level, x, z);
        Assignment computed = compute(geometry, survey);
        synchronized (CACHE) {
            return CACHE
                    .computeIfAbsent(
                            level.getLevel(), ignored -> new LinkedHashMap<>()
                    )
                    .computeIfAbsent(
                            geometry.parentKey(), ignored -> computed
                    );
        }
    }

    private static Assignment compute(
            AuthoritativeRegionSampler.GeometrySample geometry,
            MacroClimateSurvey.ClusterSurvey survey) {
        int count = geometry.childCount();
        if (geometry.continent() == Continent.ANTARCTICA) {
            return new Assignment(
                    java.util.Collections.nCopies(count, SubDir.CENTRAL)
            );
        }

        int[][] scores = new int[count][DIRECTIONS.length];
        for (int child = 0; child < count; child++) {
            MacroClimateSurvey.ClimateSummary climate =
                    survey.children().get(child);
            for (int direction = 0;
                 direction < DIRECTIONS.length; direction++) {
                scores[child][direction] = climateScore(
                        climate, geometry.continent(), DIRECTIONS[direction]
                );
            }
        }

        Search search = new Search(
                geometry.parentKey(), geometry.continent(), scores
        );
        search.visit(0);
        return new Assignment(Arrays.stream(search.best())
                .mapToObj(index -> DIRECTIONS[index])
                .toList());
    }

    private static int climateScore(
            MacroClimateSurvey.ClimateSummary summary,
            Continent continent, SubDir direction) {
        int score = 0;
        for (Map.Entry<ResourceLocation, Integer> entry
                : summary.biomeSamples().entrySet()) {
            int weight = TagRules.biomeProfile(entry.getKey())
                    .placementWeight();
            if (weight == 0) continue;
            score += entry.getValue() * weight
                    * TagRules.directionAffinity(
                            continent, direction, entry.getKey()
                    ).score();
        }
        return score;
    }

    private static List<Integer> neighborIndices(int child, int count) {
        List<Integer> neighbors = new ArrayList<>();
        if (child == 0) {
            for (int ring = 1; ring < count; ring++) neighbors.add(ring);
            return neighbors;
        }
        neighbors.add(0);
        int previous = child == 1 ? count - 1 : child - 1;
        int next = child == count - 1 ? 1 : child + 1;
        neighbors.add(previous);
        if (next != previous) neighbors.add(next);
        return neighbors;
    }

    private record Assignment(List<SubDir> directions) {
        private Assignment {
            directions = List.copyOf(directions);
        }
    }

    /** Exhaustive search is bounded by 5^8 = 390,625 assignments. */
    private static final class Search {
        private final long parentKey;
        private final Continent continent;
        private final int[][] scores;
        private final int[] current;
        private int[] best;
        private int bestScore = Integer.MIN_VALUE;
        private int bestOriginalMatches = Integer.MIN_VALUE;
        private long bestTie;

        private Search(long parentKey, Continent continent, int[][] scores) {
            this.parentKey = parentKey;
            this.continent = continent;
            this.scores = scores;
            this.current = new int[scores.length];
        }

        private void visit(int child) {
            if (child < current.length) {
                for (int direction = 0;
                     direction < DIRECTIONS.length; direction++) {
                    current[child] = direction;
                    visit(child + 1);
                }
                return;
            }

            if (distinctCount() != Math.min(current.length, DIRECTIONS.length)
                    || !repeatsAreConnected()) {
                return;
            }

            int score = 0;
            int originalMatches = 0;
            long tie = parentKey;
            for (int index = 0; index < current.length; index++) {
                score += scores[index][current[index]];
                SubDir direction = DIRECTIONS[current[index]];
                if (direction == AuthoritativeRegionSampler.initialDirection(
                        parentKey, index, continent
                )) {
                    originalMatches++;
                }
                tie = mix64(tie ^ ((long) index << 32) ^ direction.ordinal());
            }

            if (best == null
                    || score > bestScore
                    || (score == bestScore
                    && originalMatches > bestOriginalMatches)
                    || (score == bestScore
                    && originalMatches == bestOriginalMatches
                    && Long.compareUnsigned(tie, bestTie) < 0)) {
                best = current.clone();
                bestScore = score;
                bestOriginalMatches = originalMatches;
                bestTie = tie;
            }
        }

        private int distinctCount() {
            boolean[] used = new boolean[DIRECTIONS.length];
            int count = 0;
            for (int direction : current) {
                if (!used[direction]) {
                    used[direction] = true;
                    count++;
                }
            }
            return count;
        }

        private boolean repeatsAreConnected() {
            for (int direction = 0;
                 direction < DIRECTIONS.length; direction++) {
                int first = -1;
                int expected = 0;
                for (int child = 0; child < current.length; child++) {
                    if (current[child] == direction) {
                        expected++;
                        if (first < 0) first = child;
                    }
                }
                if (expected < 2) continue;

                boolean[] visited = new boolean[current.length];
                ArrayDeque<Integer> open = new ArrayDeque<>();
                open.add(first);
                visited[first] = true;
                int reached = 0;
                while (!open.isEmpty()) {
                    int child = open.removeFirst();
                    reached++;
                    for (int neighbor : neighborIndices(
                            child, current.length
                    )) {
                        if (!visited[neighbor]
                                && current[neighbor] == direction) {
                            visited[neighbor] = true;
                            open.addLast(neighbor);
                        }
                    }
                }
                if (reached != expected) return false;
            }
            return true;
        }

        private int[] best() {
            if (best == null) {
                throw new IllegalStateException(
                        "No connected direction assignment found"
                );
            }
            return best;
        }

        private static long mix64(long value) {
            value ^= value >>> 33;
            value *= 0xFF51AFD7ED558CCDL;
            value ^= value >>> 33;
            value *= 0xC4CEB9FE1A85EC53L;
            value ^= value >>> 33;
            return value;
        }
    }
}
