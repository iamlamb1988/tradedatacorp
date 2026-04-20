/**
 * @author Bruce Lamb
 * @since 18 APR 2026
 */
package tradedatacorp.tools.time;



import java.util.ArrayList;

/**
 * A class that tracks time buckets of micro interval time lengths.
 * Each timeframe within this class is a mutiple of the microInterval and accounts for the time offset as well.
 * The microInterval does not have to exist within the actual data.
 */
public class QuantizedTimeSpan{
    private static final String microRef = "UNIT";
    private static final String quantizedName = "QUANTIZED";

    private FixedInterval microInterval;
    private long offsetMod; //the floor modulus to handle the offset of times
    private ArrayList<FixedInterval> timeSpan;
    private ArrayList<Long> microCount;
    private boolean isMerged;

    public QuantizedTimeSpan(FixedInterval microInterval){
        if(microInterval.durationMillis == 0)
            throw new IllegalArgumentException("QuantizedTimeSpan requires a micro interval with a duration > 0.");

        this.microInterval = microInterval;

        offsetMod = Math.floorMod(
            microInterval.START_UTC_MILLI,
            microInterval.durationMillis
        );

        timeSpan = new ArrayList<>();
        microCount = new ArrayList<>();
        isMerged = true;
    }

    public FixedInterval getMicroInterval(){return microInterval;}

    public int getIntervalSegmentCount(){
        if(!isMerged) mergeTimeSpan();
        return timeSpan.size();
    }

    public boolean isMerged(){return isMerged;}

    public long getMicroIntervalCount(){
        if(!isMerged) mergeTimeSpan();
        long count = 0;
        for(long mc : microCount) count += mc;  // Use cached values
        return count;
    }

    public ArrayList<FixedInterval> getTimeSpanIntervalList(){
        ArrayList<FixedInterval> list = new ArrayList<>(timeSpan.size());
        for(FixedInterval e : timeSpan){list.add(e);}
        return list;
    }

    public FixedInterval getTimeSpanInterval(int index){
        return timeSpan.get(index);
    }

    /**
     * Adds to the timespan. This does NOT merge the span
     * If newInterval is does not synchronize with the microInterval, it will be expanded or cut to synchronize with microInterval chunks
     */
    public void addInterval(
        FixedInterval newInterval,
        boolean expandLeft,
        boolean expandRight,
        boolean isLeftSnapInclusive,
        boolean isRightSnapInclusive
    ){
        long dist; //distance from begin or endpoint to next snap
        long newStart;
        long newEnd;
        boolean newLeftInclusive;
        boolean newRightInclusive;

        dist = Math.floorMod(newInterval.START_UTC_MILLI - offsetMod, microInterval.durationMillis);
        if(dist != 0){
            if(expandLeft){ //Expand Left on the number line (subtract)
                newStart = newInterval.START_UTC_MILLI - dist;
                newLeftInclusive = isLeftSnapInclusive;
            }else{ //Contract right on the number line (add)
                newStart = newInterval.START_UTC_MILLI + (microInterval.durationMillis - dist);
                newLeftInclusive = isLeftSnapInclusive;
            }
        } else{
            newStart = newInterval.START_UTC_MILLI;
            newLeftInclusive = newInterval.inclusiveStart; //unchanged inclusion
        }

        dist = Math.floorMod(newInterval.END_UTC_MILLI - offsetMod, microInterval.durationMillis);
        if(dist != 0){
            if(expandRight){ //Expand Right on the number line (add)
                newEnd = newInterval.END_UTC_MILLI + (microInterval.durationMillis - dist);
                newRightInclusive = isRightSnapInclusive;
            }else{ //Contract left on the number line (subtract)
                newEnd = newInterval.END_UTC_MILLI - dist;
                newRightInclusive = isRightSnapInclusive;
            }
        } else{
            newEnd = newInterval.END_UTC_MILLI;
            newRightInclusive = newInterval.inclusiveEnd; //unchanged inclusion
        }

        if(newEnd < newStart || (newEnd == newStart && !newLeftInclusive && !newRightInclusive)) return;

        FixedInterval newQuantizedInterval = new FixedInterval(
            newInterval.name,
            newStart,
            newEnd,
            newLeftInclusive,
            newRightInclusive
        );

        timeSpan.add(newQuantizedInterval);

        //post tasks
        if(timeSpan.isEmpty()){
            microCount.clear();
            isMerged = true;
            return;
        }

        if(timeSpan.size() == 1){
            microCount.add(Long.valueOf(
                (newQuantizedInterval.END_UTC_MILLI - newQuantizedInterval.START_UTC_MILLI)/microInterval.durationMillis)
            );
            isMerged = true;
            return;
        }

        isMerged = false;
    }

