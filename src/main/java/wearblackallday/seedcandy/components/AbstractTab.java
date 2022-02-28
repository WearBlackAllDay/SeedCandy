package wearblackallday.seedcandy.components;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.data.Strings;
import wearblackallday.seedcandy.SeedCandy;

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
				new PrintStream(file).println(output);
			} catch(FileNotFoundException ignored) {
			}
		}, () -> this.setOutputDefault(output));
	}

	protected void setOutput(List<Long> seeds) {
		StringJoiner joiner = new StringJoiner("\n");
		seeds.forEach(seed -> joiner.add(seed.toString()));
		this.setOutput(joiner.toString());
	}

	public void copyOutput() {
		Strings.clipboard(this.getOutput());
	}
}
