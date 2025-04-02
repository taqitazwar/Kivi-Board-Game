import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PlayerSettings extends JDialog {
    private JComboBox<String>[] difficultyDropdowns;
    private JComboBox<String>[] colorDropdowns;
    private BobbleheadPanel[] bobbleheads;
    private boolean[] isHuman;  // New field to track if player is human
    private int playerCount;
    private boolean saved = false;

    // Updated constructor to also receive the isHuman array.
    public PlayerSettings(JFrame parent, int playerCount, JComboBox<String>[] difficultyDropdowns, 
                          JComboBox<String>[] colorDropdowns, BobbleheadPanel[] bobbleheads, boolean[] isHuman) {
        super(parent, "Game Settings", true);
        this.setSize(600, 400);
        this.setLocationRelativeTo(parent);
        
        this.playerCount = playerCount;
        this.difficultyDropdowns = difficultyDropdowns;
        this.colorDropdowns = colorDropdowns;
        this.bobbleheads = bobbleheads;
        this.isHuman = isHuman; // Save the human/CPU flags
        
        initUI();
    }
    
    private void initUI() {
        JPanel playerSettingsPanel = new JPanel(new GridLayout(playerCount, 1, 5, 10));
        playerSettingsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        for (int i = 0; i < playerCount; i++) {
            JPanel playerPanel = new JPanel(new GridBagLayout());
            playerPanel.setBorder(BorderFactory.createTitledBorder("Player " + (i+1) + " Settings"));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);
            
            JLabel difficultyLabel = new JLabel("Difficulty:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 1;
            playerPanel.add(difficultyLabel, gbc);
            
            // If this player is human, disable the difficulty drop-down.
            difficultyDropdowns[i].setEnabled(!isHuman[i]);
            gbc.gridx = 1;
            playerPanel.add(difficultyDropdowns[i], gbc);
            
            JLabel colorLabel = new JLabel("Player Color:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            playerPanel.add(colorLabel, gbc);
            
            gbc.gridx = 1;
            playerPanel.add(colorDropdowns[i], gbc);
            
            final int playerIndex = i;
            colorDropdowns[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String selectedColor = (String) colorDropdowns[playerIndex].getSelectedItem();
                    Color newColor = getColorFromString(selectedColor);
                    bobbleheads[playerIndex].setColor(newColor);
                    updateColorOptions(); // Update available colors for all players
                }
            });
            
            playerSettingsPanel.add(playerPanel);
        }
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Ensure final update of colors before saving
                for (int i = 0; i < playerCount; i++) {
                    String selectedColor = (String) colorDropdowns[i].getSelectedItem();
                    bobbleheads[i].setColor(getColorFromString(selectedColor));
                }
                saved = true;
                dispose();
            }
        });
        
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        setLayout(new BorderLayout());
        add(playerSettingsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    // This method updates each color drop-down to remove colors that are already chosen by other players.
    private void updateColorOptions() {
        String[] allColors = {"Blue", "Red", "Green", "Orange", "Purple", "Black"};
        for (int i = 0; i < playerCount; i++) {
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            // Collect colors taken by other players.
            java.util.Set<String> taken = new java.util.HashSet<>();
            for (int j = 0; j < playerCount; j++) {
                if (j != i) {
                    Object sel = colorDropdowns[j].getSelectedItem();
                    if (sel != null) {
                        taken.add(sel.toString());
                    }
                }
            }
            String currentSelection = (colorDropdowns[i].getSelectedItem() != null) ? 
                                        colorDropdowns[i].getSelectedItem().toString() : "";
            for (String color : allColors) {
                if (!taken.contains(color) || color.equals(currentSelection)) {
                    model.addElement(color);
                }
            }
            colorDropdowns[i].setModel(model);
            colorDropdowns[i].setSelectedItem(currentSelection);
        }
    }
    
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
    
    public interface BobbleheadPanel {
        void setColor(Color color);
    }
}
