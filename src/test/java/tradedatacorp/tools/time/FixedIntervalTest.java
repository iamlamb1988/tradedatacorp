package tradedatacorp.tools.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FixedIntervalTest{
    @Test
    public void smallIntervalDefaultTest(){
        FixedInterval smallInterval = new FixedInterval("test", 2, 5);
        assertEquals("test", smallInterval.getName());
        assertEquals("test", smallInterval.name);
        assertTrue(smallInterval.inclusiveStart);
        assertFalse(smallInterval.inclusiveEnd);

        assertTrue(smallInterval.isUTCmilliWithinInterval(3));
        assertFalse(smallInterval.isUTCmilliWithinInterval(1));
        assertFalse(smallInterval.isUTCmilliWithinInterval(8));

        assertTrue(smallInterval.isUTCmilliWithinInterval(2));
        assertFalse(smallInterval.isUTCmilliWithinInterval(5));
    }
}