/**
 * @author Bruce Lamb
 * @since 20 JAN 2025
 */
package tradedatacorp.smelter.lexical.binary;

import tradedatacorp.item.stick.primitive.StickDouble;

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
    public static final byte H1_PW_LEN_LEN = 4; //Should be MUCH BIGGER
    public static final byte H1_PF_LEN_LEN = 5; //Should be 50 bits to support 15 decimal points
    public static final byte H1_VW_LEN_LEN = 4; //Should be MUCH BIGGER
    public static final byte H1_VF_LEN_LEN = 5; //Should be 50 bits to support 15 decimal points
    public static final byte H2_SYM_LEN_LEN = 7;

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
        H1_VF_LEN_LEN;

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

    //Binary Lexical H2
    private boolean[] h2_sym_len;
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

    //Translated Lexical H2
    private byte t_h2_sym_len;
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
        t_h2_sym_len = (byte)(T_sym.length() << 3);
        h2_sym_len = BinaryTools.genBoolArrayFromUnsignedInt(T_sym.length(),H2_SYM_LEN_LEN);
        header[10] = h2_sym_len;

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
            h2_sym_len.length +
            h2_sym.length + 
            h2_data_ct.length +
            h2_h_gap.length;

        h_total_len = H1_TOTAL_LEN + h2_total_len;
    }

    public Original(String symbol, String interval){
        constructHeaderFromTranslatedValues(
            false,// boolean T_byid,
            interval,// String T_int,
            (byte)0,// byte T_h_gap_len,
            (byte)44,// byte T_utc_len,
            (byte)31,// byte T_pw_len,
            (byte)15,// byte T_pf_len,
            (byte)31,// byte T_vw_len,
            (byte)15,// byte T_vf_len,
            symbol// String T_sym
        );

        // t_h2_sym=symbol;
        // this.interval=interval;
        // stickList = new ArrayList<StickDouble>();

        // //Set ALL H1 bin sizes and some bin values
        // h1_byid = new boolean[]{false};
        // h1_int = new boolean[H1_INT_LEN];
        // h1_ct_len = new boolean[H1_CT_LEN_LEN];
        // h1_data_len = new boolean[H1_DATA_LEN_LEN]; //Value set AFTER h1_(pv)(wf)_len values set
        // h1_h_gap_len = new boolean[H1_H_GAP_LEN_LEN]; //Value set AFTER ALL h2 sizes

        // h1_utc_len = BinaryTools.genBoolArrayFromUnsignedInt(44,H1_UTC_LEN_LEN); //44 bits default, fixed 6 bits

        // h1_pw_len = new boolean[]{true,true,true,true,true};
        // h1_pf_len = new boolean[]{true,true,true,true};

        // h1_vw_len = new boolean[]{true,true,true,true,true};
        // h1_vf_len = new boolean[]{true,true,true,true};

        // // Set translated and binary H1 values where applicable
        // t_h1_byid=h1_byid[0];
        // t_h1_int=Integer.parseUnsignedInt(interval);
        // BinaryTools.setUnsignedIntToBoolArray(t_h1_int,h1_int);

        // t_h1_ct_len = 1; // 1 bit required to represent 0. Minimum bits required to represent current count
        // BinaryTools.setUnsignedIntToBoolArray(0,h1_ct_len); //Initially 0 Sticks

        // t_h1_utc_len = (byte)BinaryTools.toUnsignedInt(h1_utc_len);
        // t_h1_pw_len  = (byte)BinaryTools.toUnsignedInt(h1_pw_len);
        // t_h1_pf_len  = (byte)BinaryTools.toUnsignedInt(h1_pf_len);

        // t_h1_vw_len  = (byte)BinaryTools.toUnsignedInt(h1_vw_len);
        // t_h1_vf_len  = (byte)BinaryTools.toUnsignedInt(h1_vf_len);

        // base10PriceMaxFractionDigit  = (int)Math.ceil(Math.log10(Math.pow(2,t_h1_pf_len)-1));
        // base10VolumeMaxFractionDigit = (int)Math.ceil(Math.log10(Math.pow(2,t_h1_vf_len)-1));

        // t_h1_data_len = 44 + 4*(t_h1_pw_len + t_h1_pf_len) + t_h1_vw_len + t_h1_vf_len;
        // BinaryTools.setUnsignedIntToBoolArray(t_h1_data_len,h1_data_len);

        // //Set H2 translated values (these are already known)
        // t_h2_sym_len = (byte)(symbol.length() << 3);
        // t_h2_data_ct = 0;

        // //Set the H2 sizes and values
        // h2_sym_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h2_sym_len,H2_SYM_LEN_LEN);
        // h2_sym = BinaryTools.genBoolArrayFrom8BitCharString(symbol);

        // h2_data_ct = new boolean[]{false};

        // //Set gap
        // int remainder = (H1_TOTAL_LEN + h2_sym_len.length + h2_sym.length + h2_data_ct.length)%8;
        // if(remainder != 0){
        //     t_h1_h_gap_len = (byte)(8 - remainder);
        //     BinaryTools.setUnsignedIntToBoolArray(t_h1_h_gap_len,h1_h_gap_len);
        //     h2_h_gap = BinaryTools.genBoolArrayFromUnsignedInt(0,t_h1_h_gap_len);
        // }else{
        //     h2_h_gap = new boolean[0];
        //     t_h1_h_gap_len = 0; //h2_h_gap.length
        // }

        // //set remaining dependent initial values
        // h2_total_len =
        //     h2_sym_len.length +
        //     h2_sym.length + 
        //     h2_data_ct.length +
        //     h2_h_gap.length;

        // h_total_len = H1_TOTAL_LEN + h2_total_len;
    }

    // BinaryLexical Overrides
    //Get Binary Header
    @Override
    public boolean[][] getBinaryHeader(){
        boolean[][] h1 = genBinaryHeader1Ref();
        boolean[][] h2 = genBinaryHeader2Ref();

        boolean[][] h = new boolean[h1.length + h2.length][];
        int headerIndex = 0;

        for(boolean[] binField : h1){
            h[headerIndex] = BinaryTools.genClone(binField);
            ++headerIndex;
        }

        for(boolean[] binField : h2){
            h[headerIndex] = BinaryTools.genClone(binField);
            ++headerIndex;
        }

        return h;
    }

    @Override
    public boolean[] getBinaryHeaderFlat(){return BinaryTools.genConcatenatedBoolArrays(getBinaryHeader());}

    //Get Binary Data from Data instances
    @Override
    public boolean[][] getBinaryData(StickDouble singleData){
        boolean[][] binData = new boolean[11][];

        int[] tmpWholeFractionInts = new int[3];

        binData[0] = BinaryTools.genBoolArrayFromUnsignedLong(singleData.getUTC(),t_h1_utc_len); //UTC

        splitWholeFraction(singleData.getO(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
        binData[1] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len);  //Open Whole
        binData[2] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len);  //Open Fraction

        splitWholeFraction(singleData.getH(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
        binData[3] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len);  //High Whole
        binData[4] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len);  //High Fraction

        splitWholeFraction(singleData.getL(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
        binData[5] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len);  //Low Whole
        binData[6] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len);  //Low Fraction

        splitWholeFraction(singleData.getC(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
        binData[7] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len);  //Close Whole
        binData[8] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len);  //Close Fraction

        splitWholeFraction(singleData.getV(),base10VolumeMaxFractionDigit,tmpWholeFractionInts);
        binData[9] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_vw_len);  //Volume Whole
        binData[10] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_vf_len); //Volume Fraction

        return binData;
    }

    @Override
    public boolean[] getBinaryDataFlat(StickDouble singleData){
        boolean[] binData = new boolean[t_h1_data_len];
        int[] tmpWholeFractionInts = new int[3];
        int nextIndex = 0;

        BinaryTools.setSubsetUnsignedLong(nextIndex,t_h1_utc_len,singleData.getUTC(),binData);
        nextIndex+=t_h1_utc_len;

        //Open
        splitWholeFraction(singleData.getO(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pw_len,tmpWholeFractionInts[0],binData);
        nextIndex+=t_h1_pw_len;

        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pf_len,tmpWholeFractionInts[1],binData);
        nextIndex+=t_h1_pf_len;

        //High
        splitWholeFraction(singleData.getH(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pw_len,tmpWholeFractionInts[0],binData);
        nextIndex+=t_h1_pw_len;

        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pf_len,tmpWholeFractionInts[1],binData);
        nextIndex+=t_h1_pf_len;

        //Low
        splitWholeFraction(singleData.getL(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pw_len,tmpWholeFractionInts[0],binData);
        nextIndex+=t_h1_pw_len;

        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pf_len,tmpWholeFractionInts[1],binData);
        nextIndex+=t_h1_pf_len;

        //Close
        splitWholeFraction(singleData.getC(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pw_len,tmpWholeFractionInts[0],binData);
        nextIndex+=t_h1_pw_len;

        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pf_len,tmpWholeFractionInts[1],binData);
        nextIndex+=t_h1_pf_len;

        //Volume
        splitWholeFraction(singleData.getV(),base10VolumeMaxFractionDigit,tmpWholeFractionInts);
        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_vw_len,tmpWholeFractionInts[0],binData);
        nextIndex+=t_h1_vw_len;

        BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_vf_len,tmpWholeFractionInts[1],binData);
        nextIndex+=t_h1_vf_len;

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

            binData[0] = BinaryTools.genBoolArrayFromUnsignedLong(singleData.getUTC(),t_h1_utc_len); //UTC

            splitWholeFraction(singleData.getO(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
            binData[1] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len);  //Open Whole
            binData[2] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len);  //Open Fraction

            splitWholeFraction(singleData.getH(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
            binData[3] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len);  //High Whole
            binData[4] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len);  //High Fraction

            splitWholeFraction(singleData.getL(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
            binData[5] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len);  //Low Whole
            binData[6] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len);  //Low Fraction

            splitWholeFraction(singleData.getC(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
            binData[7] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len);  //Close Whole
            binData[8] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len);  //Close Fraction

            splitWholeFraction(singleData.getV(),base10VolumeMaxFractionDigit,tmpWholeFractionInts);
            binData[9] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_vw_len);  //Volume Whole
            binData[10] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_vf_len); //Volume Fraction

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
            BinaryTools.setSubsetUnsignedLong(nextIndex,t_h1_utc_len,singleData.getUTC(),r);
            nextIndex+=t_h1_utc_len;

            //Open
            splitWholeFraction(singleData.getO(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pw_len,tmpWholeFractionInts[0],r);
            nextIndex+=t_h1_pw_len;

            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pf_len,tmpWholeFractionInts[1],r);
            nextIndex+=t_h1_pf_len;

            //High
            splitWholeFraction(singleData.getH(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pw_len,tmpWholeFractionInts[0],r);
            nextIndex+=t_h1_pw_len;

            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pf_len,tmpWholeFractionInts[1],r);
            nextIndex+=t_h1_pf_len;

            //Low
            splitWholeFraction(singleData.getL(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pw_len,tmpWholeFractionInts[0],r);
            nextIndex+=t_h1_pw_len;

            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pf_len,tmpWholeFractionInts[1],r);
            nextIndex+=t_h1_pf_len;

            //Close
            splitWholeFraction(singleData.getC(),base10PriceMaxFractionDigit,tmpWholeFractionInts);
            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pw_len,tmpWholeFractionInts[0],r);
            nextIndex+=t_h1_pw_len;

            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_pf_len,tmpWholeFractionInts[1],r);
            nextIndex+=t_h1_pf_len;

            //Volume
            splitWholeFraction(singleData.getV(),base10VolumeMaxFractionDigit,tmpWholeFractionInts);
            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_vw_len,tmpWholeFractionInts[0],r);
            nextIndex+=t_h1_vw_len;

            BinaryTools.setSubsetUnsignedInt(nextIndex,t_h1_vf_len,tmpWholeFractionInts[1],r);
            nextIndex+=t_h1_vf_len;
        }

        return r;
    }

    public boolean[] getBinaryDataPointsFlat(Collection dataCollection){return null;}

    //Get Data instance from Binary
    public StickDouble getRefinedData(boolean[][] singleBinaryData){return null;}
    public StickDouble getRefinedDataFlat(boolean[] singleFlatBinaryData){return null;}
    public StickDouble[] getRefinedDataArray(boolean[][][] BinaryDataArray){return null;}
    public StickDouble[] getRefinedDataArrayFlat(boolean[] BinaryFlatDataArray){return null;}

    // Original methods
    //Get methods
    public boolean[][] genBinaryHeader1(){
        boolean[][] h1=new boolean[10][];

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

        return h1;
    }

    private boolean[][] genBinaryHeader1Ref(){
        boolean[][] h1=new boolean[10][];

        h1[0] = h1_byid;
        h1[1] = h1_int;
        h1[2] = h1_ct_len;
        h1[3] = h1_data_len;
        h1[4] = h1_h_gap_len;
        h1[5] = h1_utc_len;
        h1[6] = h1_pw_len;
        h1[7] = h1_pf_len;
        h1[8] = h1_vw_len;
        h1[9] = h1_vf_len;

        return h1;
    }

    public boolean[][] genBinaryHeader2(){
        boolean[][] h2=new boolean[4][];

        h2[0] = BinaryTools.genClone(h2_sym_len); //h2_sym_len
        h2[1] = BinaryTools.genClone(h2_sym); //h2_sym
        h2[2] = BinaryTools.genClone(h2_data_ct); //h2_data_ct
        h2[3] = BinaryTools.genClone(h2_h_gap); //h2_h_gap

        return h2;
    }

    private boolean[][] genBinaryHeader2Ref(){
        boolean[][] h2=new boolean[4][];

        h2[0] = h2_sym_len;
        h2[1] = h2_sym;
        h2[2] = h2_data_ct;
        h2[3] = h2_h_gap;

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

    public int getHeaderBitLength(){return h_total_len;}
    public int getHeader2BitLength(){return h2_total_len;}

    public String getSymbol(){return t_h2_sym;}
    public String getInterval(){return interval;}

    public boolean getByID(){return t_h1_byid;}
    public int getDataBitLength(){return t_h1_data_len;}
}