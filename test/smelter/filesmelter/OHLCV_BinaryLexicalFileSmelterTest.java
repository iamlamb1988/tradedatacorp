/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
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

import tradedatacorp.test.java.TestResourceFetcher;
import tradedatacorp.smelter.filesmelter.OHLCV_ExpectedResourceValues;
import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

import java.nio.file.Files;
import java.nio.file.Path;

public class OHLCV_BinaryLexicalFileSmelterTest{
    TestResourceFetcher testFileFetcher;
    byte[] EXPECTED_ONEDATAPOINT_BYTE_VALUES;
    byte[] EXPECTED_TWODATAPOINTS_BYTE_VALUES;

    OHLCV_BinaryLexicalFileSmelterTest(){
        testFileFetcher = new TestResourceFetcher();
        EXPECTED_ONEDATAPOINT_BYTE_VALUES = OHLCV_ExpectedResourceValues.expectedOneDatapoint();
        EXPECTED_TWODATAPOINTS_BYTE_VALUES = OHLCV_ExpectedResourceValues.expectedTwoDatapoints();
    }

    @Test
    void writeOneDatapoint(@TempDir Path tempDir){
        OHLCV_BinaryLexicalFileSmelter smelter = new OHLCV_BinaryLexicalFileSmelter(OHLCV_BinaryLexical.genMiniLexical("TEST", 60, (byte)0));
        OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();

        Path testFilePath = testFileFetcher.getFilePath("smelter/filesmelter/OneDatapoint.brclmb");
        Path resultFilePath = tempDir.resolve("result.brclmb");

        smelter.addData(new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5));
        smelter.setTargetFile(resultFilePath);
        smelter.smeltToFile();
        String stringResult = smelter.smeltToString();

        boolean ismatch = false;
        try{ismatch = Files.mismatch(testFilePath,resultFilePath) == -1;
        }catch(Exception err){err.printStackTrace();}

        assertTrue(ismatch); //Gurantees no mismatch or errors on resultant file

        //Additional checks, will ensure String resolution is also a match
        assertEquals(21,EXPECTED_ONEDATAPOINT_BYTE_VALUES.length);
        assertEquals(21,stringResult.length());

        for(int i=0;i<stringResult.length(); ++i){
            assertEquals(
                EXPECTED_ONEDATAPOINT_BYTE_VALUES[i],
                (byte)stringResult.charAt(i),
                "byte mismatch: expected: "+EXPECTED_ONEDATAPOINT_BYTE_VALUES[i]+" but was "+(byte)stringResult.charAt(i)
            );
        }
    }

    @Test
    void writeTwoDatapoints(@TempDir Path tempDir){
        OHLCV_BinaryLexicalFileSmelter smelter = new OHLCV_BinaryLexicalFileSmelter(OHLCV_BinaryLexical.genMiniLexical("TEST", 60, (byte)0));
        OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();

        Path testFilePath = testFileFetcher.getFilePath("smelter/filesmelter/TwoDatapoints.brclmb");
        Path resultFilePath = tempDir.resolve("result.brclmb");

        smelter.addData(new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5));
        smelter.addData(new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6));
        smelter.setTargetFile(resultFilePath);
        smelter.smeltToFile();

        //CHANGE IN FUTURE
        //Future issue: Should not test for exact file match, only need to check if rendered candles stick is exact match within the file
        boolean ismatch = false;
        try{
            ismatch = Files.mismatch(testFilePath,resultFilePath) == -1;
        }catch(Exception err){err.printStackTrace();}

        assertTrue(ismatch,"Mismatch at index: ");
        //END CHANGE IN FUTURE
    }
}