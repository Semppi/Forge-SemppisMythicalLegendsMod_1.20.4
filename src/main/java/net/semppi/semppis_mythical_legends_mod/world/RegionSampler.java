package net.semppi.semppis_mythical_legends_mod.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public final class RegionSampler {
    // fine cells (directions)
    private static final int GRID = 1280;
    // coarse cells (continent clusters)
    private static final int CLUSTER = GRID * 4;

    // salts
    private static final long SALT_CENTERS         = 0xB76C3C51D4A3B1E7L;
    private static final long SALT_PICK            = 0x4F72B9138C0D2A55L;
    private static final long SALT_CLUSTER_CENTERS = 0xA4B1D2C3E5F60719L;
    private static final long SALT_CLUSTER_PICK    = 0x3D6F9A2C5B7E8D10L;
    private static final long SALT_NOISE           = 0x9D7F6C3B2A159E37L;

    // warp tuning (2D Simplex)
    private static final double WARP_FREQ = 1.0 / 360.0;
    private static final double WARP_MAG  = 64.0;
    private final SimplexNoise warpX = new SimplexNoise(RandomSource.create(SALT_NOISE ^ 0xA61E5F1DL));
    private final SimplexNoise warpZ = new SimplexNoise(RandomSource.create(SALT_NOISE ^ 0xC3D2E1F0L));

    // cached per (seed,cgx,cgz)
    private final Map<Long, ClusterDecision> clusterCache = new ConcurrentHashMap<>();

    // ---------- public API ----------

    /** Legacy: continent+dir from noise only (pre-biome). */
    public Region landRegion(long worldSeed, int x, int z) {
        long cKey = clusterKey(worldSeed, x, z);
        Continent cont = pickContinent(cKey);

        long site = siteKey(worldSeed, x, z, true);
        int cgx = Math.floorDiv(x, CLUSTER), cgz = Math.floorDiv(z, CLUSTER);
        long mix = hash(worldSeed, cgx, cgz, SALT_CLUSTER_PICK);
        SubDir dir = pickSubdir(site ^ (mix << 1));

        return Region.land(cont, dir);
    }

    /** Preferred: biome-aware land region (deterministic per seed). */
    public Region landRegion(ServerLevel level, int x, int z) {
        long seed = level.getSeed();
        ClusterDecision dec = clusterDecision(level, x, z);

        // choose direction (N/E/S/W/C) using the fine “blobby” layer
        long site = siteKey(seed, x, z, true);
        long clusterPick = hash(seed, Math.floorDiv(x, CLUSTER), Math.floorDiv(z, CLUSTER), SALT_CLUSTER_PICK);
        SubDir dir = pickSubdir(site ^ (clusterPick << 1));

        // the biome at this exact block position
        ResourceLocation biomeId = biomeId(level, x, z);

        // pick a continent that ALLOWS this biome for the chosen direction
        Continent chosen;
        if (dec.primary != null && TagRules.allows(dec.primary, dir, biomeId)) {
            chosen = dec.primary;
        } else if (dec.secondary != null && TagRules.allows(dec.secondary, dir, biomeId)) {
            chosen = dec.secondary;
        } else {
            // fallback: whichever continent likes this biome best (stable per seed)
            chosen = bestContinentForBiome(seed, biomeId);
            if (chosen == null) chosen = dec.primary != null ? dec.primary : Continent.values()[0];
        }
        // Antarctica has no directions — force a neutral dir
        if (chosen == Continent.ANTARCTICA) {
            dir = SubDir.CENTRAL; // use one canonical dir for all Antarctic land
        }

        return Region.land(chosen, dir);
    }

    /** Seas unchanged; flip warp=true here if you want wavier seas. */
    public Region seaRegion(long worldSeed, int x, int z) {
        long key = siteKey(worldSeed, x, z, false);
        return Region.sea(pickSea(key));
    }

    /** Coarse Voronoi: pick which CONTINENT this area belongs to. */
    private long clusterKey(long worldSeed, int x, int z) {
        double[] w = warpCoords(worldSeed, x, z);
        double sx = w[0], sz = w[1];

        int gx = (int)Math.floor(sx / CLUSTER);
        int gz = (int)Math.floor(sz / CLUSTER);

        double best = Double.POSITIVE_INFINITY;
        long bestKey = 0L;

        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                int cgx = gx + dx, cgz = gz + dz;
                long h = hash(worldSeed, cgx, cgz, SALT_CLUSTER_CENTERS);

                int offX = (int)(unsignedInt(h) % CLUSTER);
                int offZ = (int)(unsignedInt(h >>> 32) % CLUSTER);
                int cx = cgx * CLUSTER + offX;
                int cz = cgz * CLUSTER + offZ;

                double dxw = sx - cx;
                double dzw = sz - cz;
                double d2  = dxw*dxw + dzw*dzw;

                if (d2 < best) {
                    best = d2;
                    bestKey = hash(worldSeed, cgx, cgz, SALT_CLUSTER_PICK);
                }
            }
        }
        return bestKey;
    }

    // ---------- cluster decision ----------

    private ClusterDecision clusterDecision(ServerLevel level, int x, int z) {
        long seed = level.getSeed();
        int cgx = Math.floorDiv(x, CLUSTER);
        int cgz = Math.floorDiv(z, CLUSTER);
        long key = (((long)cgx) << 32) ^ (cgz & 0xFFFFFFFFL) ^ seed;
        ClusterDecision cached = clusterCache.get(key);
        if (cached != null) return cached;

        ClusterDecision dec = chooseDecision(level, cgx, cgz);
        clusterCache.put(key, dec);
        return dec;
    }

    private ClusterDecision chooseDecision(ServerLevel level, int cgx, int cgz) {
        long seed = level.getSeed();
        final int baseX = cgx * CLUSTER, baseZ = cgz * CLUSTER;
        final int SAMPLES = 48;
        final double KEEP_THRESH = 0.55;
        final double SECOND_MIN  = 0.30;

        int[] score = new int[Continent.values().length];
        for (int i = 0; i < SAMPLES; i++) {
            long h = hash(seed, cgx, cgz, SALT_NOISE + i * 0x9E3779B97F4A7C15L);
            int sx = baseX + (int)(unsignedInt(h) % CLUSTER);
            int sz = baseZ + (int)(unsignedInt(h >>> 32) % CLUSTER);
            ResourceLocation id = biomeId(level, sx, sz);
            for (Continent c : Continent.values()) {
                if (TagRules.continentAllows(c, id)) score[c.ordinal()]++;
            }
        }

        Continent base = pickContinent(hash(seed, cgx, cgz, SALT_CLUSTER_PICK));

        Continent primary = base;
        int bestScore = score[primary.ordinal()];
        if ((double)bestScore / SAMPLES < KEEP_THRESH) {
            for (Continent c : Continent.values()) {
                int sc = score[c.ordinal()];
                if (sc > bestScore || (sc == bestScore && tiebreak(seed, cgx, cgz, c, primary) > 0)) {
                    bestScore = sc;
                    primary = c;
                }
            }
        }

        Continent secondary = null;
        int secondScore = Integer.MIN_VALUE;
        for (Continent c : Continent.values()) {
            if (c == primary) continue;
            int sc = score[c.ordinal()];
            if (sc > secondScore || (sc == secondScore && tiebreak(seed, cgx, cgz, c, secondary) > 0)) {
                secondScore = sc;
                secondary = c;
            }
        }
        if ((double)secondScore / SAMPLES < SECOND_MIN) secondary = null;

        return new ClusterDecision(primary, secondary);
    }

    private static int tiebreak(long seed, int cgx, int cgz, Continent a, Continent b) {
        if (b == null) return 1;
        long ha = hash(seed, cgx ^ a.ordinal(), cgz ^ 0x7F, SALT_PICK);
        long hb = hash(seed, cgx ^ b.ordinal(), cgz ^ 0x7F, SALT_PICK);
        return Long.compare(ha, hb);
    }

    private record ClusterDecision(Continent primary, Continent secondary) {}

    private Continent bestContinentForBiome(long seed, ResourceLocation biomeId) {
        int bestIdx = -1;
        long bestH = Long.MIN_VALUE;
        for (Continent c : Continent.values()) {
            boolean ok = false;
            for (SubDir d : SubDir.values()) {
                if (TagRules.allows(c, d, biomeId)) { ok = true; break; }
            }
            if (!ok) continue;
            long h = hash(seed, c.ordinal(), biomeId.hashCode(), SALT_PICK);
            if (h > bestH) { bestH = h; bestIdx = c.ordinal(); }
        }
        return bestIdx >= 0 ? Continent.values()[bestIdx] : null;
    }

    // ---------- fine Voronoi (blobby) ----------
    private long siteKey(long worldSeed, int x, int z, boolean warp) {
        double sx = x, sz = z;
        if (warp) {
            double[] w = warpCoords(worldSeed, x, z);
            sx = w[0]; sz = w[1];
        }

        int gx = (int)Math.floor(sx / GRID);
        int gz = (int)Math.floor(sz / GRID);

        double bestScore = Double.POSITIVE_INFINITY;
        long bestKey = 0L;

        for (int dz = -1; dz <= 1; dz++) for (int dx = -1; dx <= 1; dx++) {
            int cgx = gx + dx, cgz = gz + dz;
            long h = hash(worldSeed, cgx, cgz, SALT_CENTERS);

            int offX = (int)(unsignedInt(h) % GRID);
            int offZ = (int)(unsignedInt(h >>> 32) % GRID);
            int cx = cgx * GRID + offX;
            int cz = cgz * GRID + offZ;

            double dxw = sx - cx, dzw = sz - cz;
            double d2  = dxw*dxw + dzw*dzw;

            int roll = (int)Math.floorMod(h, 100);
            double weight = (roll < 20) ? 0.0
                    : (roll < 70 ? sq(GRID * 0.75)
                    : sq(GRID * 1.25));

            double score = d2 - weight;
            if (score < bestScore) {
                bestScore = score;
                bestKey   = hash(worldSeed, cgx, cgz, SALT_PICK);
            }
        }
        return bestKey;
    }

    // ---------- warp (2D Simplex) ----------
    private double[] warpCoords(long worldSeed, double x, double z) {
        double fx = WARP_FREQ * 0.97 + ((worldSeed >>> 17) & 255) * 1e-6;
        double fz = WARP_FREQ * 1.03 + ((worldSeed >>> 29) & 255) * 1e-6;
        double wx = warpX.getValue(x * fx, z * fx);
        double wz = warpZ.getValue(x * fz, z * fz);
        return new double[]{ x + wx * WARP_MAG, z + wz * WARP_MAG };
    }

    // ---------- helpers ----------
    private static ResourceLocation biomeId(ServerLevel level, int x, int z) {
        Holder<Biome> hb = level.getBiome(new BlockPos(x, level.getMinBuildHeight() + 64, z));
        return level.registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                .getKey(hb.value());
    }

    private static Continent pickContinent(long k) {
        return Continent.values()[(int)Math.floorMod(k, 7)];
    }
    private static SubDir pickSubdir(long k) {
        return SubDir.values()[(int)Math.floorMod(k >>> 16, 5)];
    }
    private static Ocean pickSea(long k) {
        return Ocean.values()[(int)Math.floorMod(k >>> 32, Ocean.values().length)];
    }

    private static long hash(long seed, int ax, int az, long salt) {
        long h = seed ^ salt;
        h ^= (long)ax * 0x9E3779B97F4A7C15L;
        h ^= (long)az * 0xC2B2AE3D27D4EB4FL;
        h ^= (h >>> 33);
        h *= 0xFF51AFD7ED558CCDL;
        h ^= (h >>> 33);
        h *= 0xC4CEB9FE1A85EC53L;
        h ^= (h >>> 33);
        return h;
    }
    private static long unsignedInt(long v){ return v & 0xFFFFFFFFL; }
    private static double sq(double v){ return v*v; }
}