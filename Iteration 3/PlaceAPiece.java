import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * A helper class to manage all piece-placement logic on the board:
 * - Checking if a dice combination is valid for a given square
 * - Physically placing the piece onto the JButton[][]
 * - Handling partial placements (currentStone)
 */
public class PlaceAPiece {

    private final int boardSize;
    private final JButton[][] boardSquares;
    private final String[] squareTypes;

    public PlaceAPiece(int boardSize, JButton[][] boardSquares, String[] squareTypes) {
        this.boardSize = boardSize;
        this.boardSquares = boardSquares;
        this.squareTypes = squareTypes;
    }

    /**
     * Attempts to place the piece on the board at (row, col)
     * using the selected dice values.
     * - If it's invalid, show an error message and return false.
     * - If valid, place the piece and return true.
     */
    public boolean attemptPlacePiece(JFrame parent,
            int row, int col,
            int currentStoneRow, int currentStoneCol,
            JPanel currentStone,
            ArrayList<Integer> selectedValues,
            boolean isHumanPlayer) {
        // If not human, skip. (Optional check)
        if (!isHumanPlayer) {
            return false;
        }

        // If no dice are selected, block
        if (selectedValues.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Please select dice first!");
            return false;
        }

        // If this square is already occupied by a different stone
        if (boardSquares[row][col].getComponentCount() > 1
                && !(row == currentStoneRow && col == currentStoneCol)) {
            JOptionPane.showMessageDialog(parent, "This square is already occupied!");
            return false;
        }

        // Check if it's valid for that square
        int index = row * boardSize + col;
        String squareType = squareTypes[index];
        if (!isValidPlacement(squareType, selectedValues)) {
            JOptionPane.showMessageDialog(parent,
                    "Invalid placement! This combination doesn't match the square requirements.");
            return false;
        }

        // If valid, place the piece
        placePiece(row, col, currentStoneRow, currentStoneCol, currentStone);

        return true;
    }

    /**
     * Actually put the piece on (row, col).
     * If there's an existing "currentStone" on the board, remove it first.
     */
    public void placePiece(int row, int col, int currentStoneRow, int currentStoneCol, JPanel currentStone) {
        if (currentStone != null && currentStoneRow >= 0 && currentStoneCol >= 0) {
            boardSquares[currentStoneRow][currentStoneCol].remove(currentStone);
            boardSquares[currentStoneRow][currentStoneCol].revalidate();
            boardSquares[currentStoneRow][currentStoneCol].repaint();
        }
        boardSquares[row][col].add(currentStone, BorderLayout.CENTER);
        boardSquares[row][col].revalidate();
        boardSquares[row][col].repaint();
    }

    /**
     * Checks whether the selected dice values fulfill the requirements of
     * squareType.
     * Migrate your existing logic from isValidPlacement(...) here.
     */
    public boolean isValidPlacement(String squareType, ArrayList<Integer> selectedValues) {
        if (selectedValues.isEmpty())
            return false;
        selectedValues.sort(null);

        switch (squareType) {
            case "≤12": {
                int sum = 0;
                for (int value : selectedValues)
                    sum += value;
                return sum <= 12;
            }
            case "≥30": {
                int sum = 0;
                for (int value : selectedValues)
                    sum += value;
                return sum >= 30;
            }
            case "=1,3,5":
                return (selectedValues.contains(1) && selectedValues.contains(3) && selectedValues.contains(5));
            case "=2,4,6":
                return (selectedValues.contains(2) && selectedValues.contains(4) && selectedValues.contains(6));
            case "ABCDE":
                return (selectedValues.contains(1) && selectedValues.contains(2) &&
                        selectedValues.contains(3) && selectedValues.contains(4) &&
                        selectedValues.contains(5));
            case "ABCD":
                return (selectedValues.contains(1) && selectedValues.contains(2) &&
                        selectedValues.contains(3) && selectedValues.contains(4));
            case "AAA":
                return hasAtLeastCount(selectedValues, 3);
            case "AAAA":
                return hasAtLeastCount(selectedValues, 4);
            case "AA/BB":
                return hasPairs(selectedValues, 2);
            case "AA/BB/CC":
                return hasPairs(selectedValues, 3);
            case "AAA/BB":
                return hasFullHouse(selectedValues);
            case "AAAA/BB":
                return hasFourAndPair(selectedValues);
            case "AAA/BBB":
                return hasTwoTriplets(selectedValues);
            default:
                return false;
        }
    }

    // ---------------------- Helper Methods ----------------------

    private boolean hasAtLeastCount(ArrayList<Integer> values, int needed) {
        int[] counts = new int[7];
        for (int v : values)
            counts[v]++;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= needed)
                return true;
        }
        return false;
    }

    private boolean hasPairs(ArrayList<Integer> values, int pairsNeeded) {
        int[] counts = new int[7];
        for (int v : values)
            counts[v]++;
        int pairCount = 0;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 2)
                pairCount++;
        }
        return pairCount >= pairsNeeded;
    }

    private boolean hasFullHouse(ArrayList<Integer> values) {
        int[] counts = new int[7];
        for (int v : values)
            counts[v]++;
        boolean hasThree = false;
        boolean hasPair = false;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 3)
                hasThree = true;
            else if (counts[i] >= 2)
                hasPair = true;
        }
        return hasThree && hasPair;
    }

    private boolean hasFourAndPair(ArrayList<Integer> values) {
        int[] counts = new int[7];
        for (int v : values)
            counts[v]++;
        boolean hasFour = false;
        boolean hasPair = false;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 4)
                hasFour = true;
            else if (counts[i] >= 2)
                hasPair = true;
        }
        return hasFour && hasPair;
    }

    private boolean hasTwoTriplets(ArrayList<Integer> values) {
        int[] counts = new int[7];
        for (int v : values)
            counts[v]++;
        int tripletCount = 0;
        for (int i = 1; i <= 6; i++) {
            if (counts[i] >= 3)
                tripletCount++;
        }
        return tripletCount >= 2;
    }
}
