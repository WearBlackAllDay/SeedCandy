package gui.components;

import javax.swing.*;
import java.awt.*;

public class TextBlock extends JScrollPane {

    private final JTextArea text;

    public TextBlock(boolean input) {
        this.text = new JTextArea();
        this.setPreferredSize(new Dimension(200,600));
        this.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        this.text.setEditable(input);

        this.setViewportView(this.text);
    }

    public String getText() {
        return this.text.getText();
    }

    public void setText(String text) {
        this.text.setText(text);
    }

    public void addEntry(String entry) {
        this.text.setText(this.text.getText() + entry + "\n");
    }
}
