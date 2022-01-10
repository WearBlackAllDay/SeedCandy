package wearblackallday.seedcandy;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.components.AbstractTab;
import wearblackallday.seedcandy.components.dungeontab.DungeonTab;
import wearblackallday.seedcandy.components.structuretab.StructureTab;
import wearblackallday.seedcandy.components.worldtab.WorldTab;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LMenuBar;
import wearblackallday.seedcandy.util.Icons;

import javax.swing.*;

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

	private static final SeedCandy INSTANCE = new SeedCandy();

	public MCVersion version = SUPPORTED_VERSIONS[0];
	private final AbstractTab[] tabs = {new DungeonTab(), new StructureTab(), new WorldTab()};
	private final JTabbedPane tabSelection = SwingUtils.addSet(new JTabbedPane(), this.tabs);

	public static void main(String[] args) {
		get().setVisible(true);
	}

	public SeedCandy() {
		super("SeedCandy");

		JMenuBar menu = new LMenuBar()
			.addMenu(this.version.name, lMenu -> {
				ButtonGroup buttonGroup = new ButtonGroup();
				for(var version : SUPPORTED_VERSIONS) {
					var button = new JRadioButtonMenuItem(version.name);
					button.addActionListener(e -> {
						this.version = version;
						lMenu.setText(version.name);
					});
					buttonGroup.add(button);
					lMenu.add(button);
				}
				buttonGroup.getElements().nextElement().setSelected(true);
			});

		JButton copyButton = new JButton("copy Output");
		copyButton.addActionListener(e ->
			((AbstractTab)this.tabSelection.getSelectedComponent()).copyOutput());
		menu.add(copyButton);

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