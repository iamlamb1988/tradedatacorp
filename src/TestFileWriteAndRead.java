import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalSmallFileSmelter;
import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileSmelter;
import tradedatacorp.smelter.filesmelter.OHLCV_BinaryLexicalFileUnsmelter;
import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;
import java.nio.file.Path;
import java.util.Collection;

public class TestFileWriteAndRead{
    public static void main(String[] args){
        OHLCV_BinaryLexicalFileSmelter smelter = new OHLCV_BinaryLexicalFileSmelter(OHLCV_BinaryLexical.genMiniLexical("TEST",60,(byte)0));
        String binFileName="tmp2dp.brclmb";

        CandleStickFixedDouble stick1 = new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5);
        CandleStickFixedDouble stick2 = new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6);

        smelter.addData(stick1);
        smelter.addData(stick2);
        smelter.setTargetFile(binFileName);
        smelter.smelt();

        readSticks(binFileName);
    }

    public static void readSticks(String FilePathName){
        OHLCV_BinaryLexicalFileUnsmelter unsmelter = new OHLCV_BinaryLexicalFileUnsmelter();
        Collection<StickDouble> stickList = unsmelter.unsmelt(FilePathName);

        int count=0;
        for(StickDouble stick : stickList){
            System.out.println(
                "Stick["+count+"] UTC="+stick.getUTC() +
                " O="+stick.getO() +
                " H="+stick.getH() +
                " L="+stick.getL() +
                " C="+stick.getC() +
                " V="+stick.getV()
            );
            ++count;
        }
    }
}