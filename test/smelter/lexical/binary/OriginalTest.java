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
            5 +  //h1_pf_len
            5;   //h1_vf_len
        
        int data_len = 44 + 4*31 + 4*15 + 31 + 15; // UTC + 4*OHLC + V

        boolean[][] generatedHeader1 = first_lexical.genBinaryHeader1();
        boolean[] flatHeader1 = BinaryTools.genConcatenatedBoolArrays(generatedHeader1);

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
        }

        @Test
        public void testInitialConstructor(){
            assertEquals("BTCUSD",first_lexical.getSymbol());
            assertEquals("60",first_lexical.getInterval());
        }

        @Test
        public void testDefaultHeader1(){
            assertEquals(84,h1_len); //84 bits for h1
            assertEquals(84,flatHeader1.length);
        }
    }
}