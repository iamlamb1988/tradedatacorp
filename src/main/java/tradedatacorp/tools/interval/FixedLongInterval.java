/**
 * @author Bruce Lamb
 * @since 23 APR 2026
 */
package tradedatacorp.tools.interval;

/**
 * Represents a integer based mathematical set interval with open or closed endpoints.
 *
 * Note: Empty sets
 * 0 width 1 point cases where both ends are not inclusive will be considered {}
 * Examples: [3, 3), (3, 3], and (3, 3) result to {}
 * 
 * Point cases:
 * 0 width cases where both points are inclusive will represented as {P}, not [P, P]
 * Example: [3, 3] result to {3}
 */
public class FixedLongInterval{
    public final long start;
    public final long end;
    public final long width;
    public final boolean inclusiveStart; //default inclusion
    public final boolean inclusiveEnd;   //default inclusion
    public final boolean isEmpty;

    public FixedLongInterval(
        long startPoint,
        long endPoint,
        boolean isInclusiveStart,
        boolean isInclusiveEnd
    ){
        if(startPoint == endPoint){
            start = end = endPoint;
            inclusiveStart = isInclusiveStart;
            inclusiveEnd = isInclusiveEnd;
            isEmpty = !inclusiveStart || !inclusiveEnd;
        }else if(startPoint < endPoint){
            start = startPoint;
            end = endPoint;
            inclusiveStart = isInclusiveStart;
            inclusiveEnd = isInclusiveEnd;
            isEmpty = false;
        }else{ //invert
            end = startPoint;
            start = endPoint;
            inclusiveStart = isInclusiveEnd;
            inclusiveEnd = isInclusiveStart;
            isEmpty = false;
        }

        width = end - start;
    }

    public FixedLongInterval(
        long startPoint,
        long endPoint
    ){this(startPoint, endPoint, true, false);}

    public long getStart(){return start;}

    public long getEnd(){return end;}

    public long getWidth(){return width;}

    public boolean contains(long point, boolean isInclusiveStart, boolean isInclusiveEnd){
        return 
            point > start && point < end ||
            point == start && inclusiveStart ||
            point == end && inclusiveEnd;
    }

    public boolean contains(long point){return contains(point, inclusiveStart, inclusiveEnd);}

    /**
     * Checks for any overlap between this and interval
     */
    public boolean overlaps(FixedLongInterval interval){
        if(isEmpty || interval.isEmpty) return false;

        if(start > interval.start){
            if(start < interval.end) return true;
            else if(start > interval.end) return false;
            return start == interval.end && inclusiveStart && interval.inclusiveEnd;

        }

        if(end < interval.end){
            if(end > interval.start) return true;
            else if(end < interval.start) return false; //this is less than interval
            return end == interval.start && inclusiveEnd && interval.inclusiveStart;
        }

        return true;
    }

    // return
    //                 end > interval.start ||
    //                 end == interval.start && inclusiveEnd && interval.inclusiveStart;

    public static boolean equals(FixedLongInterval int1, FixedLongInterval int2){
        return
            int1.start == int2.start &&
            int1.end == int2.end &&
            int1.inclusiveStart == int2.inclusiveStart &&
            int1.inclusiveEnd == int2.inclusiveEnd;
    }

    @Override
    public String toString(){
        if(isEmpty) return "{}";
        if(width == 0) return "{"+start+"}";
        return 
            (inclusiveStart ? "[" : "(")
            +start + "," +
            end + (inclusiveEnd ? "]" : ")");
    }
}