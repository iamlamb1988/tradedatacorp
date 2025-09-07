/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */
package tradedatacorp.smelter.filesmelter;

import java.nio.file.Path;
import java.util.Collection;

/**
 * The purpose of this interface is to enable a stateful file writer that manages both data and file location internally.
 * @param <T> the type of data element this smelter processes.
 */
public interface FileSmelterStateful<T> extends FileSmelter<T>{
    /**
     * Saves the single data element to a specified location. The file location is handled by the state of implementing class.
     *
     * @param rawDataElement the data element to process
     */
    public void smeltToFile(T rawDataElement);

    /**
     * Saves the array of data to a specified location. The file location is handled by the state of implementing class.
     *
     * @param rawDataArray the array of data elements to process
     */
    public void smeltToFile(T[] rawDataArray);

    /**
     * Saves the collection of data to a specified location. The file location is handled by the state of implementing class.
     *
     * @param rawDataCollection the collection of data elements to process
     */
    public void smeltToFile(Collection<T> rawDataCollection);

    /**
     * Saves the data to a specified location. The data is handled by the state of implementing class.
     * @param destinationPathName The full or relative path and name of file to save to.
     */
    public void smeltToFile(Path destinationPathName);

    /**
     * Saves data to a location. The data and file location is handled by the state of implementing class.
     */
    public void smeltToFile();
}