/**
 * @author Bruce Lamb
 * @since 03 JUL 2025
 */

package tradedatacorp.smelter.stringsmelter;

import java.util.Collection;

/**
 * The purpose of this interface is to enable the creation of string representations from collections of raw data elements.
 * The format of the string will be defined by the implementing class.
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface StringSmelterStateful<T> extends StringSmelter<T>{
    /**
     * Processes data elements to a String. The data is handled by the state of implementing class.
     * @return the string representation of collection of data.
     * NOTE: Not necessarily the .toString() Object method.
     */
    public String smeltToString();
}