package wearblackallday;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import wearblackallday.components.dungeonpanel.DungeonPanel;
import wearblackallday.components.structurepanel.StructurePanel;
import wearblackallday.components.worldpanel.WorldPanel;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LFrame;
import wearblackallday.util.Icons;
import wearblackallday.util.ThreadPool;

import javax.swing.*;

public class SeedCandy {
	public static final ThreadPool POOL = new ThreadPool();

	public static void main(String[] args) {
		FlatOneDarkIJTheme.install();

		new LFrame("SeedCandy", null,
			SwingUtils.addSet(new JTabbedPane(), new DungeonPanel(), new StructurePanel(), new WorldPanel()))
			.center().sizeLock().setIconImage(Icons.SEED);
	}
}
