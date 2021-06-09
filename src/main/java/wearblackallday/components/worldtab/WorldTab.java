package wearblackallday.components.worldtab;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.mcutils.rand.seed.ChunkSeeds;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import wearblackallday.components.SeedTab;
import wearblackallday.swing.components.LPanel;
import wearblackallday.util.QuadHuts;

import javax.swing.*;
import java.awt.GridLayout;

public class WorldTab extends SeedTab {
	public WorldTab() {
		super("WorldSeed");

		LPanel selectionPanel = new LPanel()
			.addTextField("X", "x")
			.addTextField("Z", "z")
			.addTextField("salt", "s")
			.addComponent(this.progressBar);

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("check for nextLong()", () -> this.mapToString(worldSeed ->
				WorldSeed.isRandom(worldSeed)
					? "possible nextLong()"
					: "is NOT nextLong()"))
			.addButton("locate Quadhuts", () -> this.mapToString(worldSeed ->
				QuadHuts.find(worldSeed, this.getVersion()).toString()))
			.addButton("show Spawnpoint", () -> this.mapToString(worldSeed -> {
				var biomeSource = new OverworldBiomeSource(this.getVersion(), worldSeed);
				return "{" + biomeSource.getSpawnPoint().getX() + ", " +
					biomeSource.getSpawnPoint().getZ() + "}";
			}))
			.addButton("convert to hash", () -> this.mapSeeds(WorldSeed::toHash))
			.addButton("switch to ShadowSeed", () -> this.mapSeeds(WorldSeed::getShadowSeed))
			.addButton("get SisterSeeds", () -> this.mapToString(worldSeed -> {
				var buffer = new StringBuilder();
				WorldSeed.getSisterSeeds(worldSeed).forEachRemaining(sisterSeed ->
					buffer.append(sisterSeed).append("\n"));
				return buffer.toString();
			}))
			.addButton("reduce to StructureSeed", () -> this.mapSeeds(WorldSeed::toStructureSeed))
			.addButton("get PopulationSeed", () -> this.mapSeeds(worldSeed ->
				ChunkSeeds.getPopulationSeed(worldSeed, selectionPanel.getInt("x"),
					selectionPanel.getInt("z"), this.getVersion())))
			.addButton("get RegionSeed", () -> this.mapSeeds(worldSeed ->
				ChunkSeeds.getRegionSeed(worldSeed, selectionPanel.getInt("x"),
					selectionPanel.getInt("z"), selectionPanel.getInt("s"), this.getVersion())))
			.addButton("get PillarSeed", () -> this.mapSeeds(WorldSeed::toPillarSeed));

		this.addComponents(buttons, selectionPanel);
	}
}
