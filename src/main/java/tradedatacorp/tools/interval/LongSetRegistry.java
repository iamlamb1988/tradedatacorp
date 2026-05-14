/**
 * @author Bruce Lamb
 * @since 14 MAY 2026
 */
package tradedatacorp.tools.interval;

import java.util.ArrayList;

/**
 * A generic coverage tracker for a collection of bounded windows.
 *
 * <p>A {@code LongSetRegistry} manages an ordered, non-overlapping list of {@link Slot}s.
 * Each slot has a {@link FixedLongInterval} {@code boundedInterval} (its domain) and an
 * {@link AlignedLongSet} {@code doneCoverage} that tracks the "truthy" portion of the
 * domain marked as covered. Per-slot {@code doneCoverage} is always a subset of that
 * slot's {@code boundedInterval}.
 *
 * <p>All slots share a single {@code microInterval} grid (phase + step). Gaps between
 * slots are permitted; aggregate views across all slots are exposed via
 * {@code totalBoundary} and {@code totalCoverage}.
 *
 * <p>A slot is "done" ({@link Slot#isSlotDone()}) when its {@code doneCoverage} is a
 * single segment mathematically equal to its {@code boundedInterval}.
 *
 * <p>This class is a general-purpose utility with no dependencies on any other layer of
 * the project. Applications include timers and byte tracks.
 *
 * //TODO: Class still in development and documentation may change.
 */
public class LongSetRegistry{
    private final FixedLongInterval microInterval;

    //Aggregate views; valid only when isMerged is true. Otherwise an aggregate refresh is pending.
    private boolean isMerged;
    private AlignedLongSet totalBoundary; //union of every slot's boundedInterval
    private AlignedLongSet totalCoverage; //union of every slot's doneCoverage

    private ArrayList<Slot> slotList;     // ordered list of slots

    /**
     * Constructs an empty registry whose slots all share the given micro-interval grid.
     *
     * @param microInterval the grid unit; its {@code start} sets the phase and its {@code width}
     *                      sets the step size for every slot's coverage. Must have positive width.
     * @throws IllegalArgumentException if {@code microInterval.width == 0}.
     */
    public LongSetRegistry(FixedLongInterval microInterval){
        if(microInterval.width == 0)
            throw new IllegalArgumentException("LongSetRegistry requires a micro interval with a width > 0.");

        this.microInterval = microInterval;

        totalBoundary = new AlignedLongSet(microInterval);
        totalCoverage = new AlignedLongSet(microInterval);
        slotList = new ArrayList<>();
        isMerged = true;
    }

    public FixedLongInterval getMicroInterval(){return microInterval;}

    /**
     * Returns {@code true} when the aggregate {@code totalBoundary} and {@code totalCoverage} views
     * are up to date with the underlying slot list.
     *
     * @return {@code true} if no merge is pending; {@code false} if an aggregate refresh is needed.
     */
    public boolean isMerged(){return isMerged;}

    /**
     * Return true if each Slot boundary is connected with no gaps.
     * No slots added is considered a continuous registry.
     */
    public boolean isBoundContinuous(){
        if(slotList.size() <= 1) return true;

        if(!isMerged) merge();

        return totalBoundary.getIntervalSegmentCount() <= 1;
    }

    /**
     * Returns the number of registered slots
     */
    public int getSlotCount(){return slotList.size();}

    /**
     * Returns the mathematical interval string representing the entire domain of this registry state
     */
    public String getBoundaryIntervalString(){
        if(!isMerged) merge();
        return totalBoundary.toString();
    }

    AlignedLongSet getBoundary(){
        if(!isMerged) merge();
        return totalBoundary;
    }

    /**
     * Returns the slot's bounded-interval as a mathematical interval string.
     *
     * @param slotIndex position in the ordered slot list.
     * @return interval string (e.g. {@code "[3,7)"}) for the slot's domain.
     */
    public String getSlotBoundIntervalString(int slotIndex){
        return slotList.get(slotIndex).boundedInterval.toString();
    }

    public FixedLongInterval getSlotBoundary(int slotIndex){
        return slotList.get(slotIndex).boundedInterval;
    }

    public FixedLongInterval getLastSlotBoundary(){
        return slotList.size() > 0 ?
               getSlotBoundary(slotList.size() - 1) :
               null;
    }

