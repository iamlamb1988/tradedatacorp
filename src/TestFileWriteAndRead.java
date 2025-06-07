/**
 * @author Bruce Lamb
 * @since 6 JUN 2025
 */

import tradedatacorp.smelter.filesmelter.OriginalSmallFileSmelter;
import tradedatacorp.smelter.filesmelter.OriginalFileSmelter;
import tradedatacorp.smelter.filesmelter.OriginalFileUnsmelter;
import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;
import java.nio.file.Path;
import java.util.Collection;
 
public class TestFileWriteAndRead{
    public static void main(String[] args){
        OriginalSmallFileSmelter smelterUnthreaded = new OriginalSmallFileSmelter(Original.genMiniLexical("TEST",60,(byte)0));
        OriginalFileSmelter smelter = new OriginalFileSmelter(Original.genMiniLexical("TEST",60,(byte)0));
        String binFileName1="datapointsUnthreaded.brclmb";
        String binFileName2="datapoints.brclmb";

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

        // OriginalFileUnsmelter unsmelter = new OriginalFileUnsmelter();
        // System.out.println("Reading file now");
        // Collection<StickDouble> stickList = unsmelter.unsmelt(binFileName1);
        // unsmelter.unsmelt(binFileName1);
        // System.out.println("Echoing results now.");
        // int count=0;
        // for(StickDouble stick : stickList){
        //     System.out.println(
        //         "Stick["+count+"] UTC="+stick.getUTC() +
        //         " O="+stick.getO() +
        //         " H="+stick.getH() +
        //         " L="+stick.getL() +
        //         " C="+stick.getC() +
        //         " V="+stick.getV()
        //     );
        //     ++count;
        // }
    }
}