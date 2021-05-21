package wearblackallday.components;

import javax.swing.*;

public abstract class SeedPanel extends Box {
	protected final TextBlock input = new TextBlock(true);
	protected final TextBlock output = new TextBlock(false);
	private final Box mainPanel = new Box(BoxLayout.Y_AXIS);

	public SeedPanel(String title) {
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
}
