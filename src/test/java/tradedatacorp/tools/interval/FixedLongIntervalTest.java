/**
 * @author Bruce Lamb
 * @since 10 MAY 2026
 */
package tradedatacorp.tools.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

public final class FixedLongIntervalTest{
    @Test
    public void normalizeTest1(){
        FixedLongInterval smallInterval = new FixedLongInterval(7, 9, true, true);
        FixedLongInterval normalizedInterval = FixedLongInterval.getNormalizedInterval(smallInterval);

        assertEquals("[1,3]",normalizedInterval.toString());
    }

    @Test
    public void normalizeTest2(){
        FixedLongInterval smallInterval = new FixedLongInterval(-1, 3);
        FixedLongInterval normalizedInterval = FixedLongInterval.getNormalizedInterval(smallInterval);

        assertEquals("[3,7)",normalizedInterval.toString());
    }

    @Test
    public void normalizeTest3(){
        FixedLongInterval smallInterval = new FixedLongInterval(3, 3, false, false);
        FixedLongInterval normalizedInterval = FixedLongInterval.getNormalizedInterval(smallInterval);

        assertEquals("{}",normalizedInterval.toString());
        assertEquals(0, normalizedInterval.start);
        assertEquals(0, normalizedInterval.end);
        assertFalse(normalizedInterval.inclusiveStart);
        assertFalse(normalizedInterval.inclusiveEnd);
    }

