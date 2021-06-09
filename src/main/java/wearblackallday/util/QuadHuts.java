package wearblackallday.util;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.SwampHut;
import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.component.vector.QVector;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.RegionSeed;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import mjtb49.hashreversals.Lattice2D;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QuadHuts {
	private static final Lattice2D REGION_LATTICE = new Lattice2D(RegionSeed.A, RegionSeed.B, 1L << 48);
	private static final long[] REGION_SEEDS = getQuadRegionSeeds();

	public static List<BPos> find(long worldSeed, MCVersion version) {
		List<BPos> quadHuts = new ArrayList<>(2);
		SwampHut swampHut = new SwampHut(version);
		var biomeSource = new OverworldBiomeSource(version, worldSeed);

		for(long regionSeed : REGION_SEEDS) {
			for(QVector solution : REGION_LATTICE.findSolutionsInBox(regionSeed - worldSeed - swampHut.getSalt(),
				-60000, -60000, 60000, 60000)) {
				if(!checkBiomes(biomeSource, solution, swampHut)) break;
				solution.scaleAndSet(512);
				quadHuts.add(new BPos(solution.get(0).intValue(), 0, solution.get(1).intValue()));
			}
		}
		return quadHuts;
	}


	private static boolean checkBiomes(OverworldBiomeSource source, QVector solution, SwampHut hut) {
		if(checkStructure(source, solution.get(0), solution.get(1), hut)) return false;
		if(checkStructure(source, solution.get(0).subtract(1), solution.get(1), hut)) return false;
		if(checkStructure(source, solution.get(0), solution.get(1).subtract(1), hut)) return false;
		if(checkStructure(source, solution.get(0).subtract(1), solution.get(1).subtract(1), hut)) return false;
		return true;
	}

	private static boolean checkStructure(OverworldBiomeSource source, Rational x, Rational z, SwampHut structure) {
		CPos chunk = structure.getInRegion(source.getWorldSeed(), x.intValue(), z.intValue(), new ChunkRand());
		return !structure.canSpawn(chunk.getX(), chunk.getZ(), source);
	}

	private static long[] getQuadRegionSeeds() {
		return new BufferedReader(new InputStreamReader(QuadHuts.class.getResourceAsStream("/regionSeeds.txt")))
			.lines().mapToLong(Long::parseLong).toArray();
	}
}
