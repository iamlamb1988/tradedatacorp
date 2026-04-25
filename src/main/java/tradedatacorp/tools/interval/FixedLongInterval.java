/**
 * @author Bruce Lamb
 * @since 25 APR 2026
 */
package tradedatacorp.tools.interval;

/**
 * Immutable integer-based mathematical interval with configurable open or closed endpoints.
 *
 * <p>If {@code startPoint > endPoint} is passed to the constructor the endpoints are silently
 * swapped so that {@code start <= end} always holds; the inclusivity flags follow their
 * respective endpoints through the swap.
 *
 * <p><b>Empty-set semantics:</b> A zero-width interval where both endpoints are equal and at
 * least one endpoint is exclusive is treated as the empty set {@code {}}:
 * <ul>
 *   <li>{@code [3,&nbsp;3)} &rarr; {@code {}}</li>
 *   <li>{@code (3,&nbsp;3]} &rarr; {@code {}}</li>
 *   <li>{@code (3,&nbsp;3)} &rarr; {@code {}}</li>
 * </ul>
 *
 * <p><b>Singleton semantics:</b> A zero-width interval where both endpoints are inclusive is
 * the singleton set {@code {P}}:
 * <ul>
 *   <li>{@code [3,&nbsp;3]} &rarr; {@code {3}}</li>
 * </ul>
 */
public class FixedLongInterval{
    /** Lower bound of this interval after endpoint normalization. */
    public final long start;

    /** Upper bound of this interval after endpoint normalization. */
    public final long end;

    /** Span of this interval: {@code end - start}. Always {@code >= 0}. */
    public final long width;

    /** {@code true} if {@link #start} is included in the interval (closed start). */
    public final boolean inclusiveStart;

    /** {@code true} if {@link #end} is included in the interval (closed end). */
    public final boolean inclusiveEnd;

    /** {@code true} if this interval represents the empty set. */
    public final boolean isEmpty;

    /**
     * Constructs a {@code FixedLongInterval} with explicit endpoint inclusivity.
     *
     * <p>If {@code startPoint > endPoint} the two values are swapped and their
     * inclusivity flags are swapped with them so the canonical form always satisfies
     * {@code start <= end}.
     *
     * @param startPoint       one endpoint; assigned to {@link #start} after normalization
     * @param endPoint         the other endpoint; assigned to {@link #end} after normalization
     * @param isInclusiveStart {@code true} to make the start endpoint closed (inclusive)
     * @param isInclusiveEnd   {@code true} to make the end endpoint closed (inclusive)
     */
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

    /**
     * Constructs the half-open interval by default {@code [startPoint,&nbsp;endPoint)}.
     *
     * <p>Equivalent to {@code FixedLongInterval(startPoint, endPoint, true, false)}.
     *
     * @param startPoint the inclusive lower bound
     * @param endPoint   the exclusive upper bound
     */
    public FixedLongInterval(
        long startPoint,
        long endPoint
    ){this(startPoint, endPoint, true, false);}

    /** Returns the lower bound of this interval. */
    public long getStart(){return start;}

    /** Returns the upper bound of this interval. */
    public long getEnd(){return end;}

    /** Returns {@code end - start}; always {@code >= 0}. */
    public long getWidth(){return width;}

    /**
     * Returns {@code true} if {@code point} lies within this interval.
     *
     * @param point the value to test
     * @return {@code true} if {@code point} is contained in this interval
     */
    public boolean contains(long point){
        return
            !isEmpty &&
            (
                point > start && point < end ||
                point == start && inclusiveStart ||
                point == end && inclusiveEnd
            );
    }

    /**
     * Returns {@code true} if this interval and {@code interval} share at least one common point.
     *
     * <p>Two empty intervals never overlap. A boundary-touch between two non-empty intervals
     * counts as an overlap only when both touching endpoints are closed (inclusive).
     *
     * @param interval the other interval to test against; must not be {@code null}
     * @return {@code true} if the intersection of the two intervals is non-empty
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

    /**
     * Returns {@code true} if {@code int1} and {@code int2} represent the same mathematical set.
     *
     * <ul>
     *   <li>Both empty &rarr; {@code true} (all empty sets are equal regardless of representation)</li>
     *   <li>One empty, one non-empty &rarr; {@code false}</li>
     *   <li>Both non-empty &rarr; {@code true} iff {@link #start}, {@link #end},
     *       {@link #inclusiveStart}, and {@link #inclusiveEnd} are all equal</li>
     * </ul>
     *
     * @param int1 the first interval; must not be {@code null}
     * @param int2 the second interval; must not be {@code null}
     * @return {@code true} if the two intervals are mathematically equivalent
     */
    public static boolean equals(FixedLongInterval int1, FixedLongInterval int2){
        return
            int1.isEmpty && int2.isEmpty ||
            int1.start == int2.start &&
            int1.end == int2.end &&
            int1.inclusiveStart == int2.inclusiveStart &&
            int1.inclusiveEnd == int2.inclusiveEnd;
    }

    /**
     * Returns {@code true} if {@code int1} and {@code int2} have identical primitive fields.
     *
     * <p>Compares {@link #start}, {@link #end}, {@link #inclusiveStart}, and {@link #inclusiveEnd}
     * directly. {@link #width} and {@link #isEmpty} are excluded because they are fully determined
     * by those four fields at construction time and will always agree when the four match.
     *
     * <p>Unlike {@link #equals}, this method does not consider mathematical equivalence — two
     * empty intervals with different representations (e.g. {@code [3,3)} and {@code (5,5)}) are
     * not structurally equal.
     *
     * @param int1 the first interval; must not be {@code null}
     * @param int2 the second interval; must not be {@code null}
     * @return {@code true} if all four primitive fields are identical
     */
    public static boolean equalsStructural(FixedLongInterval int1, FixedLongInterval int2){
        return
            int1.start == int2.start &&
            int1.end == int2.end &&
            int1.inclusiveStart == int2.inclusiveStart &&
            int1.inclusiveEnd == int2.inclusiveEnd;
    }

    /**
     * Returns a standard mathematical interval notation string.
     *
     * <ul>
     *   <li>Empty set &rarr; {@code "{}"}</li>
     *   <li>Singleton &rarr; {@code "{P}"} (e.g. {@code "{3}"})</li>
     *   <li>Normal interval &rarr; e.g. {@code "[3,7)"} or {@code "(2,5]"}</li>
     * </ul>
     *
     * @return a human-readable string representing this interval
     */
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