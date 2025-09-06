/**
 * @author Bruce Lamb
 * @since 31 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;

import java.nio.file.Path;
import java.util.Collection;

public interface FileUnsmelterPartial<RefinedT>{
    public Collection<RefinedT> unsmeltFileToCollectionFromTo(Path originalBinaryFile, int fromIndex, int toIndex, boolean isFile);
    public Collection<RefinedT> unsmeltFileToCollectionFromQuantity(String originalBinaryFilePathName, int fromIndex, int quantity, boolean isFile);
    public Collection<RefinedT> unsmeltFileToCollectionFromQuantity(Path originalBinaryFile, int fromIndex, int quantity, boolean isFile);
    public RefinedT[] unsmeltFileToArrayFromTo(Path originalBinaryFile, int fromIndex, int toIndex, boolean isFile);
    public RefinedT[] unsmeltFileToArrayFromQuantity(String originalBinaryFilePathName, int fromIndex, int quantity, boolean isFile);
    public RefinedT[] unsmeltFileToArrayFromQuantity(Path originalBinaryFile, int fromIndex, int quantity, boolean isFile);
}