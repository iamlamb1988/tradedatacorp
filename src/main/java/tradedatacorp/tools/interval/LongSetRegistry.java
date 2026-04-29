/**
 * @author Bruce Lamb
 * @since 29 APR 2026
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
 * Slots never overlap. Gaps between slots are permitted. {@code totalBoundry} and
 * {@code totalCoverage} provide aggregate views across all slots.
 *
 * This class is a general-purpose utility with no dependencies on any other layer of the
 * project. It can be used independently for any coverage tracking need.
 *
 * //TODO: Class still in development and documentation may change.
 */
public class LongSetRegistry{
    public static final FixedLongInterval EMPTY_INTERVAL = new FixedLongInterval(0, 0, false, false);

    private final FixedLongInterval microInterval;

    //"merged" status fields: This class is not merged until these fields are accurate and updated
    private boolean isMerged;
    private AlignedLongSet totalBoundry;  //the boundry across all slots
    private AlignedLongSet totalCoverage; //the "truthy" done coverage across all slots
    //END merged status fields

    private ArrayList<Slot> slotList;     // ordered list of slots

    public LongSetRegistry(FixedLongInterval microInterval){
        if(microInterval.width == 0)
            throw new IllegalArgumentException("LongSetRegistry requires a micro interval with a width > 0.");

        this.microInterval = microInterval;

        totalBoundry = new AlignedLongSet(microInterval);
        totalCoverage = new AlignedLongSet(microInterval);
        slotList = new ArrayList<>();
        isMerged = true;
    }

    public boolean isMerged(){return isMerged;}

    /**
     * Return true if each Slot boundry is connected with no gaps.
     * No slots added is considedered a continuous registry.
     */
    public boolean isBoundContinuous(){
        if(slotList.size() <= 1) return true;

        Slot current = slotList.get(0);
        for(int i=1; i<slotList.size(); ++i){
            Slot next = slotList.get(i);
            if(
                current.boundedInterval.end != next.boundedInterval.start ||
                current.boundedInterval.inclusiveEnd == next.boundedInterval.inclusiveStart
            ) return false;
        }
        return true;
    }

    /**
     * Returns the number of registered slots
     */
    public int getSlotCount(){return slotList.size();}

    /**
     * Returns the mathematical interval string representing the entire domain of this registry state
     */
    public String getBoundryIntervalString(){
        if(!isMerged) merge();
        return totalBoundry.toString();
    }

    public String getCoverageIntervalString(){
        if(!isMerged) merge();
        return totalCoverage.toString();
    }

    public long getMicroIntervalCountInBoundry(){
        if(!isMerged) merge();
        return totalBoundry.getMicroIntervalCount();
    }

    public long getMicroIntervalCountCovered(){
        if(!isMerged) merge();
        return totalCoverage.getMicroIntervalCount();
    }

    /**
     * Adds an interval slot to the registry. Will snap to microIntervals if required.
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
        boolean expandToRightSlot,
        boolean contractOverlap
    ){
        if(domain.width == 0)
            throw new IllegalArgumentException("A slot within a registry requires a micro interval with a width > 0.");

        if(slotList.size() == 0){
            FixedLongInterval snappedDomain = totalBoundry.getSnappedInterval(
                domain,
                expandLeft,
                expandRight,
                isLeftSnapInclusive,
                isRightSnapInclusive
            );
            slotList.add(new Slot(snappedDomain.start, snappedDomain.end, snappedDomain.inclusiveStart, snappedDomain.inclusiveEnd));
        }
        //TODO: Handle cases to trim or expand domain based on booleans OR throw exception
        //no overlap or gaps possible, add as normal
        //may need to trim left or right IF overlapped or gapped to existing slot
        isMerged = false;
    }

    public void addSlot(
        FixedLongInterval domain
    ){
        addSlot(
            domain,
            domain.inclusiveStart,
            domain.inclusiveEnd,
            domain.inclusiveStart,
            domain.inclusiveEnd,
            true,
            true,
            true
        );
    }

    public void addSlot(long point, boolean expandGap){}

    public void merge(){
        AlignedLongSet tmpTotalBound = new AlignedLongSet(microInterval);
        AlignedLongSet tmpTotalDone  = new AlignedLongSet(microInterval);
        for(Slot slot : slotList){
            if(!slot.doneCoverage.isMerged()) slot.doneCoverage.merge();
            tmpTotalBound.addInterval(slot.boundedInterval);
            tmpTotalDone.addSet(slot.doneCoverage);
        }

        totalBoundry = tmpTotalBound;
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
        private final long maxMicroIntervals;      //number of possible microIntervals within bounded

        private Slot(long start, long end, boolean isInclusiveStart, boolean isInclusiveEnd){
            boundedInterval = new FixedLongInterval(start, end, isInclusiveStart, isInclusiveEnd);
            doneCoverage = new AlignedLongSet(microInterval); //Used as a tmp normalizer for snapping boundedInterval
            maxMicroIntervals = boundedInterval.width/microInterval.width;
        }

        private Slot(long start, long end){this(start, end, true, false);}

        public boolean isMerged(){return doneCoverage.isMerged();}
        public boolean isSlotDone(){
            return
                doneCoverage.getIntervalSegmentCount() == 1 &&
                FixedLongInterval.equals(boundedInterval, doneCoverage.getInterval(0));
        }

        private void merge(){doneCoverage.merge();}
    }
}