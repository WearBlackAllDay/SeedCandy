package wearblackallday.seedcandy.components.dungeontab;

import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.seedcandy.util.Dungeon;
import wearblackallday.seedcandy.util.Icons;
import wearblackallday.swing.Events;
import wearblackallday.swing.components.GridPanel;

import javax.swing.*;
import java.awt.*;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import static javax.swing.SwingUtilities.isRightMouseButton;

public class FloorPanel extends JComponent {
	protected FloorPanel() {
		this.setLayout(new CardLayout());

		for(Dungeon.Size dungeonSize : Dungeon.Size.values()) {
			this.add(new GridPanel<>(dungeonSize.x, dungeonSize.z, FloorButton::new), dungeonSize.toString());
		}
	}

	protected List<Dungeon.FloorBlock> getPattern() {
		List<Dungeon.FloorBlock> pattern = new ArrayList<>(82);
		this.forEach(button -> pattern.add(button.getBlock()));
		return pattern;
	}

	protected void fromString(String floor) {
		CharacterIterator blocks = new StringCharacterIterator(floor);
		this.forEach(button -> {
			button.setEnabled(blocks.current() != '2');
			button.setSelected(blocks.current() == '0');
			blocks.next();
		});
	}

	protected void setFloor(Dungeon.Size size) {
		((CardLayout)this.getLayout()).show(this, size.toString());
	}

	private GridPanel<FloorButton> getFloor() {
		for(Component c : this.getComponents()) {
			if(c.isVisible()) return (GridPanel<FloorButton>)c;
		}
		return null;
	}

	private void forEach(Consumer<FloorButton> buttonAction) {
		var floor = this.getFloor();
		for(int col = 0; col < floor.getGridWidth(); col++) {
			floor.forEachY(col, buttonAction);
		}
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		this.forEach(button -> stringBuilder.append(button.getBlock().toString()));
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
			this.addMouseListener(Events.Mouse.onReleased(e -> {
				if(isRightMouseButton(e)) this.setEnabled(!this.isEnabled());
				SeedCandy.get().dungeonTab.updateBits();
			}));
		}

		private Dungeon.FloorBlock getBlock() {
			return !this.isEnabled() ? Dungeon.FloorBlock.UNKNOWN : this.isSelected()
				? Dungeon.FloorBlock.COBBLE
				: Dungeon.FloorBlock.MOSSY;
		}
	}
}
