/**
 * @author Bruce Lamb
 * @since 31 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.SmelterStateful;
import java.nio.file.Path;

public interface FileUnsmelterPartialCachedHeader{
    public void unsmelt(String originalBinaryFile);
}