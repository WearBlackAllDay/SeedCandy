package wearblackallday.seedcandy;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.LMenuBar;
import wearblackallday.javautils.util.Filters;
import wearblackallday.seedcandy.components.SeedCandyTab;
import wearblackallday.seedcandy.components.dungeontab.DungeonTab;
import wearblackallday.seedcandy.components.structuretab.StructureTab;
import wearblackallday.seedcandy.components.worldtab.WorldTab;
import wearblackallday.seedcandy.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GridLayout;
import java.awt.Taskbar;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import static com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;
import static wearblackallday.seedcandy.util.Config.Theme;

public class SeedCandy extends JFrame {
	private static final List<MCVersion> SUPPORTED_VERSIONS = Arrays.stream(MCVersion.values())
		.filter(MCVersion.v1_8::isOlderOrEqualTo)
		.filter(Filters.byInt(MCVersion::getSubVersion, 0))
		.toList();

	static {
		// needs to be called prior to any Swing-components for darkMode to work on osx
		// https://bugs.openjdk.java.net/browse/JDK-8235363
		if(System.getProperty("os.name").toLowerCase().contains("mac"))
			System.setProperty("apple.awt.application.appearance", "system");

		if(Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE))
			Taskbar.getTaskbar().setIconImage(Icons.SEED);
	}

	private static final SeedCandy INSTANCE = new SeedCandy();

	private MCVersion version = Config.version();
	private Theme theme = Config.theme();
	private File outputFile;

	public static void main(String[] args) {
		get().setVisible(true);
	}

	private SeedCandy() {
		super("SeedCandy");

		this.setJMenuBar(this.buildMenuBar());
		this.setContentPane(SwingUtils.addSet(new JTabbedPane(), new DungeonTab(), new StructureTab(), new WorldTab()));
		this.setIconImage(Icons.SEED);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);

		this.setVersion(this.version);
		this.setTheme(this.theme);
	}

	private JMenuBar buildMenuBar() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addActionListener(e -> this.setOutputFile(fileChooser.getSelectedFile()));

		Map<Boolean, List<FlatIJLookAndFeelInfo>> themes = Arrays.stream(FlatAllIJThemes.INFOS)
			.collect(Collectors.partitioningBy(FlatIJLookAndFeelInfo::isDark));

		return new LMenuBar()
			.addMenu(this.version.name, versionMenu ->
				Factory.addSelection(versionMenu, SUPPORTED_VERSIONS, MCVersion::toString, this.version::equals, version -> {
					this.setVersion(version);
					versionMenu.setText(version.name);
			}))
			.addMenu("Output", outPutMenu -> outPutMenu
				.withItem("copy to clipBoard", () ->
					((SeedCandyTab)((JTabbedPane)this.getContentPane()).getSelectedComponent()).copyOutput())
				.withCheckBox("use file (none)", (parentMenu, checkBox, e) -> {
					if(checkBox.isSelected()) {
						fileChooser.showOpenDialog(this);
						checkBox.setSelected(this.getOutputFile().isPresent());
					} else this.setOutputFile(null);
					checkBox.setText("use file (" +
						this.getOutputFile().map(File::getName).orElse("none") + ')');
				}))
			.addMenu("Theme", themeMenu -> themeMenu
					.subMenu("dark", darkThemes -> {
						darkThemes.getPopupMenu().setLayout(new GridLayout(0, 2));
						Factory.addSelection(darkThemes, themes.get(true), FlatIJLookAndFeelInfo::getName,
							info -> info.getClassName().equals(this.getTheme().className()),
							info -> this.setTheme(info::getClassName));
					})
					.subMenu("light", lightThemes -> Factory.addSelection(lightThemes, themes.get(false), FlatIJLookAndFeelInfo::getName,
						info -> info.getClassName().equals(this.getTheme().className()),
						info -> this.setTheme(info::getClassName)))
			);
	}

	public MCVersion getVersion() {
		return this.version;
	}

	private void setVersion(MCVersion version) {
		this.version = version;
		((SeedCandyTab)((JTabbedPane)this.getContentPane()).getSelectedComponent()).onVersionChanged(version);
	}

	public Theme getTheme() {
		return this.theme;
	}

	private void setTheme(Theme theme) {
		this.theme = theme;
		theme.apply();
		SwingUtilities.updateComponentTreeUI(this);
	}

	public Optional<File> getOutputFile() {
		return Optional.ofNullable(this.outputFile).filter(file -> file.getName().endsWith(".txt"));
	}

	private void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public static SeedCandy get() {
		return INSTANCE;
	}
}