package wearblackallday.util;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.seedutils.lcg.LCG;
import kaptainwutax.seedutils.mc.MCVersion;
import mjtb49.hashreversals.ChunkRandomReverser;
import randomreverser.call.java.FilteredSkip;
import randomreverser.call.java.NextInt;
import randomreverser.device.JavaRandomDevice;
import randomreverser.device.LCGReverserDevice;

import java.util.ArrayList;
import java.util.List;

public class Dungeon {
	public static List<Long> crack(String dungeonString, int posX, int posY, int posZ, MCVersion version, Biome biome) {
		List<Long> structureSeeds = new ArrayList<>();
		if(!dungeonString.matches("[0-2]+")) return structureSeeds;
		int offsetX = posX & 15;
		int offsetZ = posZ & 15;
		LCG failedDungeon = LCG.JAVA.combine(-5);

		JavaRandomDevice device = new JavaRandomDevice();
		device.addCall(NextInt.withValue(16, offsetX));
		if(version.isNewerThan(MCVersion.v1_14_4)) {
			device.addCall(NextInt.withValue(16, offsetZ));
			device.addCall(NextInt.withValue(256, posY));
		} else {
			device.addCall(NextInt.withValue(256, posY));
			device.addCall(NextInt.withValue(16, offsetZ));
		}
		device.addCall(NextInt.consume(2, 2));

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
						posX & -16,
						posZ & -16,
						version));
					decoratorSeed = failedDungeon.nextSeed(decoratorSeed);
				}
			});
		return structureSeeds;
	}

	private static long getDungeonSalt(MCVersion version, Biome biome) {
		if(version != MCVersion.v1_16) return 20003L;
		if(biome == Biome.DESERT ||
			biome == Biome.SWAMP ||
			biome == Biome.SWAMP_HILLS) {
			return 30003L;
		}
		return 30002L;
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
