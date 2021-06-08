package wearblackallday.util;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.biome.Biomes;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.seedutils.lcg.LCG;
import mjtb49.hashreversals.ChunkRandomReverser;
import randomreverser.call.java.FilteredSkip;
import randomreverser.call.java.NextInt;
import randomreverser.device.JavaRandomDevice;
import randomreverser.device.LCGReverserDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Dungeon {
	public static final Set<Biome> FOSSIL_BIOMES = Set.of(Biomes.DESERT, Biomes.SWAMP, Biomes.SWAMP_HILLS);

	public static List<Long> crack(String dungeonString, int posX, int posY, int posZ, MCVersion version, Biome biome) {
		List<Long> structureSeeds = new ArrayList<>();
		if(!dungeonString.matches("[0-2]+")) return structureSeeds;

		LCG failedDungeon = LCG.JAVA.combine(-5);
		JavaRandomDevice device = getDungeonRand(posX, posY, posZ, version);

		for(char c : dungeonString.toCharArray()) {
			switch(c) {
				case '0' -> device.addCall(NextInt.withValue(4, 0));
				case '1' -> device.addCall(FilteredSkip.filter(LCG.JAVA, r ->
					r.nextInt(4) != 0, 1));
				case '2' -> device.addCall(NextInt.consume(4, 1));
			}
		}

		device.streamSeeds(LCGReverserDevice.Process.EVERYTHING)
			.parallel()
			.limit(1)
			.findAny()
			.ifPresent(decoratorSeed -> {
				for(int i = 0; i < 8; i++) {
					structureSeeds.addAll(ChunkRandomReverser.reversePopulationSeed(
						(decoratorSeed ^ LCG.JAVA.multiplier) - getDungeonSalt(version, biome),
						posX & -16, posZ & -16, version));
					decoratorSeed = failedDungeon.nextSeed(decoratorSeed);
				}
			});
		return structureSeeds;
	}

	private static JavaRandomDevice getDungeonRand(int posX, int posY, int posZ, MCVersion version) {
		JavaRandomDevice device = new JavaRandomDevice();
		device.addCall(NextInt.withValue(16, posX & 15));
		if(version.isNewerOrEqualTo(MCVersion.v1_15)) {
			device.addCall(NextInt.withValue(16, posZ & 15));
			device.addCall(NextInt.withValue(256, posY));
		} else {
			device.addCall(NextInt.withValue(256, posY));
			device.addCall(NextInt.withValue(16, posZ & 15));
		}
		device.addCall(NextInt.consume(2, 2));
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

		public final int x, y;

		Size(int x, int y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return this.x + "x" + this.y;
		}
	}
}
