/**
 * @author Bruce Lamb
 * @since 15 JUN 2025
 */

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
        OHLCV_BinaryLexicalSmallFileSmelter smelterUnthreaded = new OHLCV_BinaryLexicalSmallFileSmelter(OHLCV_BinaryLexical.genMiniLexical("TEST",60,(byte)0));
        OHLCV_BinaryLexicalFileSmelter smelter = new OHLCV_BinaryLexicalFileSmelter(OHLCV_BinaryLexical.genMiniLexical("TEST",60,(byte)0));
        String binFileName1="OneDatapointUnthreaded.brclmb";
        String binFileName2="OneDatapoint.brclmb";
        String binFileName3="TwoDatapointsUnthreaded.brclmb";
        String binFileName4="TwoDatapoints.brclmb";

        CandleStickFixedDouble stick1 = new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5);
        CandleStickFixedDouble stick2 = new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6);

        System.out.println("Testing 1 data point");
        smelterUnthreaded.addData(stick1);
        smelter.addData(stick1);

        System.out.println("Setting Unthreaded file to: "+binFileName1);
        System.out.println("Setting Threaded file to: "+binFileName2);
        smelterUnthreaded.setTargetFile(binFileName1);
        smelter.setTargetFile(binFileName2);

        System.out.println("Write the binary files");
        smelterUnthreaded.smelt();
        smelter.smelt();

        OHLCV_BinaryLexicalFileUnsmelter unsmelter = new OHLCV_BinaryLexicalFileUnsmelter();

        System.out.println("Reading "+binFileName1+" now.");
        Collection<StickDouble> stickList = unsmelter.unsmelt(binFileName1);
        unsmelter.unsmelt(binFileName1);
        System.out.println("Echoing results now:");
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

        System.out.println("Reading "+binFileName2+" now.");
        stickList = unsmelter.unsmelt(binFileName2);
        unsmelter.unsmelt(binFileName2);
        System.out.println("Echoing results now:");
        count=0;
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

        //Two data points
        smelterUnthreaded.addData(new StickDouble[]{stick1,stick2});
        smelter.addData(new StickDouble[]{stick1,stick2});

        System.out.println("Setting Unthreaded file to: "+binFileName3);
        System.out.println("Setting Threaded file to: "+binFileName4);
        smelterUnthreaded.setTargetFile(binFileName3);
        smelter.setTargetFile(binFileName4);

        System.out.println("Write the binary files");
        smelterUnthreaded.smelt();
        smelter.smelt();

        System.out.println("Reading "+binFileName3+" now.");
        stickList = unsmelter.unsmelt(binFileName3);
        unsmelter.unsmelt(binFileName3);
        System.out.println("Echoing results now:");
        count=0;
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

        System.out.println("Reading "+binFileName4+" now.");
        stickList = unsmelter.unsmelt(binFileName4);
        unsmelter.unsmelt(binFileName4);
        System.out.println("Echoing results now:");
        count=0;
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