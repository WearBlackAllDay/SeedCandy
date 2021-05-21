package wearblackallday.components;

import wearblackallday.data.Strings;

import javax.swing.*;
import java.awt.Dimension;

public class TextBlock extends JScrollPane {
	private final JTextArea textArea = new JTextArea();

	public TextBlock(boolean editable) {
		this.textArea.setEditable(editable);
		this.textArea.setFocusable(editable);
		this.setPreferredSize(new Dimension(200, 600));
		this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		this.setViewportView(this.textArea);
	}

	public String getText() {
		return this.textArea.getText();
	}

	public long[] getLongs() {
		return Strings.splitToLongs(this.getText());
	}

	public void setText(String text) {
		this.textArea.setText(text);
	}

	public void addEntry(String entry) {
		this.textArea.setText(this.textArea.getText() + entry + "\n");
	}

	public void addEntry(long entry) {
		this.addEntry(String.valueOf(entry));
	}
}
