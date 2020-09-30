package util;

import gui.SeedCandy;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;

public class Icons {
    public static final Image COBBLE = load("/icons/cobble.png");
    public static final Image MOSSY = load("/icons/mossy.png");
    public static final Image UNKNOWN = load("/icons/unknown.png");
    public static final Image SEED = load("/icons/seed.png");

    private static Image load(String path) {
        try {
            return ImageIO.read(SeedCandy.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
