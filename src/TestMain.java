/**
 * @author Bruce Lamb
 * @since 19 APR 2025
 */
import javax.lang.model.util.Elements.Origin;

import tradedatacorp.smelter.filesmelter.OriginalSmallFileSmelter;
import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

public class TestMain{
	public static void main(String[] args){
		OriginalSmallFileSmelter smelter = new OriginalSmallFileSmelter(Original.genMiniLexical("TEST",60,(byte)0));

		System.out.println("Testing 1 data point");
		smelter.addData(new CandleStickFixedDouble(12, 4, 9, 2, 5, 10.5));

		System.out.println("Setting file to: my1datapoint.brclmb");
		smelter.setTargetFile("my1datapoint.brclmb");

		System.out.println("Write the binary file");
		smelter.smelt();
	}
}
