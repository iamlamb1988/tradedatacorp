/**
 * @author Bruce Lamb
 * @since 23 FEB 2025
 */
package tradedatacorp.smelter.lexical.binary;

import tradedatacorp.smelter.lexical.Lexical;
import java.util.Collection;

/**
 * An interface for taking objects and translating to and from binary format (serializing and deserializing).
 * This interface has a header that represents the metadata about the data points. There is one header and one or more data.
 * @param <RefinedT> A singular Datapoint object.
 * Each data point is exactly the same number of bits concatenated together.
 */
public interface BinaryLexical<RefinedT> extends Lexical{
    /**
     * Returns a 2D boolean array representing the binary header.
     * @return 2D boolean array. Each element (the first dimension) of the array represents an individual header field.
     */
    public boolean[][] getBinaryHeader();

    /**
     * Returns the binary header where all header fields are concatenated into a boolean array.
     * @return Takes the result of {@code getBinaryHeader()} and concatenates them into a singular array.
     */
    public boolean[] getBinaryHeaderFlat();

    /**
     * Returns a 2D boolean array representing a singular data point.
     * @param singleData A datapoint Object that will be converted to a 2D boolean Array.
     * @return 2D boolean array. Each element (the first dimension) represents a data field for that specific data point.
     * This is the reverse process of {@code getRefinedData(boolean[][] singleBinaryData)}.
     */
    public boolean[][] getBinaryData(RefinedT singleData);

    /**
     * Returns the binary datapoint where all data fields are concatenated into a boolean array.
     * @param singleData A datapoint Object that will be converted to a boolean Array.
     * @return Takes the result of {@code getBinaryData(RefinedT singleData)} and concatenates them into a singular array.
     * This is the reverse process of {@code getRefinedDataFlat(boolean[] singleFlatBinaryData)}.
     */
    public boolean[] getBinaryDataFlat(RefinedT singleData);

    /**
     * Returns a 3D boolean array representing an array of Refined Objects.
     * @param dataArray An array of RefinedT Objects.
     * @return 3D boolean array. The first element Represents a 2D boolean array of a single datapoint.
     * The 2nd element represents the field of that datapoint.
     * Similar to {@code getBinaryDataPoints(Collection<RefinedT> dataCollection)}.
     * This is the reverse process of {@code getRefinedDataArray(boolean[][][] BinaryDataArray)}.
     */
    public boolean[][][] getBinaryDataPoints(RefinedT[] dataArray);

    /**
     * Returns a 3D boolean array representing an array of Refined Objects.
     * @param dataCollection A generic collection of RefinedT objects.
     * @return 3D boolean array. The first element Represents a 2D boolean array of a single datapoint.
     * The 2nd element represents the field of that datapoint.
     * Similar to {@code getBinaryDataPoints(RefinedT[] dataArray)}.
     * This is the reverse process of {@code getRefinedDataArray(boolean[][][] BinaryDataArray)}.
     */
    public boolean[][][] getBinaryDataPoints(Collection<RefinedT> dataCollection);

    /**
     * Returns a boolean array representing an array of Refined Objects.
     * @param dataArray An array of RefinedT Objects.
     * @return Takes the result of {@code getBinaryDataPoints(Collection<RefinedT> dataCollection)} and concatenates them into a singular array.
     * Similar to {@code getBinaryDataPointsFlat(Collection<RefinedT> dataCollection)}.
     * This is the reverse process of {@code getRefinedDataArrayFlat(boolean[] binaryFlatDataArray)}.
     */
    public boolean[] getBinaryDataPointsFlat(RefinedT[] dataArray);

    /**
     * Returns a boolean array representing an array of Refined Objects.
     * @param dataCollection A generic collection of RefinedT objects.
     * @return Takes the result of {@code getBinaryDataPoints(Collection<RefinedT> dataCollection)} and concatenates them into a singular array.
     * Similar to {@code getBinaryDataPointsFlat(RefinedT[] dataArray)}.
     * This is the reverse process of {@code getRefinedDataArrayFlat(boolean[] binaryFlatDataArray)}.
     */
    public boolean[] getBinaryDataPointsFlat(Collection<RefinedT> dataCollection);

    /**
     * Returns the refined Object from a 2D boolean array representing binary format.
     * @param singleBinaryData 2D boolean array where each element (the first dimension) represents a data field for that specific data point.
     * @return The object of RefinedT.
     * This is the reverse process of {@code getBinaryData(RefinedT singleData)}.
     */
    public RefinedT getRefinedData(boolean[][] singleBinaryData);

    /**
     * Returns the refined Object from a boolean array representing binary format.
     * @param singleFlatBinaryData A boolean array representing a RefinedT object.
     * @return The object of RefinedT.
     * This is the reverse process of {@code getBinaryDataFlat(RefinedT singleData)}.
     */
    public RefinedT getRefinedDataFlat(boolean[] singleFlatBinaryData);

    /**
     * Returns an array of RefinedT objects by decoding the boolean array objects.
     * @param binaryDataArray A 3D array representing a collection of Data points.
     * The first element Represents a 2D boolean array of a single datapoint.
     * The 2nd element represents the field of that datapoint.
     * @return An array of RefinedT objects. The reverse process of {@code getBinaryDataPoints(RefinedT[] dataArray)}.
     */
    public RefinedT[] getRefinedDataArray(boolean[][][] binaryDataArray);

    /**
     * Returns an array of RefinedT objects by decoding the boolean array objects.
     * @param binaryFlatDataArray A boolean array representing a collection of Data points.
     * @return An array of RefinedT objects. The reverse process of {@code getBinaryDataPoints(RefinedT[] dataArray)}.
     */
    public RefinedT[] getRefinedDataArrayFlat(boolean[] binaryFlatDataArray);
}