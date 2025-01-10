/**
 * @author Bruce Lamb
 * @since 09 JAN 2025
 */
package tradedatacorp.smelter.lexical.binary;

public class BinaryTools{
    public static final double LOG10_BASE2 = Math.log10(2);

    //Gets the minimum number of bits to represent this unsigned value
    public static int getMinimumNumberOfBits(int positiveInteger){return (int)Math.ceil(Math.log10(positiveInteger)/LOG10_BASE2);}

    public static int toUnsignedInt(boolean[] bin){
        int r=(bin[0] ? 1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = 2*r + (bin[i] ? 1 : 0);}
        return r;
    }

    public static long toUnsignedLong(boolean[] bin){
        long r=(bin[0] ? 1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = 2*r + (bin[i] ? 1 : 0);}
        return r;
    }

    public static boolean[] unsignedIntToBoolArray(int val, byte bit_size){
        boolean[] r = new boolean[bit_size];
        for(byte i=(byte)(bit_size-1); i>=0; --i){
            r[i] = ((val & 1) == 1 ? true : false);
            val = val >>> 1;
        }
        return r;
    }

    public static boolean[] unsignedIntToMinimumBoolArray(int value){
        return unsignedIntToBoolArray(
            value,
            (byte)getMinimumNumberOfBits(value)
        );
    }

    public static boolean[] stringTo8BitCharArray(String asciiString){
        boolean[] r = new boolean[8*asciiString.length()];
        byte EIGHT_BITS = (byte)8;
        for(int i=0; i<asciiString.length(); ++i){
            boolean[] binChar = unsignedIntToBoolArray((byte)asciiString.charAt(i),EIGHT_BITS);
            byte eight_i = (byte)(8*i);
            for(byte j=0;j<binChar.length;++j){
                r[eight_i + j] = binChar[j];
            }
        }
        return r;
    }

    public static void setIntToBoolArray(int val, boolean[] boolArray){
        for(byte i=(byte)(boolArray.length-1); i>=0; --i){
            boolArray[i] = ((val & 1) == 1 ? true : false);
            val = val >>> 1;
        }
    }

    public static boolean[] longToBoolArray(long val, byte bit_size){
        boolean[] r = new boolean[bit_size];
        for(byte i=(byte)(bit_size-1); i>=0; --i){
            r[i] = ((val & 1) == 1 ? true : false);
            val = val >>> 1;
        }
        return r;
    }
}