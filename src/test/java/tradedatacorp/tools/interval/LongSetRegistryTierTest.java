/**
 * @author Bruce Lamb
 * @since 12 MAY 2026
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
}