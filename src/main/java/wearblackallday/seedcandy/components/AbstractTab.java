package wearblackallday.seedcandy.components;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.seedcandy.SeedCandy;
import wearblackallday.data.Strings;

import javax.swing.*;
import java.io.*;
import java.util.*;

public abstract class AbstractTab extends JComponent {

	public MCVersion getVersion() {
		return SeedCandy.get().version;
	}

	public abstract String getOutput();

	public abstract void setOutputDefault(String output);

	protected void setOutput(String output) {
		SeedCandy.get().outputFile.ifPresentOrElse(file -> {
			try {
				new PrintStream(file).append(output).flush();
			} catch(FileNotFoundException ignored) {
			}
		}, () -> this.setOutputDefault(output));
	}

	protected void setOutput(Collection<Long> seeds) {
		StringJoiner joiner = new StringJoiner("\n");
		seeds.forEach(seed -> joiner.add(seed.toString()));
		this.setOutput(joiner.toString());
	}

	public void copyOutput() {
		Strings.clipboard(this.getOutput());
	}
}
