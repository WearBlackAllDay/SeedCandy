package gui.components.dungeonpanel;

import gui.components.TextBlock;
import wearblackallday.swing.components.GridPanel;

import javax.swing.*;
import java.awt.*;

public class DungeonPanel extends JPanel {

    private GridPanel<StringButton> guiPanel;
    private final InputPanel inputPanel;
    public final TextBlock dungeonOutput;

    public DungeonPanel() {
        this.guiPanel = new GridPanel<>(9, 9, StringButton::new);
        this.inputPanel = new InputPanel();
        this.dungeonOutput = new TextBlock(false);

        this.setLayout(new BorderLayout());
        this.add(this.guiPanel, BorderLayout.CENTER);
        this.add(this.inputPanel, BorderLayout.SOUTH);
        this.add(this.dungeonOutput, BorderLayout.EAST);
        this.setName("DungeonCracker");
        this.setVisible(true);
        this.updateInfo();
    }

    public void resizeGUI(String size) {
        String[] dim = size.split("x");
        this.guiPanel.removeAll();
        this.guiPanel = new GridPanel<>(Integer.parseInt(dim[0]), Integer.parseInt(dim[1]), StringButton::new);
        this.add(this.guiPanel,BorderLayout.CENTER);
        this.guiPanel.revalidate();
        this.guiPanel.repaint();
        this.updateInfo();
    }

    public void updateInfo() {
        float bits = 0F;
        StringBuilder dungeonString = new StringBuilder();

        for (int column = 0; column < this.guiPanel.getColumns(); column++) {
            for (int row = 0; row < this.guiPanel.getRows(); row++) {
                bits += this.guiPanel.getComponent(row, column).getBits();
                dungeonString.append(this.guiPanel.getComponent(row, column).getString());
            }
        }

        this.inputPanel.updateBits(bits);
        this.inputPanel.dungeonString.setText(dungeonString.toString());
    }

}
