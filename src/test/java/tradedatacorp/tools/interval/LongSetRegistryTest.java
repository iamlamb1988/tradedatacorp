/**
 * @author Bruce Lamb
 * @since 29 APR 2026
 */
package tradedatacorp.tools.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

public class LongSetRegistryTest{
    @Test
    public void constructorDefaultTest(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(3, 7));
        assertTrue(regi.isMerged());
        assertTrue(regi.isBoundContinuous());
        assertEquals("{}", regi.getBoundryIntervalString());
        assertEquals("{}", regi.getCoverageIntervalString());

        assertEquals(0, regi.getSlotCount());
        assertEquals(0, regi.getMicroIntervalCountInBoundry());
        assertEquals(0, regi.getMicroIntervalCountCovered());
    }

    @Test
    public void oneSlotTest(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(3, 6));

        assertFalse(regi.isMerged());
        assertTrue(regi.isBoundContinuous());

        assertEquals("[3,6)", regi.getBoundryIntervalString());
        assertEquals("{}", regi.getCoverageIntervalString());
        assertTrue(regi.isMerged()); //Lazy check, should automerge upon checking intervals
        assertEquals(1, regi.getSlotCount());
        assertEquals(3, regi.getMicroIntervalCountInBoundry());
        assertEquals(0, regi.getMicroIntervalCountCovered());
    }
}