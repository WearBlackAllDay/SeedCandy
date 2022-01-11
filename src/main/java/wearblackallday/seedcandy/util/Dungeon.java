package wearblackallday.seedcandy.util;

import com.seedfinding.latticg.reversal.DynamicProgram;
import com.seedfinding.latticg.reversal.calltype.java.JavaCalls;
import com.seedfinding.latticg.util.LCG;
import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mccore.version.UnsupportedVersion;
import com.seedfinding.mcreversal.ChunkRandomReverser;

import java.util.*;
import java.util.stream.LongStream;

public record Dungeon(BPos position, Size size, String floor, MCVersion version, Biome biome) {
	public static final Set<Biome> FOSSIL_BIOMES = Set.of(Biomes.DESERT, Biomes.SWAMP, Biomes.SWAMP_HILLS);
	private static final LCG FAILED_DUNGEON = LCG.JAVA.combine(-5);

	public List<Long> crack() {
		if(this.size.x * this.size.z != this.floor.length() || !this.floor.matches("[0-2]+"))
			return Collections.emptyList();

		DynamicProgram device = this.getDungeonRand();

		for(int i = 0; i < this.floor.length(); i++) {
			switch(this.floor.charAt(i)) {
				case '0' -> device.add(JavaCalls.nextInt(4).equalTo(0));
				case '1' -> device.filteredSkip(r -> r.nextInt(4) != 0, 1);
				case '2' -> device.skip(1);
			}
		}

		List<Long> structureSeeds = Collections.synchronizedList(new ArrayList<>());
		long[] dungeonSpawns = device.reverse().flatMap(Dungeon::getSpawnAttempts).toArray();
		BPos chunkCorner = this.position.toChunkCorner();

		Arrays.stream(dungeonSpawns).parallel()
			.forEach(spawnAttempt -> structureSeeds.addAll(ChunkRandomReverser.reversePopulationSeed(
				(spawnAttempt ^ LCG.JAVA.multiplier) - this.getSalt(),
				chunkCorner.getX(), chunkCorner.getZ(), this.version)));

		return structureSeeds;
	}

	private static LongStream getSpawnAttempts(long dungeonSeed) {
		long[] spawnAttempts = new long[8];
		for(int i = 0; i < spawnAttempts.length; i++) {
			spawnAttempts[i] = dungeonSeed;
			dungeonSeed = FAILED_DUNGEON.nextSeed(dungeonSeed);
		}
		return Arrays.stream(spawnAttempts);
	}

	private DynamicProgram getDungeonRand() {
		DynamicProgram device = DynamicProgram.create(LCG.JAVA);
		device.add(JavaCalls.nextInt(16).equalTo(this.position().getX() & 15));
		if(this.version.isNewerOrEqualTo(MCVersion.v1_15)) {
			device.add(JavaCalls.nextInt(16).equalTo(this.position.getZ() & 15));
			device.add(JavaCalls.nextInt(256).equalTo(this.position().getY()));
		} else {
			device.add(JavaCalls.nextInt(256).equalTo(this.position().getY()));
			device.add(JavaCalls.nextInt(16).equalTo(this.position().getZ() & 15));
		}
		device.add(JavaCalls.nextInt(2).equalTo(this.size.x >> 3));
		device.add(JavaCalls.nextInt(2).equalTo(this.size.z >> 3));
		return device;
	}

	private long getSalt() {
		return switch(this.version) {
			case v1_17, v1_16 -> FOSSIL_BIOMES.contains(this.biome) ? 30003L : 30002L;
			case v1_15, v1_14, v1_13 -> 20003L;
			default -> throw new UnsupportedVersion(this.version, "single-Dungeon reversal");
		};
	}

	public enum Size {
		_9x9(9, 9),
		_9x7(9, 7),
		_7x9(7, 9),
		_7x7(7, 7);

		public final int x, z;

		Size(int x, int z) {
			this.x = x;
			this.z = z;
		}

		@Override
		public String toString() {
			return this.x + "x" + this.z;
		}
	}
}
