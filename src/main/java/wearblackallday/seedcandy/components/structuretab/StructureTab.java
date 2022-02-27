package wearblackallday.seedcandy.components.structuretab;

import com.seedfinding.mccore.rand.seed.StructureSeed;
import com.seedfinding.mccore.util.data.SeedIterator;
import wearblackallday.javautils.data.Strings;
import wearblackallday.javautils.swing.components.LPanel;
import wearblackallday.seedcandy.components.SeedTab;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;

public class StructureTab extends SeedTab {
	public StructureTab() {
		super("StructureSeed");
		BiomePanel biomePanel = new BiomePanel();

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("reverse to nextLong()", () -> this.setOutput(Arrays.stream(this.input.getLongs())
				.mapToObj(StructureSeed::toRandomWorldSeeds)
				.flatMap(List::stream)
				.toList()
			))
			.addButton("crack with biomes", () -> {
				long[] worldSeeds = Arrays.stream(this.input.getLongs())
					.mapToObj(StructureSeed::getWorldSeeds)
					.flatMapToLong(SeedIterator::asStream)
					.toArray();

				this.threadedMap(worldSeeds, seed -> biomePanel.matchesSeed(seed) ? String.valueOf(seed) : "");
			})
			.addButton("verify WorldSeeds", () -> this.setOutput(Arrays.stream(this.input.getLongs())
				.filter(biomePanel::matchesSeed)
				.boxed().toList()
			))
			.addButton("copy Output", () -> Strings.clipboard(this.getOutput()));

		this.addComponents(biomePanel, buttons);
	}
}
