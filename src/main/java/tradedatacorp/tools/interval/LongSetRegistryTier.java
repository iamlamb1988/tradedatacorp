/**
 * @author Bruce Lamb
 * @since 25 MAY 2026
 */
package tradedatacorp.tools.interval;

import java.util.ArrayList;

/**
 * TODO: Still in progress
 * The purpose of this class is track overlaping tiers Registries.
 * Ideally, the tier with the highest merit (Registry index 0) is the final overlapping tier of completion. This is optional.
 * The higher the tier (lower the index), the more precedence it takes.
 * Every amount of coverage will be in increments of the microInterval provided at construction.
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

    public int getTierSlotCount(int tierIndex){return tierList.get(tierIndex).getSlotCount();}

    public String getBoundaryIntervalString(){
        if(!isMerged) merge();
        return totalBoundary.toString();
    }

    public String getCoverageIntervalString(){
        if(!isMerged) merge();
        return totalCoverage.toString();
    }

    public String getTierCoverageIntervalString(int tierIndex){
        if(!isMerged) merge();
        return tierList.get(tierIndex).getCoverage().toString();
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
        //1. check if coverage set fully encapsulates a lower tier boundary
        //   if so, remove the slot (even if not covered)

        //should probably start with tier 0 to n - 1 to save redundant looping.
    }

    /**
     * Adds a new tier to the end of this registry (will have the lowest merit)
     * 
     */
    public void addTier(){tierList.add(new LongSetRegistry(microInterval));}

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
        FixedLongInterval endBoundary = endR.getLastSlotBoundary();

        endR.addSlot(
            new FixedLongInterval(endBoundary.end, width + endBoundary.end, isInclusiveStart, isInclusiveEnd),
            true
        );
    }

    public void appendSlotWidthToRegistry(int tierIndex, long width){
        appendSlotWidthToRegistry(tierIndex, width, true, false);
    }

    /**
     * Increases a (tier,slot) pair's coverage to complete
     * 
     * O(n) time. O(n) + O(1) <- clears coverage list then adds is's own interval 
     * @param tierIndex
     * @param slotIndex
     */
    public void completeSlotCoverage(int tierIndex, int slotIndex){
        tierList.get(tierIndex).completeSlotCoverage(slotIndex);
    }

    //This is a helper function
    private void dropCascadingCoverage(ArrayList<ArrayList<AlignedLongSet>> slottedCoverageLists, int rootTierIndex, int rootSlotIndex){
        ArrayList<AlignedLongSet> rootTierRegi = slottedCoverageLists.get(rootTierIndex);

        AlignedLongSet rootSlot = rootTierRegi.get(rootSlotIndex);
        rootTierRegi.remove(rootSlotIndex);

        // Lower Tier Check
        int nextTierIndex = rootTierIndex + 1;
        if(nextTierIndex >= rootTierIndex) return;

        ArrayList<AlignedLongSet> nextTier;
        AlignedLongSet topTierSlot;

        //remove all cascading tiers based from root level
        for(int i=nextTierIndex; i<slottedCoverageLists.size(); ++i){
            nextTier = slottedCoverageLists.get(i);

            int j=0;
            while(j<nextTier.size()){
                topTierSlot = nextTier.get(j);
                if(rootSlot.engulfs(topTierSlot)){
                    nextTier.remove(j);
                }else ++j;
            }
        }
    }

    //Helper function to toggle off elgibility for lesser tiers on addCoverage() function
    private void disableElgibility(boolean[][] elgibilityCheckList, AlignedLongSet set, int rootTierIndex){
        if(rootTierIndex > tierList.size()) return;

        //disable all slots engulfed by interval at this root tier index and all lower indexes
        for(int i=rootTierIndex; i<tierList.size(); ++i){
            LongSetRegistry tierRegistry = tierList.get(i);
            for(int j=0; j<tierRegistry.getSlotCount(); ++j){
                if(!elgibilityCheckList[i][j]) continue;
                FixedLongInterval slotInterval = tierRegistry.getSlotBoundary(j);
                if(set.engulfs(slotInterval)){
                    elgibilityCheckList[i][j] = false;
                    disableElgibility(
                        elgibilityCheckList,
                        new AlignedLongSet(microInterval, slotInterval),
                        rootTierIndex+1
                    );
                }
            }
        }
    }

    /**
     * Will add Coverage to highest possible encapsulating tier.
     * Will ignore lower tiers if covered at a higher tier.
     * Will not merge but add to totalCoverage lazily
     * Any coverage that is less than the microUnit will be dropped. Example: single point {7}
     * will be dropped due to a width of 0 which is less than the smallest possible micro unit.
     * @param intv
     */
    public void addCoverage(
        FixedLongInterval intv,
        boolean expandLeft,
        boolean expandRight,
        boolean isLeftSnapInclusive,
        boolean isRightSnapInclusive){

        //0. set up variables
        AlignedLongSet baseCoverage = new AlignedLongSet( //new coverage to be added
            totalCoverage.getSnappedInterval(
                intv,
                expandLeft,
                expandRight,
                isLeftSnapInclusive,
                isRightSnapInclusive
            )
        );


        int tierCount = tierList.size();
        int lastTierIndex = tierCount - 1;
        AlignedLongSet[] cascadingCoverageSets = new AlignedLongSet[tierList.size()]; //each tier will absorb coverage from all lower tiers
        AlignedLongSet totalNewCoverage;

        LongSetRegistry topTier = tierList.get(0);

        boolean[][] elgibilityCoverArray = new boolean[tierCount][]; //tmp variable to track elgible slots to prevent redundancy.
        //All elemets are initialized to true and parallel with <tier, slots>. Initializled at steps 1.1 and 1.2 to prevent additional loops

        //1. obtain all current coverage slots cumulatively (O(n!)) prior to adding base coverage

        //1.1 Obtain lowest tier
        {
            LongSetRegistry weakestRegistry = tierList.get(lastTierIndex);
            boolean[] weakestElgibilityArray = elgibilityCoverArray[lastTierIndex] = new boolean[weakestRegistry.getSlotCount()];
            cascadingCoverageSets[lastTierIndex] = weakestRegistry.getCoverage();
            for(int i=0; i<weakestElgibilityArray.length; ++i) weakestElgibilityArray[i] = true;
        }

        //1.2 get remaining indexes
        //Skip last tier because the weakest tier does have any lower tiers to absorb
        for(int i=tierCount-2; i>=0; --i){ 
            AlignedLongSet currentCascadingCoverage = cascadingCoverageSets[i] = new AlignedLongSet(microInterval);
            LongSetRegistry currentRegistry = tierList.get(i);
            boolean[] currentElgibilityArray = elgibilityCoverArray[i] = new boolean[currentRegistry.getSlotCount()];
            for(int j=0; j<currentElgibilityArray.length; ++j) currentElgibilityArray[j] = true;

            currentCascadingCoverage.addSet(currentRegistry.getCoverage()); //Add current level coverage
            currentCascadingCoverage.addSet(tierList.get(i+1).getCoverage()); //Add previous level cascading coverage
        }

        //2 set total coverage including base
        totalNewCoverage = new AlignedLongSet(intv);
        totalNewCoverage.addSet(baseCoverage);
        totalNewCoverage.addSet(topTier.getCoverage());

        //3. Add elgible coverage from strongest tier to weakest tier.
        //3.1 Check all slots in strongest tier
        for(int i=0; i<topTier.getSlotCount(); ++i){
            FixedLongInterval currentSlotBoundry = topTier.getSlotBoundary(i);
            if(totalNewCoverage.engulfs(currentSlotBoundry)){
                topTier.completeSlotCoverage(i);
                disableElgibility(elgibilityCoverArray, totalNewCoverage, 0);
            }
        }

        //3.2 Check all slot in remaining sub tiers
        //TODO: Continue here;

    }

    public void addCoverage(FixedLongInterval intv){
        addCoverage(intv, false, false, intv.inclusiveStart, intv.inclusiveEnd);
    }

    //TODO Will add Coverage to highest possible tier.
    //May be slower and complex
    //Will not purge nor remove any redundant coverage from lesser tiers
    public void addCoverage(AlignedLongSet set){

    }

    //TODO Add coverage to a specific tier, even if redundant upon a higher tier (promote will remove redundant coverage) (purge will remove slots that are fully covered)
    public void addCoverage(int tierIndex, FixedLongInterval intv){
        LongSetRegistry r = tierList.get(tierIndex);
        r.addCoverage(intv);
        isMerged = false;
    }

    //TODO Add coverage to a specific tier, even if redundant upon a higher tier (promote will remove redundant coverage) (purge will remove slots that are fully covered)
    public void addCoverage(int tierIndex, AlignedLongSet set){
        LongSetRegistry r = tierList.get(tierIndex);
        r.addCoverage(set);
        isMerged = false;
    }

    //TODO: Will add coverage to lowest Tier
    //Should be a fast operation that will get consolidated upon a promotion method call
    //Equivalent to addCoverage(tierList.size() -1, myNewSet)
    public void addCoverageLowTier(){

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