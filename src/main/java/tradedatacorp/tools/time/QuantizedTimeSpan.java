/**
 * @author Bruce Lamb
 * @since 20 APR 2026
 */
package tradedatacorp.tools.time;

import java.util.ArrayList;

/**
 * Accumulates time intervals and merges them into the minimal set of non-overlapping
 * {@link FixedInterval}s, where every boundary is snapped to a fixed micro-interval grid.
 *
 * <p>Each interval added via {@link #addInterval} is first quantized: its start and end are
 * shifted to the nearest grid point (either expanded outward or contracted inward, per caller
 * choice). Intervals that miss the grid entirely after contraction are silently dropped.
 *
 * <p>The grid is defined by {@code microInterval}: a {@link FixedInterval} whose duration is
 * the grid step and whose start anchors the phase via {@code START_UTC_MILLI mod duration}.
 * The micro-interval does not need to fall inside any of the added data — it acts purely as
 * a phase reference extending infinitely in both directions.
 *
 * <p>Merging is lazy: it is deferred until the first read of {@link #getIntervalSegmentCount()}
 * or {@link #getMicroIntervalCount()}, or until an explicit call to {@link #mergeTimeSpan()}.
 * After merging, {@code microCount} is kept parallel to {@code timeSpan}: index {@code i} of
 * {@code microCount} holds the number of micro-interval grid steps spanning {@code timeSpan.get(i)}.
 */
public class QuantizedTimeSpan{
    /** The unit grid interval; its duration is the snap step and its start sets the grid phase. */
    private FixedInterval microInterval;

    /** {@code floorMod(microInterval.START_UTC_MILLI, microInterval.durationMillis)} — the phase offset applied when computing snap points. */
    private long offsetMod;

    /** Accumulated (possibly overlapping, possibly unsorted) intervals after quantization. */
    private ArrayList<FixedInterval> timeSpan;

    /** Parallel to {@code timeSpan} after a merge: micro-interval grid-step count per segment. */
    private ArrayList<Long> microCount;

    /** {@code true} when {@code timeSpan} and {@code microCount} are in merged, sorted form. */
    private boolean isMerged;

    /**
     * Constructs a {@code QuantizedTimeSpan} with the given micro-interval as the snap grid.
     *
     * @param microInterval the grid unit; its {@code START_UTC_MILLI} sets the phase and its
     *                      {@code durationMillis} sets the step size. Must have duration {@code > 0}.
     * @throws IllegalArgumentException if {@code microInterval.durationMillis == 0}.
     */
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

    /** Returns the micro-interval that defines the snap grid. */
    public FixedInterval getMicroInterval(){return microInterval;}

    /**
     * Returns the number of disjoint merged segments. Triggers a merge if one is pending.
     *
     * @return count of non-overlapping intervals after merging.
     */
    public int getIntervalSegmentCount(){
        if(!isMerged) mergeTimeSpan();
        return timeSpan.size();
    }

    /** Returns {@code true} if the internal interval list is already in merged, sorted form. */
    public boolean isMerged(){return isMerged;}

    /**
     * Returns the total number of micro-interval grid steps covered across all merged segments.
     * Triggers a merge if one is pending.
     *
     * @return sum of micro-interval counts over all segments.
     */
    public long getMicroIntervalCount(){
        if(!isMerged) mergeTimeSpan();
        long count = 0;
        for(long mc : microCount) count += mc;
        return count;
    }

    /**
     * Returns a shallow copy of the internal interval list in its current state.
     * Does NOT trigger a merge; call {@link #mergeTimeSpan()} first if a merged view is needed.
     *
     * @return new {@code ArrayList} containing the same {@link FixedInterval} references.
     */
    public ArrayList<FixedInterval> getTimeSpanIntervalList(){
        ArrayList<FixedInterval> list = new ArrayList<>(timeSpan.size());
        for(FixedInterval e : timeSpan){list.add(e);}
        return list;
    }

    /**
     * Returns the interval at {@code index} without triggering a merge.
     *
     * @param index position in the internal list.
     * @return the {@link FixedInterval} at that position.
     */
    public FixedInterval getTimeSpanInterval(int index){
        return timeSpan.get(index);
    }

    /**
     * Quantizes {@code newInterval} to the micro-interval grid and appends it to the span.
     * Does not merge; sets {@link #isMerged} to {@code false} when more than one interval is present.
     *
     * <p>Snap behavior per endpoint:
     * <ul>
     *   <li>If the endpoint already falls on a grid point it is kept as-is, preserving its
     *       original inclusivity.</li>
     *   <li>If {@code expandLeft} is {@code true}, the start is snapped to the grid point
     *       immediately to the left (earlier); otherwise it is snapped right (later).</li>
     *   <li>If {@code expandRight} is {@code true}, the end is snapped to the grid point
     *       immediately to the right (later); otherwise it is snapped left (earlier).</li>
     *   <li>The inclusivity of snapped endpoints is set by {@code isLeftSnapInclusive} /
     *       {@code isRightSnapInclusive}.</li>
     * </ul>
     *
     * <p>If the resulting interval collapses to an empty set (end {@code <} start, or end
     * {@code ==} start with both endpoints exclusive), the interval is silently dropped.
     *
     * @param newInterval          the interval to quantize and add.
     * @param expandLeft           {@code true} to snap the start outward (earlier);
     *                             {@code false} to snap inward (later).
     * @param expandRight          {@code true} to snap the end outward (later);
     *                             {@code false} to snap inward (earlier).
     * @param isLeftSnapInclusive  inclusivity assigned to the start when it is snapped.
     * @param isRightSnapInclusive inclusivity assigned to the end when it is snapped.
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
     * Convenience overload of {@link #addInterval(FixedInterval, boolean, boolean, boolean, boolean)}
     * that preserves the original inclusivity of {@code newInterval} for any snapped endpoints.
     *
     * @param newInterval  the interval to quantize and add.
     * @param expandLeft   {@code true} to snap the start outward (earlier); {@code false} inward.
     * @param expandRight  {@code true} to snap the end outward (later); {@code false} inward.
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
                    ? new FixedInterval(curStart, curEnd, curLeftInc, curRightInc)
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
            ? new FixedInterval(curStart, curEnd, curLeftInc, curRightInc)
            : base);
        microCount.add((curEnd - curStart) / microDur);

        if(write < n) timeSpan.subList(write, n).clear();
        isMerged = true;
    }
}