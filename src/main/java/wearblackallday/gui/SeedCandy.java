package wearblackallday.gui;

import com.formdev.flatlaf.FlatDarkLaf;
import wearblackallday.gui.components.dungeonpanel.DungeonPanel;
import wearblackallday.gui.components.structurepanel.StructurePanel;
import wearblackallday.gui.components.worldpanel.WorldPanel;
import wearblackallday.util.Icons;
import wearblackallday.threading.ThreadPool;

import javax.swing.*;

public class SeedCandy extends JFrame {

    public static SeedCandy INSTANCE;
    public static final ThreadPool POOL = new ThreadPool();
    public DungeonPanel dungeonPanel;
    public StructurePanel structurePanel;
    public WorldPanel worldPanel;
    public final JTabbedPane tabbedPane;

    public static void main(String[] args) {

        FlatDarkLaf.install();
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
