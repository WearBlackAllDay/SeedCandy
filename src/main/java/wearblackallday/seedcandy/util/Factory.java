package wearblackallday.seedcandy.util;

import com.seedfinding.mcbiome.biome.Biome;

import javax.swing.*;

public class Factory {

	public static Biome namedBiome(String name) {
		return new Biome(null, null, -1, name, null,
			null, Float.NaN, Float.NaN, Float.NaN, null, null);
	}

	public static JSpinner numberSelector(String tooltip) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel());
		spinner.setToolTipText(tooltip);
		return spinner;
	}
}
