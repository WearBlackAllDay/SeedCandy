package gui.components.dungeonpanel;

import gui.SeedCandy;
import util.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static javax.swing.SwingUtilities.isRightMouseButton;

public class StringButton extends JToggleButton {

    public StringButton() {
        this.setPreferredSize(new Dimension(64, 64));
        this.setMinimumSize(new Dimension(48, 48));
        this.setIcon(new ImageIcon(Icons.MOSSY));
        this.setSelectedIcon(new ImageIcon(Icons.COBBLE));
        this.setDisabledIcon(new ImageIcon(Icons.UNKNOWN));
        this.setDisabledSelectedIcon(new ImageIcon(Icons.UNKNOWN));
        this.setFocusable(false);
        this.setFocusPainted(false);
        this.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
        this.addActionListener(e -> SeedCandy.INSTANCE.dungeonPanel.updateInfo());
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isRightMouseButton(e)) {
                    StringButton.this.setEnabled(!StringButton.this.isEnabled());
                    SeedCandy.INSTANCE.dungeonPanel.updateInfo();
                }
            }
        });
    }
    public float getBits() {
        return !this.isEnabled() ? 0F : this.isSelected() ? 2F : 0.415F;
    }

    public String getString() {
        return !this.isEnabled() ? "2" : this.isSelected() ? "0" : "1";
    }
}
