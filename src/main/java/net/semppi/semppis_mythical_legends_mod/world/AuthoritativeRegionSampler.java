package net.semppi.semppis_mythical_legends_mod.world;

import java.util.ArrayList;
import java.util.List;

/**
 * The seed-and-coordinate authority for continental candidate geometry.
 *
 * <p>This sampler never reads chunks, biomes, height, game time or mutable
 * world state. Given the same algorithm version, world seed and X/Z, it always
 * returns the same candidate. Macro climate may relabel child directions but
 * never changes these shapes or creates a second layout.</p>
 */
public final class AuthoritativeRegionSampler {
    public static final int ALGORITHM_VERSION = 3;

    /**
     * Continents are intentionally enormous. The protected origin lies near
     * the centre of one of these cells rather than on a grid intersection.
     */
    public static final int CONTINENT_SCALE = 24_576;

    public static final int MIN_SUBREGIONS = 3;
    public static final int MAX_SUBREGIONS = 8;

    private static final double JITTER = 0.22;
    private static final double[] WARP_WAVELENGTHS = {1.80, 0.85, 0.40};
    private static final double[] WARP_AMPLITUDES = {0.060, 0.030, 0.012};
    private static final double[][] WARP_ROTATIONS = {
            {1.0, 0.0},
            {0.81915204429, 0.57357643635},
            {0.37460659341, -0.92718385456}
    };
    private static final double CHILD_RING_MIN = 0.28;
    private static final double CHILD_RING_VARIATION = 0.10;

    private static final long SALT_CONTINENT_SITE = 0x5C4A7D39B821E60FL;
    private static final long SALT_CONTINENT_PICK = 0x19E3B6C74A502D8FL;
    private static final long SALT_SUBREGION_COUNT = 0x73D91A46C2E85B0FL;
    private static final long SALT_SUBREGION_POSITION = 0x2A8F50D1E7634CB9L;
    private static final long SALT_SUBREGION_PICK = 0x41C7E95A2B603D8FL;
    private static final long SALT_OCEAN_SITE = 0x64B2F907D13A5CE8L;
    private static final long SALT_OCEAN_PICK = 0x3D75A9C1E60B42F8L;
    private static final long SALT_WARP_X = 0x7A2C91E5D40B63F8L;
    private static final long SALT_WARP_Z = 0x16E8B34F9A70C25DL;
    private static final long SALT_WARP_OCTAVE = 0x4F1BBCDCBFA54001L;
    private static final long SITE_KEY_SALT = 0x6A09E667F3BCC909L;

    private AuthoritativeRegionSampler() {}

    public static Region landRegion(long worldSeed, int x, int z) {
        Site continentSite = nearestSite(
                worldSeed, x, z, CONTINENT_SCALE,
                SALT_CONTINENT_SITE, true
        );
        Continent continent = pickContinent(
                mix64(continentSite.key() ^ SALT_CONTINENT_PICK)
        );
        int childCount = childCount(continentSite);
        int childIndex = nearestChildIndex(
                worldSeed, continentSite, childCount
        );

        SubDir direction = continent == Continent.ANTARCTICA
                ? SubDir.CENTRAL
                : initialDirection(continentSite.key(), childIndex, continent);

        return Region.land(continent, direction);
    }

