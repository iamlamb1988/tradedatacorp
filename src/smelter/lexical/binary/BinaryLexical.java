/**
 * @author Bruce Lamb
 * @since 12 OCT 2024
 */
package tradedatacorp.smelter.lexical.binary;

import tradedatacorp.smelter.lexical.Lexical;

//TODO: H1 is fixed bit length values, H2 is dependant length values based on H1
public interface BinaryLexical<RefinedT> extends Lexical{
    public byte[][] getH(String name, String interval);

    public boolean[] toBits(RefinedT Data);
    public byte[][] toBytesWithRemainder(RefinedT Data);

    public RefinedT toRefined(boolean[] bitL);
    public RefinedT toRefined(byte[][] byteL);
}