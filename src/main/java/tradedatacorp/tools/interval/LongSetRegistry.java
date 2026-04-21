/**
 * @author Bruce Lamb
 * @since 21 APR 2026
 */
package tradedatacorp.tools.interval;

import java.util.ArrayList;

/**
 * A generic coverage tracker for a collection of bounded time windows.
 *
 * A {@code LongSetRegistry} manages an ordered list of {@link Slot}s. Each slot represents a bounded time
 * window at a fixed tick resolution defined by {@code microInterval}. All slots share the same
 * {@code microInterval}.
 *
 * Within each slot, {@code doneTime} tracks which portions of the window have been marked as
 * covered. When {@code doneTime} forms a single contiguous span that exactly covers the slot's
 * full boundary, the slot is considered complete ({@link Slot#isSlotDone()} returns {@code true}).
 *
 * Slots never overlap. Gaps between slots are permitted. {@code totalBoundry} and
 * {@code totalCoverage} provide aggregate views across all slots.
 *
 * This class is a general-purpose time utility with no dependencies on any other layer of the
 * project. It can be used independently for any time coverage tracking need.
 *
 * Class still in development and documentation may change.
 */
public class LongSetRegistry{
    private final FixedLongInterval microInterval;
    private AlignedLongSet totalBoundry;  //the boundry across all slots
    private AlignedLongSet totalCoverage; //the "truthy" done time across all time slots
    private ArrayList<Slot> slotList;        // ordered list of time slots

    public LongSetRegistry(FixedLongInterval microInterval){
        if(microInterval.width == 0)
            throw new IllegalArgumentException("LongSetRegistry requires a micro interval with a duration > 0.");

        this.microInterval = microInterval;

        totalBoundry = new AlignedLongSet(microInterval);
        totalCoverage = new AlignedLongSet(microInterval);
        slotList = new ArrayList<>();
    }

    /**
     * Will add a time slot to the tier if available. Will flex appropriately time boundries to fit and touch adjacent slots
     */
    public void addTimeSlot(
        long startMilli,
        long endMilli,
        boolean expandIfGap,
        boolean contractIfoverlap
    ){
        if(startMilli == endMilli) return;
        Slot leftSlot = null;
        Slot rightSlot = null;

        if(expandIfGap){
            //1. Find adjacent slot to the left (if no slot left of startMilli, then skip)
            //expand to the next time left (if any). If no slot to the left, skip.
            //need to compare startMilli to the nearest left slot endMilli slot Boundry endpoint.

            //2. Find adjacent slot to the right (if no slot right of endMilli, then skip)
            //expand to the next time right (if any). If no slot to the right, skip.
            //need to compare endMilli to the nearest right slot startMilli slot Boundry endpoint.
        }

        if(contractIfoverlap){
            //similar if any overlap of leftSlot and rightSlot (if there is an adjacent left)
        }

        //create slot with updated left and right limits
    }

    public void addTimeSlot(
        long startMilli,
        long endMilli
    ){addTimeSlot(startMilli, endMilli, true, true);}

    public void addSlotToBeginning(long time_dt){}

    public void addSlotToEnd(long time_dt){}

    // public FixedInterval removeSlot(int index){}

    //markIntervalDone(FixedInterval interval){...}

    //markTimeSpanDone(AlignedLongSet span){...} //same as markIntervalDone

    //A slot of bounded AlignedLongSet
    private class Slot{
        private FixedLongInterval boundedTimeInterval; //The domain, no time can exist outside the bounds of the interval.
        private AlignedLongSet doneTime;        //a "truthy" interval. Will never go out of bounded time interval.
        private Slot(long startMilli, long endMilli){
            boundedTimeInterval = new FixedLongInterval(startMilli, endMilli);
            doneTime = new AlignedLongSet(microInterval);
        }

        public boolean isSlotDone(){
            return
                doneTime.getIntervalSegmentCount() == 1 &&
                FixedLongInterval.equals(boundedTimeInterval, doneTime.getInterval(0));
        }
    }
}