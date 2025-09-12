/**
 * @author Bruce Lamb
 * @since 11 SEP 2025
 */
package tradedatacorp.tools.time;

import java.time.ZonedDateTime;
import java.util.GregorianCalendar;

public class MonthInterval implements TimeRange{
    private String name;
    private final long START_UTC;
    private final long END_UTC;

    public MonthInterval(String intervalName, long utcMillisecond){
        name = intervalName;

        GregorianCalendar tmpCalendar = new GregorianCalendar();
        tmpCalendar.setTimeInMillis(utcMillisecond);

        ZonedDateTime tmpTime = tmpCalendar.toZonedDateTime();

        tmpCalendar = new GregorianCalendar(tmpTime.getYear(), tmpTime.getMonthValue() - 1, 1);
        tmpTime = tmpCalendar.toZonedDateTime();

        START_UTC = tmpCalendar.getTimeInMillis();

        tmpTime = tmpTime.plusMonths(1);
        tmpTime = tmpTime.minusNanos(1_000_000L); //Minus 1 millisecond
        END_UTC = GregorianCalendar.from(tmpTime).getTimeInMillis();

        
    }

    @Override
    public String getName(){return name;}

    @Override
    public long getStartUTC(){return START_UTC;}

    @Override
    public long getEndUTC(){return END_UTC;}
}