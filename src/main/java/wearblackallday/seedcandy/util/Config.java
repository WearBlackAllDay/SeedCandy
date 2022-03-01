package wearblackallday.seedcandy.util;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.SeedCandy;

import javax.swing.*;
import java.util.prefs.Preferences;

public class Config {
	private static final Preferences ROOT = Preferences.userRoot().node("seedcandy");

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(Config::save));
	}

	private static void save() {
		ROOT.put("theme", SeedCandy.get().theme.className());
		ROOT.put("mcversion", SeedCandy.get().version.name);
	}

	public static MCVersion version() {
		return MCVersion.fromString(ROOT.get("mcversion", "1.17"));
	}

	public static Theme theme() {
		return () -> ROOT.get("theme", "com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
	}

	@FunctionalInterface
	public interface Theme {
		String className();

		default void load() {
			try {
				Class.forName(this.className()).getDeclaredMethod("setup").invoke(null);
			} catch(ReflectiveOperationException ignored) {
			}
			SwingUtilities.updateComponentTreeUI(SeedCandy.get());
		}
	}
}
