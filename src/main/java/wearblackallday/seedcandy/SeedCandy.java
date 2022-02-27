package wearblackallday.seedcandy;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.swing.Events;
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
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;
import static com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.FlatIJLookAndFeelInfo;
import static com.formdev.flatlaf.intellijthemes.FlatAllIJThemes.INFOS;

public class SeedCandy extends JFrame {
	private static final MCVersion[] SUPPORTED_VERSIONS = Arrays.stream(MCVersion.values())
		.filter(MCVersion.v1_8::isOlderOrEqualTo)
		.filter(Filters.byInt(MCVersion::getSubVersion, 0))
		.toArray(MCVersion[]::new);

	public final DungeonTab dungeonTab = new DungeonTab();
	private static final SeedCandy INSTANCE = new SeedCandy();
	private final JTabbedPane tabSelection = SwingUtils.addSet(new JTabbedPane(), this.dungeonTab, new StructureTab(), new WorldTab());

	public Optional<File> outputFile = Optional.empty();
	public MCVersion version;
	public Class<?> theme;

	public static void main(String[] args) {
		get().setVisible(true);
	}

	private SeedCandy() {
		super("SeedCandy");
		this.version = Config.version();
		this.theme = Config.theme();
		this.dungeonTab.biomeSelector.setEnabled(this.version.isNewerThan(MCVersion.v1_15));

		this.setJMenuBar(this.buildMenuBar());
		this.setContentPane(this.tabSelection);
		this.setIconImage(Icons.SEED);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
		this.loadLAF(this.theme);
		this.addWindowListener(Events.Window.onClosing(e -> Config.save()));
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
					if(version == this.version) button.setSelected(true);
				}
			})
			.addMenu("Output", outPutMenu -> outPutMenu
				.withItem("copy to clipBoard", () ->
					((AbstractTab)this.tabSelection.getSelectedComponent()).copyOutput())
				.withCheckBox("use file (none)", (parentMenu, checkBox, e) -> {
					if(checkBox.isSelected()) {
						fileChooser.showOpenDialog(this);
						checkBox.setSelected(this.outputFile.isPresent());
					} else this.outputFile = Optional.empty();
					checkBox.setText("use file" + "\s(" +
						this.outputFile.map(File::getName).orElse("none") + ")");
				}))
			.addMenu("Theme", themeMenu -> {
				ButtonGroup themeButtons = new ButtonGroup();
				JMenu darkThemes = new JMenu("dark");
				JMenu lightThemes = new JMenu("light");
				for(FlatIJLookAndFeelInfo info : INFOS) {
					var button = new JRadioButtonMenuItem(info.getName());
					button.addActionListener(e -> {
						try {
							this.loadLAF(Class.forName(info.getClassName()));
						} catch(ClassNotFoundException classNotFoundException) {
							classNotFoundException.printStackTrace();
						}
					});
					button.setFont(button.getFont().deriveFont(10F));
					themeButtons.add(button);
					if(info.isDark()) darkThemes.add(button);
					else lightThemes.add(button);

					if(info.getClassName().equals(this.theme.getName())) button.setSelected(true);
				}
				themeMenu.add(darkThemes);
				themeMenu.add(lightThemes);
			});
	}

	private void loadLAF(Class<?> theme) {
		try {
			this.theme = theme;
			theme.getDeclaredMethod("setup", null).invoke(null);
		} catch(IllegalAccessException | InvocationTargetException | NoSuchMethodException ignored) {
		}
		SwingUtilities.updateComponentTreeUI(this);
	}

	public static SeedCandy get() {
		return INSTANCE;
	}
}