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

public class OriginalTest{
    @Nested
    @DisplayName("Constructor Test")
    public class SmallConstructorTest{
        Original first_lexical = new Original("BTCUSD","60");
        int h1_len =
            1 +  //h1_byid
            25 + //h1_int 
            9 +  //h1_data_len
            26 + //h1_ct_len
            3 +  //h1_h_gap
            5 +  //h1_pw_len
            5 +  //h1_vw_len
            4 +  //h1_pf_len
            4;   //h1_vf_len

        int h2_len = 
            7 +  //h2_sym_len (the only fixed h2 header)
            48 + //h2_sym characters "BTCUSD" x 8 bits
            1 +  //h2_data_ct 1 bit to represent the value 0
            6;   //h2_h_gap (only need to add 6 to make the total header divisible by 8)
        
        int h_len = h1_len + h2_len;
        int data_len = 44 + 4*31 + 4*15 + 31 + 15; // UTC + 4*OHLC + V

        boolean[][] generatedHeader1 = first_lexical.genBinaryHeader1();
        boolean[] flatHeader1 = BinaryTools.genConcatenatedBoolArrays(generatedHeader1);
        boolean[][] generatedHeader2 = first_lexical.genBinaryHeader2();
        boolean[] flatHeader2 = BinaryTools.genConcatenatedBoolArrays(generatedHeader2);

        @Nested
        @DisplayName("Test Default Constructor Header 1 values independantly")
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
                assertEquals(9,bin_h1_data_len.length);  // 9 bits to represent a single databoint
                assertEquals(data_len,BinaryTools.toUnsignedInt(bin_h1_data_len));
            }

            @Test
            public void testDefault_Gap(){
                boolean[] bin_h1_gap = generatedHeader1[4];
                assertEquals(3,bin_h1_gap.length);
                //Value not applicable yet...
            }

            @Test
            public void testDefault_StickOHLC_Whole(){
                boolean[] bin_h1_pw_len = generatedHeader1[5];
                assertEquals(5,bin_h1_pw_len.length);
                assertEquals(31,BinaryTools.toUnsignedInt(bin_h1_pw_len));
            }

            @Test
            public void testDefault_StickOHLC_Fraction(){
                boolean[] bin_h1_pf_len = generatedHeader1[6];
                assertEquals(4,bin_h1_pf_len.length);
                assertEquals(15,BinaryTools.toUnsignedInt(bin_h1_pf_len));
            }
            @Test
            public void testDefault_StickVolume_Whole(){
                boolean[] bin_h1_vw_len = generatedHeader1[7];
                assertEquals(5,bin_h1_vw_len.length);
                assertEquals(31,BinaryTools.toUnsignedInt(bin_h1_vw_len));
            }

            @Test
            public void testDefault_StickVolume_Fraction(){
                boolean[] bin_h1_vf_len = generatedHeader1[8];
                assertEquals(4,bin_h1_vf_len.length);
                assertEquals(15,BinaryTools.toUnsignedInt(bin_h1_vf_len));
            }
        }

        @Nested
        @DisplayName("Test Default Constructor Header 2 values independantly")
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
            public void testDefault_gap(){
                boolean[] bin_h2_h_gap = generatedHeader2[3];;
                int  exptected_total_length = 
                    first_lexical.H1_TOTAL_LEN +
                    7 +
                    48 +
                    1 +
                    6; //This is the gap, With
                int expected_gap_bit_length = 6; //2 more bits to get a total of 

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
            assertEquals(82,h1_len);
            assertEquals(h1_len,first_lexical.H1_TOTAL_LEN);
            assertEquals(first_lexical.H1_TOTAL_LEN,flatHeader1.length);
            assertEquals(flatHeader1.length,first_lexical.H1_TOTAL_LEN);
        }

        @Test
        public void testDefaultHeader2(){
            assertEquals(62,h2_len);
            assertEquals(h2_len,first_lexical.getHeader2BitLength());
        }

        @Test
        public void testDefaultHeader(){
            assertEquals(144,h1_len+h2_len);
            assertEquals(144,h_len);
            assertEquals(h_len,first_lexical.getHeaderBitLength());
            assertEquals(0,h_len%8);
        }
    }
}