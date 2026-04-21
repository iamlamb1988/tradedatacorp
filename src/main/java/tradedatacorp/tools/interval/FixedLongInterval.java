/**
 * @author Bruce Lamb
 * @since 21 APR 2026
 */
package tradedatacorp.tools.interval;

/**
 * Represents a integer based mathematical set interval with open or closed endpoints
 */
public class FixedLongInterval{
    public final long start;
    public final long end;
    public final long width;
    public final boolean inclusiveStart; //default inclusion
    public final boolean inclusiveEnd;   //default inclusion

    public FixedLongInterval(
        long startPoint,
        long endPoint,
        boolean isInclusiveStart,
        boolean isInclusiveEnd
    ){
        if(startPoint <= endPoint){
            start = startPoint;
            end = endPoint;
            inclusiveStart = isInclusiveStart;
            inclusiveEnd = isInclusiveEnd;
        }else{ //invert
            end = startPoint;
            start = endPoint;
            inclusiveStart = isInclusiveEnd;
            inclusiveEnd = isInclusiveStart;
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

    public static boolean equals(FixedLongInterval int1, FixedLongInterval int2){
        return
            int1.start == int2.start &&
            int1.end == int2.end &&
            int1.inclusiveStart == int2.inclusiveStart &&
            int1.inclusiveEnd == int2.inclusiveEnd;
    }
}