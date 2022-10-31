package wearblackallday.seedcandy.components;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;
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
		JMenu versionMenu = Factory.selectionMenu(Config.get().getMcVersion().name,
			SeedCandy.SUPPORTED_VERSIONS,
			Factory::shortVersionName,
			Config.get().getMcVersion()::equals,
			(menu, version) -> {
				Config.get().setMcVersion(version);
				menu.setText(Factory.shortVersionName(version));
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

		Map<Boolean, List<FlatIJLookAndFeelInfo>> themes = Arrays.stream(INFOS)
			.collect(partitioningBy(FlatIJLookAndFeelInfo::isDark));

		JMenu darkThemes = Factory.selectionMenu("dark",
			themes.get(true),
			FlatIJLookAndFeelInfo::getName,
			info -> info.getClassName().equals(Config.get().getTheme().className()),
			(menu, info) -> Config.get().setTheme(info::getClassName));
		darkThemes.getPopupMenu().setLayout(new GridLayout(0, 2));
		themeMenu.add(darkThemes);

		JMenu lightThemes = Factory.selectionMenu("light",
			themes.get(false),
			FlatIJLookAndFeelInfo::getName,
			info -> info.getClassName().equals(Config.get().getTheme().className()),
			(menu, info) -> Config.get().setTheme(info::getClassName));
		themeMenu.add(lightThemes);

		this.add(themeMenu);
	}
}
