package wearblackallday.seedcandy.components;

import javax.swing.*;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.stream.LongStream;

public class TextBox extends JScrollPane {
	private final JTextArea textArea = new JTextArea();

	public TextBox(boolean editable) {
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

	public LongStream seeds() {
		return Arrays.stream(this.getText().trim().split("[\t\r\n\s]+")).mapToLong(Long::parseLong);
	}

	public void setText(String text) {
		this.textArea.setText(text);
	}
}
