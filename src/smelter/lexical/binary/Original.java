/**
 * @author Bruce Lamb
 * @since 10 MAY 2025
 */
package tradedatacorp.smelter.lexical.binary;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

import java.util.Collection;
import java.util.ArrayList;

/**
 * A BinaryLexical implementation for translating {@link StickDouble} instances into binary arrays.
 * <p>This class is designed to efficiently convert {@link StickDouble} instances into boolean arrays 
 * that represent their binary form. Each instance of this class is tied to a specific ticker 
 * symbol and a single time interval, ensuring precise and consistent translations.
 * This classes primary focus is to prepare a binary file reader for accurate and efficient reading rather than maximum write speed.
 * A file write should be able to write pretty fast if certain meta information is known beforehand.</p>
 * <p>The state of the lexical primarily consists of the Header and the Data.
 * The Header represents the meta data about all the data, including how many data points are stored and how long each field is.
 * The Header is split into 2 parts H1 and H2. H1 contains all fixed bit lengths but values may change.
 * H2 consists of variable bit lengths based on different dependencies.
 * The Data consists of many Stick data points. The details about each stick attributes are in the Header fields.
 * The Header and the Data make up the Content of this {@link BinaryLexical}. This class is not meant to hold an entire large file all at once.
 * It is meant to hold portions of Content such that a file writer can write portions of a full file at a time.</p>
 * <p>When a Lexical reads valid Content for the first time, it is exptected to read from left to right: H1, H2, data elements.
 * The Lexical knows the exact length of H1 and each of it's static fields. H2 bit lengths and values will be known based on the value of H1 Fields.
 * The Data will be known based on all the Header data. Such as the bit size of each data point and all of it's attributes.</p>
 * <p>Below is a table representing the Header</p>
 * <table>
 * <caption>Header Fields</caption>
 * <tr><th>Field</th><th>Bit Length</th><th>Full Name</th><th>Description</th></tr>
 * <tr><td>h1_byid</td><td>1</td><td>By ID</td><td>May not be needed anymore.</td></tr>
 * <tr><td>h1_int</td><td>25</td><td>Time Frame Interval</td><td>The unsigned integer value represents the number of seconds for the time frame.</td></tr>
 * <tr><td>h1_ct_len</td><td>26</td><td>Data Count Bit Length</td><td>The unsigned integer value represents number of bits for field h2_data_ct.</td></tr>
 * <tr><td>h1_data_len</td><td>9</td><td>Data point bit length</td><td>The total number of bits to represent a single stick instance.</td></tr>
 * <tr><td>h1_h_gap_len</td><td>3</td><td>Header Gap bit length</td><td>The unsigned integer value represents the number of ignored bits between the Header and the datapoint within the content. This is the bit length of field h2_h_gap</td></tr>
 * <tr><td>h1_utc_len</td><td>6</td><td>Data stick UTC bit length</td><td>The unsigned integer value represents the number of bits used to represent the UTC timestamp of each stick.</td></tr>
 * <tr><td>h1_pw_len</td><td>?</td><td>Whole number price</td><td>The unsigned integer value represents the number of bits used to represent the whole part of 4 stick attribute, O, H, L, C.</td></tr>
 * <tr><td>h1_pf_len</td><td>?</td><td>Fractional number price</td><td>The unsigned integer value represents the number of bits used to represent the fraction part of 4 stick attribute, O, H, L, C.</td></tr>
 * <tr><td>h1_vw_len</td><td>?</td><td>Whole number volume</td><td>The unsigned integer value represents the number of bits used to represent the whole part of stick attribute V.</td></tr>
 * <tr><td>h1_vf_len</td><td>?</td><td>Fractional number volume</td><td>The unsigned integer value represents the number of bits used to represent the fraction part of stick attribute V.</td></tr>
 * <tr><td>h1_sym_len</td><td>7</td><td>Symbol bit length</td><td>The unsigned integer value represents The number of bits to represent the Symbol string. This is 8 times the number of characters.</td></tr>
 * <tr><td>h2_sym</td><td>t_h1_sym_len</td><td>Symbol</td><td>8 bit character string of the Symbol.</td></tr>
 * <tr><td>h2_data_ct</td><td>t_h1_data_len</td><td>Symbol</td><td>The number of sticks stored in the data section of content.</td></tr>
 * <tr><td>h2_h_gap</td><td>t_h1_h_gap_len</td><td>Header gap</td><td>The number of bits between the header and the first data point. The value is ignored. The purpose of this field is for memory alignment when writing.</td></tr>
 * </table>
 * <p>Below is the table representing each datapoint attribute of content. Consecutive datapoints are directly next to each other with no spacing.</p>
 * <table>
 * <caption>Data Point Fields</caption>
 * <tr><th>Full Name</th><th>Bit Length</th><th>Description</th></tr>
 * <tr><td>UTC Time Stamp</td><td>t_h1_utc_len</td><td>The unsigned integer UTC timestamp that represents the number of milliseconds since Jan 1st 1970.</td></tr>
 * <tr><td>Open Whole Value</td><td>t_h1_pw_len</td><td>The unsigned integer that represents the whole number portion of the Open price.</td></tr>
 * <tr><td>Open Fractional Value</td><td>t_h1_pf_len</td><td>The unsigned integer that represents the fractional side of the Open price.</td></tr>
 * <tr><td>High Whole Value</td><td>t_h1_pw_len</td><td>The unsigned integer that represents the whole number portion of the High price.</td></tr>
 * <tr><td>High Fractional Value</td><td>t_h1_pf_len</td><td>The unsigned integer that represents the fractional side of the High price.</td></tr>
 * <tr><td>Low Whole Value</td><td>t_h1_pw_len</td><td>The unsigned integer that represents the whole number portion of the Low price.</td></tr>
 * <tr><td>Low Fractional Value</td><td>t_h1_pf_len</td><td>The unsigned integer that represents the fractional side of the Low price.</td></tr>
 * <tr><td>Close Whole Value</td><td>t_h1_pw_len</td><td>The unsigned integer that represents the whole number portion of the Close price.</td></tr>
 * <tr><td>Close Fractional Value</td><td>t_h1_pf_len</td><td>The unsigned integer that represents the fractional side of the Close price.</td></tr>
 * <tr><td>Volume Whole Value</td><td>t_h1_vw_len</td><td>The unsigned integer that represents the whole number portion of the Volume price.</td></tr>
 * <tr><td>Volume Fractional Value</td><td>t_h1_vf_len</td><td>The unsigned integer that represents the fractional side of the Volume price.</td></tr>
 * </table>
 */
