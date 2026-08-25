package net.semppi.semppis_mythical_legends_mod.world;

/**
 * The single seed-and-coordinate authority for the continental overlay.
 *
 * <p>This sampler never reads chunks, biomes, height, game time or mutable
 * world state. Given the same algorithm version, world seed and X/Z, it always
 * returns the same label. Biome affinity is deliberately a later bounded layer
 * and must never create a second layout.</p>
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
    private static final double WARP = 0.08;
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

        SubDir direction = continent == Continent.ANTARCTICA
                ? SubDir.CENTRAL
                : directionForChild(continentSite, nearestChildIndex(worldSeed, continentSite));

        return Region.land(continent, direction);
    }

    public static Region seaRegion(long worldSeed, int x, int z) {
        Site oceanSite = nearestSite(
                worldSeed, x, z, CONTINENT_SCALE,
                SALT_OCEAN_SITE, true
        );
        Ocean ocean = pickOcean(mix64(oceanSite.key() ^ SALT_OCEAN_PICK));
        return Region.sea(ocean);
    }

    /**
     * Selects one of the owning continent's explicit child sites. Each
     * continent creates exactly three to eight children; there is no global
     * direction grid and children can never belong to a neighboring parent.
     */
    private static int nearestChildIndex(long seed, Site parent) {
        int count = MIN_SUBREGIONS + (int) Math.floorMod(
                mix64(parent.key() ^ SALT_SUBREGION_COUNT),
                MAX_SUBREGIONS - MIN_SUBREGIONS + 1
        );

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

    private static SubDir directionForChild(Site parent, int childIndex) {
        long value = hash(
                parent.key(), childIndex, ALGORITHM_VERSION,
                SALT_SUBREGION_PICK
        );
        return pickDirection(value);
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

    private static double[] warp(long seed, double x, double z, int scale) {
        double frequency = 1.0 / (scale * 0.72);
        double phaseX = unit(hash(seed, scale, 0, SALT_WARP_X)) * Math.PI * 2.0;
        double phaseZ = unit(hash(seed, 0, scale, SALT_WARP_Z)) * Math.PI * 2.0;

        double waveX = Math.sin((x + z * 0.61) * frequency + phaseX)
                - Math.sin(phaseX);
        double waveZ = Math.sin((z - x * 0.57) * frequency + phaseZ)
                - Math.sin(phaseZ);

        double magnitude = scale * WARP;
        return new double[]{x + waveX * magnitude, z + waveZ * magnitude};
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
}
