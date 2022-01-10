package wearblackallday.seedcandy.components;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.data.Strings;

import javax.swing.*;

public abstract class AbstractTab extends JComponent {

	public MCVersion getVersion() {
		return SeedCandy.get().version;
	}

	public abstract String getOutput();

	public void copyOutput() {
		Strings.clipboard(this.getOutput());
	}
}