public class Original implements BinaryLexical<StickDouble>, Cloneable{
    private static final byte[] H1_LEN;
    private static final long[] tenToPow;
    private static final byte[] maxFractionFrombits;
    private static final byte[] bitsNeededForTenPow;
    private static final byte[] bitsNeededForMaxFraction;

    //Binary Header Index
    public static final byte H1_COUNT = 11; //Number of H1 Fields
    public static final byte H2_COUNT = 3;  //Number of H2 Fields

    public static final byte H_INDEX_BYID = 0;
    public static final byte H_INDEX_INT = 1;
    public static final byte H_INDEX_CT_LEN = 2;
    public static final byte H_INDEX_DATA_LEN = 3;
    public static final byte H_INDEX_H_GAP_LEN = 4;
    public static final byte H_INDEX_UTC_LEN = 5;
    public static final byte H_INDEX_PW_LEN = 6;
    public static final byte H_INDEX_PF_LEN = 7;
    public static final byte H_INDEX_VW_LEN = 8;
    public static final byte H_INDEX_VF_LEN = 9;
    public static final byte H_INDEX_SYM_LEN = 10;
    public static final byte H_INDEX_SYM = 11;
    public static final byte H_INDEX_DATA_CT = 12;
    public static final byte H_INDEX_H_GAP = 13;

    // Binary Fixed field bit lengths
    public static final byte H1_BYID_LEN = 1;
    public static final byte H1_INT_LEN = 25;
    public static final byte H1_CT_LEN_LEN = 5; //Clarity max number of bits 2^5 -1, 31 h2_ct max = 2^32 -1 
    public static final byte H1_DATA_LEN_LEN = 9;
    public static final byte H1_H_GAP_LEN_LEN = 3;
    public static final byte H1_UTC_LEN_LEN = 6;
    public static final byte H1_PW_LEN_LEN = 6;
    public static final byte H1_PF_LEN_LEN = 6;
    public static final byte H1_VW_LEN_LEN = 6;
    public static final byte H1_VF_LEN_LEN = 6;
    public static final byte H1_SYM_LEN_LEN = 7;

