package wearblackallday.util;

import com.seedfinding.latticg.reversal.DynamicProgram;
import com.seedfinding.latticg.reversal.calltype.java.JavaCalls;
import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.biome.Biomes;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.seedutils.lcg.LCG;
import mjtb49.hashreversals.ChunkRandomReverser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Dungeon {
	public static final Set<Biome> FOSSIL_BIOMES = Set.of(Biomes.DESERT, Biomes.SWAMP, Biomes.SWAMP_HILLS);

	public static List<Long> crack(String dungeonString, int posX, int posY, int posZ,Size size, MCVersion version, Biome biome) {
		if(!dungeonString.matches("[0-2]+")) return Collections.emptyList();

		LCG failedDungeon = LCG.JAVA.combine(-5);
		DynamicProgram device = getDungeonRand(posX, posY, posZ, size, version);

		for(char c : dungeonString.toCharArray()) {
			switch(c) {
				case '0' -> device.add(JavaCalls.nextInt(4).equalTo(0));
				case '1' -> device.filteredSkip( r -> r.nextInt(4) != 0, 1);
				case '2' -> device.skip(1);
			}
		}

		return device.reverse().boxed()
			.parallel()
			.map(decoratorSeed -> {
				List<Long> structureSeeds = new ArrayList<>();
				for(int i = 0; i < 8; i++) {
					structureSeeds.addAll(ChunkRandomReverser.reversePopulationSeed(
						(decoratorSeed ^ LCG.JAVA.multiplier) - getDungeonSalt(version, biome),
						posX & -16, posZ & -16, version));
					decoratorSeed = failedDungeon.nextSeed(decoratorSeed);
				}
				return structureSeeds;
			})
			.flatMap(List::stream)
			.collect(Collectors.toList());
	}

	private static DynamicProgram getDungeonRand(int posX, int posY, int posZ, Size size, MCVersion version) {
		DynamicProgram device = DynamicProgram.create(com.seedfinding.latticg.util.LCG.JAVA);
		device.add(JavaCalls.nextInt(16).equalTo(posX & 15));
		if(version.isNewerOrEqualTo(MCVersion.v1_15)) {
			device.add(JavaCalls.nextInt(16).equalTo(posZ & 15));
			device.add(JavaCalls.nextInt(256).equalTo(posY));
		} else {
			device.add(JavaCalls.nextInt(256).equalTo(posY));
			device.add(JavaCalls.nextInt(16).equalTo(posZ & 15));
		}
		device.add(JavaCalls.nextInt(2).equalTo((size.x - 7) / 2));
		device.add(JavaCalls.nextInt(2).equalTo((size.z - 7) / 2));
		return device;
	}

	private static long getDungeonSalt(MCVersion version, Biome biome) {
		return switch(version) {
			case v1_17, v1_16 -> FOSSIL_BIOMES.contains(biome) ? 30003L : 30002L;
			case v1_15, v1_14, v1_13 -> 20003L;
			default -> throw new IllegalArgumentException();
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
