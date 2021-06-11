package wearblackallday.components.structuretab;

import kaptainwutax.mcutils.rand.seed.StructureSeed;
import wearblackallday.components.SeedTab;
import wearblackallday.data.Strings;
import wearblackallday.swing.components.LPanel;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StructureTab extends SeedTab {
	public StructureTab() {
		super("StructureSeed");
		BiomePanel biomePanel = new BiomePanel();

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(2, 2))
			.addButton("reverse to nextLong()", () -> {
				this.output.setText("");
				for(long structureSeed : this.input.getLongs()) {
					StructureSeed.toRandomWorldSeeds(structureSeed)
						.forEach(this.output::addEntry);
				}
			})
			.addButton("crack with biomes", () -> {
				this.toggleComponents(false);
				long[] structureSeeds = this.input.getLongs();
				long[] worldSeeds = new long[structureSeeds.length << 16];

				int[] c = {0};
				for(long structureSeed : structureSeeds) {
					StructureSeed.getWorldSeeds(structureSeed)
						.forEachRemaining(worldSeed -> worldSeeds[c[0]++] = worldSeed);
				}

				AtomicInteger progress = new AtomicInteger(0);
				this.progressBar.setMaximum(structureSeeds.length << 16);
				List<String> validSeeds =
					Collections.synchronizedList(new ArrayList<>(worldSeeds.length));
				int threads = this.pool.getThreadCount();

				for(int i = 0; i < threads; i++) {
					int start = i;
					this.pool.execute(() -> {
						int current = start;
						while(current < worldSeeds.length) {
							if(biomePanel.matchesSeed(worldSeeds[current])) {
								validSeeds.add(String.valueOf(worldSeeds[current]));
							}
							SwingUtilities.invokeLater(() -> {
								this.progressBar.setValue(progress.incrementAndGet());
								if(progress.get() == this.progressBar.getMaximum()) {
									this.output.setText(String.join("\n", validSeeds));
									this.toggleComponents(true);
								}
							});
							current += threads;
						}
					});
				}
			})
			.addButton("verify WorldSeeds", () -> {
				this.output.setText("");
				for(long worldSeed : this.input.getLongs()) {
					if(biomePanel.matchesSeed(worldSeed)) {
						SwingUtilities.invokeLater(() -> this.output.addEntry(worldSeed));
					}
				}
			})
			.addButton("copy Output", () -> Strings.clipboard(this.getOutput()));

		this.addComponents(biomePanel, buttons);
	}
}
