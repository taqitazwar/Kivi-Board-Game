import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class KiviGame extends JFrame {
    // UI components
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
    private BobbleheadPanel[] bobbleheads = new BobbleheadPanel[4];
    private Timer animationTimer;

    public KiviGame() {
        super("KIVI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 650);
        initUI();
        setLocationRelativeTo(null);

        // Initialize with 2 players by default
        playerCountDropdown.setSelectedItem("2");
        updateVisiblePlayers();

        // Start bobblehead animation
        startBobbleheadAnimation();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(null);
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Kivi by Group 3", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(250, 20, 300, 30);
        mainPanel.add(titleLabel);

        loadGameButton = new JButton("Load Game");
        loadGameButton.setBounds(200, 70, 150, 30);
        mainPanel.add(loadGameButton);

        onlineMultiplayerButton = new JButton("Online Multiplayer");
        onlineMultiplayerButton.setBounds(450, 70, 150, 30);
        mainPanel.add(onlineMultiplayerButton);

        JLabel timerLabel = new JLabel("Timer");
        timerLabel.setBounds(200, 120, 50, 30);
        mainPanel.add(timerLabel);

        timerDropdown = new JComboBox<>(new String[] { "15 sec", "30 sec", "45 sec" });
        timerDropdown.setBounds(250, 120, 100, 30);
        mainPanel.add(timerDropdown);

        instructionManualButton = new JButton("Instruction Manual");
        instructionManualButton.setBounds(450, 120, 150, 30);
        // Add an ActionListener to open the instruction manual
        instructionManualButton.addActionListener(e -> openInstructionManual());
        mainPanel.add(instructionManualButton);

        JLabel playerCountLabel = new JLabel("Players:");
        playerCountLabel.setBounds(200, 170, 60, 30);
        mainPanel.add(playerCountLabel);

        playerCountDropdown = new JComboBox<>(new String[] { "2", "3", "4" });
        playerCountDropdown.setBounds(260, 170, 60, 30);
        playerCountDropdown.addActionListener(e -> updateVisiblePlayers());
        mainPanel.add(playerCountDropdown);

        startNewGameButton = new JButton("Start New Game");
        startNewGameButton.setBounds(325, 170, 150, 30);
        startNewGameButton.addActionListener(e -> startNewGame());
        mainPanel.add(startNewGameButton);

        createPlayerPanels(mainPanel);

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

        setContentPane(mainPanel);
    }

    private void updateVisiblePlayers() {
        int count = Integer.parseInt((String) playerCountDropdown.getSelectedItem());
        for (int i = 0; i < 4; i++) {
            if (playerPanels[i] != null) {
                playerPanels[i].setVisible(i < count);
            }
        }
    }

    private void createPlayerPanels(JPanel mainPanel) {
        int panelWidth = 150;
        int panelHeight = 210;
        int spacing = 20;
        int totalWidth = (panelWidth * 4) + (spacing * 3);
        int startX = (800 - totalWidth) / 2;
        int yPosition = 220;

        Color[] defaultColors = {
                new Color(30, 144, 255),
                new Color(220, 20, 60),
                new Color(50, 205, 50),
                new Color(255, 165, 0)
        };

        for (int i = 0; i < 4; i++) {
            int xPosition = startX + (i * (panelWidth + spacing));
            playerPanels[i] = new JPanel(null);
            playerPanels[i].setBounds(xPosition, yPosition, panelWidth, panelHeight);
            playerPanels[i].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            mainPanel.add(playerPanels[i]);

            JLabel playerLabel = new JLabel("Player " + (i + 1), JLabel.CENTER);
            playerLabel.setBounds(0, 0, panelWidth, 30);
            playerLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            playerPanels[i].add(playerLabel);

            bobbleheads[i] = new BobbleheadPanel(i, defaultColors[i]);
            bobbleheads[i].setBounds(25, 35, 100, 100);
            playerPanels[i].add(bobbleheads[i]);

            playerNameFields[i] = new JTextField("Player " + (i + 1));
            if (i == 0) {
                playerNameFields[i].setText("You");
            }
            playerNameFields[i].setBounds(10, 145, panelWidth - 20, 25);
            playerPanels[i].add(playerNameFields[i]);

            humanCheckboxes[i] = new JCheckBox("Human");
            humanCheckboxes[i].setBounds(10, 175, 70, 20);
            playerPanels[i].add(humanCheckboxes[i]);

            cpuCheckboxes[i] = new JCheckBox("CPU");
            cpuCheckboxes[i].setBounds(80, 175, 70, 20);
            playerPanels[i].add(cpuCheckboxes[i]);

            colorDropdowns[i] = new JComboBox<>(new String[] { "Blue", "Red", "Green", "Orange", "Purple", "Black" });
            if (i == 0)
                colorDropdowns[i].setSelectedItem("Blue");
            else if (i == 1)
                colorDropdowns[i].setSelectedItem("Red");
            else if (i == 2)
                colorDropdowns[i].setSelectedItem("Green");
            else if (i == 3)
                colorDropdowns[i].setSelectedItem("Orange");

            difficultyDropdowns[i] = new JComboBox<>(new String[] { "Easy", "Hard" });

            if (i == 0) {
                humanCheckboxes[i].setSelected(true);
                humanCheckboxes[i].setEnabled(false);
                cpuCheckboxes[i].setEnabled(false);
            } else {
                cpuCheckboxes[i].setSelected(true);
            }

            if (i > 0) {
                final int idx = i;
                ActionListener listener = e -> {
                    if (e.getSource() == humanCheckboxes[idx]) {
                        cpuCheckboxes[idx].setSelected(!humanCheckboxes[idx].isSelected());
                    } else {
                        humanCheckboxes[idx].setSelected(!cpuCheckboxes[idx].isSelected());
                    }
                    bobbleheads[idx].setCpu(cpuCheckboxes[idx].isSelected());
                };
                humanCheckboxes[i].addActionListener(listener);
                cpuCheckboxes[i].addActionListener(listener);
            }
            bobbleheads[i].setCpu(cpuCheckboxes[i].isSelected());
        }
    }

    private void startNewGame() {
        int playerCount = Integer.parseInt((String) playerCountDropdown.getSelectedItem());
        String[] playerNames = new String[playerCount];
        Color[] playerColors = new Color[playerCount];
        boolean[] isHuman = new boolean[playerCount];

        for (int i = 0; i < playerCount; i++) {
            playerNames[i] = playerNameFields[i].getText();
            isHuman[i] = humanCheckboxes[i].isSelected();
            String colorName = (String) colorDropdowns[i].getSelectedItem();
            playerColors[i] = getColorFromName(colorName);
        }

        String timeString = (String) timerDropdown.getSelectedItem();
        int turnTime = Integer.parseInt(timeString.split(" ")[0]);

        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new KiviGameplay(playerCount, playerNames, playerColors, isHuman, turnTime));
        this.setVisible(false);
    }

    private Color getColorFromName(String colorName) {
        switch (colorName) {
            case "Blue":
                return new Color(30, 144, 255);
            case "Red":
                return new Color(220, 20, 60);
            case "Green":
                return new Color(50, 205, 50);
            case "Orange":
                return new Color(255, 165, 0);
            case "Purple":
                return new Color(128, 0, 128);
            case "Black":
                return Color.BLACK;
            default:
                return Color.BLUE;
        }
    }

    private void openSettingsDialog() {
        JDialog settingsDialog = new JDialog(this, "Settings", true);
        settingsDialog.setSize(300, 150);
        settingsDialog.setLocationRelativeTo(this);
        settingsDialog.setLayout(new BorderLayout());

        JPanel optionsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        optionsPanel.add(playerSettingsButton);
        optionsPanel.add(displaySettingsButton);

        settingsDialog.add(optionsPanel, BorderLayout.CENTER);
        settingsDialog.setVisible(true);
    }

    private void showPlayerSettings() {
        int count = Integer.parseInt((String) playerCountDropdown.getSelectedItem());
        boolean[] currentIsHuman = new boolean[count];
        for (int i = 0; i < count; i++) {
            currentIsHuman[i] = humanCheckboxes[i].isSelected();
        }
        PlayerSettings ps = new PlayerSettings(this, count, difficultyDropdowns, colorDropdowns, bobbleheads,
                currentIsHuman);
        ps.setVisible(true);
    }

    private void openDisplaySettings() {
        new DisplaySettings();
    }

    private void startBobbleheadAnimation() {
        animationTimer = new Timer(50, e -> {
            for (BobbleheadPanel bobblehead : bobbleheads) {
                if (bobblehead != null && bobblehead.isVisible()) {
                    bobblehead.updateAnimation();
                    bobblehead.repaint();
                }
            }
        });
        animationTimer.start();
    }

    @Override
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        super.dispose();
    }

    /**
     * Opens a modal dialog containing the instruction manual for the game.
     */
    private void openInstructionManual() {
        // Create a modal dialog
        JDialog instructionDialog = new JDialog(this, "Instruction Manual", true);
        instructionDialog.setSize(600, 400);
        instructionDialog.setLocationRelativeTo(this);
        instructionDialog.setLayout(new BorderLayout());

        // Create a read-only text area to display the rules
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        // Set the game rules text
        textArea.setText(
                "Kivi draws inspiration from the classic dice game Yachtzee. The game combines\n"
                        + "Yachtzee's dice with a 7×7 board of 49 squares. Each player has ten stones.\n"
                        + "Unlike Yachtzee (5 dice), Kivi uses 6 dice.\n\n"
                        + "On a turn, a player rolls all 6 dice (with up to two partial or full rethrows)\n"
                        + "and must place a stone on a square matching the final dice combination.\n"
                        + "Squares have different colors:\n"
                        + " - Pink squares: 3 points\n"
                        + " - Black squares: 2 points\n"
                        + " - White squares: 1 point\n\n"

                        + "Combinations:\n"
                        + "• Two pairs (1 pt)\n"
                        + "• Three of a kind (1 pt)\n"
                        + "• Little straight (4 dice consecutive) (1 pt)\n"
                        + "• Full house (3 of a kind + pair) (1 pt)\n"
                        + "• Four of a kind (2 pts)\n"
                        + "• Large straight (5 dice consecutive) (2 pts)\n"
                        + "• All even (2 pts)\n"
                        + "• All odd (2 pts)\n"
                        + "• 12 or fewer (sum ≤ 12) (2 pts)\n"
                        + "• 30 or more (sum ≥ 30) (2 pts)\n"
                        + "• Three pairs (3 pts)\n"
                        + "• Two times three of a kind (3 pts)\n"
                        + "• Four of a kind + pair (3 pts)\n\n"
                        + "Special Combinations:\n"
                        + "• Five of a kind or 6-dice straight (1–6): place on any free square.\n"
                        + "• Six of a kind: place on any square (even occupied, relocating stone if needed).\n\n"
                        + "If no valid square is available, the stone is returned to the box.\n\n"
                        + "Scoring:\n"
                        + "After 10 rounds, stones on the board are scored. Contiguous rows (horizontal or\n"
                        + "vertical) score as (sum of square points) × (length of row). Highest total wins.\n"
                        + "taken from https://en.everybodywiki.com/Kivi_(board_game)\n");

        // Put the text area in a scroll pane
        JScrollPane scrollPane = new JScrollPane(textArea);
        instructionDialog.add(scrollPane, BorderLayout.CENTER);

        instructionDialog.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new KiviGame().setVisible(true));
    }
}
