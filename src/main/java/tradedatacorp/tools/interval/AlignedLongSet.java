/**
 * @author Bruce Lamb
 * @since 22 APR 2026
 */
package tradedatacorp.tools.interval;

import java.util.ArrayList;

/**
 * Represents a mathematical set where each {@link FixedLongInterval} is snapped and aligned to a fixed micro-interval grid.
 * Merging is lazy. Merging can be called immediately or automatically if any information requiring a merge is required.
 *
 * <p>Each interval added via {@link #addInterval} is first quantized: its start and end are
 * shifted to the nearest grid point (either expanded outward or contracted inward, per caller
 * choice). Intervals that miss the grid entirely after contraction are silently dropped.
 *
 * <p>The grid is defined by {@code microInterval}: a {@link FixedLongInterval} whose width is
 * the grid step and whose start anchors the phase via {@code start mod width}.
 * The micro-interval does not need to fall inside any of the added data — it acts purely as
 * a phase reference extending infinitely in both directions.
 *
 * <p>Merging is lazy: it is deferred until the first read of {@link #getIntervalSegmentCount()}
 * or {@link #getMicroIntervalCount()}, or until an explicit call to {@link #merge()}.
 * After merging, {@code microCount} is kept parallel to {@code mergeList}: index {@code i} of
 * {@code microCount} holds the number of micro-interval grid steps spanning {@code mergeList.get(i)}.
 */
public class AlignedLongSet{
    /** The unit grid interval; its width is the snap step and its start sets the grid phase. */
    private FixedLongInterval microInterval;

    /** {@code floorMod(microInterval.start, microInterval.width)} — the phase offset applied when computing snap points. */
    private long offsetMod;

    /** Accumulated (possibly overlapping, possibly unsorted) intervals after quantization. */
    private ArrayList<FixedLongInterval> mergeList;

    /** Parallel to {@code mergeList} after a merge: micro-interval grid-step count per segment. */
    private ArrayList<Long> microCount;

    /** {@code true} when {@code mergeList} and {@code microCount} are in merged, sorted form. */
    private boolean isMerged;

    /**
     * Constructs a {@code AlignedLongSet} with the given micro-interval as the snap grid.
     *
     * @param microInterval the grid unit; its {@code start} sets the phase and its
     *                      {@code width} sets the step size. Must have width {@code > 0}.
     * @throws IllegalArgumentException if {@code microInterval.width == 0}.
     */
    public AlignedLongSet(FixedLongInterval microInterval){
        if(microInterval.width == 0)
            throw new IllegalArgumentException("AlignedLongSet requires a micro interval with a width > 0.");

        this.microInterval = microInterval;

        offsetMod = Math.floorMod(
            microInterval.start,
            microInterval.width
        );

        mergeList = new ArrayList<>();
        microCount = new ArrayList<>();
        isMerged = true;
    }

    /** Returns the micro-interval that defines the snap grid. */
    public FixedLongInterval getMicroInterval(){return microInterval;}

    /**
     * Returns the number of disjoint merged segments. Triggers a merge if one is pending.
     *
     * @return count of non-overlapping intervals after merging.
     */
    public int getIntervalSegmentCount(){
        if(!isMerged) merge();
        return mergeList.size();
    }

    /**
     * Returns {@code true} if the internal interval list is already in merged, sorted form.
     * 
     * @return if the internal interval list is already in merged, sorted form, otherwise false */
    public boolean isMerged(){return isMerged;}

    /**
     * Returns the total number of micro-interval grid steps covered across all merged segments.
     * Triggers a merge if one is pending.
     * A return value that is greater than 1 indicates a gap. For every N intervals, there are N-1 gaps except for when N is 0.
     *
     * @return sum of micro-interval counts over all segments.
     */
    public long getMicroIntervalCount(){
        if(!isMerged) merge();
        long count = 0;
        for(long mc : microCount) count += mc;
        return count;
    }

    /**
     * Returns a shallow copy of the internal interval list in its current state.
     * Does NOT trigger a merge; call {@link #merge()} first if a merged view is needed.
     *
     * @return new {@code ArrayList} containing the same {@link FixedLongInterval} references.
     */
    public ArrayList<FixedLongInterval> getUnmergedIntervals(){
        ArrayList<FixedLongInterval> list = new ArrayList<>(mergeList.size());
        for(FixedLongInterval e : mergeList){list.add(e);}
        return list;
    }