    /**
     * Returns the mathematical interval string representing the union of done coverage across
     * all slots. Triggers a merge if one is pending.
     *
     * @return the merged coverage as a union-of-intervals string (e.g. {@code "[0,5)U[8,11]"}),
     *         or {@code "{}"} if no slot has any coverage.
     */
    public String getCoverageIntervalString(){
        if(!isMerged) merge();
        return totalCoverage.toString();
    }

    AlignedLongSet getCoverage(){
        if(!isMerged) merge();
        return totalCoverage;
    }

    /**
     * Returns the slot's done-coverage as a union-of-intervals string. Triggers a per-slot
     * merge if pending.
     *
     * @param slotIndex position in the ordered slot list.
     * @return coverage string (e.g. {@code "[3,5)U{8}"}); {@code "{}"} if empty.
     */
    public String getSlotCoverageIntervalString(int slotIndex){
        return slotList.get(slotIndex).getCoverage().toString();
    }

    /**
     * Returns the total number of micro-interval grid steps spanned by the union of all slot
     * boundaries. Triggers a merge if one is pending.
     *
     * @return aggregate boundary step count; {@code 0} if there are no slots.
     */
    public long getMicroIntervalCountInBoundary(){
        if(!isMerged) merge();
        return totalBoundary.getMicroIntervalCount();
    }

    /**
     * Returns the total number of micro-interval grid steps marked as done across all slots.
     * Triggers a merge if one is pending.
     *
     * @return aggregate done-coverage step count; {@code 0} if no slot has any coverage.
     */
    public long getMicroIntervalCountCovered(){
        if(!isMerged) merge();
        return totalCoverage.getMicroIntervalCount();
    }

    /**
     * Returns {@code true} if the slot's {@code doneCoverage} is a single segment mathematically
     * equal to its {@code boundedInterval}.
     *
     * @param slotIndex position in the ordered slot list.
     */
    public boolean isSlotDone(int slotIndex){return slotList.get(slotIndex).isSlotDone();}

    /**
     * Adds a slot to the registry. The slot's domain is snapped to the {@code microInterval}
     * grid, then inserted at its sorted position so {@code slotList} stays sorted by
     * {@code boundedInterval.start} ascending.
     *
     * <p>If the snapped candidate overlaps existing slots, it is sliced against those slots'
     * {@code boundedInterval}s; only the non-overlapping fragments are inserted, and the
     * {@code expandToLeftSlot} / {@code expandToRightSlot} flags are ignored on this path.
     * If {@code domain.width == 0} the call is a silent no-op.
     *
     * @param domain               the interval defining the slot's window before snapping.
     * @param expandLeft           {@code true} to snap an off-grid start outward (lesser);
     *                             {@code false} to snap inward (greater).
     * @param expandRight          {@code true} to snap an off-grid end outward (greater);
     *                             {@code false} to snap inward (lesser).
     * @param isLeftSnapInclusive  inclusivity assigned to the start when it is off-grid and snapped.
     * @param isRightSnapInclusive inclusivity assigned to the end when it is off-grid and snapped.
     * @param expandToLeftSlot     when no overlap and a previous slot exists, extend this slot's
     *                             start leftward to abut the prior slot's end (closing the gap).
     * @param expandToRightSlot    when no overlap and a next slot exists, extend this slot's end
     *                             rightward to abut the next slot's start (closing the gap).
     */
    public void addSlot(
        FixedLongInterval domain,
        boolean expandLeft,
        boolean expandRight,
        boolean isLeftSnapInclusive,
        boolean isRightSnapInclusive,
        boolean expandToLeftSlot,
        boolean expandToRightSlot
    ){
        if(domain.width == 0) return;

        FixedLongInterval candidate = totalBoundary.getSnappedInterval(
            domain,
            expandLeft,
            expandRight,
            isLeftSnapInclusive,
            isRightSnapInclusive
        );

        int n = slotList.size();
        if(n == 0){
            slotList.add(new Slot(candidate.start, candidate.end, candidate.inclusiveStart, candidate.inclusiveEnd));
            isMerged = false;
            return;
        }

        int insertPos = binarySearchInsertPos(candidate.start);

        int overlapStart = insertPos,
            overlapEnd = insertPos;
        if(insertPos > 0 && slotList.get(insertPos - 1).boundedInterval.overlaps(candidate)){
            overlapStart = insertPos - 1;
        }
        while(overlapEnd < n && slotList.get(overlapEnd).boundedInterval.overlaps(candidate)){
            ++overlapEnd;
        }

        if(overlapStart < overlapEnd){
            AlignedLongSet unslottedRange = new AlignedLongSet(microInterval, candidate);
            for(int i = overlapStart; i < overlapEnd; ++i){
                unslottedRange.subtractInterval(slotList.get(i).boundedInterval);
            }

            int fragInsert = overlapStart;
            for(FixedLongInterval intv : unslottedRange.getIntervals()){
                while(fragInsert < slotList.size() && slotList.get(fragInsert).boundedInterval.start < intv.start){
                    ++fragInsert;
                }
                slotList.add(fragInsert, new Slot(intv.start, intv.end, intv.inclusiveStart, intv.inclusiveEnd));
                ++fragInsert;
            }

            isMerged = false;
            return;
        }

        long candidateStart = candidate.start,
             candidateEnd = candidate.end;

        boolean candidateInclStart = candidate.inclusiveStart,
                candidateInclEnd = candidate.inclusiveEnd;

        if(insertPos > 0 && expandToLeftSlot){
            FixedLongInterval left = slotList.get(insertPos - 1).boundedInterval;
            candidateStart = left.end;
            candidateInclStart = !left.inclusiveEnd;
        }

        if(insertPos < n && expandToRightSlot){
            FixedLongInterval right = slotList.get(insertPos).boundedInterval;
            candidateEnd = right.start;
            candidateInclEnd = !right.inclusiveStart;
        }

        slotList.add(insertPos, new Slot(candidateStart, candidateEnd, candidateInclStart, candidateInclEnd));
        isMerged = false;
    }