    static {
        H1_LEN = new byte[H1_COUNT];
        tenToPow = new long[16];
        maxFractionFrombits = new byte[64];
        bitsNeededForTenPow = new byte[16];
        bitsNeededForMaxFraction = new byte[20];

        H1_LEN[H_INDEX_BYID] = H1_BYID_LEN;           // Index 0
        H1_LEN[H_INDEX_INT] = H1_INT_LEN;             // Index 1
        H1_LEN[H_INDEX_CT_LEN] = H1_CT_LEN_LEN;       // Index 2
        H1_LEN[H_INDEX_DATA_LEN] = H1_DATA_LEN_LEN;   // Index 3
        H1_LEN[H_INDEX_H_GAP_LEN] = H1_H_GAP_LEN_LEN; // Index 4
        H1_LEN[H_INDEX_UTC_LEN] = H1_UTC_LEN_LEN;     // Index 5
        H1_LEN[H_INDEX_PW_LEN] = H1_PW_LEN_LEN;       // Index 6
        H1_LEN[H_INDEX_PF_LEN] = H1_PF_LEN_LEN;       // Index 7
        H1_LEN[H_INDEX_VW_LEN] = H1_VW_LEN_LEN;       // Index 8
        H1_LEN[H_INDEX_VF_LEN] = H1_VF_LEN_LEN;       // Index 9
        H1_LEN[H_INDEX_SYM_LEN] = H1_SYM_LEN_LEN;     // Index 10

        tenToPow[0] = 1; //10^0 == 1
        tenToPow[1] = 10; //10^1 == 10
        tenToPow[2] = 100; //10^2 == 100
        tenToPow[3] = 1000; //10^3 == 1000
        tenToPow[4] = 10000;
        tenToPow[5] = 100_000L;
        tenToPow[6] = 1_000_000L;
        tenToPow[7] = 10_000_000L;
        tenToPow[8] = 100_000_000L;
        tenToPow[9] = 1_000_000_000L;
        tenToPow[10] = 10_000_000_000L;
        tenToPow[11] = 100_000_000_000L;
        tenToPow[12] = 1_000_000_000_000L;
        tenToPow[13] = 10_000_000_000_000L;
        tenToPow[14] = 100_000_000_000_000L;
        tenToPow[15] = 1_000_000_000_000_000L;

        bitsNeededForTenPow[0] = 1; //One bit is needed to represent 1
        bitsNeededForTenPow[1] = 4; //log2(10) =~ 3.3 => 4
        bitsNeededForTenPow[2] = 7; //log2(10^2) =~ 6.6 => 7
        bitsNeededForTenPow[3] = 10; //log2(10^3) =~ 10.0 => 10
        bitsNeededForTenPow[4] = 14; //log2(10^4) =~ 13.2 => 14
        bitsNeededForTenPow[5] = 17; //log2(10^5) =~ 16.6 => 17
        bitsNeededForTenPow[6] = 20; //log2(10^6) =~ 19.9 => 20
        bitsNeededForTenPow[7] = 24; //log2(10^7) =~ 23.5 => 24
        bitsNeededForTenPow[8] = 27; //log2(10^8) =~ 26.6 => 27
        bitsNeededForTenPow[9] = 30; //log2(10^9) =~ 29.9 => 30
        bitsNeededForTenPow[10] = 34; //log2(10^10) =~ 33.2 => 34
        bitsNeededForTenPow[11] = 37; //log2(10^11) =~ 36.5 => 37
        bitsNeededForTenPow[12] = 40; //log2(10^12) =~ 39.9 => 40
        bitsNeededForTenPow[13] = 44; //log2(10^13) =~ 43.1 => 44
        bitsNeededForTenPow[14] = 47; //log2(10^14) =~ 46.5 => 47
        bitsNeededForTenPow[15] = 50; //log2(10^15) =~ 49.8 => 50

        maxFractionFrombits[0] = 0; // edge case: no digit to represent no fractional point
        maxFractionFrombits[1] = 0;
        maxFractionFrombits[2] = 0;
        maxFractionFrombits[3] = 0;
        maxFractionFrombits[4] = 1; // Encapsulates 10ths base 10 .0 to .9
        maxFractionFrombits[5] = 1;
        maxFractionFrombits[6] = 1;
        maxFractionFrombits[7] = 2; // Encapsulates 100ths base 10 .0 to .99
        maxFractionFrombits[8] = 2;
        maxFractionFrombits[9] = 2;
        maxFractionFrombits[10] = 3; // Encapsulates 1000ths base 10 .0 to .999
        maxFractionFrombits[11] = 3;
        maxFractionFrombits[12] = 3;
        maxFractionFrombits[13] = 3;
        maxFractionFrombits[14] = 4; // Encapsulates 1000ths base 10 .0 to .9999
        maxFractionFrombits[15] = 4;
        maxFractionFrombits[16] = 4;
        maxFractionFrombits[17] = 5; // Encapsulates 10000ths base 10 .0 to .99_999
        maxFractionFrombits[18] = 5;
        maxFractionFrombits[19] = 5;
        maxFractionFrombits[20] = 6; // Encapsulates 100000ths base 10 .0 to .999_999
        maxFractionFrombits[21] = 6;
        maxFractionFrombits[22] = 6;
        maxFractionFrombits[23] = 6;
        maxFractionFrombits[24] = 7; // Encapsulates 7 base 10 digits .0 to .9_999_999
        maxFractionFrombits[25] = 7;
        maxFractionFrombits[26] = 7;
        maxFractionFrombits[27] = 8; // Encapsulates 8 base 10 digits .0 to .99_999_999
        maxFractionFrombits[28] = 8;
        maxFractionFrombits[29] = 8;
        maxFractionFrombits[30] = 9; // Encapsulates 9 base 10 .0 to .999_999_999
        maxFractionFrombits[31] = 9;
        maxFractionFrombits[32] = 9;
        maxFractionFrombits[33] = 9;
        maxFractionFrombits[34] = 10; // Encapsulates 10 base 10 .0 to .9_999_999_999
        maxFractionFrombits[35] = 10;
        maxFractionFrombits[36] = 10;
        maxFractionFrombits[37] = 11; // Encapsulates 11 base 10 .0 to .99_999_999_999
        maxFractionFrombits[38] = 11;
        maxFractionFrombits[39] = 11;
        maxFractionFrombits[40] = 12; // Encapsulates 12 base 10 .0 to .999_999_999_999
        maxFractionFrombits[41] = 12;
        maxFractionFrombits[42] = 12;
        maxFractionFrombits[43] = 12;
        maxFractionFrombits[44] = 13; // Encapsulates 13 base 10 .0 to .9_999_999_999_999
        maxFractionFrombits[45] = 13;
        maxFractionFrombits[46] = 13;
        maxFractionFrombits[47] = 14; // Encapsulates 14 base 10 .0 to .99_999_999_999_999
        maxFractionFrombits[48] = 14;
        maxFractionFrombits[49] = 14;
        maxFractionFrombits[50] = 15; // Encapsulates 15 base 10 .0 to .999_999_999_999_999
        maxFractionFrombits[51] = 15;
        maxFractionFrombits[52] = 15;
        maxFractionFrombits[53] = 15;
        maxFractionFrombits[54] = 16; // Encapsulates 16 base 10 .0 to .9_999_999_999_999_999
        maxFractionFrombits[55] = 16;
        maxFractionFrombits[56] = 16;
        maxFractionFrombits[57] = 17; // Encapsulates 17 base 10 .0 to .99_999_999_999_999_999
        maxFractionFrombits[58] = 17;
        maxFractionFrombits[59] = 17;
        maxFractionFrombits[60] = 18; // Encapsulates 18 base 10 .0 to .999_999_999_999_999_999
        maxFractionFrombits[61] = 18;
        maxFractionFrombits[62] = 18;
        maxFractionFrombits[63] = 19; // Encapsulates 19 base 10 .0 to .9_999_999_999_999_999_999

        //Reverse min lookup for maxFractionFrombits
        bitsNeededForMaxFraction[0] = 0; //No bits needed for no fraction
        bitsNeededForMaxFraction[1] = 4; //log2(9) =~ 3.1 => 4
        bitsNeededForMaxFraction[2] = 7; //log2(99) =~ 6.6 => 7
        bitsNeededForMaxFraction[3] = 10; //log2(999) =~ 10.0 => 10
        bitsNeededForMaxFraction[4] = 14; //log2(9,999) =~ 13.2 => 14
        bitsNeededForMaxFraction[5] = 17; //log2(99,999) =~ 16.6 => 17
        bitsNeededForMaxFraction[6] = 20; //log2(999,999) =~ 19.9 => 20
        bitsNeededForMaxFraction[7] = 24; //log2(9,999,999) =~ 23.2 => 24
        bitsNeededForMaxFraction[8] = 27; //log2(99,999,999) =~ 26.6 => 27
        bitsNeededForMaxFraction[9] = 30; //log2(999,999,999) =~ 29.9 => 30
        bitsNeededForMaxFraction[10] = 34; //log2(9,999,999,999) =~ 33.2 => 34
        bitsNeededForMaxFraction[11] = 37; //log2(99,999,999,999) =~ 36.5 => 37
        bitsNeededForMaxFraction[12] = 40; //log2(999,999,999,999) =~ 39.9 => 40
        bitsNeededForMaxFraction[13] = 44; //log2(9,999,999,999,999) =~ 43.1 => 44
        bitsNeededForMaxFraction[14] = 47; //log2(99,999,999,999,999) =~ 46.5 => 47
        bitsNeededForMaxFraction[15] = 50; //log2(999,999,999,999,999) =~ 49.8 => 50
        bitsNeededForMaxFraction[16] = 54;
        bitsNeededForMaxFraction[17] = 57;
        bitsNeededForMaxFraction[18] = 60;
        bitsNeededForMaxFraction[19] = 63;
    }

