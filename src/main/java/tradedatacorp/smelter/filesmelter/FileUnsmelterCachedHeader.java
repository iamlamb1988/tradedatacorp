/**
 * @author Bruce Lamb
 * @since 31 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;

import java.nio.file.Path;
import java.util.Collection;

public interface FileUnsmelterCachedHeader<RefinedT>{
    public Collection<RefinedT> unsmeltFileToCollection(Path originalBinaryFile, boolean[][] cachedHeader);
    public RefinedT[] unsmeltFileToArray(Path originalBinaryFile, boolean[][] cachedHeader);
}