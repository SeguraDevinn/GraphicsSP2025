import javax.swing.*;
import java.awt.*;

public class TerrainGUI {
    public static void main(String[] args) {
        // Create the main frame
        JFrame frame = new JFrame("Terrain Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 250);
        frame.setLayout(new FlowLayout());

        // Create a slider to control terrain height multiplier
        JSlider heightSlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 5);
        heightSlider.setMajorTickSpacing(1);
        heightSlider.setPaintTicks(true);
        heightSlider.setPaintLabels(true);

        // Label to show the current height multiplier
        JLabel label = new JLabel("Height Multiplier: 1");

        // Flatten button
        JButton flattenButton = new JButton("Flatten");
        flattenButton.addActionListener(e -> {
            System.out.println("Flattening Terrain...");
            TerrainGeneration.updateTerrain(0, "flatten");
        });

        // Reset button
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            System.out.println("Resetting Terrain...");
            TerrainGeneration.updateTerrain(1, "reset"); // Reset the terrain
        });

        // Update button
        JButton updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            int heightMultiplier = heightSlider.getValue();
            System.out.println("Updating Terrain with Height Multiplier: " + heightMultiplier);
            TerrainGeneration.updateTerrain(heightMultiplier, "update");
        });

        // End button
        JButton endButton = new JButton("End");
        endButton.addActionListener(e -> {
            System.out.println("Program Ended.");
            System.exit(0);
        });

        // Update label when slider moves
        heightSlider.addChangeListener(e -> label.setText("Height Multiplier: " + heightSlider.getValue()));

        // Add components to the frame
        frame.add(label);
        frame.add(heightSlider);
        frame.add(updateButton);
        frame.add(flattenButton);
        frame.add(resetButton);
        frame.add(endButton);

        // Show GUI
        frame.setVisible(true);
    }
}
