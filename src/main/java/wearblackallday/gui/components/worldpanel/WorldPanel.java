package wearblackallday.gui.components.worldpanel;

import wearblackallday.gui.SeedCandy;
import wearblackallday.gui.components.TextBlock;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.ChunkSeeds;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import wearblackallday.swing.components.CustomPanel;
import wearblackallday.util.QuadFinder;
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
        SelectionBox<MCVersion> versionSelector = new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);
        JProgressBar progressBar = new JProgressBar(0,1);
        this.setLayout(new BorderLayout());
        this.setName("WorldSeed");

        CustomPanel selectionPanel = new CustomPanel(40, 25).
                addTextField("X", "x").
                addTextField("Z", "z").
                addTextField("salt", 60, 25, "s").
                addComponent(() -> versionSelector).
                addComponent(() -> progressBar);

        CustomPanel buttonPanel = new CustomPanel(new GridLayout(0, 2)).
                addButton("check for nextLong()", e -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(WorldSeed.isRandom(seed) ? "possible nextLong()" : "is NOT nextLong()");
                    }
                }).
                addButton("locate Quadhuts", e -> {
                    outputText.setText("");
                    progressBar.setMaximum(Strings.countLines(inputText.getText()));
                    SeedCandy.POOL.execute((Strings.splitToLongs(inputText.getText())), seed ->
                            outputText.addEntry(QuadFinder.find(seed, versionSelector.getSelected())));
                }).
                addButton("show/filter Spawnpoint", e -> {
                    MCVersion version = versionSelector.getSelected();
                    outputText.setText("");
                    try {
                        BPos search = new BPos(Integer.parseInt(selectionPanel.getText("x").trim()),0,
                                Integer.parseInt(selectionPanel.getText("z").trim()));

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
                }).
                addButton("convert to hash", e -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(WorldSeed.toHash(seed)));
                    }
                }).
                addButton("switch to ShadowSeed", e -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(WorldSeed.getShadowSeed(seed)));
                    }
                }).
                addButton("get SisterSeeds", e -> {
                    outputText.setText("");
                    ArrayList<Long> seedList = new ArrayList<>(Strings.splitToLongs(inputText.getText()).length * 65536);
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        WorldSeed.getSisterSeeds(seed).forEachRemaining(seedList::add);
                    }
                    outputText.setText(String.join("\n", seedList.stream().map(String::valueOf).toArray(String[]::new)));
                }).
                addButton("reduce to StructureSeed", e -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(WorldSeed.toStructureSeed(seed)));
                    }
                }).
                addButton("get PopulationSeed", e -> {
                    outputText.setText("");
                    MCVersion version = versionSelector.getSelected();
                    int x,z;
                    try {
                        x = Integer.parseInt(selectionPanel.getText("x").trim());
                        z = Integer.parseInt(selectionPanel.getText("z").trim());
                    } catch (NumberFormatException exception) {
                        x = z = 0;
                    }
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(ChunkSeeds.getPopulationSeed(seed, x, z, version)));
                    }
                }).
                addButton("get RegionSeed", e -> {
                    outputText.setText("");
                    MCVersion version = versionSelector.getSelected();
                    int x, z, salt;
                    try {
                        x = Integer.parseInt(selectionPanel.getText("x").trim());
                        z = Integer.parseInt(selectionPanel.getText("z").trim());
                        salt = Integer.parseInt(selectionPanel.getText("s").trim());
                    } catch (NumberFormatException exception) {
                        x = z = 0;
                        salt = 14357620;
                    }
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(ChunkSeeds.getRegionSeed(seed, x, z, salt, version)));
                    }
                }).
                addButton("get PillarSeed", e -> {
                    outputText.setText("");
                    for (long seed : Strings.splitToLongs(inputText.getText())) {
                        outputText.addEntry(String.valueOf(WorldSeed.toPillarSeed(seed)));
                    }
                }).
                addButton("copy Output", e -> Strings.clipboard(outputText.getText())).
                addButton("clear text", e -> {
                    inputText.setText("");
                    outputText.setText("");
                });

        inputPanel.add(buttonPanel, BorderLayout.CENTER);
        inputPanel.add(selectionPanel, BorderLayout.SOUTH);
        this.add(inputText, BorderLayout.WEST);
        this.add(outputText, BorderLayout.CENTER);
        this.add(inputPanel,BorderLayout.EAST);
    }
}
