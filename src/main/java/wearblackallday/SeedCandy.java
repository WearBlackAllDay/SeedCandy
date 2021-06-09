package wearblackallday;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import kaptainwutax.mcutils.version.MCVersion;
import wearblackallday.components.AbstractTab;
import wearblackallday.components.dungeontab.DungeonTab;
import wearblackallday.components.structuretab.StructureTab;
import wearblackallday.components.worldtab.WorldTab;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LMenuBar;
import wearblackallday.util.Icons;

import javax.swing.*;

public class SeedCandy extends JFrame {
	private static final MCVersion[] SUPPORTED_VERSIONS = {
		MCVersion.v1_17,
		MCVersion.v1_16,
		MCVersion.v1_15,
		MCVersion.v1_14,
		MCVersion.v1_13,
	};

	public static void main(String[] args) {
		FlatOneDarkIJTheme.setup();
		new SeedCandy().setVisible(true);
	}

	public MCVersion version = SUPPORTED_VERSIONS[0];
	private final AbstractTab[] tabs = {new DungeonTab(), new StructureTab(), new WorldTab()};
	private final JTabbedPane tabbedPane = SwingUtils.addSet(new JTabbedPane(), this.tabs);

	public SeedCandy() {
		super("SeedCandy");

		this.setJMenuBar(new LMenuBar()
			.addMenu("version", lMenu -> {
				ButtonGroup buttonGroup = new ButtonGroup();
				for(var version : SUPPORTED_VERSIONS) {
					var button = new JRadioButtonMenuItem(version.name);
					button.addActionListener(e -> this.version = version);
					buttonGroup.add(button);
					lMenu.add(button);
				}
				buttonGroup.getElements().nextElement().setSelected(true);
			}));

		JButton copyButton = new JButton("copy Output");
		copyButton.addActionListener(e ->
			((AbstractTab)this.tabbedPane.getSelectedComponent()).copyOutput());
		this.getJMenuBar().add(copyButton);
		this.setContentPane(this.tabbedPane);
		this.setIconImage(Icons.SEED);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);
	}
}