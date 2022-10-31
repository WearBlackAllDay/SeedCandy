package wearblackallday.seedcandy.components.structuretab;

import com.seedfinding.mccore.rand.seed.StructureSeed;
import com.seedfinding.mccore.util.data.SeedIterator;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.seedcandy.components.SeedTab;
import wearblackallday.seedcandy.util.Factory;

import javax.swing.*;
import java.awt.GridLayout;
import java.util.*;

public class StructureTab extends SeedTab {
	public StructureTab() {
		super("StructureSeed");
		BiomePanel biomePanel = new BiomePanel();

		JPanel buttons = SwingUtils.addAll(new JPanel(new GridLayout(0, 2)),
			Factory.actionButton("reverse to nextLong()", () -> this.flatMap(StructureSeed::toRandomWorldSeeds)),
			Factory.actionButton("crack with biomes", () -> {
				long[] worldSeeds = this.getInput()
					.mapToObj(StructureSeed::getWorldSeeds)
					.flatMapToLong(SeedIterator::asStream)
					.toArray();

				this.mapParallel(worldSeeds, seed -> biomePanel.matchesSeed(seed) ? String.valueOf(seed) : "");
			}),
			Factory.actionButton("verify WorldSeeds", () -> this.setOutput(this.getInput()
				.filter(biomePanel::matchesSeed)
				.boxed().toList()
			)),
			Factory.actionButton("find dupes", () -> {
				List<Long> dupes = new ArrayList<>();
				this.getInput().collect(HashSet::new, (set, seed) -> {
					if(!set.add(seed)) dupes.add(seed);
				}, Set::addAll);
				this.setOutput(dupes);
			})
		);

		this.add(biomePanel);
		this.add(buttons);
	}
}
