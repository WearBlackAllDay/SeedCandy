package wearblackallday.seedcandy.components.dungeontab;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mccore.util.pos.BPos;
import wearblackallday.seedcandy.components.AbstractTab;
import wearblackallday.seedcandy.components.TextBox;
import wearblackallday.seedcandy.util.Dungeon;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LPanel;
import wearblackallday.swing.components.SelectionBox;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;

public class DungeonTab extends AbstractTab {
	private final FloorPanel floorPanel = new FloorPanel();
	private final TextBox dungeonOutput = new TextBox(false);
	private final JTextField floorString = new JTextField();
	private final SelectionBox<Dungeon.Size> sizeSelector = new SelectionBox<>(Dungeon.Size.values());
	private final SelectionBox<Biome> biomeSelector = new SelectionBox<>(Biome::getName, getFossilBiomeSelection());
	private final JLabel bitLabel = new JLabel();
	private final LPanel userEntry = this.buildUserEntry();

	public DungeonTab() {
		this.setName("DungeonCracker");
		this.floorString.setFont(this.floorString.getFont().deriveFont(16F));
		this.floorString.setHorizontalAlignment(JTextField.CENTER);
		this.floorString.setEditable(false);
		this.floorString.setFocusable(false);

		Toolkit.getDefaultToolkit().addAWTEventListener(e -> this.updateInfo(), AWTEvent.MOUSE_EVENT_MASK);
		this.sizeSelector.addActionListener(e -> this.floorPanel.setFloor(this.sizeSelector.getSelected()));

		this.setLayout(new BorderLayout());
		this.add(this.floorPanel, BorderLayout.CENTER);
		this.add(SwingUtils.addSet(new Box(BoxLayout.Y_AXIS), this.userEntry, this.floorString), BorderLayout.SOUTH);
		this.add(this.dungeonOutput, BorderLayout.EAST);
		this.updateInfo();
	}

	private void updateInfo() {
		var info = this.floorPanel.getInfo();
		this.bitLabel.setText("Bits: " + info.bits());
		this.floorString.setText(info.floor());
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
			.addButton("crack", (panel, button, event) -> {
				List<Long> structureSeeds = this.parseDungeon().crack();
				if(structureSeeds.isEmpty()) this.dungeonOutput.setText("no results");
				else this.setOutput(structureSeeds);
			})
			.addComponent(this.bitLabel);
	}

	private Dungeon parseDungeon() {
		return new Dungeon(
			new BPos(this.userEntry.getInt("x"), this.userEntry.getInt("y"), this.userEntry.getInt("z")),
			this.sizeSelector.getSelected(),
			this.floorString.getText(),
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
