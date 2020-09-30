package gui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import gui.components.dungeonpanel.DungeonPanel;
import gui.components.structurepanel.StructurePanel;
import gui.components.worldpanel.WorldPanel;
import util.Icons;

import javax.swing.*;

public class SeedCandy extends JFrame {

    public static SeedCandy INSTANCE;
    public DungeonPanel dungeonPanel;
    public StructurePanel structurePanel;
    public WorldPanel worldPanel;
    public final JTabbedPane tabbedPane;

    public static void main(String[] args) {

        FlatLightLaf.install();
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        INSTANCE = new SeedCandy();
    }

    public SeedCandy() {
        this.dungeonPanel = new DungeonPanel();
        this.structurePanel = new StructurePanel();
        this.worldPanel = new WorldPanel();
        this.tabbedPane = new JTabbedPane();

        this.tabbedPane.add(this.dungeonPanel);
        this.tabbedPane.add(this.structurePanel);
        this.tabbedPane.add(this.worldPanel);
        this.add(this.tabbedPane);
        this.setVisible(true);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("SeedCandy");
        this.setIconImage(Icons.SEED);
        this.pack();
    }
}
