package wearblackallday.seedcandy.components;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.data.Strings;
import wearblackallday.seedcandy.SeedCandy;

import java.io.*;
import java.util.*;

public interface SeedCandyTab {

	void onVersionChanged(MCVersion newVersion);

	String getOutput();

	void setOutputDefault(String output);

	 default void setOutput(String output) {
		SeedCandy.get().getOutputFile().ifPresentOrElse(file -> {
			try(PrintStream out = new PrintStream(file)) {
				out.println(output);
			} catch(FileNotFoundException ignored) {
			}
		}, () -> this.setOutputDefault(output));
	}

	 default void setOutput(List<Long> seeds) {
		StringJoiner joiner = new StringJoiner("\n");
		seeds.forEach(seed -> joiner.add(seed.toString()));
		this.setOutput(joiner.toString());
	}

	default void copyOutput() {
		Strings.clipboard(this.getOutput());
	}
}
