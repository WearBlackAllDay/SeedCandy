package wearblackallday.seedcandy;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.LMenuBar;
import wearblackallday.javautils.util.Filters;
import wearblackallday.seedcandy.components.AbstractTab;
import wearblackallday.seedcandy.components.dungeontab.DungeonTab;
import wearblackallday.seedcandy.components.structuretab.StructureTab;
import wearblackallday.seedcandy.components.worldtab.WorldTab;
import wearblackallday.seedcandy.util.Config;
import wearblackallday.seedcandy.util.Icons;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.GridLayout;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import static com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;
import static com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.INFOS;
import static wearblackallday.seedcandy.util.Config.Theme;

public class SeedCandy extends JFrame {
	private static final MCVersion[] SUPPORTED_VERSIONS = Arrays.stream(MCVersion.values())
		.filter(MCVersion.v1_8::isOlderOrEqualTo)
		.filter(Filters.byInt(MCVersion::getSubVersion, 0))
		.toArray(MCVersion[]::new);

	private static final SeedCandy INSTANCE = new SeedCandy();
	public final DungeonTab dungeonTab = new DungeonTab();

	public Optional<File> outputFile = Optional.empty();
	public MCVersion version;
	public Theme theme;

	public static void main(String[] args) {
		get().theme.load();
		get().setVisible(true);
	}

	private SeedCandy() {
		super("SeedCandy");
		this.version = Config.version();
		this.theme = Config.theme();
		this.dungeonTab.biomeSelector.setEnabled(this.version.isNewerThan(MCVersion.v1_15));

		this.setJMenuBar(this.buildMenuBar());
		this.setContentPane(SwingUtils.addSet(new JTabbedPane(), this.dungeonTab, new StructureTab(), new WorldTab()));
		this.setIconImage(Icons.SEED);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	private JMenuBar buildMenuBar() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addActionListener(e -> this.outputFile = Optional.ofNullable(fileChooser.getSelectedFile())
			.filter(file -> file.getName().endsWith(".txt")));

		return new LMenuBar()
			.addMenu(this.version.name, versionMenu -> {
				ButtonGroup buttonGroup = new ButtonGroup();
				for(MCVersion version : SUPPORTED_VERSIONS) {
					var button = new JRadioButtonMenuItem(version.name);
					button.addActionListener(e -> {
						this.version = version;
						versionMenu.setText(version.name);
						this.dungeonTab.biomeSelector.setEnabled(version.isNewerThan(MCVersion.v1_15));
					});
					buttonGroup.add(button);
					versionMenu.add(button);
					button.setSelected(version == this.version);
				}
			})
			.addMenu("Output", outPutMenu -> outPutMenu
				.withItem("copy to clipBoard", () ->
					((AbstractTab)((JTabbedPane)this.getContentPane()).getSelectedComponent()).copyOutput())
				.withCheckBox("use file (none)", (parentMenu, checkBox, e) -> {
					if(checkBox.isSelected()) {
						fileChooser.showOpenDialog(this);
						checkBox.setSelected(this.outputFile.isPresent());
					} else this.outputFile = Optional.empty();
					checkBox.setText("use file (" +
						this.outputFile.map(File::getName).orElse("none") + ')');
				}))
			.addMenu("Theme", themeMenu -> {
				ButtonGroup themeButtons = new ButtonGroup();
				JMenu darkThemes = new JMenu("dark");
				JMenu lightThemes = new JMenu("light");
				darkThemes.getPopupMenu().setLayout(new GridLayout(0, 2));
				for(FlatIJLookAndFeelInfo info : INFOS) {
					var button = new JRadioButtonMenuItem(info.getName());
					button.addActionListener(e -> {
						this.theme = info::getClassName;
						this.theme.load();
					});
					themeButtons.add(button);
					if(info.isDark()) darkThemes.add(button);
					else lightThemes.add(button);

					button.setSelected(info.getClassName().equals(this.theme.className()));
				}
				themeMenu.add(darkThemes);
				themeMenu.add(lightThemes);
			});
	}

	public static SeedCandy get() {
		return INSTANCE;
	}
}