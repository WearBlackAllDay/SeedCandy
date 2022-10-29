package wearblackallday.seedcandy.components;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.util.Strings;
import wearblackallday.seedcandy.SeedCandy;

import java.io.*;
import java.util.*;
import java.util.function.Supplier;

public interface SeedCandyTab extends Supplier<TextBox> {

	void onVersionChanged(MCVersion newVersion);

	default void setOutput(String output) {
		SeedCandy.get().getOutputFile().ifPresentOrElse(file -> {
			try(PrintStream out = new PrintStream(file)) {
				out.println(output);
			} catch(FileNotFoundException ignored) {
			}
		}, () -> this.get().setText(output));
	}

	default void setOutput(List<Long> seeds) {
		StringJoiner joiner = new StringJoiner("\n").setEmptyValue("no results");
		seeds.forEach(seed -> joiner.add(seed.toString()));
		this.setOutput(joiner.toString());
	}

	default void copyOutput() {
		Strings.clipboard(this.get().getText());
	}
}
