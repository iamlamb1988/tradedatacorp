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
    private ArrayList<Long> microCount;
    private boolean isMerged;

    public QuantizedTimeSpan(FixedInterval microInterval){
        if(microInterval.durationMillis == 0)
            throw new IllegalArgumentException("QuantizedTimeSpan requires a micro interval with a duration > 0.");

        this.microInterval = microInterval;

        offsetMod = Math.floorMod(
            microInterval.START_UTC_MILLI,
            microInterval.durationMillis
        );

        timeSpan = new ArrayList<>();
        microCount = new ArrayList<>();
        isMerged = true;
    }

    public FixedInterval getMicroInterval(){return microInterval;}

    public boolean isMerged(){return isMerged;}

    public long getMicroIntervalCount(){
        if(!isMerged) mergeTimeSpan();
        long count = 0;
        for(FixedInterval i : timeSpan) count += i.durationMillis/microInterval.durationMillis;
        return count;
    }

    public ArrayList<FixedInterval> getTimeSpanIntervalList(){
        ArrayList<FixedInterval> list = new ArrayList<>(timeSpan.size());
        for(FixedInterval e : timeSpan){list.add(e);}
        return list;
    }

    public FixedInterval getTimeSpanInterval(int index){
        return timeSpan.get(index);
    }

    /**
     * Adds to the timespan. This does NOT merge the span
     * If newInterval is does not synchronize with the microInterval, it will be expanded or cut to synchronize with microInterval chunks
     */
    public void addInterval(
        FixedInterval newInterval,
        boolean expandLeft,
        boolean expandRight,
        boolean isLeftSnapInclusive,
        boolean isRightSnapInclusive
    ){
        if(newInterval.durationMillis == 0) return;
        long dist; //distance from begin or endpoint to next snap
        long newStart;
        long newEnd;
        boolean newLeftInclusive;
        boolean newRightInclusive;

        dist = Math.floorMod(newInterval.START_UTC_MILLI - offsetMod, microInterval.durationMillis);
        if(dist != 0){
            if(expandLeft){ //Expand Left on the number line (subtract)
                newStart = newInterval.START_UTC_MILLI - dist;
                newLeftInclusive = isLeftSnapInclusive;
            }else{ //Contract right on the number line (add)
                newStart = newInterval.START_UTC_MILLI + (microInterval.durationMillis - dist);
                newLeftInclusive = isLeftSnapInclusive;
            }
        } else{
            newStart = newInterval.START_UTC_MILLI;
            newLeftInclusive = newInterval.inclusiveStart; //unchanged inclusion
        }

        dist = Math.floorMod(newInterval.END_UTC_MILLI - offsetMod, microInterval.durationMillis);
        if(dist != 0){
            if(expandRight){ //Expand Right on the number line (add)
                newEnd = newInterval.END_UTC_MILLI + (microInterval.durationMillis - dist);
                newRightInclusive = isRightSnapInclusive;
            }else{ //Contract left on the number line (subtract)
                newEnd = newInterval.END_UTC_MILLI - dist;
                newRightInclusive = isRightSnapInclusive;
            }
        } else{
            newEnd = newInterval.END_UTC_MILLI;
            newRightInclusive = newInterval.inclusiveEnd; //unchanged inclusion
        }

        if(newEnd < newStart || (newEnd == newStart && !newLeftInclusive && !newRightInclusive)) return;

        FixedInterval newQuantizedInterval = new FixedInterval(
            newInterval.name,
            newStart,
            newEnd,
            newLeftInclusive,
            newRightInclusive
        );

        timeSpan.add(newQuantizedInterval);

        //post tasks
        if(timeSpan.isEmpty()){
            microCount.clear();
            isMerged = true;
            return;
        }

        if(timeSpan.size() == 1){
            microCount.add(Long.valueOf(
                (newQuantizedInterval.END_UTC_MILLI - newQuantizedInterval.START_UTC_MILLI)/microInterval.durationMillis)
            );
            isMerged = true;
            return;
        }

        isMerged = false;
    }

    /**
     * Adds to the timespan. This does NOT merge the span
     * If newInterval is does not synchronize with the microInterval, it will be expanded or cut to synchronize with microInterval chunks
     */
    public void addInterval(FixedInterval newInterval, boolean expandLeft, boolean expandRight){
        addInterval(
            newInterval,
            expandLeft,
            expandRight,
            newInterval.inclusiveStart,
            newInterval.inclusiveEnd
        );
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
                microCount.add(Long.valueOf(timeSpan.get(0).durationMillis/microInterval.durationMillis));
            }
            return;
        }
        //core implementation
        //1. merge the timespan in an ordered fashion like a number line
        //2. update the microCount to count the exact number of microInterval chunks within the timeline.
        isMerged = true;
    }
}