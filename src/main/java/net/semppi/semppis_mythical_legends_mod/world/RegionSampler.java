package net.semppi.semppis_mythical_legends_mod.world;

import java.util.HashMap;
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
    // ===== scales =====
    // Direction blobs ~50% larger (as you set)
    private static final int GRID = 1920;
    // Continental clusters scale with GRID
    private static final int CLUSTER = GRID * 6;

    // ===== salts =====
    private static final long SALT_CENTERS         = 0xB76C3C51D4A3B1E7L;
    private static final long SALT_PICK            = 0x4F72B9138C0D2A55L;
    private static final long SALT_CLUSTER_CENTERS = 0xA4B1D2C3E5F60719L;
    private static final long SALT_CLUSTER_PICK    = 0x3D6F9A2C5B7E8D10L;
    private static final long SALT_NOISE           = 0x9D7F6C3B2A159E37L;
    private static final long SALT_OCEAN_PICK      = 0x6C8E9FA1B2C3D4E5L;

    // ===== warp (2D Simplex) =====
    private static final double WARP_FREQ = 1.0 / 360.0;
    private static final double WARP_MAG  = 64.0;
    private final SimplexNoise warpX = new SimplexNoise(RandomSource.create(SALT_NOISE ^ 0xA61E5F1DL));
    private final SimplexNoise warpZ = new SimplexNoise(RandomSource.create(SALT_NOISE ^ 0xC3D2E1F0L));

    // cached per (seed,cgx,cgz)
    private final Map<Long, ClusterDecision> clusterCache = new ConcurrentHashMap<>();

    // ---------- public API ----------

    /** Legacy: noise-only (kept because other code still calls it). */
    public Region landRegion(long worldSeed, int x, int z) {
        long cKey = clusterKey(worldSeed, x, z);   // restored
        Continent cont = pickContinent(cKey);

        long site = siteKey(worldSeed, x, z, true);
        int cgx = Math.floorDiv(x, CLUSTER), cgz = Math.floorDiv(z, CLUSTER);
        long mix = hash(worldSeed, cgx, cgz, SALT_CLUSTER_PICK);
        SubDir dir = pickSubdir(site ^ (mix << 1));
        return Region.land(cont, dir);
    }

    /** Land: biome-aware + consensus so whole biomes tend to agree. */
    public Region landRegion(ServerLevel level, int x, int z) {
        // connector-aware early guard (keep as-is)
        ResourceLocation centerId = biomeId(level, x, z);
        if (isConnector(centerId)) {
            Region fromNeighbors = majorityRegionAcrossConnectors(level, x, z);
            if (fromNeighbors != null) return fromNeighbors;
        }

        // NEW: tiny-patch adoption
        if (isTinyPatch(level, x, z, centerId)) {
            Region adopt = adoptFromNeighbors(level, x, z, centerId);
            if (adopt != null) return adopt;
        }

        Region base = rawLandRegion(level, x, z);
        return biomeConsensus(level, x, z, base);
    }

