package wearblackallday.seedcandy.components;

import com.seedfinding.mccore.version.MCVersion;
import wearblackallday.javautils.util.ThreadPool;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public abstract class SeedTab extends JComponent implements SeedCandyTab {

	private final TextBox input = new TextBox(true);
	private final TextBox output = new TextBox(false);

	private final Box mainPanel = new Box(BoxLayout.Y_AXIS);
	private final JProgressBar progressBar = new JProgressBar(0, 0);

	private final ThreadPool pool = new ThreadPool();
	private final Set<String> outputBuffer = Collections.synchronizedSet(new HashSet<>());

	protected SeedTab(String title) {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		this.setName(title);

		this.input.getVerticalScrollBar().addAdjustmentListener(e ->
			this.output.getVerticalScrollBar().setValue(e.getValue()));

		this.output.getVerticalScrollBar().addAdjustmentListener(e ->
			this.input.getVerticalScrollBar().setValue(e.getValue()));

		 new Thread(this::awaitProgress, this.getClass().getSimpleName() + "\sMonitor").start();

		this.mainPanel.add(this.progressBar);
		super.add(this.input);
		super.add(this.output);
		super.add(this.mainPanel);
	}

	private synchronized void awaitProgress() {
		try {
			while(true) {
				this.wait();

				EventQueue.invokeLater(() -> this.setEnabled(false));
				while(this.pool.getActiveCount() > 0) {
					EventQueue.invokeLater(() -> this.progressBar.setValue(this.progressBar.getMaximum() - this.pool.getActiveCount()));
				}
				this.outputBuffer.remove("");

				EventQueue.invokeAndWait(() -> {
					this.setOutput(String.join("\n", this.outputBuffer));
					this.setEnabled(true);
				});

				this.outputBuffer.clear();
			}
		} catch(InterruptedException | InvocationTargetException ignored) {
		}
	}

	protected LongStream getInput() {
		return this.input.seeds();
	}

	protected void map(LongUnaryOperator mapper) {
		this.setOutput(this.input.seeds()
			.map(mapper).boxed().toList());
	}

	protected void flatMap(LongFunction<List<Long>> mapper) {
		this.mapSequential(l -> mapper.apply(l).stream()
			.map(String::valueOf)
			.collect(Collectors.joining("\n")));
	}

	protected void mapSequential(LongFunction<String> mapper) {
		this.setOutput(this.input.seeds()
			.mapToObj(mapper)
			.filter(Predicate.not(String::isEmpty))
			.collect(Collectors.joining("\n")));
	}

	protected synchronized void mapParallel(long[] seeds, LongFunction<String> mapper) {
		this.progressBar.setValue(0);
		this.progressBar.setMaximum(seeds.length);
		this.pool.execute(seeds, seed -> this.outputBuffer.add(mapper.apply(seed)));

		this.notify();
	}

	private static void deepSetEnabled(Component component, boolean enabled) {
		component.setEnabled(enabled);

		if(component instanceof Container parent) {
			for(Component child : parent.getComponents()) {
				deepSetEnabled(child, enabled);
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		deepSetEnabled(this.mainPanel, enabled);
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
