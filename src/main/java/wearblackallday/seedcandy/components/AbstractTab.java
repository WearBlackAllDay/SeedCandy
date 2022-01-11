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
		if(SeedCandy.get().outputFile == null) this.setOutputDefault(output);
		else {
			try {
				new PrintStream(SeedCandy.get().outputFile).append(output).flush();
			} catch(FileNotFoundException ignored) {
			}
		}
	}

	protected void setOutput(Collection<String> outputCol) {
		this.setOutput(String.join("\n", outputCol));
	}

	protected void setOutput(List<Long> seedList) {
		StringJoiner joiner = new StringJoiner("\n");
		seedList.forEach(seed -> joiner.add(seed.toString()));
		this.setOutput(joiner.toString());
	}

	public void copyOutput() {
		Strings.clipboard(this.getOutput());
	}
}
