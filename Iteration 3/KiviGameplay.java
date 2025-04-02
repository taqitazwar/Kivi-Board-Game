import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class KiviGameplay extends JFrame {
    // Game board components
    private JPanel gameBoard;
    private JButton[][] boardSquares;
    private JPanel dicePanel;
    private JButton[] dice;
    private JButton rollDiceButton, endTurnButton;
    private JButton pauseButton, resumeButton, saveButton; // Added saveButton
    private JPanel playerInfoPanel;
    private JLabel currentPlayerLabel, timerLabel;

    // Game state variables
    private int currentPlayer = 0;
    private int playerCount;
    private String[] playerNames;
    private Color[] playerColors;
    private boolean[] isHuman;
    private int[] stonesLeft;
    private int turnTimeTotal; // total time for a turn in seconds
    private int[] playerScores;

    // Timer variables
    private Timer gameTimer;
    private long turnStartTime; // records the system time (in ms) when a turn starts

    // *** DICE LOGIC EXTRACTED TO A NEW CLASS! ***
    private RollADice diceLogic;

    // *** NEW CLASS for piece placement logic ***
    private PlaceAPiece placeLogic;

    // Board constants
    private final int BOARD_SIZE = 7;
    private final String[] SQUARE_TYPES = {
            "AA/BB", "ABCDE", "≤12", "AAA", "=1,3,5", "=2,4,6", "AAA",
            "=2,4,6", "AAAA/BB", "AAA", "AA/BB/CC", "ABCD", "AAA/BBB", "≥30",
            "ABCD", "AAAA", "≥30", "ABCDE", "AAAA/BB", "=1,3,5", "AAA/BB",
            "≤12", "AAA/BB", "=2,4,6", "AAA/BBB", "≤12", "AA/BB", "ABCDE",
            "AAA", "ABCDE", "AA/BB/CC", "=1,3,5", "AAAA", "≥30", "AA/BB",
            "=1,3,5", "AAA/BBB", "ABCD", "AAAA/BB", "AAA/BB", "AA/BB/CC", "≤12",
            "ABCD", "≥30", "AAAA", "AA/BB", "=1,3,5", "AAAA", "AAA/BB"
    };

    // To store each square's base color so we can reset after highlighting
    private Color[] originalColors;

    // Colors for squares based on points
    private final Color WHITE_SQUARE = Color.WHITE;
    private final Color PINK_SQUARE = new Color(255, 182, 193);
    private final Color HOT_PINK_SQUARE = new Color(255, 105, 180);

    // Variables to hold the currently placed (tentative) stone
    private int currentStoneRow = -1;
    private int currentStoneCol = -1;
    private StonePanel currentStone = null;

    // Pause flag
    private boolean isPaused = false;

    public KiviGameplay(int playerCount, String[] playerNames, Color[] playerColors, boolean[] isHuman, int turnTime) {
        super("KIVI - Game");
        this.playerCount = playerCount;
        this.playerNames = playerNames;
        this.playerColors = playerColors;
        this.isHuman = isHuman;
        this.turnTimeTotal = turnTime;

        // Each player starts with 10 stones
        stonesLeft = new int[playerCount];
        playerScores = new int[playerCount];
        for (int i = 0; i < playerCount; i++) {
            stonesLeft[i] = 10;
            playerScores[i] = 0;
        }

        // Instantiate dice logic
        diceLogic = new RollADice();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 700);
        setLayout(new BorderLayout(10, 10));

        createGameBoard();
        createDicePanel();
        createPlayerInfoPanel();

        // Instantiate piece placement logic AFTER the board is ready
        placeLogic = new PlaceAPiece(BOARD_SIZE, boardSquares, SQUARE_TYPES);

        startGame();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Returns points based on the square type.
    private int getPointsForSquareType(String squareType) {
        switch (squareType) {
            case "AA/BB":
            case "AAA":
            case "ABCD":
            case "AAA/BB":
                return 1;
            case "AAAA":
            case "ABCDE":
            case "≤12":
            case "≥30":
                return 2;
            case "AA/BB/CC":
            case "AAA/BBB":
            case "AAAA/BB":
                return 3;
            default:
                return 0;
        }
    }

    private void createGameBoard() {
        gameBoard = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE, 2, 2));
        gameBoard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        boardSquares = new JButton[BOARD_SIZE][BOARD_SIZE];
        originalColors = new Color[BOARD_SIZE * BOARD_SIZE];

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int index = i * BOARD_SIZE + j;
                String squareType = SQUARE_TYPES[index];
                int points = getPointsForSquareType(squareType);
                Color squareColor;
                if (points == 1 || points == 0) {
                    squareColor = WHITE_SQUARE;
                } else if (points == 2) {
                    squareColor = PINK_SQUARE;
                } else {
                    squareColor = HOT_PINK_SQUARE;
                }
                originalColors[index] = squareColor;

                JButton square = new JButton();
                square.setPreferredSize(new Dimension(80, 80));
                square.setBackground(DisplaySettings.ColorBlindnessFilter.transformColor(squareColor));
                square.setOpaque(true);
                square.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                square.setFocusPainted(false);

                square.setLayout(new BorderLayout());
                JLabel typeLabel = new JLabel(squareType, JLabel.CENTER);
                typeLabel.setFont(new Font("Arial", Font.BOLD, 12));
                square.add(typeLabel, BorderLayout.CENTER);

                final int row = i;
                final int col = j;
                // Instead of "attemptPlacePiece(row, col)", we do the new logic:
                square.addActionListener(e -> {
                    // If it's not the player's turn or no roll has happened, skip
                    if (!isHuman[currentPlayer] || diceLogic.getRollCount() == 0) {
                        return;
                    }
                    // Gather the dice the player selected
                    ArrayList<Integer> selectedValues = new ArrayList<>();
                    boolean[] selected = diceLogic.getDiceSelected();
                    int[] values = diceLogic.getDiceValues();
                    for (int k = 0; k < 6; k++) {
                        if (selected[k]) {
                            selectedValues.add(values[k]);
                        }
                    }

                    // If we have no stone yet, create one
                    if (currentStone == null) {
                        currentStone = new StonePanel(playerColors[currentPlayer]);
                        currentStone.setPreferredSize(new Dimension(40, 40));
                    }

                    // Attempt placement via PlaceAPiece
                    boolean placed = placeLogic.attemptPlacePiece(
                            this, // Pass the JFrame reference for JOptionPane
                            row, col,
                            currentStoneRow, // Where the stone currently is
                            currentStoneCol,
                            currentStone,
                            selectedValues,
                            isHuman[currentPlayer]);
                    if (placed) {
                        // If successful, update these
                        currentStoneRow = row;
                        currentStoneCol = col;
                        endTurnButton.setEnabled(true);
                    }
                });

                boardSquares[i][j] = square;
                gameBoard.add(square);
            }
        }

        add(gameBoard, BorderLayout.CENTER);
    }

    private void createDicePanel() {
        dicePanel = new JPanel(new FlowLayout());

        dice = new JButton[6];
        for (int i = 0; i < 6; i++) {
            dice[i] = new JButton("?");
            dice[i].setPreferredSize(new Dimension(60, 60));
            dice[i].setFont(new Font("Arial", Font.BOLD, 20));
            final int dieIndex = i;

            // Toggling a die means we call diceLogic.toggleDieSelection() + UI update
            dice[i].addActionListener(e -> toggleDieSelection(dieIndex));
            dicePanel.add(dice[i]);
        }

        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.addActionListener(e -> rollDice());
        dicePanel.add(rollDiceButton);

        endTurnButton = new JButton("End Turn");
        endTurnButton.setEnabled(false);
        endTurnButton.addActionListener(e -> endTurn());
        dicePanel.add(endTurnButton);

        pauseButton = new JButton("Pause");
        pauseButton.addActionListener(e -> pauseGame());
        dicePanel.add(pauseButton);

        resumeButton = new JButton("Resume");
        resumeButton.addActionListener(e -> resumeGame());
        resumeButton.setEnabled(false);
        dicePanel.add(resumeButton);

        // Add the "Save" button
        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            // Placeholder for save functionality
            System.out.println("Save button clicked. Functionality not implemented yet.");
        });
        dicePanel.add(saveButton);

        add(dicePanel, BorderLayout.SOUTH);
    }

    private void createPlayerInfoPanel() {
        playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.Y_AXIS));
        playerInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        currentPlayerLabel = new JLabel("Current Player: " + playerNames[currentPlayer]);
        currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        playerInfoPanel.add(currentPlayerLabel);

        playerInfoPanel.add(Box.createVerticalStrut(10));

        // Initially show the total time for the turn.
        timerLabel = new JLabel("Time left: " + turnTimeTotal + "s");
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        playerInfoPanel.add(timerLabel);

        playerInfoPanel.add(Box.createVerticalStrut(20));

        updatePlayerStats();

        add(playerInfoPanel, BorderLayout.EAST);
    }

    // Update player statistics without recreating the time label
    private void updatePlayerStats() {
        // Remove only player stats components (keep currentPlayerLabel and timerLabel)
        Component[] components = playerInfoPanel.getComponents();
        for (int i = components.length - 1; i >= 0; i--) {
            Component comp = components[i];
            if (!(comp == currentPlayerLabel || comp == timerLabel || comp instanceof Box.Filler)) {
                playerInfoPanel.remove(comp);
            }
        }

        // Add updated player stats
        for (int i = 0; i < playerCount; i++) {
            JPanel playerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel nameLabel = new JLabel(playerNames[i]
                    + " - Stones: " + stonesLeft[i]
                    + " | Score: " + playerScores[i]);
            nameLabel.setForeground(playerColors[i]);
            playerPanel.add(nameLabel);
            playerInfoPanel.add(playerPanel);
        }

        playerInfoPanel.revalidate();
        playerInfoPanel.repaint();
    }

    // Starts a turn by recording the start time, resetting dice, etc.
    private void startTurn() {
        // Reset turn time by recording current time
        turnStartTime = System.currentTimeMillis();

        // Reset dice logic for new turn
        diceLogic.resetForNextTurn();

        // Clear out dice UI
        for (int i = 0; i < 6; i++) {
            dice[i].setText("?");
            dice[i].setBackground(null);
            dice[i].setEnabled(true);
        }

        // If a stone was partially placed last turn, remove it
        if (currentStone != null && currentStoneRow >= 0 && currentStoneCol >= 0) {
            boardSquares[currentStoneRow][currentStoneCol].remove(currentStone);
            boardSquares[currentStoneRow][currentStoneCol].revalidate();
            boardSquares[currentStoneRow][currentStoneCol].repaint();
            currentStone = null;
            currentStoneRow = -1;
            currentStoneCol = -1;
        }

        currentPlayerLabel.setText("Current Player: " + playerNames[currentPlayer]);
        rollDiceButton.setEnabled(true);
        endTurnButton.setEnabled(false);

        // Set timer label to show full turn time initially
        timerLabel.setText("Time left: " + turnTimeTotal + ".0s");

        // Create and start a timer that ticks every 100ms for smooth updates.
        if (gameTimer != null) {
            gameTimer.stop();
        }

        gameTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - turnStartTime;
                int remainingSec = turnTimeTotal - (int) (elapsed / 1000);
                int dec = (int) ((elapsed % 1000) / 100);
                if (remainingSec < 0) {
                    remainingSec = 0;
                }
                timerLabel.setText("Time left: " + remainingSec + "." + dec + "s");
                if (elapsed >= turnTimeTotal * 1000) {
                    gameTimer.stop();
                    endTurn();
                }
            }
        });
        gameTimer.start();

        // Run CPU turn on a separate thread so dice changes are visible
        if (!isHuman[currentPlayer]) {
            new Thread(() -> handleCpuTurn()).start();
        }
    }

    private void startGame() {
        currentPlayer = 0;
        startTurn();
    }

    private void rollDice() {
        if (isPaused)
            return;

        // Remove current stone if it exists
        if (currentStone != null && currentStoneRow >= 0 && currentStoneCol >= 0) {
            boardSquares[currentStoneRow][currentStoneCol].remove(currentStone);
            boardSquares[currentStoneRow][currentStoneCol].revalidate();
            boardSquares[currentStoneRow][currentStoneCol].repaint();
            currentStone = null;
            currentStoneRow = -1;
            currentStoneCol = -1;
        }

        // Ask diceLogic to do the actual rolling
        diceLogic.rollDice();

        // Update the on-screen dice
        updateDiceUI();

        // Highlight squares that match new dice
        highlightValidMoves();
    }

    /**
     * Update the dice buttons to match the state in diceLogic.
     * This method is called after rolling or toggling a die.
     */
    private void updateDiceUI() {
        int[] values = diceLogic.getDiceValues();
        boolean[] selected = diceLogic.getDiceSelected();

        for (int i = 0; i < 6; i++) {
            // If dice haven't been rolled yet, show "?"
            if (values[i] == 0 && diceLogic.getRollCount() == 0) {
                dice[i].setText("?");
            } else {
                dice[i].setText(String.valueOf(values[i]));
            }
            // Color background if selected
            if (selected[i]) {
                dice[i].setBackground(playerColors[currentPlayer]);
            } else {
                dice[i].setBackground(null);
            }
            // If we've rolled 3 times, disable dice from toggling
            dice[i].setEnabled(diceLogic.getRollCount() > 0 && diceLogic.getRollCount() < 3);
        }
        // If we've rolled 3 times, disable rollDice
        rollDiceButton.setEnabled(diceLogic.getRollCount() < 3);
    }

    private void toggleDieSelection(int dieIndex) {
        if (isPaused)
            return;
        if (diceLogic.getRollCount() == 0) {
            // Can't toggle if we haven't rolled
            return;
        }

        // Toggle the selection in diceLogic
        diceLogic.toggleDieSelection(dieIndex);

        // Update UI and highlight
        updateDiceUI();
        highlightValidMoves();
    }

    private void highlightValidMoves() {
        // Reset squares to original color
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                int index = i * BOARD_SIZE + j;
                boardSquares[i][j].setBackground(
                        DisplaySettings.ColorBlindnessFilter.transformColor(originalColors[index]));
            }
        }

        // If we haven't rolled yet, nothing to highlight
        if (diceLogic.getRollCount() == 0)
            return;

        // Build list of selected dice values
        ArrayList<Integer> selectedValues = new ArrayList<>();
        boolean[] selected = diceLogic.getDiceSelected();
        int[] values = diceLogic.getDiceValues();
        for (int i = 0; i < 6; i++) {
            if (selected[i]) {
                selectedValues.add(values[i]);
            }
        }
        if (selectedValues.isEmpty())
            return;

        // For each square, check if the combination is valid
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                // If there's already a stone here (and it's not the currentStone), skip
                if (boardSquares[i][j].getComponentCount() > 1 && !(i == currentStoneRow && j == currentStoneCol)) {
                    continue;
                }
                // Now call placeLogic to see if the combination is valid
                String squareType = SQUARE_TYPES[i * BOARD_SIZE + j];
                if (placeLogic.isValidPlacement(squareType, selectedValues)) {
                    boardSquares[i][j].setBackground(
                            DisplaySettings.ColorBlindnessFilter.transformColor(Color.GREEN.brighter()));
                }
            }
        }
    }

    private void endTurn() {
        if (gameTimer != null) {
            gameTimer.stop();
        }

        // If a piece is placed, calculate score
        if (currentStone != null && currentStoneRow >= 0 && currentStoneCol >= 0) {
            int index = currentStoneRow * BOARD_SIZE + currentStoneCol;
            String squareType = SQUARE_TYPES[index];

            // Gather the dice the player has selected
            boolean[] selected = diceLogic.getDiceSelected();
            int[] values = diceLogic.getDiceValues();
            ArrayList<Integer> selectedValues = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                if (selected[i]) {
                    selectedValues.add(values[i]);
                }
            }

            int points = calculatePoints(squareType, selectedValues);
            playerScores[currentPlayer] += points;
            stonesLeft[currentPlayer]--;
            currentStone = null;
            currentStoneRow = -1;
            currentStoneCol = -1;
        }

        // Disable dice
        for (int i = 0; i < 6; i++) {
            dice[i].setEnabled(false);
        }
        rollDiceButton.setEnabled(false);

        updatePlayerStats();
        currentPlayer = (currentPlayer + 1) % playerCount;
        if (isGameOver()) {
            endGame();
            return;
        }
        startTurn();
    }

    private int calculatePoints(String squareType, ArrayList<Integer> diceUsed) {
        switch (squareType) {
            case "AA/BB":
            case "AAA":
            case "ABCD":
            case "AAA/BB":
                return 1;
            case "AAAA":
            case "ABCDE":
            case "≤12":
            case "≥30":
                return 2;
            case "AA/BB/CC":
            case "AAA/BBB":
            case "AAAA/BB":
                return 3;
            default:
                return 0;
        }
    }

    private boolean isGameOver() {
        for (int i = 0; i < playerCount; i++) {
            if (stonesLeft[i] <= 0) {
                return true;
            }
        }
        boolean boardFull = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (boardSquares[i][j].getComponentCount() <= 1) {
                    boardFull = false;
                    break;
                }
            }
            if (!boardFull)
                break;
        }
        return boardFull;
    }

    private void endGame() {
        int[] scores = calculateScores();
        int maxScore = -1;
        int winner = -1;
        for (int i = 0; i < playerCount; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                winner = i;
            }
        }
        StringBuilder message = new StringBuilder();
        message.append("Game Over!\n\n");
        message.append("Final Scores:\n");
        for (int i = 0; i < playerCount; i++) {
            message.append(playerNames[i]).append(": ").append(scores[i]).append(" points\n");
        }
        message.append("\nWinner: ").append(playerNames[winner]).append("!");
        JOptionPane.showMessageDialog(this, message.toString(), "Game Over", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private int[] calculateScores() {
        return playerScores;
    }

    private void handleCpuTurn() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        rollDice();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // We'll attempt to find a valid dice combination
        boolean foundMove = false;
        for (int numDice = 1; numDice <= 6 && !foundMove; numDice++) {
            boolean[] candidate = new boolean[6];
            foundMove = tryDiceCombination(candidate, 0, 0, numDice);
            if (foundMove) {
                // If we found a valid combination, set it in diceLogic
                diceLogic.setDiceSelected(candidate);
                updateDiceUI(); // reflect the CPU's choice

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }

                // Attempt to place the stone on a valid square
                boolean placed = false;
                for (int i = 0; i < BOARD_SIZE && !placed; i++) {
                    for (int j = 0; j < BOARD_SIZE && !placed; j++) {
                        if (boardSquares[i][j].getComponentCount() > 1)
                            continue;

                        String squareType = SQUARE_TYPES[i * BOARD_SIZE + j];

                        // Build the CPU's selected dice values
                        ArrayList<Integer> selValues = new ArrayList<>();
                        boolean[] sel = diceLogic.getDiceSelected();
                        int[] vals = diceLogic.getDiceValues();
                        for (int k = 0; k < 6; k++) {
                            if (sel[k]) {
                                selValues.add(vals[k]);
                            }
                        }
                        // Use placeLogic to see if valid
                        if (placeLogic.isValidPlacement(squareType, selValues)) {
                            // 1) If the stone isn't created, create it now
                            if (currentStone == null) {
                                currentStone = new StonePanel(playerColors[currentPlayer]);
                                currentStone.setPreferredSize(new Dimension(40, 40));
                            }

                            // 2) Now place it
                            placeLogic.placePiece(i, j, currentStoneRow, currentStoneCol, currentStone);

                            // 3) Update your row/col tracking and repaint
                            currentStoneRow = i;
                            currentStoneCol = j;

                            // (No need to add currentStone directly in handleCpuTurn now,
                            // since placePiece(...) already does the add(...) call)

                            placed = true;
                        }
                    }
                }
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e3) {
            e3.printStackTrace();
        }
        endTurn();
    }

    /**
     * CPU helper: tries all combinations of exactly 'target' dice
     * (starting from index "index"), to see if it yields a valid square.
     */
    private boolean tryDiceCombination(boolean[] selected, int index, int count, int target) {
        if (count == target) {
            // Check if there's a valid square for this combination
            ArrayList<Integer> selValues = new ArrayList<>();
            int[] vals = diceLogic.getDiceValues();
            for (int i = 0; i < 6; i++) {
                if (selected[i]) {
                    selValues.add(vals[i]);
                }
            }
            // See if there's at least one valid square
            for (int row = 0; row < BOARD_SIZE; row++) {
                for (int col = 0; col < BOARD_SIZE; col++) {
                    if (boardSquares[row][col].getComponentCount() > 1)
                        continue;
                    String squareType = SQUARE_TYPES[row * BOARD_SIZE + col];
                    if (placeLogic.isValidPlacement(squareType, selValues)) {
                        return true;
                    }
                }
            }
            return false;
        }
        if (index >= 6)
            return false;

        // Try selecting the current die
        selected[index] = true;
        if (tryDiceCombination(selected, index + 1, count + 1, target)) {
            return true;
        }
        // Backtrack
        selected[index] = false;
        // Try skipping the current die
        return tryDiceCombination(selected, index + 1, count, target);
    }

    private void pauseGame() {
        if (!isPaused) {
            isPaused = true;
            if (gameTimer != null) {
                gameTimer.stop();
            }
            setGameControlButtonsEnabled(false);
            pauseButton.setEnabled(false);
            resumeButton.setEnabled(true);
            System.out.println("Game paused.");
        }
    }

    private void resumeGame() {
        if (isPaused) {
            isPaused = false;
            if (gameTimer != null) {
                gameTimer.start();
            }
            setGameControlButtonsEnabled(true);
            pauseButton.setEnabled(true);
            resumeButton.setEnabled(false);
            System.out.println("Game resumed.");
        }
    }

    private void setGameControlButtonsEnabled(boolean enabled) {
        for (JButton d : dice) {
            d.setEnabled(enabled && diceLogic.getRollCount() < 3 && diceLogic.getRollCount() > 0);
        }
        rollDiceButton.setEnabled(enabled && diceLogic.getRollCount() < 3);
        endTurnButton.setEnabled(enabled && currentStone != null);
    }

    @Override
    public void dispose() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        super.dispose();
    }

    // Inner class for a better-looking stone
    private class StonePanel extends JPanel {
        private Color color;

        public StonePanel(Color color) {
            this.color = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.fillOval(0, 0, getWidth(), getHeight());
            g2d.setColor(Color.BLACK);
            g2d.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    // Test main (optional)
    public static void main(String[] args) {
        String[] names = { "Player 1", "CPU" };
        Color[] colors = { new Color(30, 144, 255), new Color(220, 20, 60) };
        boolean[] human = { true, false };
        SwingUtilities.invokeLater(() -> new KiviGameplay(2, names, colors, human, 30));
    }
}