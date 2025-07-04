/**
 * @author Bruce Lamb
 * @since 14 MAY 2025
 */
package tradedatacorp.smelter.lexical.binary;

import tradedatacorp.tools.binarytools.BinaryTools;
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
 * <tr><td>h1_freeform</td><td>10</td><td>Free Form bits</td><td>Free form bits to be set by the user.</td></tr>
 * <tr><td>h1_int</td><td>25</td><td>Time Frame Interval</td><td>The unsigned integer value represents the number of seconds for the time frame.</td></tr>
 * <tr><td>h1_ct_len</td><td>5</td><td>Data Count Bit Length</td><td>The unsigned integer value represents number of bits for field h2_data_ct.</td></tr>
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
public class OHLCV_BinaryLexical implements BinaryLexical<StickDouble>, Cloneable{
    private static final byte[] H1_LEN;
    private static final long[] tenToPow;
    private static final byte[] maxFractionFrombits;
    private static final byte[] bitsNeededForTenPow;
    private static final byte[] bitsNeededForMaxFraction;

    /**
     * The number of fixed-length header fields (H1). The first 11 fields of the binary header are always present and have a known bit length.
     */
    public static final byte H1_COUNT = 11;

    /**
     * The number of variable-length header fields (H2). The last 3 fields of the binary header depend on values set by H1 fields.
     * For details on each field and its dependencies, see the class-level Javadoc header tables.
     */
    public static final byte H2_COUNT = 3;

    /**
     * The index in the header array for the {@code h1_freeform} field (H1[0] or H[0]).
     * Used for referencing the 'Free Form' field in the binary header structure.
     */
    public static final byte H_INDEX_FREE_FORM = 0;

    /**
     * The index in the header array for the {@code h1_int} field (H1[1] or H[1]).
     * Used for referencing the 'Interval' (timeframe in seconds) field in the binary header structure.
     */
    public static final byte H_INDEX_INT = 1;

    /**
     * The index in the header array for the {@code h1_ct_len} field (H1[2] or H[2]).
     * Used for referencing the 'Data Count Bit Length' field in the binary header structure.
     */
    public static final byte H_INDEX_CT_LEN = 2;

    /**
     * The index in the header array for the {@code h1_data_len} field (H1[3] or H[3]).
     * Used for referencing the 'Data Point Bit Length' field in the binary header structure.
     */
    public static final byte H_INDEX_DATA_LEN = 3;

    /**
     * The index in the header array for the {@code h1_h_gap_len} field (H1[4] or H[4]).
     * Used for referencing the 'Header Gap Bit Length' field in the binary header structure.
     */
    public static final byte H_INDEX_H_GAP_LEN = 4;

    /**
     * The index in the header array for the {@code h1_utc_len} field (H1[5] or H[5]).
     * Used for referencing the 'UTC Bit Length' field in the binary header structure.
     */
    public static final byte H_INDEX_UTC_LEN = 5;

    /**
     * The index in the header array for the {@code h1_pw_len} field (H1[6] or H[6]).
     * Used for referencing the 'Price Whole Length' field in the binary header structure.
     */
    public static final byte H_INDEX_PW_LEN = 6;

    /**
     * The index in the header array for the {@code h1_pf_len} field (H1[7] or H[7]).
     * Used for referencing the 'Price Fraction Length' field in the binary header structure.
     */
    public static final byte H_INDEX_PF_LEN = 7;

    /**
     * The index in the header array for the {@code h1_vw_len} field (H1[8] or H[8]).
     * Used for referencing the 'Volume Whole Length' field in the binary header structure.
     */
    public static final byte H_INDEX_VW_LEN = 8;

    /**
     * The index in the header array for the {@code h1_vf_len} field (H1[9] or H[9]).
     * Used for referencing the 'Volume Fraction Length' field in the binary header structure.
     */
    public static final byte H_INDEX_VF_LEN = 9;

    /**
     * The index in the header array for the {@code h1_sym_len} field (H1[10] or H[10]).
     * Used for referencing the 'Symbol Length' field in the binary header structure.
     */
    public static final byte H_INDEX_SYM_LEN = 10;

