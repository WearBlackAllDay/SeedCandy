package wearblackallday.seedcandy.components;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import wearblackallday.javautils.swing.components.LMenuBar;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.util.Config;
import wearblackallday.seedcandy.util.Factory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GridLayout;
import java.io.File;
import java.util.*;
import static com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.INFOS;
import static java.util.stream.Collectors.partitioningBy;

public class MenuBar extends JMenuBar {

	private final JFileChooser fileChooser = new JFileChooser();

	public MenuBar() {
		this.fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		this.fileChooser.setAcceptAllFileFilterUsed(false);
		this.fileChooser.addActionListener(e -> SeedCandy.get().setOutputFile(this.fileChooser.getSelectedFile()));

		this.createVersionMenu();
		this.createFileMenu();
		this.createThemeMenu();
	}

	private void createVersionMenu() {
		JMenu versionMenu = new JMenu(Config.get().getMcVersion().name);
		Factory.selectionGroup(versionMenu, SeedCandy.SUPPORTED_VERSIONS, Factory::shortVersionName, Config.get().getMcVersion()::equals, version -> {
			Config.get().setMcVersion(version);
			versionMenu.setText(Factory.shortVersionName(version));
		});
		this.add(versionMenu);
	}

	private void createFileMenu() {
		JMenu fileMenu = new JMenu("Output");

		JMenuItem copyButton = new JMenuItem("copy to clipBoard");
		copyButton.addActionListener(e -> ((SeedCandyTab)SeedCandy.get().getContentPane().getSelectedComponent()).copyOutput());
		fileMenu.add(copyButton);

		JCheckBoxMenuItem fileSelection = new JCheckBoxMenuItem("use file (none)");
		fileSelection.addActionListener(e -> {
			if(fileSelection.isSelected()) {
				this.fileChooser.showOpenDialog(SeedCandy.get());
				fileSelection.setSelected(SeedCandy.get().getOutputFile().isPresent());
			} else SeedCandy.get().setOutputFile(null);
			fileSelection.setText("use file (" +
				SeedCandy.get().getOutputFile().map(File::getName).orElse("none") + ')');
		});
		fileMenu.add(fileSelection);

		this.add(fileMenu);
	}

	private void createThemeMenu() {
		JMenu themeMenu = new JMenu("Theme");

		Map<Boolean, List<FlatAllIJThemes.FlatIJLookAndFeelInfo>> themes = Arrays.stream(INFOS)
			.collect(partitioningBy(FlatAllIJThemes.FlatIJLookAndFeelInfo::isDark));

		JMenu darkThemes = new JMenu("dark");
		darkThemes.getPopupMenu().setLayout(new GridLayout(0, 2));
		Factory.selectionGroup(darkThemes, themes.get(true), FlatAllIJThemes.FlatIJLookAndFeelInfo::getName,
			info -> info.getClassName().equals(Config.get().getTheme().className()),
			info -> Config.get().setTheme(info::getClassName));
		themeMenu.add(darkThemes);

		JMenu lightThemes = new JMenu("light");
		Factory.selectionGroup(lightThemes, themes.get(false), FlatAllIJThemes.FlatIJLookAndFeelInfo::getName,
			info -> info.getClassName().equals(Config.get().getTheme().className()),
			info -> Config.get().setTheme(info::getClassName));
		themeMenu.add(lightThemes);

		this.add(themeMenu);
	}
}
