package wearblackallday.seedcandy.components.dungeontab;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.LPanel;
import wearblackallday.javautils.swing.components.SelectionBox;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.components.SeedCandyTab;
import wearblackallday.seedcandy.components.TextBox;
import wearblackallday.seedcandy.util.Dungeon;
import wearblackallday.seedcandy.util.Factory;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class DungeonTab extends JComponent implements SeedCandyTab {
	private final FloorPanel floorPanel = new FloorPanel();
	private final TextBox dungeonOutput = new TextBox(false);

	private final SelectionBox<Dungeon.Size> sizeSelector = new SelectionBox<>(Dungeon.Size.values());
	private final JSpinner xPos = Factory.numberSelector("X");
	private final JSpinner yPos = Factory.numberSelector("Y");
	private final JSpinner zPos = Factory.numberSelector("Z");
	private final SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, getFossilBiomeSelection());
	private final JLabel bitLabel = new JLabel();

	private final JTextField floorString = new JTextField();

	public DungeonTab() {
		this.setName("DungeonCracker");

		this.sizeSelector.addActionListener(e -> this.floorPanel.setFloor(this.sizeSelector.getSelected()));
		this.floorString.setHorizontalAlignment(JTextField.CENTER);

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(SwingUtils.addAll(new Box(BoxLayout.Y_AXIS), this.createUserEntry(), this.floorString), BorderLayout.SOUTH);
		this.add(this.dungeonOutput, BorderLayout.EAST);

		this.updateBits();
	}

	protected void updateBits() {
		this.bitLabel.setText("Bits:\s" + (int)this.floorPanel.getPattern().stream()
		.mapToDouble(fb -> fb.bits)
		.sum());
	}

	private static List<Biome> getFossilBiomeSelection() {
		List<Biome> biomes = new ArrayList<>(Dungeon.FOSSIL_BIOMES);
		biomes.add(0, Factory.namedBiome("other Biome"));
		return biomes;
	}

	private JPanel createUserEntry() {
		return new LPanel()
			.addComponent(this.sizeSelector)
			.addComponent(this.xPos)
			.addComponent(this.yPos)
			.addComponent(this.zPos)
			.addComponent(this.biomeSelector)
			.addButton("from String", () -> this.floorPanel.setPattern(this.floorString.getText()))
			.addButton("to String", () -> this.floorString.setText(this.floorPanel.toString()))
			.addButton("crack", () -> this.setOutput(this.parseDungeon().reverseStructureSeeds()))
			.addComponent(this.bitLabel);
	}

	private Dungeon parseDungeon() {
		return new Dungeon(
			new BPos((Integer)this.xPos.getValue(), (Integer)this.yPos.getValue(), (Integer)this.zPos.getValue()),
			new Dungeon.Floor(this.sizeSelector.getSelected(), this.floorPanel.getPattern()),
			SeedCandy.get().getVersion(),
			this.biomeSelector.getSelected()
		);
	}

	@Override
	public void onVersionChanged(MCVersion newVersion) {
		this.biomeSelector.setEnabled(!newVersion.isOlderThan(MCVersion.v1_15));
	}

	@Override
	public TextBox get() {
		return this.dungeonOutput;
	}
}