// --- helpers (add near the other helpers) ---

    /** Treats very small biome islands as adoptive: let them inherit neighbor majority. */
    private static boolean isTinyPatch(ServerLevel level, int x, int z, ResourceLocation centerId) {
        final int STEP = 24;
        int same = 0, total = 0;
        for (int oz = -1; oz <= 1; oz++) for (int ox = -1; ox <= 1; ox++) {
            if (ox == 0 && oz == 0) continue;
            int sx = x + ox * STEP, sz = z + oz * STEP;
            ResourceLocation id = biomeId(level, sx, sz);
            total++;
            if (id.equals(centerId)) same++;
        }
        // If fewer than ~25% of neighbors match, consider it a tiny patch
        return same * 4 < total; // < 25%
    }

    /** Majority region among neighbors that are NOT the tiny center biome. */
    private Region adoptFromNeighbors(ServerLevel level, int x, int z, ResourceLocation centerId) {
        final int STEP = 24;
        int[] contVotes = new int[Continent.values().length];
        Map<SubDir, Integer> dirVotesWithinWinner = new HashMap<>();

        for (int oz = -1; oz <= 1; oz++) for (int ox = -1; ox <= 1; ox++) {
            if (ox == 0 && oz == 0) continue;
            int sx = x + ox * STEP, sz = z + oz * STEP;
            ResourceLocation id = biomeId(level, sx, sz);
            if (id.equals(centerId)) continue;
            Region r = rawLandRegion(level, sx, sz);
            if (r.ocean()) continue;
            contVotes[r.continent().ordinal()]++;
        }

        // winner continent
        int best = -1; Continent winner = null;
        for (Continent c : Continent.values()) {
            int v = contVotes[c.ordinal()];
            if (v > best) { best = v; winner = c; }
        }
        if (winner == null || best <= 0) return null;

        // pick a direction favored among neighbors with the winner continent
        for (int oz = -1; oz <= 1; oz++) for (int ox = -1; ox <= 1; ox++) {
            if (ox == 0 && oz == 0) continue;
            int sx = x + ox * STEP, sz = z + oz * STEP;
            ResourceLocation id = biomeId(level, sx, sz);
            if (id.equals(centerId)) continue;
            Region r = rawLandRegion(level, sx, sz);
            if (!r.ocean() && r.continent() == winner) {
                dirVotesWithinWinner.merge(r.dir(), 1, Integer::sum);
            }
        }

        SubDir dir = SubDir.CENTRAL;
        int dirBest = -1;
        for (Map.Entry<SubDir, Integer> e : dirVotesWithinWinner.entrySet()) {
            if (e.getValue() > dirBest) { dirBest = e.getValue(); dir = e.getKey(); }
        }
        if (winner == Continent.ANTARCTICA) dir = SubDir.CENTRAL;
        return Region.land(winner, dir);
    }

    /** Sea: biome-aware + consensus (TagRules.oceanAllows) */
    public Region seaRegion(ServerLevel level, int x, int z) {
        Ocean o = rawSeaRegion(level, x, z);
        o = oceanConsensus(level, x, z, o);
        return Region.sea(o);
    }

    // ---------- core land selection ----------
    private Region rawLandRegion(ServerLevel level, int x, int z) {
        long seed = level.getSeed();
        ClusterDecision dec = clusterDecision(level, x, z);

        long site = siteKey(seed, x, z, true);
        long clusterPick = hash(seed, Math.floorDiv(x, CLUSTER), Math.floorDiv(z, CLUSTER), SALT_CLUSTER_PICK);
        SubDir dir = pickSubdir(site ^ (clusterPick << 1));

        ResourceLocation biomeId = biomeId(level, x, z);

        Continent chosen = null;

        // 1) Try primary (same dir or best allowed dir on that continent)
        if (dec.primary != null) {
            if (TagRules.allows(dec.primary, dir, biomeId)) {
                chosen = dec.primary;
            } else {
                SubDir alt = bestAllowedDir(seed, dec.primary, biomeId, site, clusterPick);
                if (alt != null) { chosen = dec.primary; dir = alt; }
            }
        }

        // 2) Try secondary
        if (chosen == null && dec.secondary != null) {
            if (TagRules.allows(dec.secondary, dir, biomeId)) {
                chosen = dec.secondary;
            } else {
                SubDir alt = bestAllowedDir(seed, dec.secondary, biomeId, site, clusterPick);
                if (alt != null) { chosen = dec.secondary; dir = alt; }
            }
        }

        // 3) FINAL fallback: choose ANY continent that accepts this biome (stable tie-break)
        if (chosen == null) {
            Continent bestC = null; SubDir bestD = null; long bestH = Long.MIN_VALUE;
            for (Continent c : Continent.values()) {
                SubDir alt = bestAllowedDir(seed, c, biomeId, site, clusterPick);
                if (alt == null) continue; // this continent can't host this biome
                long h = hash(seed, (c.ordinal() << 8) ^ alt.ordinal(), biomeId.hashCode(), SALT_PICK ^ site ^ clusterPick);
                if (h > bestH) { bestH = h; bestC = c; bestD = alt; }
            }
            if (bestC != null) {
                chosen = bestC;
                dir = bestD;
            } else {
                // If truly nothing matches, fall back to base pick deterministically
                chosen = (dec.primary != null) ? dec.primary : Continent.values()[0];
            }
        }

        if (chosen == Continent.ANTARCTICA) dir = SubDir.CENTRAL;
        return Region.land(chosen, dir);
    }

    private static SubDir bestAllowedDir(long seed, Continent c, ResourceLocation biomeId, long site, long clusterPick) {
        SubDir best = null;
        long bestH = Long.MIN_VALUE;
        for (SubDir cand : SubDir.values()) {
            if (!TagRules.allows(c, cand, biomeId)) continue;
            long h = hash(seed, (c.ordinal() << 8) ^ cand.ordinal(), biomeId.hashCode(),
                    SALT_PICK ^ site ^ clusterPick);
            if (h > bestH) { bestH = h; best = cand; }
        }
        return best;
    }

    /** Coarse Voronoi: pick which CONTINENT cluster this area belongs to (legacy path). */
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

    // ---------- connector handling ----------
    private static boolean isConnector(ResourceLocation id) {
        if (!"minecraft".equals(id.getNamespace())) return false;
        String p = id.getPath();
        return p.equals("river") || p.equals("frozen_river")
                || p.equals("beach") || p.equals("snowy_beach")
                || p.equals("stony_shore");
    }

    private Region majorityRegionAcrossConnectors(ServerLevel level, int x, int z) {
        final int[] steps = {24, 40, 56};
        Region winner = null;
        int votes = 0;

        for (var d : net.minecraft.core.Direction.Plane.HORIZONTAL) {
            for (int s : steps) {
                int sx = x + d.getStepX() * s;
                int sz = z + d.getStepZ() * s;

                ResourceLocation id = biomeIdIfLoaded(level, sx, sz);
                if (id == null) continue;           // don’t load/generate
                if (isConnector(id)) continue;       // keep peeking outward

                Region r = rawLandRegion(level, sx, sz); // first non-connector we *already* have
                if (winner == null || !same(winner, r)) { winner = r; votes = 1; }
                else votes++;
                break;
            }
        }
        return votes > 0 ? winner : null;
    }

    // ---------- land biome consensus (whole-biome coherence) ----------
    private Region biomeConsensus(ServerLevel level, int x, int z, Region base) {
        int y0 = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, x, z);
        Holder<Biome> here = level.getBiome(new BlockPos(x, y0, z));

        final int STEP = 24;
        int[] contVotes = new int[Continent.values().length];
        Map<SubDir, Integer> dirVotesWithinWinner = new HashMap<>();

        if (!base.ocean()) contVotes[base.continent().ordinal()]++;

        for (int oz = -1; oz <= 1; oz++) for (int ox = -1; ox <= 1; ox++) {
            int sx = x + ox * STEP, sz = z + oz * STEP;
            int sy = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, sx, sz);
            if (!level.getBiome(new BlockPos(sx, sy, sz)).equals(here)) continue;

            Region r = rawLandRegion(level, sx, sz);
            if (r.ocean()) continue;
            contVotes[r.continent().ordinal()]++;
        }

        Continent winner = base.continent();
        int best = contVotes[winner.ordinal()];
        for (Continent c : Continent.values()) {
            int v = contVotes[c.ordinal()];
            if (v > best) { best = v; winner = c; }
        }

        for (int oz = -1; oz <= 1; oz++) for (int ox = -1; ox <= 1; ox++) {
            int sx = x + ox * STEP, sz = z + oz * STEP;
            int sy = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, sx, sz);
            if (!level.getBiome(new BlockPos(sx, sy, sz)).equals(here)) continue;

            Region r = rawLandRegion(level, sx, sz);
            if (!r.ocean() && r.continent() == winner) {
                dirVotesWithinWinner.merge(r.dir(), 1, Integer::sum);
            }
        }

        SubDir dir = base.dir();
        int dirBest = -1;
        for (Map.Entry<SubDir, Integer> e : dirVotesWithinWinner.entrySet()) {
            int v = e.getValue();
            if (v > dirBest || (v == dirBest && e.getKey() == dir)) {
                dirBest = v; dir = e.getKey();
            }
        }

        if (winner == Continent.ANTARCTICA) dir = SubDir.CENTRAL;
        return Region.land(winner, dir);
    }

    // ---------- sea selection ----------
    public Region seaRegion(long worldSeed, int x, int z) {
        return Region.sea(pickSea(hash(worldSeed,
                Math.floorDiv(x, CLUSTER), Math.floorDiv(z, CLUSTER),
                SALT_CLUSTER_PICK ^ SALT_OCEAN_PICK)));
    }

    private Ocean rawSeaRegion(ServerLevel level, int x, int z) {
        long seed = level.getSeed();
        int cgx = Math.floorDiv(x, CLUSTER);
        int cgz = Math.floorDiv(z, CLUSTER);

        long basinKey = hash(seed, cgx, cgz, SALT_CLUSTER_PICK ^ SALT_OCEAN_PICK);
        Ocean base = pickSea(basinKey);

        ResourceLocation id = biomeId(level, x, z);

        if (TagRules.oceanAllows(base, id)) return base;

        Ocean[] neigh = neighbors(base);
        Ocean bestNei = null; long bestH = Long.MIN_VALUE;
        for (Ocean n : neigh) {
            if (!TagRules.oceanAllows(n, id)) continue;
            long h = hash(seed, cgx ^ n.ordinal(), cgz ^ 0x7F, SALT_OCEAN_PICK ^ id.hashCode());
            if (h > bestH) { bestH = h; bestNei = n; }
        }
        if (bestNei != null) return bestNei;

        Ocean any = null; bestH = Long.MIN_VALUE;
        for (Ocean o : Ocean.values()) {
            if (!TagRules.oceanAllows(o, id)) continue;
            long h = hash(seed, cgx ^ o.ordinal(), cgz ^ 0x55, SALT_OCEAN_PICK ^ (id.hashCode() * 31L));
            if (h > bestH) { bestH = h; any = o; }
        }
        return any != null ? any : base;
    }

    private Ocean oceanConsensus(ServerLevel level, int x, int z, Ocean prelim) {
        int y0 = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, x, z);
        ResourceLocation centerId = biomeId(level, x, z);

        final int STEP = 32;
        int[] votes = new int[Ocean.values().length];
        votes[prelim.ordinal()]++;

        for (int oz = -1; oz <= 1; oz++) for (int ox = -1; ox <= 1; ox++) {
            int sx = x + ox * STEP, sz = z + oz * STEP;
            int sy = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, sx, sz);
            ResourceLocation id = biomeId(level, sx, sz);
            if (!id.equals(centerId)) continue;

            Ocean o = rawSeaRegion(level, sx, sz);
            votes[o.ordinal()]++;
        }

        Ocean winner = prelim;
        int best = votes[winner.ordinal()];
        for (Ocean o : Ocean.values()) {
            int v = votes[o.ordinal()];
            if (v > best) { best = v; winner = o; }
        }
        return winner;
    }

    private static Ocean[] neighbors(Ocean o) {
        return switch (o) {
            case ARCTIC          -> new Ocean[]{ Ocean.NORTH_ATLANTIC, Ocean.NORTH_PACIFIC };
            case NORTH_ATLANTIC  -> new Ocean[]{ Ocean.ARCTIC, Ocean.SOUTH_ATLANTIC };
            case SOUTH_ATLANTIC  -> new Ocean[]{ Ocean.NORTH_ATLANTIC, Ocean.SOUTHERN, Ocean.INDIAN };
            case INDIAN          -> new Ocean[]{ Ocean.SOUTHERN, Ocean.SOUTH_ATLANTIC, Ocean.SOUTH_PACIFIC };
            case NORTH_PACIFIC   -> new Ocean[]{ Ocean.ARCTIC, Ocean.SOUTH_PACIFIC };
            case SOUTH_PACIFIC   -> new Ocean[]{ Ocean.NORTH_PACIFIC, Ocean.SOUTHERN, Ocean.INDIAN };
            case SOUTHERN        -> new Ocean[]{ Ocean.INDIAN, Ocean.SOUTH_ATLANTIC, Ocean.SOUTH_PACIFIC };
        };
    }

    // ---------- continent cluster decision ----------
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

        // Lighter sampling + no forced chunk loads
        final int SAMPLES = 48;          // was 96
        final double KEEP_THRESH = 0.55;
        final double SECOND_MIN  = 0.30;

        int[] score = new int[Continent.values().length];
        int validSamples = 0;

        for (int i = 0; i < SAMPLES; i++) {
            long h = hash(seed, cgx, cgz, SALT_NOISE + i * 0x9E3779B97F4A7C15L);
            int sx = baseX + (int)(unsignedInt(h) % CLUSTER);
            int sz = baseZ + (int)(unsignedInt(h >>> 32) % CLUSTER);

            // Only count if the chunk is already loaded (avoid freezes)
            ResourceLocation id = biomeIdIfLoaded(level, sx, sz);
            if (id == null) continue;

            validSamples++;
            for (Continent c : Continent.values()) {
                if (TagRules.continentAllows(c, id)) score[c.ordinal()]++;
            }
        }

        Continent base = pickContinent(hash(seed, cgx, cgz, SALT_CLUSTER_PICK));

        // If we saw too few loaded samples, fall back to the base pick
        if (validSamples < 16) {
            return new ClusterDecision(base, null);
        }

        // Use the number of valid samples for percentages
        int denom = Math.max(1, validSamples);

        Continent primary = base;
        int bestScore = score[primary.ordinal()];
        if ((double)bestScore / denom < KEEP_THRESH) {
            for (Continent c : Continent.values()) {
                int sc = score[c.ordinal()];
                if (sc > bestScore || (sc == bestScore && tiebreak(seed, cgx, cgz, c, primary) > 0)) {
                    bestScore = sc; primary = c;
                }
            }
        }

        Continent secondary = null;
        int secondScore = Integer.MIN_VALUE;
        for (Continent c : Continent.values()) {
            if (c == primary) continue;
            int sc = score[c.ordinal()];
            if (sc > secondScore || (sc == secondScore && tiebreak(seed, cgx, cgz, c, secondary) > 0)) {
                secondScore = sc; secondary = c;
            }
        }
        if ((double)secondScore / denom < SECOND_MIN) secondary = null;

        return new ClusterDecision(primary, secondary);
    }

    private static int tiebreak(long seed, int cgx, int cgz, Continent a, Continent b) {
        if (b == null) return 1;
        long ha = hash(seed, cgx ^ a.ordinal(), cgz ^ 0x7F, SALT_PICK);
        long hb = hash(seed, cgx ^ b.ordinal(), cgz ^ 0x7F, SALT_PICK);
        return Long.compare(ha, hb);
    }

    private record ClusterDecision(Continent primary, Continent secondary) {}

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

            // Big blobs, fewer tiny ones (your tuned weights)
            int roll = (int)Math.floorMod(h, 100);
            double weight = (roll < 5) ? sq(GRID * 0.50)
                    : (roll < 55 ? sq(GRID * 1.125)
                    : sq(GRID * 1.875));

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
        int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, x, z);
        y = Math.max(level.getMinBuildHeight(), Math.min(y, level.getMaxBuildHeight() - 1));
        Holder<Biome> hb = level.getBiome(new BlockPos(x, y, z));
        return level.registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                .getKey(hb.value());
    }

    private static ResourceLocation biomeIdIfLoaded(ServerLevel level, int x, int z) {
        int cx = x >> 4, cz = z >> 4;
        if (!level.getChunkSource().hasChunk(cx, cz)) return null; // don't load/generate
        int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, x, z);
        y = Math.max(level.getMinBuildHeight(), Math.min(y, level.getMaxBuildHeight() - 1));
        Holder<Biome> hb = level.getBiome(new net.minecraft.core.BlockPos(x, y, z));
        return level.registryAccess()
                .registryOrThrow(net.minecraft.core.registries.Registries.BIOME)
                .getKey(hb.value());
    }

    private static boolean same(Region a, Region b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.ocean() != b.ocean()) return false;
        return a.ocean() ? a.sea() == b.sea()
                : (a.continent() == b.continent() && a.dir() == b.dir());
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