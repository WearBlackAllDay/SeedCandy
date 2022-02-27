package wearblackallday.seedcandy.util;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.SeedCandy;

import java.util.prefs.Preferences;

public class Config {
	private static final Preferences ROOT = Preferences.userRoot().node("seedcandy");

	public static void save() {
		ROOT.put("theme", SeedCandy.get().theme.getName());
		ROOT.put("mcversion", SeedCandy.get().version.name);
	}

	public static MCVersion version() {
		return MCVersion.fromString(ROOT.get("mcversion", "1.17"));
	}

	public static Class<?> theme() {
		try {
			return Class.forName(ROOT.get("theme", "com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme"));
		} catch(ClassNotFoundException e) {
			throw new RuntimeException();
		}
	}
}