    /**
     * The index in the header array for the {@code h2_sym} field (H2[0] or H[11]).
     * Used for referencing the 'Symbol Value' field in the binary header structure.
     */
    public static final byte H_INDEX_SYM = 11;

    /**
     * The index in the header array for the {@code h2_data_ct} field (H2[1] or H[12]).
     * Used for referencing the 'Data Count Value' field in the binary header structure.
     */
    public static final byte H_INDEX_DATA_CT = 12;

    /**
     * The index in the header array for the {@code h2_h_gap} field (H2[2] or H[13]).
     * Used for referencing the 'Header Gap Value' field in the binary header structure.
     * Note: The values is not used, only the length is for bit alignment.
     */
    public static final byte H_INDEX_H_GAP = 13;

    /**
     * Bit length of the {@code h1_freeform} header field.
     * This is a free-form bit and is not utilized in the data encoding or decoding process.
     * Value: 10 bits.
     */
    public static final byte H1_FREE_FORM_LEN = 10;

    /**
     * Bit length of the {@code h1_int} header field.
     * Represents the number of seconds in the time frame interval for the data.
     * Value: 25 bits.
     */
    public static final byte H1_INT_LEN = 25;

    /**
     * Bit length of the {@code h1_ct_len} header field.
     * Specifies the number of bits used to encode the data count field ({@code h2_data_ct}).
     * Value: 5 bits. The maximum number of bits is 2^5 - 1 = 31, so the maximum {@code h2_data_ct} value is 2^32 - 1.
     */
    public static final byte H1_CT_LEN_LEN = 5;

    /**
     * Bit length of the {@code h1_data_len} header field.
     * Indicates the number of bits required to represent a single data point in the binary encoding.
     * Value: 9 bits.
     */
    public static final byte H1_DATA_LEN_LEN = 9;

    /**
     * Bit length of the {@code h1_h_gap_len} header field.
     * Specifies the number of bits used for the header gap, which is for bit alignment between the header and data sections.
     * Value: 3 bits.
     */
    public static final byte H1_H_GAP_LEN_LEN = 3;

    /**
     * Bit length of the {@code h1_utc_len} header field.
     * Specifies the number of bits used to represent the UTC timestamp for each data point.
     * Value: 6 bits. The maximum number of bits is 2^3 - 1 = 7.
     */
    public static final byte H1_UTC_LEN_LEN = 6;

    /**
     * Bit length of the {@code h1_pw_len} header field.
     * Specifies the number of bits used for the whole (integer) part of the price attributes (Open, High, Low, Close).
     * Value: 6 bits.
     */
    public static final byte H1_PW_LEN_LEN = 6;

    /**
     * Bit length of the {@code h1_pf_len} header field.
     * Specifies the number of bits used for the fractional part of the price attributes (Open, High, Low, Close).
     * Value: 6 bits.
     */
    public static final byte H1_PF_LEN_LEN = 6;

    /**
     * Bit length of the {@code h1_vw_len} header field.
     * Specifies the number of bits used for the whole (integer) part of the volume attribute.
     * Value: 6 bits.
     */
    public static final byte H1_VW_LEN_LEN = 6;

    /**
     * Bit length of the {@code h1_vf_len} header field.
     * Specifies the number of bits used for the fractional part of the volume attribute.
     * Value: 6 bits.
     */
    public static final byte H1_VF_LEN_LEN = 6;

    /**
     * Bit length of the {@code h1_sym_len} header field.
     * Specifies the number of bits used to represent the symbol length, where each character is 8 bits.
     * Value: 7 bits.
     */
    public static final byte H1_SYM_LEN_LEN = 7;

