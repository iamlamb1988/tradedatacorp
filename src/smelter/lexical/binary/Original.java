package tradedatacorp.smelter.lexical.binary;

import tradedatacorp.item.stick.primitive.StickDouble;

public class Original implements BinaryLexical<StickDouble[]>{
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
    private byte t_h1_v;
    private boolean t_h1_byid;
    private int t_h1_int;
    private int t_h1_ct_len;
    private int t_h1_data_len;
    private byte t_h1_h_gap;

    private byte t_h1_pw_len; //price (ohlc)
    private byte t_h1_vw_len; //volume

    private byte t_h1_pf_len;
    private byte t_h1_vf_len;

    //Translated Lexical H2
    private byte t_h2_sym_len;

    public Original(){
        //Set the H1 values
        h1_byid = new boolean[]{false};
        h1_int = new boolean[25];
        h1_data_len = new boolean[9];
        h1_h_gap = new boolean[3];

        h1_pw_len = new boolean[5];
        h1_vw_len = new boolean[5];

        h1_pf_len = new boolean[5];
        h1_vf_len = new boolean[5];

        //Set the H2 values (where applicable)
        h2_sym_len = new boolean[7];
        //h2 counts will be determined upon writing

        //set translated H1
        t_h1_byid=h1_byid[0];

        h2_sym = new boolean[t_h2_sym_len];

        byte isym=0;
        // while(isym<t_h2_sym_len){
        //     String charBool;
        //     for(byte i=0; i<this.symbol.length(); ++i){
        //         charBool=Integer.toBinaryString((byte)symbol.charAt(i));
        //         h2_sym[isym]=false;
        //         ++isym;
        //         for(byte j=0; j<charBool.length(); ++j,++isym){
        //             h2_sym[isym]=(charBool.charAt(j) == '1' ? true : false);
        //         }
        //     }
        // }
    }

    // Smelter Overrides
    @Override
    public byte[][] getH(String name, String interval){return null;}

    @Override
    public boolean[] toBits(StickDouble[] Data){return null;}

    @Override
    public byte[][] toBytesWithRemainder(StickDouble[] Data){return null;}

    @Override
    public StickDouble[] toRefined(boolean[] bitL){return null;}

    @Override
    public StickDouble[] toRefined(byte[][] byteL){return null;}
}