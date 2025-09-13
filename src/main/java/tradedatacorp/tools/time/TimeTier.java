/**
 * @author Bruce Lamb
 * @since 12 SEP 2025
 */
package tradedatacorp.tools.time;

import java.util.Collection;

public interface TimeTier<T extends TimeRange>{
    public String getName();
    public T[] getCompletedTimeRangeArray();
    public Collection<T> getCompletedTimeRangeCollection();
    public T[] getGapTimeRangeArray();
    public Collection<T> getGapTimeRangeCollection();
}