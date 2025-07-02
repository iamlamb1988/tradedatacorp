/**
 * @author Bruce Lamb
 * @since 01 JUL 2025
 */

package tradedatacorp.smelter;

import java.util.Collection;

/**
 * The purpose of this generic interface is to process data of type T.
 * The word "process", in this interface could mean:
 * - Saving or modifying a file on a system.
 * - Inserting or Updating records in a database.
 * - Making an API call to a 3rd party site with the data.
 * It will depend on the implementing class
 * 
 * In general, this interface is designed to make a permanent change somewhere external of it's running Java process.
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface Smelter<T>{
    /**
     * Processes a single data element.
     *
     * @param rawDataElement the data element to process
     */
    public void smelt(T rawDataElement);

     /**
     * Processes an array of data elements.
     *
     * @param rawDataArray the array of data elements to process
     */
    public void smelt(T[] rawDataArray);

    /**
     * Processes a collection of data elements.
     *
     * @param rawDataCollection the collection of data elements to process
     */
    public void smelt(Collection<T> rawDataCollection);

    /**
     * Processes a single data element.
     *
     * @param rawDataElement the data element to process
     */
    public String smeltToString(T rawDataElement);

     /**
     * Processes an array of data elements.
     *
     * @param rawDataArray the array of data elements to process
     */
    public String smeltToString(T[] rawDataArray);

    /**
     * Processes a collection of data elements.
     *
     * @param rawDataCollection the collection of data elements to process
     */
    public String smeltToString(Collection<T> rawDataCollection);
}