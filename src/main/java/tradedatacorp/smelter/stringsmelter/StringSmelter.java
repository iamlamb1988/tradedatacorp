/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */
package tradedatacorp.smelter.stringsmelter;

import java.util.Collection;

/**
 * Enables the creation of string representations of raw data elements.
 * Implementing classes are typically stateless, but other contexts are possible.
 * The string format is defined by the implementing class.
 * These methods are not necessarily related to {@link Object#toString()}, but may provide alternative representations.
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface StringSmelter<T>{
    /**
     * Processes a single data element to a String.
     *
     * @param rawDataElement the data element to process
     * @return The string representation of a collection containing a single element.
     */
    public String smeltToString(T rawDataElement);

    /**
     * Processes an array of data elements to a String.
     *
     * @param rawDataArray the array of data elements to process
     * @return The string representation of an array of elements.
     */
    public String smeltToString(T[] rawDataArray);

    /**
     * Processes a collection of data elements to a String.
     *
     * @param rawDataCollection the collection of data elements to process
     * @return The string representation of a collection of elements.
     */
    public String smeltToString(Collection<T> rawDataCollection);
}