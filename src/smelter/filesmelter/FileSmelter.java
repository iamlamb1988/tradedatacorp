/**
 * @author Bruce Lamb
 * @since 14 AUG 2024
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.Smelter;
import java.nio.file.Path;

/**
 * Filebased smelter specifically for reading and writing to files.
 */
public interface FileSmelter<RawT,RefinedT,ParamT> extends Smelter<RawT,RefinedT,ParamT>{
    public void smeltToFile(Path destinationPathName);
    public void unionFiles(Path[] fileArray, Path destinationPathName);
}