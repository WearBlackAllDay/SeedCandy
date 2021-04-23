package wearblackallday.gui.components.dungeonpanel;

import wearblackallday.gui.SeedCandy;
import wearblackallday.swing.Events;
import wearblackallday.util.Icons;

import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;

import static javax.swing.SwingUtilities.isRightMouseButton;

public class StringButton extends JToggleButton {

	protected StringButton() {
		this.setPreferredSize(new Dimension(64, 64));
		this.setMinimumSize(new Dimension(48, 48));
		this.setIcon(new ImageIcon(Icons.MOSSY));
		this.setSelectedIcon(new ImageIcon(Icons.COBBLE));
		this.setDisabledIcon(new ImageIcon(Icons.UNKNOWN));
		this.setDisabledSelectedIcon(new ImageIcon(Icons.UNKNOWN));
		this.setFocusable(false);
		this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
		this.addMouseListener(Events.Mouse.onClicked(e -> {
			if(isRightMouseButton(e)) {
				this.setEnabled(!this.isEnabled());
			}
			SeedCandy.DUNGEON_PANEL.updateInfo();
		}));
	}

	protected double getBits() {
		return !this.isEnabled() ? 0D : this.isSelected() ? 2D : 0.415D;
	}

	protected String getString() {
		return !this.isEnabled() ? "2" : this.isSelected() ? "0" : "1";
	}
}
