package wearblackallday.seedcandy.components;

import wearblackallday.javautils.util.ThreadPool;

import javax.swing.*;
import java.awt.Container;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;

public abstract class SeedTab extends AbstractTab {
	protected final TextBox input = new TextBox(true);
	protected final TextBox output = new TextBox(false);
	protected final JProgressBar progressBar = new JProgressBar(0, 1);
	private final Box mainPanel = new Box(BoxLayout.Y_AXIS);
	private final ThreadPool pool = new ThreadPool();

	protected SeedTab(String title) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setName(title);
		this.add(this.input);
		this.add(this.output);
		this.add(this.mainPanel);

		this.input.getVerticalScrollBar().addAdjustmentListener(e ->
			this.output.getVerticalScrollBar().setValue(e.getValue()));

		this.output.getVerticalScrollBar().addAdjustmentListener(e ->
			this.input.getVerticalScrollBar().setValue(e.getValue()));
	}

	protected void addComponents(JComponent... components) {
		for(var component : components) {
			this.mainPanel.add(component);
		}
		this.mainPanel.add(this.progressBar);
	}

	protected void mapSeeds(LongUnaryOperator mapper) {
		var buffer = new StringBuilder();

		for(long seed : this.input.getLongs()) {
			buffer.append(mapper.applyAsLong(seed)).append("\n");
		}
		this.output.setText(buffer.toString());
	}

	protected void mapToString(LongFunction<String> mapper) {
		long[] seeds = this.input.getLongs();

		if(seeds.length < this.pool.getThreadCount() << 2) {
			StringJoiner joiner = new StringJoiner("\n");

			for(long seed : seeds) {
				joiner.add(mapper.apply(seed));
			}
			this.setOutput(joiner.toString());
		} else {
			this.threadedMap(seeds, mapper);
		}
	}

	protected void threadedMap(long[] seeds, LongFunction<String> mapper) {
		int threads = this.pool.getThreadCount();
		Set<String> results = Collections.synchronizedSet(new HashSet<>());
		AtomicInteger progress = new AtomicInteger(0);
		this.progressBar.setMaximum(seeds.length);
		this.toggleComponents(false);

		for(int i = 0; i < threads; i++) {
			int start = i;
			this.pool.execute(() -> {
				int current = start;
				while(current < seeds.length) {
					results.add(mapper.apply(seeds[current]));
					current += threads;
					SwingUtilities.invokeLater(() -> {
						this.progressBar.setValue(progress.incrementAndGet());
						if(progress.get() == this.progressBar.getMaximum()) {
							this.toggleComponents(true);
							results.remove("");
							this.setOutput(String.join("\n", results));
						}
					});
				}
			});
		}
	}

	protected void toggleComponents(boolean activated) {
		for(var panel : this.mainPanel.getComponents()) {
			for(var button : ((Container)panel).getComponents()) {
				button.setEnabled(activated);
			}
		}
	}

	@Override
	public String getOutput() {
		return this.output.getText();
	}

	@Override
	public void setOutputDefault(String output) {
		this.output.setText(output);
	}
}
