/**
 * @author Bruce Lamb
 * @since 17 JUN 2025
 */
package tradedatacorp.smelter.filesmelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public class OHLCV_BinaryLexicalSmallFileSmelterTest{
    @Nested
    @DisplayName("Super Simple Tests")
    public class SuperSimpleTests{
        @Test
        public void OneDataPointTest(@TempDir Path tempPath) throws Exception{
            //TODO
        }
    }
}