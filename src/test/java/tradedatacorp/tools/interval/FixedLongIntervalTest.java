/**
 * @author Bruce Lamb
 * @since 23 APR 2026
 */
package tradedatacorp.tools.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

public final class FixedLongIntervalTest{
    @Test
    public void smallIntervalDefaultTest(){
        FixedLongInterval smallInterval = new FixedLongInterval(2, 5);

        assertEquals("[2,5)", smallInterval.toString());

        assertTrue(smallInterval.inclusiveStart);
        assertFalse(smallInterval.inclusiveEnd);

        assertEquals(2, smallInterval.start);
        assertEquals(2, smallInterval.getStart());

        assertEquals(5, smallInterval.end);
        assertEquals(5, smallInterval.getEnd());

        assertEquals(3, smallInterval.width);      //5 - 2
        assertEquals(3, smallInterval.getWidth()); //5 - 2


        assertTrue(smallInterval.contains(3));
        assertFalse(smallInterval.contains(1));
        assertFalse(smallInterval.contains(8));

        assertTrue(smallInterval.contains(2));
        assertFalse(smallInterval.contains(5));
    }

    @Test
    public void smallIntervalInclusiveTest(){
        FixedLongInterval smallInterval = new FixedLongInterval(2, 5, true, true);

        assertEquals("[2,5]", smallInterval.toString());

        assertTrue(smallInterval.inclusiveStart);
        assertTrue(smallInterval.inclusiveEnd);

        assertEquals(2, smallInterval.start);
        assertEquals(2, smallInterval.getStart());

        assertEquals(5, smallInterval.end);
        assertEquals(5, smallInterval.getEnd());

        assertTrue(smallInterval.contains(3));
        assertFalse(smallInterval.contains(1));
        assertFalse(smallInterval.contains(8));

        assertTrue(smallInterval.contains(2));
        assertTrue(smallInterval.contains(5));
    }

    @Test
    public void smallIntervalExclusiveTest(){
        FixedLongInterval smallInterval = new FixedLongInterval(2, 5, false, false);

        assertEquals("(2,5)", smallInterval.toString());

        assertFalse(smallInterval.inclusiveStart);
        assertFalse(smallInterval.inclusiveEnd);

        assertEquals(2, smallInterval.start);
        assertEquals(2, smallInterval.getStart());

        assertEquals(5, smallInterval.end);
        assertEquals(5, smallInterval.getEnd());

        assertTrue(smallInterval.contains(3));
        assertFalse(smallInterval.contains(1));
        assertFalse(smallInterval.contains(8));

        assertFalse(smallInterval.contains(2));
        assertFalse(smallInterval.contains(5));
    }

    @Test
    public void smallIntervalComparisonTest1(){
        FixedLongInterval int1 = new FixedLongInterval(2, 5, true, true);
        FixedLongInterval int2 = new FixedLongInterval(2, 5, true, true);

        assertEquals("[2,5]", int1.toString());
        assertEquals("[2,5]", int2.toString());
        assertTrue(FixedLongInterval.equals(int1, int2));
    }

    @Test
    public void smallIntervalComparisonTest2(){
        FixedLongInterval int1 = new FixedLongInterval(2, 5, true, true);
        FixedLongInterval int2 = new FixedLongInterval(2, 5, false, true);

        assertEquals("[2,5]", int1.toString());
        assertEquals("(2,5]", int2.toString());
        assertFalse(FixedLongInterval.equals(int1, int2));
    }

    @Test
    public void smallIntervalComparisonTest3(){
        FixedLongInterval int1 = new FixedLongInterval(2, 5, true, true);
        FixedLongInterval int2 = new FixedLongInterval(99, 5, true, false);

        assertEquals("[2,5]", int1.toString());
        assertEquals("(5,99]", int2.toString()); //NOTE: this is the SWAP in constructor enforcing proper ordering.
        assertFalse(FixedLongInterval.equals(int1, int2));
    }

    @Test
    public void smallIntervalComparisonTest4(){
        FixedLongInterval int1 = new FixedLongInterval(2, 555, true, true);
        FixedLongInterval int2 = new FixedLongInterval(2, 5, true, false);

        assertEquals("[2,555]", int1.toString());
        assertEquals("[2,5)", int2.toString());
        assertFalse(FixedLongInterval.equals(int1, int2));
    }

    @Test
    public void smallIntervalComparisonTest5(){
        FixedLongInterval int1 = new FixedLongInterval(1, 1, false, false);
        FixedLongInterval int2 = new FixedLongInterval(1, 1, false, false);

        assertEquals("{}", int1.toString());
        assertEquals("{}", int2.toString());
        assertTrue(FixedLongInterval.equals(int1, int2));
    }

