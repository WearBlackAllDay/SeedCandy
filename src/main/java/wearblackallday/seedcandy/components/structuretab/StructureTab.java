package wearblackallday.seedcandy.components.structuretab;

import com.seedfinding.mccore.rand.seed.StructureSeed;
import com.seedfinding.mccore.util.data.SeedIterator;
import wearblackallday.data.Strings;
import wearblackallday.seedcandy.components.SeedTab;
import wearblackallday.swing.components.LPanel;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.Arrays;

public class StructureTab extends SeedTab {
	public StructureTab() {
		super("StructureSeed");
		BiomePanel biomePanel = new BiomePanel();

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("reverse to nextLong()", () -> {
				this.output.clear();
				for(long structureSeed : this.input.getLongs()) {
					StructureSeed.toRandomWorldSeeds(structureSeed)
						.forEach(this.output::addEntry);
				}
			})
			.addButton("crack with biomes", () -> {
				this.output.clear();
				long[] worldSeeds = Arrays.stream(this.input.getLongs())
					.mapToObj(StructureSeed::getWorldSeeds)
					.flatMapToLong(SeedIterator::asStream)
					.toArray();

				this.threadedMap(worldSeeds, seed -> biomePanel.matchesSeed(seed) ? String.valueOf(seed) : "");
			})
			.addButton("verify WorldSeeds", () -> {
				this.output.clear();
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
