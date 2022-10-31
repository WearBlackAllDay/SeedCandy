package wearblackallday.seedcandy;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.swing.SwingUtils;
import wearblackallday.seedcandy.components.MenuBar;
import wearblackallday.seedcandy.components.dungeontab.DungeonTab;
import wearblackallday.seedcandy.components.structuretab.StructureTab;
import wearblackallday.seedcandy.components.worldtab.WorldTab;
import wearblackallday.seedcandy.util.Config;
import wearblackallday.seedcandy.util.Icons;

import javax.swing.*;
import java.awt.Image;
import java.awt.Taskbar;
import java.io.File;
import java.util.*;
import static com.seedfinding.mccore.version.MCVersion.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

public class SeedCandy extends JFrame {
	public static final Collection<MCVersion> SUPPORTED_VERSIONS = Arrays.stream(values(), v1_17.ordinal(), v1_0.ordinal() + 1)
		.collect(groupingBy(MCVersion::getRelease, LinkedHashMap::new, reducing(v1_0, (v0, v1) -> v1)))
		.values();

	static {
		// needs to be called prior to any Swing-components for darkMode to work on osx
		// https://bugs.openjdk.java.net/browse/JDK-8235363
		if(System.getProperty("os.name").startsWith("Mac OS"))
			System.setProperty("apple.awt.application.appearance", "system");

		Config.get().getTheme().apply();
	}

	private static final SeedCandy INSTANCE = new SeedCandy();

	private transient File outputFile;

	public static void main(String[] args) {
		INSTANCE.setVisible(true);
	}

	private SeedCandy() {
		super("SeedCandy");

		this.setJMenuBar(new MenuBar());
		this.setContentPane(SwingUtils.addAll(new JTabbedPane(), new DungeonTab(), new StructureTab(), new WorldTab()));

		this.setIconImage(Icons.SEED);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setResizable(false);
		this.pack();
		this.setLocationRelativeTo(null);

		this.getContentPane().setSelectedIndex(Config.get().getSelectedTab());
	}

	public Optional<File> getOutputFile() {
		return Optional.ofNullable(this.outputFile).filter(file -> file.getName().endsWith(".txt"));
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	@Override
	public JTabbedPane getContentPane() {
		return (JTabbedPane)super.getContentPane();
	}

	@Override
	public void setIconImage(Image image) {
		super.setIconImage(image);

		if(Taskbar.isTaskbarSupported() && Taskbar.getTaskbar().isSupported(Taskbar.Feature.ICON_IMAGE))
			Taskbar.getTaskbar().setIconImage(image);
	}

	public static SeedCandy get() {
		return INSTANCE;
	}
}