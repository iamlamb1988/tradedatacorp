/**
 * @author Bruce Lamb
 * @since 8 AUG 2024
 */

package tradedatacorp.smelter;

/**
 * This interface:
 * - Put data into a standby collection of type RefinedT.
 * - Permantly place standby data into permanet location.
 */
public interface Smelter<RawT, RefinedT, ParamT>{
    /**
     * Add raw data that be processed into RefinedT data and added to the standby collection.
     * @param rawData
     * The raw data that will be transformed into RefinedT data to be added to standby collection.
     */
    public void addRawData(RawT rawData);

    /**
     * Add refined data to the standby collection.
     * @param refinedData
     */
    public void addRefinedData(RefinedT refinedData);

    /*
     * Empties the StandBy collection.
     */
    public void clearStandBy();

    /**
     * Permanently place any standby data to it's final destination or form
     */
    public void smelt();

    /**
     * Removes data from standby collection and returns the RefinedT
     */
    public RefinedT fetch(ParamT criteria);
}