package wearblackallday.seedcandy.components.structuretab;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.state.Dimension;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.Filters;

import javax.swing.*;
import java.util.Comparator;

public class BiomePanel extends Box {
	private static final Biome[] BIOMES = Biomes.REGISTRY.values().stream()
		.filter(Filters.byKeyID(Biome::getDimension, Dimension.OVERWORLD))
		.sorted(Comparator.comparing(Biome::getName))
		.toArray(Biome[]::new);

	protected BiomePanel() {
		super(BoxLayout.Y_AXIS);

		for(int i = 0; i < 16; i++) {
			this.add(new RestrictionSelector());
		}
	}

	protected boolean matchesSeed(long seed) {
		var biomeSource = new OverworldBiomeSource(SeedCandy.get().version, seed);

		for(var restriction : this.getComponents()) {
			if(!((RestrictionSelector)restriction).matchesSource(biomeSource)) {
				return false;
			}
		}
		return true;
	}

	private static class RestrictionSelector extends JPanel {
		private final JTextField xCord = new JTextField();
		private final JTextField zCord = new JTextField();
		private final SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, BIOMES);

		private RestrictionSelector() {
			SwingUtils.setPrompt(this.xCord, "X");
			SwingUtils.setPrompt(this.zCord, "Z");
			SwingUtils.addSet(this, this.xCord, this.zCord, this.biomeSelector);
		}

		private boolean matchesSource(OverworldBiomeSource biomeSource) {
			try {
				int xPos = Integer.parseInt(this.xCord.getText().trim());
				int zPos = Integer.parseInt(this.zCord.getText().trim());
				return biomeSource.getBiome(xPos, 0, zPos)
					== this.biomeSelector.getSelected();
			} catch(NumberFormatException exception) {
				return true;
			}
		}
	}
}
