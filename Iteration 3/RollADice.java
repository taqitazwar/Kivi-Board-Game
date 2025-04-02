import java.util.Random;

/**
 * A helper class to manage all dice logic:
 * - Rolling up to 3 times
 * - Tracking which dice are selected
 * - Storing dice face values
 */
public class RollADice {
    private final int MAX_ROLLS = 3;

    private int[] diceValues; // Stores current face values (1-6) for 6 dice
    private boolean[] diceSelected;
    private int rollCount; // How many times we've rolled this turn

    private Random random;

    public RollADice() {
        diceValues = new int[6];
        diceSelected = new boolean[6];
        rollCount = 0;
        random = new Random();
    }

    /**
     * Roll the dice. If it's the first roll, roll all.
     * If subsequent rolls, re-roll only dice that are NOT selected
     * (unless none are selected, in which case roll them all).
     */
    public void rollDice() {
        if (rollCount >= MAX_ROLLS) {
            return; // Already rolled 3 times, no more rolling
        }

        if (rollCount == 0) {
            // First roll: roll all dice
            for (int i = 0; i < 6; i++) {
                diceValues[i] = random.nextInt(6) + 1; // [1..6]
                diceSelected[i] = false;
            }
        } else {
            // 2nd or 3rd roll
            boolean anySelected = false;
            for (boolean selected : diceSelected) {
                if (selected) {
                    anySelected = true;
                    break;
                }
            }
            if (!anySelected) {
                // If no dice are selected, re-roll all
                for (int i = 0; i < 6; i++) {
                    diceValues[i] = random.nextInt(6) + 1;
                    diceSelected[i] = false;
                }
            } else {
                // Otherwise, only re-roll dice that are NOT selected
                for (int i = 0; i < 6; i++) {
                    if (!diceSelected[i]) {
                        diceValues[i] = random.nextInt(6) + 1;
                    }
                }
            }
        }

        rollCount++;
    }

    /**
     * Toggle whether a specific die is 'locked' (kept) or not.
     * Typically only meaningful after the first roll.
     */
    public void toggleDieSelection(int dieIndex) {
        // If we haven't rolled yet, there's nothing to toggle
        if (rollCount == 0)
            return;
        diceSelected[dieIndex] = !diceSelected[dieIndex];
    }

    /**
     * Reset the dice arrays so they're fresh for a new turn.
     */
    public void resetForNextTurn() {
        rollCount = 0;
        for (int i = 0; i < 6; i++) {
            diceValues[i] = 0;
            diceSelected[i] = false;
        }
    }

    // -------------------- Getters / Setters --------------------

    public int[] getDiceValues() {
        return diceValues;
    }

    public boolean[] getDiceSelected() {
        return diceSelected;
    }

    public int getRollCount() {
        return rollCount;
    }

    /**
     * In case you need to programmatically set an entire selection array
     * (e.g., CPU logic).
     */
    public void setDiceSelected(boolean[] newSelections) {
        System.arraycopy(newSelections, 0, diceSelected, 0, 6);
    }
}
