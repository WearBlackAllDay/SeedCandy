package wearblackallday.seedcandy.components.structuretab;

import com.seedfinding.mccore.rand.seed.StructureSeed;
import com.seedfinding.mccore.util.data.SeedIterator;
import wearblackallday.javautils.data.ArrayUtils;
import wearblackallday.javautils.swing.components.LPanel;
import wearblackallday.seedcandy.components.SeedTab;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.*;

public class StructureTab extends SeedTab {
	public StructureTab() {
		super("StructureSeed");
		BiomePanel biomePanel = new BiomePanel();

		JComponent buttons = new LPanel()
			.withLayout(new GridLayout(0, 2))
			.addButton("reverse to nextLong()", () -> this.setOutput(this.input.seeds()
				.mapToObj(StructureSeed::toRandomWorldSeeds)
				.flatMap(List::stream)
				.toList()
			))
			.addButton("crack with biomes", () -> {
				long[] worldSeeds = this.input.seeds()
					.mapToObj(StructureSeed::getWorldSeeds)
					.flatMapToLong(SeedIterator::asStream)
					.toArray();

				this.threadedMap(worldSeeds, seed -> biomePanel.matchesSeed(seed) ? String.valueOf(seed) : "");
			})
			.addButton("verify WorldSeeds", () -> this.setOutput(this.input.seeds()
				.filter(biomePanel::matchesSeed)
				.boxed().toList()
			))
			.addButton("find dupes", () -> this.setOutput(String.join("\n",
				ArrayUtils.getDupes(this.input.getText().split("\n")))));

		this.addComponents(biomePanel, buttons);
	}
}
