/**
 * @author Bruce Lamb
 * @since 25 MAY 2026
 */
package tradedatacorp.tools.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static tradedatacorp.tools.interval.FixedLongInterval.getNormalizedInterval;

public class LongSetRegistryTierTest{
    private static FixedLongInterval smallestBase;
    private static FixedLongInterval[] exampleIntervalArray1; //used to construct Tiers

    @BeforeAll
    public static void setup(){
        smallestBase = new FixedLongInterval(0, 1);
        exampleIntervalArray1 = new FixedLongInterval[]{
            getNormalizedInterval(new FixedLongInterval(0, 25)),
            getNormalizedInterval(new FixedLongInterval(0, 5)),
            getNormalizedInterval(new FixedLongInterval(0, 1))
        };
    }

    private LongSetRegistryTier genThreeTierExample(){
        LongSetRegistryTier tierRegi = new LongSetRegistryTier(smallestBase);
        tierRegi.addTier();
        tierRegi.addTier();
        tierRegi.addTier();

        //add boundries [0, 12) in 4 even slots (width = 3) to top tier
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(0, 3));
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(3, 6));
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(6, 9));
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(9, 12));

        //add boundries [0, 12) in 6 even slots (width = 2) to middle tier
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(0, 2));
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(2, 4));
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(4, 6));
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(6, 8));
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(8, 10));
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(10, 12));

        //add boundries [0, 12) in 12 1 wide slots at the lowest tier
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(0, 1));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(1, 2));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(2, 3));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(3, 4));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(4, 5));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(5, 6));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(6, 7));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(7, 8));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(8, 9));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(9, 10));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(10, 11));
        tierRegi.addSlotToRegistry(2, new FixedLongInterval(11, 12));

        return tierRegi;
    }

    @Test
    public void constructorTest1(){
        LongSetRegistryTier tierRegi = new LongSetRegistryTier(smallestBase);

        assertEquals(0, tierRegi.getTierCount());
        assertEquals(smallestBase, tierRegi.getMicroInterval());
        assertEquals("{}", tierRegi.getBoundaryIntervalString());
        assertEquals("{}", tierRegi.getCoverageIntervalString());
    }

    @Test
    public void constructorTest2(){
        LongSetRegistryTier tierRegi = new LongSetRegistryTier(smallestBase, exampleIntervalArray1);

        assertEquals(3, tierRegi.getTierCount());
        assertEquals(smallestBase, tierRegi.getMicroInterval());
        assertEquals("[0,25)", tierRegi.getBoundaryIntervalString());
        assertEquals("{}", tierRegi.getCoverageIntervalString());

        assertEquals("[0,25)",tierRegi.getTierBoundaryIntervalString(0));
        assertEquals("[0,5)",tierRegi.getTierBoundaryIntervalString(1));
        assertEquals("[0,1)",tierRegi.getTierBoundaryIntervalString(2));
    }

    @Test
    public void testThreeTierNoCoverageExample(){
        LongSetRegistryTier example = genThreeTierExample();

        assertEquals("[0,12)", example.getBoundaryIntervalString());;
        assertEquals("[0,12)", example.getTierBoundaryIntervalString(0));
        assertEquals("[0,12)", example.getTierBoundaryIntervalString(1));
        assertEquals("[0,12)", example.getTierBoundaryIntervalString(2));

        assertEquals("{}", example.getCoverageIntervalString());
        assertEquals("{}", example.getTierCoverageIntervalString(0));
        assertEquals("{}", example.getTierCoverageIntervalString(1));
        assertEquals("{}", example.getTierCoverageIntervalString(2));
    }

    @Test
    public void addSlotsToTier1(){
        LongSetRegistryTier tierRegi = new LongSetRegistryTier(smallestBase, 5); //creates a collection size of 5 elements

        assertEquals(0, tierRegi.getTierCount());
        assertEquals(smallestBase, tierRegi.getMicroInterval());
        assertEquals("{}", tierRegi.getBoundaryIntervalString());
        assertEquals("{}", tierRegi.getCoverageIntervalString());

        //Add top master tier
        tierRegi.addTier();
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(0, 10));
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(10, 20));
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(20, 30));

        assertEquals(1, tierRegi.getTierCount());

        assertEquals(3, tierRegi.getTierSlotCount(0));
        assertEquals("[0,30)", tierRegi.getTierBoundaryIntervalString(0));
        assertEquals("[0,10)", tierRegi.getTierSlotBoundaryIntervalString(0,0));
        assertEquals("[10,20)", tierRegi.getTierSlotBoundaryIntervalString(0,1));
        assertEquals("[20,30)", tierRegi.getTierSlotBoundaryIntervalString(0,2));
    }

    @Test
    public void addSlotToTier2(){
        LongSetRegistryTier tierRegi = new LongSetRegistryTier(smallestBase);

        assertEquals(0, tierRegi.getTierCount());
        assertEquals(smallestBase, tierRegi.getMicroInterval());
        assertEquals("{}", tierRegi.getBoundaryIntervalString());
        assertEquals("{}", tierRegi.getCoverageIntervalString());

        //Add top master tier
        tierRegi.addTier();
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(0, 10));
        tierRegi.addSlotToRegistry(0, new FixedLongInterval(10, 20));

        assertEquals(1, tierRegi.getTierCount());

        assertEquals(2, tierRegi.getTierSlotCount(0));
        assertEquals("[0,20)", tierRegi.getTierBoundaryIntervalString(0));
        assertEquals("[0,10)", tierRegi.getTierSlotBoundaryIntervalString(0,0));
        assertEquals("[10,20)", tierRegi.getTierSlotBoundaryIntervalString(0,1));

        //Add 2nd tier
        tierRegi.addTier();
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(0, 4));
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(4, 8));
        tierRegi.addSlotToRegistry(1, new FixedLongInterval(8, 12));

        assertEquals(2, tierRegi.getTierCount());

        assertEquals(3, tierRegi.getTierSlotCount(1));
        assertEquals("[0,12)", tierRegi.getTierBoundaryIntervalString(1));
        assertEquals("[0,4)", tierRegi.getTierSlotBoundaryIntervalString(1,0));
        assertEquals("[4,8)", tierRegi.getTierSlotBoundaryIntervalString(1,1));
        assertEquals("[8,12)", tierRegi.getTierSlotBoundaryIntervalString(1,2));

        assertEquals("{}", tierRegi.getCoverageIntervalString());
    }

    @Test
    public void addCoverageToSlotTier1(){
        LongSetRegistryTier tierRegi = genThreeTierExample();

        //Add coverage
        tierRegi.addCoverage(new FixedLongInterval(2, 7, true, true));

        //Check coverage
        // total coverage will be [2,7]
        // For each individual tier ONLY fully covered slots at the highest tier possible tier will be added.
        assertEquals("[2,7)", tierRegi.getCoverageIntervalString());//NOTE: the drop, the 7th slot is [6,7), the inclusive endings will match the slot coverage.
        assertEquals("[3,6)", tierRegi.getTierCoverageIntervalString(0));
        assertEquals("[2,4)", tierRegi.getTierCoverageIntervalString(1));
        assertEquals("[6,7)", tierRegi.getTierCoverageIntervalString(2));
    }

    @Test
    public void addCoverageToSlotTier2(){
        LongSetRegistryTier tierRegi = genThreeTierExample();
        //Add specific coverage to mid tier
        tierRegi.addCoverage(1, new FixedLongInterval(7, 7, true, true));
        //This {7} will fail to add because it's width (0) is less than it's microInterval

        assertEquals("{}", tierRegi.getTierCoverageIntervalString(0));
        assertEquals("{}", tierRegi.getTierCoverageIntervalString(1));
        assertEquals("{}", tierRegi.getTierCoverageIntervalString(2));
    
        //Add coverage
        tierRegi.addCoverage(new FixedLongInterval(2, 7, true, true));

        //Check coverage
        // total coverage will be [2,7]
        // For each individual tier ONLY fully covered slots at the highest tier possible tier will be added.
        assertEquals("[2,7)", tierRegi.getCoverageIntervalString()); //NOTE: the drop, the 7th slot is [6,7), the inclusive endings will match the slot coverage.
        assertEquals("[3,6)", tierRegi.getTierCoverageIntervalString(0));
        assertEquals("[2,4)", tierRegi.getTierCoverageIntervalString(1));
        assertEquals("[6,7)", tierRegi.getTierCoverageIntervalString(2));
    }
}