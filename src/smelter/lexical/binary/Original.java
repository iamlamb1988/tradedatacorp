/**
 * @author Bruce Lamb
 * @since 20 JAN 2025
 */
package tradedatacorp.smelter.lexical.binary;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

import java.util.Collection;
import java.util.ArrayList;

//This is the first BinaryLexical in design
//Thhe headers generated will be dependant on the collection of sticks to be written
public class Original implements BinaryLexical<StickDouble>{
    private String interval;
    private ArrayList<StickDouble> stickList;

    private int h2_total_len;
    private int h_total_len;

    //Cache/memoization variables for speedy reference
    private int base10PriceMaxFractionDigit;  //used internally as a cache for splitWholeFraction functions
    private int base10VolumeMaxFractionDigit; //used internally as a cache for splitWholeFraction functions

    //Binary Header
    boolean[][] header;

    // Binary Fixed field bit lengths
    public static final byte H1_BYID_LEN = 1;
    public static final byte H1_INT_LEN = 25;
    public static final byte H1_CT_LEN_LEN = 26;
    public static final byte H1_DATA_LEN_LEN = 9;
    public static final byte H1_H_GAP_LEN_LEN = 3;
    public static final byte H1_UTC_LEN_LEN = 6;
    public static final byte H1_PW_LEN_LEN = 5; //Should be MUCH BIGGER
    public static final byte H1_PF_LEN_LEN = 4; //Should be 50 bits to support 15 decimal points
    public static final byte H1_VW_LEN_LEN = 5; //Should be MUCH BIGGER
    public static final byte H1_VF_LEN_LEN = 4; //Should be 50 bits to support 15 decimal points
    public static final byte H1_SYM_LEN_LEN = 7;

    public final int H1_TOTAL_LEN = 
        H1_BYID_LEN +
        H1_INT_LEN +
        H1_CT_LEN_LEN +
        H1_DATA_LEN_LEN +
        H1_H_GAP_LEN_LEN +
        H1_UTC_LEN_LEN +
        H1_PW_LEN_LEN + 
        H1_PF_LEN_LEN +
        H1_VW_LEN_LEN + 
        H1_VF_LEN_LEN +
        H1_SYM_LEN_LEN;

    //Binary Lexical H1
    private boolean[] h1_byid;
    private boolean[] h1_int;
    private boolean[] h1_ct_len;
    private boolean[] h1_data_len;
    private boolean[] h1_h_gap_len;

    private boolean[] h1_utc_len; //UTC bit length;
    private boolean[] h1_pw_len;  //price (ohlc) bit length
    private boolean[] h1_pf_len;  //price (ohlc) bit length
    private boolean[] h1_vw_len;  //volume bit length
    private boolean[] h1_vf_len;  //volume bit length
    private boolean[] h1_sym_len;

    //Binary Lexical H2
    private boolean[] h2_sym;
    private boolean[] h2_data_ct;
    private boolean[] h2_h_gap;

    //Translated Lexical H1
    private boolean t_h1_byid;
    private int t_h1_int;
    private int t_h1_data_len;
    private int t_h1_ct_len;
    private byte t_h1_h_gap_len;

    private byte t_h1_utc_len;
    private byte t_h1_pw_len; //price (ohlc)
    private byte t_h1_pf_len;
    private byte t_h1_vw_len; //volume
    private byte t_h1_vf_len;
    private byte t_h1_sym_len;

    //Translated Lexical H2
    private String t_h2_sym;
    private int t_h2_data_ct;

    //Constructor
    private void constructHeaderFromTranslatedValues(
        boolean T_byid,
        String T_int,
        byte T_h_gap_len,
        byte T_utc_len,
        byte T_pw_len,
        byte T_pf_len,
        byte T_vw_len,
        byte T_vf_len,
        String T_sym
    ){
        header = new boolean[14][];

        //Header 0: by_id
        t_h1_byid = T_byid;
        h1_byid = new boolean[]{T_byid};
        header[0] = h1_byid;

        //Header 1: int
        interval = T_int; //Need strictly parse different String formats in the future
        t_h1_int = Integer.parseUnsignedInt(interval); //interval will be evaluated to correct integer in the future
        h1_int = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_int,H1_INT_LEN);
        header[1] = h1_int;

