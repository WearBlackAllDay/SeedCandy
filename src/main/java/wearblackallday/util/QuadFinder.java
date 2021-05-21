package wearblackallday.util;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.OldStructure;
import kaptainwutax.featureutils.structure.SwampHut;
import kaptainwutax.mathutils.arithmetic.Rational;
import kaptainwutax.mathutils.component.vector.QVector;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.mc.seed.RegionSeed;
import mjtb49.hashreversals.Lattice2D;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class QuadFinder {
	private static final Lattice2D REGION_LATTICE = new Lattice2D(RegionSeed.A, RegionSeed.B, 1L << 48);
	private static final long[] REGION_SEEDS = getQuadRegionSeeds();

	public static String find(long worldSeed, MCVersion version) {
		OverworldBiomeSource biomeSource = new OverworldBiomeSource(version, worldSeed);
		OldStructure<?> swampHut = new SwampHut(version);
		String result = "";

		for(long regionSeed : REGION_SEEDS) {
			long target = regionSeed - worldSeed - swampHut.getSalt();
			List<QVector> vectorList = REGION_LATTICE.findSolutionsInBox(target, -60000, -60000, 60000, 60000);
			for(QVector solution : vectorList) {
				if(!checkBiomes(biomeSource, solution, swampHut)) break;
				solution.scaleAndSet(16 * 32);
				result = String.format(result + "[%d]" + "\n" + "-> (%d, %d)" + "\n", worldSeed, solution.get(0).intValue(), solution.get(1).intValue());
			}
		}
		return result.isEmpty() ? String.format("[%d]" + "\n" + "-> no huts", worldSeed) : result;
	}


	private static boolean checkBiomes(OverworldBiomeSource source, QVector solution, OldStructure<?> structure) {
		if(checkStructure(source, solution.get(0), solution.get(1), structure)) return false;
		if(checkStructure(source, solution.get(0).subtract(1), solution.get(1), structure)) return false;
		if(checkStructure(source, solution.get(0), solution.get(1).subtract(1), structure)) return false;
		return !checkStructure(source, solution.get(0).subtract(1), solution.get(1).subtract(1), structure);
	}

	private static boolean checkStructure(OverworldBiomeSource source, Rational x, Rational z, OldStructure<?> structure) {
		CPos chunk = structure.getInRegion(source.getWorldSeed(), x.intValue(), z.intValue(), new ChunkRand());
		return !structure.canSpawn(chunk.getX(), chunk.getZ(), source);
	}

	private static long[] getQuadRegionSeeds() {
		return new BufferedReader(new InputStreamReader(QuadFinder.class.getResourceAsStream("/regionSeeds.txt")))
			.lines().mapToLong(Long::parseLong).toArray();
	}
}
