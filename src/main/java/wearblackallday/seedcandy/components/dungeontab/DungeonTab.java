package wearblackallday.seedcandy.components.dungeontab;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mccore.util.pos.BPos;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.LPanel;
import wearblackallday.javautils.swing.components.SelectionBox;
import wearblackallday.seedcandy.components.AbstractTab;
import wearblackallday.seedcandy.components.TextBox;
import wearblackallday.seedcandy.util.Dungeon;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class DungeonTab extends AbstractTab {
	private final FloorPanel floorPanel = new FloorPanel();
	private final TextBox dungeonOutput = new TextBox(false);
	private final JTextField floorString = new JTextField();
	private final SelectionBox<Dungeon.Size> sizeSelector = new SelectionBox<>(Dungeon.Size.values());
	public final SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, getFossilBiomeSelection());
	private final JLabel bitLabel = new JLabel();
	private final LPanel userEntry = this.buildUserEntry();

	public DungeonTab() {
		this.setName("DungeonCracker");
		this.floorString.setFont(this.floorString.getFont().deriveFont(16F));
		this.floorString.setHorizontalAlignment(JTextField.CENTER);

		this.sizeSelector.addActionListener(e -> this.floorPanel.setFloor(this.sizeSelector.getSelected()));

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), this.userEntry, this.floorString), BorderLayout.SOUTH);
		this.add(this.dungeonOutput, BorderLayout.EAST);
		this.updateBits();
	}

	protected void updateBits() {
		this.bitLabel.setText("Bits: " + (int)this.floorPanel.getPattern().stream()
		.mapToDouble(fb -> fb.bits)
		.sum());
	}

	private static List<Biome> getFossilBiomeSelection() {
		List<Biome> biomes = new ArrayList<>(Dungeon.FOSSIL_BIOMES);
		biomes.add(new Biome(null, null, -1, "other Biome", null,
			null, Float.NaN, Float.NaN, Float.NaN, null, null));
		biomes.sort(Comparator.comparingInt(Biome::getId));
		return biomes;
	}

	private LPanel buildUserEntry() {
		return new LPanel()
			.addComponent(this.sizeSelector)
			.addTextField("X", "x")
			.addTextField("Y", "y")
			.addTextField("Z", "z")
			.addComponent(this.biomeSelector)
			.addButton("from String", () -> {
				String floor = this.floorString.getText();
				if(!floor.matches("[0-2]+") ||
					floor.length() != this.sizeSelector.getSelected().x * this.sizeSelector.getSelected().z)
					return;
				this.floorPanel.fromString(floor);
			})
			.addButton("to String", () -> this.floorString.setText(this.floorPanel.toString()))
			.addButton("crack", () -> {
				List<Long> structureSeeds = this.parseDungeon().reverseStructureSeeds();
				if(structureSeeds.isEmpty()) this.dungeonOutput.setText("no results");
				else this.setOutput(structureSeeds);
			})
			.addComponent(this.bitLabel);
	}

	private Dungeon parseDungeon() {
		return new Dungeon(
			new BPos(this.userEntry.getInt("x"), this.userEntry.getInt("y"), this.userEntry.getInt("z")),
			new Dungeon.Floor(this.sizeSelector.getSelected(), this.floorPanel.getPattern()),
			this.getVersion(),
			this.biomeSelector.getSelected()
		);
	}

	@Override
	public String getOutput() {
		return this.dungeonOutput.getText();
	}

	@Override
	public void setOutputDefault(String output) {
		this.dungeonOutput.setText(output);
	}
}
