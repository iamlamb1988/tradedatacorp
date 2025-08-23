/**
 * @author Bruce Lamb
 * @since 23 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;

import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileUnsmelter;
import tradedatacorp.test.java.TestResourceFetcher;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

public class OHLCV_BinaryLexicalFileUnsmelterTest{
    private static TestResourceFetcher testFileFetcher;
    private static CandleStickFixedDouble[] expectedStickList;

    @BeforeAll
    static void initialize(){
        testFileFetcher = new TestResourceFetcher();
        expectedStickList = new CandleStickFixedDouble[]{
            new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5),       //Index: 0
            new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6), //Index: 1
            new CandleStickFixedDouble(14, 5.3, 8.6, 2.6, 6.7, 9.7) //Index: 2
        };
    }

    @Nested
    @DisplayName("OneDatapoint Unsmelt tests")
    class TestsForOneDatapoint{
        @Test
        public void unsmeltAllTest(){
            StickDouble expectedStick0 = expectedStickList[0];
            StickDouble actualStick0; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/OneDatapoint.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmelt(filepath.toAbsolutePath().toString());
            assertEquals(1,actualStickCollection.size());

            Iterator<StickDouble> it = actualStickCollection.iterator();
            actualStick0 = it.next();

            assertEquals(expectedStick0.getUTC(),actualStick0.getUTC());
            assertEquals(expectedStick0.getO(),actualStick0.getO());
            assertEquals(expectedStick0.getH(),actualStick0.getH());
            assertEquals(expectedStick0.getL(),actualStick0.getL());
            assertEquals(expectedStick0.getC(),actualStick0.getC());
            assertEquals(expectedStick0.getV(),actualStick0.getV());
        }

        @Test
        public void ThreeDatapointsExtractAllDataPoints(){
            StickDouble[] expectedStickArr = new StickDouble[]{expectedStickList[0]};
            StickDouble expectedStick0 = expectedStickList[0];

            StickDouble[] actualStickArr = new StickDouble[1];
            StickDouble actualStick0; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                0,
                1,
                true
            );

            assertEquals(1, actualStickCollection.size());
            Iterator<StickDouble> it = actualStickCollection.iterator();
            StickDouble currentStick;
            int actualIndex=0;
            while(it.hasNext()){
                currentStick = it.next();
                for(int i=0; i<expectedStickArr.length; ++i){ //Minor redundancy. OK for 1 element.
                    if(expectedStickArr[i].getUTC() == currentStick.getUTC()){
                        actualStickArr[actualIndex] = currentStick;
                        ++actualIndex;
                        break;
                    }
                }
            }
            actualStick0 = actualStickArr[0];

            //data point
            assertEquals(expectedStick0.getUTC(),actualStick0.getUTC());
            assertEquals(expectedStick0.getO(),actualStick0.getO());
            assertEquals(expectedStick0.getH(),actualStick0.getH());
            assertEquals(expectedStick0.getL(),actualStick0.getL());
            assertEquals(expectedStick0.getC(),actualStick0.getC());
            assertEquals(expectedStick0.getV(),actualStick0.getV());
        }
    }

    @Nested
    @DisplayName("TwoDatapoints Unsmelt tests")
    class TestsForTwoDatapoints{
        @Test
        public void unsmeltAllTest(){
            StickDouble[] expectedStickArr = new StickDouble[]{
                expectedStickList[0],
                expectedStickList[1]
            };

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

        @Test
        public void TwoDatapointsExtractFirstDataPoint(){
            StickDouble expectedStick0 = expectedStickList[0];
            StickDouble actualStick0; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/TwoDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                0,
                1,
                true
            );

            assertEquals(1, actualStickCollection.size());
            actualStick0 = actualStickCollection.iterator().next();

            //2nd data point
            assertEquals(expectedStick0.getUTC(),actualStick0.getUTC());
            assertEquals(expectedStick0.getO(),actualStick0.getO());
            assertEquals(expectedStick0.getH(),actualStick0.getH());
            assertEquals(expectedStick0.getL(),actualStick0.getL());
            assertEquals(expectedStick0.getC(),actualStick0.getC());
            assertEquals(expectedStick0.getV(),actualStick0.getV());
        }

        @Test
        public void TwoDatapointsExtractSecondDataPoint(){
            StickDouble expectedStick1 = expectedStickList[1];
            StickDouble actualStick1; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/TwoDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                1,
                1,
                true
            );

            assertEquals(1, actualStickCollection.size());
            actualStick1 = actualStickCollection.iterator().next();

            //2nd data point
            assertEquals(expectedStick1.getUTC(),actualStick1.getUTC());
            assertEquals(expectedStick1.getO(),actualStick1.getO());
            assertEquals(expectedStick1.getH(),actualStick1.getH());
            assertEquals(expectedStick1.getL(),actualStick1.getL());
            assertEquals(expectedStick1.getC(),actualStick1.getC());
            assertEquals(expectedStick1.getV(),actualStick1.getV());
        }

        @Test
        public void TwoDatapointsExtractAllDataPoints(){
            StickDouble[] expectedStickArr = new StickDouble[]{
                expectedStickList[0],
                expectedStickList[1]
            };
            StickDouble expectedStick0 = expectedStickList[0];
            StickDouble expectedStick1 = expectedStickList[1];

            StickDouble[] actualStickArr = new StickDouble[3];
            StickDouble actualStick0; //Actual Stick saved in the file.
            StickDouble actualStick1; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                0,
                2,
                true
            );

            assertEquals(2, actualStickCollection.size());
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
            actualStick0 = actualStickArr[0];
            actualStick1 = actualStickArr[1];

            //1st data point
            assertEquals(expectedStick0.getUTC(),actualStick0.getUTC());
            assertEquals(expectedStick0.getO(),actualStick0.getO());
            assertEquals(expectedStick0.getH(),actualStick0.getH());
            assertEquals(expectedStick0.getL(),actualStick0.getL());
            assertEquals(expectedStick0.getC(),actualStick0.getC());
            assertEquals(expectedStick0.getV(),actualStick0.getV());

            //2nd data point
            assertEquals(expectedStick1.getUTC(),actualStick1.getUTC());
            assertEquals(expectedStick1.getO(),actualStick1.getO());
            assertEquals(expectedStick1.getH(),actualStick1.getH());
            assertEquals(expectedStick1.getL(),actualStick1.getL());
            assertEquals(expectedStick1.getC(),actualStick1.getC());
            assertEquals(expectedStick1.getV(),actualStick1.getV());
        }
    }

    @Nested
    @DisplayName("ThreeDatapoints Unsmelt tests")
    class TestsForThreeDatapoints{
        @Test
        public void unsmeltAllTest(){
            StickDouble[] expectedStickArr = new StickDouble[]{
                expectedStickList[0],
                expectedStickList[1],
                expectedStickList[2]
            };

            StickDouble[] actualStickArr = new StickDouble[3];

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmelt(filepath.toAbsolutePath().toString());
            assertEquals(3,actualStickCollection.size());

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

            //3rd data point
            assertEquals(expectedStickArr[2].getUTC(),actualStickArr[2].getUTC());
            assertEquals(expectedStickArr[2].getO(),actualStickArr[2].getO());
            assertEquals(expectedStickArr[2].getH(),actualStickArr[2].getH());
            assertEquals(expectedStickArr[2].getL(),actualStickArr[2].getL());
            assertEquals(expectedStickArr[2].getC(),actualStickArr[2].getC());
            assertEquals(expectedStickArr[2].getV(),actualStickArr[2].getV());
        }

        @Test
        public void ThreeDatapointsExtractFirstDataPoint(){
            StickDouble expectedStick0 = expectedStickList[0];
            StickDouble actualStick0; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                0,
                1,
                true
            );

            assertEquals(1, actualStickCollection.size());
            actualStick0 = actualStickCollection.iterator().next();

            //2nd data point
            assertEquals(expectedStick0.getUTC(),actualStick0.getUTC());
            assertEquals(expectedStick0.getO(),actualStick0.getO());
            assertEquals(expectedStick0.getH(),actualStick0.getH());
            assertEquals(expectedStick0.getL(),actualStick0.getL());
            assertEquals(expectedStick0.getC(),actualStick0.getC());
            assertEquals(expectedStick0.getV(),actualStick0.getV());
        }

        @Test
        public void ThreeDatapointsExtractSecondDataPoint(){
            StickDouble expectedStick1 = expectedStickList[1];
            StickDouble actualStick1; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                1,
                1,
                true
            );

            assertEquals(1, actualStickCollection.size());
            actualStick1 = actualStickCollection.iterator().next();

            //2nd data point
            assertEquals(expectedStick1.getUTC(),actualStick1.getUTC());
            assertEquals(expectedStick1.getO(),actualStick1.getO());
            assertEquals(expectedStick1.getH(),actualStick1.getH());
            assertEquals(expectedStick1.getL(),actualStick1.getL());
            assertEquals(expectedStick1.getC(),actualStick1.getC());
            assertEquals(expectedStick1.getV(),actualStick1.getV());
        }

        @Test
        public void ThreeDatapointsExtractThirdDataPoint(){
            StickDouble expectedStick2 = expectedStickList[2];
            StickDouble actualStick2; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                2,
                1,
                true
            );

            assertEquals(1, actualStickCollection.size());
            actualStick2 = actualStickCollection.iterator().next();

            //2nd data point
            assertEquals(expectedStick2.getUTC(),actualStick2.getUTC());
            assertEquals(expectedStick2.getO(),actualStick2.getO());
            assertEquals(expectedStick2.getH(),actualStick2.getH());
            assertEquals(expectedStick2.getL(),actualStick2.getL());
            assertEquals(expectedStick2.getC(),actualStick2.getC());
            assertEquals(expectedStick2.getV(),actualStick2.getV());
        }

        @Test
        public void ThreeDatapointsExtractFirstAndSecondDataPoint(){
            StickDouble[] expectedStickArr = new StickDouble[]{
                expectedStickList[0],
                expectedStickList[1]
            };
            StickDouble expectedStick0 = expectedStickList[0];
            StickDouble expectedStick1 = expectedStickList[1];

            StickDouble[] actualStickArr = new StickDouble[2];
            StickDouble actualStick0; //Actual Stick saved in the file.
            StickDouble actualStick1; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                0,
                2,
                true
            );

            assertEquals(2, actualStickCollection.size());
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
            actualStick0 = actualStickArr[0];
            actualStick1 = actualStickArr[1];

            //1st data point
            assertEquals(expectedStick0.getUTC(),actualStick0.getUTC());
            assertEquals(expectedStick0.getO(),actualStick0.getO());
            assertEquals(expectedStick0.getH(),actualStick0.getH());
            assertEquals(expectedStick0.getL(),actualStick0.getL());
            assertEquals(expectedStick0.getC(),actualStick0.getC());
            assertEquals(expectedStick0.getV(),actualStick0.getV());

            //2nd data point
            assertEquals(expectedStick1.getUTC(),actualStick1.getUTC());
            assertEquals(expectedStick1.getO(),actualStick1.getO());
            assertEquals(expectedStick1.getH(),actualStick1.getH());
            assertEquals(expectedStick1.getL(),actualStick1.getL());
            assertEquals(expectedStick1.getC(),actualStick1.getC());
            assertEquals(expectedStick1.getV(),actualStick1.getV());
        }

        @Test
        public void ThreeDatapointsExtractSecondAndThirdDataPoint(){
            StickDouble[] expectedStickArr = new StickDouble[]{
                expectedStickList[1],
                expectedStickList[2]
            };
            StickDouble expectedStick1 = expectedStickList[1];
            StickDouble expectedStick2 = expectedStickList[2];

            StickDouble[] actualStickArr = new StickDouble[2];
            StickDouble actualStick1; //Actual Stick saved in the file.
            StickDouble actualStick2; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                1,
                2,
                true
            );

            assertEquals(2, actualStickCollection.size());
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
            actualStick1 = actualStickArr[0];
            actualStick2 = actualStickArr[1];

            //2nd data point
            assertEquals(expectedStick1.getUTC(),actualStick1.getUTC());
            assertEquals(expectedStick1.getO(),actualStick1.getO());
            assertEquals(expectedStick1.getH(),actualStick1.getH());
            assertEquals(expectedStick1.getL(),actualStick1.getL());
            assertEquals(expectedStick1.getC(),actualStick1.getC());
            assertEquals(expectedStick1.getV(),actualStick1.getV());

            //3rd data point
            assertEquals(expectedStick2.getUTC(),actualStick2.getUTC());
            assertEquals(expectedStick2.getO(),actualStick2.getO());
            assertEquals(expectedStick2.getH(),actualStick2.getH());
            assertEquals(expectedStick2.getL(),actualStick2.getL());
            assertEquals(expectedStick2.getC(),actualStick2.getC());
            assertEquals(expectedStick2.getV(),actualStick2.getV());
        }

        @Test
        public void ThreeDatapointsExtractAllDataPoints(){
            StickDouble[] expectedStickArr = new StickDouble[]{
                expectedStickList[0],
                expectedStickList[1],
                expectedStickList[2]
            };
            StickDouble expectedStick0 = expectedStickList[0];
            StickDouble expectedStick1 = expectedStickList[1];
            StickDouble expectedStick2 = expectedStickList[2];

            StickDouble[] actualStickArr = new StickDouble[3];
            StickDouble actualStick0; //Actual Stick saved in the file.
            StickDouble actualStick1; //Actual Stick saved in the file.
            StickDouble actualStick2; //Actual Stick saved in the file.

            OHLCV_BinaryLexicalFileUnsmelter reader = new OHLCV_BinaryLexicalFileUnsmelter();
            Path filepath = testFileFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");

            Collection<StickDouble> actualStickCollection = reader.unsmeltFromQuantity(
                filepath,
                0,
                3,
                true
            );

            assertEquals(3, actualStickCollection.size());
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
            actualStick0 = actualStickArr[0];
            actualStick1 = actualStickArr[1];
            actualStick2 = actualStickArr[2];

            //1st data point
            assertEquals(expectedStick0.getUTC(),actualStick0.getUTC());
            assertEquals(expectedStick0.getO(),actualStick0.getO());
            assertEquals(expectedStick0.getH(),actualStick0.getH());
            assertEquals(expectedStick0.getL(),actualStick0.getL());
            assertEquals(expectedStick0.getC(),actualStick0.getC());
            assertEquals(expectedStick0.getV(),actualStick0.getV());

            //2nd data point
            assertEquals(expectedStick1.getUTC(),actualStick1.getUTC());
            assertEquals(expectedStick1.getO(),actualStick1.getO());
            assertEquals(expectedStick1.getH(),actualStick1.getH());
            assertEquals(expectedStick1.getL(),actualStick1.getL());
            assertEquals(expectedStick1.getC(),actualStick1.getC());
            assertEquals(expectedStick1.getV(),actualStick1.getV());

            //3rd data point
            assertEquals(expectedStick2.getUTC(),actualStick2.getUTC());
            assertEquals(expectedStick2.getO(),actualStick2.getO());
            assertEquals(expectedStick2.getH(),actualStick2.getH());
            assertEquals(expectedStick2.getL(),actualStick2.getL());
            assertEquals(expectedStick2.getC(),actualStick2.getC());
            assertEquals(expectedStick2.getV(),actualStick2.getV());
        }
    }
}