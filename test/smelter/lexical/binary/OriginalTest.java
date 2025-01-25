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
                assertEquals(6*8,bin_h2_sym.length); //BTCUSD has 6 characters
            }
        }

        @Test
        public void testInitialConstructor(){
            assertEquals("BTCUSD",first_lexical.getSymbol());
            assertEquals("60",first_lexical.getInterval());
        }

        @Test
        public void testDefaultHeader1(){
            assertEquals(82,first_lexical.H1_TOTAL_LEN);
            assertEquals(first_lexical.H1_TOTAL_LEN,flatHeader1.length);
            assertEquals(flatHeader1.length,first_lexical.H1_TOTAL_LEN);
        }
    }
}