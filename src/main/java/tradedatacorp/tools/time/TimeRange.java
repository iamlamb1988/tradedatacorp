/**
 * @author Bruce Lamb
 * @since 11 SEP 2025
 */
package tradedatacorp.tools.time;

public interface TimeRange{
    public String getName();
    public long getStartUTC();
    public long getEndUTC();
}