/**
 * @author Bruce Lamb
 * @since 8 MAY 2026
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

    @Test
    public void twoSlotTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        //adding 2 slots that touch but do not overlap

        assertEquals("[0,2)", regi.getBoundryIntervalString());
        assertEquals(2, regi.getSlotCount());
    }

    @Test
    public void twoSlotTest2(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        //adding 2 slots that touch but do not overlap

        assertEquals("[0,2)", regi.getBoundryIntervalString());
        assertEquals(2, regi.getSlotCount());
    }

    @Test
    public void threeSlotTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        regi.addSlot(new FixedLongInterval(2, 3)); //[2, 3)

        assertEquals("[0,3)", regi.getBoundryIntervalString());
        assertEquals(3, regi.getSlotCount());
    }

    @Test
    public void fourSlotTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        regi.addSlot(new FixedLongInterval(2, 3)); //[2, 3)

        regi.addSlot(new FixedLongInterval(5, 7), false); //[5, 7)
        assertEquals("[0,3)U[5,7)", regi.getBoundryIntervalString());
        assertEquals(4, regi.getSlotCount());
    }

    @Test
    public void fourSlotTest2(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        regi.addSlot(new FixedLongInterval(2, 3)); //[2, 3)

        regi.addSlot(new FixedLongInterval(5, 7), true); //[5, 7)
        assertEquals("[0,7)", regi.getBoundryIntervalString()); //only 1 gap, [3, 5) that will be closed
        assertEquals(4, regi.getSlotCount());
    }

    @Test
    public void fourSlotTest3(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(5, 6)); //[5, 6)
        regi.addSlot(new FixedLongInterval(6, 7)); //[6, 7)
        regi.addSlot(new FixedLongInterval(7, 8)); //[7, 8)

        regi.addSlot(new FixedLongInterval(1, 3, true, true), false); //[1, 3]
        assertEquals("[1,3]U[5,8)", regi.getBoundryIntervalString());
        assertEquals(4, regi.getSlotCount());
        assertEquals("[1,3]", regi.getSlotBoundIntervalString(0));
        assertEquals("[5,6)", regi.getSlotBoundIntervalString(1));
        assertEquals("[6,7)", regi.getSlotBoundIntervalString(2));
        assertEquals("[7,8)", regi.getSlotBoundIntervalString(3));
    }

    @Test
    public void SlotGapTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundryIntervalString()); //pre check

        regi.addSlot(new FixedLongInterval(4, 6, false, false), false); //(4, 6)
        
        assertEquals("[0,3)U(4,6)U[7,10)", regi.getBoundryIntervalString());
        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("(4,6)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void SlotGapTest2(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundryIntervalString()); //pre check

        regi.addSlot(
            new FixedLongInterval(4, 6, false, false), //[7, 10)
            true, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            true, //Will be ignored because domain is already perfectly snapped
            true, //WILL close gap leftward
            false //Will not close righward
        );
        
        assertEquals("[0,6)U[7,10)", regi.getBoundryIntervalString());
        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("[3,6)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void SlotGapTest3(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundryIntervalString()); //pre check

        regi.addSlot(
            new FixedLongInterval(4, 6, false, false), //[7, 10)
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //WILL not close gap leftward
            true //Will close gap righward
        );
        
        assertEquals("[0,3)U(4,10)", regi.getBoundryIntervalString());
        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("(4,7)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void SlotGapTest4(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundryIntervalString()); //pre check

        regi.addSlot(
            new FixedLongInterval(4, 6, false, false), //[7, 10)
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            true, //WILL close gap leftward
            true //Will close gap righward
        );
        
        assertEquals("[0,10)", regi.getBoundryIntervalString());
        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("[3,7)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void SlotGapTest5(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundryIntervalString()); //pre check

        regi.addSlot(
            new FixedLongInterval(4, 6, false, false), //[7, 10)
            true
        );
        
        assertEquals("[0,10)", regi.getBoundryIntervalString());
        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("[3,7)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void SlotOverlapTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        assertEquals("[0,3)", regi.getBoundryIntervalString()); //pre check
        assertEquals(1, regi.getSlotCount());

        regi.addSlot(new FixedLongInterval(1, 4), false);
        assertEquals("[0,4)", regi.getBoundryIntervalString());

        assertEquals(2, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("[3,4)", regi.getSlotBoundIntervalString(1));
    }
}