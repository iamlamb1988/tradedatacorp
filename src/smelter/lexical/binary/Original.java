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

    //Binary Lexical H1
    private boolean[] h1_byid;
    private boolean[] h1_int;
    private boolean[] h1_ct_len;
    private boolean[] h1_data_len;
    private boolean[] h1_h_gap;

    private boolean[] h1_pw_len; //price (ohlc) bit length
    private boolean[] h1_vw_len; //volume bit length

    private boolean[] h1_pf_len; //price (ohlc) bit length
    private boolean[] h1_vf_len; //volume bit length

    //Binary Lexical H2
    private boolean[] h2_sym_len;
    private boolean[] h2_sym;
    private boolean[] h2_data_ct;

    //Translated Lexical H1
    private boolean t_h1_byid;
    private int t_h1_int;
    private int t_h1_data_len;
    private int t_h1_ct_len;
    private byte t_h1_h_gap;

    private byte t_h1_pw_len; //price (ohlc)
    private byte t_h1_vw_len; //volume

    private byte t_h1_pf_len;
    private byte t_h1_vf_len;

    //Translated Lexical H2
    private byte t_h2_sym_len;

    public Original(String symbol, String interval){
        this.symbol=symbol;
        this.interval=interval;
        stickList = new ArrayList<StickDouble>();

        //Set the H1 sizes and some values
        h1_byid = new boolean[]{false};
        h1_int = new boolean[25];
        h1_data_len = new boolean[9];
        h1_ct_len = new boolean[26];
        h1_h_gap = new boolean[3];

        h1_pw_len = new boolean[]{true,true,true,true,true};
        h1_vw_len = new boolean[]{true,true,true,true,true};

        h1_pf_len = new boolean[]{true,true,true,true,true};
        h1_vf_len = new boolean[]{true,true,true,true,true};

        // Set translated and binary H1 values where applicable
        t_h1_int=Integer.parseUnsignedInt(interval);
        BinaryTools.setUnsignedIntToBoolArray(t_h1_int,h1_int);

        t_h1_data_len = h1_data_len.length;
        t_h1_ct_len = 1; // 1 bit required to represent 0.
        t_h1_pw_len = (byte)BinaryTools.toUnsignedInt(h1_pw_len);
        t_h1_vw_len = (byte)BinaryTools.toUnsignedInt(h1_vw_len);
        t_h1_pf_len = (byte)BinaryTools.toUnsignedInt(h1_pf_len);
        t_h1_vf_len = (byte)BinaryTools.toUnsignedInt(h1_vf_len);

        //Set H2 translated values (these are already known)
        t_h2_sym_len = (byte)(symbol.length() << 3);

        //Set the H2 sizes and values
        h2_sym_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h2_sym_len,(byte)7);
        h2_sym = BinaryTools.genBoolArrayFrom8BitCharString(symbol);
        h2_data_ct = new boolean[]{false};
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
    public boolean[][] getBinaryData(StickDouble singleData){return null;}
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
        h1[4] = BinaryTools.genClone(h1_h_gap); // h1_h_gap
        h1[5] = BinaryTools.genClone(h1_pw_len); // h1_pw_len
        h1[6] = BinaryTools.genClone(h1_vw_len); // h1_vw_len
        h1[7] = BinaryTools.genClone(h1_pf_len); // h1_pf_len
        h1[8] = BinaryTools.genClone(h1_vf_len); // h1_vf_len

        return h1;
    }

    private boolean[][] genBinaryHeader1Ref(){
        boolean[][] h1=new boolean[9][];

        h1[0] = h1_byid;
        h1[1] = h1_int;
        h1[2] = h1_ct_len;
        h1[3] = h1_data_len;
        h1[4] = h1_h_gap;
        h1[5] = h1_pw_len;
        h1[6] = h1_vw_len;
        h1[7] = h1_pf_len;
        h1[8] = h1_vf_len;

        return h1;
    }

    public boolean[][] genBinaryHeader2(){
        boolean[][] h2=new boolean[3][];

        h2[0] = BinaryTools.genClone(h2_sym_len); //h2_sym_len
        h2[1] = BinaryTools.genClone(h2_sym); //h2_sym
        h2[2] = BinaryTools.genClone(h2_data_ct); //h2_data_ct

        return h2;
    }

    private boolean[][] genBinaryHeader2Ref(){
        boolean[][] h2=new boolean[3][];

        h2[0] = h2_sym_len;
        h2[1] = h2_sym;
        h2[2] = h2_data_ct;

        return h2;
    }

    public String getSymbol(){return symbol;}
    public String getInterval(){return interval;}
}
