import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileUnsmelter;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;
import tradedatacorp.test.java.TestResourceFetcher;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;

public class TestMain {
    public static void main(String[] args){
        TestResourceFetcher filePathFetcher = new TestResourceFetcher();
        Path binFilePath = filePathFetcher.getFilePath("smelter/filesmelter/TwoDatapoints.brclmb");
        StickDouble[] stickArray;
        System.out.printf("PATH: %s\n", binFilePath.toAbsolutePath());

        OHLCV_BinaryLexicalFileUnsmelter unsmelter = new OHLCV_BinaryLexicalFileUnsmelter();
        Collection<StickDouble> stickCollection = unsmelter.unsmeltFromQuantity(
            binFilePath,
            1,
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