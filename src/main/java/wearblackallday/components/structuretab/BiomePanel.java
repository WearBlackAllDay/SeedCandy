package wearblackallday.components.structuretab;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.biome.Biomes;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
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
		var biomeSource = new OverworldBiomeSource(MCVersion.v1_16, seed);

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
