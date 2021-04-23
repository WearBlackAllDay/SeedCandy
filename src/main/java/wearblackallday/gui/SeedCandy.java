package wearblackallday.gui;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import wearblackallday.gui.components.dungeonpanel.DungeonPanel;
import wearblackallday.gui.components.structurepanel.StructurePanel;
import wearblackallday.gui.components.worldpanel.WorldPanel;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.builder.FrameBuilder;
import wearblackallday.threading.ThreadPool;
import wearblackallday.util.Icons;

import javax.swing.*;

public class SeedCandy {

    static {
        FlatOneDarkIJTheme.install();
    }

	public static final ThreadPool POOL = new ThreadPool();
	public static final DungeonPanel DUNGEON_PANEL = new DungeonPanel();

	public static void main(String[] args) {
		JTabbedPane tabbedPane = new JTabbedPane();
		SwingUtils.addSet(tabbedPane, DUNGEON_PANEL, new StructurePanel(), new WorldPanel());

		new FrameBuilder().visible().centered().title("SeedCandy")
			.contentPane(tabbedPane).create().setIconImage(Icons.SEED);
	}
}
