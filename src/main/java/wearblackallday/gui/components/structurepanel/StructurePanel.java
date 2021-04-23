package wearblackallday.gui.components.structurepanel;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.seed.StructureSeed;
import wearblackallday.data.Strings;
import wearblackallday.gui.SeedCandy;
import wearblackallday.gui.components.TextBlock;
import wearblackallday.swing.components.CustomPanel;
import wearblackallday.swing.components.GridPanel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class StructurePanel extends JPanel {
	public StructurePanel() {
		TextBlock inputText = new TextBlock(true);
		TextBlock outputText = new TextBlock(false);
		Box inputPanel = new Box(BoxLayout.Y_AXIS);
		GridPanel<BiomeUnit> biomePanel = new GridPanel<>(16, 1, BiomeUnit::new);
		JProgressBar progressBar = new JProgressBar(0, 1);

		JPanel buttonPanel = new CustomPanel(new GridLayout(2, 2), 170, 30)
			.addButton("reverse to nextLong()", (panel, button, event) -> {
				outputText.setText("");
				Arrays.stream(Strings.splitToLongs(inputText.getText()))
					.flatMap(seed -> StructureSeed.toRandomWorldSeeds(seed).stream()
						.mapToLong(Long::longValue))
					.mapToObj(String::valueOf)
					.forEach(outputText::addEntry);
			})
			.addButton("crack with biomes", (panel, button, event) -> {
				outputText.setText("");
				progressBar.setMaximum(Strings.countLines(inputText.getText()) * 65536);
				AtomicInteger progress = new AtomicInteger(0);
				SeedCandy.POOL.execute(Strings.splitToLongs(inputText.getText()), seed -> StructureSeed.getWorldSeeds(seed).forEachRemaining(candidate -> {
					OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_2, candidate);
					progressBar.setValue(progress.incrementAndGet());
					if(biomePanel.allMatch(biomeUnit -> biomeUnit.matches(biomeSource))) {
						SwingUtilities.invokeLater(() -> outputText.addEntry(String.valueOf(candidate)));
					}
				}));
			})
			.addButton("verify WorldSeeds", (panel, button, event) -> {
				outputText.setText("");
				for(long seed : Strings.splitToLongs(inputText.getText())) {
					OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_2, seed);
					if(biomePanel.allMatch(biomeUnit -> biomeUnit.matches(biomeSource))) {
						SwingUtilities.invokeLater(() -> outputText.addEntry(String.valueOf(seed)));
					}
				}
			})
			.addComponent(progressBar);

		this.setLayout(new BorderLayout());
		inputPanel.add(biomePanel);
		inputPanel.add(buttonPanel);
		this.add(inputText, BorderLayout.WEST);
		this.add(outputText, BorderLayout.CENTER);
		this.add(inputPanel, BorderLayout.EAST);
		this.setName("StructureSeed");
	}
}
