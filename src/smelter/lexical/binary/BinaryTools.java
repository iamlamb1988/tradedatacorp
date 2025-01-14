/**
 * @author Bruce Lamb
 * @since 09 JAN 2025
 */
package tradedatacorp.smelter.lexical.binary;

public class BinaryTools{
    public static final byte ZED=(byte)0;
    public static final byte ONE=(byte)1;
    public static final byte NEG_ONE=(byte)-1;
    public static final double LOG10_BASE2 = Math.log10(2);

    //Gets the minimum number of bits to represent this unsigned value
    public static int getMinimumNumberOfBits(int positiveInteger){return (int)Math.ceil(Math.log10(positiveInteger)/LOG10_BASE2);}

    public static int toUnsignedInt(boolean[] bin){
        int r=(bin[0] ? 1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    public static int to2sCompInt(boolean[] bin){
        int r=(bin[0] ? -1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    public static long toUnsignedLong(boolean[] bin){
        long r=(bin[0] ? 1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

    public static long to2sCompLong(boolean[] bin){
        long r=(bin[0] ? -1 : 0);
        for(byte i=(byte)1;i<bin.length;++i){r = (r << 1) | (bin[i] ? 1 : 0);}
        return r;
    }

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
        for(byte i=(byte)(r.length-1);i>=0;--i){r[i]=originalBin[i];}
        return r;
    }

    public static boolean[] genBoolArrayFromUnsignedInt(int val, byte bit_size){
        boolean[] r = new boolean[bit_size];
        for(byte i=(byte)(bit_size-1); i>=0; --i){
            r[i] = ((val & 1) == 1 ? true : false);
            val = val >>> 1;
        }
        return r;
    }

    public static boolean[] genBoolArrayFromUnsignedLong(long val, byte bit_size){
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

    public static void setUnsignedIntToBoolArray(int val, boolean[] boolArray){
        for(byte i=(byte)(boolArray.length-1); i>=0; --i){
            boolArray[i] = ((val & 1) == 1 ? true : false);
            val = val >>> 1;
        }
    }

    public static void setUnsignedLongToBoolArray(long val, boolean[] boolArray){
        for(byte i=(byte)(boolArray.length-1); i>=0; --i){
            boolArray[i] = ((val & 1) == 1 ? true : false);
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

    public static boolean[] stringTo8BitCharArray(String asciiString){
        boolean[] r = new boolean[8*asciiString.length()];
        byte EIGHT_BITS = (byte)8;
        for(int i=0; i<asciiString.length(); ++i){
            boolean[] binChar = genBoolArrayFromUnsignedInt((byte)asciiString.charAt(i),EIGHT_BITS);
            byte eight_i = (byte)(8*i);
            for(byte j=0;j<binChar.length;++j){
                r[eight_i + j] = binChar[j];
            }
        }
        return r;
    }
}