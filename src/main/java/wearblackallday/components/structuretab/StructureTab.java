package wearblackallday.components.structuretab;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.seed.StructureSeed;
import wearblackallday.components.SeedTab;
import wearblackallday.data.Strings;
import wearblackallday.swing.components.GridPanel;
import wearblackallday.swing.components.LPanel;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.concurrent.atomic.AtomicInteger;

public class StructureTab extends SeedTab {
	public StructureTab() {
		super("StructureSeed");
		JProgressBar progressBar = new JProgressBar(0, 1);
		GridPanel<BiomeUnit> biomePanel = new GridPanel<>(1, 16, BiomeUnit::new);

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(2, 2))
			.addButton("reverse to nextLong()", () -> {
				this.output.setText("");
				for(long structureSeed : this.input.getLongs()) {
					for(long worldSeed : StructureSeed.toRandomWorldSeeds(structureSeed)) {
						this.output.addEntry(worldSeed);
					}
				}
			})
			.addButton("crack with biomes", () -> {
				this.output.setText("");
				progressBar.setMaximum(Strings.countLines(this.input.getText()) << 16);
				AtomicInteger progress = new AtomicInteger(0);
				POOL.execute(this.input.getLongs(), structureSeed ->
					StructureSeed.getWorldSeeds(structureSeed).forEachRemaining(worldSeed -> {
						var biomeSource = new OverworldBiomeSource(MCVersion.v1_16, worldSeed);
						progressBar.setValue(progress.incrementAndGet());
						if(biomePanel.allMatch(biomeUnit -> biomeUnit.matches(biomeSource))) {
							SwingUtilities.invokeLater(() -> this.output.addEntry(worldSeed));
						}
					}));
			})
			.addButton("verify WorldSeeds", () -> {
				this.output.setText("");
				for(long worldSeed : this.input.getLongs()) {
					var biomeSource = new OverworldBiomeSource(MCVersion.v1_16, worldSeed);
					if(biomePanel.allMatch(biomeUnit -> biomeUnit.matches(biomeSource))) {
						SwingUtilities.invokeLater(() -> this.output.addEntry(worldSeed));
					}
				}
			})
			.addButton("copy Output", () -> Strings.clipboard(this.output.getText()));

		this.addComponents(biomePanel, buttons, progressBar);
	}
}
