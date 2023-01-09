package wearblackallday.seedcandy.util;

import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.rand.seed.RegionSeed;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.SwampHut;
import com.seedfinding.mcmath.arithmetic.Rational;
import com.seedfinding.mcmath.component.vector.QVector;
import com.seedfinding.mcreversal.Lattice2D;
import wearblackallday.javautils.io.ByteSlice;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class QuadHuts {
	private static final Lattice2D REGION_LATTICE = new Lattice2D(RegionSeed.A, RegionSeed.B, 1L << 48);
	private static final long[] REGION_SEEDS;

	static {
		try(ByteSlice in = new ByteSlice(QuadHuts.class.getResourceAsStream("/regionSeeds"))) {
			REGION_SEEDS = new long[in.available() / Long.BYTES];

			for(int i = 0; i < REGION_SEEDS.length; i++) {
				REGION_SEEDS[i] = in.readLong();
			}

		} catch(IOException e) {
			throw new RuntimeException();
		}
	}

	private QuadHuts() {}

	public static List<BPos> find(long worldSeed, MCVersion version) {
		List<BPos> quadHuts = new ArrayList<>(2);
		SwampHut swampHut = new SwampHut(version);
		OverworldBiomeSource biomeSource = new OverworldBiomeSource(version, worldSeed);

		for(long regionSeed : REGION_SEEDS) {
			for(QVector solution : REGION_LATTICE.findSolutionsInBox(regionSeed - worldSeed - swampHut.getSalt(),
				-60_000, -60_000, 60_000, 60_000)) {
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
}
