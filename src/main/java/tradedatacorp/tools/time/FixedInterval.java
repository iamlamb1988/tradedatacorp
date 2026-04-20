/**
 * @author Bruce Lamb
 * @since 18 APR 2026
 */
package tradedatacorp.tools.time;

/**
 * Represents a high-precision time interval on the UTC timeline using Unix epoch milliseconds.
 * This accounts for the length of time and offsets.
 */
public class FixedInterval{
    public final String name;
    public final long START_UTC_MILLI;
    public final long END_UTC_MILLI;
    public final long durationMillis;
    public final boolean inclusiveStart;
    public final boolean inclusiveEnd;

    public FixedInterval(
        String intervalName,
        long utcStartMilli,
        long utcEndMilli,
        boolean isInclusiveStart,
        boolean isInclusiveEnd
    ){
        name = intervalName;
        if(utcStartMilli <= utcEndMilli){
            START_UTC_MILLI = utcStartMilli;
            END_UTC_MILLI = utcEndMilli;
            inclusiveStart = isInclusiveStart;
            inclusiveEnd = isInclusiveEnd;
        }else{ //invert
            END_UTC_MILLI = utcStartMilli;
            START_UTC_MILLI = utcEndMilli;
            inclusiveStart = isInclusiveEnd;
            inclusiveEnd = isInclusiveStart;
        }

        durationMillis = END_UTC_MILLI - START_UTC_MILLI;
    }

    public FixedInterval(
        String intervalName,
        long utcStartMilli,
        long utcEndMilli
    ){this(intervalName, utcStartMilli, utcEndMilli, true, false);}

    public String getName(){return name;}

    public long getStartUTC(){return START_UTC_MILLI;}

    public long getEndUTC(){return END_UTC_MILLI;}

    public long getIntervalMilli(){return durationMillis;}

    public long getIntervalSec(){return durationMillis/1000;}

    public boolean isUTCmilliWithinInterval(long utcMilli, boolean isInclusiveStart, boolean isInclusiveEnd){
        if(utcMilli > START_UTC_MILLI && utcMilli < END_UTC_MILLI) return true;
        if(isInclusiveStart && utcMilli == START_UTC_MILLI) return true;
        if(isInclusiveEnd && utcMilli == END_UTC_MILLI) return true;
        return false;
    }

    public boolean isUTCmilliWithinInterval(long utcMilli){return isUTCmilliWithinInterval(utcMilli, inclusiveStart, inclusiveEnd);}

    public boolean isUTCsecWithinInterval(long utcSec, boolean isInclusiveStart, boolean isInclusiveEnd){
        return isUTCmilliWithinInterval(utcSec * 1000, isInclusiveStart, isInclusiveEnd);
    }

    public boolean isUTCsecWithinInterval(long utcSec){
        return isUTCmilliWithinInterval(utcSec * 1000, inclusiveStart, inclusiveEnd);
    }

    public static boolean isEqual(FixedInterval int1, FixedInterval int2){
        return
            int1.START_UTC_MILLI == int2.START_UTC_MILLI &&
            int1.END_UTC_MILLI == int2.END_UTC_MILLI &&
            int1.inclusiveStart == int2.inclusiveStart &&
            int1.inclusiveEnd == int2.inclusiveEnd;
    }
}