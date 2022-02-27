package wearblackallday.seedcandy.util;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.SeedCandy;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Properties;

public class Config {
	private static final File FILE;
	private static final Properties CONFIG = new Properties();

	static {
		try {
			FILE = new File(Config.class.getResource("/config.properties").toURI());
			load();
		} catch(URISyntaxException | IOException e) {
			throw new RuntimeException();
		}
	}

	public static void load() throws IOException{
		CONFIG.load(new FileInputStream(FILE));
	}

	public static void save() {
		CONFIG.setProperty("version", SeedCandy.get().version.name);
		CONFIG.setProperty("theme", SeedCandy.get().theme.getName());
		try {
			CONFIG.store(new FileOutputStream(FILE), null);
		} catch(IOException ignored) {
		}
	}

	public static MCVersion version() {
		return MCVersion.fromString(CONFIG.getProperty("version"));
	}

	public static Class<?> theme() {
		try {
			return Class.forName(CONFIG.getProperty("theme"));
		} catch(ClassNotFoundException e) {
			throw new RuntimeException();
		}
	}
}
