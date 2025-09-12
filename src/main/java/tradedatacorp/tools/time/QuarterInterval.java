/**
 * @author Bruce Lamb
 * @since 11 SEP 2025
 */
package tradedatacorp.tools.time;

import java.util.GregorianCalendar;
import java.time.ZonedDateTime;

public class QuarterInterval implements TimeRange{
    private String name;
    private final long START_UTC;
    private final long END_UTC;

    public QuarterInterval(String intervalName, long utcMillisecond){
        name = intervalName;

        GregorianCalendar tmpCalendar = new GregorianCalendar();
        tmpCalendar.setTimeInMillis(utcMillisecond);

        ZonedDateTime tmpTime = tmpCalendar.toZonedDateTime();

        int startMonth = tmpTime.getMonthValue() - 1;

        //Gregorian Index 0 == JAN
        if(startMonth < 3) startMonth = 0;      // 0 -> JAN, 1 -> FEB, 2 -> MAR
        else if(startMonth < 6) startMonth = 3; // 3 -> APR, 4 -> MAY, 5 -> JUN
        else if(startMonth < 9) startMonth = 6; // 6 -> JUL, 7 -> AUG, 8 -> SEP
        else startMonth = 9;                    // 9 -> OCT, 10 -> NOV, 11 -> DEC

        tmpCalendar = new GregorianCalendar(tmpTime.getYear(), startMonth, 1);
        tmpTime = tmpCalendar.toZonedDateTime();

        START_UTC = tmpCalendar.getTimeInMillis();

        tmpTime = tmpTime.plusMonths(3);
        tmpTime = tmpTime.minusNanos(1_000_000L); //Minus 1 millisecond
        END_UTC = GregorianCalendar.from(tmpTime).getTimeInMillis();
    }

    //TimeRange Overrides
    @Override
    public String getName(){return name;}

    @Override
    public long getStartUTC(){return START_UTC;}

    @Override
    public long getEndUTC(){return END_UTC;}
}