    /**
     * Adds to the timespan. This does NOT merge the span
     * If newInterval is does not synchronize with the microInterval, it will be expanded or cut to synchronize with microInterval chunks
     */
    public void addInterval(FixedInterval newInterval, boolean expandLeft, boolean expandRight){
        addInterval(
            newInterval,
            expandLeft,
            expandRight,
            newInterval.inclusiveStart,
            newInterval.inclusiveEnd
        );
    }

    /**
     * Consolidates {@code timeSpan} into a minimal, ordered list of non-overlapping {@link FixedInterval}s,
     * merging any intervals that overlap or are adjacent on the number line.
     * <p>
     * After this call, {@code isMerged()} returns {@code true} and {@code microCount} is kept
     * parallel to {@code timeSpan}: {@code microCount.get(i)} holds the number of micro-interval
     * chunks contained in {@code timeSpan.get(i)}.
     * <p>
     * Algorithm: sort by start ascending (O(n log n), TimSort; fast on partially-sorted input),
     * then a single linear sweep merges touching runs in place. A new {@link FixedInterval} is
     * allocated only when a run actually merged; runs with no merges reuse the original reference.
     * <p>
     * When two intervals are merged, the resulting interval spans from the lesser start to the
     * greater end. If both share the same boundary point, the merged boundary is inclusive if
     * either original boundary was inclusive.
     */
    public void mergeTimeSpan(){
        final int n = timeSpan.size();
        microCount.clear();
        final long microDur = microInterval.durationMillis;

        if(n == 0){ isMerged = true; return; }
        if(n == 1){
            microCount.add(timeSpan.get(0).durationMillis / microDur);
            isMerged = true;
            return;
        }

        timeSpan.sort((x, y) -> Long.compare(x.START_UTC_MILLI, y.START_UTC_MILLI));

        FixedInterval base = timeSpan.get(0);
        long curStart = base.START_UTC_MILLI;
        long curEnd = base.END_UTC_MILLI;
        boolean curLeftInc = base.inclusiveStart;
        boolean curRightInc = base.inclusiveEnd;
        //zero-length interval: either inclusive brace means the point exists, so treat both as inclusive
        if(curStart == curEnd && (curLeftInc || curRightInc)){ curLeftInc = true; curRightInc = true; }
        boolean dirty = false;
        int write = 0;

        for(int read = 1; read < n; read++){
            FixedInterval curr = timeSpan.get(read);
            long cs = curr.START_UTC_MILLI;
            long ce = curr.END_UTC_MILLI;
            boolean cLeftInc = curr.inclusiveStart;
            boolean cRightInc = curr.inclusiveEnd;
            //zero-length interval: either inclusive brace means the point exists, so treat both as inclusive
            if(cs == ce && (cLeftInc || cRightInc)){ cLeftInc = true; cRightInc = true; }

            boolean touches =
                cs < curEnd ||
                (cs == curEnd && (cLeftInc || curRightInc));

            if(touches){
                if(cs == curStart && cLeftInc && !curLeftInc){
                    curLeftInc = true;
                    dirty = true;
                }
                if(ce > curEnd){
                    curEnd = ce;
                    curRightInc = cRightInc;
                    dirty = true;
                }else if(ce == curEnd && cRightInc && !curRightInc){
                    curRightInc = true;
                    dirty = true;
                }
            }else{
                timeSpan.set(write++, dirty
                    ? new FixedInterval(base.name, curStart, curEnd, curLeftInc, curRightInc)
                    : base);
                microCount.add((curEnd - curStart) / microDur);

                base = curr;
                curStart = cs;
                curEnd = ce;
                curLeftInc = cLeftInc;
                curRightInc = cRightInc;
                dirty = false;
            }
        }

        timeSpan.set(write++, dirty
            ? new FixedInterval(base.name, curStart, curEnd, curLeftInc, curRightInc)
            : base);
        microCount.add((curEnd - curStart) / microDur);

        if(write < n) timeSpan.subList(write, n).clear();
        isMerged = true;
    }
}