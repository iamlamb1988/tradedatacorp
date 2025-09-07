/**
 * @author Bruce Lamb
 * @since 31 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;

import java.nio.file.Path;
import java.util.Collection;

public interface FileUnsmelter<RefinedT>{
    public Collection<RefinedT> unsmeltFileToCollection(Path originalBinaryFile);
    public RefinedT[] unsmeltFileToArray(Path originalBinaryFile);
}