package wearblackallday.components.structurepanel;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.Dimension;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.SelectionBox;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BiomeUnit extends JPanel {
	private static final List<Biome> BIOMES = Biome.REGISTRY.values().stream()
		.filter(biome -> biome.getDimension() == Dimension.OVERWORLD)
		.sorted(Comparator.comparing(Biome::getName))
		.collect(Collectors.toList());

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
