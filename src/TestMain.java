/**
 * @author Bruce Lamb
 * @since 1 MAY 2025
 */

import tradedatacorp.smelter.filesmelter.OriginalSmallFileSmelter;
import tradedatacorp.smelter.filesmelter.OriginalFileUnsmelter;
import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;
import java.nio.file.Path;
import java.util.Collection;

public class TestMain{
	public static void main(String[] args){
		OriginalSmallFileSmelter smelter = new OriginalSmallFileSmelter(Original.genMiniLexical("TEST",60,(byte)0));
		String binFileName="mydatapoints.brclmb";

		System.out.println("Testing 1 data point");
		smelter.addData(new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5));
		smelter.addData(new CandleStickFixedDouble(13, 4.1, 9.7, 2.2, 5, 15.6));

		System.out.println("Setting file to: "+binFileName);
		smelter.setTargetFile(binFileName);

		System.out.println("Write the binary file");
		smelter.smelt();

		OriginalFileUnsmelter unsmelter = new OriginalFileUnsmelter();
		System.out.println("Reading file now");
		Collection<StickDouble> stickList = unsmelter.unsmelt(binFileName);
		unsmelter.unsmelt(binFileName);
		System.out.println("Echoing results now.");
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
