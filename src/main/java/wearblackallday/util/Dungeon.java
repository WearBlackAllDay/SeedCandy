package wearblackallday.util;

import com.seedfinding.latticg.reversal.DynamicProgram;
import com.seedfinding.latticg.reversal.calltype.java.JavaCalls;
import com.seedfinding.latticg.util.LCG;
import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mccore.version.UnsupportedVersion;
import com.seedfinding.mcreversal.ChunkRandomReverser;

import java.util.*;

public record Dungeon(Size size, String floor, int posX, int posY, int posZ, MCVersion version, Biome biome) {
	public static final Set<Biome> FOSSIL_BIOMES = Set.of(Biomes.DESERT, Biomes.SWAMP, Biomes.SWAMP_HILLS);

	public List<Long> crack() {
		if(!this.floor.matches("[0-2]+")) return Collections.emptyList();

		LCG failedDungeon = LCG.JAVA.combine(-5);
		DynamicProgram device = this.getDungeonRand();

		for(int i = 0; i < this.floor.length(); i++) {
			switch(this.floor.charAt(i)) {
				case '0' -> device.add(JavaCalls.nextInt(4).equalTo(0));
				case '1' -> device.filteredSkip( r -> r.nextInt(4) != 0, 1);
				case '2' -> device.skip(1);
			}
		}

		return device.reverse().parallel()
			.mapToObj(decoratorSeed -> {
				List<Long> structureSeeds = new ArrayList<>();
				for(int i = 0; i < 8; i++) {
					structureSeeds.addAll(ChunkRandomReverser.reversePopulationSeed(
						(decoratorSeed ^ LCG.JAVA.multiplier) - this.getSalt(),
						this.posX & -16, this.posZ & -16, this.version));
					decoratorSeed = failedDungeon.nextSeed(decoratorSeed);
				}
				return structureSeeds;
			})
			.flatMap(List::stream)
			.toList();
	}

	private DynamicProgram getDungeonRand() {
		DynamicProgram device = DynamicProgram.create(com.seedfinding.latticg.util.LCG.JAVA);
		device.add(JavaCalls.nextInt(16).equalTo(this.posX & 15));
		if(this.version.isNewerOrEqualTo(MCVersion.v1_15)) {
			device.add(JavaCalls.nextInt(16).equalTo(this.posZ & 15));
			device.add(JavaCalls.nextInt(256).equalTo(this.posY));
		} else {
			device.add(JavaCalls.nextInt(256).equalTo(this.posY));
			device.add(JavaCalls.nextInt(16).equalTo(this.posZ & 15));
		}
		device.add(JavaCalls.nextInt(2).equalTo((this.size.x - 7) >> 1));
		device.add(JavaCalls.nextInt(2).equalTo((this.size.z - 7) >> 1));
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
