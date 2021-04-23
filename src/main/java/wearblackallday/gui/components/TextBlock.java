package wearblackallday.gui.components;

import javax.swing.*;
import java.awt.Dimension;

public class TextBlock extends JScrollPane {

	private final JTextArea textArea = new JTextArea();

	public TextBlock(boolean input) {
		this.setPreferredSize(new Dimension(200, 600));
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.textArea.setEditable(input);
		this.setViewportView(this.textArea);
	}

	public String getText() {
		return this.textArea.getText();
	}

	public void setText(String textArea) {
		this.textArea.setText(textArea);
	}

	public void addEntry(String entry) {
		this.textArea.setText(this.textArea.getText() + entry + "\n");
	}
}
