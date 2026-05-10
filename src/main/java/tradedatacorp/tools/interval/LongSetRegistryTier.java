/**
 * @author Bruce Lamb
 * @since 10 MAY 2026
 */
package tradedatacorp.tools.interval;

import java.util.ArrayList;

/**
 * TODO: Still in progress
 * The purpose of this class is track overlaping tiers Registries.
 * Ideally, the tier with the highest merit (Registry index 0) is the final overlapping tier of completion. This is optional.
 * The higher the tier (lower the index), the more precedence it takes.
 * 
 * There will be methods that will promote coverage from lower tiers to highest possible tiers,
 * purge and remove slots from lower redundant tiers.
 * If a lower tiers have coverage that fully encapsulates a slot on an upper tier, a prmote to upper tier and perging full encapsulated lower tiers.
 * 
 * There may be methods to demote coverage but is not the original design of the class.
 */
public class LongSetRegistryTier{
    private final FixedLongInterval microInterval;
    private ArrayList<LongSetRegistry> tierList;

    private AlignedLongSet totalBoundary; //union of every slot's boundedInterval
    private AlignedLongSet totalCoverage; //union of every slot's doneCoverage

    public LongSetRegistryTier(FixedLongInterval micro, int tierSize){
        microInterval = micro;
        totalBoundary = new AlignedLongSet(micro);
        totalCoverage = new AlignedLongSet(micro);
        tierList = new ArrayList<>(tierSize);
    }

    //Order matters, highest of merit is index 0 and each tier is of smaller merit on higher indexes
    public LongSetRegistryTier(FixedLongInterval micro, LongSetRegistry... registryArray){
        this(micro, registryArray.length);
        for(LongSetRegistry r : registryArray){tierList.add(r);}
    }
    public LongSetRegistryTier(FixedLongInterval micro){this(micro, 6);}

    public int getTierCount(){return tierList.size();}

    public void clearAllCoverage(){
        for(LongSetRegistry r : tierList){r.clearAllCoverage();}
    }

    public void clearAllTiers(){
        for(LongSetRegistry r : tierList){r.clear();}
    }

    public void clear(){
        clearAllTiers();
        tierList.clear();
    }
}