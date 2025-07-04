/**
 * @author Bruce Lamb
 * @since 03 JUL 2025
 */

package tradedatacorp.smelter.stringsmelter;

import java.util.Collection;

/**
 * The purpose of this interface is to enable the creation of string representations from collections of raw data elements.
 * These methods are primarily intended for stateless implementing classes, but they can be used in other contexts.
 * The format of the string will be defined by the implementing class.
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface StringSmelter<T>{
    /**
     * Processes a single data element to a String.
     *
     * @param rawDataElement the data element to process
     */
    public String smeltToString(T rawDataElement);

    /**
     * Processes an array of data elements to a String.
     *
     * @param rawDataArray the array of data elements to process
     */
    public String smeltToString(T[] rawDataArray);

    /**
     * Processes a collection of data elements to a String.
     *
     * @param rawDataCollection the collection of data elements to process
     */
    public String smeltToString(Collection<T> rawDataCollection);
}