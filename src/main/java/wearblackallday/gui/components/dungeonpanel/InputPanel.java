package wearblackallday.gui.components.dungeonpanel;

import wearblackallday.gui.SeedCandy;
import kaptainwutax.seedutils.lcg.LCG;
import kaptainwutax.seedutils.mc.MCVersion;
import mjtb49.hashreversals.ChunkRandomReverser;
import randomreverser.call.java.FilteredSkip;
import randomreverser.call.java.NextInt;
import randomreverser.device.JavaRandomDevice;
import randomreverser.device.LCGReverserDevice;
import wearblackallday.data.Strings;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.CustomPanel;
import wearblackallday.swing.components.SelectionBox;

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
        JPanel lower = new JPanel();
        SelectionBox<MCVersion> versionSelector = new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);
        JComboBox<String> biomeSelector = new JComboBox<>(new String[]{"other Biome", "desert", "swamp", "swamp_hill"});
        JComboBox<String> sizeSelector = new JComboBox<>(new String[]{"9x9", "9x7", "7x9", "7x7"});

        CustomPanel upper = new CustomPanel(50, 25).
                addComponent(() -> sizeSelector).
                addTextField("X","x").
                addTextField("Y", "y").
                addTextField("Z", "z").
                addComponent(() -> versionSelector).
                addComponent(() -> biomeSelector).
                addButton("move", 80, 25, e -> {
                    SeedCandy.INSTANCE.structurePanel.inputText.setText(SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.getText());
                    SeedCandy.INSTANCE.tabbedPane.setSelectedComponent(SeedCandy.INSTANCE.structurePanel);
                }).
                addButton("copy", 80, 25, e -> Strings.clipboard(SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.getText()));

        this.dungeonString.setPreferredSize(new Dimension(520, 25));
        this.dungeonString.setFont(new Font("Arial", Font.PLAIN, 10));
        versionSelector.addActionListener(e -> biomeSelector.setEnabled(versionSelector.getSelected() == MCVersion.v1_16));
        sizeSelector.addActionListener(e -> SeedCandy.INSTANCE.dungeonPanel.resizeGUI(String.valueOf(sizeSelector.getSelectedItem())));
        this.crackButton.addActionListener(e -> {
            SeedCandy.INSTANCE.dungeonPanel.dungeonOutput.setText("");
            int posX = Integer.parseInt(upper.getText("x").trim());
            int posY = Integer.parseInt(upper.getText("y").trim());
            int posZ = Integer.parseInt(upper.getText("z").trim());
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

        SwingUtils.addSet(lower, this.dungeonString, this.crackButton);
        this.add(upper, BorderLayout.CENTER);
        this.add(lower, BorderLayout.SOUTH);
    }

    public void updateBits(float bits) {
        this.crackButton.setText(Math.round(bits) + "/32");
        this.crackButton.setEnabled(Math.round(bits) > 32);
    }
}