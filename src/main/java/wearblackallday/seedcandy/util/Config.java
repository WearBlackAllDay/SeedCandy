package wearblackallday.seedcandy.util;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.SeedCandy;

import java.util.prefs.Preferences;

public final class Config {
	private static final Preferences ROOT = Preferences.userRoot().node("seedcandy");

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(Config::save));
	}

	private static void save() {
		ROOT.put("mcversion", SeedCandy.get().getVersion().name);
		ROOT.put("theme", SeedCandy.get().getTheme().className());
		ROOT.putInt("selectedTab", SeedCandy.get().getContentPane().getSelectedIndex());
	}

	public static MCVersion version() {
		return MCVersion.fromString(ROOT.get("mcversion", "1.17"));
	}

	public static Theme theme() {
		return () -> ROOT.get("theme", "com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
	}

	public static int selectedTab() {
		return ROOT.getInt("selectedTab", 0);
	}

	@FunctionalInterface
	public interface Theme {
		String className();

		default void apply() {
			try {
				Class.forName(this.className()).getMethod("setup").invoke(null);
			} catch(ReflectiveOperationException ignored) {
			}
		}
	}
}
