package wearblackallday.seedcandy.components.structuretab;

import com.seedfinding.mccore.rand.seed.StructureSeed;
import com.seedfinding.mccore.util.data.SeedIterator;
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
			.addButton("reverse to nextLong()", () -> this.setOutput(this.getInput()
				.mapToObj(StructureSeed::toRandomWorldSeeds)
				.flatMap(List::stream)
				.toList()
			))
			.addButton("crack with biomes", () -> {
				long[] worldSeeds = this.getInput()
					.mapToObj(StructureSeed::getWorldSeeds)
					.flatMapToLong(SeedIterator::asStream)
					.toArray();

				this.threadedMap(worldSeeds, seed -> biomePanel.matchesSeed(seed) ? String.valueOf(seed) : "");
			})
			.addButton("verify WorldSeeds", () -> this.setOutput(this.getInput()
				.filter(biomePanel::matchesSeed)
				.boxed().toList()
			))
			.addButton("find dupes", () -> {
				List<Long> dupes = new ArrayList<>();
				this.getInput().collect(HashSet::new, (set, seed) -> {
					if(!set.add(seed)) dupes.add(seed);
				}, Set::addAll);
				this.setOutput(dupes);
			});

		this.add(biomePanel);
		this.add(buttons);
	}
}
