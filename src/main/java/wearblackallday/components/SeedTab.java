package wearblackallday.components;

import wearblackallday.util.ThreadPool;

import javax.swing.*;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;

public abstract class SeedTab extends Box {
	protected final TextBlock input = new TextBlock(true);
	protected final TextBlock output = new TextBlock(false);
	private final Box mainPanel = new Box(BoxLayout.Y_AXIS);
	public static final ThreadPool POOL = new ThreadPool();

	public SeedTab(String title) {
		super(BoxLayout.X_AXIS);
		this.setName(title);
		this.add(this.input);
		this.add(this.output);
		this.add(this.mainPanel);
	}

	protected void addComponents(JComponent... components) {
		for(JComponent c : components) {
			this.mainPanel.add(c);
		}
	}

	protected void mapSeeds(LongUnaryOperator mapper) {
		var buffer = new StringBuilder();

		for(long seed : this.input.getLongs()) {
			buffer.append(mapper.applyAsLong(seed)).append("\n");
		}
		this.output.setText(buffer.toString());
	}

	protected void mapToString(LongFunction<String> mapper) {
		var buffer = new StringBuilder();

		for(long seed : this.input.getLongs()) {
			buffer.append(mapper.apply(seed)).append("\n");
		}
		this.output.setText(buffer.toString());
	}
}
