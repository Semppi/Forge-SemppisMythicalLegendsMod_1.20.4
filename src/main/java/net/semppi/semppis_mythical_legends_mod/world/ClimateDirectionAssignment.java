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
 * Selects a conservative parent-continent identity and assigns its child
 * directions using the cached macro climate survey. Candidate geometry never
 * changes. The internal ANTARCTICA identity is exposed to players as the rare
 * directionless Frozen Pole region.
 */
public final class ClimateDirectionAssignment {
    private static final SubDir[] DIRECTIONS = SubDir.values();
    private static final int FROZEN_POLE_RARITY = 4;
    private static final int MIN_FROZEN_POLE_LOWLAND_SAMPLES = 48;
    private static final Map<ServerLevel, Map<Long, Assignment>> CACHE =
            new WeakHashMap<>();

    private ClimateDirectionAssignment() {}

    public static Region landRegion(ServerLevelAccessor level, int x, int z) {
        long seed = level.getLevel().getSeed();
        AuthoritativeRegionSampler.GeometrySample geometry =
                AuthoritativeRegionSampler.geometrySample(seed, x, z);
        Assignment assignment = assignment(level, geometry, x, z);
        return Region.land(
                assignment.continent(),
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
                        assignment.continent(),
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

    public static Continent assignedContinent(
            ServerLevelAccessor level, int x, int z) {
        long seed = level.getLevel().getSeed();
        AuthoritativeRegionSampler.GeometrySample geometry =
                AuthoritativeRegionSampler.geometrySample(seed, x, z);
        return assignment(level, geometry, x, z).continent();
    }

    public static ContinentDecision continentDecision(
            ServerLevelAccessor level, int x, int z) {
        long seed = level.getLevel().getSeed();
        AuthoritativeRegionSampler.GeometrySample geometry =
                AuthoritativeRegionSampler.geometrySample(seed, x, z);
        MacroClimateSurvey.ClusterSurvey survey =
                MacroClimateSurvey.survey(level, x, z);
        Assignment assignment = assignment(level, geometry, x, z);
        return new ContinentDecision(
                geometry.continent(), assignment.continent(),
                survey.parent().placementWeight(),
                unavoidableRejectionWeight(
                        survey, geometry.continent()
                ),
                unavoidableRejectionWeight(
                        survey, assignment.continent()
                )
        );
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
        Continent continent = chooseContinent(geometry, survey);
        if (continent == Continent.ANTARCTICA) {
            return new Assignment(
                    continent,
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
                        climate, continent, DIRECTIONS[direction]
                );
            }
        }

        Search search = new Search(
                geometry.parentKey(), continent, scores
        );
        search.visit(0);
        return new Assignment(
                continent,
                Arrays.stream(search.best())
                        .mapToObj(index -> DIRECTIONS[index])
                        .toList()
        );
    }

    /**
     * A continent is retained whenever at least half of the weighted climate
     * can be inherited by some direction. A replacement must cross back below
     * that majority-rejection line and improve unavoidable rejection by at
     * least ten percent (with a four-point floor for sparse surveys).
     */
    private static Continent chooseContinent(
            AuthoritativeRegionSampler.GeometrySample geometry,
            MacroClimateSurvey.ClusterSurvey survey) {
        Continent initial = geometry.continent();

        boolean frozenPole = qualifiesAsFrozenPole(survey.parent())
                && Math.floorMod(
                        mix64(geometry.parentKey()), FROZEN_POLE_RARITY
                ) == 0;
        if (frozenPole) return Continent.ANTARCTICA;

        // A failed polar roll must always inherit an ordinary continent. This
        // also removes hot or forested ANTARCTICA identities produced by the
        // old geometry-only hash.
        if (initial == Continent.ANTARCTICA) {
            return bestOrdinaryContinent(geometry, survey);
        }

        int totalWeight = survey.parent().placementWeight();
        if (totalWeight == 0) return initial;

        int initialRejected = unavoidableRejectionWeight(
                survey, initial
        );
        if ((long) initialRejected * 2L <= totalWeight) {
            return initial;
        }

        int requiredImprovement = Math.max(
                4, (totalWeight + 9) / 10
        );
        Continent best = initial;
        int bestRejected = initialRejected;
        int bestScore = continentScore(survey.parent(), initial);
        long bestTie = mix64(geometry.parentKey() ^ initial.ordinal());

        for (Continent candidate : Continent.values()) {
            if (candidate == initial
                    || candidate == Continent.ANTARCTICA) {
                continue;
            }

            int rejected = unavoidableRejectionWeight(
                    survey, candidate
            );
            if ((long) rejected * 2L > totalWeight
                    || initialRejected - rejected < requiredImprovement) {
                continue;
            }

            int score = continentScore(survey.parent(), candidate);
            long tie = mix64(
                    geometry.parentKey() ^ candidate.ordinal()
            );
            if (best == initial
                    || rejected < bestRejected
                    || (rejected == bestRejected && score > bestScore)
                    || (rejected == bestRejected && score == bestScore
                    && Long.compareUnsigned(tie, bestTie) < 0)) {
                best = candidate;
                bestRejected = rejected;
                bestScore = score;
                bestTie = tie;
            }
        }
        return best;
    }

    /**
     * Frozen Pole requires a sizeable frozen-barren lowland province. Context
     * and mountains are excluded, while only a small sampled edge allowance is
     * made for forest and ordinary open terrain.
     */
    private static boolean qualifiesAsFrozenPole(
            MacroClimateSurvey.ClimateSummary climate) {
        int lowland = climate.totalSamples()
                - climate.contextualSamples() - climate.mountainSamples();
        if (lowland < MIN_FROZEN_POLE_LOWLAND_SAMPLES) return false;
        if ((long) climate.frozenBarrenSamples() * 10L
                < (long) lowland * 7L) {
            return false;
        }
        if ((long) climate.forestSamples() * 20L > lowland) return false;
        return (long) climate.openLowlandSamples() * 20L <= lowland;
    }

    private static Continent bestOrdinaryContinent(
            AuthoritativeRegionSampler.GeometrySample geometry,
            MacroClimateSurvey.ClusterSurvey survey) {
        Continent best = null;
        int bestRejected = Integer.MAX_VALUE;
        int bestScore = Integer.MIN_VALUE;
        long bestTie = 0L;
        for (Continent candidate : Continent.values()) {
            if (candidate == Continent.ANTARCTICA) continue;
            int rejected = unavoidableRejectionWeight(survey, candidate);
            int score = continentScore(survey.parent(), candidate);
            long tie = mix64(geometry.parentKey() ^ candidate.ordinal());
            if (best == null
                    || rejected < bestRejected
                    || (rejected == bestRejected && score > bestScore)
                    || (rejected == bestRejected && score == bestScore
                    && Long.compareUnsigned(tie, bestTie) < 0)) {
                best = candidate;
                bestRejected = rejected;
                bestScore = score;
                bestTie = tie;
            }
        }
        return best;
    }

    private static int unavoidableRejectionWeight(
            MacroClimateSurvey.ClusterSurvey survey,
            Continent continent) {
        int rejected = 0;
        for (MacroClimateSurvey.ClimateSummary child : survey.children()) {
            int bestDirectionRejection = Integer.MAX_VALUE;
            for (SubDir direction : DIRECTIONS) {
                int directionRejection = 0;
                for (Map.Entry<ResourceLocation, Integer> entry
                        : child.biomeSamples().entrySet()) {
                    int weight = TagRules.biomeProfile(entry.getKey())
                            .placementWeight();
                    if (weight > 0
                            && TagRules.directionAffinity(
                                    continent, direction, entry.getKey()
                            ) == TagRules.Affinity.STRONGLY_UNSUITABLE) {
                        directionRejection += entry.getValue() * weight;
                    }
                }
                bestDirectionRejection = Math.min(
                        bestDirectionRejection, directionRejection
                );
            }
            rejected += bestDirectionRejection;
        }
        return rejected;
    }

    private static int continentScore(
            MacroClimateSurvey.ClimateSummary summary,
            Continent continent) {
        int score = 0;
        for (Map.Entry<ResourceLocation, Integer> entry
                : summary.biomeSamples().entrySet()) {
            int weight = TagRules.biomeProfile(entry.getKey())
                    .placementWeight();
            if (weight == 0) continue;
            score += entry.getValue() * weight
                    * TagRules.continentAffinity(
                            continent, entry.getKey()
                    ).score();
        }
        return score;
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

    private record Assignment(Continent continent,
                              List<SubDir> directions) {
        private Assignment {
            directions = List.copyOf(directions);
        }
    }

    public record ContinentDecision(
            Continent initial,
            Continent assigned,
            int totalWeight,
            int initialRejectedWeight,
            int assignedRejectedWeight
    ) {}

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
