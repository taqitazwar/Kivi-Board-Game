import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;


public class KiviGameTester {

    @Test
    void testGameIsNotNull() {
        KiviGame game = new KiviGame();  // Adjust if KiviGame needs parameters
        assertNotNull(game);
    }
}
