/**
 * @author Bruce Lamb
 * @since 20 APR 2026
 */
package tradedatacorp.tools.time;

import java.util.ArrayList;

/**
 * A time range tracker containing a list of FixedIntervals designed for checking continuous coverage.
 * This is a quantitative containing many slots of time.
 * 
 * Each slot has a continuos bounded time interval.
 * No slot will overlap boundries with another slot but gaps can exist between slots.
 * Each slot share the same microInterval time.
 * Each slot will have a completed time interval which represents a "truthy" interval within the bounded slot.
 * 
 * Class still in development and documentation may change.
 */
public class TimeTier{
    private final FixedInterval microInterval;
    private QuantizedTimeSpan totalBoundry;  //the boundry across all slots
    private QuantizedTimeSpan totalCoverage; //the "truthy" done time across all time slots
    private ArrayList<Slot> slotList;        // ordered list of time slots

    public TimeTier(FixedInterval microInterval){
        if(microInterval.durationMillis == 0)
            throw new IllegalArgumentException("TimeTier requires a micro interval with a duration > 0.");

        this.microInterval = microInterval;

        totalBoundry = new QuantizedTimeSpan(microInterval);
        totalCoverage = new QuantizedTimeSpan(microInterval);
        slotList = new ArrayList<>();
    }

    //addTimeSlot(startMilli, endMilli, boolean expandIfGap, boolean contractIfoverlap){ .. }
    //removeSlot(int index){...}
    //addTimeSlot(startMilli, endMilli){
        //1. check if overlaping other time slots
        //2. contract if overlap
        //3. if dist is 0, do not add
    // }

    //markIntervalDone(FixedInterval interval){...}

    //markTimeSpanDone(QuantizedTimeSpan span){...} //same as markIntervalDone

    //A slot of bounded time
    private class Slot{
        private FixedInterval boundedTimeInterval; //The domain, no time can exist outside the bounds of the interval.
        private QuantizedTimeSpan doneTime;        //a "truthy" interval. Will never go out of bounded time interval.
        private Slot(long startMilli, long endMilli){
            boundedTimeInterval = new FixedInterval(startMilli, endMilli);
            doneTime = new QuantizedTimeSpan(microInterval);
        }

        public boolean isSlotDone(){
            return
                doneTime.getIntervalSegmentCount() == 1 &&
                FixedInterval.isEqual(boundedTimeInterval, doneTime.getTimeSpanInterval(0));
        }
    }
}