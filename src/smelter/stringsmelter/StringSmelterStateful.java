/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */
package tradedatacorp.smelter.stringsmelter;

import java.util.Collection;

/**
 * The purpose of this interface is to enable the creation of string representations from collections of raw data elements.
 * The format of the string is defined by the implementing class.
 * These methods are not necessarily related to {@link Object#toString()}, but may provide alternative representations.
 * This is intended for a stateful class where the data of the implementing class is not directly passed in as a parameter.
 * @param <T> the type of data element this smelter processes.
 */
public interface StringSmelterStateful<T> extends StringSmelter<T>{
    /**
     * Processes data elements to a String. The data is handled by the state of implementing class.
     * @return the string representation of collection of data.
     */
    public String smeltToString();
}