package wearblackallday.seedcandy.components.worldtab;

import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.seed.ChunkSeeds;
import com.seedfinding.mccore.rand.seed.WorldSeed;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.LPanel;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.components.SeedTab;
import wearblackallday.seedcandy.util.Factory;
import wearblackallday.seedcandy.util.QuadHuts;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.stream.Collectors;

public class WorldTab extends SeedTab {
	public WorldTab() {
		super("WorldSeed");

		JSpinner xPos = Factory.numberSelector("X");
		JSpinner zPos = Factory.numberSelector("Z");
		JSpinner saltSpinner = Factory.numberSelector("salt");

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("check for nextLong()", () -> this.mapToString(worldSeed ->
				WorldSeed.isRandom(worldSeed)
					? "possible nextLong()"
					: "is NOT nextLong()"))
			.addButton("locate Quadhuts", () -> this.mapToString(worldSeed -> QuadHuts.find(worldSeed, SeedCandy.get().getVersion()).toString()))
			.addButton("show Spawnpoint", () -> this.mapToString(worldSeed ->
				SpawnPoint.getApproximateSpawn(new OverworldBiomeSource(SeedCandy.get().getVersion(), worldSeed)).toString()))
			.addButton("convert to hash", () -> this.mapSeeds(WorldSeed::toHash))
			.addButton("switch to ShadowSeed", () -> this.mapSeeds(WorldSeed::getShadowSeed))
			.addButton("get SisterSeeds", () -> this.mapToString(worldSeed ->
				WorldSeed.getSisterSeeds(worldSeed).asStream()
				.mapToObj(String::valueOf)
				.collect(Collectors.joining("\n"))))
			.addButton("reduce to StructureSeed", () -> this.mapSeeds(WorldSeed::toStructureSeed))
			.addButton("get PopulationSeed", () -> this.mapSeeds(worldSeed -> ChunkSeeds.getPopulationSeed(worldSeed, (Integer)xPos.getValue(),
				(Integer)zPos.getValue(), SeedCandy.get().getVersion())))
			.addButton("get RegionSeed", () -> this.mapSeeds(worldSeed -> ChunkSeeds.getRegionSeed(worldSeed, (Integer)xPos.getValue(),
				(Integer)zPos.getValue(), (Integer)saltSpinner.getValue(), SeedCandy.get().getVersion())))
			.addButton("get PillarSeed", () -> this.mapSeeds(WorldSeed::toPillarSeed));

		this.addComponents(buttons, SwingUtils.addSet(new JPanel(), xPos, zPos, saltSpinner));
	}
}
