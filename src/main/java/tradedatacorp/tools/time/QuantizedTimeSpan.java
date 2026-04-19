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
    private FixedInterval microInterval;
    private long offsetMod; //the floor modulus to handle the offset of times
    private ArrayList<FixedInterval> timeSpan;
    private ArrayList<Integer> microCount;
    private boolean isMerged;

    public QuantizedTimeSpan(FixedInterval microInterval){
        offsetMod = Math.floorMod(
            microInterval.START_UTC_MILLI,
            microInterval.durationMillis
        );

        this.microInterval = new FixedInterval(
            microRef,
            offsetMod,
            offsetMod + microInterval.durationMillis,
            microInterval.inclusiveStart,
            microInterval.inclusiveEnd
        );

        timeSpan = new ArrayList<>();
        microCount = new ArrayList<>();
        isMerged = true;
    }

    /**
     * Adds to the timespan. This does NOT merge the span
     * If newInterval is does not synchronize with the microInterval, it will be expanded or cut to synchronize with microInterval chunks
     */
    public void addInterval(FixedInterval newInterval, boolean expandLeft, boolean expandRight){
        long dist; //distance from begin or endpoint to next snap
        long newStart;
        long newEnd;

        dist = newInterval.START_UTC_MILLI % offsetMod;
        if(dist != 0){
            if(expandLeft){
                //expand left (reduce the start until the next snap)
            }else{ //chop off (increase the start into the next snap)

            }
        }

        dist = newInterval.END_UTC_MILLI % offsetMod;
        if(dist != 0){
            if(expandRight){
                //expand right (increase the start into the next snap)
            }else{ //chop off (decrease the start into the next snap)

            }
        }
        isMerged = false;
    }

    /**
     * Will consolidate
     */
    public void mergeTimeSpan(){
        if(timeSpan.size() <= 1){
            isMerged = true;
            if(timeSpan.size() == 0) microCount.clear();
            else if(timeSpan.size() == 1){
                microCount.clear();
                microCount.add(Integer.valueOf((int)(timeSpan.get(0).durationMillis/microInterval.durationMillis)));
            }
            return;
        }
        //core implementation
        //1. merge the timespan in an ordered fashion like a number line
        //2. update the microCount to count the exact number of microInterval chunks within the timeline.
        isMerged = true;
    }
}