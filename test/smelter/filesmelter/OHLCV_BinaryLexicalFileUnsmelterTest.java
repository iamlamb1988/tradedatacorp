/**
 * @author Bruce Lamb
 * @since 23 JUN 2025
 */
package tradedatacorp.smelter.filesmelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileUnsmelter;
import tradedatacorp.test.java.TestResourceFetcher;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

public class OHLCV_BinaryLexicalFileUnsmelterTest{
    TestResourceFetcher testFileFetcher;
    OHLCV_BinaryLexicalFileUnsmelterTest(){
        testFileFetcher = new TestResourceFetcher();
    }

    @Test
    public void OneDataPointTest(){
        StickDouble expectedStick1 = new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5);
        StickDouble actualStick1; //Actual Stick saved in the file.

        OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
        Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/OneDatapoint.brclmb");

        Collection<StickDouble> actualStickCollection = reader.unsmelt(filepath.toAbsolutePath().toString());
        assertEquals(1,actualStickCollection.size());

        Iterator<StickDouble> it = actualStickCollection.iterator();
        actualStick1 = it.next();

        assertEquals(expectedStick1.getUTC(),actualStick1.getUTC());
        assertEquals(expectedStick1.getO(),actualStick1.getO());
        assertEquals(expectedStick1.getH(),actualStick1.getH());
        assertEquals(expectedStick1.getL(),actualStick1.getL());
        assertEquals(expectedStick1.getC(),actualStick1.getC());
        assertEquals(expectedStick1.getV(),actualStick1.getV());
    }

    @Test
    public void TwoDataPointTest(){
        StickDouble[] expectedStickArr = new StickDouble[2];
        expectedStickArr[0] = new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5);
        expectedStickArr[1] = new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6);

        StickDouble[] actualStickArr = new StickDouble[2];

        OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
        Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/TwoDatapoints.brclmb");

        Collection<StickDouble> actualStickCollection = reader.unsmelt(filepath.toAbsolutePath().toString());
        assertEquals(2,actualStickCollection.size());

        Iterator<StickDouble> it = actualStickCollection.iterator();
        StickDouble currentStick;
        int actualIndex=0;
        while(it.hasNext()){
            currentStick = it.next();
            for(int i=0; i<expectedStickArr.length; ++i){ //Minor redundancy. OK for 2 elements.
                if(expectedStickArr[i].getUTC() == currentStick.getUTC()){
                    actualStickArr[actualIndex] = currentStick;
                    ++actualIndex;
                    break;
                }
            }
        }

        //1st data point
        assertEquals(expectedStickArr[0].getUTC(),actualStickArr[0].getUTC());
        assertEquals(expectedStickArr[0].getO(),actualStickArr[0].getO());
        assertEquals(expectedStickArr[0].getH(),actualStickArr[0].getH());
        assertEquals(expectedStickArr[0].getL(),actualStickArr[0].getL());
        assertEquals(expectedStickArr[0].getC(),actualStickArr[0].getC());
        assertEquals(expectedStickArr[0].getV(),actualStickArr[0].getV());

        //2nd data point
        assertEquals(expectedStickArr[1].getUTC(),actualStickArr[1].getUTC());
        assertEquals(expectedStickArr[1].getO(),actualStickArr[1].getO());
        assertEquals(expectedStickArr[1].getH(),actualStickArr[1].getH());
        assertEquals(expectedStickArr[1].getL(),actualStickArr[1].getL());
        assertEquals(expectedStickArr[1].getC(),actualStickArr[1].getC());
        assertEquals(expectedStickArr[1].getV(),actualStickArr[1].getV());
    }
}