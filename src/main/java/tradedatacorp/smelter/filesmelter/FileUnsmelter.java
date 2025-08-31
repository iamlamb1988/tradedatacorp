/**
 * @author Bruce Lamb
 * @since 31 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.SmelterStateful;
import tradedatacorp.tools.stick.primitive.StickDouble;

import java.util.Collection;

public interface FileUnsmelter<T>{
    public Collection<T> unsmeltFileToCollection(String originalBinaryFile);
    public T[] unsmeltFileToArray(String originalBinaryFile);
}