    /**
     * Exposes the logical topology of the selected child without adding work
     * to ordinary {@link #landRegion(long, int, int)} lookups. Child zero is
     * the cluster centre; ring children connect to it and to their previous
     * and next ring neighbors. The graph is always connected and remains
     * entirely inside one parent continent.
     */
    public static ChildAdjacency childAdjacency(long worldSeed, int x, int z) {
        ChildGeometry geometry = childGeometry(worldSeed, x, z);
        Site parent = geometry.parent();
        Continent continent = pickContinent(
                mix64(parent.key() ^ SALT_CONTINENT_PICK)
        );
        int count = geometry.childCount();
        int selected = geometry.childIndex();
        SubDir selectedDirection = directionFor(
                continent, parent, selected
        );
        List<ChildNeighbor> neighbors = new ArrayList<>();

        if (selected == 0) {
            for (int child = 1; child < count; child++) {
                neighbors.add(new ChildNeighbor(
                        child, directionFor(continent, parent, child)
                ));
            }
        } else {
            neighbors.add(new ChildNeighbor(
                    0, directionFor(continent, parent, 0)
            ));
            int previous = selected == 1 ? count - 1 : selected - 1;
            int next = selected == count - 1 ? 1 : selected + 1;
            neighbors.add(new ChildNeighbor(
                    previous, directionFor(continent, parent, previous)
            ));
            if (next != previous) {
                neighbors.add(new ChildNeighbor(
                        next, directionFor(continent, parent, next)
                ));
            }
        }

        return new ChildAdjacency(
                parent.key(), selected, count,
                Region.land(continent, selectedDirection), neighbors
        );
    }

    /**
     * Returns the final warped parent/child candidate geometry without reading
     * climate or applying any biome-border adjustment. Macro surveys use this
     * to measure the shapes that label assignment will actually receive.
     */
    public static GeometrySample geometrySample(long worldSeed, int x, int z) {
        ChildGeometry geometry = childGeometry(worldSeed, x, z);
        Site parent = geometry.parent();
        double latticeShift = CONTINENT_SCALE * 0.5;
        return new GeometrySample(
                parent.key(), geometry.childIndex(), geometry.childCount(),
                pickContinent(mix64(parent.key() ^ SALT_CONTINENT_PICK)),
                floorToInt(parent.centerX() - latticeShift),
                floorToInt(parent.centerZ() - latticeShift)
        );
    }

    public static Region seaRegion(long worldSeed, int x, int z) {
        Site oceanSite = nearestSite(
                worldSeed, x, z, CONTINENT_SCALE,
                SALT_OCEAN_SITE, true
        );
        Ocean ocean = pickOcean(mix64(oceanSite.key() ^ SALT_OCEAN_PICK));
        return Region.sea(ocean);
    }

    /** Every owning continent creates exactly three to eight child sites. */
    private static int childCount(Site parent) {
        return MIN_SUBREGIONS + (int) Math.floorMod(
                mix64(parent.key() ^ SALT_SUBREGION_COUNT),
                MAX_SUBREGIONS - MIN_SUBREGIONS + 1
        );
    }

    private static int nearestChildIndex(long seed, Site parent, int count) {

        long originParentKey = hash(
                seed, 0, 0, SALT_CONTINENT_SITE ^ SITE_KEY_SALT
        );
        boolean protectsOrigin = parent.key() == originParentKey;

        double clusterCenterX = protectsOrigin
                ? CONTINENT_SCALE * 0.5
                : parent.centerX();
        double clusterCenterZ = protectsOrigin
                ? CONTINENT_SCALE * 0.5
                : parent.centerZ();

        double bestDistance = Double.POSITIVE_INFINITY;
        int bestIndex = 0;

        for (int child = 0; child < count; child++) {
            double childX = clusterCenterX;
            double childZ = clusterCenterZ;

            if (child > 0) {
                int ringCount = count - 1;
                long childHash = hash(
                        parent.key(), child, count, SALT_SUBREGION_POSITION
                );
                double phase = unit(mix64(parent.key() ^ SALT_SUBREGION_POSITION))
                        * Math.PI * 2.0;
                double angularJitter = signedUnit(childHash)
                        * (Math.PI / Math.max(3, ringCount)) * 0.35;
                double angle = phase
                        + Math.PI * 2.0 * (child - 1) / ringCount
                        + angularJitter;
                double radius = CONTINENT_SCALE * (
                        CHILD_RING_MIN
                                + unit(mix64(childHash))
                                * CHILD_RING_VARIATION
                );

                childX += Math.cos(angle) * radius;
                childZ += Math.sin(angle) * radius;
            }

            double deltaX = parent.sampleX() - childX;
            double deltaZ = parent.sampleZ() - childZ;
            double distance = deltaX * deltaX + deltaZ * deltaZ;

            if (distance < bestDistance) {
                bestDistance = distance;
                bestIndex = child;
            }
        }

        return bestIndex;
    }

