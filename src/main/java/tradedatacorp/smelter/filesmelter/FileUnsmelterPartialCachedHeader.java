/**
 * @author Bruce Lamb
 * @since 06 SEP 2025
 */
package tradedatacorp.smelter.filesmelter;

import java.nio.file.Path;
import java.util.Collection;

public interface FileUnsmelterPartialCachedHeader<RefinedT>{
    public Collection<RefinedT> unsmeltFileToCollectionFromTo(Path originalBinaryFile, int fromIndex, int toIndex, boolean isFile, boolean[][] cachedHeader);
    public Collection<RefinedT> unsmeltFileToCollectionFromQuantity(Path originalBinaryFile, int fromIndex, int quantity, boolean isFile, boolean[][] cachedHeader);
    public RefinedT[] unsmeltFileToArrayFromTo(Path originalBinaryFile, int fromIndex, int toIndex, boolean isFile, boolean[][] cachedHeader);
    public RefinedT[] unsmeltFileToArrayFromQuantity(Path originalBinaryFile, int fromIndex, int quantity, boolean isFile, boolean[][] cachedHeader);
}