package wearblackallday.seedcandy.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Image;
import java.io.IOException;

public final class Icons {
	private Icons() {}

	public static final Icon COBBLE = new ImageIcon(load("/icons/cobble.png"));
	public static final Icon MOSSY = new ImageIcon(load("/icons/mossy.png"));
	public static final Icon UNKNOWN = new ImageIcon(load("/icons/unknown.png"));
	public static final Image SEED = load("/icons/seed.png");

	private static Image load(String path) {
		try {
			return ImageIO.read(Icons.class.getResource(path));
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