    static {
        H1_LEN = new byte[H1_COUNT];
        tenToPow = new long[16];
        maxFractionFrombits = new byte[64];
        bitsNeededForTenPow = new byte[16];
        bitsNeededForMaxFraction = new byte[20];

        H1_LEN[H_INDEX_FREE_FORM] = H1_FREE_FORM_LEN;           // Index 0
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

    /**
     * Bit length sum of all H1 fields.
     * Value: 89 bits.
     */
    public static final int H1_TOTAL_LEN = 
        H1_FREE_FORM_LEN +
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
    private boolean[] h1_freeform;
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
    private byte t_h1_freeform;
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
        boolean[] H_h1_freeform,
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
        h1_freeform = header[H_INDEX_FREE_FORM] = BinaryTools.genClone(H_h1_freeform);
        t_h1_freeform = (byte)BinaryTools.toUnsignedInt(h1_freeform);

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
        byte T_freeform,
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

        //Header 0: freeform
        t_h1_freeform = T_freeform;
        h1_freeform = BinaryTools.genBoolArrayFromUnsignedInt(t_h1_freeform,H1_FREE_FORM_LEN);
        header[H_INDEX_FREE_FORM] = h1_freeform;

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

    /**
     * Creates an OHLCV_BinaryLexical instance with maximum (fat) alignment for all bit fields, allowing for the largest possible ranges for value and volume fields.
     * This will be bloated and not as compressed as possible.
     * 
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @param freeFormValue Must be between 0 and 1023 (inclusively). A free form value of the users choosing.
     * @param numberOfFloatingValueDigits The number of decimal digits for price fields (Open/High/Low/Close).
     * @param numberOfFloatingVolumeDigits The number of decimal digits for volume field.
     * @return a new OHLCV_BinaryLexical instance configured for maximum/fat alignment.
     */
    public static OHLCV_BinaryLexical genFatAlignedLexical(String symbol, int interval,byte freeFormValue, byte numberOfFloatingValueDigits, byte numberOfFloatingVolumeDigits){
        byte valueWholeDigits=(byte)(52-numberOfFloatingValueDigits);
        byte volumeWholeDigits=(byte)(52-numberOfFloatingVolumeDigits);

        return new OHLCV_BinaryLexical(
            freeFormValue,// boolean T_freeform,
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

        /**
     * Creates an OHLCV_BinaryLexical instance with maximum (fat) alignment for all bit fields, allowing for the largest possible ranges for value and volume fields.
     * This will be bloated and not as compressed as possible.
     * 
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @param numberOfFloatingValueDigits The number of decimal digits for price fields (Open/High/Low/Close).
     * @param numberOfFloatingVolumeDigits The number of decimal digits for volume field.
     * @return a new OHLCV_BinaryLexical instance configured for maximum/fat alignment.
     */
    public static OHLCV_BinaryLexical genFatAlignedLexical(String symbol, int interval, byte numberOfFloatingValueDigits, byte numberOfFloatingVolumeDigits){
        return genFatAlignedLexical(symbol,interval,(byte)0,numberOfFloatingValueDigits,numberOfFloatingVolumeDigits);
    }

    /**
     * Creates an OHLCV_BinaryLexical instance with maximum (fat) alignment using the default maximum (16) decimal digits for both value and volume fields.
     * This will be bloated and not as compressed as possible.
     * 
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @return a new OHLCV_BinaryLexical instance configured for maximum/fat alignment with default decimal precision.
     */
    public static OHLCV_BinaryLexical genFatAlignedLexical(String symbol, int interval){
        return genFatAlignedLexical(symbol,interval,(byte)16,(byte)16);
    }

    /**
     * Creates an OHLCV_BinaryLexical instance with "standard" alignment, suitable for common use cases 
     * (e.g., 16 bits for data count, 44 bits for UTC, 31/15 bits for OHLC, and 31/15 for volume).
     *
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @param freeFormValue Must be between 0 and 1023 (inclusively). A free form value of the users choosing.
     * @return a new OHLCV_BinaryLexical instance configured for standard alignment and typical financial data.
     */
    public static OHLCV_BinaryLexical genStandardAlignedLexical(String symbol, int interval, byte freeFormValue){
        return new OHLCV_BinaryLexical(
            freeFormValue, // byte T_freeform,
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

    
    /**
     * Creates an OHLCV_BinaryLexical instance with "standard" alignment, suitable for common use cases 
     * (e.g., 16 bits for data count, 44 bits for UTC, 31/15 bits for OHLC, and 31/15 for volume).
     *
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @return a new OHLCV_BinaryLexical instance configured for standard alignment and typical financial data.
     */
    public static OHLCV_BinaryLexical genStandardAlignedLexical(String symbol, int interval){
        return genStandardAlignedLexical(symbol,interval,(byte)0);
    }

    /**
     * Creates an OHLCV_BinaryLexical instance with "standard" alignment but allows you to specify the header gap length.
     * Useful for cases where you need to control byte alignment or padding between header and data.
     * 
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @param freeFormValue Must be between 0 and 1023 (inclusively). A free form value of the users choosing.
     * @param gapLength The bit-length of the header gap (padding).
     * @return a new OHLCV_BinaryLexical instance configured for standard alignment with a custom gap.
     */
    public static OHLCV_BinaryLexical genStandardLexical(String symbol, int interval, byte freeFormValue, byte gapLength){
        return new OHLCV_BinaryLexical(
            freeFormValue,// byte T_freeform,
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

    /**
     * Creates an OHLCV_BinaryLexical instance with "standard" alignment but allows you to specify the header gap length.
     * Useful for cases where you need to control byte alignment or padding between header and data.
     * 
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @param gapLength The bit-length of the header gap (padding).
     * @return a new OHLCV_BinaryLexical instance configured for standard alignment with a custom gap.
     */
    public static OHLCV_BinaryLexical genStandardLexical(String symbol, int interval, byte gapLength){
        return genStandardLexical(symbol,interval,(byte)0,gapLength);
    }

    /**
     * Creates an OHLCV_BinaryLexical instance with minimal bit usage, suitable for very compact data (e.g., only 3 bits for count, 
     * 4 bits for each field). Good for testing, educational, or very small data sets.
     * Useful for testing small datasets by hand.
     * 
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @param freeFormValue Must be between 0 and 1023 (inclusively). A free form value of the users choosing.
     * @param gapLength The bit-length of the header gap (padding).
     * @return a new OHLCV_BinaryLexical instance configured for minimal/compact bit usage.
     */
    public static OHLCV_BinaryLexical genMiniLexical(String symbol, int interval, byte freeFormValue, byte gapLength){
        return new OHLCV_BinaryLexical(
            freeFormValue,// boolean T_freeform,
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


    /**
     * Creates an OHLCV_BinaryLexical instance with minimal bit usage, suitable for very compact data (e.g., only 3 bits for count, 
     * 4 bits for each field). Good for testing, educational, or very small data sets.
     * Useful for testing small datasets by hand.
     * 
     * @param symbol The ticker symbol this lexical will represent.
     * @param interval The interval (in seconds) for each data point.
     * @param gapLength The bit-length of the header gap (padding).
     * @return a new OHLCV_BinaryLexical instance configured for minimal/compact bit usage.
     */
    public static OHLCV_BinaryLexical genMiniLexical(String symbol, int interval, byte gapLength){
        return genMiniLexical(symbol, interval, (byte)0, gapLength);
    }

    /**
     * Constructs an OHLCV_BinaryLexical instance by directly providing the binary header fields.
     * This constructor is intended for cases where the binary header representation is already available,
     * such as when deserializing or cloning an OHLCV_BinaryLexical instance.
     * Not recommended by hand, contradictions will NOT be checked.
     *
     * @param H_h1_freeform         Binary representation of 'free form' header field.
     * @param H_h1_int          Binary representation of 'interval' header field.
     * @param H_h1_ct_len       Binary representation of 'count length' header field.
     * @param H_h1_data_len     Binary representation of 'data length' header field.
     * @param H_h1_h_gap_len    Binary representation of 'header gap length' field.
     * @param H_h1_utc_len      Binary representation of 'UTC length' header field.
     * @param H_h1_pw_len       Binary representation of 'price whole length' header field.
     * @param H_h1_pf_len       Binary representation of 'price fraction length' header field.
     * @param H_h1_vw_len       Binary representation of 'volume whole length' header field.
     * @param H_h1_vf_len       Binary representation of 'volume fraction length' header field.
     * @param H_h1_sym_len      Binary representation of 'symbol length' header field.
     * @param H_h2_sym          Binary representation of 'symbol' (value) header field.
     * @param H_h2_data_ct      Binary representation of 'data count' header field.
     * @param H_h2_h_gap        Binary representation of 'header gap' value field.
     */
    public OHLCV_BinaryLexical(
        boolean[] H_h1_freeform,
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
            H_h1_freeform,
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

    /**
     * Constructs an OHLCV_BinaryLexical instance from already-translated (typed) header values.
     * This constructor is ideal for creating a new binary lexical structure from scratch,
     * where all header parameters (such as symbol, interval, bit lengths) are known in advance.
     *
     * @param T_h1_freeform      The boolean value for the 'free form' header field.
     * @param T_h1_ct_len    The number of bits for the 'count length' header field.
     * @param T_h1_interval  The interval (in seconds) for each data point.
     * @param T_h1_HeaderGap The number of bits of header gap for byte alignment.
     * @param T_h1_utc_len   The number of bits for the UTC timestamp field.
     * @param T_h1_pw_len    The number of bits for the whole part of price fields.
     * @param T_h1_pf_len    The number of bits for the fractional part of price fields.
     * @param T_h1_vw_len    The number of bits for the whole part of the volume field.
     * @param T_h1_vf_len    The number of bits for the fractional part of the volume field.
     * @param T_h2_sym       The ticker symbol as a string.
     */
    public OHLCV_BinaryLexical(
        byte T_h1_freeform,
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
            T_h1_freeform,// boolean T_freeform,
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
     * The first element is a Stick
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

    /**
     * Returns a {@link StickDouble} instance from an inflated binary datapoint.
     * This is the reverse of {@code getBinaryData}
     * @param singleBinaryData A single inflated datapoint.
     * There must be exactly 11 elements, one that represents each datapoint field. The individiual lenghts correspond to the header values.
     * This is the same format as the return value in {@code getBinaryData} function.
     * @return A StickDouble instance from an inflated binary datapoint.
     */
    @Override
    public StickDouble getRefinedData(boolean[][] singleBinaryData){
        return new CandleStickFixedDouble(
            BinaryTools.toUnsignedLong(singleBinaryData[0]), //UTC

            BinaryTools.toUnsignedInt(singleBinaryData[1]) + 
            (double)BinaryTools.toUnsignedInt(singleBinaryData[2])/tenToPow[base10PriceMaxFractionDigit], //Open

            BinaryTools.toUnsignedInt(singleBinaryData[3]) + 
            (double)BinaryTools.toUnsignedInt(singleBinaryData[4])/tenToPow[base10PriceMaxFractionDigit], //High
            
            BinaryTools.toUnsignedInt(singleBinaryData[5]) + 
            (double)BinaryTools.toUnsignedInt(singleBinaryData[6])/tenToPow[base10PriceMaxFractionDigit], //Low
            
            BinaryTools.toUnsignedInt(singleBinaryData[7]) + 
            (double)BinaryTools.toUnsignedInt(singleBinaryData[8])/tenToPow[base10PriceMaxFractionDigit], //Close

            BinaryTools.toUnsignedInt(singleBinaryData[9]) + 
            (double)BinaryTools.toUnsignedInt(singleBinaryData[10])/tenToPow[base10VolumeMaxFractionDigit] //Volume
        );
    }

    /**
     * Returns a {@link StickDouble} instance from a flattened binary datapoint.
     * This is the reverse of {@code getBinaryDataFlat}
     * @param singleFlatBinaryData A single flattened datapoint.
     * This is the same format as the return value in {@code getBinaryDataFlat} function
     * @return A StickDouble instance from a flattened binary datapoint.
     */
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

    /**
     * Returns an array of {@link StickDouble} elements from an array of inflated sticks.
     * @param binaryDataArray A 3 dimensional array where:
     * The first element is the Stick
     * The second element is associated field (11 fields so this length is always 11)
     * The third element is bit associated with the field.
     */
    @Override
    public StickDouble[] getRefinedDataArray(boolean[][][] binaryDataArray){
        StickDouble[] r = new StickDouble[binaryDataArray.length];
        int index=0;
        for(boolean[][] singleBinData : binaryDataArray){
            r[index] = getRefinedData(singleBinData);
        }
        return r;
    }

    /**
     * Returns an array of {@link StickDouble} elements from a flattened array of sticks.
     * @param binaryFlatDataArray A flattened array of all datapoints. length be a multiple of {@code t_h1_data_len}.
     */
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
            open = tmpWhole + (double)tmpFraction/tenToPow[base10PriceMaxFractionDigit];

            //High
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pw_len);
            nextIndex += t_h1_pw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pf_len);
            nextIndex += t_h1_pf_len;
            high = tmpWhole + (double)tmpFraction/tenToPow[base10PriceMaxFractionDigit];

            //Low
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pw_len);
            nextIndex += t_h1_pw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pf_len);
            nextIndex += t_h1_pf_len;
            low = tmpWhole + (double)tmpFraction/tenToPow[base10PriceMaxFractionDigit];

            //Close
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pw_len);
            nextIndex += t_h1_pw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_pf_len);
            nextIndex += t_h1_pf_len;
            close = tmpWhole + (double)tmpFraction/tenToPow[base10PriceMaxFractionDigit];

            //Volume
            tmpWhole = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_vw_len);
            nextIndex += t_h1_vw_len;

            tmpFraction = BinaryTools.toUnsignedIntFromBoolSubset(binaryFlatDataArray, nextIndex, t_h1_vf_len);
            nextIndex += t_h1_vf_len;
            volume = tmpWhole + (double)tmpFraction/tenToPow[base10VolumeMaxFractionDigit];

            r[i] = new CandleStickFixedDouble(utc, open, high, low, close, volume);
        }

        return r;
    }

    // OHLCV_BinaryLexical methods
    /**
     * @return a deep copy of this lexical.
     */
    @Override
    public OHLCV_BinaryLexical clone(){
        synchronized (this){
            return new OHLCV_BinaryLexical(
                h1_freeform,
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
    /**
     * Returns a deep copy of header 1.
     * @return a deep copy of header 1.
     */
    public boolean[][] genBinaryHeader1(){
        boolean[][] h1=new boolean[11][];

        h1[0] = BinaryTools.genClone(h1_freeform); //h1_freeform
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

    /**
     * Returns a deep copy of header 2.
     * @return a deep copy of header 2.
     */
    public boolean[][] genBinaryHeader2(){
        boolean[][] h2=new boolean[3][];

        h2[0] = BinaryTools.genClone(h2_sym); //h2_sym
        h2[1] = BinaryTools.genClone(h2_data_ct); //h2_data_ct
        h2[2] = BinaryTools.genClone(h2_h_gap); //h2_h_gap

        return h2;
    }

    /**
     * Returns the maximimum number of decimal places that will be returned for Open, High, Low and Close data fields.
     * @return the maximimum number of decimal places that will be returned for Open, High, Low and Close data fields.
     */
    public int getBase10PriceDigits(){return base10PriceMaxFractionDigit;}

    /**
     * Returns the maximimum number of decimal places that will be returned for Volume.
     * @return the maximimum number of decimal places that will be returned for Volume.
     */
    public int getBase10VolumeDigits(){return base10VolumeMaxFractionDigit;}

    //TODO: This will need to be sped up, replace the fraction piece with base10 constant
    /**
     * Will alter the first 3 elements of array to be the whole number, whole fraction, and number of digits.
     * Ex 1: splitWholeFraction(4.5,3,myIntArr);
     * myIntArr[0] => 4
     * myIntArr[1] => 500 //500 is the 3 digit fractional part.
     * myIntArr[2] => 3
     * 
     * Ex 2: splitWholeFraction(2.75,5,myIntArr);
     * myIntArr[0] => 2
     * myIntArr[1] => 75000 //75000 is the 5 digit fractional part.
     * myIntArr[2] => 5
     * @param value This will be parsed and modified into {@code valueParts} array.
     * @param maxDigits The number of digits that the max fraction will represent.
     * @param valueParts This array must have at least 3 elements.
     * The first element will contain the whole number part (no rounding up)
     * The second element will contain the fraction part as a whole number with maxDigit base10 count.
     * The third element will contain the maxDigits value. This represents the number of base10 digits the fraction piece has.
     */
    public static void splitWholeFraction(double value, int maxDigits, int[] valueParts){
        int whole = (int)Math.abs(value);
        valueParts[0] = whole;

        //Initial fraction digit
        int fraction = (int)Math.round(tenToPow[maxDigits]*(value - whole));
        valueParts[1] = fraction;
        valueParts[2] = maxDigits;
    }

    //TODO: This will need to be sped up, replace the fraction piece with base10 constant
    /**
     * Will alter the first 3 elements of array to be the whole number, whole fraction, and number of digits.
     * This is similar to {@code splitWholeFraction} except will trim any trailing 0s off the fraction.
     * Ex 1: splitWholeFraction(4.5,3,myIntArr);
     * myIntArr[0] => 4
     * myIntArr[1] => 5 //5 is the 1 digit fractional part.
     * myIntArr[2] => 1
     * 
     * Ex 2: splitWholeFraction(2.75,5,myIntArr);
     * myIntArr[0] => 2
     * myIntArr[1] => 75 //75 is the 1 digit fractional part.
     * myIntArr[2] => 2
     * @param value This will be parsed and modified into {@code valueParts} array.
     * @param maxDigits The number of digits that the max fraction will represent.
     * @param valueParts This array must have at least 3 elements.
     * The first element will contain the whole number part (no rounding up).
     * The second element will contain the fraction part as a whole number with maxDigit base10 count.
     * The third element will contain the number of base10 fraction digits after trimming off 0s.
     */
    public static void splitWholeFractionTrim(double value, int maxDigits, int[] valueParts){
        int whole = (int)Math.abs(value);
        valueParts[0] = whole;

        //Initial fraction digit
        int fraction = (int)Math.round(tenToPow[maxDigits]*(value - whole));
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

    /**
     * Returns the length of a specific header 1 field. All h1 fields are static and final.
     * @param index the index value of the first array of header.
     * @return number of bits for the header 1 field.
     */
    public static byte getHeader1BitLength(int index){return H1_LEN[index];}

    /**
     * Returns total number of bits of entire header.
     * @return total number of bits of entire header.
     */
    public int getHeaderBitLength(){return h_total_len;}

    /**
     * Returns the length of a specific header 2 field.
     * @return total header 2 bit length.
     */
    public int getHeader2BitLength(){return h2_total_len;}

    /**
     * Returns the boolean of header index 0. This is a single bit boolean translation.
     * @return translated header index 0 boolean value. 1=true, 0=false.
     */
    public byte getFreeFormValue(){return t_h1_freeform;} //H0

    /**
     * Returns the int of header index 1. This represents the number of milliseconds timeframe for intraday stick data.
     * @return translated header index 1 integer value.
     */
    public int getInterval(){return t_h1_int;} //H1

    /**
     * Returns the int of header index 3. This represents the number of bits for each data point.
     * @return translated header index 3 integer value.
     */
    public int getDataBitLength(){return t_h1_data_len;} //H3

    /**
     * Returns the string of header index 11. This returns a string of the ticker symbol.
     * @return translated header index 11 integer value.
     */
    public String getSymbol(){return t_h2_sym;} //H11

    /**
     * Returns the int of header index 12. This represents the number of bits for each data point.
     * @return translated header index 12 integer value.
     */
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

    /**
     * Alters the gap bit length to the specified length.
     * @param gapBitLength Must be between 0 and 7 inclusively.
     */
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