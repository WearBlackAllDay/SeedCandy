package gui.components.dungeonpanel;

import gui.components.TextBlock;
import swing.content.GridPanel;

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
        String dungeonString = "";
        for (int row = 0; row < this.guiPanel.xSize(); row++) {
            for (int column = 0; column < this.guiPanel.ySize(); column++) {
                bits += this.guiPanel.componentAt(row, column).getBits();
                dungeonString = dungeonString.concat(this.guiPanel.componentAt(row, column).getString());
            }
        }
        this.inputPanel.updateBits(bits);
        this.inputPanel.dungeonString.setText(dungeonString);
    }
}
