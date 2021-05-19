package wearblackallday.components.worldpanel;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.ChunkSeeds;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import wearblackallday.data.Strings;
import wearblackallday.components.TextBlock;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.QuadFinder;

import javax.swing.*;
import java.awt.GridLayout;

import static wearblackallday.SeedCandy.POOL;

public class WorldPanel extends Box {
	public WorldPanel() {
		super(BoxLayout.X_AXIS);
		TextBlock inputText = new TextBlock(true);
		TextBlock outputText = new TextBlock(false);
		SelectionBox<MCVersion> versionSelector = new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);
		JProgressBar progressBar = new JProgressBar(0, 1);

		LPanel selectionPanel = new LPanel()
			.defaultSize(40, 25)
			.addTextField("X", "x")
			.addTextField("Z", "z")
			.addTextField("salt", "s", 60, 25)
			.addComponent(versionSelector)
			.addComponent(progressBar);

		LPanel buttonPanel = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("check for nextLong()", (panel, button, event) -> {
				outputText.setText("");
				for(long worldSeed : inputText.getLongs()) {
					outputText.addEntry(WorldSeed.isRandom(worldSeed) ? "possible nextLong()" : "is NOT nextLong()");
				}
			})
			.addButton("locate Quadhuts", (panel, button, event) -> {
				outputText.setText("");
				progressBar.setMaximum(Strings.countLines(inputText.getText()));
				POOL.execute(inputText.getLongs(), worldSeed ->
					outputText.addEntry(QuadFinder.find(worldSeed, versionSelector.getSelected())));
			})
			.addButton("show/filter Spawnpoint", (panel, button, event) -> {
				MCVersion version = versionSelector.getSelected();
				outputText.setText("");
				try {
					BPos target = new BPos(Integer.parseInt(selectionPanel.getText("x").trim()), 0,
						Integer.parseInt(selectionPanel.getText("z").trim()));

					POOL.execute(inputText.getLongs(), worldSeed -> {
						OverworldBiomeSource biomeSource = new OverworldBiomeSource(version, worldSeed);
						if(biomeSource.getSpawnPoint().equals(target)) {
							outputText.addEntry(worldSeed);
						}
					});
				} catch(NumberFormatException exception) {
					POOL.execute(inputText.getLongs(), worldSeed -> {
						OverworldBiomeSource biomeSource = new OverworldBiomeSource(version, worldSeed);
						outputText.addEntry(String.format("%d (%d, %d)", worldSeed,
							biomeSource.getSpawnPoint().getX(), biomeSource.getSpawnPoint().getZ()));
					});
				}
			})
			.addButton("convert to hash", (panel, button, event) -> {
				outputText.setText("");
				for(long worldSeed : inputText.getLongs()) {
					outputText.addEntry(WorldSeed.toHash(worldSeed));
				}
			})
			.addButton("switch to ShadowSeed", (panel, button, event) -> {
				outputText.setText("");
				for(long worldSeed : inputText.getLongs()) {
					outputText.addEntry(WorldSeed.getShadowSeed(worldSeed));
				}
			})
			.addButton("get SisterSeeds", (panel, button, event) -> {
				outputText.setText("");
				StringBuilder stringBuilder = new StringBuilder();
				for(long worldSeed : inputText.getLongs()) {
					WorldSeed.getSisterSeeds(worldSeed).forEachRemaining(sisterSeed ->
						stringBuilder.append(sisterSeed).append("\n"));
				}
				outputText.setText(stringBuilder.toString());
			})
			.addButton("reduce to StructureSeed", (panel, button, event) -> {
				outputText.setText("");
				for(long worldSeed : inputText.getLongs()) {
					outputText.addEntry(WorldSeed.toStructureSeed(worldSeed));
				}
			})
			.addButton("get PopulationSeed", (panel, button, event) -> {
				outputText.setText("");
				MCVersion version = versionSelector.getSelected();
				int x, z;
				try {
					x = Integer.parseInt(selectionPanel.getText("x").trim());
					z = Integer.parseInt(selectionPanel.getText("z").trim());
				} catch(NumberFormatException exception) {
					x = z = 0;
				}
				for(long worldSeed : inputText.getLongs()) {
					outputText.addEntry(ChunkSeeds.getPopulationSeed(worldSeed, x, z, version));
				}
			})
			.addButton("get RegionSeed", (panel, button, event) -> {
				outputText.setText("");
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
				for(long worldSeed : inputText.getLongs()) {
					outputText.addEntry(ChunkSeeds.getRegionSeed(worldSeed, x, z, salt, version));
				}
			})
			.addButton("get PillarSeed", (panel, button, event) -> {
				outputText.setText("");
				for(long worldSeed : inputText.getLongs()) {
					outputText.addEntry(WorldSeed.toPillarSeed(worldSeed));
				}
			})
			.addButton("copy Output", (panel, button, event) -> Strings.clipboard(outputText.getText()))
			.addButton("clear text", (panel, button, event) -> {
				inputText.setText("");
				outputText.setText("");
			});

		SwingUtils.addSet(this, inputText, outputText,
			new JSplitPane(JSplitPane.VERTICAL_SPLIT, buttonPanel, selectionPanel));
		this.setName("WorldSeed");
	}
}
