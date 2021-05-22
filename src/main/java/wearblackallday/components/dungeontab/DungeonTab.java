package wearblackallday.components.dungeontab;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.seedutils.mc.MCVersion;
import wearblackallday.components.TextBlock;
import wearblackallday.data.Strings;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.Dungeon;

import javax.swing.*;
import java.awt.BorderLayout;

public class DungeonTab extends JComponent {
	private final FloorPanel floorPanel = new FloorPanel();
	private final JTextField dungeonString = new JTextField();
	private final JLabel bitLabel = new JLabel();
	private final SelectionBox<Dungeon.Size> sizeSelector = new SelectionBox<>(Dungeon.Size.values());

	public DungeonTab() {
		this.setName("DungeonCracker");
		this.dungeonString.setFont(this.dungeonString.getFont().deriveFont(16F));
		this.dungeonString.setHorizontalAlignment(JTextField.CENTER);
		TextBlock dungeonOutput = new TextBlock(false);

		this.sizeSelector.addActionListener(e -> {
			this.floorPanel.changeGrid(this.sizeSelector.getSelected());
			this.updateInfo();
		});

		SelectionBox<Biome> biomeSelector =
			new SelectionBox<>(DungeonTab::getBiomeName, Biome.THE_VOID, Biome.DESERT, Biome.SWAMP, Biome.SWAMP_HILLS);
		SelectionBox<MCVersion> versionSelector =
			new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);

		versionSelector.addActionListener(e ->
			biomeSelector.setEnabled(versionSelector.getSelected() == MCVersion.v1_16));

		JComponent userEntry = new LPanel()
			.addComponent(this.sizeSelector)
			.addTextField("X", "x")
			.addTextField("Y", "y")
			.addTextField("Z", "z")
			.addComponent(versionSelector)
			.addComponent(biomeSelector)
			.addButton("crack", (panel, button, event) -> {
				int posX, posY, posZ;
				try {
					posX = panel.getInt("x");
					posY = panel.getInt("y");
					posZ = panel.getInt("z");
				} catch(NumberFormatException exception) {
					return;
				}
				dungeonOutput.setText("");
				Dungeon.crack(this.dungeonString.getText(), posX, posY, posZ,
					versionSelector.getSelected(), biomeSelector.getSelected()).forEach(dungeonOutput::addEntry);
			})
			.addButton("copy", (panel, button, event) ->
				Strings.clipboard(dungeonOutput.getText()))
			.addComponent(this.bitLabel);

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), userEntry, this.dungeonString), BorderLayout.SOUTH);
		this.add(dungeonOutput, BorderLayout.EAST);
		this.updateInfo();
	}

	protected void updateInfo() {
		this.bitLabel.setText("Bits: " + Math.round(this.floorPanel.getBits()));
		this.dungeonString.setText(this.floorPanel.getString());
	}

	private static String getBiomeName(Biome biome) {
		return biome == Biome.THE_VOID ? "other Biome" : biome.getName();
	}
}
