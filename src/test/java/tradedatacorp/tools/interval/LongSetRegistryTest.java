/**
 * @author Bruce Lamb
 * @since 28 APR 2026
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
    }

    @Test
    public void oneSlotTest(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));

        //Need to clearly define addSlot definitions before testing
    }
}