    @Test
    public void emptySetTest(){
        FixedLongInterval int1 = new FixedLongInterval(1, 1, false, false); //(1, 1)
        FixedLongInterval int2 = new FixedLongInterval(1, 1, false, true);  //(1, 1]
        FixedLongInterval int3 = new FixedLongInterval(1, 1, true, false);  //[1, 1)

        assertEquals("{}", int1.toString());
        assertEquals("{}", int2.toString());
        assertEquals("{}", int3.toString());
    }

    @Nested
    public class TestsForOverlap{
        @Test
        public void engulfsTargetTest(){
            FixedLongInterval int1 = new FixedLongInterval(1, 10, true, true);
            FixedLongInterval int2 = new FixedLongInterval(4, 7, false, false);

            assertTrue(int1.overlaps(int2));
            assertTrue(int2.overlaps(int1));
        }

        @Test
        public void partialOverlapTargetTest(){
            FixedLongInterval int1 = new FixedLongInterval(1, 10, false, false);
            FixedLongInterval int2 = new FixedLongInterval(5, 15, false, false);

            assertTrue(int1.overlaps(int2));
            assertTrue(int2.overlaps(int1));
        }

        @Test
        public void endpointTouchTest(){
            FixedLongInterval int1 = new FixedLongInterval(1, 5, false, true);
            FixedLongInterval int2 = new FixedLongInterval(5, 15, true, false);

            assertTrue(int1.overlaps(int2));
            assertTrue(int2.overlaps(int1));
        }

        @Test
        public void noOverlapTest(){
            FixedLongInterval int1 = new FixedLongInterval(1, 5, true, true);
            FixedLongInterval int2 = new FixedLongInterval(10, 15, true, true);

            assertFalse(int1.overlaps(int2));
            assertFalse(int2.overlaps(int1));
        }

        @Test
        public void TouchOverlapTest(){
            FixedLongInterval int1 = new FixedLongInterval(1, 5, false, true);
            FixedLongInterval int2 = new FixedLongInterval(5, 10, true, false);

            assertTrue(int1.overlaps(int2));
            assertTrue(int2.overlaps(int1));
        }

        @Test
        public void TouchPointOverlapTest(){
            FixedLongInterval int1 = new FixedLongInterval(5, 5, true, true);
            FixedLongInterval int2 = new FixedLongInterval(5, 10, true, false);

            assertTrue(int1.overlaps(int2));
            assertTrue(int2.overlaps(int1));
        }

        @Test
        public void PointToPointOverlapTest(){
            FixedLongInterval int1 = new FixedLongInterval(7, 7, true, true);
            FixedLongInterval int2 = new FixedLongInterval(7, 7, true, true);

            assertTrue(int1.overlaps(int2));
            assertTrue(int2.overlaps(int1));
        }

        @Test
        public void NoTouchOverlapTest(){
            FixedLongInterval int1 = new FixedLongInterval(1, 5, false, false);
            FixedLongInterval int2 = new FixedLongInterval(5, 10, false, false);

            assertFalse(int1.overlaps(int2));
            assertFalse(int2.overlaps(int1));
        }

        @Test
        public void NoTouchAdjacentTest(){
            FixedLongInterval int1 = new FixedLongInterval(1, 5, false, false);
            FixedLongInterval int2 = new FixedLongInterval(5, 10, true, false);

            assertFalse(int1.overlaps(int2));
            assertFalse(int2.overlaps(int1));
        }

        @Test
        public void emptySetOverlapTest1(){
            FixedLongInterval int1 = new FixedLongInterval(7, 7, false, true); //emptyset
            FixedLongInterval int2 = new FixedLongInterval(7, 7, true, true);

            assertFalse(int1.overlaps(int2));
            assertFalse(int2.overlaps(int1));
        }

        @Test
        public void emptySetOverlapTest2(){
            FixedLongInterval int1 = new FixedLongInterval(7, 7, false, true); //emptyset
            FixedLongInterval int2 = new FixedLongInterval(7, 7, false, false);//emptyset

            assertFalse(int1.overlaps(int2));
            assertFalse(int2.overlaps(int1));
        }

        @Test
        public void emptySetOverlapTest3(){
            FixedLongInterval int1 = new FixedLongInterval(1, 10, false, true);
            FixedLongInterval int2 = new FixedLongInterval(7, 7, false, false); //emptyset

            assertFalse(int1.overlaps(int2));
            assertFalse(int2.overlaps(int1));
        }
    }
}