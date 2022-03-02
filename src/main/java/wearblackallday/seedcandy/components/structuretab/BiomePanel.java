package wearblackallday.seedcandy.components.structuretab;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.state.Dimension;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.SelectionBox;
import wearblackallday.javautils.util.Filters;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.util.Factory;

import javax.swing.*;
import java.util.Comparator;
import java.util.stream.Stream;

public class BiomePanel extends Box {
	private static final Biome[] BIOMES = Stream.concat(Biomes.REGISTRY.values().stream()
			.filter(Filters.byKeyID(Biome::getDimension, Dimension.OVERWORLD)),
			Stream.of(Factory.namedBiome("any Biome")))
		.sorted(Comparator.comparing(Biome::getName))
		.toArray(Biome[]::new);

	protected BiomePanel() {
		super(BoxLayout.Y_AXIS);

		for(int i = 0; i < 16; i++) {
			this.add(new RestrictionSelector());
		}
	}

	protected boolean matchesSeed(long seed) {
		for(var restriction : this.getComponents()) {
			if(!((RestrictionSelector)restriction).matchesSeed(seed)) {
				return false;
			}
		}
		return true;
	}

	private static String biomeName(Biome biome) {
		return biome.getName()
			.replaceAll("_", "\s")
			.replaceAll("modified", "mod");
	}

	private static class RestrictionSelector extends JPanel {
		private final JSpinner xPos = Factory.numberSelector("X");
		private final JSpinner zPos = Factory.numberSelector("Z");
		private final SelectionBox<Biome> biomeSelector = new SelectionBox<>(BiomePanel::biomeName, BIOMES);

		private RestrictionSelector() {
			SwingUtils.addSet(this, this.xPos, this.zPos, this.biomeSelector);
		}

		private boolean matchesSeed(long seed) {
			if(this.biomeSelector.getSelected().getId() == -1) return true;
			OverworldBiomeSource biomeSource = new OverworldBiomeSource(SeedCandy.get().version, seed);
			return biomeSource.getBiome((Integer)this.xPos.getValue(), 0, (Integer)this.zPos.getValue())
				== this.biomeSelector.getSelected();
		}
	}
}
