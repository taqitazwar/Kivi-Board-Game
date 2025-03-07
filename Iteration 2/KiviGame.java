import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class KiviGame extends JFrame {
    // Main components
    private JButton loadGameButton, onlineMultiplayerButton, instructionManualButton;
    private JButton startNewGameButton, settingsButton, creditsButton, exitGameButton;
    private JComboBox<String> timerDropdown;
    private JComboBox<String> playerCountDropdown;
    private JTextField[] playerNameFields = new JTextField[4];
    private JCheckBox[] humanCheckboxes = new JCheckBox[4];
    private JCheckBox[] cpuCheckboxes = new JCheckBox[4];
    private JComboBox<String>[] difficultyDropdowns = new JComboBox[4];
    private JComboBox<String>[] colorDropdowns = new JComboBox[4];
    private JPanel[] playerPanels = new JPanel[4];
    private BobbleheadPanel[] bobbleheads = new BobbleheadPanel[4]; // New array for bobbleheads

    public KiviGame() {
        // Basic frame setup
        super("KIVI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        
        // Use a single panel with null layout to match your design
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("Kivi by Group 3", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(250, 20, 300, 30);
        mainPanel.add(titleLabel);
        
        // Load Game and Online Multiplayer buttons
        loadGameButton = new JButton("Load Game");
        loadGameButton.setBounds(200, 70, 150, 30);
        mainPanel.add(loadGameButton);
        
        onlineMultiplayerButton = new JButton("Online Multiplayer");
        onlineMultiplayerButton.setBounds(450, 70, 150, 30);
        mainPanel.add(onlineMultiplayerButton);
        
        // Timer and Instruction Manual
        JLabel timerLabel = new JLabel("Timer");
        timerLabel.setBounds(200, 120, 50, 30);
        mainPanel.add(timerLabel);
        
        // Timer with specified options
        timerDropdown = new JComboBox<>(new String[]{"15 sec", "30 sec", "45 sec"});
        timerDropdown.setBounds(250, 120, 100, 30);
        mainPanel.add(timerDropdown);
        
        instructionManualButton = new JButton("Instruction Manual");
        instructionManualButton.setBounds(450, 120, 150, 30);
        mainPanel.add(instructionManualButton);
        
        // Player count selection
        JLabel playerCountLabel = new JLabel("Players:");
        playerCountLabel.setBounds(200, 170, 60, 30);
        mainPanel.add(playerCountLabel);
        
        playerCountDropdown = new JComboBox<>(new String[]{"2", "3", "4"});
        playerCountDropdown.setBounds(260, 170, 60, 30);
        playerCountDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateVisiblePlayers();
            }
        });
        mainPanel.add(playerCountDropdown);
        
        // Start New Game button
        startNewGameButton = new JButton("Start New Game");
        startNewGameButton.setBounds(325, 170, 150, 30);
        mainPanel.add(startNewGameButton);
        
        // Player panels - with human/CPU choice and bobbleheads
        createPlayerPanels(mainPanel);
        
        // Single Settings button (replacing separate player and display settings buttons)
        settingsButton = new JButton("Settings");
        settingsButton.setBounds(325, 470, 150, 30);
        settingsButton.addActionListener(e -> openSettingsDialog());
        mainPanel.add(settingsButton);
        
        creditsButton = new JButton("Credits");
        creditsButton.setBounds(325, 510, 150, 30);
        mainPanel.add(creditsButton);
        
        exitGameButton = new JButton("Exit Game");
        exitGameButton.setBounds(325, 550, 150, 30);
        exitGameButton.addActionListener(e -> System.exit(0));
        mainPanel.add(exitGameButton);
        
        // Add panel to frame
        setContentPane(mainPanel);
        setLocationRelativeTo(null);
        
        // Initialize with 2 players by default
        playerCountDropdown.setSelectedItem("2");
        updateVisiblePlayers();
        
        // Start bobblehead animation
        startBobbleheadAnimation();
    }
    
    private void updateVisiblePlayers() {
        int playerCount = Integer.parseInt((String) playerCountDropdown.getSelectedItem());
        
        // Show player panels based on selected count
        for (int i = 0; i < 4; i++) {
            boolean visible = i < playerCount;
            if (playerPanels[i] != null) {
                playerPanels[i].setVisible(visible);
            }
        }
    }
    
    private void createPlayerPanels(JPanel mainPanel) {
        // Properly spaced positions for player panels
        // Increased height to accommodate bobbleheads
        int panelWidth = 150;  // Increased width for bobblehead
        int panelHeight = 210; // Increased height
        int spacing = 20;      // Space between panels
        int totalWidth = (panelWidth * 4) + (spacing * 3);  // Total width of all panels with spacing
        int startX = (800 - totalWidth) / 2;  // Center the panels horizontally
        int yPosition = 220;
        
        // Create default colors for each player
        Color[] defaultColors = {
            new Color(30, 144, 255),  // Blue
            new Color(220, 20, 60),   // Red
            new Color(50, 205, 50),   // Green
            new Color(255, 165, 0)    // Orange
        };
        
        for (int i = 0; i < 4; i++) {
            // Calculate x position with proper spacing
            int xPosition = startX + (i * (panelWidth + spacing));
            
            // Create a panel for each player
            playerPanels[i] = new JPanel(null);
            playerPanels[i].setBounds(xPosition, yPosition, panelWidth, panelHeight);
            playerPanels[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            mainPanel.add(playerPanels[i]);
            
            // Player label
            JLabel playerLabel = new JLabel("Player " + (i+1), JLabel.CENTER);
            playerLabel.setBounds(0, 0, panelWidth, 30);
            playerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            playerPanels[i].add(playerLabel);
            
            // Add bobblehead panel
            bobbleheads[i] = new BobbleheadPanel(i, defaultColors[i]);
            bobbleheads[i].setBounds(25, 35, 100, 100);
            playerPanels[i].add(bobbleheads[i]);
            
            // Player name field - moved down to accommodate bobblehead
            playerNameFields[i] = new JTextField("Player " + (i+1));
            if (i == 0) {
                playerNameFields[i].setText("You");
            }
            playerNameFields[i].setBounds(10, 145, panelWidth-20, 25);
            playerPanels[i].add(playerNameFields[i]);
            
            // Human/CPU checkboxes - moved down further
            humanCheckboxes[i] = new JCheckBox("Human");
            humanCheckboxes[i].setBounds(10, 175, 70, 20);
            playerPanels[i].add(humanCheckboxes[i]);
            
            cpuCheckboxes[i] = new JCheckBox("CPU");
            cpuCheckboxes[i].setBounds(80, 175, 70, 20);
            playerPanels[i].add(cpuCheckboxes[i]);
            
            // Create color dropdowns for player colors
            colorDropdowns[i] = new JComboBox<>(new String[]{"Blue", "Red", "Green", "Orange", "Purple", "Black"});
            // Set default color based on player index
            if (i == 0) colorDropdowns[i].setSelectedItem("Blue");
            else if (i == 1) colorDropdowns[i].setSelectedItem("Red");
            else if (i == 2) colorDropdowns[i].setSelectedItem("Green");
            else if (i == 3) colorDropdowns[i].setSelectedItem("Orange");
            
            // Create difficulty dropdowns
            difficultyDropdowns[i] = new JComboBox<>(new String[]{"Easy", "Hard"});
            
            // Set default values
            if (i == 0) {
                humanCheckboxes[i].setSelected(true);
                humanCheckboxes[i].setEnabled(false);
                cpuCheckboxes[i].setEnabled(false);
            } else {
                cpuCheckboxes[i].setSelected(true);
            }
            
            // Set up mutual exclusivity for player types (except player 1)
            if (i > 0) {
                final int playerIndex = i;
                ActionListener playerTypeListener = new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (e.getSource() == humanCheckboxes[playerIndex]) {
                            cpuCheckboxes[playerIndex].setSelected(!humanCheckboxes[playerIndex].isSelected());
                            // Update bobblehead based on player type
                            bobbleheads[playerIndex].setCpu(cpuCheckboxes[playerIndex].isSelected());
                        } else {
                            humanCheckboxes[playerIndex].setSelected(!cpuCheckboxes[playerIndex].isSelected());
                            // Update bobblehead based on player type
                            bobbleheads[playerIndex].setCpu(cpuCheckboxes[playerIndex].isSelected());
                        }
                    }
                };
                
                humanCheckboxes[i].addActionListener(playerTypeListener);
                cpuCheckboxes[i].addActionListener(playerTypeListener);
            }
            
            // Set CPU flag based on initial selection
            bobbleheads[i].setCpu(cpuCheckboxes[i].isSelected());
        }
    }
    
    // Start the animation for all bobbleheads
    private void startBobbleheadAnimation() {
        Timer timer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (BobbleheadPanel bobblehead : bobbleheads) {
                    if (bobblehead != null && bobblehead.isVisible()) {
                        bobblehead.updateAnimation();
                        bobblehead.repaint();
                    }
                }
            }
        });
        timer.start();
    }
    
    // New method to open the unified settings dialog
    private void openSettingsDialog() {
        // Create a unified settings dialog
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setSize(300, 150);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setLayout(new BorderLayout());
        
        // Create panel for settings options
        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create buttons for each setting type
        JButton playerSettingsButton = new JButton("Player Settings");
        playerSettingsButton.addActionListener(e -> {
            settingsDialog.dispose();
            showPlayerSettings();
        });
        
        JButton displaySettingsButton = new JButton("Display Settings");
        displaySettingsButton.addActionListener(e -> {
            settingsDialog.dispose();
            openDisplaySettings();
        });
        
        // Add buttons to panel
        optionsPanel.add(playerSettingsButton);
        optionsPanel.add(displaySettingsButton);
        
        // Add panel to dialog
        settingsDialog.add(optionsPanel, BorderLayout.CENTER);
        
        // Show dialog
        settingsDialog.setVisible(true);
    }
    
    // Method to show the separate PlayerSettings dialog
    private void showPlayerSettings() {
        int playerCount = Integer.parseInt((String) playerCountDropdown.getSelectedItem());
        PlayerSettings playerSettings = new PlayerSettings(
            this, 
            playerCount, 
            difficultyDropdowns, 
            colorDropdowns, 
            bobbleheads
        );
        playerSettings.setVisible(true);
    }
    
    // Method to open the separate DisplaySettings window
    private void openDisplaySettings() {
        // Simply create a new instance of DisplaySettings
        DisplaySettings displaySettings = new DisplaySettings();
    }
    
    // Custom panel for drawing bobbleheads - made public to be accessible from PlayerSettings
    public static class BobbleheadPanel extends JPanel implements PlayerSettings.BobbleheadPanel {
        private Color color;
        private int playerId;
        private boolean isCpu;
        private double bobbleAngle = 0;
        private double bobbleSpeed = 0.1;
        private double bobbleMagnitude = 5;

        public BobbleheadPanel(int playerId, Color color) {
            this.playerId = playerId;
            this.color = color;
            this.setOpaque(false);
        }
        
        public void setCpu(boolean isCpu) {
            this.isCpu = isCpu;
            repaint();
        }
        
        public Color getColor() {
            return color;
        }
        
        @Override
        public void setColor(Color color) {
            this.color = color;
            repaint();
        }
        
        public void updateAnimation() {
            bobbleAngle += bobbleSpeed;
            if (bobbleAngle > 2 * Math.PI) {
                bobbleAngle -= 2 * Math.PI;
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // For smoother graphics
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Calculate bobble effect for head
            double offsetX = Math.sin(bobbleAngle) * bobbleMagnitude;
            double offsetY = Math.cos(bobbleAngle) * (bobbleMagnitude / 2);
            
            // Draw body (smaller for bobblehead effect)
            int bodyWidth = width / 3;
            int bodyHeight = height / 3;
            int bodyX = (width - bodyWidth) / 2;
            int bodyY = height - bodyHeight;
            
            // Body color slightly darker than head
            Color bodyColor = color.darker();
            g2d.setColor(bodyColor);
            
            // Human or CPU specific body
            if (isCpu) {
                // CPU: Draw a robot-like body
                g2d.fillRect(bodyX, bodyY, bodyWidth, bodyHeight);
                
                // Draw some circuit lines or patterns on the body
                g2d.setColor(Color.GRAY);
                g2d.drawLine(bodyX + 5, bodyY + 5, bodyX + bodyWidth - 5, bodyY + 5);
                g2d.drawLine(bodyX + 5, bodyY + bodyHeight/2, bodyX + bodyWidth - 5, bodyY + bodyHeight/2);
                g2d.drawRect(bodyX + bodyWidth/3, bodyY + 8, bodyWidth/3, bodyHeight/3);
            } else {
                // Human: Draw a more human-like body
                g2d.fillRoundRect(bodyX, bodyY, bodyWidth, bodyHeight, 8, 8);
                
                // Draw a shirt collar
                g2d.setColor(Color.WHITE);
                g2d.fillArc(bodyX, bodyY, bodyWidth, bodyHeight/3, 0, 180);
            }
            
            // Draw neck
            int neckWidth = width / 6;
            int neckHeight = height / 10;
            int neckX = (width - neckWidth) / 2;
            int neckY = bodyY - neckHeight;
            
            g2d.setColor(new Color(255, 222, 173)); // Neck color
            g2d.fillRect(neckX, neckY, neckWidth, neckHeight);
            
            // Draw head (larger for bobblehead effect)
            int headDiameter = width / 2 + 10;
            int headX = (width - headDiameter) / 2 + (int)offsetX;
            int headY = neckY - headDiameter + 5 + (int)offsetY;
            
            // Head
            g2d.setColor(color);
            g2d.fillOval(headX, headY, headDiameter, headDiameter);
            
            // Face - different for human vs CPU
            if (isCpu) {
                // CPU: Robot face with display screen
                g2d.setColor(Color.BLACK);
                int faceSize = headDiameter * 2/3;
                int faceX = headX + (headDiameter - faceSize) / 2;
                int faceY = headY + (headDiameter - faceSize) / 2;
                g2d.fillRect(faceX, faceY, faceSize, faceSize);
                
                // Display screen
                g2d.setColor(Color.GREEN);
                g2d.fillRect(faceX + 2, faceY + 2, faceSize - 4, faceSize - 4);
                
                // Eyes (digital)
                g2d.setColor(Color.RED);
                g2d.fillRect(faceX + faceSize/4 - 2, faceY + faceSize/3, 4, 4);
                g2d.fillRect(faceX + faceSize*3/4 - 2, faceY + faceSize/3, 4, 4);
                
                // Mouth (digital)
                g2d.drawLine(faceX + faceSize/4, faceY + faceSize*2/3, 
                             faceX + faceSize*3/4, faceY + faceSize*2/3);
            } else {
                // Human face
                // Eyes
                g2d.setColor(Color.WHITE);
                int eyeSize = headDiameter / 6;
                g2d.fillOval(headX + headDiameter/3 - eyeSize/2, headY + headDiameter/3, eyeSize, eyeSize);
                g2d.fillOval(headX + 2*headDiameter/3 - eyeSize/2, headY + headDiameter/3, eyeSize, eyeSize);
                
                // Pupils
                g2d.setColor(Color.BLACK);
                int pupilSize = eyeSize / 2;
                g2d.fillOval(headX + headDiameter/3 - pupilSize/2, headY + headDiameter/3 + eyeSize/4, pupilSize, pupilSize);
                g2d.fillOval(headX + 2*headDiameter/3 - pupilSize/2, headY + headDiameter/3 + eyeSize/4, pupilSize, pupilSize);
                
                // Mouth
                int mouthWidth = headDiameter / 3;
                int mouthHeight = headDiameter / 10;
                g2d.setColor(Color.RED);
                g2d.fillArc(headX + (headDiameter - mouthWidth)/2, 
                           headY + 2*headDiameter/3, 
                           mouthWidth, mouthHeight, 0, 180);
            }
            
            // Player number
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            String playerText = "P" + (playerId + 1);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(playerText);
            g2d.drawString(playerText, 
                          (width - textWidth) / 2, 
                          height - 5);
        }
    }
    
    public static void main(String[] args) {
        // Try to set native look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Show the frame
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new KiviGame().setVisible(true);
            }
        });
    }
}