    /**
     * Adds {@code set}'s coverage to every slot whose {@code boundedInterval} overlaps it.
     * Each slot independently dices the input against its own bounds; portions that fall
     * outside every slot are silently dropped. Marks the registry's aggregate views as stale.
     *
     * @param set the coverage to add.
     */
    public void addCoverage(AlignedLongSet set){
        for(Slot s : slotList){s.addCoverage(set);}
        isMerged = false;
    }

    /**
     * Adds {@code intv}'s coverage to every slot whose {@code boundedInterval} overlaps it.
     * Each slot independently dices the input against its own bounds; portions that fall
     * outside every slot are silently dropped. Marks the registry's aggregate views as stale.
     *
     * @param intv the interval to add as coverage.
     */
    public void addCoverage(FixedLongInterval intv){
        for(Slot s : slotList){s.addCoverage(intv);}
        isMerged = false;
    }

    private int binarySearchInsertPos(long start){
        int lo = 0,
            hi = slotList.size();
        while(lo < hi){
            int m = (lo + hi) >>> 1;
            if(slotList.get(m).boundedInterval.start < start) lo = m + 1;
            else hi = m;
        }
        return lo;
    }

    /**
     * Convenience overload that snaps each off-grid endpoint in the direction that preserves the
     * original endpoint's set membership: an inclusive endpoint snaps outward (its point stays
     * inside), an exclusive endpoint snaps inward (its point stays outside). Snap-inclusivity
     * matches {@code domain}'s original inclusivity. Does not expand into adjacent slot gaps.
     *
     * @param domain the interval defining the slot's window.
     */
    public void addSlot(FixedLongInterval domain){
        addSlot(
            domain,
            domain.inclusiveStart,
            domain.inclusiveEnd,
            domain.inclusiveStart,
            domain.inclusiveEnd,
            false,
            false
        );
    }

    /**
     * Convenience overload that snaps both endpoints outward, preserves {@code domain}'s
     * original inclusivity at off-grid endpoints, and applies {@code expandGap} to both
     * gap-closing flags.
     *
     * @param domain    the interval defining the slot's window.
     * @param expandGap when {@code true}, the inserted slot is stretched on insertion to abut
     *                  adjacent slots on either side (closing any gap with no overlap).
     */
    public void addSlot(FixedLongInterval domain, boolean expandGap){
        addSlot(
            domain,
            true,
            true,
            domain.inclusiveStart,
            domain.inclusiveEnd,
            expandGap,
            expandGap
        );
    }

