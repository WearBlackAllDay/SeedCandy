package wearblackallday.seedcandy;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.components.AbstractTab;
import wearblackallday.seedcandy.components.dungeontab.DungeonTab;
import wearblackallday.seedcandy.components.structuretab.StructureTab;
import wearblackallday.seedcandy.components.worldtab.WorldTab;
import wearblackallday.seedcandy.util.Icons;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LMenuBar;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class SeedCandy extends JFrame {
	private static final MCVersion[] SUPPORTED_VERSIONS = {
		MCVersion.v1_17,
		MCVersion.v1_16,
		MCVersion.v1_15,
		MCVersion.v1_14,
		MCVersion.v1_13,
	};

	static {
		FlatOneDarkIJTheme.setup();
	}

	public final DungeonTab dungeonTab = new DungeonTab();
	private static final SeedCandy INSTANCE = new SeedCandy();
	private final JTabbedPane tabSelection = SwingUtils.addSet(new JTabbedPane(), this.dungeonTab, new StructureTab(), new WorldTab());

	public MCVersion version = SUPPORTED_VERSIONS[0];
	public File outputFile;


	public static void main(String[] args) {
		get().setVisible(true);
	}

	private SeedCandy() {
		super("SeedCandy");

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addActionListener(e -> {
			if(fileChooser.getSelectedFile().getName().endsWith(".txt"))
				this.outputFile = fileChooser.getSelectedFile();
		});

		JMenuBar menu = new LMenuBar()
			.addMenu(this.version.name, versionMenu -> {
				ButtonGroup buttonGroup = new ButtonGroup();
				for(MCVersion version : SUPPORTED_VERSIONS) {
					var button = new JRadioButtonMenuItem(version.name);
					button.addActionListener(e -> {
						this.version = version;
						versionMenu.setText(version.name);
					});
					buttonGroup.add(button);
					versionMenu.add(button);
				}
				buttonGroup.getElements().nextElement().setSelected(true);
			})
			.addMenu("Output", outPutMenu -> outPutMenu
				.withItem("copy to clipBoard" , () ->
					((AbstractTab)this.tabSelection.getSelectedComponent()).copyOutput())
				.withCheckBox("use file", (parentMenu, checkBox, e) -> {
					if(checkBox.isSelected()) {
						fileChooser.showOpenDialog(this);
						checkBox.setSelected(this.outputFile != null);
					} else this.outputFile = null;

					checkBox.setText("use file" + (this.outputFile == null
						? "" : " (" + this.outputFile.getName() + ")"));
				}));

		this.setJMenuBar(menu);
		this.setContentPane(this.tabSelection);
		this.setIconImage(Icons.SEED);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	public static SeedCandy get() {
		return INSTANCE;
	}
}