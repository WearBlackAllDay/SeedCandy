package wearblackallday.gui.components.dungeonpanel;

import kaptainwutax.seedutils.lcg.LCG;
import kaptainwutax.seedutils.mc.MCVersion;
import mjtb49.hashreversals.ChunkRandomReverser;
import randomreverser.call.java.FilteredSkip;
import randomreverser.call.java.NextInt;
import randomreverser.device.JavaRandomDevice;
import randomreverser.device.LCGReverserDevice;
import wearblackallday.data.Strings;
import wearblackallday.gui.components.TextBlock;
import wearblackallday.swing.components.CustomPanel;
import wearblackallday.swing.components.GridPanel;
import wearblackallday.swing.components.SelectionBox;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DungeonPanel extends JPanel {
	private final JTextField dungeonString = new JTextField();
	private final JLabel bitLabel = new JLabel();
	private final SelectionBox<GridPanel<StringButton>> sizeSelector;

	@SuppressWarnings("unchecked")
	public DungeonPanel() {
		JPanel guiPanel = new JPanel(new CardLayout());
		TextBlock dungeonOutput = new TextBlock(false);

		guiPanel.add(new GridPanel<>(9, 9, StringButton::new), "99");
		guiPanel.add(new GridPanel<>(9, 7, StringButton::new), "97");
		guiPanel.add(new GridPanel<>(7, 9, StringButton::new), "79");
		guiPanel.add(new GridPanel<>(7, 7, StringButton::new), "77");

		this.sizeSelector = new SelectionBox<>(gridPanel ->
			gridPanel.getColumns() + "x" + gridPanel.getRows(),
			Arrays.stream(guiPanel.getComponents())
				.map(component -> (GridPanel<StringButton>)component)
				.collect(Collectors.toList()));
		this.sizeSelector.addActionListener(e -> {
			((CardLayout)guiPanel.getLayout()).show(guiPanel, "" +
				this.sizeSelector.getSelected().getRows() +
				this.sizeSelector.getSelected().getColumns());
			this.updateInfo();
		});
		SelectionBox<MCVersion> versionSelector =
			new SelectionBox<>(MCVersion.v1_16, MCVersion.v1_15, MCVersion.v1_14, MCVersion.v1_13);
		SelectionBox<String> biomeSelector =
			new SelectionBox<>("other Biome", "desert", "swamp", "swamp_hill");
		versionSelector.addActionListener(e -> biomeSelector.setEnabled(versionSelector.getSelected() == MCVersion.v1_16));

		JSplitPane userEntry = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
			new CustomPanel(50, 25)
				.addComponent(this.sizeSelector)
				.addTextField("X", "x")
				.addTextField("Y", "y")
				.addTextField("Z", "z")
				.addComponent(versionSelector)
				.addComponent(biomeSelector)
				.addButton("copy", 80, 25, (panel, button, event) ->
					Strings.clipboard(dungeonOutput.getText()))
				.addButton("crack", 80, 25, (panel, button, event) -> {
					dungeonOutput.setText("");
					int posX, posY, posZ;
					try {
						posX = Integer.parseInt(panel.getText("x").trim());
						posY = Integer.parseInt(panel.getText("y").trim());
						posZ = Integer.parseInt(panel.getText("z").trim());
					} catch(NumberFormatException e) {
						return;
					}
					if(!this.dungeonString.getText().matches("[0-2]+")) return;

					int offsetX = posX & 15;
					int offsetZ = posZ & 15;
					LCG failedDungeon = LCG.JAVA.combine(-5);
					JavaRandomDevice device = new JavaRandomDevice();
					device.addCall(NextInt.withValue(16, offsetX));
					if((versionSelector.getSelected()).isNewerThan(MCVersion.v1_14_4)) {
						device.addCall(NextInt.withValue(16, offsetZ));
						device.addCall(NextInt.withValue(256, posY));
					} else {
						device.addCall(NextInt.withValue(256, posY));
						device.addCall(NextInt.withValue(16, offsetZ));
					}
					device.addCall(NextInt.consume(2, 2));

					this.dungeonString.getText().chars().forEach(i -> {
						switch(i) {
							case '0':
								device.addCall(NextInt.withValue(4, 0));
								break;
							case '1':
								device.addCall(FilteredSkip.filter(LCG.JAVA, r ->
									r.nextInt(4) != 0, 1));
								break;
							case '2':
								device.addCall(NextInt.consume(4, 1));
								break;
						}
					});
					device.streamSeeds(LCGReverserDevice.Process.EVERYTHING)
						.parallel()
						.limit(1)
						.findAny()
						.ifPresent(decoratorSeed -> {
							for(int i = 0; i < 8; i++) {
								ChunkRandomReverser.reversePopulationSeed(
									(decoratorSeed ^ LCG.JAVA.multiplier) -
										(versionSelector.getSelected() != MCVersion.v1_16 ? 20003L :
											biomeSelector.getSelected().equals("other Biome") ? 30002L : 30003L),
									posX & -16,
									posZ & -16,
									versionSelector.getSelected())
									.forEach(seed -> dungeonOutput.addEntry(String.valueOf(seed)));
								decoratorSeed = failedDungeon.nextSeed(decoratorSeed);
							}
						});


					if(dungeonOutput.getText().isEmpty()) {
						dungeonOutput.setText("no results");
					}
				})
				.addComponent(this.bitLabel),
			this.dungeonString);

		this.setLayout(new BorderLayout());
		this.add(guiPanel, BorderLayout.CENTER);
		this.add(userEntry, BorderLayout.SOUTH);
		this.add(dungeonOutput, BorderLayout.EAST);
		this.setName("DungeonCracker");
		this.updateInfo();
	}

	protected void updateInfo() {
		this.bitLabel.setText("Bits: " + Math.round(this.sizeSelector.getSelected().stream()
			.mapToDouble(StringButton::getBits)
			.sum()));

		this.dungeonString.setText(this.sizeSelector.getSelected().stream()
			.map(StringButton::getString)
			.collect(Collectors.joining()));
	}
}
