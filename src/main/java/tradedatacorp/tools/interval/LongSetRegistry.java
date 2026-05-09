/**
 * @author Bruce Lamb
 * @since 8 MAY 2026
 */
package tradedatacorp.tools.interval;

import java.util.ArrayList;

/**
 * A generic coverage tracker for a collection of bounded windows.
 * A Registry instance will contain an ordered list of conceptual slots that are bounded by a {@code FixedLongInterval}.
 * No slot interval will overlap with another slot
 * Withn each slot, will be a {@AlignedLongSet} that will be a subset of the bounded interval for that slot which represents a "done" or "truthy" set for the slot.
 * {@code FixedLongInterval}s and {@AlignedLongSet}s can be added to slots. The Set
 * Applications of this class include timers, and byte tracks.
 *
 * A {@code LongSetRegistry} manages an ordered list of {@link Slot}s. Each slot represents a bounded
 * window at a fixed step resolution defined by {@code microInterval}. All slots share the same
 * {@code microInterval}.
 *
 * Within each slot, {@code doneCoverage} tracks which portions of the window have been marked as
 * covered. When {@code doneCoverage} forms a single contiguous span that exactly covers the slot's
 * full boundary, the slot is considered complete ({@link Slot#isSlotDone()} returns {@code true}).
 *
 * Slots never overlap. Gaps between slots are permitted. {@code totalBoundary} and
 * {@code totalCoverage} provide aggregate views across all slots.
 *
 * This class is a general-purpose utility with no dependencies on any other layer of the
 * project. It can be used independently for any coverage tracking need.
 *
 * //TODO: Class still in development and documentation may change.
 */
public class LongSetRegistry{
    private final FixedLongInterval microInterval;

    //"merged" status fields: This class is not merged until these fields are accurate and updated
    private boolean isMerged;
    private AlignedLongSet totalBoundary;  //the boundary across all slots
    private AlignedLongSet totalCoverage; //the "truthy" done coverage across all slots
    //END merged status fields

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

    /**
     * Returns {@code true} when the aggregate {@code totalBoundary} and {@code totalCoverage} views
     * are up to date with the underlying slot list.
     *
     * @return {@code true} if no merge is pending; {@code false} if an aggregate refresh is needed.
     */
    public boolean isMerged(){return isMerged;}

    /**
     * Return true if each Slot boundary is connected with no gaps.
     * No slots added is considedered a continuous registry.
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

    public String getSlotBoundIntervalString(int slotIndex){
        return slotList.get(slotIndex).boundedInterval.toString();
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

    public String getSlotCoverageIntervalSring(int slotIndex){
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

    public boolean isSlotDone(int slotIndex){return slotList.get(slotIndex).isSlotDone();}

    /**
     * Adds an interval slot to the registry. Will snap to microIntervals if required.
     * The slot is inserted at its correct position so {@code slotList} stays sorted
     * by {@code boundedInterval.start} ascending. On overlap, the candidate is split
     * against existing slots and each non-overlapping fragment is inserted in order.
     * TODO:
     *   What happens if domain overlaps with an existing Slot domain?
     *      - Should it drop, ignore, or throw exception?
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
     * Adds coverage to slots that overlap the slot.
     * Portion of the set will be ignored if there is no slot that can bind the set.
     * @param set
     */
    public void addCoverage(AlignedLongSet set){
        for(Slot s : slotList){s.addCoverage(set);}
        isMerged = false;
    }

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
     * Convenience overload of
     * {@link #addSlot(FixedLongInterval, boolean, boolean, boolean, boolean, boolean, boolean, boolean)}
     * that preserves {@code domain}'s original inclusivity at any off-grid snapped endpoints and
     * defaults to expanding into adjacent slot gaps and contracting on overlap.
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

    public void clearAllCoverage(){
        for(Slot s : slotList){s.clearCoverage();}
        totalCoverage.clear();
    }

    public void clearSlotCoverage(int slotIndex){
        slotList.get(slotIndex).clearCoverage();
        isMerged=false;
    }

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
    // public FixedInterval removeSlot(int index){}

    //markIntervalDone(FixedInterval interval){...}

    //markSpanDone(AlignedLongSet span){...} //same as markIntervalDone

    //A slot of bounded AlignedLongSet
    private class Slot{
        private FixedLongInterval boundedInterval; //The domain, no value can exist outside the bounds of the interval.
        private AlignedLongSet doneCoverage;       //a "truthy" interval. Will never go out of bounded interval.

        private Slot(long start, long end, boolean isInclusiveStart, boolean isInclusiveEnd){
            boundedInterval = new FixedLongInterval(start, end, isInclusiveStart, isInclusiveEnd);
            doneCoverage = new AlignedLongSet(microInterval); //Used as a tmp normalizer for snapping boundedInterval
        }

        private Slot(long start, long end){this(start, end, true, false);}

        public boolean isSlotDone(){
            return
                doneCoverage.getIntervalSegmentCount() == 1 &&
                FixedLongInterval.equals(boundedInterval, doneCoverage.getInterval(0));
        }

        private AlignedLongSet getCoverage(){
            if(!doneCoverage.isMerged()) doneCoverage.merge();
            return doneCoverage;
        }
        private void addCoverage(AlignedLongSet set){
            doneCoverage.addSet(set);

            doneCoverage.cutLower(boundedInterval.start, !boundedInterval.inclusiveStart);
            doneCoverage.cutUpper(boundedInterval.end, !boundedInterval.inclusiveEnd);
        }

        private void addCoverage(FixedLongInterval intv){
            doneCoverage.addInterval(intv);

            doneCoverage.cutLower(boundedInterval.start, !boundedInterval.inclusiveStart);
            doneCoverage.cutUpper(boundedInterval.end, !boundedInterval.inclusiveEnd);
        }

        private void clearCoverage(){doneCoverage.clear();}
    }
}