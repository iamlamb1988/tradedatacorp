/**
 * @author Bruce Lamb
 * @since 20 JAN 2025
 */
package tradedatacorp.smelter.lexical.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

public class OriginalTest{
    Original first_lexical = new Original("BTCUSD","60");

    @Nested
    @DisplayName("Split Fraction and Decimal")
    public class FractionAndDecimal{
        @Test
        public void test(){
            int[] val = new int[3];
            double pi = 3.14;
            double pi2 = 3.1400; 
            double e = 2.718;
            double num = 13.0061;
            
            Original.splitWholeFraction(pi,5,val); //Set to check up to 5 decimal places

            //pi
            assertEquals(3,val[0]);
            assertEquals(14000,val[1]); //key check NOT 14000 even though 5 decimals are placed. Ignores trailing 0's

            Original.splitWholeFractionTrim(pi,5,val); //Set to check up to 5 decimal places
            assertEquals(3,val[0]);
            assertEquals(14,val[1]); //key check NOT 14000 even though 5 decimals are placed. Ignores trailing 0's

            //pi2
            Original.splitWholeFraction(pi2,5,val);

            assertEquals(3,val[0]);
            assertEquals(14000,val[1]);

            Original.splitWholeFractionTrim(pi2,5,val);

            assertEquals(3,val[0]);
            assertEquals(14,val[1]);

            //e
            Original.splitWholeFraction(e,5,val);

            assertEquals(2,val[0]);
            assertEquals(71800,val[1]);

            Original.splitWholeFractionTrim(e,5,val);

            assertEquals(2,val[0]);
            assertEquals(718,val[1]);

            //num
            Original.splitWholeFraction(num,5,val);

            assertEquals(13,val[0]);
            assertEquals(610,val[1]);

            Original.splitWholeFractionTrim(num,5,val);

            assertEquals(13,val[0]);
            assertEquals(61,val[1]);
        }
    }

    @Nested
    @DisplayName("Constructor Test")
    public class SmallConstructorTest{
        int expected_h1_len =
            1 +  //h1_byid
            25 + //h1_int 
            26 + //h1_ct_len
            9 +  //h1_data_len
            3 +  //h1_h_gap_len
            6 +  //h1_utc_len
            5 +  //h1_pw_len
            5 +  //h1_vw_len
            4 +  //h1_pf_len
            4;   //h1_vf_len

        int expected_h2_len = 
            7 +  //h2_sym_len (the only fixed h2 header)
            48 + //h2_sym characters "BTCUSD" x 8 bits
            1 +  //h2_data_ct_len 1 bit to represent the value 0
            0;   //h2_h_gap (only need to add 0 to make the total header divisible by 8) EX: 8 - (h1_len + h2_sym_len + h2_sym + h2_dada_ct_l3n)%8

        int expected_h_len = expected_h1_len + expected_h2_len;
        int expected_data_len = 44 + 4*31 + 4*15 + 31 + 15; // UTC + 4*OHLC + V

        boolean[][] generatedHeader1 = first_lexical.genBinaryHeader1();
        boolean[] flatHeader1 = BinaryTools.genConcatenatedBoolArrays(generatedHeader1);
        boolean[][] generatedHeader2 = first_lexical.genBinaryHeader2();
        boolean[] flatHeader2 = BinaryTools.genConcatenatedBoolArrays(generatedHeader2);

        @Nested
        @DisplayName("Test Default Constructor Header 1 values independently")
        public class DefaultHeader1Test{
            @Test
            public void testDefault_ByID(){
            assertEquals("BTCUSD",first_lexical.getSymbol());
                assertFalse(generatedHeader1[0][0]);
                assertEquals(false,first_lexical.getByID());
            }

            @Test
            public void testDefault_Interval(){
                boolean[] bin_h1_int = generatedHeader1[1];
                assertEquals(60,BinaryTools.toUnsignedInt(bin_h1_int));
                assertEquals(25,bin_h1_int.length);
            }

            @Test
            public void testDefault_DataCountLength(){
                boolean[] bin_h1_ct_len = generatedHeader1[2];
                assertEquals(26,bin_h1_ct_len.length);
                assertEquals(0,BinaryTools.toUnsignedInt(bin_h1_ct_len)); //0 data points
            }

            @Test
            public void testDefault_IndividualDataBitLength(){
                boolean[] bin_h1_data_len = generatedHeader1[3];
                assertEquals(9,bin_h1_data_len.length);  // 9 bits to represent a single datapoint
                assertEquals(expected_data_len,BinaryTools.toUnsignedInt(bin_h1_data_len));
            }

