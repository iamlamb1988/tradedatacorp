/**
 * @author Bruce Lamb
 * @since 23 APR 2026
 */
package tradedatacorp.tools.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AlignedLongSetTest{
    @Test
    public void constructorExceptionTest(){
        FixedLongInterval zeroLengthInterval = new FixedLongInterval(50, 50); //default [incl, excl) endpoints
        assertEquals(0, zeroLengthInterval.width);
        assertEquals(0, zeroLengthInterval.getWidth());

        assertThrows(IllegalArgumentException.class, () -> {new AlignedLongSet(zeroLengthInterval);});
    }

    @Test
    public void simpleQuantizedEmptyTest(){
        FixedLongInterval micro = new FixedLongInterval(1, 6); //default [incl, excl) endpoints
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());

        assertEquals(0, span.getMicroIntervalCount());
        assertTrue(span.isMerged()); //0 span elements IS merged by default.
    }

    @Test
    public void simpleQuantizedOneIntervalTest1(){
        //Micro interval:  [1, 6)
        //Adding interval: [1, 6)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12 ...
        //Current:       <[< [^------------------------^) >)>
        //Result:            [^S----------------------S^)
        //Note: perfectly snapped, no changes required
        FixedLongInterval micro = new FixedLongInterval(1, 6); //default [incl, excl) endpoints
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        //Keypoint: because this original range has both endpoints snapped, the addInterval boolean parameters are irrelevant.
        FixedLongInterval perfectRange = new FixedLongInterval(1, 6); //default [incl, excl) endpoints
        span.addInterval(
            perfectRange,
            true, //from left endpoint: Expand leftward to snap IGNORED because already snapped
            true  //from right endpoint: Expand rightward to snap IGNORED because already snapped
            //default true  //inclusive left endpoint at next snap (match original inclusive val) IGNORED because already snapped
            //default false //exclusive right endpoint at next snap (match original inclusive val) IGNORED because already snapped
        );

        assertTrue(span.isMerged()); //1 span element IS merged by default.
        assertEquals(1, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);
        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(1L, quantizedInt.start);
        assertEquals(6L, quantizedInt.end);
        assertEquals(5L, quantizedInt.width);
        assertEquals(5L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedOneIntervalTest2(){
        //Micro interval:  [1, 6)
        //Adding interval: [3, 9]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12 ...
        //Current:                 <[< [^-----------------------------^] >]>
        //Result:            [^S-----------------------------------------------S^)
        //Note: interval is set to expand IF not already snapped.
        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(3, 9, true, true); //[incl, incl] endpoints
        span.addInterval( //Default: snap endpoints default to original interval
            range,
            true, //from left endpoint: Expand leftward to snap
            true, //from right endpoint: Expand rightward to snap
            true, //inclusive left endpoint at next snap
            false //exclusive right endpoint at next snap
        );
        assertEquals(2, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);
        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(1L, quantizedInt.start);
        assertEquals(11L, quantizedInt.end);
        assertEquals(10L, quantizedInt.width);
        assertEquals(10L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedOneIntervalTest3(){
        //Micro interval:  [1, 6)
        //Adding interval: (3, 9]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                 >(> (^-----------------------------^] >]>
        //Result:                                     (^S----------------------S^]
        //NOTE: contract left exclusive, expand Right inclusive

        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(3, 9, false, true); //(excl, incl]
        span.addInterval( //Default: snap endpoints default to original interval (in this case (excl, incl])
            range,
            false, //from left endpoint: Contract rightward to snap
            true   //from right endpoint: Expand rightward to snap
            //default false //exclusive left endpoint at next snap (match original inclusive val)
            //default true  //inclusive right endpoint at next snap (match original inclusive val)
        );

        assertEquals(1, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.start);
        assertEquals(11L, quantizedInt.end);
        assertEquals(5L, quantizedInt.width);
        assertEquals(5L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedOneIntervalTest4(){
        //Micro interval:  [1, 6)
        //Adding interval: (3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                 <[< (^-----------------------------^) <]<
        //Result:            [^S----------------------S^]
        //NOTE: expand left and contract Right both inclusive
        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(3, 9, false, false); //(excl, excl)
        span.addInterval(range,
            true,  //from left endpoint: Expand leftward to snap
            false, //from right endpoint: Contract leftward to snap
            true,  //inclusive left endpoint at next snap
            true   //inclusive right endpoint at next snap
        );

        assertEquals(1, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(1L, quantizedInt.start);
        assertEquals(6L, quantizedInt.end);
        assertEquals(5L, quantizedInt.width);
        assertEquals(5L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedOneIntervalTest5(){
        //Micro interval:  [1, 6)
        //Adding interval: (3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                 >(> (^-----------------------------^) <)<
        //Result:                                     (S) //will not add empty exclusive value
        //NOTE: Result (6, 6) is fully empty and will be dropped. Will not be added to the mergable list.
        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(3, 9, false, false); //(excl, excl)
        span.addInterval(
            range,
            false, //from left endpoint: Contract rightward to snap
            false  //from right endpoint: Contract leftward to snap
            //default false //exclusive left endpoint at next snap (match original inclusive val)
            //default false //exclusive right endpoint at next snap (match original inclusive val)
        );

        assertEquals(0, span.getMicroIntervalCount()); //none are added so size is still 0 given the (6, 6) drop
        assertEquals(0, span.getIntervalSegmentCount());
    }

    @Test
    public void simpleQuantizedOneIntervalTest6(){
        //Micro interval:  [1, 6)
        //Adding interval: (3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                 >(> (^-----------------------------^) <]<
        //Result:                                     (S]
        //NOTE: Result (6, 6] will be added because at least 1 side is inclusive
        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(3, 9, false, false); //(excl, excl)
        span.addInterval(
            range,
            false, //from left endpoint: Contract rightward to snap
            false, //from right endpoint: Contract leftward to snap
            false, //exclusive left endpoint at next snap
            true   //inclusive right endpoint at next snap
        );

        assertEquals(0, span.getMicroIntervalCount()); //NOTE: There are 0 chunked micro intervals in a 0 lenghed time segment.
                                                       //      BUT there is still 1 element in the merge queue
        assertEquals(1, span.getIntervalSegmentCount()); //1 segment of 0 width
        FixedLongInterval quantizedInt = span.getInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.start);
        assertEquals(6L, quantizedInt.end);
        assertEquals(0L, quantizedInt.width);
        assertEquals(0L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedOneIntervalTest7(){
        //Micro interval:  [1, 6)
        //Adding interval: (3, 9]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                 >[> (^-----------------------------^] <)<
        //Result:                                     [S)
        //NOTE: Result [6, 6) will be added because at least 1 side is inclusive
        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(3, 9, false, true); //(excl, incl]
        span.addInterval(
            range,
            false, //from left endpoint: Contract rightward to snap
            false, //from right endpoint: Contract leftward to snap
            true,  //inclusive left endpoint at next snap
            false  //exclusive left endpoint at next snap
        );

        assertEquals(0, span.getMicroIntervalCount()); //NOTE: There are 0 chunked micro intervals in a 0 lenghed time segment.
                                                       //      BUT there is still 1 element in the merge queue
        assertEquals(1, span.getIntervalSegmentCount()); //1 segment of 0 width

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.start);
        assertEquals(6L, quantizedInt.end);
        assertEquals(0L, quantizedInt.width);
        assertEquals(0L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedOneIntervalTest8(){
        //Micro interval:  [1, 6)
        //Adding interval: [3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                     [^-----------------------------^)
        //Result:                                     [S]
        //NOTE: Result [6, 6] will be added because at least 1 side is inclusive
        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(3, 9); //[incl, excl)
        span.addInterval(
            range,
            false, //from left endpoint: Contract rightward to snap
            false, //from right endpoint: Contract leftward to snap
            true,  //inclusive left endpoint at next snap
            true   //inclusive right endpoint at next snap
        );

        assertEquals(0, span.getMicroIntervalCount()); //NOTE: There are 0 chunked micro intervals in a 0 lenghed time segment.
                                                       //      BUT there is still 1 element in the merge queue
        assertEquals(1, span.getIntervalSegmentCount()); //1 segment of 0 width
        FixedLongInterval quantizedInt = span.getInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.start);
        assertEquals(6L, quantizedInt.end);
        assertEquals(0L, quantizedInt.width);
        assertEquals(0L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedOneIntervalTest9(){
        //Micro interval:  [1, 6)
        //Adding interval: [1, 6]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:       >(> [^------------------------^] <)<
        //Result:            [^S----------------------S^]
        //NOTE: Despite setting snaps to inclusive, because new range is aligned to snaps, the inclusion state remain unchanged.
        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        //Key takeaway: when adding and requesting snap inclusive changes, they are ignored and irrelevant because range is already snapped.
        FixedLongInterval range = new FixedLongInterval(1, 6, true, true);//[incl, incl]
        span.addInterval(
            range,
            false, //from left endpoint: Contract rightward to snap IGNORED because already snapped
            false, //from right endpoint: Contract leftward to snap IGNORED because already snapped
            false, //exclusive left endpoint at next snap IGNORED because already snapped
            false  //exclusive right endpoint at next snap IGNORED because already snapped
        );

        assertEquals(1, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(1L, quantizedInt.start);
        assertEquals(6L, quantizedInt.end);
        assertEquals(5L, quantizedInt.width);
        assertEquals(5L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedOneIntervalTest10(){
        //Micro interval:  [1, 6)
        //Adding interval: [8, 9]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                                          >[> [^----^] <]<
        //Result: NONE
        //NOTE: will have a criss cross start and end. Will be rejected.
        // start: 8 -> 11 and 9 -> 6. start cannot be greater than end -> reject
        FixedLongInterval micro = new FixedLongInterval(1, 6);
        assertEquals(5L, micro.width);
        assertEquals(5L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(8, 9, true, true);//[incl, incl]
        span.addInterval(
            range,
            false, //from left endpoint: Contract rightward to snap
            false  //from right endpoint: Contract leftward to snap
            //default true: //inclusive left endpoint at next snap
            //default true: //inclusive right endpoint at next snap
        );

        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());
    }

    @Test
    public void simpleQuantizedOneIntervalTest11(){
        //Micro interval:  [6, 8)
        //Adding interval: (-5, -1]
        //Snap offsets:         v         v         v          v
        //Time Line:  ...  -5 | -4 | -3 | -2 | -1 | 0  |  1 |  2
        //Current:       >(> (^-------------------^] <)<
        //Result:              (^S-------S^)
        //NOTE: contract both sides exclusively
        FixedLongInterval micro = new FixedLongInterval(6, 8);
        assertEquals(2L, micro.width);
        assertEquals(2L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());
        assertEquals(0, span.getIntervalSegmentCount());

        FixedLongInterval range = new FixedLongInterval(-5, -1, false, true);//(excl, incl]
        span.addInterval(
            range,
            false, //from left endpoint: Contract rightward to snap
            false, //from right endpoint: Contract leftward to snap
            false, //exclusive left endpoint at next snap
            false  //exclusive right endpoint at next snap
        );
        assertEquals(1, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(-4L, quantizedInt.start);
        assertEquals(-2L, quantizedInt.end);
        assertEquals(2L, quantizedInt.width);
        assertEquals(2L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedTwoIntervalTest1(){
        //Adding interval: (-4, 0]
        //                 (-2, 4)
        //Snap offsets:         v         v         v         v         v
        //Time Line:  ...  -5 | -4 | -3 | -2 | -1 | 0  | 1  | 2  | 3  | 4
        //Current:              (^------------------^]
        //                               (^-----------------------------^)
        //Result:               (S--------------------------------------S)
        //NOTE: merge perfect snaps

        FixedLongInterval micro = new FixedLongInterval(6, 8);
        assertEquals(2L, micro.width);
        assertEquals(2L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval perfectSnap1 = new FixedLongInterval(-4, 0, false, true);
        FixedLongInterval perfectSnap2 = new FixedLongInterval(-2, 4, false, false);

        span.addInterval(perfectSnap1, true, true);
        span.addInterval(perfectSnap2, true, true);

        assertFalse(span.isMerged());
        span.merge();
        assertTrue(span.isMerged());

        //4 interval chunks: <-4, -2>, <-2, 0>, <0, 2>, <2, 4>
        assertEquals(4, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(-4L, quantizedInt.start);
        assertEquals(4L, quantizedInt.end);
        assertEquals(8L, quantizedInt.width);
        assertEquals(8L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedTwoIntervalTest2(){
        //Adding interval: (0, 3]
        //                 (1, 7)
        //Snap offsets:    v               v              v              v
        //Time Line:  ...  -1  | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8
        //Current:          <(< (^i1----------i1^] >)>
        //Snap:            (^S1-------------------------S1^)
        //                       >[> (^i2-------------------------i2^) >]>
        //                                (^S2-------------------------S2^]
        //Result:          (^R------------------------------------------R^]
        //NOTE: int1 expand left (exclude) expand right (exclude)
        //      int2 contract left (include) expand right (include)

        FixedLongInterval micro = new FixedLongInterval(-1, 2);
        assertEquals(3L, micro.width);
        assertEquals(3L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval int1 = new FixedLongInterval(0, 3, false, true);
        FixedLongInterval int2 = new FixedLongInterval(1, 7, false, false);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, false, true, true, true);

        assertFalse(span.isMerged());
        span.merge();
        assertTrue(span.isMerged());

        //3 interval chunks: <-1, 2>, <2, 5>, <5, 8>
        assertEquals(3, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(-1L, quantizedInt.start);
        assertEquals(8L, quantizedInt.end);
        assertEquals(9L, quantizedInt.width);
        assertEquals(9L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedTwoIntervalTest3(){
        //Adding interval: [0, 3)
        //                 (3, 3]
        //Snap offsets:    v    v    v    v    v    v    v    v    v    v
        //Time Line:  ...  -1 | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8
        //Current:         <(< [^i1----------i1^] >)>
        //Snap:                [^S1----------S1^]
        //                                >[> (^i2] >]>
        //                                    (^S2]
        //Result:              [^R------------R^]
        //NOTE: inclusive flags will not change due to perfect snaps

        FixedLongInterval micro = new FixedLongInterval(0, 1);
        assertEquals(1L, micro.width);
        assertEquals(1L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval int1 = new FixedLongInterval(0, 3, true, false);
        FixedLongInterval int2 = new FixedLongInterval(3, 3, false, true);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, false, true, true, true);

        assertFalse(span.isMerged());
        span.merge();
        assertTrue(span.isMerged());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.start);
        assertEquals(3L, quantizedInt.end);
        assertEquals(3L, quantizedInt.width);
        assertEquals(3L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedTwoIntervalTest4(){
        //Adding interval: [0, 3)
        //                 [3, 3)
        //Snap offsets:    v    v    v    v    v    v    v    v    v    v
        //Time Line:  ...  -1 | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8
        //Current:         <(< [^i1----------i1^] >)>
        //Snap:                [^S1----------S1^]
        //                                <[< [^i2) >]>
        //                                    [^S2)
        //Result:              [^R------------R^]
        //NOTE: inclusive flags will not change due to perfect snaps

        FixedLongInterval micro = new FixedLongInterval(0, 1);
        assertEquals(1L, micro.width);
        assertEquals(1L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval int1 = new FixedLongInterval(0, 3, true, false);
        FixedLongInterval int2 = new FixedLongInterval(3, 3, true, false);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, true, true, true, true);

        assertFalse(span.isMerged());
        span.merge();
        assertTrue(span.isMerged());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.start);
        assertEquals(3L, quantizedInt.end);
        assertEquals(3L, quantizedInt.width);
        assertEquals(3L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedTwoIntervalTest5(){
        //Adding interval: [0, 3)
        //                 [3, 3]
        //Snap offsets:    v    v    v    v    v    v    v    v    v    v
        //Time Line:  ...  -1 | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8
        //Current:         <(< [^i1----------i1^) >)>
        //Snap:                [^S1----------S1^)
        //                                <[< [^i2] >]>
        //                                    [^S2]
        //Result:              [^R------------R^]
        //NOTE: inclusive flags will not change due to perfect snaps

        FixedLongInterval micro = new FixedLongInterval(0, 1);
        assertEquals(1L, micro.width);
        assertEquals(1L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval int1 = new FixedLongInterval(0, 3, true, false);
        FixedLongInterval int2 = new FixedLongInterval(3, 3, true, true);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, true, true, true, true);

        assertFalse(span.isMerged());
        span.merge();
        assertTrue(span.isMerged());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.start);
        assertEquals(3L, quantizedInt.end);
        assertEquals(3L, quantizedInt.width);
        assertEquals(3L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedTwoIntervalTest6(){
        //Adding interval: [0, 3)
        //                 (3, 3)
        //Snap offsets:    v    v    v    v    v    v    v    v    v    v
        //Time Line:  ...  -1 | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8
        //Current:         <(< [^i1----------i1^) >)>
        //Snap:                [^S1----------S1^)
        //                                <[< (^i2) >]>
        //                                    (^S2)
        //Result:              [^R------------R^)
        //NOTE: inclusive flags will not change due to perfect snaps

        FixedLongInterval micro = new FixedLongInterval(0, 1);
        assertEquals(1L, micro.width);
        assertEquals(1L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval int1 = new FixedLongInterval(0, 3, true, false);
        FixedLongInterval int2 = new FixedLongInterval(3, 3, false, false);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, true, true, true, true); //Empty and will be rejected upon addition

        assertTrue(span.isMerged());

        assertEquals(1, span.getIntervalSegmentCount());

        FixedLongInterval quantizedInt = span.getInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.start);
        assertEquals(3L, quantizedInt.end);
        assertEquals(3L, quantizedInt.width);
        assertEquals(3L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedTwoIntervalTest7(){
        //Adding interval: [0, 3)
        //                 (6, 9)
        //Snap offsets:         v              v              v              v
        //Time Line:  ...  -1 | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9
        //Current:             [^S1----------S1^)
        //                                                   (^S2----------S2^)
        //Result:              [^R------------R^)            (^R------------R^)
        //NOTE: Merged and gap. 2 intervals will remain merged

        FixedLongInterval micro = new FixedLongInterval(0, -3);
        assertEquals(3L, micro.width);
        assertEquals(3L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval int1 = new FixedLongInterval(0, 3, true, false);
        FixedLongInterval int2 = new FixedLongInterval(6, 9, false, false);

        span.addInterval(int1, true, true, false, false); //Snapped, expansions and inclusions not relevant
        span.addInterval(int2, true, true, true, true);   //Snapped, expansions and inclusions not relevant

        assertFalse(span.isMerged());
        span.merge();
        assertTrue(span.isMerged());

        assertEquals(2, span.getIntervalSegmentCount());
        assertEquals(2, span.getMicroIntervalCount());

        FixedLongInterval quantizedInt = span.getInterval(0);
        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.start);
        assertEquals(3L, quantizedInt.end);
        assertEquals(3L, quantizedInt.width);
        assertEquals(3L, quantizedInt.getWidth());

        quantizedInt = span.getInterval(1);
        assertFalse(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.start);
        assertEquals(9L, quantizedInt.end);
        assertEquals(3L, quantizedInt.width);
        assertEquals(3L, quantizedInt.getWidth());
    }

    @Test
    public void simpleQuantizedThreeIntervalTest1(){
        //Adding interval: [0, 3)
        //                 (6, 9)
        //                 [2, 7]
        //Snap offsets:         v              v              v              v
        //Time Line:  ...  -1 | 0  | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9
        //Current:             [^S1----------S1^)
        //                                                   (^S2----------S2^)
        //                           >[> [^i3--------------------i3^] >)>
        //Result:              [^R------------------------------------------R^)
        //NOTE: Merged and gap. 2 intervals will remain merged

        FixedLongInterval micro = new FixedLongInterval(0, -3);
        assertEquals(3L, micro.width);
        assertEquals(3L, micro.getWidth());

        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval int1 = new FixedLongInterval(0, 3, true, false);
        FixedLongInterval int2 = new FixedLongInterval(6, 9, false, false);
        FixedLongInterval int3 = new FixedLongInterval(2, 7, true, true);

        span.addInterval(int1, true, true, false, false); //Snapped, expansions and inclusions not relevant
        span.addInterval(int2, true, true, true, true);   //Snapped, expansions and inclusions not relevant
        span.addInterval(int3, false, true, true, false);   // will snap to [3, 9)

        assertFalse(span.isMerged());
        span.merge();
        assertTrue(span.isMerged());

        assertEquals(1, span.getIntervalSegmentCount());
        assertEquals(3, span.getMicroIntervalCount());

        FixedLongInterval quantizedInt = span.getInterval(0);
        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.start);
        assertEquals(9L, quantizedInt.end);
        assertEquals(9L, quantizedInt.width);
        assertEquals(9L, quantizedInt.getWidth());
    }

    @Test
    public void subtractIntervalTest1(){
        //Current Set: (2, 11]
        //Subtract Set: [5, 8]
        //Result Set: (2, 5) U (8, 11]
        //Snap offsets:        v              v              v              v
        //Time Line:  ... 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:            (^M------------------------------------------M^]
        //Unsnapped sub:                          [P]
        //Snapped sub:                       [^P------------P^]
        //Result:             (^M------------M^)            (^M------------M^]

        FixedLongInterval micro = new FixedLongInterval(2, 5, true, true);
        assertEquals(3L, micro.width);
        assertEquals(3L, micro.getWidth());
        assertEquals("[2,5]", micro.toString());

        //1. Setup current/original
        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval original = new FixedLongInterval(2, 11, false, true); //(2, 11]
        span.addInterval(original);
        assertEquals("(2,11]", original.toString());

        FixedLongInterval subtract = new FixedLongInterval(5, 8, true, true); //[5, 8]
        assertEquals("[5,8]", subtract.toString());

        FixedLongInterval expectedLeftSet = new FixedLongInterval(2, 5, false, false); //(2, 5)
        FixedLongInterval expectedRightSet = new FixedLongInterval(8, 11, false, true);//(8, 11]

        AlignedLongSet expectedSpan = new AlignedLongSet(
            micro,
            expectedLeftSet,
            expectedRightSet
        );
        assertEquals("(2,5)U(8,11]", expectedSpan.toString());

        //2. Execution
        span.subtractInterval(subtract);

        //3. Check expectations
        assertEquals(2, span.getIntervalSegmentCount());
        assertEquals(2, span.getMicroIntervalCount());

        assertTrue(FixedLongInterval.equals(expectedLeftSet, span.getInterval(0)));
        assertTrue(FixedLongInterval.equals(expectedRightSet, span.getInterval(1)));

        //Primary check
        assertTrue(AlignedLongSet.equals(expectedSpan, span));
        assertEquals("(2,5)U(8,11]", span.toString());
    }

    @Test
    public void subtractIntervalTest2(){
        //Current Set: (2, 11]
        //Subtract interval: {6} -> snaps -> [5, 8]
        //Snap offsets:        v              v              v              v
        //Time Line:  ... 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:            (^M------------------------------------------M^]
        //Unsnapped sub:                          (P]
        //Snapped sub:                       (^P------------P^]
        //Result:             (^M------------M^]            (^M------------M^]

        FixedLongInterval micro = new FixedLongInterval(2, 5, true, true);
        assertEquals(3L, micro.width);
        assertEquals(3L, micro.getWidth());
        assertEquals("[2,5]", micro.toString());

        //1. Setup current/original
        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval original = new FixedLongInterval(2, 11, false, true); //(2, 11]
        span.addInterval(original);
        assertEquals("(2,11]", original.toString());

        FixedLongInterval subtract = new FixedLongInterval(6, 6, true, true); //[5, 8]
        assertEquals("{6}", subtract.toString());
        span.subtractInterval(subtract, true, true, false ,true); //expands to (5, 8] prior to subtraction

        FixedLongInterval expectedLeftSet = new FixedLongInterval(2, 5, false, true); //(2, 5]
        FixedLongInterval expectedRightSet = new FixedLongInterval(8, 11, false, true);//(8, 11]

        AlignedLongSet expectedSpan = new AlignedLongSet(
            micro,
            expectedLeftSet,
            expectedRightSet
        );
        assertEquals("(2,5]U(8,11]", expectedSpan.toString());

        //2. Execution
        span.subtractInterval(subtract);

        //3. Check expectations
        assertEquals(2, span.getIntervalSegmentCount());
        assertEquals(2, span.getMicroIntervalCount());

        assertTrue(FixedLongInterval.equals(expectedLeftSet, span.getInterval(0)));
        assertTrue(FixedLongInterval.equals(expectedRightSet, span.getInterval(1)));

        assertTrue(AlignedLongSet.equals(expectedSpan, span));
    }

    @Test
    public void subtractIntervalTest3(){
        //Current Set: (2, 11]
        //Subtract interval: {5} // exactly snapped
        //Snap offsets:        v              v              v              v
        //Time Line:  ... 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:            (^M------------------------------------------M^]
        //Unsnapped sub:                     [P]
        //Snapped sub:                       [P]
        //Result:             (^M------------M^)(M------------M^]

        FixedLongInterval micro = new FixedLongInterval(2, 5, true, true);
        assertEquals(3L, micro.width);
        assertEquals(3L, micro.getWidth());
        assertEquals("[2,5]", micro.toString());

        //1. Setup current/original
        AlignedLongSet span = new AlignedLongSet(micro);
        FixedLongInterval original = new FixedLongInterval(2, 11, false, true); //(2, 11]
        span.addInterval(original);
        assertEquals("(2,11]", original.toString());

        FixedLongInterval subtract = new FixedLongInterval(5, 5, true, true); //{5}
        assertEquals("{5}", subtract.toString());
        span.subtractInterval(subtract, true, true, false ,true); //No change, booleans irrelevant, remains {5}

        FixedLongInterval expectedLeftSet = new FixedLongInterval(2, 5, false, false); //(2, 5)
        FixedLongInterval expectedRightSet = new FixedLongInterval(5, 11, false, true);//(8, 11]

        AlignedLongSet expectedSpan = new AlignedLongSet(
            micro,
            expectedLeftSet,
            expectedRightSet
        );
        assertEquals("(2,5)U(5,11]", expectedSpan.toString());

        //2. Execution
        span.subtractInterval(subtract);

        //3. Check expectations
        assertEquals(2, span.getIntervalSegmentCount());
        assertEquals(2, span.getMicroIntervalCount());

        assertTrue(FixedLongInterval.equals(expectedLeftSet, span.getInterval(0)));
        assertTrue(FixedLongInterval.equals(expectedRightSet, span.getInterval(1)));

        assertTrue(AlignedLongSet.equals(expectedSpan, span));
        assertEquals("(2,5)U(5,11]", span.toString());
    }

    @Test
    public void subtractIntervalTest4(){
        //TODO
        //Micro interval: [2, 5]
        //Current Set: (2, 11]
        //Subtract interval: (5] // exactly snapped
        //Snap offsets:        v              v              v              v
        //Time Line:  ... 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:            (^M------------------------------------------M^]
        //Unsnapped sub:                     (P]
        //Snapped sub:                       (P]
        //Result:             (^M------------------------------------------M^] //NO CHANGE

        //TODO The resultant merge should remain (2, 11]
        //step 1: Expand interval to snaps (if necessary)
        //step 2: Subtract the interval from current state

        //BOTH subtraction points must be inclusive to truly subtract a single point.
    }

    @Test
    public void subtractIntervalTest5(){
        //TODO
        //Micro interval: [2, 5]
        //Current Set: (2, 8)
        //Subtract interval: (7, 10) // exactly snapped
        //Snap offsets:        v              v              v              v
        //Time Line:  ... 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:            (^M---------------------------M^)
        //Unsnapped sub:                               (^P------------P^)
        //Snapped sub:                       (^P---------------------------P^)
        //Result:             (^M------------M^]

        //TODO The resultant merge should remain (2, 11]
        //step 1: Expand interval to snaps (if necessary)
        //step 2: Subtract the interval from current state

        //Result will leave (2, 5]
    }
}