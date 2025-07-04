/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.SmelterStateful;

import java.nio.file.Path;
import java.util.Collection;

/**
 * The purpose of this interface is to behave as a file writing stream to save files.
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface FileSmelterStateful<T> extends FileSmelter<T>{
    /**
     * Processes a single data element. The file location is handled by the state of implementing class.
     *
     * @param rawDataElement the data element to process
     */
    public void smeltToFile(T rawDataElement);

    /**
     * Processes an array of data elements. The file location is handled by the state of implementing class.
     *
     * @param rawDataArray the array of data elements to process
     */
    public void smeltToFile(T[] rawDataArray);

    /**
     * Processes a collection of data elements. The file location is handled by the state of implementing class.
     *
     * @param rawDataCollection the collection of data elements to process
     */
    public void smeltToFile(Collection<T> rawDataCollection);

    /**
     * Save the data to a specified location. The data is handled by the state of implementing class.
     * @param destinationPathName The full or relative path and name of file to save to.
     */
    public void smeltToFile(Path destinationPathName);

    /**
     * Save data to a location. The data and file location is handled by the state of implementing class.
     */
    public void smeltToFile();
}