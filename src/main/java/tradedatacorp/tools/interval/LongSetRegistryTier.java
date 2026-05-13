/**
 * @author Bruce Lamb
 * @since 12 MAY 2026
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

    //"merged" state fields
    private AlignedLongSet totalBoundary; //union of every slot's boundedInterval
    private AlignedLongSet totalCoverage; //union of every slot's doneCoverage
    public boolean isMerged;

    public LongSetRegistryTier(FixedLongInterval micro){this(micro, 6);}

    public LongSetRegistryTier(FixedLongInterval micro, int tierSize){
        microInterval = micro;
        totalBoundary = new AlignedLongSet(micro);
        totalCoverage = new AlignedLongSet(micro);
        tierList = new ArrayList<>(tierSize);
        isMerged = true;
    }

    //Order matters, highest of merit is index 0 and each tier is of smaller merit on higher indexes
    public LongSetRegistryTier(FixedLongInterval micro, LongSetRegistry... registryArray){
        this(micro, registryArray.length);
        for(LongSetRegistry r : registryArray){tierList.add(r);}
    }

    /**
     * Constructs a Tiered registry and adds single slot to each tier.
     * This will merge immediately. This is the only non-lazy merge.
     * 
     * @param micro
     * @param intvArray
     */
    public LongSetRegistryTier(FixedLongInterval micro, FixedLongInterval... intvArray){
        this(micro, intvArray.length);
        LongSetRegistry[] rArr = new LongSetRegistry[intvArray.length];
        for(int i=0; i<intvArray.length; ++i){
            LongSetRegistry regi =rArr[i];
            regi = new LongSetRegistry(micro);
            regi.addSlot(intvArray[i]);
            tierList.add(regi);
            regi.merge();
            totalBoundary.addInterval(intvArray[i]);
        }
        totalBoundary.merge();
    }

    public FixedLongInterval getMicroInterval(){return microInterval;}

    public int getTierCount(){return tierList.size();}

    public String getBoundaryIntervalString(){
        if(!isMerged) merge();
        return totalBoundary.toString();
    }

    public String getCoverageIntervalString(){
        if(!isMerged) merge();
        return totalCoverage.toString();
    }

    public String getTierBoundaryIntervalString(int tierIndex){
        if(!isMerged) merge();
        return tierList.get(tierIndex).getBoundary().toString();
    }

    public String getTierSlotBoundaryIntervalString(int tierIndex, int slotIndex){
        if(!isMerged) merge();
        return tierList.get(tierIndex).getSlotBoundIntervalString(slotIndex);
    }

    public void promoteAll(){
        //TODO: if any tier is potentially covered but any lower tier, it get's promoted.
        //Will not purge or remove any redundancy upon coverage increase.

        //1. start with 2nd to lowest tier (index tierList.size() - 2)
        //2. Grab any fully encapsulated increase from each tier (O( (n - 1)! )) //will need to keep checking due to possible unique offsets of each tier
        //3. 
    }

    /**
     * will remove lower tiered slots if fully covered by any higher tier.
     * 
     */
    public void purgeRedundantCoverage(){
        //TODO: 
        //1. check if coverage set fully encapsulates a lower tier boundry
        //   if so, remove the slot (even if not covered)

        //should probably start with tier 0 to n - 1 to save redundant looping.
    }

    /**
     * Adds a new tier to the end of this registry (will have the lowest merit)
     * 
     */
    public void addTier(){
        tierList.add(new LongSetRegistry(microInterval));
    }

    public void addTier(int newIndex){
        tierList.add(newIndex, new LongSetRegistry(microInterval));
    }

    public void addSlotToRegistry(int tierIndex, FixedLongInterval slotIntv){
        tierList.get(tierIndex).addSlot(slotIntv);
        isMerged = false;
    }

    public void appendSlotWidthToRegistry(int tierIndex, long width, boolean isInclusiveStart, boolean isInclusiveEnd){
        if(tierList.size() == 0) return; //Should this throw an error?

        LongSetRegistry endR = tierList.get(tierList.size() - 1);
        FixedLongInterval endBoundry = endR.getLastSlotBoundary();

        endR.addSlot(
            new FixedLongInterval(endBoundry.end, width + endBoundry.end, isInclusiveStart, isInclusiveEnd),
            true
        );
    }

    public void appendSlotWidthToRegistry(int tierIndex, long width){
        appendSlotWidthToRegistry(tierIndex, width, true, false);
    }

    public void merge(){
        for(LongSetRegistry r : tierList){
            r.merge();
            totalBoundary.addSet(r.getBoundary());
            totalBoundary.merge();

            totalCoverage.addSet(r.getCoverage());
            totalCoverage.merge();
        }
        isMerged = true;
    }

    /**
     * Clears all coverage to an empty set on all tiers
     */
    public void clearAllCoverage(){
        for(LongSetRegistry r : tierList){r.clearAllCoverage();} //necessary? (garbage collectable?)
        tierList.clear();
    }

    /**
     * Removes all registries from tiers
     */
    public void clearAllTiers(){
        for(LongSetRegistry r : tierList){r.clear();}
    }

    public void clear(){
        clearAllTiers();
        tierList.clear();
    }
}