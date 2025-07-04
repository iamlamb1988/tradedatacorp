/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.SmelterStateful;

import java.nio.file.Path;
import java.util.Collection;

/**
 * The purpose of this interface is to directly write data to a new file from provided data and file at specified path.
 * These methods are primarily intended for stateless implementing classes, but they can be used in other contexts.
 * The format of the file is specified by the implementing class.
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface FileSmelter<T> extends SmelterStateful<T>{
    /**
     * Writes a single data element to a specified new file.
     *
     * @param rawDataElement the data element to be written to specified file.
     * @param destinationPathName The full or relative path and name of file to save to.
     */
    public void smeltToFile(T rawDataElement, Path destinationPathName);

    /**
     * Writes an array of data elements to a specified new file.
     *
     * @param rawDataArray the array of data elements to be written to specified file.
     * @param destinationPathName The full or relative path and name of file to save to.
     */
    public void smeltToFile(T[] rawDataArray, Path destinationPathName);

    /**
     * Writes a collection of data elements to a specified new file.
     *
     * @param rawDataCollection the collection of data elements to be written to specified file.
     * @param destinationPathName The full or relative path and name of file to save to.
     */
    public void smeltToFile(Collection<T> rawDataCollection, Path destinationPathName);
}