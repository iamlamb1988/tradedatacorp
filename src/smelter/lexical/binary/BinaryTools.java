/**
 * @author Bruce Lamb
 * @since 09 JAN 2025
 */
package tradedatacorp.smelter.lexical.binary;

/**
 * A utility class providing stateless static methods for converting between 
 * binary representations (arrays or collections) and integers.
 */
public class BinaryTools{
    public static final byte ZED=(byte)0;
    public static final byte ONE=(byte)1;
    public static final byte NEG_ONE=(byte)-1;
    public static final double LOG10_BASE2 = Math.log10(2);

    /**
     * Calculates the minimum number of bits required to represent a non-negative base-10 integer in binary.
     * @param nonNegativeInteger a non-negative integer to be evaluated.
     * @return The minimum number of bits to represent the non-negative integer.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code getMinimumNumberOfBits(3); // returns 2. binary 11 => 3, minimum of 2 bits represents 3.}
     * Example 2: {@code getMinimumNumberOfBits(5); // returns 3. binary 101 => 5, minimum of 3 bits represents 5.}
     * Example 3: {@code getMinimumNumberOfBits(0); // returns 1. binary 0 => 0, minimum of 1 bit represents 0.}
     * </code></pre> 
     */
    public static int getMinimumNumberOfBits(int nonNegativeInteger){
        if(nonNegativeInteger == 0) return 1; //1 bit required to represent 0.
        return (int)Math.ceil(Math.log10(nonNegativeInteger)/LOG10_BASE2);
    }

