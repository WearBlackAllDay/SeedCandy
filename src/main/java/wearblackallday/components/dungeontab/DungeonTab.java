package wearblackallday.components.dungeontab;

import kaptainwutax.biomeutils.biome.Biome;
import wearblackallday.components.AbstractTab;
import wearblackallday.components.TextBlock;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;
import wearblackallday.util.Dungeon;

import javax.swing.*;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class DungeonTab extends AbstractTab {
	private final FloorPanel floorPanel = new FloorPanel();
	private final JTextField dungeonString = new JTextField();
	private final JLabel bitLabel = new JLabel();
	private final SelectionBox<Dungeon.Size> sizeSelector = new SelectionBox<>(Dungeon.Size.values());
	private final TextBlock dungeonOutput = new TextBlock(false);

	public DungeonTab() {
		this.setName("DungeonCracker");
		this.dungeonString.setFont(this.dungeonString.getFont().deriveFont(16F));
		this.dungeonString.setHorizontalAlignment(JTextField.CENTER);

		this.sizeSelector.addActionListener(e -> {
			this.floorPanel.changeGrid(this.sizeSelector.getSelected());
			this.updateInfo();
		});

		SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, getFossilBiomeSelection());

		JComponent userEntry = new LPanel()
			.addComponent(this.sizeSelector)
			.addTextField("X", "x")
			.addTextField("Y", "y")
			.addTextField("Z", "z")
			.addComponent(biomeSelector)
			.addButton("crack", (panel, button, event) -> {
				this.dungeonOutput.setText("");
				Dungeon.crack(this.dungeonString.getText(), panel.getInt("x"), panel.getInt("y"),
					panel.getInt("z"), this.getVersion(), biomeSelector.getSelected())
					.forEach(this.dungeonOutput::addEntry);
				if(this.getOutput().isEmpty()) this.dungeonOutput.setText("no results");
			})
			.addComponent(this.bitLabel);

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), userEntry, this.dungeonString), BorderLayout.SOUTH);
		this.add(this.dungeonOutput, BorderLayout.EAST);
		this.updateInfo();
	}

	protected void updateInfo() {
		this.bitLabel.setText("Bits: " + Math.round(this.floorPanel.getBits()));
		this.dungeonString.setText(this.floorPanel.getString());
	}

	private static List<Biome> getFossilBiomeSelection() {
		List<Biome> biomes = new ArrayList<>(Dungeon.FOSSIL_BIOMES);
		biomes.add(new Biome(null, null, -1, "other Biome", null,
			null, Float.NaN, Float.NaN, Float.NaN, null));
		biomes.sort(Comparator.comparingInt(Biome::getId));
		return biomes;
	}

	@Override
	public String getOutput() {
		return this.dungeonOutput.getText();
	}
}
