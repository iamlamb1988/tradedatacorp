/**
 * @author Bruce Lamb
 * @since 12 SEP 2025
 */
package tradedatacorp.tools.time;

import java.util.TimeZone;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MonthInterval implements TimeRange{
    private String name;
    private TimeZone timezone;
    private DateTimeFormatter timeformat;
    private final long START_UTC;
    private final long END_UTC;
    private final ZonedDateTime START;
    private final ZonedDateTime END;
    private String formattedStart;
    private String formattedEnd;

    public MonthInterval(String intervalName, TimeZone timezone, DateTimeFormatter format, long utcMillisecond){
        name = intervalName;
        this.timezone = timezone;
        timeformat = format;

        Instant tmpTimeInst = Instant.ofEpochMilli(utcMillisecond);
        ZonedDateTime tmpTime = ZonedDateTime.ofInstant(tmpTimeInst, timezone.toZoneId());

        START = ZonedDateTime.of(
            tmpTime.getYear(),
            tmpTime.getMonthValue(),
            1,
            0,
            0,
            0,
            0,
            timezone.toZoneId()
        );
        START_UTC = START.toInstant().toEpochMilli();

        END = START.plusMonths(1).minusNanos(1_000_000L); //1 month minus 1 millisecond
        END_UTC = END.toInstant().toEpochMilli();

        formattedStart = START.format(format);
        formattedEnd = END.format(format);
    }

    public MonthInterval(String intervalName, TimeZone timezone, long utcMillisecond){
        this(intervalName, timezone, DateTimeFormatter.ISO_ZONED_DATE_TIME, utcMillisecond);
    }

    public MonthInterval(String intervalName, long utcMillisecond){
        this(intervalName, TimeZone.getTimeZone("UTC"), DateTimeFormatter.ISO_ZONED_DATE_TIME, utcMillisecond);
    }

    //MonthInterval Overrides
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