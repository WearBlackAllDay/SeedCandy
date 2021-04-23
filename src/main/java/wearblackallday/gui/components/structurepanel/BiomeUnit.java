package wearblackallday.gui.components.structurepanel;

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

	private static final List<Biome> BIOMES;

	static {
		BIOMES = Biome.REGISTRY.values().stream()
			.filter(biome -> biome.getDimension() == Dimension.OVERWORLD)
			.sorted(Comparator.comparing(Biome::getName))
			.collect(Collectors.toList());
	}

	private final JTextField xCord;
	private final JTextField zCord;
	private final SelectionBox<Biome> biomeSelector;

	protected BiomeUnit() {
		this.xCord = new JTextField();
		this.zCord = new JTextField();
		this.biomeSelector = new SelectionBox<>(Biome::getName, BIOMES);
		SwingUtils.setPrompt(this.xCord, "X");
		SwingUtils.setPrompt(this.zCord, "Z");
		SwingUtils.addSet(this, this.xCord, this.zCord, this.biomeSelector);
	}

	protected boolean matches(OverworldBiomeSource biomeSource) {
		try {
			return biomeSource.getBiome(Integer.parseInt(this.xCord.getText().trim()),
				0,
				Integer.parseInt(this.zCord.getText().trim()))
				== this.biomeSelector.getSelected();
		} catch(NumberFormatException exception) {
			return true;
		}
	}
}
