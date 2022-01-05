package wearblackallday.components.dungeontab;

import wearblackallday.swing.Events;
import wearblackallday.swing.components.GridPanel;
import wearblackallday.util.Dungeon;
import wearblackallday.util.Icons;

import javax.swing.*;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import static javax.swing.SwingUtilities.isRightMouseButton;

public class FloorPanel extends JComponent {
	protected FloorPanel() {
		this.setLayout(new CardLayout());

		for(Dungeon.Size dungeonSize : Dungeon.Size.values()) {
			this.add(new GridPanel<>(dungeonSize.x, dungeonSize.z, FloorButton::new), dungeonSize.toString());
		}
	}

	protected FloorInfo getInfo() {
		float bits = 0f;
		StringBuilder stringBuilder = new StringBuilder();
		var floor = this.getFloor();

		for(int col = 0; col < floor.getGridWidth(); col++) {
			for(int row = 0; row < floor.getGridHeight(); row++) {
				var info = floor.getComponent(col, row).getInfo();
				bits += info.bits;
				stringBuilder.append(info.stringRep);
			}
		}
		return new FloorInfo((int)bits, stringBuilder.toString());
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

	private class FloorButton extends JToggleButton {
		private static final ButtonInfo COBBLE = new ButtonInfo(2f, '0');
		private static final ButtonInfo MOSSY = new ButtonInfo(0.415f, '1');
		private static final ButtonInfo UNKNOWN = new ButtonInfo(0f, '2');

		private FloorButton() {
			this.setPreferredSize(new Dimension(64, 64));
			this.setIcon(Icons.MOSSY);
			this.setSelectedIcon(Icons.COBBLE);
			this.setDisabledIcon(Icons.UNKNOWN);
			this.setDisabledSelectedIcon(Icons.UNKNOWN);
			this.setFocusable(false);
			this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
			this.addMouseListener(Events.Mouse.onClicked(e -> {
				if(isRightMouseButton(e)) {
					this.setEnabled(!this.isEnabled());
				}
				((DungeonTab)FloorPanel.this.getParent()).updateInfo();
			}));
		}

		private ButtonInfo getInfo() {
			return !this.isEnabled() ? UNKNOWN : this.isSelected() ? COBBLE : MOSSY;
		}

		private record ButtonInfo(float bits, char stringRep) {}
	}

	protected record FloorInfo(int bits, String floor) {}
}
