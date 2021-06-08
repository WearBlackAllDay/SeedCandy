package wearblackallday.components.worldtab;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.mcutils.rand.seed.ChunkSeeds;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.version.MCVersion;
import wearblackallday.SeedCandy;
import wearblackallday.components.SeedTab;
import wearblackallday.data.Strings;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.QuadHuts;

import javax.swing.*;
import java.awt.GridLayout;

public class WorldTab extends SeedTab {
	public WorldTab() {
		super("WorldSeed");
		SelectionBox<MCVersion> versionSelector = new SelectionBox<>(SeedCandy.SUPPORTED_VERSIONS);
		JProgressBar progressBar = new JProgressBar(0, 1);

		LPanel selectionPanel = new LPanel()
			.addTextField("X", "x")
			.addTextField("Z", "z")
			.addTextField("salt", "s")
			.addComponent(versionSelector)
			.addComponent(progressBar);

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("check for nextLong()", () -> this.mapToString(worldSeed ->
				WorldSeed.isRandom(worldSeed)
					? "possible nextLong()"
					: "is NOT nextLong()"))
			.addButton("locate Quadhuts", () -> this.mapToString(worldSeed ->
				QuadHuts.find(worldSeed, versionSelector.getSelected()).toString()))
			.addButton("show/filter Spawnpoint", () -> {
				MCVersion version = versionSelector.getSelected();
				this.output.setText("");
				try {
					BPos target = new BPos(selectionPanel.getInt("x"), 0,
						selectionPanel.getInt("z"));

					POOL.execute(this.input.getLongs(), worldSeed -> {
						var biomeSource = new OverworldBiomeSource(version, worldSeed);
						if(biomeSource.getSpawnPoint().equals(target))
							this.output.addEntry(worldSeed);
					});
				} catch(NumberFormatException exception) {
					POOL.execute(this.input.getLongs(), worldSeed -> {
						var biomeSource = new OverworldBiomeSource(version, worldSeed);
						this.output.addEntry(String.format("%d (%d, %d)", worldSeed,
							biomeSource.getSpawnPoint().getX(), biomeSource.getSpawnPoint().getZ()));
					});
				}
			})
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
					selectionPanel.getInt("z"), versionSelector.getSelected())))
			.addButton("get RegionSeed", () -> this.mapSeeds(worldSeed ->
				ChunkSeeds.getRegionSeed(worldSeed, selectionPanel.getInt("x"),
					selectionPanel.getInt("z"), selectionPanel.getInt("s"), versionSelector.getSelected())))
			.addButton("get PillarSeed", () -> this.mapSeeds(WorldSeed::toPillarSeed))
			.addButton("copy Output", () -> Strings.clipboard(this.output.getText()))
			.addButton("clear text", () -> {
				this.input.setText("");
				this.output.setText("");
			});

		this.addComponents(buttons, selectionPanel);
	}
}
