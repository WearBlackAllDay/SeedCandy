package wearblackallday.seedcandy.components;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.util.ThreadPool;

import javax.swing.*;
import java.awt.Component;
import java.awt.Container;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;

public abstract class SeedTab extends JComponent implements SeedCandyTab {

	private final TextBox input = new TextBox(true);
	private final TextBox output = new TextBox(false);

	private final Box mainPanel = new Box(BoxLayout.Y_AXIS);
	private final JProgressBar progressBar = new JProgressBar(0, 1);

	private final ThreadPool pool = new ThreadPool();

	protected SeedTab(String title) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setName(title);

		this.input.getVerticalScrollBar().addAdjustmentListener(e ->
			this.output.getVerticalScrollBar().setValue(e.getValue()));

		this.output.getVerticalScrollBar().addAdjustmentListener(e ->
			this.input.getVerticalScrollBar().setValue(e.getValue()));

		this.mainPanel.add(this.progressBar);
		super.add(this.input);
		super.add(this.output);
		super.add(this.mainPanel);
	}

	protected LongStream getInput() {
		return this.input.seeds();
	}

	protected void mapSeeds(LongUnaryOperator mapper) {
		this.setOutput(this.input.seeds()
			.map(mapper).boxed().toList());
	}

	protected void mapToString(LongFunction<String> mapper) {
		long[] seeds = this.input.seeds().toArray();

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

	private void toggleComponents(boolean activated) {
		for(var panel : this.mainPanel.getComponents()) {
			for(var button : ((Container)panel).getComponents()) {
				button.setEnabled(activated);
			}
		}
	}

	@Override
	public Component add(Component comp) {
		return this.mainPanel.add(comp, this.mainPanel.getComponentCount() - 1);
	}

	@Override
	public void onVersionChanged(MCVersion newVersion) {
	}

	@Override
	public TextBox get() {
		return this.output;
	}
}