    /**
     * Returns a shallow copy of the internal interval list in its current state.
     * Does NOT trigger a merge; call {@link #merge()} first if a merged view is needed.
     *
     * @return new {@code ArrayList} containing the same {@link FixedLongInterval} references.
     */
    public ArrayList<FixedLongInterval> getIntervals(){
        merge();
        return getUnmergedIntervals();
    }

    /**
     * Returns the interval at {@code index} without triggering a merge.
     *
     * @param index position in the internal list.
     * @return the {@link FixedLongInterval} at that position.
     */
    public FixedLongInterval getInterval(int index){
        return mergeList.get(index);
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
     *       immediately to the left (lesser); otherwise it is snapped right (greater).</li>
     *   <li>If {@code expandRight} is {@code true}, the end is snapped to the grid point
     *       immediately to the right (greater); otherwise it is snapped left (lesser).</li>
     *   <li>The inclusivity of snapped endpoints is set by {@code isLeftSnapInclusive} /
     *       {@code isRightSnapInclusive}.</li>
     * </ul>
     *
     * <p>If the resulting interval collapses to an empty set (end {@code <} start, or end
     * {@code ==} start with both endpoints exclusive), the interval is silently dropped.
     *
     * @param newInterval          the interval to quantize and add.
     * @param expandLeft           {@code true} to snap the start outward (lesser);
     *                             {@code false} to snap inward (greater).
     * @param expandRight          {@code true} to snap the end outward (greater);
     *                             {@code false} to snap inward (lesser).
     * @param isLeftSnapInclusive  inclusivity assigned to the start when it is snapped.
     * @param isRightSnapInclusive inclusivity assigned to the end when it is snapped.
     */
    public void addInterval(
        FixedLongInterval newInterval,
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

        dist = Math.floorMod(newInterval.start - offsetMod, microInterval.width);
        if(dist != 0){
            if(expandLeft){ //Expand Left on the number line (subtract)
                newStart = newInterval.start - dist;
                newLeftInclusive = isLeftSnapInclusive;
            }else{ //Contract right on the number line (add)
                newStart = newInterval.start + (microInterval.width - dist);
                newLeftInclusive = isLeftSnapInclusive;
            }
        } else{
            newStart = newInterval.start;
            newLeftInclusive = newInterval.inclusiveStart; //unchanged inclusion
        }

        dist = Math.floorMod(newInterval.end - offsetMod, microInterval.width);
        if(dist != 0){
            if(expandRight){ //Expand Right on the number line (add)
                newEnd = newInterval.end + (microInterval.width - dist);
                newRightInclusive = isRightSnapInclusive;
            }else{ //Contract left on the number line (subtract)
                newEnd = newInterval.end - dist;
                newRightInclusive = isRightSnapInclusive;
            }
        } else{
            newEnd = newInterval.end;
            newRightInclusive = newInterval.inclusiveEnd; //unchanged inclusion
        }

        if(newEnd < newStart || (newEnd == newStart && !newLeftInclusive && !newRightInclusive)) return;

        FixedLongInterval newQuantizedInterval = new FixedLongInterval(
            newStart,
            newEnd,
            newLeftInclusive,
            newRightInclusive
        );

        mergeList.add(newQuantizedInterval);

        //post tasks
        if(mergeList.isEmpty()){
            microCount.clear();
            isMerged = true;
            return;
        }

        if(mergeList.size() == 1){
            microCount.add(Long.valueOf(
                (newQuantizedInterval.end - newQuantizedInterval.start)/microInterval.width)
            );
            isMerged = true;
            return;
        }

        isMerged = false;
    }

    //TODO
    public void subtractInterval(
        FixedLongInterval newInterval,
        boolean expandLeft,
        boolean expandRight,
        boolean isLeftSnapInclusive,
        boolean isRightSnapInclusive
    ){
        //TODO: implement
    }

    public void subtractInterval(long point){
        subtractInterval(
            new FixedLongInterval(point, point, true, true),
            true, true, true, true
        );
    }

    /**
     * Convenience overload of {@link #addInterval(FixedLongInterval, boolean, boolean, boolean, boolean)}
     * that preserves the original inclusivity of {@code newInterval} for any snapped endpoints.
     *
     * @param newInterval  the interval to quantize and add.
     * @param expandLeft   {@code true} to snap the start outward (lesser); {@code false} inward.
     * @param expandRight  {@code true} to snap the end outward (greater); {@code false} inward.
     */
    public void addInterval(FixedLongInterval newInterval, boolean expandLeft, boolean expandRight){
        addInterval(
            newInterval,
            expandLeft,
            expandRight,
            newInterval.inclusiveStart,
            newInterval.inclusiveEnd
        );
    }

    /**
     * Consolidates {@code mergeList} into a minimal, ordered list of non-overlapping {@link FixedLongInterval}s,
     * merging any intervals that overlap or are adjacent on the number line.
     * <p>
     * After this call, {@code isMerged()} returns {@code true} and {@code microCount} is kept
     * parallel to {@code mergeList}: {@code microCount.get(i)} holds the number of micro-interval
     * chunks contained in {@code mergeList.get(i)}.
     * <p>
     * Algorithm: sort by start ascending (O(n log n), TimSort; fast on partially-sorted input),
     * then a single linear sweep merges touching runs in place. A new {@link FixedLongInterval} is
     * allocated only when a run actually merged; runs with no merges reuse the original reference.
     * <p>
     * When two intervals are merged, the resulting interval spans from the lesser start to the
     * greater end. If both share the same boundary point, the merged boundary is inclusive if
     * either original boundary was inclusive.
     */
    public void merge(){
        final int n = mergeList.size();
        microCount.clear();
        final long microDur = microInterval.width;

        if(n == 0){ isMerged = true; return; }
        if(n == 1){
            microCount.add(mergeList.get(0).width / microDur);
            isMerged = true;
            return;
        }

        mergeList.sort((x, y) -> Long.compare(x.start, y.start));

        FixedLongInterval base = mergeList.get(0);
        long curStart = base.start;
        long curEnd = base.end;
        boolean curLeftInc = base.inclusiveStart;
        boolean curRightInc = base.inclusiveEnd;
        //zero-length interval: either inclusive brace means the point exists, so treat both as inclusive
        if(curStart == curEnd && (curLeftInc || curRightInc)){ curLeftInc = true; curRightInc = true; }
        boolean dirty = false;
        int write = 0;

        for(int read = 1; read < n; read++){
            FixedLongInterval curr = mergeList.get(read);
            boolean cLeftInc = curr.inclusiveStart;
            boolean cRightInc = curr.inclusiveEnd;
            //zero-length interval: either inclusive brace means the point exists, so treat both as inclusive
            if(curr.start == curr.end && (cLeftInc || cRightInc)){ cLeftInc = true; cRightInc = true; }

            if( //if touches an endpoint
                curr.start < curEnd ||
                (curr.start == curEnd && (cLeftInc || curRightInc))
            ){
                if(curr.start == curStart && cLeftInc && !curLeftInc){
                    curLeftInc = true;
                    dirty = true;
                }
                if(curr.end > curEnd){
                    curEnd = curr.end;
                    curRightInc = cRightInc;
                    dirty = true;
                }else if(curr.end == curEnd && cRightInc && !curRightInc){
                    curRightInc = true;
                    dirty = true;
                }
            }else{
                mergeList.set(write++, dirty
                    ? new FixedLongInterval(curStart, curEnd, curLeftInc, curRightInc)
                    : base);
                microCount.add((curEnd - curStart) / microDur);

                base = curr;
                curStart = curr.start;
                curEnd = curr.end;
                curLeftInc = cLeftInc;
                curRightInc = cRightInc;
                dirty = false;
            }
        }

        mergeList.set(write++, dirty
            ? new FixedLongInterval(curStart, curEnd, curLeftInc, curRightInc)
            : base);
        microCount.add((curEnd - curStart) / microDur);

        if(write < n) mergeList.subList(write, n).clear();
        isMerged = true;
    }
}