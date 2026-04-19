package tradedatacorp.tools.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

// import org.junit.jupiter.api.Nested;
// import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 
 */
public class QuantizedTimeSpanTest{
    @Test
    public void constructorExceptionTest(){
        FixedInterval zeroLengthInterval = new FixedInterval("micro", 50, 50);
        assertEquals(0, zeroLengthInterval.durationMillis);
        assertEquals(0, zeroLengthInterval.getIntervalMilli());

        assertThrows(IllegalArgumentException.class, () -> {new QuantizedTimeSpan(zeroLengthInterval);});
    }

    @Test
    public void simpleQuantizedEmptyTest(){
        FixedInterval micro = new FixedInterval("micro", 1, 6);
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval perfectRange = new FixedInterval("int1", 1, 6);
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 3, 9); //should expand to 1, 11
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

        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 3, 9, false, true);
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 3, 9, false, false);
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 3, 9, false, false); //should expand to 1, 11
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 3, 9, false, false);
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 3, 9, false, true);
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 3, 9);
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 1, 6, true, true);
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
        FixedInterval micro = new FixedInterval("micro", 1, 6);
        assertEquals(5L, micro.durationMillis);
        assertEquals(5L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", 8, 9, true, true);
        span.addInterval(range, false, false);

        assertEquals(0, span.getMicroIntervalCount());
    }

    @Test
    public void simpleQuantizedOneIntervalTest11(){
        //Adding interval: (-5, -1]
        //Snap offsets:         v         v         v          v
        //Time Line:  ...  -5 | -4 | -3 | -2 | -1 | 0  |  1 |  2
        //Current:        [^-------------------^]
        //Result:              (S---------S)
        //NOTE: contract both sides exclusively
        FixedInterval micro = new FixedInterval("micro", 6, 8);
        assertEquals(2L, micro.durationMillis);
        assertEquals(2L, micro.getIntervalMilli());

        QuantizedTimeSpan span = new QuantizedTimeSpan(micro);
        assertEquals(micro, span.getMicroInterval());
        assertEquals(0, span.getMicroIntervalCount());

        FixedInterval range = new FixedInterval("int1", -5, -1, false, true);
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
}