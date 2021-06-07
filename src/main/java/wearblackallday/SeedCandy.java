package wearblackallday;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import wearblackallday.components.dungeontab.DungeonTab;
import wearblackallday.components.structuretab.StructureTab;
import wearblackallday.components.worldtab.WorldTab;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LFrame;
import wearblackallday.util.Icons;

import javax.swing.*;

public class SeedCandy {
	public static void main(String[] args) {
		FlatOneDarkIJTheme.install();

		new LFrame("SeedCandy", null,
			SwingUtils.addSet(new JTabbedPane(), new DungeonTab(), new StructureTab(), new WorldTab()))
			.center().sizeLock().setIconImage(Icons.SEED);
	}
}
