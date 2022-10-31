package wearblackallday.seedcandy.components.dungeontab;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.swing.Events;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.javautils.swing.components.SelectionBox;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.components.SeedCandyTab;
import wearblackallday.seedcandy.components.TextBox;
import wearblackallday.seedcandy.util.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class DungeonTab extends JComponent implements SeedCandyTab {
	private final FloorPanel floorPanel = new FloorPanel();
	private final TextBox dungeonOutput = new TextBox(false);

	private final SelectionBox<Dungeon.Floor.Size> sizeSelector = new SelectionBox<>(Dungeon.Floor.Size.values());
	private final PositionSelector positionSelector = new PositionSelector();
	private final SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, getFossilBiomeSelection());
	private final JLabel bitLabel = new JLabel();

	private final JTextField floorString = new JTextField();

	public DungeonTab() {
		this.setName("DungeonCracker");

		this.sizeSelector.addItemListener(e -> this.floorPanel.setFloorSize(this.sizeSelector.getSelected()));
		this.floorPanel.addMouseListener(Events.Mouse.onClicked(e -> this.displayBits()));
		this.floorString.setHorizontalAlignment(JTextField.CENTER);

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(SwingUtils.addAll(Box.createVerticalBox(), this.createUserEntry(), this.floorString), BorderLayout.SOUTH);
		this.add(this.dungeonOutput, BorderLayout.EAST);

		this.displayBits();
	}

	private void displayBits() {
		this.bitLabel.setText("Bits:\s" + Math.round(this.floorPanel.getSelectedBits()));
	}

	private static List<Biome> getFossilBiomeSelection() {
		List<Biome> biomes = new ArrayList<>(Dungeon.FOSSIL_BIOMES);
		biomes.add(0, Factory.namedBiome("other Biome"));
		return biomes;
	}

	private JPanel createUserEntry() {
		return SwingUtils.addAll(new JPanel(), this.sizeSelector, this.positionSelector, this.biomeSelector,
			Factory.actionButton("from String", () -> this.floorPanel.setPatternFromString(this.floorString.getText())),
			Factory.actionButton("to String", () -> this.floorString.setText(this.floorPanel.toString())),
			Factory.actionButton("crack", () -> this.setOutput(this.getDungeon().reverseStructureSeeds())),
			this.bitLabel
		);
	}

	private Dungeon getDungeon() {
		return new Dungeon(
			this.positionSelector.getPosition(),
			this.floorPanel.getFloor(),
			Config.get().getMcVersion(),
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

	private static class PositionSelector extends JPanel {

		private PositionSelector() {
			for(char c = 'X'; c <= 'Z'; c++) {
				this.add(Factory.numberSelector(Character.toString(c)));
			}
		}

		private BPos getPosition() {
			return new BPos(this.getValue(0), this.getValue(1), this.getValue(2));
		}

		private int getValue(int index) {
			return (Integer)((JSpinner)this.getComponent(index)).getValue();
		}
	}
}
