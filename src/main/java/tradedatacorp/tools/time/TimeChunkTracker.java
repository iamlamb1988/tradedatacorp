/**
 * @author Bruce Lamb
 * @since 12 SEP 2025
 */
package tradedatacorp.tools.time;

import java.util.Collection;
import java.util.ArrayList;

public class TimeChunkTracker implements TimeTier<TimeRange>{
    private String name;
    private TimeRange mergedComplete;
    private ArrayList<TimeRange> completedChunkList;
    private ArrayList<TimeRange> partialChunkList;

    //TimeTier<TimeRange> Overrides
    public String getName(){return name;}
    public TimeRange[] getCompletedTimeRangeArray(){return null;}
    public Collection<TimeRange> getCompletedTimeRangeCollection(){return null;}
    public TimeRange[] getGapTimeRangeArray(){return null;}
    public Collection<TimeRange> getGapTimeRangeCollection(){return null;}

    //TimeChunkTracker methods
    //NOTES:
    //This class has no impact on actual data. It ONLY tracks data by timestamp
    //Used by another class to signal a Tier level file write ...
    //- need to interact with this class directly
    //  - Set new partialChunkList
    //  - Upgrade from partial chunk to completed chunk.
    //  - Merge completed chunk into a single merged chunk
    //  - potentially need to reset chunks
    //- need to retrieve state about Tiers
}