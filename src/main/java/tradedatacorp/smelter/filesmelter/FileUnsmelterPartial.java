/**
 * @author Bruce Lamb
 * @since 31 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;

import java.nio.file.Path;
import java.util.Collection;

public interface FileUnsmelterPartial<T>{
    public Collection<T> unsmeltFileToCollectionFromTo(Path originalBinaryFile, int fromIndex, int toIndex, boolean isFile);
    public Collection<T> unsmeltFileToCollectionFromQuantity(String originalBinaryFilePathName, int fromIndex, int quantity, boolean isFile);
    public Collection<T> unsmeltFileToCollectionFromQuantity(Path originalBinaryFile, int fromIndex, int quantity, boolean isFile);
    public T[] unsmeltFileToArrayFromTo(Path originalBinaryFile, int fromIndex, int toIndex, boolean isFile);
    public T[] unsmeltFileToArrayFromQuantity(String originalBinaryFilePathName, int fromIndex, int quantity, boolean isFile);
    public T[] unsmeltFileToArrayFromQuantity(Path originalBinaryFile, int fromIndex, int quantity, boolean isFile);
}