package tradedatacorp.tools.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public final class FixedIntervalTest{
    @Test
    public void smallIntervalDefaultTest(){
        FixedInterval smallInterval = new FixedInterval("test", 2, 5);
        assertEquals("test", smallInterval.getName());
        assertEquals("test", smallInterval.name);
        assertTrue(smallInterval.inclusiveStart);
        assertFalse(smallInterval.inclusiveEnd);

        assertEquals(3, smallInterval.durationMillis); //5 - 2
        assertEquals(3, smallInterval.getIntervalMilli()); //5 - 2

        assertTrue(smallInterval.isUTCmilliWithinInterval(3));
        assertFalse(smallInterval.isUTCmilliWithinInterval(1));
        assertFalse(smallInterval.isUTCmilliWithinInterval(8));

        assertTrue(smallInterval.isUTCmilliWithinInterval(2));
        assertFalse(smallInterval.isUTCmilliWithinInterval(5));
    }

    @Test
    public void smallIntervalInclusiveTest(){
        FixedInterval smallInterval = new FixedInterval("test", 2, 5, true, true);
        assertEquals("test", smallInterval.getName());
        assertEquals("test", smallInterval.name);
        assertTrue(smallInterval.inclusiveStart);
        assertTrue(smallInterval.inclusiveEnd);

        assertTrue(smallInterval.isUTCmilliWithinInterval(3));
        assertFalse(smallInterval.isUTCmilliWithinInterval(1));
        assertFalse(smallInterval.isUTCmilliWithinInterval(8));

        assertTrue(smallInterval.isUTCmilliWithinInterval(2));
        assertTrue(smallInterval.isUTCmilliWithinInterval(5));
    }

    @Test
    public void smallIntervalExclusiveTest(){
        FixedInterval smallInterval = new FixedInterval("test", 2, 5, false, false);
        assertEquals("test", smallInterval.getName());
        assertEquals("test", smallInterval.name);
        assertFalse(smallInterval.inclusiveStart);
        assertFalse(smallInterval.inclusiveEnd);

        assertTrue(smallInterval.isUTCmilliWithinInterval(3));
        assertFalse(smallInterval.isUTCmilliWithinInterval(1));
        assertFalse(smallInterval.isUTCmilliWithinInterval(8));

        assertFalse(smallInterval.isUTCmilliWithinInterval(2));
        assertFalse(smallInterval.isUTCmilliWithinInterval(5));
    }
}