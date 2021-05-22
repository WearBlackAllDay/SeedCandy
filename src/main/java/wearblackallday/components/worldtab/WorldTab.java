package wearblackallday.components.worldtab;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.ChunkSeeds;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import wearblackallday.components.SeedTab;
import wearblackallday.data.Strings;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.QuadFinder;

import javax.swing.*;
import java.awt.GridLayout;

import static wearblackallday.SeedCandy.POOL;

public class WorldTab extends SeedTab {
	public WorldTab() {
		super("WorldSeed");
		SelectionBox<MCVersion> versionSelector =
			new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);
		JProgressBar progressBar = new JProgressBar(0, 1);

		LPanel selectionPanel = new LPanel()
			.addTextField("X", "x")
			.addTextField("Z", "z")
			.addTextField("salt", "s")
			.addComponent(versionSelector)
			.addComponent(progressBar);

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("check for nextLong()", (panel, button, event) -> {
				this.output.setText("");
				for(long worldSeed : this.input.getLongs()) {
					this.output.addEntry(WorldSeed.isRandom(worldSeed) ? "possible nextLong()" : "is NOT nextLong()");
				}
			})
			.addButton("locate Quadhuts", (panel, button, event) -> {
				this.output.setText("");
				progressBar.setMaximum(Strings.countLines(this.input.getText()));
				POOL.execute(this.input.getLongs(), worldSeed ->
					this.output.addEntry(QuadFinder.find(worldSeed, versionSelector.getSelected())));
			})
			.addButton("show/filter Spawnpoint", (panel, button, event) -> {
				MCVersion version = versionSelector.getSelected();
				this.output.setText("");
				try {
					BPos target = new BPos(Integer.parseInt(selectionPanel.getText("x").trim()), 0,
						Integer.parseInt(selectionPanel.getText("z").trim()));

					POOL.execute(this.input.getLongs(), worldSeed -> {
						OverworldBiomeSource biomeSource = new OverworldBiomeSource(version, worldSeed);
						if(biomeSource.getSpawnPoint().equals(target)) {
							this.output.addEntry(worldSeed);
						}
					});
				} catch(NumberFormatException exception) {
					POOL.execute(this.input.getLongs(), worldSeed -> {
						OverworldBiomeSource biomeSource = new OverworldBiomeSource(version, worldSeed);
						this.output.addEntry(String.format("%d (%d, %d)", worldSeed,
							biomeSource.getSpawnPoint().getX(), biomeSource.getSpawnPoint().getZ()));
					});
				}
			})
			.addButton("convert to hash", (panel, button, event) -> {
				this.output.setText("");
				for(long worldSeed : this.input.getLongs()) {
					this.output.addEntry(WorldSeed.toHash(worldSeed));
				}
			})
			.addButton("switch to ShadowSeed", (panel, button, event) -> {
				this.output.setText("");
				for(long worldSeed : this.input.getLongs()) {
					this.output.addEntry(WorldSeed.getShadowSeed(worldSeed));
				}
			})
			.addButton("get SisterSeeds", (panel, button, event) -> {
				this.output.setText("");
				StringBuilder stringBuilder = new StringBuilder();
				for(long worldSeed : this.input.getLongs()) {
					WorldSeed.getSisterSeeds(worldSeed).forEachRemaining(sisterSeed ->
						stringBuilder.append(sisterSeed).append("\n"));
				}
				this.output.setText(stringBuilder.toString());
			})
			.addButton("reduce to StructureSeed", (panel, button, event) -> {
				this.output.setText("");
				for(long worldSeed : this.input.getLongs()) {
					this.output.addEntry(WorldSeed.toStructureSeed(worldSeed));
				}
			})
			.addButton("get PopulationSeed", (panel, button, event) -> {
				this.output.setText("");
				MCVersion version = versionSelector.getSelected();
				int x, z;
				try {
					x = Integer.parseInt(selectionPanel.getText("x").trim());
					z = Integer.parseInt(selectionPanel.getText("z").trim());
				} catch(NumberFormatException exception) {
					x = z = 0;
				}
				for(long worldSeed : this.input.getLongs()) {
					this.output.addEntry(ChunkSeeds.getPopulationSeed(worldSeed, x, z, version));
				}
			})
			.addButton("get RegionSeed", (panel, button, event) -> {
				this.output.setText("");
				MCVersion version = versionSelector.getSelected();
				int x, z, salt;
				try {
					x = Integer.parseInt(selectionPanel.getText("x").trim());
					z = Integer.parseInt(selectionPanel.getText("z").trim());
					salt = Integer.parseInt(selectionPanel.getText("s").trim());
				} catch(NumberFormatException exception) {
					x = z = 0;
					salt = 14357620;
				}
				for(long worldSeed : this.input.getLongs()) {
					this.output.addEntry(ChunkSeeds.getRegionSeed(worldSeed, x, z, salt, version));
				}
			})
			.addButton("get PillarSeed", (panel, button, event) -> {
				this.output.setText("");
				for(long worldSeed : this.input.getLongs()) {
					this.output.addEntry(WorldSeed.toPillarSeed(worldSeed));
				}
			})
			.addButton("copy Output", (panel, button, event) -> Strings.clipboard(this.output.getText()))
			.addButton("clear text", (panel, button, event) -> {
				this.input.setText("");
				this.output.setText("");
			});

		this.addComponents(buttons, selectionPanel);
	}
}
