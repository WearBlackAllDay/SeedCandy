package gui.components.structurepanel;

import data.Strings;
import gui.SeedCandy;
import gui.components.TextBlock;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.seed.StructureSeed;
import swing.components.GridPanel;
import threading.ThreadPool;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StructurePanel extends JPanel {

    public final TextBlock inputText;

    public StructurePanel() {
        this.inputText = new TextBlock(true);
        TextBlock outputText = new TextBlock(false);
        JPanel inputPanel = new JPanel(new BorderLayout());
        GridPanel<BiomeUnit> biomePanel = new GridPanel<>(1, 16, BiomeUnit::new);
        JPanel selectionPanel = new JPanel(new GridLayout(2, 2));
        JButton longButton = new JButton("reverse to nextLong()");
        JButton biomeButton = new JButton("crack with Biomes");
        JButton verifyButton = new JButton("verify WorldSeeds");
        JProgressBar progressBar = new JProgressBar();

        progressBar.setMinimum(0);
        this.setLayout(new BorderLayout());
        this.setName("StructureSeed");

        longButton.addActionListener(e -> {
            outputText.setText("");
            for (long seed : Strings.splitToLongs(this.inputText.getText())) {
                StructureSeed.toRandomWorldSeeds(seed).forEach(worldseed -> outputText.addEntry(String.valueOf(worldseed)));
            }
        });

        biomeButton.addActionListener(e -> {
            outputText.setText("");
            progressBar.setMaximum(Strings.countLines(this.inputText.getText()) * 65536);
            AtomicInteger progress = new AtomicInteger(0);
            SeedCandy.POOL.execute(Strings.splitToLongs(this.inputText.getText()), seed -> StructureSeed.getWorldSeeds(seed).forEachRemaining(candidate -> {
                OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_2, candidate);
                progressBar.setValue(progress.incrementAndGet());
                boolean match = true;
                for (int i = 0; i < 16; i++) {
                    if (!biomePanel.componentAt(0, i).matches(biomeSource)) {
                        match = false;
                        break;
                    }
                }
                if(match) SwingUtilities.invokeLater(() -> outputText.addEntry(String.valueOf(candidate)));
            }));
        });

        verifyButton.addActionListener(e -> {
            outputText.setText("");
            for (long seed : Strings.splitToLongs(this.inputText.getText())) {
                OverworldBiomeSource biomeSource = new OverworldBiomeSource(MCVersion.v1_16_2, seed);
                boolean match = true;
                for (int i = 0; i < 16; i++) {
                    if (!biomePanel.componentAt(0, i).matches(biomeSource)) {
                        match = false;
                        break;
                    }
                }
                if(match) outputText.addEntry(String.valueOf(seed));
            }
        });

        selectionPanel.add(longButton);
        selectionPanel.add(biomeButton);
        selectionPanel.add(verifyButton);
        selectionPanel.add(progressBar);
        inputPanel.add(biomePanel, BorderLayout.CENTER);
        inputPanel.add(selectionPanel, BorderLayout.SOUTH);
        this.add(this.inputText, BorderLayout.WEST);
        this.add(outputText, BorderLayout.CENTER);
        this.add(inputPanel, BorderLayout.EAST);
    }
}
