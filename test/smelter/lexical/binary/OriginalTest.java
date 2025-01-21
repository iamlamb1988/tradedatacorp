/**
 * @author Bruce Lamb
 * @since 20 JAN 2025
 */
package tradedatacorp.smelter.lexical.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        @Test
        public void testInitialConstructor(){
            assertEquals("BTCUSD",first_lexical.getSymbol());
            assertEquals("60",first_lexical.getInterval());
        }

        @Test
        public void testDefaultHeader1(){
            assertEquals(84,h1_len); //84 bits for h1
        }
    }
}