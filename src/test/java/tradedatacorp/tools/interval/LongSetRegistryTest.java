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
        assertEquals("{}", regi.getBoundaryIntervalString());
        assertEquals("{}", regi.getCoverageIntervalString());

        assertEquals(0, regi.getSlotCount());
        assertEquals(0, regi.getMicroIntervalCountInBoundary());
        assertEquals(0, regi.getMicroIntervalCountCovered());
    }

    @Test
    public void oneSlotTest(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(3, 6));

        assertFalse(regi.isMerged());
        assertTrue(regi.isBoundContinuous());

        assertEquals("[3,6)", regi.getBoundaryIntervalString());
        assertEquals("{}", regi.getCoverageIntervalString());
        assertTrue(regi.isMerged()); //Lazy check, should automerge upon checking intervals
        assertEquals(1, regi.getSlotCount());
        assertEquals(3, regi.getMicroIntervalCountInBoundary());
        assertEquals(0, regi.getMicroIntervalCountCovered());
    }

    @Test
    public void twoSlotTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        //adding 2 slots that touch but do not overlap

        assertEquals("[0,2)", regi.getBoundaryIntervalString());
        assertTrue(regi.isBoundContinuous());

        assertEquals(2, regi.getSlotCount());
    }

    @Test
    public void twoSlotTest2(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        //adding 2 slots that touch but do not overlap

        assertEquals("[0,2)", regi.getBoundaryIntervalString());
        assertTrue(regi.isBoundContinuous());

        assertEquals(2, regi.getSlotCount());
    }

    @Test
    public void threeSlotTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        regi.addSlot(new FixedLongInterval(2, 3)); //[2, 3)

        assertEquals("[0,3)", regi.getBoundaryIntervalString());
        assertTrue(regi.isBoundContinuous());

        assertEquals(3, regi.getSlotCount());
    }

    @Test
    public void fourSlotTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        regi.addSlot(new FixedLongInterval(2, 3)); //[2, 3)

        regi.addSlot(new FixedLongInterval(5, 7), false); //[5, 7)
        assertEquals("[0,3)U[5,7)", regi.getBoundaryIntervalString());
        assertFalse(regi.isBoundContinuous());

        assertEquals(4, regi.getSlotCount());
    }

    @Test
    public void fourSlotTest2(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 1)); //[0, 1)
        regi.addSlot(new FixedLongInterval(1, 2)); //[1, 2)
        regi.addSlot(new FixedLongInterval(2, 3)); //[2, 3)

        regi.addSlot(new FixedLongInterval(5, 7), true); //[5, 7)
        assertEquals("[0,7)", regi.getBoundaryIntervalString()); //only 1 gap, [3, 5) that will be closed
        assertTrue(regi.isBoundContinuous());

        assertEquals(4, regi.getSlotCount());
    }

    @Test
    public void fourSlotTest3(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(5, 6)); //[5, 6)
        regi.addSlot(new FixedLongInterval(6, 7)); //[6, 7)
        regi.addSlot(new FixedLongInterval(7, 8)); //[7, 8)

        regi.addSlot(new FixedLongInterval(1, 3, true, true), false); //[1, 3]
        assertEquals("[1,3]U[5,8)", regi.getBoundaryIntervalString());
        assertFalse(regi.isBoundContinuous());

        assertEquals(4, regi.getSlotCount());
        assertEquals("[1,3]", regi.getSlotBoundIntervalString(0));
        assertEquals("[5,6)", regi.getSlotBoundIntervalString(1));
        assertEquals("[6,7)", regi.getSlotBoundIntervalString(2));
        assertEquals("[7,8)", regi.getSlotBoundIntervalString(3));
    }

    @Test
    public void slotGapTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundaryIntervalString()); //pre check

        regi.addSlot(new FixedLongInterval(4, 6, false, false), false); //(4, 6)
        
        assertEquals("[0,3)U(4,6)U[7,10)", regi.getBoundaryIntervalString());
        assertFalse(regi.isBoundContinuous());

        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("(4,6)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void slotGapTest2(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundaryIntervalString()); //pre check

        regi.addSlot(
            new FixedLongInterval(4, 6, false, false), //[7, 10)
            true, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            true, //Will be ignored because domain is already perfectly snapped
            true, //WILL close gap leftward
            false //Will not close righward
        );
        
        assertEquals("[0,6)U[7,10)", regi.getBoundaryIntervalString());
        assertFalse(regi.isBoundContinuous());

        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("[3,6)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void slotGapTest3(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundaryIntervalString()); //pre check

        regi.addSlot(
            new FixedLongInterval(4, 6, false, false), //[7, 10)
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //WILL not close gap leftward
            true //Will close gap righward
        );
        
        assertEquals("[0,3)U(4,10)", regi.getBoundaryIntervalString());
        assertFalse(regi.isBoundContinuous());

        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("(4,7)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void slotGapTest4(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundaryIntervalString()); //pre check

        regi.addSlot(
            new FixedLongInterval(4, 6, false, false), //[7, 10)
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            false, //Will be ignored because domain is already perfectly snapped
            true, //WILL close gap leftward
            true //Will close gap righward
        );
        
        assertEquals("[0,10)", regi.getBoundaryIntervalString());
        assertTrue(regi.isBoundContinuous());

        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("[3,7)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void slotGapTest5(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        regi.addSlot(new FixedLongInterval(7, 10)); //[7, 10)
        assertEquals("[0,3)U[7,10)", regi.getBoundaryIntervalString()); //pre check

        regi.addSlot(
            new FixedLongInterval(4, 6, false, false), //[4, 6)
            true
        );
        
        assertEquals("[0,10)", regi.getBoundaryIntervalString());
        assertTrue(regi.isBoundContinuous());

        assertEquals(3, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("[3,7)", regi.getSlotBoundIntervalString(1));
        assertEquals("[7,10)", regi.getSlotBoundIntervalString(2));
    }

    @Test
    public void slotOverlapTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 3)); //[0, 3)
        assertEquals("[0,3)", regi.getBoundaryIntervalString()); //pre check
        assertEquals(1, regi.getSlotCount());

        regi.addSlot(new FixedLongInterval(1, 4), false);
        assertEquals("[0,4)", regi.getBoundaryIntervalString());
        assertTrue(regi.isBoundContinuous());

        assertEquals(2, regi.getSlotCount());
        assertEquals("[0,3)", regi.getSlotBoundIntervalString(0));
        assertEquals("[3,4)", regi.getSlotBoundIntervalString(1));
    }

    @Test
    public void slotOverlapTest2(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(1, 7,true, true));
        assertEquals("[1,7]", regi.getBoundaryIntervalString()); //pre check
        assertEquals(1, regi.getSlotCount());

        regi.addSlot(new FixedLongInterval(-1, 4, false, false), false);
        assertEquals("(-1,7]", regi.getBoundaryIntervalString());
        assertTrue(regi.isBoundContinuous());

        assertEquals(2, regi.getSlotCount());
        assertEquals("(-1,1)", regi.getSlotBoundIntervalString(0));
        assertEquals("[1,7]", regi.getSlotBoundIntervalString(1));
    }

    @Test
    public void slotOverlapTest3(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(2, 3,true, true));
        regi.addSlot(new FixedLongInterval(5, 8, false, false), false);
        assertEquals("[2,3]U(5,8)", regi.getBoundaryIntervalString());
        assertFalse(regi.isBoundContinuous());
        assertEquals(2, regi.getSlotCount());

        regi.addSlot(new FixedLongInterval(0, 10, true, true), false);
        assertTrue(regi.isBoundContinuous());
        assertEquals(5, regi.getSlotCount());

        assertEquals("[0,2)", regi.getSlotBoundIntervalString(0));
        assertEquals("[2,3]", regi.getSlotBoundIntervalString(1));
        assertEquals("(3,5]", regi.getSlotBoundIntervalString(2));
        assertEquals("(5,8)", regi.getSlotBoundIntervalString(3));
        assertEquals("[8,10]", regi.getSlotBoundIntervalString(4));
    }

    @Test
    public void coverageTest1(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 5));
        regi.addSlot(new FixedLongInterval(5, 10));
        assertTrue(regi.isBoundContinuous());
        assertEquals("[0,10)", regi.getBoundaryIntervalString());

        assertEquals("[0,5)", regi.getSlotBoundIntervalString(0));
        assertEquals("[5,10)", regi.getSlotBoundIntervalString(1));

        assertEquals("{}", regi.getCoverageIntervalString());
        assertEquals("{}", regi.getSlotCoverageIntervalString(0));
        assertEquals("{}", regi.getSlotCoverageIntervalString(1));

        //add coverage
        regi.addCoverage(new FixedLongInterval(3, 3, true, true));
        assertEquals("{3}", regi.getCoverageIntervalString());
        assertEquals("{3}", regi.getSlotCoverageIntervalString(0));
        assertEquals("{}", regi.getSlotCoverageIntervalString(1));

        //clear coverage
        regi.clearAllCoverage();
        assertEquals("{}", regi.getCoverageIntervalString());
        assertEquals(2, regi.getSlotCount());
    }

    @Test
    public void coverageTest2(){
        LongSetRegistry regi = new LongSetRegistry(new FixedLongInterval(0, 1));
        regi.addSlot(new FixedLongInterval(0, 5));
        regi.addSlot(new FixedLongInterval(5, 10));
        assertTrue(regi.isBoundContinuous());
        assertEquals("[0,10)", regi.getBoundaryIntervalString());

        assertEquals("[0,5)", regi.getSlotBoundIntervalString(0));
        assertEquals("[5,10)", regi.getSlotBoundIntervalString(1));

        assertEquals("{}", regi.getCoverageIntervalString());
        assertEquals("{}", regi.getSlotCoverageIntervalString(0));
        assertEquals("{}", regi.getSlotCoverageIntervalString(1));

        //add coverage
        regi.addCoverage(new FixedLongInterval(3, 6, false, true));
        assertEquals("(3,6]", regi.getCoverageIntervalString());
        assertEquals("(3,5)", regi.getSlotCoverageIntervalString(0));
        assertEquals("[5,6]", regi.getSlotCoverageIntervalString(1));

        //clear coverage
        regi.clearSlotCoverage(1); //removing chunk [5,6]
        assertEquals("(3,5)", regi.getCoverageIntervalString());
        assertEquals("(3,5)", regi.getSlotCoverageIntervalString(0));
        assertEquals("{}", regi.getSlotCoverageIntervalString(1));
    }
}