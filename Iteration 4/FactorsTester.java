import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

class FactorsTester {

	@Test
	void testPerfect1()
	{	
		// TEST 1: should throw the exception because the parameter value is less than 1
		assertThrows(IllegalArgumentException.class, () -> FactorsUtility.perfect(0));
	}
	
	@Test
	void testPerfect2()
	{	
		// TEST 2: should succeed because 1 is a valid parameter value, but is not a perfect number
		assertFalse(FactorsUtility.perfect(1));
	}
	
	@Test
	void testPerfect3()
	{	
		// TEST 3: should succeed because 6 is a valid parameter value, and is a perfect number
		assertTrue(FactorsUtility.perfect(6));
	}
	
	@Test
	void testPerfect4()
	{	
		// TEST 4: should succeed because 7 is a valid parameter value, but is not a perfect number
		// I've coded this using assertEquals to show that there's often more than one way to write a test 
		boolean expected = false;
		assertEquals(expected, FactorsUtility.perfect(7));
	}

	@Test
    void testGetFactors1() {
        assertEquals(new ArrayList<>(Arrays.asList(1)), FactorsUtility.getFactors(2));
    }

    @Test
    void testGetFactors2() {
        assertEquals(new ArrayList<>(), FactorsUtility.getFactors(1));
    }

    @Test
    void testGetFactors3() {
        assertEquals(new ArrayList<>(), FactorsUtility.getFactors(0));
    }

    @Test
    void testGetFactors4() {
        assertThrows(IllegalArgumentException.class, () -> FactorsUtility.getFactors(-1));
    }

    @Test
    void testGetFactors5() {
        assertEquals(new ArrayList<>(Arrays.asList(1, 2, 3, 4, 6)), FactorsUtility.getFactors(12));
    }


    @Test
    void testFactor1() {
        assertTrue(FactorsUtility.factor(12, 3));
    }

    @Test
    void testFactor2() {
        assertFalse(FactorsUtility.factor(12, 5));
    }

    @Test
    void testFactor3() {
        assertThrows(IllegalArgumentException.class, () -> FactorsUtility.factor(-1, 1));
    }

    @Test
    void testFactor4() {
        assertThrows(IllegalArgumentException.class, () -> FactorsUtility.factor(5, 0));
    }

    @Test
    void testFactor5() {
        assertTrue(FactorsUtility.factor(5, 5));
    }
}