    private static ChildGeometry childGeometry(long seed, int x, int z) {
        Site parent = nearestSite(
                seed, x, z, CONTINENT_SCALE,
                SALT_CONTINENT_SITE, true
        );
        int count = childCount(parent);
        return new ChildGeometry(
                parent, nearestChildIndex(seed, parent, count), count
        );
    }

    public static SubDir initialDirection(long parentKey, int childIndex,
                                          Continent continent) {
        if (continent == Continent.ANTARCTICA) {
            return SubDir.CENTRAL;
        }
        long value = hash(
                parentKey, childIndex, ALGORITHM_VERSION,
                SALT_SUBREGION_PICK
        );
        return pickDirection(value);
    }

    private static SubDir directionFor(Continent continent, Site parent,
                                       int childIndex) {
        return continent == Continent.ANTARCTICA
                ? SubDir.CENTRAL
                : initialDirection(parent.key(), childIndex, continent);
    }

    /**
     * Finds a seed-jittered Voronoi site. At macro scale, shifting the lattice
     * by half a cell and keeping jitter bounded guarantees that 0,0 is well
     * inside its owning cell. The warp is zero at the origin, so it cannot move
     * the protected spawn area onto a boundary.
     */
    private static Site nearestSite(long seed, int x, int z, int scale,
                                    long siteSalt, boolean protectOrigin) {
        double[] warped = warp(seed, x, z, scale);
        double latticeShift = protectOrigin ? scale * 0.5 : 0.0;
        double sampleX = warped[0] + latticeShift;
        double sampleZ = warped[1] + latticeShift;

        int gridX = floorToInt(sampleX / scale);
        int gridZ = floorToInt(sampleZ / scale);

        double bestDistance = Double.POSITIVE_INFINITY;
        long bestKey = 0L;
        double bestCenterX = 0.0;
        double bestCenterZ = 0.0;

        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                int cellX = gridX + dx;
                int cellZ = gridZ + dz;
                long siteHash = hash(seed, cellX, cellZ, siteSalt);

                double jitterX = signedUnit(siteHash) * scale * JITTER;
                double jitterZ = signedUnit(mix64(siteHash)) * scale * JITTER;
                double siteX = (cellX + 0.5) * scale + jitterX;
                double siteZ = (cellZ + 0.5) * scale + jitterZ;

                double deltaX = sampleX - siteX;
                double deltaZ = sampleZ - siteZ;
                double distance = deltaX * deltaX + deltaZ * deltaZ;

                if (distance < bestDistance) {
                    bestDistance = distance;
                    bestKey = hash(seed, cellX, cellZ, siteSalt ^ SITE_KEY_SALT);
                    bestCenterX = siteX;
                    bestCenterZ = siteZ;
                }
            }
        }

        return new Site(bestKey, bestCenterX, bestCenterZ, sampleX, sampleZ);
    }

    /**
     * Applies three continuous displacement scales before any Voronoi lookup:
     * a broad continental bend, a regional lobe and a smaller rounded edge.
     * Each octave uses a rotated noise field so no world axis becomes the
     * preferred border direction. Conservative amplitudes keep the mapping
     * from folding over itself and creating geometric islands.
     */
    private static double[] warp(long seed, double x, double z, int scale) {
        double displacementX = 0.0;
        double displacementZ = 0.0;

        for (int octave = 0; octave < WARP_WAVELENGTHS.length; octave++) {
            double rotationX = WARP_ROTATIONS[octave][0];
            double rotationZ = WARP_ROTATIONS[octave][1];
            double sampleX = x * rotationX - z * rotationZ;
            double sampleZ = x * rotationZ + z * rotationX;
            double wavelength = scale * WARP_WAVELENGTHS[octave];
            double amplitude = scale * WARP_AMPLITUDES[octave];
            long octaveSalt = mix64(
                    SALT_WARP_OCTAVE
                            + octave * 0x9E3779B97F4A7C15L
            );

            // Subtracting the origin value makes the complete warp exactly
            // zero at 0,0 while retaining the same smooth field elsewhere.
            double originX = smoothNoise(
                    seed, 0.0, 0.0, wavelength,
                    SALT_WARP_X ^ octaveSalt
            );
            double originZ = smoothNoise(
                    seed, 0.0, 0.0, wavelength,
                    SALT_WARP_Z ^ octaveSalt
            );
            displacementX += (
                    smoothNoise(
                            seed, sampleX, sampleZ, wavelength,
                            SALT_WARP_X ^ octaveSalt
                    ) - originX
            ) * amplitude;
            displacementZ += (
                    smoothNoise(
                            seed, sampleX, sampleZ, wavelength,
                            SALT_WARP_Z ^ octaveSalt
                    ) - originZ
            ) * amplitude;
        }

        return new double[]{x + displacementX, z + displacementZ};
    }

    private static double smoothNoise(long seed, double x, double z,
                                      double wavelength, long salt) {
        double gridX = x / wavelength;
        double gridZ = z / wavelength;
        int cellX = floorToInt(gridX);
        int cellZ = floorToInt(gridZ);
        double blendX = fade(gridX - cellX);
        double blendZ = fade(gridZ - cellZ);

        double northWest = signedUnit(hash(seed, cellX, cellZ, salt));
        double northEast = signedUnit(hash(seed, cellX + 1, cellZ, salt));
        double southWest = signedUnit(hash(seed, cellX, cellZ + 1, salt));
        double southEast = signedUnit(hash(seed, cellX + 1, cellZ + 1, salt));
        double north = lerp(northWest, northEast, blendX);
        double south = lerp(southWest, southEast, blendX);
        return lerp(north, south, blendZ);
    }

    private static double fade(double value) {
        return value * value * value
                * (value * (value * 6.0 - 15.0) + 10.0);
    }

    private static double lerp(double first, double second, double amount) {
        return first + (second - first) * amount;
    }

    private static Continent pickContinent(long value) {
        Continent[] values = Continent.values();
        return values[(int) Math.floorMod(value, values.length)];
    }

    private static SubDir pickDirection(long value) {
        SubDir[] values = SubDir.values();
        return values[(int) Math.floorMod(value, values.length)];
    }

    private static Ocean pickOcean(long value) {
        Ocean[] values = Ocean.values();
        return values[(int) Math.floorMod(value, values.length)];
    }

    private static int floorToInt(double value) {
        int truncated = (int) value;
        return value < truncated ? truncated - 1 : truncated;
    }

    private static double unit(long value) {
        return (value >>> 11) * 0x1.0p-53;
    }

    private static double signedUnit(long value) {
        return unit(value) * 2.0 - 1.0;
    }

    private static long hash(long seed, int x, int z, long salt) {
        long value = seed ^ salt;
        value ^= (long) x * 0x9E3779B97F4A7C15L;
        value ^= (long) z * 0xC2B2AE3D27D4EB4FL;
        return mix64(value);
    }

    private static long mix64(long value) {
        value ^= value >>> 33;
        value *= 0xFF51AFD7ED558CCDL;
        value ^= value >>> 33;
        value *= 0xC4CEB9FE1A85EC53L;
        value ^= value >>> 33;
        return value;
    }

    private record Site(long key, double centerX, double centerZ,
                        double sampleX, double sampleZ) {}

    private record ChildGeometry(Site parent, int childIndex,
                                 int childCount) {}

    public record GeometrySample(long parentKey, int childIndex,
                                 int childCount, Continent continent,
                                 int parentCenterX,
                                 int parentCenterZ) {}

    public record ChildNeighbor(int childIndex, SubDir direction) {}

    public record ChildAdjacency(long parentKey, int childIndex,
                                 int childCount, Region region,
                                 List<ChildNeighbor> neighbors) {
        public ChildAdjacency {
            neighbors = List.copyOf(neighbors);
        }
    }
}
