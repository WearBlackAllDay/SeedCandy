package wearblackallday.components;

import kaptainwutax.mcutils.version.MCVersion;
import wearblackallday.SeedCandy;
import wearblackallday.data.Strings;

import javax.swing.*;

public abstract class AbstractTab extends JComponent {

	public MCVersion getVersion() {
		return ((SeedCandy)SwingUtilities.getWindowAncestor(this)).version;
	}

	public abstract String getOutput();

	public void copyOutput() {
		Strings.clipboard(this.getOutput());
	}
}