            @Test
            public void testDefaultGap(){
                boolean[] bin_h1_gap = generatedHeader1[4];
                assertEquals(3,bin_h1_gap.length);
                //Value not applicable yet...
            }

            @Test
            public void testDefaultUTC(){
                boolean[] bin_h1_utc_len = generatedHeader1[5];
                assertEquals(6,bin_h1_utc_len.length);
                assertEquals(44,BinaryTools.toUnsignedInt(bin_h1_utc_len));
            }

            @Test
            public void testDefault_StickOHLC_Whole(){
                boolean[] bin_h1_pw_len = generatedHeader1[6];
                assertEquals(5,bin_h1_pw_len.length);
                assertEquals(31,BinaryTools.toUnsignedInt(bin_h1_pw_len));
            }

            @Test
            public void testDefault_StickOHLC_Fraction(){
                boolean[] bin_h1_pf_len = generatedHeader1[7];
                assertEquals(4,bin_h1_pf_len.length);
                assertEquals(15,BinaryTools.toUnsignedInt(bin_h1_pf_len));
            }
            @Test
            public void testDefault_StickVolume_Whole(){
                boolean[] bin_h1_vw_len = generatedHeader1[8];
                assertEquals(5,bin_h1_vw_len.length);
                assertEquals(31,BinaryTools.toUnsignedInt(bin_h1_vw_len));
            }

            @Test
            public void testDefault_StickVolume_Fraction(){
                boolean[] bin_h1_vf_len = generatedHeader1[9];
                assertEquals(4,bin_h1_vf_len.length);
                assertEquals(15,BinaryTools.toUnsignedInt(bin_h1_vf_len));
            }
        }

        @Nested
        @DisplayName("Test Default Constructor Header 2 values independently")
        public class DefaultHeader2Test{
            @Test
            public void testDefault_SymbolBitLength(){
                boolean[] bin_h2_sym_len = generatedHeader2[0];
                assertEquals(6*8,BinaryTools.toUnsignedInt(bin_h2_sym_len)); //BTCUSD has 6 characters
                assertEquals(7,bin_h2_sym_len.length); //firxed length of 7 bits
            }

            @Test
            public void testDefault_Sybmbol(){
                boolean[] bin_h2_sym = generatedHeader2[1];
                byte ascii_B = (byte)'B';
                byte ascii_T = (byte)'T';
                byte ascii_C = (byte)'C';
                byte ascii_U = (byte)'U';
                byte ascii_S = (byte)'S';
                byte ascii_D = (byte)'D';

                boolean[] bin_B = BinaryTools.genSubset(0,8,bin_h2_sym);
                boolean[] bin_T = BinaryTools.genSubset(8,8,bin_h2_sym);
                boolean[] bin_C = BinaryTools.genSubset(16,8,bin_h2_sym);
                boolean[] bin_U = BinaryTools.genSubset(24,8,bin_h2_sym);
                boolean[] bin_S = BinaryTools.genSubset(32,8,bin_h2_sym);
                boolean[] bin_D = BinaryTools.genSubset(40,8,bin_h2_sym);

                assertEquals(6*8,bin_h2_sym.length); //BTCUSD has 6 characters (each are 8 bits)

                assertEquals(66,ascii_B);
                assertEquals(84,ascii_T);
                assertEquals(67,ascii_C);
                assertEquals(85,ascii_U);
                assertEquals(83,ascii_S);
                assertEquals(68,ascii_D);

                assertEquals(ascii_B,BinaryTools.toUnsignedInt(bin_B));
                assertEquals(ascii_T,BinaryTools.toUnsignedInt(bin_T));
                assertEquals(ascii_C,BinaryTools.toUnsignedInt(bin_C));
                assertEquals(ascii_U,BinaryTools.toUnsignedInt(bin_U));
                assertEquals(ascii_S,BinaryTools.toUnsignedInt(bin_S));
                assertEquals(ascii_D,BinaryTools.toUnsignedInt(bin_D));
            }

            @Test
            public void testDefault_DataCount(){
                boolean[] bin_h2_data_ct = generatedHeader2[2];

                assertEquals(0,BinaryTools.toUnsignedInt(bin_h2_data_ct));
                assertEquals(1,bin_h2_data_ct.length); //should match h1_data_ct_len
            }

            @Test
            public void testDefaultGap(){
                boolean[] bin_h2_h_gap = generatedHeader2[3];;
                int  exptected_total_length = 
                    first_lexical.H1_TOTAL_LEN +
                    7 +
                    48 +
                    1 +
                    0; //This is the gap, With
                int expected_gap_bit_length = 0; //2 more bits to get a total of 

                assertEquals(expected_gap_bit_length,bin_h2_h_gap.length);
            }
        }

