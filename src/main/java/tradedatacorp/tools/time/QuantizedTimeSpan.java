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
    private static final String quantizedName = "QUANTIZED";
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

        dist = Math.floorMod(newInterval.START_UTC_MILLI - offsetMod, newInterval.durationMillis);
        if(dist != 0){
            if(expandLeft){ //Expand Left on the number line (subtract)
                newStart = newInterval.START_UTC_MILLI - dist;
            }else{ //Contract right on the number line (add)
                newStart = newInterval.START_UTC_MILLI + (newInterval.durationMillis - offsetMod);
            }
        } else newStart = newInterval.START_UTC_MILLI;

        dist = Math.floorMod(newInterval.END_UTC_MILLI - offsetMod, newInterval.durationMillis);
        if(dist != 0){
            if(expandRight){ //Expand Right on the number line (add)
                newEnd = newInterval.END_UTC_MILLI + (newInterval.durationMillis - offsetMod);
            }else{ //Contract left on the number line (subtract)
                newEnd = newInterval.END_UTC_MILLI - dist;
            }
        } else newEnd = newInterval.END_UTC_MILLI;

        if(newEnd <= newStart) return;

        timeSpan.add(
            new FixedInterval(
                newInterval.name,
                newStart,
                newEnd,
                newInterval.inclusiveStart,
                newInterval.inclusiveEnd
            )
        );
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