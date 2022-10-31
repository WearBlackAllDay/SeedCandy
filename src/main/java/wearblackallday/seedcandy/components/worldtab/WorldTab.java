package wearblackallday.seedcandy.components.worldtab;

import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.seed.ChunkSeeds;
import com.seedfinding.mccore.rand.seed.WorldSeed;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.seedcandy.components.SeedTab;
import wearblackallday.seedcandy.util.*;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.stream.Collectors;

public class WorldTab extends SeedTab {
	public WorldTab() {
		super("WorldSeed");

		JSpinner xPos = Factory.numberSelector("X");
		JSpinner zPos = Factory.numberSelector("Z");
		JSpinner saltSpinner = Factory.numberSelector("salt");

		JPanel buttons = SwingUtils.addAll(new JPanel(new GridLayout(0, 1)),
			Factory.actionButton("check for nextLong()", () -> this.mapSequential(worldSeed ->
				WorldSeed.isRandom(worldSeed)
					? "\spossible nextLong()"
					: "\sis NOT nextLong()")),
			Factory.actionButton("locate Quadhuts", () -> this.mapSequential(worldSeed -> QuadHuts.find(worldSeed, Config.get().getMcVersion()).toString())),
			Factory.actionButton("show Spawnpoint", () -> this.mapSequential(worldSeed ->
				SpawnPoint.getApproximateSpawn(new OverworldBiomeSource(Config.get().getMcVersion(), worldSeed)).toString())),
			Factory.actionButton("convert to hash", () -> this.map(WorldSeed::toHash)),
			Factory.actionButton("switch to ShadowSeed", () -> this.map(WorldSeed::getShadowSeed)),
			Factory.actionButton("get SisterSeeds", () -> this.mapSequential(worldSeed ->
				WorldSeed.getSisterSeeds(worldSeed).asStream()
					.mapToObj(String::valueOf)
					.collect(Collectors.joining("\n")))),
			Factory.actionButton("reduce to StructureSeed", () -> this.map(WorldSeed::toStructureSeed)),
			Factory.actionButton("get PopulationSeed", () -> this.map(worldSeed -> ChunkSeeds.getPopulationSeed(worldSeed, (Integer)xPos.getValue(),
				(Integer)zPos.getValue(), Config.get().getMcVersion()))),
			Factory.actionButton("get RegionSeed", () -> this.map(worldSeed -> ChunkSeeds.getRegionSeed(worldSeed, (Integer)xPos.getValue(),
				(Integer)zPos.getValue(), (Integer)saltSpinner.getValue(), Config.get().getMcVersion()))),
			Factory.actionButton("get PillarSeed", () -> this.map(WorldSeed::toPillarSeed))
		);

		this.add(buttons);
		this.add(SwingUtils.addAll(new JPanel(), xPos, zPos, saltSpinner));
	}
}
