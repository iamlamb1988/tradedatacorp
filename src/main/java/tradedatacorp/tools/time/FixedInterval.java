/**
 * @author Bruce Lamb
 * @since 11 SEP 2025
 */
package tradedatacorp.tools.time;

public class FixedInterval implements TimeRange{
    private String name;
    private final long START_UTC;
    private final long END_UTC;

    public FixedInterval(String intervalName, long utcStartMillisecond, long utcEndMillisecond){
        name = intervalName;
        START_UTC = utcStartMillisecond;
        END_UTC = utcEndMillisecond;
    }

    //TimeRange Overrides
    @Override
    public String getName(){return name;}

    @Override
    public long getStartUTC(){return START_UTC;}

    @Override
    public long getEndUTC(){return END_UTC;}
}