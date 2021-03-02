package gui.components.worldpanel;

import gui.SeedCandy;
import gui.components.TextBlock;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.ChunkSeeds;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import util.QuadFinder;
import wearblackallday.data.Strings;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.ButtonSet;
import wearblackallday.swing.components.SelectionBox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class WorldPanel extends JPanel{
    public WorldPanel() {
        TextBlock inputText = new TextBlock(true);
        TextBlock outputText = new TextBlock(false);
        JPanel inputPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
        JPanel selectionPanel = new JPanel();
        ButtonSet<JButton> buttonSet = new ButtonSet<>(JButton::new,
                "check for nextLong()", "locate Quadhuts",
                "show/filter Spawnpoint", "convert to hash",
                "switch to ShadowSeed", "get SisterSeeds",
                "reduce to StructureSeed", "get PopulationSeed",
                "get RegionSeed", "get PillarSeed",
                "copy Output", "clear text");
        JTextField xCord = new JTextField();
        JTextField zCord = new JTextField();
        JTextField saltField = new JTextField();
        SelectionBox<MCVersion> versionSelector = new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);
        JProgressBar progressBar = new JProgressBar(0,1);

        this.setLayout(new BorderLayout());
        this.setName("WorldSeed");

        xCord.setPreferredSize(new Dimension(40, 25));
        SwingUtils.setPrompt("X", xCord);
        zCord.setPreferredSize(new Dimension(40, 25));
        SwingUtils.setPrompt("Z", zCord);
        saltField.setPreferredSize(new Dimension(60, 25));
        SwingUtils.setPrompt("salt", saltField);

        buttonSet.addListeners(
                nextLongButton -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(WorldSeed.isRandom(seed) ? "possible nextLong()" : "is NOT nextLong()");
                    }
                },
                quadButton -> {
                    outputText.setText("");
                    progressBar.setMaximum(Strings.countLines(inputText.getText()));
                    SeedCandy.POOL.execute((Strings.splitToLongs(inputText.getText())), seed ->
                            outputText.addEntry(QuadFinder.find(seed, versionSelector.getSelected())));
                },
                spawnButton -> {
                    MCVersion version = versionSelector.getSelected();
                    outputText.setText("");
                    try {
                        BPos search = new BPos(Integer.parseInt(xCord.getText().trim()),0, Integer.parseInt(zCord.getText().trim()));

                        SeedCandy.POOL.execute(Strings.splitToLongs(inputText.getText()), seed -> {
                            OverworldBiomeSource biomeSource = new OverworldBiomeSource(version, seed);
                            if (biomeSource.getSpawnPoint().equals(search)) {
                                outputText.addEntry(String.valueOf(seed));
                            }
                        });
                    } catch (NumberFormatException exception) {
                        SeedCandy.POOL.execute(Strings.splitToLongs(inputText.getText()), seed -> {
                            OverworldBiomeSource biomeSource = new OverworldBiomeSource(version, seed);
                            outputText.addEntry(String.format("%d (%d, %d)", seed, biomeSource.getSpawnPoint().getX(), biomeSource.getSpawnPoint().getZ()));
                        });

                    }
                },
                hashButton -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(WorldSeed.toHash(seed)));
                    }
                },
                shadowButton -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(WorldSeed.getShadowSeed(seed)));
                    }
                },
                sisterButton -> {
                    outputText.setText("");
                    ArrayList<Long> seedList = new ArrayList<>(Strings.splitToLongs(inputText.getText()).length * 65536);
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        WorldSeed.getSisterSeeds(seed).forEachRemaining(seedList::add);
                    }
                    outputText.setText(String.join("\n", seedList.stream().map(String::valueOf).toArray(String[]::new)));
                },
                structureButton -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(WorldSeed.toStructureSeed(seed)));
                    }
                },
                populationButton -> {
                    outputText.setText("");
                    MCVersion version = versionSelector.getSelected();
                    int x,z;
                    try {
                        x = Integer.parseInt(xCord.getText().trim());
                        z = Integer.parseInt(zCord.getText().trim());
                    } catch (NumberFormatException exception) {
                        x = z = 0;
                    }
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(ChunkSeeds.getPopulationSeed(seed, x, z, version)));
                    }
                },
                regionButton -> {
                    outputText.setText("");
                    MCVersion version = versionSelector.getSelected();
                    int x, z, salt;
                    try {
                        x = Integer.parseInt(xCord.getText().trim());
                        z = Integer.parseInt(zCord.getText().trim());
                        salt = Integer.parseInt(saltField.getText().trim());
                    } catch (NumberFormatException exception) {
                        x = z = 0;
                        salt = 14357620;
                    }
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(ChunkSeeds.getRegionSeed(seed, x, z, salt, version)));
                    }
                },
                pillarButton -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(WorldSeed.toPillarSeed(seed)));
                    }
                },
                copyButton -> Strings.clipboard(outputText.getText()),
                clearButton -> {
                    inputText.setText("");
                    outputText.setText("");
                }
        );

        buttonSet.addAll(buttonPanel);
        selectionPanel.add(xCord);
        selectionPanel.add(zCord);
        selectionPanel.add(saltField);
        selectionPanel.add(versionSelector);
        selectionPanel.add(progressBar);
        inputPanel.add(buttonPanel, BorderLayout.CENTER);
        inputPanel.add(selectionPanel, BorderLayout.SOUTH);
        this.add(inputText, BorderLayout.WEST);
        this.add(outputText, BorderLayout.CENTER);
        this.add(inputPanel,BorderLayout.EAST);
    }
}
