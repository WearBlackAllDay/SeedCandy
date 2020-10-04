package gui.components.dungeonpanel;

import data.Strings;
import gui.SeedCandy;
import kaptainwutax.seedutils.lcg.LCG;
import kaptainwutax.seedutils.mc.MCVersion;
import mjtb49.hashreversals.ChunkRandomReverser;
import org.jdesktop.swingx.prompt.PromptSupport;
import randomreverser.call.java.FilteredSkip;
import randomreverser.call.java.NextInt;
import randomreverser.device.JavaRandomDevice;
import randomreverser.device.LCGReverserDevice;
import swing.content.SelectionBox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;


public class InputPanel extends JPanel {

    public final JTextField dungeonString;
    private final JButton crackButton;

    public InputPanel() {
        this.dungeonString = new JTextField("String");
        this.crackButton = new JButton();
        this.setLayout(new BorderLayout());
        JPanel upper = new JPanel();
        JPanel lower = new JPanel();
        JTextField xCord = new JTextField();
        JTextField yCord = new JTextField();
        JTextField zCord = new JTextField();
        SelectionBox<MCVersion> versionSelector = new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);
        JComboBox<String> biomeSelector = new JComboBox<>(new String[]{"other Biome", "desert", "swamp", "swamp_hill"});
        JComboBox<String> sizeSelector = new JComboBox<>(new String[]{"9x9", "9x7", "7x9", "7x7"});
        JButton copyButton = new JButton("copy");
        JButton moveButton = new JButton("move");
        Dimension dimension = new Dimension(50, 25);
        Dimension buttonDimension = new Dimension(80, 25);

        xCord.setPreferredSize(dimension);
        PromptSupport.setPrompt("X", xCord);
        yCord.setPreferredSize(dimension);
        PromptSupport.setPrompt("Y", yCord);
        zCord.setPreferredSize(dimension);
        PromptSupport.setPrompt("Z", zCord);

        this.dungeonString.setPreferredSize(new Dimension(520, 25));
        this.crackButton.setPreferredSize(buttonDimension);
        copyButton.setPreferredSize(buttonDimension);
        moveButton.setPreferredSize(buttonDimension);

        versionSelector.addActionListener(e -> biomeSelector.setEnabled(versionSelector.getSelected() == MCVersion.v1_16));
        sizeSelector.addActionListener(e -> SeedCandy.INSTANCE.dungeonPanel.resizeGUI(String.valueOf(sizeSelector.getSelectedItem())));
        copyButton.addActionListener(e -> Strings.clipboard(SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.getText()));
        moveButton.addActionListener(e -> {
            SeedCandy.INSTANCE.structurePanel.inputText.setText(SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.getText());
            SeedCandy.INSTANCE.tabbedPane.setSelectedComponent(SeedCandy.INSTANCE.structurePanel);
        });
        this.crackButton.addActionListener(e -> {
            SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.setText("");
            int posX = Integer.parseInt(xCord.getText().trim());
            int posY = Integer.parseInt(yCord.getText().trim());
            int posZ = Integer.parseInt(zCord.getText().trim());
            int offsetX = posX & 15;
            int offsetZ = posZ & 15;
            Integer[] pattern = this.dungeonString.getText().chars().mapToObj(c -> c == '0' ? 0 : c == '1' ? 1 : 2).toArray(Integer[]::new);
            JavaRandomDevice device = new JavaRandomDevice();
            ArrayList<Long> StructureSeeds = new ArrayList<>();

            if ((versionSelector.getSelected()).isNewerThan(MCVersion.v1_14_4)) {
                device.addCall(NextInt.withValue(16, offsetX));
                device.addCall(NextInt.withValue(16, offsetZ));
                device.addCall(NextInt.withValue(256, posY));
                device.addCall(NextInt.consume(2, 2));
            } else {
                device.addCall(NextInt.withValue(16, offsetX));
                device.addCall(NextInt.withValue(256, posY));
                device.addCall(NextInt.withValue(16, offsetZ));
                device.addCall(NextInt.consume(2, 2));
            }

            for (Integer integer : pattern) {
                if (integer == 0) {
                    device.addCall(NextInt.withValue(4, 0));
                } else if (integer == 1) {
                    device.addCall(FilteredSkip.filter(LCG.JAVA, r -> r.nextInt(4) != 0, 1));
                } else {
                    device.addCall(NextInt.consume(4, 1));
                }
            }

            Set<Long> decoratorSeeds = device.streamSeeds(LCGReverserDevice.Process.EVERYTHING).parallel().limit(1).collect(Collectors.toSet());

            for (long decoratorSeed : decoratorSeeds) {
                LCG failedDungeon = LCG.JAVA.combine(-5);
                for (int i = 0; i < 8; i++) {
                    StructureSeeds.addAll(ChunkRandomReverser.reversePopulationSeed(
                            (decoratorSeed ^ LCG.JAVA.multiplier) - (versionSelector.getSelected() != MCVersion.v1_16 ? 20003L :
                                    biomeSelector.getSelectedItem().equals("other Biome") ? 30002L : 30003L), posX & -16, posZ & -16, versionSelector.getSelected()));

                    decoratorSeed = failedDungeon.nextSeed(decoratorSeed);
                }
            }
            StructureSeeds.forEach(seed -> SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.addEntry(String.valueOf(seed)));
            if (SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.getText().isEmpty()) {
                SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.setText("no results");
            }
        });

        upper.add(sizeSelector);
        upper.add(xCord);
        upper.add(yCord);
        upper.add(zCord);
        upper.add(versionSelector);
        upper.add(biomeSelector);
        upper.add(moveButton);
        upper.add(copyButton);
        lower.add(this.dungeonString);
        lower.add(this.crackButton);
        this.add(upper, BorderLayout.CENTER);
        this.add(lower, BorderLayout.SOUTH);
    }

    public void updateBits(float bits) {
        this.crackButton.setText(Math.round(bits) + "/32");
        this.crackButton.setEnabled(Math.round(bits) > 32);
    }
}