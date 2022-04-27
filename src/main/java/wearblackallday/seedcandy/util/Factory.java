package wearblackallday.seedcandy.util;

import com.seedfinding.mcbiome.biome.Biome;

import javax.swing.*;
import java.util.List;
import java.util.function.*;

public class Factory {

	public static Biome namedBiome(String name) {
		return new Biome(null, null, -1, name, null,
			null, Float.NaN, Float.NaN, Float.NaN, null, null);
	}

	public static JSpinner numberSelector(String tooltip) {
		JSpinner spinner = new JSpinner(new SpinnerNumberModel());
		spinner.setToolTipText(tooltip);
		spinner.addMouseWheelListener(e -> {
			int value = (e.isShiftDown() ? 10 : 1) * (e.isControlDown() ? 100 : 1);
			spinner.setValue(((Integer)spinner.getValue() + e.getWheelRotation() * value));
		});
		return spinner;
	}

	public static <T> void addSelection(JMenu menu, List<T> options, Function<T, String> buttonName, Predicate<T> selectCondition, Consumer<T> onSelected) {
		ButtonGroup buttonGroup = new ButtonGroup();
		for(T option : options) {
			JRadioButtonMenuItem button = new JRadioButtonMenuItem(buttonName.apply(option));
			button.addActionListener(e -> onSelected.accept(option));
			menu.add(button);
			buttonGroup.add(button);
			button.setSelected(selectCondition.test(option));
		}
	}
}
