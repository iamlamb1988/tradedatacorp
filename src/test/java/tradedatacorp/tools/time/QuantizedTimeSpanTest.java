/**
 * @author Bruce Lamb
 * @since 20 APR 2026
 */
package tradedatacorp.tools.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class QuantizedTimeSpanTest{
    @Test
    public void constructorExceptionTest(){
        FixedInterval zeroLengthInterval = new FixedInterval(50, 50);
        assertEquals(0, zeroLengthInterval.durationMillis);
        assertEquals(0, zeroLengthInterval.getIntervalMilli());

        assertThrows(IllegalArgumentException.class, () -> {new QuantizedTimeSpan(zeroLengthInterval);});
    }

    @Test
    public void simpleQuantizedEmptyTest(){
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());

        assertEquals(0, span.getMicroIntervalCount());
        assertTrue(span.isMerged()); //0 span elements IS merged by default.
    }

    @Test
    public void simpleQuantizedOneIntervalTest1(){
        //Adding interval: [1, 6)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:           [^------------------------^)
        //Result:            [S------------------------S)
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval perfectRange = new FixedInterval(1, 6);
        span.addInterval(perfectRange, true, true);

        assertTrue(span.isMerged()); //1 span element IS merged by default.
        assertEquals(1, span.getMicroIntervalCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);
        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(1L, quantizedInt.START_UTC_MILLI);
        assertEquals(6L, quantizedInt.END_UTC_MILLI);
        assertEquals(5L, quantizedInt.durationMillis);
        assertEquals(5L, quantizedInt.getIntervalMilli());
    }

    @Test
    public void simpleQuantizedOneIntervalTest2(){
        //Adding interval: [3, 9]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                     [^-----------------------------^)
        //Result:            [S-------------------------------------------------S)
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(3, 9); //should expand to 1, 11
        span.addInterval(range, true, true);
        assertEquals(2, span.getMicroIntervalCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);
        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(1L, quantizedInt.START_UTC_MILLI);
        assertEquals(11L, quantizedInt.END_UTC_MILLI);
        assertEquals(10L, quantizedInt.durationMillis);
        assertEquals(10L, quantizedInt.getIntervalMilli());
    }

    @Test
    public void simpleQuantizedOneIntervalTest3(){
        //Adding interval: (3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                     (^-----------------------------^)
        //Result:                                     (S------------------------S]
        //NOTE: contract left exclusive but expand Right inclusive

        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(3, 9, false, true);
        span.addInterval(range, false, true);

        assertEquals(1, span.getMicroIntervalCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.START_UTC_MILLI);
        assertEquals(11L, quantizedInt.END_UTC_MILLI);
        assertEquals(5L, quantizedInt.durationMillis);
        assertEquals(5L, quantizedInt.getIntervalMilli());
    }

    @Test
    public void simpleQuantizedOneIntervalTest4(){
        //Adding interval: (3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                     (^-----------------------------^)
        //Result:            [S------------------------S]
        //NOTE: expand left and contract Right both inclusive
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(3, 9, false, false);
        span.addInterval(range, true, false, true, true);

        assertEquals(1, span.getMicroIntervalCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(1L, quantizedInt.START_UTC_MILLI);
        assertEquals(6L, quantizedInt.END_UTC_MILLI);
        assertEquals(5L, quantizedInt.durationMillis);
        assertEquals(5L, quantizedInt.getIntervalMilli());
    }

    @Test
    public void simpleQuantizedOneIntervalTest5(){
        //Adding interval: (3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                     (^-----------------------------^)
        //Result:                                     (S)
        //NOTE: Result (6, 6) is fully empty and will be dropped. Will not be added to the mergable list.
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(3, 9, false, false); //should expand to 1, 11
        span.addInterval(range, false, false);

        assertEquals(0, span.getMicroIntervalCount()); //none are added so size is still 0 given the (6, 6) drop
    }

    @Test
    public void simpleQuantizedOneIntervalTest6(){
        //Adding interval: (3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                     (^-----------------------------^)
        //Result:                                     (S]
        //NOTE: Result (6, 6] will because at least 1 side is inclusive
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(3, 9, false, false);
        span.addInterval(range, false, false, false, true);

        assertEquals(0, span.getMicroIntervalCount()); //NOTE: There are 0 chunked micro intervals in a 0 lenghed time segment.
                                                       //      BUT there is still 1 element in the merge queue
        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.START_UTC_MILLI);
        assertEquals(6L, quantizedInt.END_UTC_MILLI);
        assertEquals(0L, quantizedInt.durationMillis);
        assertEquals(0L, quantizedInt.getIntervalMilli());
    }

    @Test
    public void simpleQuantizedOneIntervalTest7(){
        //Adding interval: (3, 9]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                     (^-----------------------------^]
        //Result:                                     [S)
        //NOTE: Result (6, 6] will because at least 1 side is inclusive
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(3, 9, false, true);
        span.addInterval(range, false, false, true, false);

        assertEquals(0, span.getMicroIntervalCount()); //NOTE: There are 0 chunked micro intervals in a 0 lenghed time segment.
                                                       //      BUT there is still 1 element in the merge queue
        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.START_UTC_MILLI);
        assertEquals(6L, quantizedInt.END_UTC_MILLI);
        assertEquals(0L, quantizedInt.durationMillis);
        assertEquals(0L, quantizedInt.getIntervalMilli());
    }

    @Test
    public void simpleQuantizedOneIntervalTest8(){
        //Adding interval: [3, 9)
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                     (^-----------------------------^]
        //Result:                                     [S]
        //NOTE: Result (6, 6] will because at least 1 side is inclusive
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(3, 9);
        span.addInterval(range, false, false, true, true);

        assertEquals(0, span.getMicroIntervalCount()); //NOTE: There are 0 chunked micro intervals in a 0 lenghed time segment.
                                                       //      BUT there is still 1 element in the merge queue
        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.START_UTC_MILLI);
        assertEquals(6L, quantizedInt.END_UTC_MILLI);
        assertEquals(0L, quantizedInt.durationMillis);
        assertEquals(0L, quantizedInt.getIntervalMilli());
    }

    @Test
    public void simpleQuantizedOneIntervalTest9(){
        //Adding interval: [1, 6]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:           [^------------------------^]
        //Result:            [S------------------------S]
        //NOTE: Despite setting snaps to inclusive, because new range is aligned to snaps, the inclusion state remain unchanged.
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(1, 6, true, true);
        span.addInterval(range, false, false);

        assertEquals(1, span.getMicroIntervalCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(1L, quantizedInt.START_UTC_MILLI);
        assertEquals(6L, quantizedInt.END_UTC_MILLI);
        assertEquals(5L, quantizedInt.durationMillis);
        assertEquals(5L, quantizedInt.getIntervalMilli());
    }

    @Test
    public void simpleQuantizedOneIntervalTest10(){
        //Adding interval: [8, 9]
        //Snap offsets:       v                        v                        v
        //Time Line:  ... 0 | 1  | 2  | 3  | 4  | 5  | 6  | 7  | 8  | 9  | 10 | 11 | 12
        //Current:                                              [^----^]
        //Result: NONE
        //NOTE: will have a criss cross star and end and will be rejected.
        FixedInterval micro = new FixedInterval(1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(8, 9, true, true);
        span.addInterval(range, false, false);

        assertEquals(0, span.getMicroIntervalCount());
    }

    @Test
    public void simpleQuantizedOneIntervalTest11(){
        //Adding interval: (-5, -1]
        //Snap offsets:         v         v         v          v
        //Time Line:  ...  -5 | -4 | -3 | -2 | -1 | 0  |  1 |  2
        //Current:        (^-------------------^]
        //Result:              (S---------S)
        //NOTE: contract both sides exclusively
        FixedInterval micro = new FixedInterval(6, 8);
        assertEquals(2L, micro.durationMillis);
        assertEquals(2L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval(-5, -1, false, true);
        span.addInterval(range, false, false, false, false);
        assertEquals(1, span.getMicroIntervalCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(-4L, quantizedInt.START_UTC_MILLI);
        assertEquals(-2L, quantizedInt.END_UTC_MILLI);
        assertEquals(2L, quantizedInt.durationMillis);
        assertEquals(2L, quantizedInt.getIntervalMilli());
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

        FixedInterval micro = new FixedInterval(6, 8);
        assertEquals(2L, micro.durationMillis);
        assertEquals(2L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        FixedInterval perfectSnap1 = new FixedInterval(-4, 0, false, true);
        FixedInterval perfectSnap2 = new FixedInterval(-2, 4, false, false);

        span.addInterval(perfectSnap1, true, true);
        span.addInterval(perfectSnap2, true, true);

        assertFalse(span.isMerged());
        span.mergeTimeSpan();
        assertTrue(span.isMerged());

        //4 interval chunks: <-4, -2>, <-2, 0>, <0, 2>, <2, 4>
        assertEquals(4, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(-4L, quantizedInt.START_UTC_MILLI);
        assertEquals(4L, quantizedInt.END_UTC_MILLI);
        assertEquals(8L, quantizedInt.durationMillis);
        assertEquals(8L, quantizedInt.getIntervalMilli());
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

        FixedInterval micro = new FixedInterval(-1, 2);
        assertEquals(3L, micro.durationMillis);
        assertEquals(3L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        FixedInterval int1 = new FixedInterval(0, 3, false, true);
        FixedInterval int2 = new FixedInterval(1, 7, false, false);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, false, true, true, true);

        assertFalse(span.isMerged());
        span.mergeTimeSpan();
        assertTrue(span.isMerged());

        //3 interval chunks: <-1, 2>, <2, 5>, <5, 8>
        assertEquals(3, span.getMicroIntervalCount());
        assertEquals(1, span.getIntervalSegmentCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertFalse(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(-1L, quantizedInt.START_UTC_MILLI);
        assertEquals(8L, quantizedInt.END_UTC_MILLI);
        assertEquals(9L, quantizedInt.durationMillis);
        assertEquals(9L, quantizedInt.getIntervalMilli());
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

        FixedInterval micro = new FixedInterval(0, 1);
        assertEquals(1L, micro.durationMillis);
        assertEquals(1L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        FixedInterval int1 = new FixedInterval(0, 3, true, false);
        FixedInterval int2 = new FixedInterval(3, 3, false, true);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, false, true, true, true);

        assertFalse(span.isMerged());
        span.mergeTimeSpan();
        assertTrue(span.isMerged());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.START_UTC_MILLI);
        assertEquals(3L, quantizedInt.END_UTC_MILLI);
        assertEquals(3L, quantizedInt.durationMillis);
        assertEquals(3L, quantizedInt.getIntervalMilli());
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

        FixedInterval micro = new FixedInterval(0, 1);
        assertEquals(1L, micro.durationMillis);
        assertEquals(1L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        FixedInterval int1 = new FixedInterval(0, 3, true, false);
        FixedInterval int2 = new FixedInterval(3, 3, true, false);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, true, true, true, true);

        assertFalse(span.isMerged());
        span.mergeTimeSpan();
        assertTrue(span.isMerged());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.START_UTC_MILLI);
        assertEquals(3L, quantizedInt.END_UTC_MILLI);
        assertEquals(3L, quantizedInt.durationMillis);
        assertEquals(3L, quantizedInt.getIntervalMilli());
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

        FixedInterval micro = new FixedInterval(0, 1);
        assertEquals(1L, micro.durationMillis);
        assertEquals(1L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        FixedInterval int1 = new FixedInterval(0, 3, true, false);
        FixedInterval int2 = new FixedInterval(3, 3, true, true);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, true, true, true, true);

        assertFalse(span.isMerged());
        span.mergeTimeSpan();
        assertTrue(span.isMerged());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertTrue(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.START_UTC_MILLI);
        assertEquals(3L, quantizedInt.END_UTC_MILLI);
        assertEquals(3L, quantizedInt.durationMillis);
        assertEquals(3L, quantizedInt.getIntervalMilli());
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

        FixedInterval micro = new FixedInterval(0, 1);
        assertEquals(1L, micro.durationMillis);
        assertEquals(1L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        FixedInterval int1 = new FixedInterval(0, 3, true, false);
        FixedInterval int2 = new FixedInterval(3, 3, false, false);

        span.addInterval(int1, true, true, false, false);
        span.addInterval(int2, true, true, true, true); //Empty and will be rejected upon addition

        assertTrue(span.isMerged());

        assertEquals(1, span.getIntervalSegmentCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);

        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.START_UTC_MILLI);
        assertEquals(3L, quantizedInt.END_UTC_MILLI);
        assertEquals(3L, quantizedInt.durationMillis);
        assertEquals(3L, quantizedInt.getIntervalMilli());
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

        FixedInterval micro = new FixedInterval(0, -3);
        assertEquals(3L, micro.durationMillis);
        assertEquals(3L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        FixedInterval int1 = new FixedInterval(0, 3, true, false);
        FixedInterval int2 = new FixedInterval(6, 9, false, false);

        span.addInterval(int1, true, true, false, false); //Snapped, expansions and inclusions not relevant
        span.addInterval(int2, true, true, true, true);   //Snapped, expansions and inclusions not relevant

        assertFalse(span.isMerged());
        span.mergeTimeSpan();
        assertTrue(span.isMerged());

        assertEquals(2, span.getIntervalSegmentCount());
        assertEquals(2, span.getMicroIntervalCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);
        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.START_UTC_MILLI);
        assertEquals(3L, quantizedInt.END_UTC_MILLI);
        assertEquals(3L, quantizedInt.durationMillis);
        assertEquals(3L, quantizedInt.getIntervalMilli());

        quantizedInt = span.getTimeSpanInterval(1);
        assertFalse(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(6L, quantizedInt.START_UTC_MILLI);
        assertEquals(9L, quantizedInt.END_UTC_MILLI);
        assertEquals(3L, quantizedInt.durationMillis);
        assertEquals(3L, quantizedInt.getIntervalMilli());
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

        FixedInterval micro = new FixedInterval(0, -3);
        assertEquals(3L, micro.durationMillis);
        assertEquals(3L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        FixedInterval int1 = new FixedInterval(0, 3, true, false);
        FixedInterval int2 = new FixedInterval(6, 9, false, false);
        FixedInterval int3 = new FixedInterval(2, 7, true, true);

        span.addInterval(int1, true, true, false, false); //Snapped, expansions and inclusions not relevant
        span.addInterval(int2, true, true, true, true);   //Snapped, expansions and inclusions not relevant
        span.addInterval(int3, false, true, true, false);   // will snap to [3, 9)

        assertFalse(span.isMerged());
        span.mergeTimeSpan();
        assertTrue(span.isMerged());

        assertEquals(1, span.getIntervalSegmentCount());
        assertEquals(3, span.getMicroIntervalCount());

        FixedInterval quantizedInt = span.getTimeSpanInterval(0);
        assertTrue(quantizedInt.inclusiveStart);
        assertFalse(quantizedInt.inclusiveEnd);
        assertEquals(0L, quantizedInt.START_UTC_MILLI);
        assertEquals(9L, quantizedInt.END_UTC_MILLI);
        assertEquals(9L, quantizedInt.durationMillis);
        assertEquals(9L, quantizedInt.getIntervalMilli());
    }
}