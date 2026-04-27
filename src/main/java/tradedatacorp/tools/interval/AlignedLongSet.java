/**
 * @author Bruce Lamb
 * @since 27 APR 2026
 */
package tradedatacorp.tools.interval;

import java.util.ArrayList;

/**
 * A mathematical set of grid-aligned {@link FixedLongInterval}s backed by a lazy merge list.
 *
 * <p>Each interval added via {@link #addInterval} is first quantized to the micro-interval grid:
 * its start and end are shifted to the nearest grid point (expanded outward or contracted inward,
 * per caller choice). Any interval that quantizes to an empty set is silently dropped.
 *
 * <p>The grid is defined by {@code microInterval}: a {@link FixedLongInterval} whose width is
 * the grid step and whose start anchors the phase via {@code start mod width}.
 * The micro-interval does not need to fall inside any of the added data — it acts purely as
 * a phase reference extending infinitely in both directions.
 *
 * <p>Merging is lazy: it is deferred until the first read of {@link #getIntervalSegmentCount()},
 * {@link #getMicroIntervalCount()}, {@link #getIntervals()}, {@link #getInterval(int)},
 * {@link #contains(long)}, {@link #overlaps(FixedLongInterval)}, or {@link #overlaps(AlignedLongSet)},
 * or until an explicit call to {@link #merge()}.
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
     * Copy constructor. Produces a shallow copy of the merged interval list, sharing the same
     * (immutable) {@link FixedLongInterval} objects.
     *
     * <p>Forces a merge on {@code set} if one is pending so the copy starts in merged, sorted form.
     *
     * @param set the source set to copy; its micro-interval, segments, and micro-counts are copied.
     */
    public AlignedLongSet(AlignedLongSet set){
        microInterval = set.getMicroInterval();
        offsetMod = Math.floorMod(
            microInterval.start,
            microInterval.width
        );
        int segmentCount = set.getIntervalSegmentCount();
        mergeList = new ArrayList<>(segmentCount);
        microCount = new ArrayList<>(segmentCount);

        for(int i=0; i<segmentCount; ++i){
            FixedLongInterval interval = set.getInterval(i);
            mergeList.add(interval);
            microCount.add(Long.valueOf(mergeList.get(i).width / microInterval.width));
        }
        isMerged = true;
    }

    /**
     * Constructs an empty {@code AlignedLongSet} with the given micro-interval as the snap grid.
     *
     * @param microInterval the grid unit; its {@code start} sets the phase and its
     *                      {@code width} sets the step size. Must have positive {@code width}.
     * @throws IllegalArgumentException if {@code microInterval.width == 0}.
     */
    public AlignedLongSet(FixedLongInterval microInterval){this(microInterval, 16);}

    private AlignedLongSet(FixedLongInterval microInterval, int defaultArraySize){
        if(microInterval.width == 0)
            throw new IllegalArgumentException("AlignedLongSet requires a micro interval with a width > 0.");

        this.microInterval = microInterval;

        offsetMod = Math.floorMod(
            microInterval.start,
            microInterval.width
        );

        mergeList = new ArrayList<>(defaultArraySize);
        microCount = new ArrayList<>(defaultArraySize);
        isMerged = true;
    }

    /**
     * Constructs an {@code AlignedLongSet} from a varargs list of pre-built intervals.
     *
     * <p>Intervals are added directly to the internal list without quantization or snapping;
     * callers are responsible for ensuring they are already grid-aligned if that matters.
     * Any interval with {@link FixedLongInterval#isEmpty} {@code == true} is silently dropped
     * by the immediate {@link #merge()} call. Overlapping and out-of-order intervals are
     * resolved by that merge.
     *
     * @param microInterval the grid unit defining the snap phase and step size.
     * @param intervalList  zero or more intervals to seed the set.
     * @throws IllegalArgumentException if {@code microInterval.width == 0}.
     */
    public AlignedLongSet(FixedLongInterval microInterval, FixedLongInterval... intervalList){
        this(microInterval, intervalList.length);
        for(FixedLongInterval i : intervalList){mergeList.add(i);}
        this.merge();
    }

    /**
     * Constructs an {@code AlignedLongSet} by re-merging the segments of an existing set
     * under a (possibly different) micro-interval.
     *
     * <p>The merged segments of {@code set} are copied as-is without re-snapping to
     * {@code microInterval}. The new micro-interval only affects how
     * {@link #getMicroIntervalCount()} counts grid steps, not the interval boundaries themselves.
     *
     * @param microInterval the grid unit for the new set's snap phase and step size.
     * @param set           the source set whose merged segments seed the new set.
     * @throws IllegalArgumentException if {@code microInterval.width == 0}.
     */
    public AlignedLongSet(FixedLongInterval microInterval, AlignedLongSet set){
        this(microInterval, set.getIntervalSegmentCount());
        ArrayList<FixedLongInterval> list = set.getIntervals();
        for(FixedLongInterval i : list){mergeList.add(i);}
        this.merge();
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
     * @return {@code true} if in merged, sorted form; {@code false} if a merge is pending.
     */
    public boolean isMerged(){return isMerged;}

    /**
     * Returns the total number of micro-interval grid steps covered across all merged segments.
     * Triggers a merge if one is pending.
     *
     * <p>This is the sum of each segment's width divided by the micro-interval width. A singleton
     * point {@code {x}} contributes 0. The presence of gaps between segments is indicated by
     * {@link #getIntervalSegmentCount()} {@code > 1}, not by this value.
     *
     * @return sum of micro-interval step counts over all segments; {@code 0} if the set is empty
     *         or contains only zero-width singleton points.
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
     * Returns a shallow copy of the internal interval list in merged, sorted form.
     * Triggers a merge if one is pending.
     *
     * @return new {@code ArrayList} containing the same {@link FixedLongInterval} references,
     *         in ascending order with no overlaps.
     */
    public ArrayList<FixedLongInterval> getIntervals(){
        if(!isMerged) merge();
        return getUnmergedIntervals();
    }

    /**
     * Returns the merged segment at {@code index}. Triggers a merge if one is pending.
     *
     * @param index position in the merged interval list; must be in
     *              {@code [0, getIntervalSegmentCount())}.
     * @return the {@link FixedLongInterval} at that position.
     * @throws IndexOutOfBoundsException if {@code index} is out of range.
     */
    public FixedLongInterval getInterval(int index){
        if(!isMerged) merge();
        return mergeList.get(index);
    }

    /**
     * Returns {@code true} if {@code point} is contained in any merged segment.
     * Triggers a merge if one is pending.
     *
     * @param point the value to test.
     * @return {@code true} if this set contains {@code point}; {@code false} otherwise.
     */
    public boolean contains(long point){
        if(!isMerged) merge();
        for(FixedLongInterval i : mergeList){
            if(i.contains(point)) return true;
        }
        return false;
    }

    /**
     * Returns {@code true} if {@code interval} overlaps any merged segment in this set.
     * Triggers a merge if one is pending.
     *
     * <p>Boundary-touch semantics are delegated to {@link FixedLongInterval#overlaps}.
     *
     * @param interval the interval to test; must not be {@code null}.
     * @return {@code true} if at least one segment in this set shares a point with
     *         {@code interval}; {@code false} otherwise.
     */
    public boolean overlaps(FixedLongInterval interval){
        if(!isMerged) merge();
        for(FixedLongInterval i : mergeList){
            if(i.overlaps(interval)) return true;
        }
        return false;
    }

    /**
     * Returns {@code true} if any segment in this set overlaps any segment in {@code set}.
     * Triggers a merge on either side if pending, so the scan runs over sorted, disjoint segments.
     *
     * <p>Complexity: {@code O(m + n)} two-pointer sweep with an {@code O(1)} bounding-box reject
     * in both directions. Each step advances the pointer whose segment ends first — that segment
     * cannot touch anything further along in the other list, since both lists are sorted and disjoint.
     *
     * <p>Inclusivity at touching boundaries is delegated to {@link FixedLongInterval#overlaps},
     * which is the authority on whether {@code [a, b)} and {@code (b, c]} count as overlapping.
     *
     * @param set the other set to test against; must not be {@code null}.
     * @return {@code true} if the two sets share at least one common point; {@code false} otherwise.
     */
    public boolean overlaps(AlignedLongSet set){
        if(!isMerged) merge();
        if(!set.isMerged) set.merge();

        final int m = mergeList.size();
        final int n = set.mergeList.size();
        if(m == 0 || n == 0) return false;

        //O(1) bounding-box reject: if one set ends strictly before the other begins, no overlap possible.
        FixedLongInterval thisLast = mergeList.get(m - 1);
        FixedLongInterval thatFirst = set.mergeList.get(0);
        if(thisLast.end < thatFirst.start) return false;
        FixedLongInterval thisFirst = mergeList.get(0);
        FixedLongInterval thatLast = set.mergeList.get(n - 1);
        if(thatLast.end < thisFirst.start) return false;

        int i = 0;
        int j = 0;
        while(i < m && j < n){
            FixedLongInterval a = mergeList.get(i);
            FixedLongInterval b = set.mergeList.get(j);

            //Strict disjoint cases: skip without calling overlaps().
            if(a.end < b.start){ ++i; continue; }
            if(b.end < a.start){ ++j; continue; }

            if(a.overlaps(b)) return true;

            //Touching-but-not-overlapping (exclusive boundaries); advance the one that ends first.
            if(a.end <= b.end) ++i;
            else ++j;
        }
        return false;
    }

    /**
     * Returns a snapped interval IAW with this state instance.
     * If the interval is already snapped, will return the same instance
     * //TODO: more javadoc elaboration
     */
    public FixedLongInterval getSnappedInterval(
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

        if(newEnd < newStart || (newEnd == newStart && !(newLeftInclusive && newRightInclusive))) return null;

        if(newStart == newInterval.start &&
           newEnd == newInterval.end &&
           newLeftInclusive == newInterval.inclusiveStart &&
           newRightInclusive == newInterval.inclusiveEnd){
           return newInterval;
        }

        return new FixedLongInterval(
            newStart,
            newEnd,
            newLeftInclusive,
            newRightInclusive
        );
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
     *   <li>The inclusivity of off-grid snapped endpoints is set by {@code isLeftSnapInclusive} /
     *       {@code isRightSnapInclusive}; for on-grid endpoints these parameters are ignored.</li>
     * </ul>
     *
     * <p>If the resulting quantized interval is empty — its end is less than its start, or its end
     * equals its start and the two endpoints are not both inclusive — it is silently dropped.
     *
     * @param newInterval          the interval to quantize and add.
     * @param expandLeft           {@code true} to snap the start outward (lesser);
     *                             {@code false} to snap inward (greater).
     * @param expandRight          {@code true} to snap the end outward (greater);
     *                             {@code false} to snap inward (lesser).
     * @param isLeftSnapInclusive  inclusivity assigned to the start when it is off-grid and snapped.
     * @param isRightSnapInclusive inclusivity assigned to the end when it is off-grid and snapped.
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

        if(newEnd < newStart || (newEnd == newStart && !(newLeftInclusive && newRightInclusive))) return;

        FixedLongInterval newQuantizedInterval = new FixedLongInterval(
            newStart,
            newEnd,
            newLeftInclusive,
            newRightInclusive
        );

        mergeList.add(newQuantizedInterval);

        if(mergeList.size() == 1){
            microCount.add(Long.valueOf(
                (newQuantizedInterval.end - newQuantizedInterval.start)/microInterval.width)
            );
            isMerged = true;
            return;
        }

        isMerged = false;
    }

    /**
     * Convenience overload of {@link #addInterval(FixedLongInterval, boolean, boolean, boolean, boolean)}
     * that preserves the original inclusivity of {@code newInterval} for any off-grid snapped endpoints.
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
     * Adds {@code newInterval} with both endpoints snapped outward, preserving the original
     * inclusivity for any off-grid snapped endpoints.
     *
     * <p>Equivalent to {@link #addInterval(FixedLongInterval, boolean, boolean) addInterval(newInterval, true, true)}.
     *
     * @param newInterval the interval to quantize and add.
     */
    public void addInterval(FixedLongInterval newInterval){
        addInterval(
            newInterval,
            true,
            true,
            newInterval.inclusiveStart,
            newInterval.inclusiveEnd
        );
    }

    /**
     * Adds the micro-interval cell that contains {@code point}.
     *
     * <p>If {@code point} is off-grid, both endpoints snap outward to the surrounding grid
     * boundaries (inclusive on both sides), adding one full micro-interval cell.
     * If {@code point} falls exactly on a grid boundary it is the edge between two cells and
     * nothing is added (the half-open default form {@code [point, point)} collapses to an empty
     * set after snapping). To add an on-grid singleton point explicitly, use
     * {@link #addInterval(FixedLongInterval)} with a both-inclusive zero-width interval.
     *
     * @param point the value whose containing micro-interval cell is added.
     */
    public void addInterval(long point){
        addInterval(
            new FixedLongInterval(point, point),
            true,
            true,
            true,
            true
        );
    }

    /**
     * Quantizes {@code newInterval} to the micro-interval grid and removes its coverage from this set.
     * Triggers a merge first if one is pending, so subtraction operates on a canonical disjoint form.
     *
     * <p>Snap behavior per endpoint mirrors {@link #addInterval}:
     * <ul>
     *   <li>Grid-aligned endpoints keep their original inclusivity; snap-inclusivity flags are ignored.</li>
     *   <li>Off-grid endpoints snap outward when {@code expand*=true} or inward when {@code expand*=false},
     *       with inclusivity set by {@code isLeftSnapInclusive} / {@code isRightSnapInclusive}.</li>
     * </ul>
     *
     * <p>The quantized subtract interval is treated strictly as a set: a zero-width interval only
     * removes anything when <em>both</em> endpoints are inclusive (a real single point). Forms like
     * {@code (x, x]}, {@code [x, x)}, and {@code (x, x)} are empty and the call is a no-op.
     *
     * <p>For each affected segment, surviving remnants keep the segment's original outer inclusivity
     * and receive the complement of the subtract's inclusivity at each cut boundary.
     *
     * <p>Complexity: {@code O(log n + k)} for the binary search and walk, where {@code n} is the
     * segment count and {@code k} is the number of segments touched; the splice into the backing
     * {@link ArrayList} adds at most one contiguous shift.
     *
     * @param newInterval          the interval to quantize and subtract.
     * @param expandLeft           {@code true} to snap the start outward (lesser); {@code false} inward.
     * @param expandRight          {@code true} to snap the end outward (greater); {@code false} inward.
     * @param isLeftSnapInclusive  inclusivity assigned to the start when it is off-grid and snapped.
     * @param isRightSnapInclusive inclusivity assigned to the end when it is off-grid and snapped.
     */
    public void subtractInterval(
        FixedLongInterval newInterval,
        boolean expandLeft,
        boolean expandRight,
        boolean isLeftSnapInclusive,
        boolean isRightSnapInclusive
    ){
        if(!isMerged) merge();
        final int n = mergeList.size();
        if(n == 0) return;

        //Quantize endpoints to the grid (same rules as addInterval).
        long sStart;
        long sEnd;
        boolean sLeftInc;
        boolean sRightInc;

        long dist = Math.floorMod(newInterval.start - offsetMod, microInterval.width);
        if(dist != 0){
            sStart = expandLeft
                ? newInterval.start - dist
                : newInterval.start + (microInterval.width - dist);
            sLeftInc = isLeftSnapInclusive;
        }else{
            sStart = newInterval.start;
            sLeftInc = newInterval.inclusiveStart;
        }

        dist = Math.floorMod(newInterval.end - offsetMod, microInterval.width);
        if(dist != 0){
            sEnd = expandRight
                ? newInterval.end + (microInterval.width - dist)
                : newInterval.end - dist;
            sRightInc = isRightSnapInclusive;
        }else{
            sEnd = newInterval.end;
            sRightInc = newInterval.inclusiveEnd;
        }

        //Strict empty check: zero-width only acts when both sides inclusive (a real point).
        if(sEnd < sStart) return;
        if(sEnd == sStart && !(sLeftInc && sRightInc)) return;

        //Binary search for first segment whose end is not strictly left of S.
        int lo = 0;
        int hi = n;
        while(lo < hi){
            int mid = (lo + hi) >>> 1;
            FixedLongInterval seg = mergeList.get(mid);
            if(seg.end < sStart ||
               (seg.end == sStart && !(seg.inclusiveEnd && sLeftInc))){
                lo = mid + 1;
            }else{
                hi = mid;
            }
        }
        final int firstIdx = lo;
        if(firstIdx >= n) return;

        final long microDur = microInterval.width;

        //Walk the affected contiguous run, building 0/1/2 remnants per segment.
        ArrayList<FixedLongInterval> buffered = new ArrayList<>();
        ArrayList<Long> bufferedCounts = new ArrayList<>();
        int lastAffected = firstIdx - 1;
        for(int i = firstIdx; i < n; i++){
            FixedLongInterval seg = mergeList.get(i);
            if(seg.start > sEnd ||
               (seg.start == sEnd && !(seg.inclusiveStart && sRightInc))){
                break;
            }
            lastAffected = i;

            //Left remnant: portion of seg strictly before S.
            if(seg.start < sStart ||
               (seg.start == sStart && seg.inclusiveStart && !sLeftInc)){
                FixedLongInterval lr = new FixedLongInterval(
                    seg.start, sStart, seg.inclusiveStart, !sLeftInc
                );
                buffered.add(lr);
                bufferedCounts.add(lr.width / microDur);
            }

            //Right remnant: portion of seg strictly after S.
            if(seg.end > sEnd ||
               (seg.end == sEnd && seg.inclusiveEnd && !sRightInc)){
                FixedLongInterval rr = new FixedLongInterval(
                    sEnd, seg.end, !sRightInc, seg.inclusiveEnd
                );
                buffered.add(rr);
                bufferedCounts.add(rr.width / microDur);
            }
        }

        //Splice buffered into mergeList[firstIdx..lastAffected] and microCount likewise.
        final int affectedCount = lastAffected - firstIdx + 1;
        final int newCount = buffered.size();
        final int overwrite = Math.min(affectedCount, newCount);

        for(int i = 0; i < overwrite; i++){
            mergeList.set(firstIdx + i, buffered.get(i));
            microCount.set(firstIdx + i, bufferedCounts.get(i));
        }
        if(newCount < affectedCount){
            mergeList.subList(firstIdx + newCount, lastAffected + 1).clear();
            microCount.subList(firstIdx + newCount, lastAffected + 1).clear();
        }else if(newCount > affectedCount){
            for(int i = affectedCount; i < newCount; i++){
                mergeList.add(firstIdx + i, buffered.get(i));
                microCount.add(firstIdx + i, bufferedCounts.get(i));
            }
        }
        //mergeList stays sorted and disjoint; isMerged stays true.
    }

    /**
     * Convenience overload of {@link #subtractInterval(FixedLongInterval, boolean, boolean, boolean, boolean)}
     * that preserves the original inclusivity of {@code newInterval} for any off-grid snapped endpoints.
     *
     * @param newInterval the interval to quantize and subtract.
     * @param expandLeft  {@code true} to snap the start outward (lesser); {@code false} inward.
     * @param expandRight {@code true} to snap the end outward (greater); {@code false} inward.
     */
    public void subtractInterval(FixedLongInterval newInterval, boolean expandLeft, boolean expandRight){
        subtractInterval(
            newInterval,
            expandLeft,
            expandRight,
            newInterval.inclusiveStart,
            newInterval.inclusiveEnd
        );
    }

    /**
     * Subtracts {@code newInterval} with both endpoints snapped outward and exclusive snap points.
     *
     * <p>Equivalent to
     * {@link #subtractInterval(FixedLongInterval, boolean, boolean, boolean, boolean)
     * subtractInterval(newInterval, true, true, false, false)}.
     * For grid-aligned endpoints the snap parameters are ignored and the original inclusivity
     * of {@code newInterval} is used, so a pre-snapped {@code [a, b]} correctly subtracts
     * {@code [a, b]}.
     *
     * @param newInterval the interval to quantize and subtract.
     */
    public void subtractInterval(FixedLongInterval newInterval){
        subtractInterval(newInterval, true, true, false, false);
    }

    /**
     * Removes the single point {@code point} from this set.
     *
     * <p>If {@code point} is on a grid boundary it is removed as the singleton {@code {point}}.
     * If it is off-grid, both endpoints snap outward (inclusive), removing the entire surrounding
     * micro-interval cell — the same coverage that {@link #addInterval(long)} would add.
     *
     * @param point the value to remove.
     */
    public void subtractInterval(long point){
        subtractInterval(
            new FixedLongInterval(point, point, true, true),
            true, true, true, true
        );
    }

    /**
     * Consolidates {@code mergeList} into a minimal, ordered list of non-overlapping {@link FixedLongInterval}s,
     * merging any intervals that overlap or are adjacent on the number line.
     * <p>
     * Any interval with {@link FixedLongInterval#isEmpty} {@code == true} is discarded before the
     * sweep. Such intervals can only enter the list through the varargs or set-copy constructors;
     * {@link #addInterval} and {@link #subtractInterval} already enforce the empty-set invariant.
     * <p>
     * After this call, {@link #isMerged()} returns {@code true} and {@code microCount} is kept
     * parallel to {@code mergeList}: {@code microCount.get(i)} holds the number of micro-interval
     * chunks contained in {@code mergeList.get(i)}.
     * <p>
     * Algorithm: sort by start ascending ({@code O(n log n)}, TimSort; fast on partially-sorted
     * input), then a single linear sweep merges touching runs in place. A new
     * {@link FixedLongInterval} is allocated only when a run actually merged; runs with no merges
     * reuse the original reference.
     * <p>
     * When two intervals are merged, the resulting interval spans from the lesser start to the
     * greater end. If both share the same boundary point, the merged boundary is inclusive if
     * either original boundary was inclusive.
     */
    public void merge(){
        microCount.clear();
        mergeList.removeIf(i -> i.isEmpty);

        final int n = mergeList.size();
        if(n == 0){ isMerged = true; return; }
        if(n == 1){
            microCount.add(mergeList.get(0).width / microInterval.width);
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
                microCount.add((curEnd - curStart) / microInterval.width);

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
        microCount.add((curEnd - curStart) / microInterval.width);

        if(write < n) mergeList.subList(write, n).clear();
        isMerged = true;
    }

    public void clear(){
        mergeList.clear();
        microCount.clear();
        isMerged = true;
    }

    /**
     * Returns {@code true} if {@code set1} and {@code set2} represent the same mathematical set.
     *
     * <p>Both sets are merged before comparison. Two sets are equal if and only if they have the
     * same number of segments and each corresponding pair of segments satisfies
     * {@link FixedLongInterval#equals}.
     *
     * @param set1 the first set; must not be {@code null}.
     * @param set2 the second set; must not be {@code null}.
     * @return {@code true} if the two sets are mathematically equivalent.
     */
    public static boolean equals(AlignedLongSet set1, AlignedLongSet set2){
        ArrayList<FixedLongInterval> intL1 = set1.getIntervals();
        ArrayList<FixedLongInterval> intL2 = set2.getIntervals();

        if(intL1.size() != intL2.size()) return false;
        for(int i = 0; i<intL1.size(); ++i){
            if(!FixedLongInterval.equals(intL1.get(i), intL2.get(i))) return false;
        }

        return true;
    }

    /**
     * Returns {@code true} if {@code set1} and {@code set2} have identical segment lists after merging.
     *
     * <p>Unlike {@link #equals}, comparison uses {@link FixedLongInterval#equalsStructural}, so two
     * empty intervals at different positions ({@code [3,3)} vs {@code (5,5)}) are not structurally
     * equal even though they are mathematically equivalent empty sets.
     *
     * @param set1 the first set; must not be {@code null}.
     * @param set2 the second set; must not be {@code null}.
     * @return {@code true} if the merged segment lists are element-wise structurally equal.
     */
    public static boolean equalsStructural(AlignedLongSet set1, AlignedLongSet set2){
        ArrayList<FixedLongInterval> intL1 = set1.getIntervals();
        ArrayList<FixedLongInterval> intL2 = set2.getIntervals();

        if(intL1.size() != intL2.size()) return false;
        for(int i = 0; i<intL1.size(); ++i){
            if(!FixedLongInterval.equalsStructural(intL1.get(i), intL2.get(i))) return false;
        }

        return true;
    }

    /**
     * Returns a string representation of this set as a union of interval notations.
     *
     * <p>Triggers a merge if one is pending. The empty set is represented as {@code "{}"}.
     * Multiple segments are joined with {@code "U"} in ascending order, for example
     * {@code "(2,5)U(8,11]"}.
     *
     * @return string representation of the merged set.
     */
    @Override
    public String toString(){
        if(mergeList.size() == 0) return "{}";
        if(!isMerged) merge();

        StringBuilder bldr = new StringBuilder();
        bldr.append(mergeList.get(0).toString());
        for(int i=1; i<mergeList.size(); ++i){bldr.append("U"+mergeList.get(i).toString());}
        return bldr.toString();
    }
}