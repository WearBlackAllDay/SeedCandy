package wearblackallday;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import kaptainwutax.mcutils.version.MCVersion;
import wearblackallday.components.dungeontab.DungeonTab;
import wearblackallday.components.structuretab.StructureTab;
import wearblackallday.components.worldtab.WorldTab;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.LFrame;
import wearblackallday.util.Icons;

import javax.swing.*;

public class SeedCandy {
	public static final MCVersion[] SUPPORTED_VERSIONS = {
		MCVersion.v1_17,
		MCVersion.v1_16,
		MCVersion.v1_15,
		MCVersion.v1_14,
		MCVersion.v1_13,
	};

	public static void main(String[] args) {
		FlatOneDarkIJTheme.setup();

		new LFrame("SeedCandy", null,
			SwingUtils.addSet(new JTabbedPane(), new DungeonTab(), new StructureTab(), new WorldTab()))
			.center().sizeLock().setIconImage(Icons.SEED);
	}
}
