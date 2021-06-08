package wearblackallday.components.structuretab;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.biome.Biomes;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.mcutils.state.Dimension;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.Filters;

import javax.swing.*;
import java.util.Comparator;

public class BiomeUnit extends JPanel {
	private static final Biome[] BIOMES = Biomes.REGISTRY.values().stream()
		.filter(Filters.byKeyID(Biome::getDimension, Dimension.OVERWORLD))
		.sorted(Comparator.comparing(Biome::getName))
		.toArray(Biome[]::new);

	private final JTextField xCord = new JTextField();
	private final JTextField zCord = new JTextField();
	private final SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, BIOMES);

	protected BiomeUnit() {
		SwingUtils.setPrompt(this.xCord, "X");
		SwingUtils.setPrompt(this.zCord, "Z");
		SwingUtils.addSet(this, this.xCord, this.zCord, this.biomeSelector);
	}

	protected boolean matches(OverworldBiomeSource biomeSource) {
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
