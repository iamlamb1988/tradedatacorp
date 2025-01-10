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
        t_h1_data_len = h1_data_len.length;
        t_h1_ct_len = 1; // 1 bit required to represent 0.
        t_h1_pw_len = t_h1_vw_len = t_h1_pf_len = t_h1_pf_len = 31; //Shortcut, Fix this later

        //Set H2 translated values (these are already known)
        t_h2_sym_len = (byte)(8*symbol.length());

        //Set the H2 sizes and values
        h2_sym_len = BinaryTools.unsignedIntToBoolArray(t_h2_sym_len,(byte)7);
        h2_sym = BinaryTools.stringTo8BitCharArray(symbol);
        h2_data_ct = new boolean[]{false};
    }

    // BinaryLexical Overrides
    //Get Binary Header
    public boolean[][] getBinaryHeader(){return null;}
    public boolean[] getBinaryHeaderFlat(){return null;}

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

    //Get methods
    public boolean[][] getBinaryHeader1(){
        boolean[][] h=new boolean[9][];

        h[0] = new boolean[1]; // h1_byid
        h[0][0]=false;

        h[1] = new boolean[h1_int.length]; // h1_int
        for(byte i=0; i<h1_int.length; ++i){h[1][i]=h1_int[i];}

        h[2] = new boolean[h1_ct_len.length]; // h1_ct_len
        for(byte i=0; i<h1_ct_len.length; ++i){h[2][i]=h1_ct_len[i];}

        h[3] = new boolean[h1_data_len.length]; // h1_data_len
        for(byte i=0; i<h1_data_len.length; ++i){h[3][i]=h1_data_len[i];}

        h[4] = new boolean[h1_h_gap.length]; // h1_h_gap
        for(byte i=0; i<h1_h_gap.length; ++i){h[4][i]=h1_h_gap[i];}

        h[5] = new boolean[h1_pw_len.length]; // h1_pw_len
        for(byte i=0; i<h1_pw_len.length; ++i){h[5][i]=h1_pw_len[i];}

        h[6] = new boolean[h1_vw_len.length]; // h1_vw_len
        for(byte i=0; i<h1_vw_len.length; ++i){h[6][i]=h1_vw_len[i];}

        h[7] = new boolean[h1_pf_len.length]; // h1_pf_len
        for(byte i=0; i<h1_pf_len.length; ++i){h[7][i]=h1_pf_len[i];}

        h[8] = new boolean[h1_vf_len.length]; // h1_vf_len
        for(byte i=0; i<h1_vf_len.length; ++i){h[8][i]=h1_vf_len[i];}

        return h;
    }

    public boolean[][] getBinaryHeader2(){return null;}
}