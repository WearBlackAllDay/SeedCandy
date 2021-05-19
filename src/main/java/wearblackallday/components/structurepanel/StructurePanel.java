package wearblackallday.components.structurepanel;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.seed.StructureSeed;
import wearblackallday.data.Strings;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.GridPanel;
import wearblackallday.swing.components.LPanel;
import wearblackallday.components.TextBlock;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.concurrent.atomic.AtomicInteger;

import static wearblackallday.SeedCandy.POOL;

public class StructurePanel extends Box {
	public StructurePanel() {
		super(BoxLayout.X_AXIS);
		TextBlock inputText = new TextBlock(true);
		TextBlock outputText = new TextBlock(false);
		GridPanel<BiomeUnit> biomePanel =
			new GridPanel<>(1, 16, BiomeUnit::new);
		JProgressBar progressBar = new JProgressBar(0, 1);

		JPanel buttonPanel = new LPanel()
			.withLayout(new GridLayout(2, 2))
			.defaultSize(170, 30)
			.addButton("reverse to nextLong()", (panel, button, event) -> {
				outputText.setText("");
				for(long structureSeed : Strings.splitToLongs(inputText.getText())) {
					for(long worldSeed : StructureSeed.toRandomWorldSeeds(structureSeed)) {
						outputText.addEntry(worldSeed);
					}
				}
			})
			.addButton("crack with biomes", (panel, button, event) -> {
				outputText.setText("");
				progressBar.setMaximum(Strings.countLines(inputText.getText()) * 65536);
				AtomicInteger progress = new AtomicInteger(0);
				POOL.execute(inputText.getLongs(), structureSeed ->
					StructureSeed.getWorldSeeds(structureSeed).forEachRemaining(worldSeed -> {
						OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_2, worldSeed);
						progressBar.setValue(progress.getAndIncrement());
						if(biomePanel.allMatch(biomeUnit -> biomeUnit.matches(biomeSource))) {
							SwingUtilities.invokeLater(() -> outputText.addEntry(worldSeed));
						}
					}));
			})
			.addButton("verify WorldSeeds", (panel, button, event) -> {
				outputText.setText("");
				for(long worldSeed : inputText.getLongs()) {
					OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_2, worldSeed);
					if(biomePanel.allMatch(biomeUnit -> biomeUnit.matches(biomeSource))) {
						SwingUtilities.invokeLater(() -> outputText.addEntry(worldSeed));
					}
				}
			})
			.addComponent(progressBar);

		SwingUtils.addSet(this, inputText, outputText,
			SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), biomePanel, buttonPanel));
		this.setName("StructureSeed");
	}
}
