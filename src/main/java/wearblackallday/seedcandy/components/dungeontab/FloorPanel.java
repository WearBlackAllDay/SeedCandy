package wearblackallday.seedcandy.components.dungeontab;

import wearblackallday.javautils.swing.Events;
import wearblackallday.javautils.swing.components.GridPanel;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.util.Dungeon;
import wearblackallday.seedcandy.util.Icons;

import javax.swing.*;
import java.awt.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import static javax.swing.SwingUtilities.isRightMouseButton;

class FloorPanel extends JComponent {
	protected FloorPanel() {
		this.setLayout(new CardLayout());

		for(Dungeon.Size dungeonSize : Dungeon.Size.values()) {
			this.add(new GridPanel<>(dungeonSize.x, dungeonSize.z, FloorButton::new), dungeonSize.toString());
		}
	}

	protected List<Dungeon.Floor.Block> getPattern() {
		List<Dungeon.Floor.Block> pattern = new ArrayList<>(82);
		this.forEach(button -> pattern.add(button.getBlock()));
		return pattern;
	}

	protected void setPattern(String floor) {
		if(floor.length() != this.getFloor().getCount() || !floor.matches("[0-2]+"))
			return;
		CharacterIterator blocks = new StringCharacterIterator(floor);
		this.forEach(button -> {
			button.setEnabled(blocks.current() != '2');
			button.setSelected(blocks.current() == '0');
			blocks.next();
		});
	}

	private GridPanel<FloorButton> getFloor() {
		for(Component c : this.getComponents()) {
			if(c.isVisible()) return (GridPanel<FloorButton>)c;
		}
		throw new AssertionError();
	}

	protected void setFloor(Dungeon.Size size) {
		((CardLayout)this.getLayout()).show(this, size.toString());
	}

	private void forEach(Consumer<FloorButton> buttonAction) {
		var floor = this.getFloor();
		for(int col = 0; col < floor.getGridWidth(); col++) {
			floor.forEachY(col, buttonAction);
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder(this.getFloor().getCount());
		this.forEach(button -> stringBuilder.append(button.getBlock()));
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
				((DungeonTab)SeedCandy.get().getContentPane().getSelectedComponent()).updateBits();
			}));
		}

		private Dungeon.Floor.Block getBlock() {
			return !this.isEnabled() ? Dungeon.Floor.Block.UNKNOWN : this.isSelected()
				? Dungeon.Floor.Block.COBBLE
				: Dungeon.Floor.Block.MOSSY;
		}
	}
}
