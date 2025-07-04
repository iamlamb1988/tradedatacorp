/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */

package tradedatacorp.smelter;

import java.util.Collection;

/**
 * An extension of Smelter. This instance is intended to maintain data for pre-processing prior to a smelting method.
 * This will allow pre-processing steps to be applied prior to a smelting method.
 * The data will be stored in a "crucible" (This could be a Collection, primitive array, external file, or database, etc)
 * The data is not intended to be preserved once processed but that will depend on the implementing class.
 * 
 * This interface will have 2 phases:
 * 1. Store pre-processed data into a crucible.
 * 2. process the data via a smelting method.
 * 
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface SmelterStateful<T> extends Smelter<T>{
    /**
     * Adds a single data element to the crucible to be processed upon smelting.
     *
     * @param rawData the data element to add to the crucible.
     */
    public void addData(T rawData);

    /**
     * Adds multiple data elements from an array to the crucible to be processed upon smelting.
     *
     * @param rawDataArray the array of data elements to add to the crucible.
     */
    public void addData(T[] rawDataArray);

    /**
     * Adds multiple data elements from a generic collection to the crucible to be processed upon smelting.
     *
     * @param rawDataCollection the collection of data elements to add to the crucible.
     */
    public void addData(Collection<T> rawDataCollection);
}