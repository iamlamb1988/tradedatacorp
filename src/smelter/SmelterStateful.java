/**
 * @author Bruce Lamb
 * @since 28 FEB 2025
 */

package tradedatacorp.smelter;

import java.util.Collection;

/**
 * An extension of Smelter. This instance is intended to maintain data for pre-processing prior to smelting.
 * This will allow pre-processing steps to be applied prior to smelt.
 * The data will be stored in a "crucible" (This could be a Collection, primitive array, external file, or database, etc)
 * The data is not intended to be preserved once processed but that will depend on the implementing class.
 * 
 * This interface will have 2 phases:
 * 1. Store pre-processed data into a crucible:
 * 2. prcess the data via smelt().
 * 
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface SmelterStateful<T> extends Smelter<T>{
    /**
     * Add a singular data element to the crucible that will processed upon smelt.
     * @param rawData A data element to be added to crucible.
     */
    public void addData(T rawData);

    /**
     * Add a many data elements from an array to the crucible that will processed upon smelt.
     * @param rawData The set of data elements to be added to crucible.
     */
    public void addData(T[] rawData);

    /**
     * Add a many data elements from a generic collection to the crucible that will processed upon smelt.
     * @param rawData
     */
    public void addData(Collection<T> rawData);

    /**
     * Process all data elements within the crucible.
     */
    public void smelt();
}