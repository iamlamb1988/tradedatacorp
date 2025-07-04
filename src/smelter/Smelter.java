/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */

package tradedatacorp.smelter;

/**
 * The purpose of this generic wrapper interface is to process data of type T.
 * The word "process", in this interface could mean:
 * - Saving or modifying a file on a system.
 * - Inserting or Updating records in a database.
 * - Making an API call to a 3rd party site with the data.
 * The resultant process will be defined by the implementing class.
 * 
 * In general, this interface is designed to make a permanent change somewhere external to its running Java process.
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface Smelter<T>{}