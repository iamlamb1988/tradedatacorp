/**
 * @author Bruce Lamb
 * @since 8 FEB 2025
 */
package tradedatacorp.smelter.lexical.binary;

import tradedatacorp.smelter.lexical.Lexical;
import java.util.Collection;

//The purpose of a Lexical is to encode and decode binary to data objects
//Generic Type RefineT represents a singular datatype
public interface BinaryLexical<RefinedT> extends Lexical{
    //Get Binary Header
    public boolean[][] getBinaryHeader();
    public boolean[] getBinaryHeaderFlat();

    //Get Binary Data from Data instances
    public boolean[][] getBinaryData(RefinedT singleData);
    public boolean[] getBinaryDataFlat(RefinedT singleData);
    public boolean[][][] getBinaryDataPoints(RefinedT[] dataArray);
    public boolean[][][] getBinaryDataPoints(Collection<RefinedT> dataCollection);
    public boolean[] getBinaryDataPointsFlat(RefinedT[] dataArray);
    public boolean[] getBinaryDataPointsFlat(Collection<RefinedT> dataCollection);

    //Get Data instance from Binary
    public RefinedT getRefinedData(boolean[][] singleBinaryData);
    public RefinedT getRefinedDataFlat(boolean[] singleFlatBinaryData);
    public RefinedT[] getRefinedDataArray(boolean[][][] BinaryDataArray);
    public RefinedT[] getRefinedDataArrayFlat(boolean[] BinaryFlatDataArray);
}