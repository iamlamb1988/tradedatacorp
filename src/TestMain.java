/**
 * @author Bruce Lamb
 * @since 17 AUG 2025
 */
import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileSmelter;
// import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileUnsmelter;
import smelter.impl.filesmelter.OHLCV_BinaryLexicalFileUnsmelter; //DEBUG TODO, restore
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;
import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.test.java.TestResourceFetcher;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

public class TestMain {
    public static void main(String[] args){
        // OHLCV_BinaryLexicalFileSmelter stickFileWriter = new OHLCV_BinaryLexicalFileSmelter(
        //     OHLCV_BinaryLexical.genMiniLexical("TEST",60,(byte)0)
        // );

        // stickFileWriter.setTargetFile(Path.of("ThreeDatapoints.brclmb"));
        // stickFileWriter.addData(new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5));
        // stickFileWriter.addData(new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6));
        // stickFileWriter.addData(new CandleStickFixedDouble(14, 5.3, 8.6, 2.6, 6.7, 9.7));

        // stickFileWriter.smeltToFile();

        ////////
        TestResourceFetcher filePathFetcher = new TestResourceFetcher();
        Path binFilePath = filePathFetcher.getFilePath("smelter/filesmelter/ThreeDatapoints.brclmb");
        StickDouble[] stickArray;
        System.out.printf("PATH: %s\n", binFilePath.toAbsolutePath());

        OHLCV_BinaryLexicalFileUnsmelter unsmelter = new OHLCV_BinaryLexicalFileUnsmelter();
        Collection<StickDouble> stickCollection = unsmelter.unsmeltFromQuantity(
            binFilePath,
            2,
            1,
            true
        );

        Iterator<StickDouble> it = stickCollection.iterator();
        stickArray = new StickDouble[stickCollection.size()];

        int i=0;
        while(it.hasNext()){
            stickArray[i] = it.next();
            ++i;
        }
        System.out.println("End");
    }
}