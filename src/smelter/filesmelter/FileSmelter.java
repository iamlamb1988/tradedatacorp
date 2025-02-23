/**
 * @author Bruce Lamb
 * @since 14 AUG 2024
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.Smelter;
import java.nio.file.Path;

/**
 * The purpose of this interface is to behave as a file writing stream to save files.
 */
public interface FileSmelter<RawT,RefinedT,ParamT> extends Smelter<RawT,RefinedT,ParamT>{
    public void smeltToFile(Path destinationPathName);
    public void unionFiles(Path[] fileArray, Path destinationPathName);
}