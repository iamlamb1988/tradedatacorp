/**
 * @author Bruce Lamb
 * @since 27 APR 2026
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

    //"merged" status fields
    private boolean isMerged;
    private AlignedLongSet totalBoundry;  //the boundry across all slots
    private AlignedLongSet totalCoverage; //the "truthy" done coverage across all slots

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

        if(slotList.size() == 0){}
    
    }

    

    public void addSlot(long point, boolean expandGap){}

    // /**
    //  * Will add a slot to the registry if available. Will flex appropriately boundries to fit and touch adjacent slots
    //  */
    // public void addSlot(
    //     long startSlotBound,
    //     long endSlotBound,
    //     boolean expandIfGap,
    //     boolean contractIfoverlap
    // ){
    //     if(startSlotBound == endSlotBound)
    //         throw new IllegalArgumentException("LongSetRegistry cannot add a slot into registry of 0 width");

    //     if(slotList.size() == 0){//No possibility of gapping or overlapping slot
    //         slotList.add(new Slot(startSlotBound, endSlotBound));
    //         isMerged = true;
    //         return;
    //     }

    //     Slot leftSlot = null;
    //     Slot rightSlot = null;

    //     if(expandIfGap){
    //         //1. Find adjacent slot to the left (if no slot left of start, then skip)
    //         //expand to the next slot left (if any). If no slot to the left, skip.
    //         //need to compare start to the nearest left slot end slot Boundry endpoint.

    //         //2. Find adjacent slot to the right (if no slot right of end, then skip)
    //         //expand to the next slot right (if any). If no slot to the right, skip.
    //         //need to compare end to the nearest right slot start slot Boundry endpoint.
    //     }

    //     if(contractIfoverlap){
    //         //similar if any overlap of leftSlot and rightSlot (if there is an adjacent left)
    //     }

    //     //create slot with updated left and right limits
    // }

    // public void addSlot(
    //     long start,
    //     long end
    // ){addSlot(start, end, false, false);}

    public void merge(){
        for(Slot slot : slotList){
            if(!slot.doneCoverage.isMerged()) slot.doneCoverage.merge();
        }
        isMerged = true;
    }
    // public FixedInterval removeSlot(int index){}

    //markIntervalDone(FixedInterval interval){...}

    //markSpanDone(AlignedLongSet span){...} //same as markIntervalDone

    //A slot of bounded AlignedLongSet
    private class Slot{
        private FixedLongInterval boundedInterval; //The domain, no value can exist outside the bounds of the interval.
        private AlignedLongSet doneCoverage;       //a "truthy" interval. Will never go out of bounded interval.
        // private final int maxMicroIntervals;           //number of possible microIntervals within bounded

        private Slot(long start, long end, boolean isInclusiveStart, boolean isInclusiveEnd){
            doneCoverage = new AlignedLongSet(microInterval); //Used as a tmp normalizer for snapping boundedInterval
            // doneCoverage.addInterval.
            boundedInterval = new FixedLongInterval(start, end, isInclusiveStart, isInclusiveEnd);
            
            
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