/**
 * @author Bruce Lamb
 * @since 28 FEB 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.Smelter;
import tradedatacorp.smelter.SmelterStateful;

import java.nio.file.Path;

/**
 * The purpose of this interface is to behave as a file writing stream to save files.
 * @param <T> the type of a single data instance this smelter processes.
 */
public interface FileSmelterStateful<T> extends SmelterStateful<T>{
    /**
     * Save the data to a specified location.
     * @param destinationPathName The full or relative path and name of file to save to.
     */
    public void smeltToFile(Path destinationPathName);
}