    /**
     * Calculates a non-negative base-10 integer from an array of boolean values.
     * @param bin The boolean array to be evaluated that represents a non-negative base-10 integer.
     * Must be non-null and contain at least 1 element.
     * Most significant bit is at index 0.
     * @return The non-negative base-10 integer to be calculated.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code toUnsignedInt(new boolean[]{true, true, true}); //returns 7. bool array represents 111}
     * Example 2: {@code toUnsignedInt(new boolean[]{true, true, false}); //returns 6. bool array represents 110}
     * </code></pre>
     */
    public static int toUnsignedInt(boolean[] bin){
        int r=(bin[0] ? 1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    /**
     * Calculates a base-10 integer from an array of boolean values that represent 2s complement number.
     * @param bin The boolean array to be evaluated that represents a base-10 integer in 2s complement notation.
     * Must be non-null and contain at least 1 element.
     * @return The base-10 integer to be calculated.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code to2sCompInt(new boolean[]{true, true, true}); //returns -1. bool array represents 111}
     * Example 2: {@code to2sCompInt(new boolean[]{false, true, true,true}); //returns 7. bool array represents 0111}
     * Example 3: {@code to2sCompInt(new boolean[]{true, true, false}); //returns -2. bool array represents 110}
     * </code></pre>
     */
    public static int to2sCompInt(boolean[] bin){
        int r=(bin[0] ? -1 : 0);
        for(byte i=(byte)1; i<bin.length; ++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    /**
     * Calculates a base-10 non-negative long integer from an array of boolean values that represent 2s complement binary number.
     * @param bin The boolean array to be evaluated that represents a non-negative long base-10 integer.
     * Must be non-null and contain at least 1 element.
     * Most significant bit is at index 0.
     * @return The base-10 long integer to be calculated.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code toUnsignedLong(new boolean[]{true, true, true}); //returns 7L. bool array represents 111}
     * Example 2: {@code toUnsignedLong(new boolean[]{true, true, false}); //returns 6L. bool array represents 110}
     * </code></pre>
     */
    public static long toUnsignedLong(boolean[] bin){
        long r=(bin[0] ? 1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    /**
     * Calculates a base-10 long integer from an array of boolean values that represent 2s complement binary number.
     * @param bin The boolean array to be evaluated that represents a base-10 integer in 2s complement notation.
     * Must be non-null and contain at least 1 element.
     * @return The base-10 integer to be calculated.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code to2sCompLong(new boolean[]{true, true, true}); //returns -1L. bool array represents 111}
     * Example 2: {@code to2sCompLong(new boolean[]{false, true, true,true}); //returns 7L. bool array represents 0111}
     * Example 3: {@code to2sCompLong(new boolean[]{true, true, false}); //returns -2L. bool array represents 110}
     * </code></pre>
     */
    public static long to2sCompLong(boolean[] bin){
        long r=(bin[0] ? -1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    /**
     * Calculates a base-10 integer from boolean array subset.
     * @param bin A boolean array. Does not represent any particular type of format.
     * Must be non-null and contain at least 1 element.
     * @param startIndex The index of the most significant bit in the subset to be used for the calculation.
     * @param length The total number of consecutive elements to be used in {@code bin}.
     * @return The base-10 integer to be calculated.
     * <p>Examples:</p><pre><code>
     * {@code boolean exampleBoolArray = new boolean[]{true,false,true,false,true}; //10101 rep}
     * Example 1: {@code toUnsignedIntFromBoolSubset(exampleBoolArray, 1, 3); //returns 2. subset array: 010, indices 1, 2, and 3.}
     * Example 2: {@code toUnsignedIntFromBoolSubset(exampleBoolArray, 1, 4); //returns 5. subset array: 0101, indices 1, 2, 3, and 4.}
     * Example 3: {@code toUnsignedIntFromBoolSubset(exampleBoolArray, 0, 4); //returns 10. subset array: 1010, indices 0, 1, 2, and 3.}
     * Example 4: {@code toUnsignedIntFromBoolSubset(exampleBoolArray, 2, 3); //returns 5. subset array: 101, indices 2, 3, and 4}
     * </code></pre>
     */
    public static int toUnsignedIntFromBoolSubset(boolean[] bin, int startIndex, int length){
        int r=(bin[startIndex] ? 1 : 0);
        int maxIndex = startIndex + length;
        for(int i=startIndex+1; i<maxIndex; ++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    /**
     * Calculates a base-10 long integer from boolean array subset.
     * @param bin A boolean array. Does not represent any particular type of format.
     * Must be non-null and contain at least 1 element.
     * @param startIndex The index of the most significant bit in the subset to be used for the calculation.
     * @param length The total number of consecutive elements to be used in {@code bin}..
     * @return The base-10 integer to be calculated.
     * <p>Examples:</p><pre><code>
     * {@code boolean exampleBoolArray = new boolean[]{true,false,true,false,true}; //10101 rep}
     * Example 1: {@code toUnsignedLongFromBoolSubset(exampleBoolArray, 1, 3); //returns 2L. subset array: 010, indices 1, 2, and 3.}
     * Example 2: {@code toUnsignedLongFromBoolSubset(exampleBoolArray, 1, 4); //returns 5L. subset array: 0101, indices 1, 2, 3, and 4.}
     * Example 3: {@code toUnsignedLongFromBoolSubset(exampleBoolArray, 0, 4); //returns 10L. subset array: 1010, indices 0, 1, 2, and 3.}
     * Example 4: {@code toUnsignedLongFromBoolSubset(exampleBoolArray, 2, 3); //returns 5L. subset array: 101, indices 2, 3, and 4}
     * </code></pre>
     */
    public static long toUnsignedLongFromBoolSubset(boolean[] bin, int startIndex, int length){
        long r=(bin[startIndex] ? 1 : 0);
        int maxIndex = startIndex + length;
        for(int i=startIndex+1; i<maxIndex; ++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    /**
     * Determines if two boolean arrays are equivalent
     * @param bin1 A boolean array. Must be non-null and contain at least 1 element.
     * @param bin2 A boolean array. Must be non-null and contain at least 1 element.
     * @return {@code true} if both arrays have the same length and every element in {@code bin1} 
     * is equal to the corresponding element in {@code bin2}; {@code false} otherwise.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code isEqualBoolArray(new boolean[]{true, false},new boolean[]{true, false}); //returns true.}
     * Example 2: {@code isEqualBoolArray(new boolean[]{true, false},new boolean[]{false, true}); //returns false.}
     * Example 3: {@code isEqualBoolArray(new boolean[]{true, true},new boolean[]{false, true, true}); //returns false.}
     * Example 4: {@code isEqualBoolArray(new boolean[]{true, true},new boolean[]{true, true, true}); //returns false.}
     * </code></pre>
     */
    public static boolean isEqualBoolArray(boolean[] bin1, boolean[] bin2){
        if(bin1.length != bin2.length) return false;
        for(int i=0; i<bin1.length; ++i){
            if(bin1[i] != bin2[i]) return false;
        }
        return true;
    }

    /**
     * Determines if two boolean arrays representing non-negative binary numbers are equivalent in value. 
     * @param bin1 A boolean array representing a non-negative base-10 integer. Must be non-null and contain at least 1 element.
     * @param bin2 A boolean array representing a non-negative base-10 integer. Must be non-null and contain at least 1 element.
     * @return {@code true} if both arrays evaluate to the same unsigned integer in {@code bin1}; {@code false} otherwise.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code isEqualUnsignedIntValue(new boolean[]{true, false},new boolean[]{true, false}); //returns true. 2 == 2}
     * Example 2: {@code isEqualUnsignedIntValue(new boolean[]{true, false},new boolean[]{false, true}); //returns false. 2 != 1}
     * Example 3: {@code isEqualUnsignedIntValue(new boolean[]{true, true},new boolean[]{false, true, true}); //returns true. 3 == 3}
     * Example 4: {@code isEqualUnsignedIntValue(new boolean[]{true, true},new boolean[]{true, true, true}); //returns false. 3 != -7}
     * </code></pre>
     */
    public static boolean isEqualUnsignedIntValue(boolean[] bin1, boolean[] bin2){
        return toUnsignedInt(bin1) == toUnsignedInt(bin2);
    }

    /**
     * Determines if two boolean arrays of a 2s complement binary number are equivalent in value.
     * @param bin1 A boolean array representing an integer in 2s complement notation. Must be non-null and contain at least 1 element.
     * @param bin2 A boolean array representing an integer in 2s complement notation. Must be non-null and contain at least 1 element.
     * @return {@code true} if both arrays evaluate to the same 2s complement integer in {@code bin1}; {@code false} otherwise.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code isEqual2sCompIntValue(new boolean[]{true, false},new boolean[]{true, false}); //returns true. -2 == -2}
     * Example 2: {@code isEqual2sCompIntValue(new boolean[]{true, false},new boolean[]{false, true}); //returns false.  -2 != 1}
     * Example 3: {@code isEqual2sCompIntValue(new boolean[]{true, true},new boolean[]{false, true, true}); //returns false. -1 != 3}
     * Example 4: {@code isEqual2sCompIntValue(new boolean[]{true, true},new boolean[]{true, true, true}); //returns true. -1 == -1}
     * </code></pre>
     */
    public static boolean isEqual2sCompIntValue(boolean[] bin1, boolean[] bin2){
        return to2sCompInt(bin1) == to2sCompInt(bin2);
    }

    /**
     * Determines if two boolean arrays representing non-negative binary numbers are equivalent in value. 
     * @param bin1 A boolean array representing a non-negative base-10 integer. Must be non-null and contain at least 1 element.
     * @param bin2 A boolean array representing a non-negative base-10 integer. Must be non-null and contain at least 1 element.
     * @return {@code true} if both arrays evaluate to the same unsigned integer in {@code bin1}; {@code false} otherwise.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code isEqualUnsignedIntValue(new boolean[]{true, false},new boolean[]{true, false}); //returns true. 2L == 2L}
     * Example 2: {@code isEqualUnsignedIntValue(new boolean[]{true, false},new boolean[]{false, true}); //returns false. 2L != 1L}
     * Example 3: {@code isEqualUnsignedIntValue(new boolean[]{true, true},new boolean[]{false, true, true}); //returns true. 3L == 3L}
     * Example 4: {@code isEqualUnsignedIntValue(new boolean[]{true, true},new boolean[]{true, true, true}); //returns false. 3L != -7L}
     * </code></pre>
     */
    public static boolean isEqualUnsignedLongValue(boolean[] bin1, boolean[] bin2){
        return toUnsignedLong(bin1) == toUnsignedLong(bin2);
    }

    /**
     * Determines if two boolean arrays of a 2s complement binary number are equivalent in value.
     * @param bin1 A boolean array representing an integer in 2s complement notation. Must be non-null and contain at least 1 element.
     * @param bin2 A boolean array representing an integer in 2s complement notation. Must be non-null and contain at least 1 element.
     * @return {@code true} if both arrays evaluate to the same 2s complement integer in {@code bin1}; {@code false} otherwise.
     * <p>Examples:</p><pre><code>
     * Example 1: {@code isEqual2sCompIntValue(new boolean[]{true, false},new boolean[]{true, false}); //returns true. -2L == -2L}
     * Example 2: {@code isEqual2sCompIntValue(new boolean[]{true, false},new boolean[]{false, true}); //returns false.  -2L != 1L}
     * Example 3: {@code isEqual2sCompIntValue(new boolean[]{true, true},new boolean[]{false, true, true}); //returns false. -1L != 3L}
     * Example 4: {@code isEqual2sCompIntValue(new boolean[]{true, true},new boolean[]{true, true, true}); //returns true. -1L == -1L}
     * </code></pre>
     */
    public static boolean isEqual2sCompLongValue(boolean[] bin1, boolean[] bin2){
        return to2sCompLong(bin1) == to2sCompLong(bin2);
    }

    /**
     * Clones a new instance of a boolean array.
     * @param originalBin The boolean array to be cloned. Must not be null.
     * @return New boolean array that is equivalent to {@code originalBin}.
     * <p>Example:</p>{@code
     * isEqualBoolArray(originalBin, genClone(originalBin)); //Evaluates to true
     * originalBin != genClone(originalBin); //Evaluates to false}
     */
    public static boolean[] genClone(boolean[] originalBin){
        boolean[] r = new boolean[originalBin.length];
        for(byte i=(byte)(r.length-1);i>=0;--i){r[i] = originalBin[i];}
        return r;
    }

    /**
     * Returns a new instance of a boolean array that is a subset of another boolean array.
     * @param inclusiveStartIndex The index of the most significant bit in the subset. This is the starting index of {@code bin}
     * @param subsetLength The total number of consecutive elements to be used in {@code bin}. Also, the length of the return value
     * @param bin The array the return value will generate a subset from. Must be non-null and contain at least 1 element.
     * @return New boolean array that is a subset of {@code bin}.
     * <p>Examples:</p><pre><code>
     * {@code boolean exampleBoolArray = new boolean[]{true,false,true,false,true}; //10101 rep}
     * Example 1: {@code genSubset(1,3,exampleBoolArray); //returns {false, true, false} //index 1, 2, and 3.}
     * Example 2: {@code genSubset(1,4,exampleBoolArray); //returns {false, true, false, true} //index 1, 2, 3, and 4.}
     * Example 3: {@code genSubset(0,4,exampleBoolArray); //returns {true, false, true, false} //index 0, 1, 3, and 3.}
     * Example 4: {@code genSubset(2,3,exampleBoolArray); //returns {true, false, true} //index 2, 3, and 4.}
     * </code></pre>
     */
    public static boolean[] genSubset(int inclusiveStartIndex, int subsetLength, boolean[] bin){
        boolean[] r = new boolean[subsetLength];
        for(int i=0; i<subsetLength; ++i){r[i] = bin[inclusiveStartIndex+i];}
        return r;
    }

    /**
     * Returns a new instance of a boolean array that is a subset of another boolean array.
     * @param inclusiveStartIndex The index of the most significant bit in the subset. This is the starting index of {@code bin}
     * @param inclusiveEndIndex The index of the least significant bit in the subset. This is the starting index of {@code bin}
     * @param bin The array the return value will generate a subset from. Must be non-null and contain at least 1 element.
     * @return New boolean array that is a subset of {@code bin}. Then length of array is {@code inclusiveStartIndex - inclusiveEndIndex + 1}.
     * <p>Example:</p><pre><code>{@code boolean exampleBoolArray = new boolean[]{true,false,true,false,true}; //10101 rep}
     * Example 1: {@code genSubsetFromIndexes(1, 3, exampleBoolArray); //returns {false, true, false} //index 1, 2, and 3.}
     * Example 2: {@code genSubsetFromIndexes(1, 4, exampleBoolArray); //returns {false, true, false, true} //index 1, 2, 3, and 4.}
     * Example 3: {@code genSubsetFromIndexes(0, 4, exampleBoolArray); //returns {true, false, true, false, true} //The entire original array.}
     * Example 4: {@code genSubsetFromIndexes(2, 3, exampleBoolArray); //returns {true, false} //index 2 and 3.}
     * </code></pre>
     */
    public static boolean[] genSubsetFromIndexes(int inclusiveStartIndex, int inclusiveEndIndex, boolean[] bin){
        boolean[] r = new boolean[inclusiveEndIndex - inclusiveStartIndex + 1];
        for(int i=0; i<r.length; ++i){r[i] = bin[inclusiveStartIndex+i];}
        return r;
    }

    /**
     * Returns a new instance of a boolean array that is a left subset of another boolean array. The most significant bit as at index 0.
     * @param exclusiveRightIndex The index immediately after the last element to include in the subset.
     * Must be greater than or equal to 0 and less than or equal to the length of {@code bin}.
     * @param bin The array the return value will generate a subset from. Must be non-null and contain at least 1 element.
     * @return New boolean array that is a subset of {@code bin}. Then length of array is {@code exclusiveRightIndex}.
     * <p>Example:</p><pre><code> {@code boolean exampleBoolArray = new boolean[]{true, false, true, false, true}; //10101 rep}
     * Example 1: {@code genLeftSubset(1, exampleBoolArray); //returns {true, false} //indices 0 and 1}
     * Example 2: {@code genLeftSubset(3, exampleBoolArray); //returns {true, false, true} //indices 0, 1, and 2}
     * Example 3: {@code genLeftSubset(5, exampleBoolArray); //returns the equivalent bin array (indices 0 - 4)}
     * </code></pre>
     */
    public static boolean[] genLeftSubset(int exclusiveRightIndex, boolean[] bin){
        boolean[] r = new boolean[exclusiveRightIndex];
        for(int i=0; i<exclusiveRightIndex; ++i){r[i] = bin[i];}
        return r;
    }

    /**
     * Returns a new instance of a boolean array that is a left subset of another boolean array. The least significant bit as at index {@code bin.length - 1}.
     * @param inclusiveLeftIndex The index of the most significant bit in the subset. This is the starting index of {@code bin}
     * @param bin The array the return value will generate a subset from. Must be non-null and contain at least 1 element.
     * @return New boolean array that is a subset of {@code bin}.
     * <p>Example:</p><pre><code>
     * {@code boolean exampleBoolArray = new boolean[]{true, false, true, false, true}; //10101 rep}
     * Example 1: {@code genRightSubset(1, exampleBoolArray); //returns {false, true, false, true} //indices 1, 2, 3, and 4}
     * Example 2: {@code genRightSubset(3, exampleBoolArray); //returns {false, true} //indices 3 and 4}
     * Example 3: {@code genRightSubset(0, 5, exampleBoolArray); //returns the equivalent bin array (indices 0 - 4)}
     * </code></pre>
     */
    public static boolean[] genRightSubset(int inclusiveLeftIndex, boolean[] bin){
        boolean[] r = new boolean[bin.length-inclusiveLeftIndex];
        for(int i=0; i<r.length; ++i){r[i] = bin[inclusiveLeftIndex+i];}
        return r;
    }

    /**
     * Returns a new instance of a boolean array representing an unsigned integer.
     * @param val The value to be represented by the return boolean array. Must be non-negative.
     * @param bitSize The length of the boolean array.
     * @return New boolean array that represents the unsigned {@code val} where most significant bit is at index 0.
     * This will return an incorrect value if {@code bitSize} is too small for {@code val}.
     * <p>Example:</p><pre><code>
     * Example 1: {@code genBoolArrayFromUnsignedInt(7, 3); //returns {true, true, true}} binary: 111
     * Example 2: {@code genBoolArrayFromUnsignedInt(7, 4); //returns {false, true, true, true}} binary: 0111
     * Example 3: {@code genBoolArrayFromUnsignedInt(5, 3); //returns {true, false, true}} binary: 101
     * Example 4: {@code genBoolArrayFromUnsignedInt(0, 5); //returns {false, false, false, false, false}} binary: 00000
     * </code></pre>
     */
    public static boolean[] genBoolArrayFromUnsignedInt(int val, int bitSize){
        boolean[] r = new boolean[bitSize];
        for(byte i=(byte)(bitSize-1); i>=0; --i){
            r[i] = ((val & 1) == 1);
            val = val >>> 1;
        }
        return r;
    }

    /**
     * Returns a new instance of a boolean array representing an unsigned integer.
     * @param val The value to be represented by the return boolean array. Must be non-negative.
     * @param bitSize The length of the boolean array.
     * @return New boolean array that represents the unsigned {@code val} where most significant bit is at index 0.
     * This will return an incorrect value if {@code bitSize} is too small for {@code val}.
     * <p>Example:</p><pre><code> 
     * Example 1: {@code genBoolArrayFromUnsignedInt(7L,3); //returns {true,true,true}} binary: 111
     * Example 2: {@code genBoolArrayFromUnsignedInt(7L,4); //returns {false,true,true,true}} binary: 0111
     * Example 3: {@code genBoolArrayFromUnsignedInt(5L,3); //returns {true,false,true}} binary: 101
     * Example 4: {@code genBoolArrayFromUnsignedInt(0L,5); //returns {false,false,false,false,false}} binary: 00000
     * </code></pre>
     */
    public static boolean[] genBoolArrayFromUnsignedLong(long val, int bitSize){
        boolean[] r = new boolean[bitSize];
        for(byte i=(byte)(bitSize-1); i>=0; --i){
            r[i] = ((val & 1) == 1);
            val = val >>> 1;
        }
        return r;
    }

    /**
     * Returns a new instance of a boolean array represents the negation of an existing array.
     * @param bin The boolean array that represents a base-10 integer in 2s complement notation. This will be negated.
     * Must be non-null and contain at least 1 element.
     * @return New boolean array that represents the 2s complement negation of {@code bin}.
     * <p>Example:</p><pre><code>
     * Example 1: {@code gen2sCompNegate(new boolean[]{true, true, true}); //returns {false, false, true} from bin: 111 = -1 to bin: 001 = 1}
     * Example 2: {@code gen2sCompNegate(new boolean[]{true, true, false, false}); //returns {false, true, false, false} from bin: 1100 = -4 to bin: 0100 = 4}
     * </code></pre>
     */
    public static boolean[] gen2sCompNegate(boolean[] bin){
        boolean[] r=new boolean[bin.length];
        byte index_preserved=-1; //msb index to preserve for

        for(byte i=(byte)(bin.length-1); i>=0; --i){
            if(bin[i]){
                index_preserved=i;
                r[i]=true;
                break;
            }
            r[i]=false;
        }

        for(byte i=(byte)(index_preserved-1); i>=0; --i){r[i]=!bin[i];}
        return r;
    }

    /**
     * This mutates a specific subset of an array to a value that represents an unsigned base-10 value.
     * @param inclusiveStartIndex The index of the most significant bit in {@code bin} that will be mutated.
     * @param bitLength The length of the subset that will be mutated.
     * @param value The unsigned value that will be converted to the boolean array subset.
     * @param bin The instance that will be mutated.
     * <p>Example:</p><pre><code>
     * Example 1:
     * setSubsetUnsignedInt(
     *   1, //Starting at index 1.
     *   3, //Length 3, manipulate indices: 1, 2, and 3.
     *   7, // unsigned 7 evaulates to boolean 111 == {true, true, true}.
     *   new boolean[]{true, false, true, false, true}, //bin: 10101 index values 1, 2, and 3 will be overridden to true, true, and true respectively.
     * ); //modified value of {@code bin}: {true, true, true, true, true}.
     * 
     * Example 2:
     * setSubsetUnsignedInt(
     *   1, //Starting at index 1.
     *   3, //Length 3, manipulate indices: 1, 2, and 3.
     *   1, // unsigned 1 evaulates to boolean 001 == {false, false, true}.
     *   new boolean[]{false, false, false, false, false}, //bin: 00000 index values 1, 2, and 3 will be overridden to false, false, and true respectively.
     * ); //modified value of {@code bin}: {false, false, false, true, false}.
     * </code></pre>
     */
    public static void setSubsetUnsignedInt(
        int inclusiveStartIndex,
        int bitLength,
        int value,
        boolean[] bin
    ){
        for(int i=bitLength-1; i>=0; --i){
            bin[i+inclusiveStartIndex] = (value & 1) == 1;
            value = value >>> 1;
        }
    }

    /**
     * This mutates a specific subset of an array to a value that represents an unsigned base-10 value.
     * @param inclusiveStartIndex The index of the most significant bit in {@code bin} that will be mutated.
     * @param bitLength The length of the subset that will be mutated.
     * @param value The unsigned value that will be converted to the boolean array subset.
     * @param bin The instance that will be mutated.
     * <p>Example:</p><pre><code>
     * Example 1:
     * setSubsetUnsignedLong(
     *   1, //Starting at index 1.
     *   3, //Length 3, manipulate indices: 1, 2, and 3.
     *   7L, // unsigned 7 evaulates to boolean 111 == {true, true, true}.
     *   new boolean[]{true, false, true, false, true}, //bin: 10101 index values 1, 2, and 3 will be overridden to true, true, and true respectively.
     * ); //modified value of {@code bin}: {true, true, true, true, true}.
     * 
     * Example 2:
     * setSubsetUnsignedLong(
     *   1, //Starting at index 1.
     *   3, //Length 3, manipulate indices: 1, 2, and 3.
     *   1L, // unsigned 1 evaulates to boolean 001 == {false, false, true}.
     *   new boolean[]{false, false, false, false, false}, //bin: 00000 index values 1, 2, and 3 will be overridden to false, false, and true respectively.
     * ); //modified value of {@code bin}: {false, false, false, true, false}.
     * </code></pre>
     */
    public static void setSubsetUnsignedLong(
        int inclusiveStartIndex,
        int bitLength,
        long value,
        boolean[] bin
    ){
        for(int i=bitLength-1; i>=0; --i){
            bin[i+inclusiveStartIndex] = (value & 1) == 1;
            value = value >>> 1;
        }
    }

    /**
     * Mutates a boolean array to represent a given non-negative integer.
     * @param value The value that mutated {@code bin} will represent upon completion.
     * @param bin The boolean array that will be mutated to represent an unsigned boolean number. The most significant bit is at index 0.
     * The value will not be correct if the size of {@code bin} is not large enough to represent the {@code value}.
     * <p>Example:</p><pre><code>
     * Example 1: setUnsignedIntToBoolArray(7, new boolean[3]); sets the bin to {true, true, true}: 7 == binary 111
     * Example 2: setUnsignedIntToBoolArray(7, new boolean[5]); sets the bin to {false, false, true, true, true}: 7 == binary 00111
     * </code></pre>
     */
    public static void setUnsignedIntToBoolArray(int value, boolean[] bin){
        for(int i=bin.length-1; i>=0; --i){
            bin[i] = (value & 1) == 1;
            value = value >>> 1;
        }
    }

    /**
     * Mutates a boolean array to represent a given non-negative integer.
     * @param value The value that mutated {@code bin} will represent upon completion.
     * @param bin The boolean array that will be mutated to represent an unsigned boolean number. The most significant bit is at index 0.
     * The value will not be correct if the size of {@code bin} is not large enough to represent the {@code value}.
     * <p>Example:</p><pre><code>
     * Example 1: setUnsignedLongToBoolArray(7L, new boolean[3]); sets the bin to {true, true, true}: 7 == binary 111
     * Example 2: setUnsignedLongToBoolArray(7L, new boolean[5]); sets the bin to {false, false, true, true, true}: 7 == binary 00111
     * </code></pre>
     */
    public static void setUnsignedLongToBoolArray(long value, boolean[] boolArray){
        for(int i=boolArray.length-1; i>=0; --i){
            boolArray[i] = (value & 1) == 1;
            value = value >>> 1;
        }
    }

    /**
     * Mutates a boolean array representing a 2's complement integer to its negative value.
     * @param bin The boolean array that will be mutated to represent its negated value.
     * <p>Example:</p><pre><code>
     * boolean[] bin1 = new boolean[]{true, true, true}; //binary 111 == -1
     * boolean[] bin2 = new boolean[]{false, false, true}; //binary 001 == 1
     * boolean[] bin3 = new boolean[]{false, true, false, true}; //binary 0101 == 5
     * boolean[] bin4 = new boolean[]{true, false, true, true}; //binary 1011 == -5
     * boolean[] bin5 = new boolean[]{false, false}; //binary 00 == 0
     * Example 1: set2sCompNegate(bin1); sets from 111 to 001. sets from -1 to 1
     * Example 2: set2sCompNegate(bin2); sets from 001 to 111. sets from 1 to -1
     * Example 3: set2sCompNegate(bin3); sets from 0101 to 1011. sets from 5 to -5
     * Example 4: set2sCompNegate(bin4); sets from 1011 to 0101. sets from -5 to 5
     * Example 5: set2sCompNegate(bin5); sets from 00 to 00. sets from 0 to 0
     * </code></pre>
     */
    public static void set2sCompNegate(boolean[] bin){
        int index_preserved=-1; //msb index to preserve for

        for(int i=bin.length-1; i>=0; --i){
            if(bin[i]){
                index_preserved=i;
                break;
            }
        }

        for(int i=index_preserved-1; i>=0; --i){bin[i]=!bin[i];}
    }

    public static boolean[] genUnsignedIntToMinimumBoolArray(int value){
        return genBoolArrayFromUnsignedInt(
            value,
            (byte)getMinimumNumberOfBits(value)
        );
    }

    public static boolean[] genConcatenatedBoolArrays(boolean[]... binArrays){
        int sizeR=0;
        int intIndex=0;

        for(boolean[] binArr : binArrays){sizeR+=binArr.length;}

        boolean[] r = new boolean[sizeR];
        for(boolean[] binArr : binArrays){
            for(int i=0; i<binArr.length; ++i, ++intIndex){
                r[intIndex]=binArr[i];
            }
        }
        return r;
    }

    public static boolean[] genConcatenatedBoolArrays(boolean[][]... bin3DArray){
        int sizeR=0;
        int intIndex=0;

        for(boolean[][] bin2DArray : bin3DArray){
            for(boolean[] binArr : bin2DArray) sizeR+=binArr.length;
        }

        boolean[] r = new boolean[sizeR];
        for(boolean[][] bin2DArray : bin3DArray){
            for(boolean[] binArr : bin2DArray){
                for(int i=0; i<binArr.length; ++i, ++intIndex){
                    r[intIndex]=binArr[i];
                }
            }
        }
        return r;
    }

    public static boolean[] genBoolArrayFrom8BitCharString(String asciiString){
        byte EIGHT_BITS = (byte)8;

        boolean[] r = new boolean[EIGHT_BITS*asciiString.length()];
        boolean[] binChar = new boolean[EIGHT_BITS];

        for(int i=0; i<asciiString.length(); ++i){
            setUnsignedIntToBoolArray((byte)asciiString.charAt(i),binChar);
            byte shift_i = (byte)(8*i);
            for(byte j=0; j<binChar.length; ++j){
                r[shift_i + j] = binChar[j];
            }
        }
        return r;
    }

    public static String genStringFrom8BitBoolCharRep(boolean[] EightBitCharBool){
        StringBuilder strBldr = new StringBuilder();
        boolean[] charBin = new boolean[8];
        int fullCharCount = (EightBitCharBool.length >>> 3); // Full 8 bits, the last character may not be 8 bits

        for(int i=0; i<fullCharCount; ++i){
            for(int j=7; j>=0; --j){
                charBin[j] = EightBitCharBool[i*8+j];
            }
            strBldr.append((char)toUnsignedInt(charBin));
        }

        return strBldr.toString();
    }
}
