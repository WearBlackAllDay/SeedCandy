package wearblackallday.seedcandy.components.dungeontab;

import wearblackallday.javautils.swing.Events;
import wearblackallday.javautils.swing.components.GridBagPanel;
import wearblackallday.javautils.swing.components.GridPanel;
import wearblackallday.seedcandy.util.Dungeon;
import wearblackallday.seedcandy.util.Icons;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.function.Consumer;
import static javax.swing.SwingUtilities.isRightMouseButton;

class FloorPanel extends GridBagPanel {

	private final Map<Dungeon.Floor.Size, GridPanel<FloorButton>> floorLayouts = new EnumMap<>(Dungeon.Floor.Size.class);

	private Dungeon.Floor.Size floorSize;

	protected FloorPanel() {
		for(Dungeon.Floor.Size dungeonSize : Dungeon.Floor.Size.values()) {
			this.floorLayouts.put(dungeonSize, new GridPanel<>(dungeonSize.x, dungeonSize.z, FloorButton::new));
		}
		this.setFloorSize(Dungeon.Floor.Size.values()[0]);
	}

	protected Dungeon.Floor getFloor() {
		return new Dungeon.Floor(this.floorSize, this.getPattern());
	}

	protected double getSelectedBits() {
		return this.floorLayouts.get(this.floorSize).stream()
			.mapToDouble(floorButton -> floorButton.getBlock().bits)
			.sum();
	}

	private List<Dungeon.Floor.Block> getPattern() {
		List<Dungeon.Floor.Block> pattern = new ArrayList<>(this.floorSize.blockCount());
		this.forEachOrdered(button -> pattern.add(button.getBlock()));
		return pattern;
	}

	protected void setPatternFromString(String pattern) {
		if(pattern.length() != this.floorSize.blockCount() || !pattern.matches("[0-2]+"))
			return;
		CharacterIterator blocks = new StringCharacterIterator(pattern);
		this.forEachOrdered(button -> {
			button.setEnabled(blocks.current() != '2');
			button.setSelected(blocks.current() == '0');
			blocks.next();
		});
	}

	protected void setFloorSize(Dungeon.Floor.Size size) {
		this.floorSize = size;
		this.removeAll();
		this.add(this.floorLayouts.get(size));
		this.revalidate();
		this.repaint();
	}

	private void forEachOrdered(Consumer<FloorButton> buttonAction) {
		var selectedFloor = this.floorLayouts.get(this.floorSize);
		for(int col = 0; col < selectedFloor.getGridWidth(); col++) {
			selectedFloor.forEachY(col, buttonAction);
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.floorSize.blockCount());
		this.forEachOrdered(button -> stringBuilder.append(button.getBlock()));
		return stringBuilder.toString();
	}

	private static class FloorButton extends JToggleButton {
		private FloorButton() {
			this.setPreferredSize(new Dimension(64, 64));
			this.setIcon(Icons.MOSSY);
			this.setSelectedIcon(Icons.COBBLE);
			this.setDisabledIcon(Icons.UNKNOWN);
			this.setDisabledSelectedIcon(Icons.UNKNOWN);
			this.setFocusable(false);
			this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
			this.addMouseListener(Events.Mouse.onClicked(e -> {
				if(isRightMouseButton(e)) this.setEnabled(!this.isEnabled());
				this.getParent().getParent().dispatchEvent(e);
			}));
		}

		private Dungeon.Floor.Block getBlock() {
			return !this.isEnabled() ? Dungeon.Floor.Block.UNKNOWN : this.isSelected()
				? Dungeon.Floor.Block.COBBLE
				: Dungeon.Floor.Block.MOSSY;
		}
	}
}
