package wearblackallday.seedcandy.util;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.components.SeedCandyTab;

import javax.swing.*;
import java.awt.Component;
import java.util.prefs.Preferences;

public final class Config {
	private static final Preferences ROOT = Preferences.userRoot().node("seedcandy");
	private static final Config CONFIG = new Config();

	private MCVersion mcVersion = MCVersion.fromString(ROOT.get("mcversion", "1.17"));
	private Theme theme = () -> ROOT.get("theme", "com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme");
	private final int selectedTab = ROOT.getInt("selectedTab", 0);


	private Config() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::save));
	}

	public MCVersion getMcVersion() {
		return this.mcVersion;
	}

	public void setMcVersion(MCVersion mcVersion) {
		this.mcVersion = mcVersion;

		for(Component tab : SeedCandy.get().getContentPane().getComponents()) {
			((SeedCandyTab)tab).onVersionChanged(mcVersion);
		}
	}

	public Theme getTheme() {
		return this.theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
		theme.apply();
		SwingUtilities.updateComponentTreeUI(SeedCandy.get());
	}

	public int getSelectedTab() {
		return this.selectedTab;
	}

	private void save() {
		ROOT.put("mcversion", this.mcVersion.name);
		ROOT.put("theme", this.theme.className());
		ROOT.putInt("selectedTab", SeedCandy.get().getContentPane().getSelectedIndex());
	}

	public static Config get() {
		return CONFIG;
	}

	@FunctionalInterface
	public interface Theme {
		String className();

		default void apply() {
			try {
				UIManager.setLookAndFeel(this.className());
			} catch(ReflectiveOperationException | UnsupportedLookAndFeelException ignored) {
			}
		}
	}
}