    public static final int H1_TOTAL_LEN = 
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

    //Binary Header
    private boolean[][] header;

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

    //Header lengths
    private int h2_total_len;
    private int h_total_len;

    //Cache/memoization variables for speedy reference
    private int base10PriceMaxFractionDigit;  //used internally as a cache for splitWholeFraction functions
    private int base10VolumeMaxFractionDigit; //used internally as a cache for splitWholeFraction functions

    //Constructor
    private static byte constructorGapCalculator(String symbol, int count_len){
        int headerExceptGapLength = 
            H1_TOTAL_LEN + 
            (symbol.length() << 3) + //symbol length * 8
            count_len; //Data count for 0 elements
        int remainder = headerExceptGapLength%8;

        if(remainder != 0) return (byte)(8-remainder);
        else return (byte)0;
    }

    private void constructHeaderFromBinaryHeaderFields(
        boolean[] H_h1_byid,
        boolean[] H_h1_int,
        boolean[] H_h1_ct_len,
        boolean[] H_h1_data_len,
        boolean[] H_h1_h_gap_len,
        boolean[] H_h1_utc_len,
        boolean[] H_h1_pw_len,
        boolean[] H_h1_pf_len,
        boolean[] H_h1_vw_len,
        boolean[] H_h1_vf_len,
        boolean[] H_h1_sym_len,
        boolean[] H_h2_sym,
        boolean[] H_h2_data_ct,
        boolean[] H_h2_h_gap
    ){
        //Header
        header = new boolean[H1_COUNT+H2_COUNT][];

        //Header 0
        h1_byid = header[H_INDEX_BYID] = BinaryTools.genClone(H_h1_byid);
        t_h1_byid = h1_byid[0];

        //Header 1
        h1_int = header[H_INDEX_INT] = BinaryTools.genClone(H_h1_int);
        t_h1_int = BinaryTools.toUnsignedInt(h1_int);

        //Header 2
        h1_ct_len = header[H_INDEX_CT_LEN] = BinaryTools.genClone(H_h1_ct_len);
        t_h1_ct_len = BinaryTools.toUnsignedInt(h1_ct_len);

        //Header 3
        h1_data_len = header[H_INDEX_DATA_LEN] = BinaryTools.genClone(H_h1_data_len);
        t_h1_data_len = BinaryTools.toUnsignedInt(h1_data_len);

        //Header 4
        h1_h_gap_len = header[H_INDEX_H_GAP_LEN] = BinaryTools.genClone(H_h1_h_gap_len);
        t_h1_h_gap_len = (byte)BinaryTools.toUnsignedInt(h1_h_gap_len);

        //Header 5
        h1_utc_len = header[H_INDEX_UTC_LEN] = BinaryTools.genClone(H_h1_utc_len);
        t_h1_utc_len = (byte)BinaryTools.toUnsignedInt(h1_utc_len);

        //Header 6
        h1_pw_len = header[H_INDEX_PW_LEN] = BinaryTools.genClone(H_h1_pw_len);
        t_h1_pw_len = (byte)BinaryTools.toUnsignedInt(h1_pw_len);

        //Header 7
        h1_pf_len = header[H_INDEX_PF_LEN] = BinaryTools.genClone(H_h1_pf_len);
        t_h1_pf_len = (byte)BinaryTools.toUnsignedInt(H_h1_pf_len);

        //Header 8
        h1_vw_len = header[H_INDEX_VW_LEN] = BinaryTools.genClone(H_h1_vw_len);
        t_h1_vw_len = (byte)BinaryTools.toUnsignedInt(h1_vw_len);

        //Header 9
        h1_vf_len = header[H_INDEX_VF_LEN] = BinaryTools.genClone(H_h1_vf_len);
        t_h1_vf_len = (byte)BinaryTools.toUnsignedInt(h1_vf_len);

        //Header 10
        h1_sym_len = header[H_INDEX_SYM_LEN] = BinaryTools.genClone(H_h1_sym_len);
        t_h1_sym_len = (byte)BinaryTools.toUnsignedInt(h1_sym_len);

        //Header 11
        h2_sym = header[H_INDEX_SYM] = BinaryTools.genClone(H_h2_sym);
        t_h2_sym = BinaryTools.genStringFrom8BitBoolCharRep(h2_sym);

        //Header 12
        h2_data_ct = header[H_INDEX_DATA_CT] = BinaryTools.genClone(H_h2_data_ct);
        t_h2_data_ct = BinaryTools.toUnsignedInt(h2_data_ct);

        //Header 13
        h2_h_gap = header[H_INDEX_H_GAP] = BinaryTools.genClone(H_h2_h_gap);

        //Header length
        h2_total_len = getHeader2BitLength();
        h_total_len = getHeaderBitLength();

        //Cache/memoization
        base10PriceMaxFractionDigit  = maxFractionFrombits[t_h1_pf_len];
        base10VolumeMaxFractionDigit = maxFractionFrombits[t_h1_vf_len];
    }