    /**
     * Clears {@code doneCoverage} on every slot. Slot domains and the {@code microInterval}
     * grid are preserved.
     */
    public void clearAllCoverage(){
        for(Slot s : slotList){s.clearCoverage();}
        totalCoverage.clear();
    }

    /**
     * Clears {@code doneCoverage} on the slot at {@code slotIndex}. The slot's domain is preserved.
     */
    public void clearSlotCoverage(int slotIndex){
        slotList.get(slotIndex).clearCoverage();
        isMerged=false;
    }

    /**
     * Removes the slot at {@code slotIndex} from the registry, including any coverage it held.
     */
    public void removeSlot(int slotIndex){
        slotList.remove(slotIndex);
        isMerged = false;
    }

    /**
     * Recomputes {@code totalBoundary} and {@code totalCoverage} from the current slot list,
     * forcing a merge on each slot's done coverage if pending. After this call
     * {@code totalCoverage} is always a subset of {@code totalBoundary}
     * {@link #isMerged()} returns {@code true}.
     *
     * <p>{@code slotList} is maintained in sorted order by {@link #addSlot} at insertion time,
     * so this method does not re-sort it.
     *
     * <p>Complexity: {@code O(s + k)} where {@code s} is the slot count and {@code k} is the
     * total number of segments across all slots' done coverage.
     */
    public void merge(){
        if(isMerged) return;

        AlignedLongSet tmpTotalBound = new AlignedLongSet(microInterval);
        AlignedLongSet tmpTotalDone  = new AlignedLongSet(microInterval);
        for(Slot slot : slotList){
            if(!slot.doneCoverage.isMerged()) slot.doneCoverage.merge();
            tmpTotalBound.addInterval(slot.boundedInterval);
            tmpTotalDone.addSet(slot.doneCoverage);
        }

        totalBoundary = tmpTotalBound;
        totalCoverage = tmpTotalDone;
        isMerged = true;
    }

    public void clear(){
        slotList.clear();
        totalBoundary.clear();
        totalCoverage.clear();
        isMerged = true;
    }

    /** A bounded slot whose AlignedLongSet doneCoverage is constrained to its boundedInterval. */
    private class Slot{
        private FixedLongInterval boundedInterval; //The domain, no value can exist outside the bounds of the interval.
        private AlignedLongSet doneCoverage;       //a "truthy" interval. Will never go out of bounded interval.

        private Slot(long start, long end, boolean isInclusiveStart, boolean isInclusiveEnd){
            boundedInterval = new FixedLongInterval(start, end, isInclusiveStart, isInclusiveEnd);
            doneCoverage = new AlignedLongSet(microInterval);
        }

        private Slot(long start, long end){this(start, end, true, false);}

        /** True iff doneCoverage is a single segment mathematically equal to boundedInterval. */
        public boolean isSlotDone(){
            return
                doneCoverage.getIntervalSegmentCount() == 1 &&
                FixedLongInterval.equals(boundedInterval, doneCoverage.getInterval(0));
        }

        /** Returns doneCoverage, forcing a merge first if pending. */
        private AlignedLongSet getCoverage(){
            if(!doneCoverage.isMerged()) doneCoverage.merge();
            return doneCoverage;
        }

        /** Adds set then chops to boundedInterval, keeping doneCoverage a subset of the slot's domain. */
        private void addCoverage(AlignedLongSet set){
            doneCoverage.addSet(set);

            doneCoverage.cutLower(boundedInterval.start, !boundedInterval.inclusiveStart);
            doneCoverage.cutUpper(boundedInterval.end, !boundedInterval.inclusiveEnd);
        }

        /** Adds intv then chops to boundedInterval, keeping doneCoverage a subset of the slot's domain. */
        private void addCoverage(FixedLongInterval intv){
            doneCoverage.addInterval(intv);

            doneCoverage.cutLower(boundedInterval.start, !boundedInterval.inclusiveStart);
            doneCoverage.cutUpper(boundedInterval.end, !boundedInterval.inclusiveEnd);
        }

        private void clearCoverage(){doneCoverage.clear();}
    }
}