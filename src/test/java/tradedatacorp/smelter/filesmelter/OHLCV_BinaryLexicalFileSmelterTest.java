/**
 * @author Bruce Lamb
 * @since 17 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileSmelter;
import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileUnsmelter;
import tradedatacorp.TestResourceFetcher;
import tradedatacorp.smelter.filesmelter.OHLCV_ExpectedResourceValues;
import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.tools.stick.primitive.StickDouble;
import tradedatacorp.tools.stick.primitive.CandleStickFixedDouble;

import java.nio.file.Files;
import java.nio.file.Path;

public class OHLCV_BinaryLexicalFileSmelterTest{
    private static TestResourceFetcher testFileFetcher;

    private static Path expectedOneDatapointFile;
    private static Path expectedTwoDatapointsFile;
    private static Path expectedThreeDatapointsFile;

    private static byte[] EXPECTED_ONEDATAPOINT_BYTE_VALUES;
    private static byte[] EXPECTED_TWODATAPOINTS_BYTE_VALUES;
    private static byte[] EXPECTED_THREEDATAPOINTS_BYTE_VALUES;

    @BeforeAll
    static void initialize(){
        testFileFetcher = new TestResourceFetcher();
        EXPECTED_ONEDATAPOINT_BYTE_VALUES = OHLCV_ExpectedResourceValues.expectedOneDatapoint();
        EXPECTED_TWODATAPOINTS_BYTE_VALUES = OHLCV_ExpectedResourceValues.expectedTwoDatapoints();
        EXPECTED_THREEDATAPOINTS_BYTE_VALUES = OHLCV_ExpectedResourceValues.expectedThreeDatapoints();

        expectedOneDatapointFile = testFileFetcher.getFilePath("smelter/filesmelter/OneDatapoint.brclmb");
        expectedTwoDatapointsFile = testFileFetcher.getFilePath("smelter/filesmelter/TwoDatapoints.brclmb");
        expectedThreeDatapointsFile = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");
    }

    @Nested
    @DisplayName("Tests for smelter/filesmelter/OneDatapoint.brclmb")
    class TestsForOneDatapoint{
        private OHLCV_BinaryLexicalFileSmelter smelter;

        @BeforeEach
        void initializeSmelterBeforeEachTest(){
            smelter = new OHLCV_BinaryLexicalFileSmelter(OHLCV_BinaryLexical.genMiniLexical("TEST", 60, (byte)0));
            smelter.addData(new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5));
        }

        @Test
        void writeOneDatapointToFile(@TempDir Path tmpDir){
            Path resultOneDatapointFile = tmpDir.resolve("testResultOneDatapoint.brclmb");
            smelter.setTargetFile(resultOneDatapointFile);
            smelter.smeltToFile();

            boolean ismatch = false;
            try{ismatch = Files.mismatch(expectedOneDatapointFile,resultOneDatapointFile) == -1;}
            catch(Exception err){err.printStackTrace();}

            assertTrue(ismatch);
        }
    }

    @Nested
    @DisplayName("Tests for smelter/filesmelter/TwoDatapoints.brclmb")
    class TestsForTwoDatapoints{
        private OHLCV_BinaryLexicalFileSmelter smelter;

        @BeforeEach
        void initializeSmelterBeforeEachTest(){
            smelter = new OHLCV_BinaryLexicalFileSmelter(OHLCV_BinaryLexical.genMiniLexical("TEST", 60, (byte)0));
            smelter.addData(new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5));
            smelter.addData(new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6));
        }

        @Test
        void writeTwoDatapointsToFile(@TempDir Path tmpDir){
            Path resultTwoDatapointsFile = tmpDir.resolve("testResultTwoDatapoints.brclmb");
            smelter.setTargetFile(resultTwoDatapointsFile);
            smelter.smeltToFile();

            boolean ismatch = false;
            try{ismatch = Files.mismatch(expectedTwoDatapointsFile,resultTwoDatapointsFile) == -1;}
            catch(Exception err){err.printStackTrace();}

            assertTrue(ismatch);
        }

        @Test
        void writeTwoDatapointsToString(){
            String stringResult = smelter.smeltToString();
            assertEquals(27,EXPECTED_TWODATAPOINTS_BYTE_VALUES.length);
            assertEquals(27,stringResult.length());

            for(int i=0;i<stringResult.length(); ++i){
                assertEquals(
                    EXPECTED_TWODATAPOINTS_BYTE_VALUES[i],
                    (byte)stringResult.charAt(i),
                    "byte mismatch: expected: "+EXPECTED_TWODATAPOINTS_BYTE_VALUES[i]+" but was "+(byte)stringResult.charAt(i) + (" at index: "+i)
                );
            }
        }
    }

    @Nested
    @DisplayName("Tests for smelter/filesmelter/ThreeDatapoints.brclmb")
    class TestsForThreeDatapoints{
        private OHLCV_BinaryLexicalFileSmelter smelter;

        @BeforeEach
        void initializeSmelterBeforeEachTest(){
            smelter = new OHLCV_BinaryLexicalFileSmelter(OHLCV_BinaryLexical.genMiniLexical("TEST", 60, (byte)0));
            smelter.addData(new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5));
            smelter.addData(new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6));
            smelter.addData(new CandleStickFixedDouble(14, 5.3, 8.6, 2.6, 6.7, 9.7));
        }

        @Test
        void writeThreeDatapointsToFile(@TempDir Path tmpDir){
            Path resultThreeDatapointsFile = tmpDir.resolve("testResultThreeDatapoints.brclmb");
            smelter.setTargetFile(resultThreeDatapointsFile);
            smelter.smeltToFile();

            boolean ismatch = false;
            try{ismatch = Files.mismatch(expectedThreeDatapointsFile,resultThreeDatapointsFile) == -1;}
            catch(Exception err){err.printStackTrace();}

            assertTrue(ismatch);
        }

        @Test
        void writeThreeDatapointsToString(){
            String stringResult = smelter.smeltToString();
            assertEquals(32,EXPECTED_THREEDATAPOINTS_BYTE_VALUES.length);
            assertEquals(32,stringResult.length());

            for(int i=0;i<stringResult.length(); ++i){
                assertEquals(
                    EXPECTED_THREEDATAPOINTS_BYTE_VALUES[i],
                    (byte)stringResult.charAt(i),
                    "byte mismatch: expected: "+EXPECTED_THREEDATAPOINTS_BYTE_VALUES[i]+" but was "+(byte)stringResult.charAt(i) + (" at index: "+i)
                );
            }
        }
    }
}