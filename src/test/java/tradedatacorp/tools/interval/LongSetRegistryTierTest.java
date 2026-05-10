/**
 * @author Bruce Lamb
 * @since 10 MAY 2026
 */
package tradedatacorp.tools.interval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import static tradedatacorp.tools.interval.FixedLongInterval.getNormalizedInterval;

public class LongSetRegistryTierTest{
    private static FixedLongInterval[] exampleIntervalArray1; //used to construct Tiers

    @BeforeAll
    public static void setup(){
        exampleIntervalArray1 = new FixedLongInterval[]{
            getNormalizedInterval(new FixedLongInterval(0, 25)),
            getNormalizedInterval(new FixedLongInterval(0, 5)),
            getNormalizedInterval(new FixedLongInterval(0, 1))
        };
    }

    @Test
    public void constructorTest(){}
}