import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PlayerSettings extends JDialog {
    private JComboBox<String>[] difficultyDropdowns;
    private JComboBox<String>[] colorDropdowns;
    private BobbleheadPanel[] bobbleheads;
    private int playerCount;
    private boolean saved = false;

    public PlayerSettings(JFrame parent, int playerCount, JComboBox<String>[] difficultyDropdowns, 
                          JComboBox<String>[] colorDropdowns, BobbleheadPanel[] bobbleheads) {
        super(parent, "Game Settings", true);
        this.setSize(600, 400);
        this.setLocationRelativeTo(parent);
        
        this.playerCount = playerCount;
        this.difficultyDropdowns = difficultyDropdowns;
        this.colorDropdowns = colorDropdowns;
        this.bobbleheads = bobbleheads;
        
        initUI();
    }
    
    private void initUI() {
        // Create player settings panel
        JPanel playerSettingsPanel = new JPanel(new GridLayout(5, 1, 5, 10));
        playerSettingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create player settings panels with bobblehead customization
        for (int i = 0; i < playerCount; i++) {
            JPanel playerPanel = new JPanel(new GridBagLayout());
            playerPanel.setBorder(BorderFactory.createTitledBorder("Player " + (i+1) + " Settings"));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            // Difficulty setting (only relevant for CPU players)
            JLabel difficultyLabel = new JLabel("Difficulty:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            playerPanel.add(difficultyLabel, gbc);
            
            gbc.gridx = 1;
            playerPanel.add(difficultyDropdowns[i], gbc);
            
            // Color selection
            JLabel colorLabel = new JLabel("Player Color:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            playerPanel.add(colorLabel, gbc);
            
            gbc.gridx = 1;
            playerPanel.add(colorDropdowns[i], gbc);
            
            // Add action listener to update bobblehead color when player color changes
            final int playerIndex = i;
            colorDropdowns[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedColor = (String) colorDropdowns[playerIndex].getSelectedItem();
                    Color newColor = getColorFromString(selectedColor);
                    bobbleheads[playerIndex].setColor(newColor);
                }
            });
            
            playerSettingsPanel.add(playerPanel);
        }
        
        // Add buttons at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        // Save button action
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Apply settings and update bobblehead colors
                for (int i = 0; i < playerCount; i++) {
                    String selectedColor = (String) colorDropdowns[i].getSelectedItem();
                    bobbleheads[i].setColor(getColorFromString(selectedColor));
                }
                saved = true;
                dispose();
            }
        });
        
        // Cancel button action
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add components to dialog
        setLayout(new BorderLayout());
        add(playerSettingsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // Helper method to convert color string to Color object
    private Color getColorFromString(String colorName) {
        switch (colorName) {
            case "Blue": return new Color(30, 144, 255);
            case "Red": return new Color(220, 20, 60);
            case "Green": return new Color(50, 205, 50);
            case "Orange": return new Color(255, 165, 0);
            case "Purple": return new Color(128, 0, 128);
            case "Black": return new Color(30, 30, 30);
            default: return Color.GRAY;
        }
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    // For simplicity, assume we have access to the BobbleheadPanel class
    // You will need to make this class public in your KiviGame file or
    // move it to a separate file as well if you want to fully modularize
    public interface BobbleheadPanel {
        void setColor(Color color);
    }
}