/**
 * @author Bruce Lamb
 * @since 12 SEP 2025
 */
package tradedatacorp.tools.time;

import java.util.TimeZone;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class QuarterInterval implements TimeRange{
    private String name;
    private TimeZone timezone;
    private DateTimeFormatter timeformat;
    private final long START_UTC;
    private final long END_UTC;
    private final ZonedDateTime START;
    private final ZonedDateTime END;
    private String formattedStart;
    private String formattedEnd;

    public QuarterInterval(String intervalName, TimeZone timezone, DateTimeFormatter format, long utcMillisecond){
        name = intervalName;
        this.timezone = timezone;
        timeformat = format;

        Instant tmpTimeInst = Instant.ofEpochMilli(utcMillisecond);
        ZonedDateTime tmpTime = ZonedDateTime.ofInstant(tmpTimeInst, timezone.toZoneId());

        int startYear = tmpTime.getYear();
        int startMonth = tmpTime.getMonthValue();

        if(startMonth < 4) startMonth = 1;
        else if(startMonth < 7) startMonth = 4; 
        else if(startMonth < 10) startMonth = 7;
        else startMonth = 10;

        START = ZonedDateTime.of(
            startYear,
            startMonth,
            1,
            0,
            0,
            0,
            0,
            timezone.toZoneId()
        );
        START_UTC = START.toInstant().toEpochMilli();

        END = START.plusMonths(3).minusNanos(1_000_000L); //3 months minus 1 millisecond
        END_UTC = END.toInstant().toEpochMilli();

        formattedStart = START.format(format);
        formattedEnd = END.format(format);
    }

    public QuarterInterval(String intervalName, TimeZone timezone, long utcMillisecond){
        this(intervalName, timezone, DateTimeFormatter.ISO_ZONED_DATE_TIME, utcMillisecond);
    }

    public QuarterInterval(String intervalName, long utcMillisecond){
        this(intervalName, TimeZone.getTimeZone("UTC"), DateTimeFormatter.ISO_ZONED_DATE_TIME, utcMillisecond);
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

    //QuarterInterval methods
    public void setFormat(DateTimeFormatter newFormat){
        if(newFormat == null) return;
        timeformat = newFormat;

        formattedStart = START.format(newFormat);
        formattedEnd = END.format(newFormat);
    }
}