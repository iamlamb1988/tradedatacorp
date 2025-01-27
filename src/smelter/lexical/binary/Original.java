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
    private String symbol;
    private String interval;
    private ArrayList<StickDouble> stickList;

    public final int H1_TOTAL_LEN;
    private int h2_total_len;
    private int h_total_len;

    //Binary Lexical H1
    private boolean[] h1_byid;
    private boolean[] h1_int;
    private boolean[] h1_ct_len;
    private boolean[] h1_data_len;
    private boolean[] h1_h_gap_len;

    private boolean[] h1_pw_len; //price (ohlc) bit length
    private boolean[] h1_vw_len; //volume bit length

    private boolean[] h1_pf_len; //price (ohlc) bit length
    private boolean[] h1_vf_len; //volume bit length

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

    private byte t_h1_pw_len; //price (ohlc)
    private byte t_h1_vw_len; //volume

    private byte t_h1_pf_len;
    private byte t_h1_vf_len;

    //Translated Lexical H2
    private byte t_h2_sym_len;
    private int t_h2_data_ct;

    public Original(String symbol, String interval){
        this.symbol=symbol;
        this.interval=interval;
        stickList = new ArrayList<StickDouble>();

        //Set ALL H1 bin sizes and some bin values
        h1_byid = new boolean[]{false};
        h1_int = new boolean[25];
        h1_ct_len = new boolean[26];
        h1_data_len = new boolean[9]; //Value set AFTER h1_(pv)(wf)_len values set
        h1_h_gap_len = new boolean[3]; //Value set AFTER ALL h2 sizes

        h1_pw_len = new boolean[]{true,true,true,true,true};
        h1_pf_len = new boolean[]{true,true,true,true};

        h1_vw_len = new boolean[]{true,true,true,true,true};
        h1_vf_len = new boolean[]{true,true,true,true};

        H1_TOTAL_LEN =
            h1_byid.length +
            h1_int.length +
            h1_ct_len.length +
            h1_data_len.length +
            h1_h_gap_len.length +
            h1_pw_len.length +
            h1_pf_len.length +
            h1_vw_len.length +
            h1_vf_len.length;

        // Set translated and binary H1 values where applicable
        t_h1_byid=h1_byid[0];
        t_h1_int=Integer.parseUnsignedInt(interval);
        BinaryTools.setUnsignedIntToBoolArray(t_h1_int,h1_int);

        t_h1_ct_len = 1; // 1 bit required to represent 0. Minimum bits required to represent current count
        BinaryTools.setUnsignedIntToBoolArray(0,h1_ct_len); //Initially 0 Sticks

        t_h1_pw_len = (byte)BinaryTools.toUnsignedInt(h1_pw_len);
        t_h1_pf_len = (byte)BinaryTools.toUnsignedInt(h1_pf_len);

        t_h1_vw_len = (byte)BinaryTools.toUnsignedInt(h1_vw_len);
        t_h1_vf_len = (byte)BinaryTools.toUnsignedInt(h1_vf_len);

        t_h1_data_len = 44 + 4*(t_h1_pw_len + t_h1_pf_len) + t_h1_vw_len + t_h1_vf_len;
        BinaryTools.setUnsignedIntToBoolArray(t_h1_data_len,h1_data_len);

        //Set H2 translated values (these are already known)
        t_h2_sym_len = (byte)(symbol.length() << 3);
        t_h2_data_ct = 0;

        //Set the H2 sizes and values
        h2_sym_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h2_sym_len,(byte)7);
        h2_sym = BinaryTools.genBoolArrayFrom8BitCharString(symbol);

        h2_data_ct = new boolean[]{false};

        //Set gap
        int remainder = (H1_TOTAL_LEN + h2_sym_len.length + h2_sym.length + h2_data_ct.length)%8;
        if(remainder != 0){
            t_h1_h_gap_len = (byte)(8 - remainder);
            BinaryTools.setUnsignedIntToBoolArray(t_h1_h_gap_len,h1_h_gap_len);
            h2_h_gap = BinaryTools.genBoolArrayFromUnsignedInt(0,t_h1_h_gap_len);
        }

        //set remaining dependent initial values
        h2_total_len =
            h2_sym_len.length +
            h2_sym.length + 
            h2_data_ct.length +
            h2_h_gap.length;

        h_total_len = H1_TOTAL_LEN + h2_total_len;
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

        int[] tmpWholeFractionInts = new int[2];

        binData[0] = BinaryTools.genBoolArrayFromUnsignedLong(singleData.getUTC(),(byte)44); //UTC

        splitWholeFraction(singleData.getO(),tmpWholeFractionInts);
        binData[1] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len); //Open Whole
        binData[2] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len); //Open Fraction

        splitWholeFraction(singleData.getH(),tmpWholeFractionInts);
        binData[3] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len); //High Whole
        binData[4] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len); //High Fraction

        splitWholeFraction(singleData.getL(),tmpWholeFractionInts);
        binData[5] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len); //Low Whole
        binData[6] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len); //Low Fraction

        splitWholeFraction(singleData.getC(),tmpWholeFractionInts);
        binData[7] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_pw_len); //Close Whole
        binData[8] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_pf_len); //Close Fraction

        splitWholeFraction(singleData.getV(),tmpWholeFractionInts);
        binData[9] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[0],t_h1_vw_len); //Close Whole
        binData[10] = BinaryTools.genBoolArrayFromUnsignedInt(tmpWholeFractionInts[1],t_h1_vf_len); //Close Fraction

        return binData;
    }
    public boolean[] getBinaryDataFlat(StickDouble singleData){return null;}
    public boolean[][][] getBinaryDataPoints(StickDouble[] dataArray){return null;}
    public boolean[][][] getBinaryDataPoints(Collection dataCollection){return null;}
    public boolean[] getBinaryDataPointsFlat(StickDouble[] dataArray){return null;}
    public boolean[] getBinaryDataPointsFlat(Collection dataCollection){return null;}

    //Get Data instance from Binary
    public StickDouble getRefinedData(boolean[][] singleBinaryData){return null;}
    public StickDouble getRefinedDataFlat(boolean[] singleFlatBinaryData){return null;}
    public StickDouble[] getRefinedDataArray(boolean[][][] BinaryDataArray){return null;}
    public StickDouble[] getRefinedDataArrayFlat(boolean[] BinaryFlatDataArray){return null;}

    // Original methods
    //Get methods
    public boolean[][] genBinaryHeader1(){
        boolean[][] h1=new boolean[9][];

        h1[0] = BinaryTools.genClone(h1_byid); //h1_biid
        h1[1] = BinaryTools.genClone(h1_int); // h1_int
        h1[2] = BinaryTools.genClone(h1_ct_len); // h1_ct_len
        h1[3] = BinaryTools.genClone(h1_data_len); // h1_data_len
        h1[4] = BinaryTools.genClone(h1_h_gap_len); // h1_h_gap_len
        h1[5] = BinaryTools.genClone(h1_pw_len); // h1_pw_len
        h1[6] = BinaryTools.genClone(h1_pf_len); // h1_pf_len
        h1[7] = BinaryTools.genClone(h1_vw_len); // h1_vw_len
        h1[8] = BinaryTools.genClone(h1_vf_len); // h1_vf_len

        return h1;
    }

    private boolean[][] genBinaryHeader1Ref(){
        boolean[][] h1=new boolean[9][];

        h1[0] = h1_byid;
        h1[1] = h1_int;
        h1[2] = h1_ct_len;
        h1[3] = h1_data_len;
        h1[4] = h1_h_gap_len;
        h1[5] = h1_pw_len;
        h1[6] = h1_pf_len;
        h1[7] = h1_vw_len;
        h1[8] = h1_vf_len;

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

    //This will need to be sped up
    private void splitWholeFraction(double value, int[] valueParts){
        int whole = (int)Math.abs(value);
        valueParts[0] = whole;

        //Initial fraction digit
        double dec = (value - whole)*10;
        int fraction = (int)dec;
        int next_digit=0;

        //Remaining digits
        while(dec > 0){
            dec *= 10;
            next_digit = ((int)dec);
            dec -= next_digit;
            fraction = fraction*10 + next_digit;
        }
        valueParts[1] = fraction;
    }

    public int getHeaderBitLength(){return h_total_len;}
    public int getHeader2BitLength(){return h2_total_len;}

    public String getSymbol(){return symbol;}
    public String getInterval(){return interval;}

    public boolean getByID(){return t_h1_byid;}
}
