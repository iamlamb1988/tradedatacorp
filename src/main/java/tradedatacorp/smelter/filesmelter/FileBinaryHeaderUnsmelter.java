/**
 * @author Bruce Lamb
 * @since 06 SEP 2025
 */
package tradedatacorp.smelter.filesmelter;

import java.nio.file.Path;

public interface FileBinaryHeaderUnsmelter{
    public boolean[][] unsmeltFileHeader(Path originalBinaryFile);
}