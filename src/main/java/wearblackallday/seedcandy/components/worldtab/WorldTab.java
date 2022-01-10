package wearblackallday.seedcandy.components.worldtab;

import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.rand.seed.ChunkSeeds;
import com.seedfinding.mccore.rand.seed.WorldSeed;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import wearblackallday.seedcandy.components.SeedTab;
import wearblackallday.swing.components.LPanel;
import wearblackallday.seedcandy.util.QuadHuts;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.stream.Collectors;

public class WorldTab extends SeedTab {
	public WorldTab() {
		super("WorldSeed");

		LPanel selectionPanel = new LPanel()
			.addTextField("X", "x")
			.addTextField("Z", "z")
			.addTextField("salt", "s");

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("check for nextLong()", () -> this.mapToString(worldSeed ->
				WorldSeed.isRandom(worldSeed)
					? "possible nextLong()"
					: "is NOT nextLong()"))
			.addButton("locate Quadhuts", () -> this.mapToString(worldSeed ->
				QuadHuts.find(worldSeed, this.getVersion()).toString()))
			.addButton("show Spawnpoint", () -> this.mapToString(worldSeed -> {
				BPos spawn = SpawnPoint.getApproximateSpawn(new OverworldBiomeSource(this.getVersion(), worldSeed));
				return "{" + spawn.getX() + ", " + spawn.getZ() + "}";
			}))
			.addButton("convert to hash", () -> this.mapSeeds(WorldSeed::toHash))
			.addButton("switch to ShadowSeed", () -> this.mapSeeds(WorldSeed::getShadowSeed))
			.addButton("get SisterSeeds", () -> this.mapToString(worldSeed ->
				WorldSeed.getSisterSeeds(worldSeed).asStream()
				.mapToObj(String::valueOf)
				.collect(Collectors.joining("\n"))))
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
