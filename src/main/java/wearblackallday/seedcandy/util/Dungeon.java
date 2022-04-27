package wearblackallday.seedcandy.util;

import com.seedfinding.latticg.reversal.DynamicProgram;
import com.seedfinding.latticg.reversal.calltype.java.JavaCalls;
import com.seedfinding.latticg.util.LCG;
import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mccore.util.math.Vec3i;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcreversal.ChunkRandomReverser;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.LongStream;

public record Dungeon(BPos position, Floor floor, MCVersion version, Biome biome) {
	public static final Set<Biome> FOSSIL_BIOMES = Set.of(Biomes.DESERT, Biomes.SWAMP, Biomes.SWAMP_HILLS);
	private static final LCG FAILED_DUNGEON = LCG.JAVA.combine(-5);
	private static final LCG REVERSE = LCG.JAVA.invert();

	public Dungeon {
		if(version.isOlderThan(MCVersion.v1_13)) position = position.add(-8, 0, -8);
	}

	public List<Long> reverseStructureSeeds() {
		DynamicProgram device = this.getDungeonRand();
		this.floor.pattern.forEach(block -> block.javaCall.accept(device));
		boolean modernDungeon = this.version.isNewerThan(MCVersion.v1_12);

		List<Long> structureSeeds = Collections.synchronizedList(new ArrayList<>());

		Vec3i chunk = modernDungeon
			? this.position.toChunkCorner()
			: this.position.toChunkPos();

		device.reverse()
			.mapToObj(this::toDecorators)
			.reduce(LongStream::concat).orElse(LongStream.empty()).parallel()
			.forEach(spawnAttempt -> structureSeeds.addAll(ChunkRandomReverser.reversePopulationSeed(
				(spawnAttempt ^ LCG.JAVA.multiplier) - this.getSalt(),
				chunk.getX(), chunk.getZ(), this.version)));
		return structureSeeds;
	}

	private DynamicProgram getDungeonRand() {
		DynamicProgram device = DynamicProgram.create(LCG.JAVA);
		device.add(JavaCalls.nextInt(16).equalTo(this.position().getX() & 15));
		if(this.version.isNewerThan(MCVersion.v1_14)) {
			device.add(JavaCalls.nextInt(16).equalTo(this.position.getZ() & 15));
			device.add(JavaCalls.nextInt(256).equalTo(this.position().getY()));
		} else {
			device.add(JavaCalls.nextInt(256).equalTo(this.position().getY()));
			device.add(JavaCalls.nextInt(16).equalTo(this.position().getZ() & 15));
		}
		device.add(JavaCalls.nextInt(2).equalTo(this.floor.size.x >> 3));
		device.add(JavaCalls.nextInt(2).equalTo(this.floor.size.z >> 3));
		return device;
	}

	private LongStream toDecorators(long dungeonSeed) {
		if(this.version.isNewerThan(MCVersion.v1_12))
			return LongStream.iterate(dungeonSeed, FAILED_DUNGEON::nextSeed).limit(8);
		else
			return LongStream.iterate(dungeonSeed, REVERSE::nextSeed).limit(2000);
	}

	private long getSalt() {
		return switch(this.version) {
			case v1_17, v1_16 -> FOSSIL_BIOMES.contains(this.biome) ? 30003L : 30002L;
			case v1_15, v1_14, v1_13 -> 20003L;
			default -> 0L;
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

	public record Floor(Size size, List<Block> pattern) {
		public enum Block {
			COBBLE(2d, device -> device.add(JavaCalls.nextInt(4).equalTo(0))),
			MOSSY(0.41503749927d, device -> device.filteredSkip(rand -> rand.nextInt(4) != 0, 1)),
			UNKNOWN(0d, device -> device.skip(1));

			public final double bits;
			private final Consumer<DynamicProgram> javaCall;

			Block(double bits, Consumer<DynamicProgram> javaCall) {
				this.bits = bits;
				this.javaCall = javaCall;
			}

			public static Block of(char stringRep) {
				return Block.values()[stringRep - 48];
			}

			@Override
			public String toString() {
				return "" + this.ordinal();
			}
		}
	}
}
