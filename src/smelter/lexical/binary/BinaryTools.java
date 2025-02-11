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
     * Example 1: {@code getMinimumNumberOfBits(3); // returns 2. binary 11 => 3, minimum of 2 bits represents 3.}
     * Example 2: {@code getMinimumNumberOfBits(5); // returns 3. binary 101 => 5, minimum of 3 bits represents 5.}
     * Example 3: {@code getMinimumNumberOfBits(0); // returns 1. binary 0 => 0, minimum of 1 bit represents 0.}
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
     * Example 1: {@code toUnsignedInt(new boolean[]{true,true,true}); //returns 7. bool array represents 111}
     * Example 2: {@code toUnsignedInt(new boolean[]{true,true,false}); //returns 6. bool array represents 110}
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
     * Example 1: {@code to2sCompInt(new boolean[]{true,true,true}); //returns -1. bool array represents 111}
     * Example 2: {@code to2sCompInt(new boolean[]{false,true,true,true}); //returns 7. bool array represents 0111}
     * Example 3: {@code to2sCompInt(new boolean[]{true,true,false}); //returns -2. bool array represents 110}
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
     * Example 1: {@code toUnsignedLong(new boolean[]{true,true,true}); //returns 7L. bool array represents 111}
     * Example 2: {@code toUnsignedLong(new boolean[]{true,true,false}); //returns 6L. bool array represents 110}
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
     * Example 1: {@code to2sCompLong(new boolean[]{true,true,true}); //returns -1L. bool array represents 111}
     * Example 2: {@code to2sCompLong(new boolean[]{false,true,true,true}); //returns 7L. bool array represents 0111}
     * Example 3: {@code to2sCompLong(new boolean[]{true,true,false}); //returns -2L. bool array represents 110}
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
     * Example: {@code boolean exampleBoolArray = new boolean[]{true,false,true,false,true}; //10101 rep}
     * Example 1: {@code toUnsignedIntFromBoolSubset(exampleBoolArray,1,3); //returns 2. subset array: 010, indexes 1, 2, and 3.}
     * Example 2: {@code toUnsignedIntFromBoolSubset(exampleBoolArray,1,4); //returns 5. subset array: 0101, indexes 1, 2, 3, and 4.}
     * Example 3: {@code toUnsignedIntFromBoolSubset(exampleBoolArray,0,4); //returns 10. subset array: 1010, indexes 0, 1, 2, and 3.}
     * Example 4: {@code toUnsignedIntFromBoolSubset(exampleBoolArray,2,3); //returns 5. subset array: 101, indexes 2, 3, and 4}
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
     * Example: {@code boolean exampleBoolArray = new boolean[]{true,false,true,false,true}; //10101 rep}
     * Example 1: {@code toUnsignedLongFromBoolSubset(exampleBoolArray,1,3); //returns 2L. subset array: 010, indexes 1, 2, and 3.}
     * Example 2: {@code toUnsignedLongFromBoolSubset(exampleBoolArray,1,4); //returns 5L. subset array: 0101, indexes 1, 2, 3, and 4.}
     * Example 3: {@code toUnsignedLongFromBoolSubset(exampleBoolArray,0,4); //returns 10L. subset array: 1010, indexes 0, 1, 2, and 3.}
     * Example 4: {@code toUnsignedLongFromBoolSubset(exampleBoolArray,2,3); //returns 5L. subset array: 101, indexes 2, 3, and 4}
     */
    public static long toUnsignedLongFromBoolSubset(boolean[] bin, int startIndex, int length){
        long r=(bin[startIndex] ? 1 : 0);
        int maxIndex = startIndex + length;
        for(int i=startIndex+1; i<maxIndex; ++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    /**
     * Determines if two boolean arrays are equivalent
     * @param bin1 A boolean array.
     * @param bin2 A boolean array.
     * @return {@code true} if both arrays have the same length and every element in {@code bin1} 
     * is equal to the corresponding element in {@code bin2}; {@code false} otherwise.
     * Example 1: {@code isEqualBoolArray(new boolean[]{true,false},new boolean[]{true,false}); //returns true.}
     * Example 2: {@code isEqualBoolArray(new boolean[]{true,false},new boolean[]{false,true}); //returns false.}
     * Example 3: {@code isEqualBoolArray(new boolean[]{true,true},new boolean[]{true,true,true}); //returns false.}
     */
    public static boolean isEqualBoolArray(boolean[] bin1, boolean[] bin2){
        if(bin1.length != bin2.length) return false;
        for(int i=0; i<bin1.length; ++i){
            if(bin1[i] != bin2[i]) return false;
        }
        return true;
    }

    public static boolean isEqualUnsignedIntValue(boolean[] bin1, boolean[] bin2){
        return toUnsignedInt(bin1) == toUnsignedInt(bin2);
    }

    public static boolean isEqual2sCompIntValue(boolean[] bin1, boolean[] bin2){
        return to2sCompInt(bin1) == to2sCompInt(bin2);
    }

    public static boolean isEqualUnsignedLongValue(boolean[] bin1, boolean[] bin2){
        return toUnsignedLong(bin1) == toUnsignedLong(bin2);
    }

    public static boolean isEqual2sCompLongValue(boolean[] bin1, boolean[] bin2){
        return to2sCompLong(bin1) == to2sCompLong(bin2);
    }

    public static boolean[] genClone(boolean[] originalBin){
        boolean[] r = new boolean[originalBin.length];
        for(byte i=(byte)(r.length-1);i>=0;--i){r[i] = originalBin[i];}
        return r;
    }

    public static boolean[] genSubset(int inclusiveStartIndex, int subsetLength, boolean[] bin){
        boolean[] r = new boolean[subsetLength];
        for(int i=0; i<subsetLength; ++i){r[i] = bin[inclusiveStartIndex+i];}
        return r;
    }

    public static boolean[] genSubsetFromIndexes(int inclusiveStartIndex, int inclusiveEndIndex, boolean[] bin){
        boolean[] r = new boolean[inclusiveEndIndex - inclusiveStartIndex + 1];
        for(int i=0; i<r.length; ++i){r[i] = bin[inclusiveStartIndex+i];}
        return r;
    }

    public static boolean[] genLeftSubset(int exclusiveRightIndex, boolean[] bin){
        boolean[] r = new boolean[exclusiveRightIndex];
        for(int i=0; i<exclusiveRightIndex; ++i){r[i] = bin[i];}
        return r;
    }

    public static boolean[] genRightSubset(int inclusiveLeftIndex, boolean[] bin){
        boolean[] r = new boolean[bin.length-inclusiveLeftIndex];
        for(int i=0; i<r.length; ++i){r[i] = bin[inclusiveLeftIndex+i];}
        return r;
    }

    public static boolean[] genBoolArrayFromUnsignedInt(int val, int bit_size){
        boolean[] r = new boolean[bit_size];
        for(byte i=(byte)(bit_size-1); i>=0; --i){
            r[i] = ((val & 1) == 1 ? true : false);
            val = val >>> 1;
        }
        return r;
    }

    public static boolean[] genBoolArrayFromUnsignedLong(long val, int bit_size){
        boolean[] r = new boolean[bit_size];
        for(byte i=(byte)(bit_size-1); i>=0; --i){
            r[i] = ((val & 1) == 1 ? true : false);
            val = val >>> 1;
        }
        return r;
    }

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

    public static void setSubsetUnsignedInt(
        int inclusiveStartIndex,
        int bitLength,
        int value,
        boolean[] bin
    ){
        for(int i=bitLength-1; i>=0; --i){
            bin[i+inclusiveStartIndex] = ((value & 1) == 1 ? true : false);
            value = value >>> 1;
        }
    }

    public static void setSubsetUnsignedLong(
        int inclusiveStartIndex,
        int bitLength,
        long value,
        boolean[] bin
    ){
        for(int i=bitLength-1; i>=0; --i){
            bin[i+inclusiveStartIndex] = ((value & 1) == 1 ? true : false);
            value = value >>> 1;
        }
    }

    public static void setUnsignedIntToBoolArray(int val, boolean[] boolArray){
        for(int i=boolArray.length-1; i>=0; --i){
            boolArray[i] = (val & 1) == 1;
            val = val >>> 1;
        }
    }

    public static void setUnsignedLongToBoolArray(long val, boolean[] boolArray){
        for(int i=boolArray.length-1; i>=0; --i){
            boolArray[i] = (val & 1) == 1;
            val = val >>> 1;
        }
    }

    public static void set2sCompNegate(boolean[] bin){
        byte index_preserved=-1; //msb index to preserve for

        for(byte i=(byte)(bin.length-1); i>=0; --i){
            if(bin[i]){
                index_preserved=i;
                break;
            }
        }

        for(byte i=(byte)(index_preserved-1); i>=0; --i){bin[i]=!bin[i];}
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
