package wearblackallday.seedcandy.util;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.swing.Events;

import javax.swing.*;
import java.util.Collection;
import java.util.function.*;

public final class Factory {

	private Factory() {}

	public static Biome namedBiome(String name) {
		return new Biome(null, null, -1, name, null,
			null, Float.NaN, Float.NaN, Float.NaN, null, null);
	}

	public static String shortVersionName(MCVersion version) {
		return "1." + version.getRelease();
	}

	public static JButton actionButton(String title, Runnable action) {
		JButton button = new JButton(title);
		button.addMouseListener(Events.Mouse.onPressed(e -> action.run()));
		return button;
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

	public static <T> JMenu selectionMenu(String title, Collection<T> options, Function<T, String> buttonName, Predicate<T> selectCondition, BiConsumer<JMenu, T> onSelected) {
		JMenu parentMenu = new JMenu(title);
		ButtonGroup buttonGroup = new ButtonGroup();
		for(T option : options) {
			JRadioButtonMenuItem button = new JRadioButtonMenuItem(buttonName.apply(option));
			button.addActionListener(e -> onSelected.accept(parentMenu, option));
			parentMenu.add(button);
			buttonGroup.add(button);
			button.setSelected(selectCondition.test(option));
		}
		return parentMenu;
	}
}