        @Test
        public void testInitialConstructor(){
            assertEquals("BTCUSD",first_lexical.getSymbol());
            assertEquals("60",first_lexical.getInterval());
        }

        @Test
        public void testDefaultHeader1(){
            assertEquals(88,expected_h1_len);
            assertEquals(expected_h1_len,first_lexical.H1_TOTAL_LEN);
            assertEquals(first_lexical.H1_TOTAL_LEN,flatHeader1.length);
            assertEquals(flatHeader1.length,first_lexical.H1_TOTAL_LEN);
        }

        @Test
        public void testDefaultHeader2(){
            assertEquals(56,expected_h2_len);
            assertEquals(expected_h2_len,first_lexical.getHeader2BitLength());
        }

        @Test
        public void testDefaultHeader(){
            assertEquals(144,expected_h1_len + expected_h2_len); // 88 + 56 = 144
            assertEquals(144,expected_h_len);
            assertEquals(expected_h_len,first_lexical.getHeaderBitLength());
            assertEquals(0,expected_h_len%8);
        }
    }

    @Nested
    @DisplayName("Passing in small data. State of Lexical is preserved")
    public class SmallInterfaceTest{
        @Test
        public void testSingleDataStick(){
            CandleStickFixedDouble stick = new CandleStickFixedDouble(1000,3,9,1,5,1.25);
            assertEquals(5,first_lexical.getBase10PriceDigits());  //15 bits => 2^15 => 32768 (base10) => 5 digits
            assertEquals(5,first_lexical.getBase10VolumeDigits()); //15 bits => 2^15 => 32768 (base10) => 5 digits

            boolean[][] inflatedBin = first_lexical.getBinaryData(stick); //Core Interface Function to test
            boolean[] flatInflatedBin = first_lexical.getBinaryDataFlat(stick);
            boolean[] manuallyFlatInflatedBin = BinaryTools.genConcatenatedBoolArrays(inflatedBin);

            assertTrue(BinaryTools.isEqualBoolArray(flatInflatedBin,manuallyFlatInflatedBin));

            boolean[] utcBin = inflatedBin[0];
            boolean[] openWholeBin = inflatedBin[1];
            boolean[] openFractionBin = inflatedBin[2];
            boolean[] highWholeBin = inflatedBin[3];
            boolean[] highFractionBin = inflatedBin[4];
            boolean[] lowWholeBin = inflatedBin[5];
            boolean[] lowFractionBin = inflatedBin[6];
            boolean[] closeWholeBin = inflatedBin[7];
            boolean[] closeFractionBin = inflatedBin[8];
            boolean[] volumeWholeBin = inflatedBin[9];
            boolean[] volumeFractionBin = inflatedBin[10];

            //Check Stick
            assertEquals(3,stick.getO());
            assertEquals(9,stick.getH());
            assertEquals(1,stick.getL());
            assertEquals(5,stick.getC());
            assertEquals(1.25,stick.getV());

            assertEquals(44,utcBin.length);
            assertEquals(1000,BinaryTools.toUnsignedLong(utcBin));

            //Open
            assertEquals(31,openWholeBin.length);
            assertEquals(3,BinaryTools.toUnsignedInt(openWholeBin));

            assertEquals(15,openFractionBin.length);
            assertEquals(0,BinaryTools.toUnsignedInt(openFractionBin));

            //High
            assertEquals(31,highWholeBin.length);
            assertEquals(9,BinaryTools.toUnsignedInt(highWholeBin));

            assertEquals(15,highFractionBin.length);
            assertEquals(0,BinaryTools.toUnsignedInt(highFractionBin));

            //Low
            assertEquals(31,lowWholeBin.length);
            assertEquals(1,BinaryTools.toUnsignedInt(lowWholeBin));

            assertEquals(15,lowFractionBin.length);
            assertEquals(0,BinaryTools.toUnsignedInt(lowFractionBin));

            //Close
            assertEquals(31,closeWholeBin.length);
            assertEquals(5,BinaryTools.toUnsignedInt(closeWholeBin));

            assertEquals(15,closeFractionBin.length);
            assertEquals(0,BinaryTools.toUnsignedInt(closeFractionBin));

            //Volume
            assertEquals(31,volumeWholeBin.length);
            assertEquals(1,BinaryTools.toUnsignedInt(volumeWholeBin));

            assertEquals(15,volumeFractionBin.length);
            assertEquals(25000,BinaryTools.toUnsignedInt(volumeFractionBin)); //5 Decimal Digits
        }
    }
}
