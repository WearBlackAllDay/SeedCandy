package wearblackallday.seedcandy.components;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.util.Config;
import wearblackallday.seedcandy.util.Factory;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GridLayout;
import java.util.*;
import static com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.INFOS;
import static java.util.stream.Collectors.partitioningBy;

public class MenuBar extends JMenuBar {

	private final JFileChooser fileChooser = new JFileChooser();

	public MenuBar() {
		this.fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		this.fileChooser.setAcceptAllFileFilterUsed(false);

		this.createVersionMenu();
		this.createFileMenu();
		this.createThemeMenu();
	}

	private void createVersionMenu() {
		JMenu versionMenu = Factory.selectionMenu(Config.get().getMcVersion().name,
			new ButtonGroup(),
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

		String defaultName = "output to file...";
		JCheckBoxMenuItem fileSelection = new JCheckBoxMenuItem(defaultName);
		fileSelection.addActionListener(e -> {
			int selectedOption = this.fileChooser.showOpenDialog(SeedCandy.get());
			boolean fileSelected = selectedOption == JFileChooser.APPROVE_OPTION;

			SeedCandy.get().setOutputFile(fileSelected ? this.fileChooser.getSelectedFile() : null);
			fileSelection.setSelected(fileSelected);
			fileSelection.setText(fileSelected ? this.fileChooser.getSelectedFile().getName() : defaultName);
		});
		fileMenu.add(fileSelection);

		this.add(fileMenu);
	}

	private void createThemeMenu() {
		JMenu themeMenu = new JMenu("Theme");

		ButtonGroup themeGroup = new ButtonGroup();
		Map<Boolean, List<FlatIJLookAndFeelInfo>> themes = Arrays.stream(INFOS)
			.collect(partitioningBy(FlatIJLookAndFeelInfo::isDark));

		JMenu darkThemes = Factory.selectionMenu("dark",
			themeGroup,
			themes.get(true),
			FlatIJLookAndFeelInfo::getName,
			info -> info.getClassName().equals(Config.get().getTheme().className()),
			(menu, info) -> Config.get().setTheme(info::getClassName));
		darkThemes.getPopupMenu().setLayout(new GridLayout(0, 2));
		themeMenu.add(darkThemes);

		JMenu lightThemes = Factory.selectionMenu("light",
			themeGroup,
			themes.get(false),
			FlatIJLookAndFeelInfo::getName,
			info -> info.getClassName().equals(Config.get().getTheme().className()),
			(menu, info) -> Config.get().setTheme(info::getClassName));
		lightThemes.setToolTipText("You´re not using one of these are you?");
		themeMenu.add(lightThemes);

		this.add(themeMenu);
	}
}
