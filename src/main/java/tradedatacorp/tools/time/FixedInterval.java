/**
 * @author Bruce Lamb
 * @since 12 SEP 2025
 */
package tradedatacorp.tools.time;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class FixedInterval implements TimeRange{
    private String name;
    private TimeZone timezone;
    private DateTimeFormatter timeformat;
    private final long START_UTC;
    private final long END_UTC;
    private final ZonedDateTime START;
    private final ZonedDateTime END;
    private String formattedStart;
    private String formattedEnd;

    public FixedInterval(String intervalName, TimeZone timezone, DateTimeFormatter format, long utcStartMillisecond, long utcEndMillisecond){
        name = intervalName;
        this.timezone = timezone;
        timeformat = format;
        START_UTC = utcStartMillisecond;
        END_UTC = utcEndMillisecond;
        START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(START_UTC), timezone.toZoneId());
        END = ZonedDateTime.ofInstant(Instant.ofEpochMilli(END_UTC), timezone.toZoneId());
        formattedStart = START.format(format);
        formattedEnd = END.format(format);
    }

    //TimeRange Overrides
    @Override
    public String getName(){return name;}

    @Override
    public TimeZone getTimeZone(){return timezone;}

    @Override
    public DateTimeFormatter getTimeFormatter(){return timeformat;}

    @Override
    public long getStartUTC(){return START_UTC;}

    @Override
    public long getEndUTC(){return END_UTC;}

    @Override
    public ZonedDateTime getStartTime(){return START;}

    @Override
    public ZonedDateTime getEndTime(){return END;}

    @Override
    public String getFormattedStartTime(){return formattedStart;}

    @Override
    public String getFormattedEndTime(){return formattedEnd;}
}