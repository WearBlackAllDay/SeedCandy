package wearblackallday.components.dungeontab;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.mcutils.version.MCVersion;
import wearblackallday.SeedCandy;
import wearblackallday.components.TextBlock;
import wearblackallday.data.Strings;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.Dungeon;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

		SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, getFossilBiomeSelection());
		SelectionBox<MCVersion> versionSelector = new SelectionBox<>(SeedCandy.SUPPORTED_VERSIONS);

		versionSelector.addActionListener(e ->
			biomeSelector.setEnabled(versionSelector.getSelected().isNewerOrEqualTo(MCVersion.v1_16)));

		JComponent userEntry = new LPanel()
			.addComponent(this.sizeSelector)
			.addTextField("X", "x")
			.addTextField("Y", "y")
			.addTextField("Z", "z")
			.addComponent(versionSelector)
			.addComponent(biomeSelector)
			.addButton("crack", (panel, button, event) -> {
				dungeonOutput.setText("");
				Dungeon.crack(this.dungeonString.getText(), panel.getInt("x"), panel.getInt("y"),
					panel.getInt("z"), versionSelector.getSelected(), biomeSelector.getSelected())
					.forEach(dungeonOutput::addEntry);
				if(dungeonOutput.getText().isEmpty()) dungeonOutput.setText("no results");
			})
			.addButton("copy", () -> Strings.clipboard(dungeonOutput.getText()))
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

	private static List<Biome> getFossilBiomeSelection() {
		List<Biome> biomes = new ArrayList<>(Dungeon.FOSSIL_BIOMES);
		biomes.add(new Biome(MCVersion.v1_0, null, -1, "other Biome", null,
			null, Float.NaN, Float.NaN, Float.NaN, null));
		biomes.sort(Comparator.comparingInt(Biome::getId));
		return biomes;
	}
}
