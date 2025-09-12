/**
 * @author Bruce Lamb
 * @since 12 SEP 2025
 */
package tradedatacorp.tools.time;

import java.util.TimeZone;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public interface TimeRange{
    public String getName();
    public TimeZone getTimeZone();
    public DateTimeFormatter getTimeFormatter();
    public long getStartUTC();
    public long getEndUTC();
    public ZonedDateTime getStartTime();
    public ZonedDateTime getEndTime();
    public String getFormattedStartTime();
    public String getFormattedEndTime();
}