        //Header 2: ct_len Always empty upon construction therefor 0
        t_h1_ct_len = 0;
        h1_ct_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_ct_len,H1_CT_LEN_LEN);
        header[2] = h1_ct_len;

        //Header 3: data_len
        t_h1_data_len = T_utc_len + 4*(T_pw_len + T_pf_len) + T_vw_len + T_vf_len;
        h1_data_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_data_len,H1_DATA_LEN_LEN);
        header[3] = h1_data_len;

        //Header 4: h_gap_len
        t_h1_h_gap_len = T_h_gap_len;
        h1_h_gap_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_h_gap_len,H1_H_GAP_LEN_LEN);
        header[4] = h1_h_gap_len;

        //Header 5: utc_len
        t_h1_utc_len = T_utc_len;
        h1_utc_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_utc_len,H1_UTC_LEN_LEN);
        header[5] = h1_utc_len;

        //Header 6: pw_len
        t_h1_pw_len = T_pw_len;
        h1_pw_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_pw_len,H1_PW_LEN_LEN);
        header[6] = h1_pw_len;

        //Header 7: pf_len
        t_h1_pf_len = T_pf_len;
        h1_pf_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_pf_len,H1_PF_LEN_LEN);
        header[7] = h1_pf_len;

        //Header 8: vw_len
        t_h1_vw_len = T_vw_len;
        h1_vw_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_vw_len,H1_VW_LEN_LEN);
        header[8] = h1_vw_len;

        //Header 9: vf_len
        t_h1_vf_len = T_vf_len;
        h1_vf_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_vf_len,H1_VF_LEN_LEN);
        header[9] = h1_vf_len;

        base10PriceMaxFractionDigit  = (int)Math.ceil(Math.log10(Math.pow(2,t_h1_pf_len)-1));
        base10VolumeMaxFractionDigit = (int)Math.ceil(Math.log10(Math.pow(2,t_h1_vf_len)-1));

        //Header 10: sym_len
        t_h1_sym_len = (byte)(T_sym.length() << 3);
        h1_sym_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_sym_len,H1_SYM_LEN_LEN);
        header[10] = h1_sym_len;

        //Header 11: sym
        t_h2_sym = T_sym;
        h2_sym = BinaryTools.genBoolArrayFrom8BitCharString(t_h2_sym);
        header[11] = h2_sym;

        //Header 12: data_ct
        t_h2_data_ct = 0; //Initially 0 datapoints
        h2_data_ct = new boolean[]{false};
        header[12] = h2_data_ct;

        //Header 13: gap
        h2_h_gap = header[13] = BinaryTools.genBoolArrayFromUnsignedInt(0,t_h1_h_gap_len);

        h2_total_len = 
            h2_sym.length + 
            h2_data_ct.length +
            h2_h_gap.length;

        h_total_len = H1_TOTAL_LEN + h2_total_len;
    }

    public Original(String symbol, String interval){
        //Calculate Gap
        int headerExceptGapLength = 
            H1_TOTAL_LEN + 
            (symbol.length() << 3) + 
            1;
        int remainder = headerExceptGapLength%8;
        byte T_gap;


        if(remainder != 0) T_gap = (byte)(8-remainder);
        else T_gap=(byte)0;

        constructHeaderFromTranslatedValues(
            false,// boolean T_byid,
            interval,// String T_int,
            T_gap,// byte T_h_gap_len,
            (byte)44,// byte T_utc_len,
            (byte)31,// byte T_pw_len,
            (byte)15,// byte T_pf_len,
            (byte)31,// byte T_vw_len,
            (byte)15,// byte T_vf_len,
            symbol// String T_sym
        );
    }

    // BinaryLexical Overrides
    //Get Binary Header
    @Override
    public boolean[][] getBinaryHeader(){
        boolean[][] clone = new boolean[header.length][];
        int headerIndex = 0;

        for(boolean[] binField : header){
            clone[headerIndex] = BinaryTools.genClone(binField);
            ++headerIndex;
        }

        return clone;
    }

    @Override
    public boolean[] getBinaryHeaderFlat(){return BinaryTools.genConcatenatedBoolArrays(header);}

    //Get Binary Data from Data instances
    @Override
    public boolean[][] getBinaryData(StickDouble singleData){
        boolean[][] binData = new boolean[11][];
        int[] tmpWholeFractionInts = new int[3];

        setBinaryDataStick(singleData,binData,tmpWholeFractionInts);

        return binData;
    }

    @Override
    public boolean[] getBinaryDataFlat(StickDouble singleData){
        boolean[] binData = new boolean[t_h1_data_len];
        int[] tmpWholeFractionInts = new int[3];

        setBinaryDataStickFlat(singleData, binData, tmpWholeFractionInts, 0);

        return binData;
    }

    @Override
    public boolean[][][] getBinaryDataPoints(StickDouble[] dataArray){
        boolean[][][] r = new boolean[dataArray.length][][];
        boolean[][] binData;
        int[] tmpWholeFractionInts = new int[3];

        int rIndex=0;
        for(StickDouble singleData : dataArray){
            binData = new boolean[11][]; //This is a singular element of r

            setBinaryDataStick(singleData,binData,tmpWholeFractionInts);

            r[rIndex] = binData;
            ++rIndex;
        }

        return r;
    }

    public boolean[][][] getBinaryDataPoints(Collection dataCollection){return null;}

    @Override
    public boolean[] getBinaryDataPointsFlat(StickDouble[] dataArray){
        boolean[] r = new boolean[t_h1_data_len * dataArray.length];
        int[] tmpWholeFractionInts = new int[3];
        int nextIndex = 0;

        for(StickDouble singleData : dataArray){
            setBinaryDataStickFlat(singleData, r, tmpWholeFractionInts, nextIndex);
            nextIndex += t_h1_data_len;
        }

        return r;
    }

    public boolean[] getBinaryDataPointsFlat(Collection dataCollection){return null;}

    //Get Data instance from Binary
    @Override
    public StickDouble getRefinedData(boolean[][] singleBinaryData){
        //Read out each chunk based on data header lengths
        int tmpWhole,
            tmpFraction;

        long utc = BinaryTools.toUnsignedLong(singleBinaryData[0]);

        tmpWhole = BinaryTools.toUnsignedInt(singleBinaryData[1]);
        tmpFraction = BinaryTools.toUnsignedInt(singleBinaryData[2]);
        double open = tmpWhole + tmpFraction/Math.pow(10,base10PriceMaxFractionDigit);

        tmpWhole = BinaryTools.toUnsignedInt(singleBinaryData[3]);
        tmpFraction = BinaryTools.toUnsignedInt(singleBinaryData[4]);
        double high = tmpWhole + tmpFraction/Math.pow(10,base10PriceMaxFractionDigit);

        tmpWhole = BinaryTools.toUnsignedInt(singleBinaryData[5]);
        tmpFraction = BinaryTools.toUnsignedInt(singleBinaryData[6]);
        double low = tmpWhole + tmpFraction/Math.pow(10,base10PriceMaxFractionDigit);

        tmpWhole = BinaryTools.toUnsignedInt(singleBinaryData[7]);
        tmpFraction = BinaryTools.toUnsignedInt(singleBinaryData[8]);
        double close = tmpWhole + tmpFraction/Math.pow(10,base10PriceMaxFractionDigit);

        tmpWhole = BinaryTools.toUnsignedInt(singleBinaryData[9]);
        tmpFraction = BinaryTools.toUnsignedInt(singleBinaryData[10]);
        double volume = tmpWhole + tmpFraction/Math.pow(10,base10VolumeMaxFractionDigit);

        return new CandleStickFixedDouble(utc,open,high,low,close,volume);
    }

    public StickDouble getRefinedDataFlat(boolean[] singleFlatBinaryData){return null;}
    public StickDouble[] getRefinedDataArray(boolean[][][] BinaryDataArray){return null;}
    public StickDouble[] getRefinedDataArrayFlat(boolean[] BinaryFlatDataArray){return null;}

    // Original methods
    //Get methods
    public boolean[][] genBinaryHeader1(){
        boolean[][] h1=new boolean[11][];

        h1[0] = BinaryTools.genClone(h1_byid); //h1_biid
        h1[1] = BinaryTools.genClone(h1_int); // h1_int
        h1[2] = BinaryTools.genClone(h1_ct_len); // h1_ct_len
        h1[3] = BinaryTools.genClone(h1_data_len); // h1_data_len
        h1[4] = BinaryTools.genClone(h1_h_gap_len); // h1_h_gap_len
        h1[5] = BinaryTools.genClone(h1_utc_len); //h1_utc_len
        h1[6] = BinaryTools.genClone(h1_pw_len); // h1_pw_len
        h1[7] = BinaryTools.genClone(h1_pf_len); // h1_pf_len
        h1[8] = BinaryTools.genClone(h1_vw_len); // h1_vw_len
        h1[9] = BinaryTools.genClone(h1_vf_len); // h1_vf_len
        h1[10] = BinaryTools.genClone(h1_sym_len); // h1_sym_len

        return h1;
    }

    public boolean[][] genBinaryHeader2(){
        boolean[][] h2=new boolean[3][];

        h2[0] = BinaryTools.genClone(h2_sym); //h2_sym
        h2[1] = BinaryTools.genClone(h2_data_ct); //h2_data_ct
        h2[2] = BinaryTools.genClone(h2_h_gap); //h2_h_gap

        return h2;
    }

    //The number of digits that represent the number of digits needed to represent the highest possible value for the bit length
    public int getBase10PriceDigits(){return base10PriceMaxFractionDigit;}
    public int getBase10VolumeDigits(){return base10VolumeMaxFractionDigit;}

    //Set methods
    //This will need to be sped up
    public static void splitWholeFraction(double value, int maxDigits, int[] valueParts){
        int whole = (int)Math.abs(value);
        valueParts[0] = whole;

        //Initial fraction digit
        int fraction = (int)Math.round(Math.pow(10,maxDigits)*(value - whole));
        valueParts[1] = fraction;
        valueParts[2] = maxDigits;
    }

    public static void splitWholeFractionTrim(double value, int maxDigits, int[] valueParts){
        int whole = (int)Math.abs(value);
        valueParts[0] = whole;

        //Initial fraction digit
        int fraction = (int)Math.round(Math.pow(10,maxDigits)*(value - whole));
        int trimmedFraction = fraction;
        while(maxDigits>0){
            if(trimmedFraction % 10 == 0){
                trimmedFraction /= 10;
                --maxDigits;
            }
            else break;
        }
        valueParts[1] = trimmedFraction;
        valueParts[2] = maxDigits;
    }

    private void setBinaryDataStick(StickDouble stick, boolean[][] binStickToBeSet, int[] tmp3size_WholeFractionInts){
        binStickToBeSet[0] = BinaryTools.genBoolArrayFromUnsignedLong(stick.getUTC(),t_h1_utc_len); //UTC

        splitWholeFraction(stick.getO(),base10PriceMaxFractionDigit,tmp3size_WholeFractionInts);
        binStickToBeSet[1] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[0],t_h1_pw_len);  //Open Whole
        binStickToBeSet[2] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[1],t_h1_pf_len);  //Open Fraction

        splitWholeFraction(stick.getH(),base10PriceMaxFractionDigit,tmp3size_WholeFractionInts);
        binStickToBeSet[3] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[0],t_h1_pw_len);  //High Whole
        binStickToBeSet[4] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[1],t_h1_pf_len);  //High Fraction

        splitWholeFraction(stick.getL(),base10PriceMaxFractionDigit,tmp3size_WholeFractionInts);
        binStickToBeSet[5] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[0],t_h1_pw_len);  //Low Whole
        binStickToBeSet[6] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[1],t_h1_pf_len);  //Low Fraction

        splitWholeFraction(stick.getC(),base10PriceMaxFractionDigit,tmp3size_WholeFractionInts);
        binStickToBeSet[7] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[0],t_h1_pw_len);  //Close Whole
        binStickToBeSet[8] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[1],t_h1_pf_len);  //Close Fraction

        splitWholeFraction(stick.getV(),base10VolumeMaxFractionDigit,tmp3size_WholeFractionInts);
        binStickToBeSet[9] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[0],t_h1_vw_len);  //Volume Whole
        binStickToBeSet[10] = BinaryTools.genBoolArrayFromUnsignedInt(tmp3size_WholeFractionInts[1],t_h1_vf_len); //Volume Fraction
    }

    private void setBinaryDataStickFlat(StickDouble stick, boolean[] flatBinSticks, int[] tmp3size_WholeFraction, int startIndex){
        BinaryTools.setSubsetUnsignedLong(startIndex,t_h1_utc_len,stick.getUTC(),flatBinSticks);
        startIndex+=t_h1_utc_len;

        //Open
        splitWholeFraction(stick.getO(),base10PriceMaxFractionDigit,tmp3size_WholeFraction);
        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_pw_len,tmp3size_WholeFraction[0],flatBinSticks);
        startIndex+=t_h1_pw_len;

        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_pf_len,tmp3size_WholeFraction[1],flatBinSticks);
        startIndex+=t_h1_pf_len;

        //High
        splitWholeFraction(stick.getH(),base10PriceMaxFractionDigit,tmp3size_WholeFraction);
        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_pw_len,tmp3size_WholeFraction[0],flatBinSticks);
        startIndex+=t_h1_pw_len;

        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_pf_len,tmp3size_WholeFraction[1],flatBinSticks);
        startIndex+=t_h1_pf_len;

        //Low
        splitWholeFraction(stick.getL(),base10PriceMaxFractionDigit,tmp3size_WholeFraction);
        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_pw_len,tmp3size_WholeFraction[0],flatBinSticks);
        startIndex+=t_h1_pw_len;

        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_pf_len,tmp3size_WholeFraction[1],flatBinSticks);
        startIndex+=t_h1_pf_len;

        //Close
        splitWholeFraction(stick.getC(),base10PriceMaxFractionDigit,tmp3size_WholeFraction);
        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_pw_len,tmp3size_WholeFraction[0],flatBinSticks);
        startIndex+=t_h1_pw_len;

        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_pf_len,tmp3size_WholeFraction[1],flatBinSticks);
        startIndex+=t_h1_pf_len;

        //Volume
        splitWholeFraction(stick.getV(),base10VolumeMaxFractionDigit,tmp3size_WholeFraction);
        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_vw_len,tmp3size_WholeFraction[0],flatBinSticks);
        startIndex+=t_h1_vw_len;

        BinaryTools.setSubsetUnsignedInt(startIndex,t_h1_vf_len,tmp3size_WholeFraction[1],flatBinSticks);
        startIndex+=t_h1_vf_len;
    }

    public int getHeaderBitLength(){return h_total_len;}
    public int getHeader2BitLength(){return h2_total_len;}

    public String getSymbol(){return t_h2_sym;}
    public String getInterval(){return interval;}

    public boolean getByID(){return t_h1_byid;}
    public int getDataBitLength(){return t_h1_data_len;}
}