    private void constructHeaderFromTranslatedValues(
        boolean T_byid,
        int T_int,
        byte T_ct_len,
        byte T_h_gap_len,
        byte T_utc_len,
        byte T_pw_len,
        byte T_pf_len,
        byte T_vw_len,
        byte T_vf_len,
        String T_sym
    ){
        header = new boolean[H1_COUNT+H2_COUNT][];

        //Header 0: by_id
        t_h1_byid = T_byid;
        h1_byid = new boolean[]{T_byid};
        header[H_INDEX_BYID] = h1_byid;

        //Header 1: int
        t_h1_int = T_int; //interval will be evaluated to correct integer in the future
        h1_int = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_int,H1_INT_LEN);
        header[H_INDEX_INT] = h1_int;

        //Header 2: ct_len
        t_h1_ct_len = T_ct_len;
        h1_ct_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_ct_len,H1_CT_LEN_LEN);
        header[H_INDEX_CT_LEN] = h1_ct_len;

        //Header 3: data_len
        t_h1_data_len = T_utc_len + 4*(T_pw_len + T_pf_len) + T_vw_len + T_vf_len;
        h1_data_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_data_len,H1_DATA_LEN_LEN);
        header[H_INDEX_DATA_LEN] = h1_data_len;

        //Header 4: h_gap_len
        t_h1_h_gap_len = T_h_gap_len;
        h1_h_gap_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_h_gap_len,H1_H_GAP_LEN_LEN);
        header[H_INDEX_H_GAP_LEN] = h1_h_gap_len;

        //Header 5: utc_len
        t_h1_utc_len = T_utc_len;
        h1_utc_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_utc_len,H1_UTC_LEN_LEN);
        header[H_INDEX_UTC_LEN] = h1_utc_len;

        //Header 6: pw_len
        t_h1_pw_len = T_pw_len;
        h1_pw_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_pw_len,H1_PW_LEN_LEN);
        header[H_INDEX_PW_LEN] = h1_pw_len;

        //Header 7: pf_len
        t_h1_pf_len = T_pf_len;
        h1_pf_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_pf_len,H1_PF_LEN_LEN);
        header[H_INDEX_PF_LEN] = h1_pf_len;

        //Header 8: vw_len
        t_h1_vw_len = T_vw_len;
        h1_vw_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_vw_len,H1_VW_LEN_LEN);
        header[H_INDEX_VW_LEN] = h1_vw_len;

        //Header 9: vf_len
        t_h1_vf_len = T_vf_len;
        h1_vf_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_vf_len,H1_VF_LEN_LEN);
        header[H_INDEX_VF_LEN] = h1_vf_len;

        //Cache/memoization
        base10PriceMaxFractionDigit  = maxFractionFrombits[t_h1_pf_len];
        base10VolumeMaxFractionDigit = maxFractionFrombits[t_h1_vf_len];

        //Header 10: sym_len
        t_h1_sym_len = (byte)(T_sym.length() << 3);
        h1_sym_len = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_sym_len,H1_SYM_LEN_LEN);
        header[H_INDEX_SYM_LEN] = h1_sym_len;

        //Header 11: sym
        t_h2_sym = T_sym;
        h2_sym = BinaryTools.genBoolArrayFrom8BitCharString(t_h2_sym);
        header[H_INDEX_SYM] = h2_sym;

        //Header 12: data_ct
        t_h2_data_ct = 0; //Initially 0 datapoints
        h2_data_ct = BinaryTools.genBoolArrayFromUnsignedInt(t_h2_data_ct,t_h1_ct_len);
        header[H_INDEX_DATA_CT] = h2_data_ct;

        //Header 13: gap
        h2_h_gap = header[H_INDEX_H_GAP] = BinaryTools.genBoolArrayFromUnsignedInt(0,t_h1_h_gap_len);

        h2_total_len = 
            h2_sym.length + 
            h2_data_ct.length +
            h2_h_gap.length;

        h_total_len = H1_TOTAL_LEN + h2_total_len;
    }

    public static Original genFatAlignedLexical(String symbol, int interval, byte numberOfFloatingValueDigits, byte numberOfFloatingVolumeDigits){
        byte valueWholeDigits=(byte)(52-numberOfFloatingValueDigits);
        byte volumeWholeDigits=(byte)(52-numberOfFloatingVolumeDigits);

        return new Original(
            false,// boolean T_byid,
            (byte)32,// byte T_h1_ct_len
            interval,// int T_int,
            constructorGapCalculator(symbol,32),// byte T_h_gap_len, T_h1_ct_len
            (byte)63,// byte T_utc_len,
            valueWholeDigits,// byte T_pw_len,
            numberOfFloatingValueDigits,// byte T_pf_len,
            volumeWholeDigits,// byte T_vw_len,
            numberOfFloatingVolumeDigits,// byte T_vf_len,
            symbol // String T_sym
        );
    }

    public static Original genFatAlignedLexical(String symbol, int interval){
        return genFatAlignedLexical(symbol,interval,(byte)16,(byte)16);
    }

    public static Original genStandardAlignedLexical(String symbol, int interval){
        return new Original(
            false, // boolean T_byid,
            (byte)16, //int T_ct_len That is 65535 maximum stick data (this is 45 days worth of 1 minute sticks (1440 sticks per day))
            interval, // int T_int,
            constructorGapCalculator(symbol,16), // byte T_h_gap_len, T_ct_len
            (byte)44, // byte T_utc_len,
            (byte)31, // byte T_pw_len,
            (byte)15, // byte T_pf_len,
            (byte)31, // byte T_vw_len,
            (byte)15, // byte T_vf_len,
            symbol // String T_sym
        );
    }

    public static Original genStandardLexical(String symbol, int interval, byte gapLength){
        return new Original(
            false,// boolean T_byid,
            (byte)16, //int T_ct_len That is 65535 maximum stick data (this is 45 days worth of 1 minute sticks (1440 sticks per day))
            interval,// int T_int,
            gapLength,// byte T_h_gap_len,
            (byte)44,// byte T_utc_len,
            (byte)31,// byte T_pw_len,
            (byte)15,// byte T_pf_len,
            (byte)31,// byte T_vw_len,
            (byte)15,// byte T_vf_len,
            symbol // String T_sym
        );
    }

    public static Original genMiniLexical(String symbol, int interval, byte gapLength){
        return new Original(
            false,// boolean T_byid,
            (byte)3, //int T_ct_len That is 8 maximum stick data points
            interval,// int T_int,
            gapLength,// byte T_h_gap_len,
            (byte)4,// byte T_utc_len, NOTE integer is 15
            (byte)4,// byte T_pw_len, NOTE integer is 15
            (byte)4,// byte T_pf_len, NOTE integer is 15
            (byte)4,// byte T_vw_len, NOTE integer is 15
            (byte)4,// byte T_vf_len, NOTE integer is 15
            symbol // String T_sym
        );
    }
    public Original(
        boolean[] H_h1_byid,
        boolean[] H_h1_int,
        boolean[] H_h1_ct_len,
        boolean[] H_h1_data_len,
        boolean[] H_h1_h_gap_len,
        boolean[] H_h1_utc_len,
        boolean[] H_h1_pw_len,
        boolean[] H_h1_pf_len,
        boolean[] H_h1_vw_len,
        boolean[] H_h1_vf_len,
        boolean[] H_h1_sym_len,
        boolean[] H_h2_sym,
        boolean[] H_h2_data_ct,
        boolean[] H_h2_h_gap
    ){
        constructHeaderFromBinaryHeaderFields(
            H_h1_byid,
            H_h1_int,
            H_h1_ct_len,
            H_h1_data_len,
            H_h1_h_gap_len,
            H_h1_utc_len,
            H_h1_pw_len,
            H_h1_pf_len,
            H_h1_vw_len,
            H_h1_vf_len,
            H_h1_sym_len,
            H_h2_sym,
            H_h2_data_ct,
            H_h2_h_gap
        );
    }

    public Original(
        boolean T_h1_byid,
        byte T_h1_ct_len,
        int T_h1_interval,
        byte T_h1_HeaderGap,
        byte T_h1_utc_len,
        byte T_h1_pw_len,
        byte T_h1_pf_len,
        byte T_h1_vw_len,
        byte T_h1_vf_len,
        String T_h2_sym
    ){
        constructHeaderFromTranslatedValues(
            T_h1_byid,// boolean T_byid,
            T_h1_interval,// int T_int,
            T_h1_ct_len, //int T_ct_len
            T_h1_HeaderGap,// byte T_h_gap_len,
            T_h1_utc_len,// byte T_utc_len,
            T_h1_pw_len,// byte T_pw_len,
            T_h1_pf_len,// byte T_pf_len,
            T_h1_vw_len,// byte T_vw_len,
            T_h1_vf_len,// byte T_vf_len,
            T_h2_sym// String T_sym
        );
    }

    public Original(byte[] compressedHeader){
        
    }
    // BinaryLexical Overrides
    /**
     * Returns a deep copy of the current state of binary header.
     * @return A deep copy of the binary header, where each index represents a specific static field as defined by {@code H_INDEX_*}.
     */
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

    /**
     * Returns a deep, flattened copy of the current state of binary header.
     * @return A deep, flattened copy of the binary header as a single one-dimensional boolean array.
     */
    @Override
    public boolean[] getBinaryHeaderFlat(){return BinaryTools.genConcatenatedBoolArrays(header);}

    /**
     * Returns a single datapoint representing a Data Point Field defined in class header.
     * @param singleData A Data Stick that will have all fields converted to boolean array IAW Data Point.
     * @return 2D boolean array that has 11 elements IAW Data Point Fields explained in the class header table.
     */
    @Override
    public boolean[][] getBinaryData(StickDouble singleData){
        boolean[][] binData = new boolean[11][];
        int[] tmpWholeFractionInts = new int[3];

        setBinaryDataStick(singleData,binData,tmpWholeFractionInts);

        return binData;
    }

    /**
     * Returns a single flattened datapoint representing a Data Point Field defined in class header.
     * @param singleData A Data Stick that will have all fields converted to boolean array IAW Data Point.
     * @return A flattened array where all 11 elements IAW Data Point Fields explained in the class header table are concatenated into a single boolean array.
     */
    @Override
    public boolean[] getBinaryDataFlat(StickDouble singleData){
        boolean[] binData = new boolean[t_h1_data_len];
        int[] tmpWholeFractionInts = new int[3];

        setBinaryDataStickFlat(singleData, binData, tmpWholeFractionInts, 0);

        return binData;
    }

    /**
     * Returns an inflated binary array of all Stick elements in the {@code dataArray}.
     * @param dataArray An array of elements that will be converted into binary.
     * @return A 3 dimensional array where:
     * The first element is 1 Stick
     * The second element is associated field (11 fields so this length is always 11)
     * The third element is bit associated with the field.
     */
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

    /**
     * Returns an inflated binary array of all Stick elements in the {@code dataCollection}.
     * @param dataCollection An array of elements that will be converted into binary.
     * @return A 3 dimensional array where:
     * The first element is 1 Stick
     * The second element is associated field (11 fields so this length is always 11)
     * The third element is bit associated with the field.
     */
    @Override
    public boolean[][][] getBinaryDataPoints(Collection<StickDouble> dataCollection){
        boolean[][][] r = new boolean[dataCollection.size()][][];
        boolean[][] binData;
        int[] tmpWholeFractionInts = new int[3];

        int rIndex=0;
        for(StickDouble singleData : dataCollection){
            binData = new boolean[11][]; //This is a singular element of r

            setBinaryDataStick(singleData, binData, tmpWholeFractionInts);
            
            r[rIndex] = binData;
            ++rIndex;
        }        

        return r;
    }

    /**
     * Returns an flattened binary array of all Stick elements in the {@code dataArray}.
     * This is similar to {@code getBinaryDataPoints} except the results are flattened to a single array.
     * @param dataArray An array of elements that will be converted into binary.
     * @return A flattened array of all datapoints. length be a multiple of {@code t_h1_data_len}.
     */
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

    /**
     * Returns an flattened binary array of all Stick elements in the {@code dataArray}.
     * This is similar to {@code getBinaryDataPoints} except the results are flattened to a single array.
     * @param dataCollection An array of elements that will be converted into binary.
     * @return A flattened array of all datapoints. length be a multiple of {@code t_h1_data_len}.
     */
    @Override
    public boolean[] getBinaryDataPointsFlat(Collection<StickDouble> dataCollection){
        boolean[] r = new boolean[t_h1_data_len * dataCollection.size()];
        int[] tmpWholeFractionInts = new int[3];
        int nextIndex = 0;

        for(StickDouble singleData : dataCollection){
            setBinaryDataStickFlat(singleData, r, tmpWholeFractionInts, nextIndex);
            nextIndex += t_h1_data_len;
        }

        return r;
    }

    //Get Data instance from Binary
    @Override
    public StickDouble getRefinedData(boolean[][] singleBinaryData){
        return new CandleStickFixedDouble(
            BinaryTools.toUnsignedLong(singleBinaryData[0]), //UTC

            BinaryTools.toUnsignedInt(singleBinaryData[1]) + 
            BinaryTools.toUnsignedInt(singleBinaryData[2])/Math.pow(10,base10PriceMaxFractionDigit), //Open

            BinaryTools.toUnsignedInt(singleBinaryData[3]) + 
            BinaryTools.toUnsignedInt(singleBinaryData[4])/Math.pow(10,base10PriceMaxFractionDigit), //High
            
            BinaryTools.toUnsignedInt(singleBinaryData[5]) + 
            BinaryTools.toUnsignedInt(singleBinaryData[6])/Math.pow(10,base10PriceMaxFractionDigit), //Low
            
            BinaryTools.toUnsignedInt(singleBinaryData[7]) + 
            BinaryTools.toUnsignedInt(singleBinaryData[8])/Math.pow(10,base10PriceMaxFractionDigit), //Close

            BinaryTools.toUnsignedInt(singleBinaryData[9]) + 
            BinaryTools.toUnsignedInt(singleBinaryData[10])/Math.pow(10,base10VolumeMaxFractionDigit) //Volume
        );
    }

    @Override
    public StickDouble getRefinedDataFlat(boolean[] singleFlatBinaryData){
        int tmpWhole,
            tmpFraction;

        int nextIndex=0;

        long utc = BinaryTools.toUnsignedLongFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_utc_len);
        nextIndex += t_h1_utc_len;

        //Open
        tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_pw_len);
        nextIndex += t_h1_pw_len;

        tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_pf_len);
        nextIndex += t_h1_pf_len;
        double open = tmpWhole + (double)tmpFraction/tenToPow[base10PriceMaxFractionDigit];

        //High
        tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_pw_len);
        nextIndex += t_h1_pw_len;

        tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_pf_len);
        nextIndex += t_h1_pf_len;
        double high = tmpWhole + (double)tmpFraction/tenToPow[base10PriceMaxFractionDigit];

        //Low
        tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_pw_len);
        nextIndex += t_h1_pw_len;

        tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_pf_len);
        nextIndex += t_h1_pf_len;
        double low = tmpWhole + (double)tmpFraction/tenToPow[base10PriceMaxFractionDigit];

        //Close
        tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_pw_len);
        nextIndex += t_h1_pw_len;

        tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_pf_len);
        nextIndex += t_h1_pf_len;
        double close = tmpWhole + (double)tmpFraction/tenToPow[base10PriceMaxFractionDigit];

        //Volume
        tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_vw_len);
        nextIndex += t_h1_vw_len;

        tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(singleFlatBinaryData, nextIndex, t_h1_vf_len);
        double volume = tmpWhole + (double)tmpFraction/tenToPow[base10VolumeMaxFractionDigit];

        return new CandleStickFixedDouble(utc, open, high, low, close, volume);
    }

    @Override
    public StickDouble[] getRefinedDataArray(boolean[][][] BinaryDataArray){
        StickDouble[] r = new StickDouble[BinaryDataArray.length];
        int index=0;
        for(boolean[][] singleBinData : BinaryDataArray){
            r[index] = getRefinedData(singleBinData);
        }
        return r;
    }

    @Override
    public StickDouble[] getRefinedDataArrayFlat(boolean[] binaryFlatDataArray){
        int dataCount = (binaryFlatDataArray.length / t_h1_data_len);
        if(dataCount == 0) return new StickDouble[0];
        StickDouble[] r = new StickDouble[dataCount];

        int tmpWhole,
            tmpFraction,
            nextIndex = 0;

        long utc;

        double open,
               high,
               low,
               close,
               volume;

        for(int i=0; i<r.length; ++i){
            utc = BinaryTools.toUnsignedLongFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_utc_len);
            nextIndex += t_h1_utc_len;

            //Open
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pw_len);
            nextIndex += t_h1_pw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pf_len);
            nextIndex += t_h1_pf_len;
            open = tmpWhole + tmpFraction/Math.pow(10,base10PriceMaxFractionDigit);

            //High
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pw_len);
            nextIndex += t_h1_pw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pf_len);
            nextIndex += t_h1_pf_len;
            high = tmpWhole + tmpFraction/Math.pow(10,base10PriceMaxFractionDigit);

            //Low
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pw_len);
            nextIndex += t_h1_pw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pf_len);
            nextIndex += t_h1_pf_len;
            low = tmpWhole + tmpFraction/Math.pow(10,base10PriceMaxFractionDigit);

            //Close
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pw_len);
            nextIndex += t_h1_pw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pf_len);
            nextIndex += t_h1_pf_len;
            close = tmpWhole + tmpFraction/Math.pow(10,base10PriceMaxFractionDigit);

            //Volume
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_vw_len);
            nextIndex += t_h1_vw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_vf_len);
            nextIndex += t_h1_vf_len;
            volume = tmpWhole + tmpFraction/Math.pow(10,base10VolumeMaxFractionDigit);

            r[i] = new CandleStickFixedDouble(utc, open, high, low, close, volume);
        }

        return r;
    }

    // Original methods
    @Override
    public Original clone(){
        synchronized (this){
            return new Original(
                h1_byid,
                h1_int,
                h1_ct_len,
                h1_data_len,
                h1_h_gap_len,
                h1_utc_len,
                h1_pw_len,
                h1_pf_len,
                h1_vw_len,
                h1_vf_len,
                h1_sym_len,
                h2_sym,
                h2_data_ct,
                h2_h_gap
            );
        }
    }

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

    private void updateHeaderLengths(){
        int newUpdatedHeader2Length = 0;
        for(int i=11; i<header.length; ++i){
            newUpdatedHeader2Length += header[i].length;
        }
        h2_total_len=newUpdatedHeader2Length;
        h_total_len = H1_TOTAL_LEN + h2_total_len;
    }

    public static byte getHeader1BitLength(int index){return H1_LEN[index];}

    public int getHeaderBitLength(){return h_total_len;}
    public int getHeader2BitLength(){return h2_total_len;}

    public boolean getByID(){return t_h1_byid;} //H0
    public int getInterval(){return t_h1_int;} //H1
    public int getDataBitLength(){return t_h1_data_len;} //H3
    public String getSymbol(){return t_h2_sym;} //H11
    public int getDataCount(){return t_h2_data_ct;} //H12

    /**
     * Alters the data count of header (h2_data_ct). Depends on h1_data_len, there must be enough bits to set to the new value.
     * @param numberOfDataPoints The new number of data points the header represents.
     */
    public void setDataCount(int numberOfDataPoints){
        if(numberOfDataPoints < 0)
            throw new IllegalArgumentException("numberOfDataPoints must be greater than or equal to 0. Received: "+numberOfDataPoints);
        t_h2_data_ct = numberOfDataPoints;
        BinaryTools.setUnsignedIntToBoolArray(numberOfDataPoints, h2_data_ct);
    }

    public void setHeaderGap(byte gapBitLength){
        if(gapBitLength < 0 || gapBitLength > 7)
            throw new IllegalArgumentException("gapBitLength must be between 0 and 7 inclusively. Received: "+gapBitLength);
        t_h1_h_gap_len = gapBitLength;

        BinaryTools.setUnsignedIntToBoolArray(gapBitLength, h1_h_gap_len);

        if(h2_h_gap.length != gapBitLength){
            h2_h_gap = header[H_INDEX_H_GAP] = BinaryTools.genBoolArrayFromUnsignedInt(0, gapBitLength);
            updateHeaderLengths();
        }
    }
}