    @Test
    public void normalizeTest4(){
        FixedLongInterval smallInterval = new FixedLongInterval(0, 0, false, true);
        FixedLongInterval normalizedInterval = FixedLongInterval.getNormalizedInterval(smallInterval);

        assertEquals("{}",normalizedInterval.toString());
        assertTrue(smallInterval == normalizedInterval); //should be same reference object
    }

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
        assertTrue(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void smallIntervalComparisonTest2(){
        FixedLongInterval int1 = new FixedLongInterval(2, 5, true, true);
        FixedLongInterval int2 = new FixedLongInterval(2, 5, false, true);

        assertEquals("[2,5]", int1.toString());
        assertEquals("(2,5]", int2.toString());
        assertFalse(FixedLongInterval.equals(int1, int2));
        assertFalse(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void smallIntervalComparisonTest3(){
        FixedLongInterval int1 = new FixedLongInterval(2, 5, true, true);
        FixedLongInterval int2 = new FixedLongInterval(99, 5, true, false);

        assertEquals("[2,5]", int1.toString());
        assertEquals("(5,99]", int2.toString()); //NOTE: this is the SWAP in constructor enforcing proper ordering.
        assertFalse(FixedLongInterval.equals(int1, int2));
        assertFalse(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void smallIntervalComparisonTest4(){
        FixedLongInterval int1 = new FixedLongInterval(2, 555, true, true);
        FixedLongInterval int2 = new FixedLongInterval(2, 5, true, false);

        assertEquals("[2,555]", int1.toString());
        assertEquals("[2,5)", int2.toString());
        assertFalse(FixedLongInterval.equals(int1, int2));
        assertFalse(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void smallIntervalComparisonTest5(){
        FixedLongInterval int1 = new FixedLongInterval(1, 1, false, false);
        FixedLongInterval int2 = new FixedLongInterval(1, 1, false, false);

        assertEquals("{}", int1.toString());
        assertEquals("{}", int2.toString());
        assertTrue(FixedLongInterval.equals(int1, int2));
        assertTrue(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void compareIntervalTest1(){
        FixedLongInterval int1 = new FixedLongInterval(3, 7, true, true);//[3, 7]
        FixedLongInterval int2 = new FixedLongInterval(3, 7, true, true);//[3, 7]

        assertFalse(int1 == int2);
        assertTrue(FixedLongInterval.equals(int1, int2));
        assertTrue(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void compareIntervalTest2(){
        FixedLongInterval int1 = new FixedLongInterval(3, 7, true, true); //[3, 7]
        FixedLongInterval int2 = new FixedLongInterval(3, 7, false, true);//(3, 7]

        assertFalse(int1 == int2);
        assertFalse(FixedLongInterval.equals(int1, int2));
        assertFalse(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void compareIntervalTest3(){
        FixedLongInterval int1 = new FixedLongInterval(0, 1, false, false);//(0, 1)
        FixedLongInterval int2 = new FixedLongInterval(1, 3, true, false); //[1, 3)

        assertFalse(int1 == int2);
        assertFalse(FixedLongInterval.equals(int1, int2));
        assertFalse(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void compareIntervalTest4(){
        FixedLongInterval int1 = new FixedLongInterval(9, 9, true, true);//[9, 9]
        FixedLongInterval int2 = new FixedLongInterval(9, 9, true, true);//[9, 9]

        assertFalse(int1 == int2);
        assertTrue(FixedLongInterval.equals(int1, int2));
        assertTrue(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void compareIntervalTest5(){
        FixedLongInterval int1 = new FixedLongInterval(9, 9, true, false);//[9, 9) empty set
        FixedLongInterval int2 = new FixedLongInterval(9, 9, true, true); //[9, 9]

        assertFalse(int1 == int2);
        assertFalse(FixedLongInterval.equals(int1, int2));
        assertFalse(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void compareIntervalTest6(){
        FixedLongInterval int1 = new FixedLongInterval(9, 9, true, false);//[9, 9) empty set
        FixedLongInterval int2 = new FixedLongInterval(1, 2, false, true);//(1, 2] empty set

        assertFalse(int1 == int2);
        assertFalse(FixedLongInterval.equals(int1, int2));
        assertFalse(FixedLongInterval.equalsStructural(int1, int2));
    }

    @Test
    public void compareIntervalTest7(){
        FixedLongInterval int1 = new FixedLongInterval(9, 9, true, false);//[9, 9) empty set
        FixedLongInterval int2 = new FixedLongInterval(9, 9, false, true);//(9, 9] empty set

        assertFalse(int1 == int2);
        assertTrue(FixedLongInterval.equals(int1, int2)); //empty set is equal to empty set
        assertFalse(FixedLongInterval.equalsStructural(int1, int2)); //primitive internal values are not the same
        }

    @Test
    public void emptySetTest(){
        FixedLongInterval int1 = new FixedLongInterval(1, 1, false, false); //(1, 1)
        FixedLongInterval int2 = new FixedLongInterval(1, 1, false, true);  //(1, 1]
        FixedLongInterval int3 = new FixedLongInterval(1, 1, true, false);  //[1, 1)

        assertEquals("{}", int1.toString());
        assertEquals("{}", int2.toString());
        assertEquals("{}", int3.toString());

        assertTrue(FixedLongInterval.equals(int1, int2));
        assertTrue(FixedLongInterval.equals(int1, int3));
        assertTrue(FixedLongInterval.equals(int2, int3));

        assertFalse(FixedLongInterval.equalsStructural(int1, int2));
        assertFalse(FixedLongInterval.equalsStructural(int1, int3));
        assertFalse(FixedLongInterval.equalsStructural(int2, int3));
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

    @Nested
    public class TestsForEngulf{
        @Test
        public void engulfTest1(){
            FixedLongInterval engulfer = new FixedLongInterval(0, 10);
            FixedLongInterval intv = new FixedLongInterval(2, 8);

            assertTrue(engulfer.engulfs(intv));
            assertFalse(intv.engulfs(engulfer));
        }

        @Test
        public void engulfTest2(){
            FixedLongInterval intv1 = new FixedLongInterval(0, 6);
            FixedLongInterval intv2 = new FixedLongInterval(2, 8);

            assertFalse(intv1.engulfs(intv2));
            assertFalse(intv2.engulfs(intv1));
        }

        @Test
        public void engulfTest3(){
            FixedLongInterval intv1 = new FixedLongInterval(1, 3, true, true);
            FixedLongInterval intv2 = new FixedLongInterval(1, 3, false, false);

            assertTrue(intv1.engulfs(intv2));
            assertFalse(intv2.engulfs(intv1));
        }

        @Test
        public void engulfTest4(){
            FixedLongInterval intv1 = new FixedLongInterval(1, 3, false, false);
            FixedLongInterval intv2 = new FixedLongInterval(1, 3, false, false);

            assertTrue(FixedLongInterval.equals(intv1, intv2)); //if they are equal, they both engulf
            assertTrue(intv1.engulfs(intv2));
            assertTrue(intv2.engulfs(intv1));
        }

        @Test
        public void engulfTest5(){
            FixedLongInterval intv1 = new FixedLongInterval(9, 9, false, true);
            FixedLongInterval intv2 = new FixedLongInterval(3, 3, true, false);

            assertTrue(FixedLongInterval.equals(intv1, intv2)); //if they are equal, they both engulf
            assertEquals("{}", intv1.toString());
            assertEquals("{}", intv1.toString());

            assertTrue(intv1.engulfs(intv2));
            assertTrue(intv2.engulfs(intv1));
        }
    }
}