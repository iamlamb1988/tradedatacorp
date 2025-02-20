/**
 * @author Bruce Lamb
 * @since 9 FEB 2025
 */
package tradedatacorp.smelter.lexical.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

public class OriginalTest{
    Original first_lexical = Original.genStandardAlignedLexical("BTCUSD","60");
    int expected_h1_len =
    1 +  //h1_byid
    25 + //h1_int 
    26 + //h1_ct_len
    9 +  //h1_data_len
    3 +  //h1_h_gap_len
    6 +  //h1_utc_len
    6 +  //h1_pw_len
    6 +  //h1_vw_len
    6 +  //h1_pf_len
    6 +  //h1_vf_len
    7;   //h1_sym_len

int expected_h2_len = 
    48 + //h2_sym characters "BTCUSD" x 8 bits
    1 +  //h2_data_ct_len 1 bit to represent the value 0
    2;   //h2_h_gap (only need to add 0 to make the total header divisible by 8) EX: 8 - (h1_len + h2_sym_len + h2_sym + h2_dada_ct_l3n)%8

    int expected_h_len = expected_h1_len + expected_h2_len;
    int expected_data_len = 44 + 4*31 + 4*15 + 31 + 15; // UTC + 4*OHLC + V

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
                assertEquals(6,bin_h1_pw_len.length);
                assertEquals(31,BinaryTools.toUnsignedInt(bin_h1_pw_len));
            }

            @Test
            public void testDefault_StickOHLC_Fraction(){
                boolean[] bin_h1_pf_len = generatedHeader1[7];
                assertEquals(6,bin_h1_pf_len.length);
                assertEquals(15,BinaryTools.toUnsignedInt(bin_h1_pf_len));
            }
            @Test
            public void testDefault_StickVolume_Whole(){
                boolean[] bin_h1_vw_len = generatedHeader1[8];
                assertEquals(6,bin_h1_vw_len.length);
                assertEquals(31,BinaryTools.toUnsignedInt(bin_h1_vw_len));
            }

            @Test
            public void testDefault_StickVolume_Fraction(){
                boolean[] bin_h1_vf_len = generatedHeader1[9];
                assertEquals(6,bin_h1_vf_len.length);
                assertEquals(15,BinaryTools.toUnsignedInt(bin_h1_vf_len));
            }
        }

        @Nested
        @DisplayName("Test Default Constructor Header 2 values independently")
        public class DefaultHeader2Test{
            @Test
            public void testDefault_SymbolBitLength(){
                boolean[] bin_h1_sym_len = generatedHeader1[10];
                assertEquals(6*8,BinaryTools.toUnsignedInt(bin_h1_sym_len)); //BTCUSD has 6 characters
                assertEquals(7,Original.H1_SYM_LEN_LEN);
                assertEquals(Original.H1_SYM_LEN_LEN,bin_h1_sym_len.length); //fixed length of 7 bits
            }

            @Test
            public void testDefault_Symbol(){
                boolean[] bin_h2_sym = generatedHeader2[0];
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
                boolean[] bin_h2_data_ct = generatedHeader2[1];

                assertEquals(0,BinaryTools.toUnsignedInt(bin_h2_data_ct));
                assertEquals(1,bin_h2_data_ct.length); //should match h1_data_ct_len
            }

            @Test
            public void testDefaultGap(){
                boolean[] bin_h2_h_gap = generatedHeader2[2];;
                int  exptected_total_length = 
                    first_lexical.H1_TOTAL_LEN +
                    48 +
                    1 +
                    2; //This is the gap, With
                int expected_gap_bit_length = 2; 

                assertEquals(expected_gap_bit_length,bin_h2_h_gap.length);
            }
        }

        @Test
        public void testInitialConstructor(){
            assertEquals("BTCUSD",first_lexical.getSymbol());
            assertEquals("60",first_lexical.getInterval());
    
            assertEquals(5,first_lexical.getBase10PriceDigits());  //15 bits => 2^15 => 32768 (base10) => 5 digits
            assertEquals(5,first_lexical.getBase10VolumeDigits()); //15 bits => 2^15 => 32768 (base10) => 5 digits
        }

        @Test
        public void testDefaultHeader1(){
            assertEquals(101,expected_h1_len);
            assertEquals(expected_h1_len,first_lexical.H1_TOTAL_LEN);
            assertEquals(first_lexical.H1_TOTAL_LEN,flatHeader1.length);
            assertEquals(flatHeader1.length,first_lexical.H1_TOTAL_LEN);
        }

        @Test
        public void testDefaultHeader2(){
            assertEquals(51,expected_h2_len);
            assertEquals(expected_h2_len,first_lexical.getHeader2BitLength());
        }

        @Test
        public void testDefaultHeader(){
            assertEquals(152,expected_h1_len + expected_h2_len); // 101 + 51 = 144
            assertEquals(152,expected_h_len);
            assertEquals(expected_h_len,first_lexical.getHeaderBitLength());
            assertEquals(0,expected_h_len%8);
        }
    }

    @Nested
    @DisplayName("Passing in small data. State of Lexical is preserved")
    public class SmallInterfaceTest{
        CandleStickFixedDouble stick1 = new CandleStickFixedDouble(
            1000, //UTC
            3,    //Open
            9,    //High
            1,    //Low
            5,    //Clse
            1.25  //Volume
        );

        boolean[][] binStick1 = first_lexical.getBinaryData(stick1);
        boolean[] binFlatStick1 = first_lexical.getBinaryDataFlat(stick1);

        StickDouble reverseStick1 = first_lexical.getRefinedData(binStick1);
        StickDouble reverseFlatStick1 = first_lexical.getRefinedDataFlat(binFlatStick1);

        boolean[] utcBin1 = binStick1[0];
        boolean[] openWholeBin1 = binStick1[1];
        boolean[] openFractionBin1 = binStick1[2];
        boolean[] highWholeBin1 = binStick1[3];
        boolean[] highFractionBin1 = binStick1[4];
        boolean[] lowWholeBin1 = binStick1[5];
        boolean[] lowFractionBin1 = binStick1[6];
        boolean[] closeWholeBin1 = binStick1[7];
        boolean[] closeFractionBin1 = binStick1[8];
        boolean[] volumeWholeBin1 = binStick1[9];
        boolean[] volumeFractionBin1 = binStick1[10];

        CandleStickFixedDouble stick2 = new CandleStickFixedDouble(
            2000,     //UTC
            4.125,    //Open
            9.0059,   //High
            1.00,     //Low
            5.100,    //Clse
            1.25      //Volume
        );

        boolean[][] binStick2 = first_lexical.getBinaryData(stick2);
        boolean[] binFlatStick2 = first_lexical.getBinaryDataFlat(stick2);

        StickDouble reverseStick2 = first_lexical.getRefinedData(binStick2);
        StickDouble reverseFlatStick2 = first_lexical.getRefinedDataFlat(binFlatStick2);

        boolean[] utcBin2 = binStick1[0];
        boolean[] openWholeBin2 = binStick1[1];
        boolean[] openFractionBin2 = binStick1[2];
        boolean[] highWholeBin2 = binStick1[3];
        boolean[] highFractionBin2 = binStick1[4];
        boolean[] lowWholeBin2 = binStick1[5];
        boolean[] lowFractionBin2 = binStick1[6];
        boolean[] closeWholeBin2 = binStick1[7];
        boolean[] closeFractionBin2 = binStick1[8];
        boolean[] volumeWholeBin2 = binStick1[9];
        boolean[] volumeFractionBin2 = binStick1[10];

        //Array bin with stick1 and stick2
        CandleStickFixedDouble[] stickArray = new CandleStickFixedDouble[]{stick1,stick2};

        boolean[][][] inflatedBinArray = first_lexical.getBinaryDataPoints(stickArray);
        boolean[] flatBinArray = first_lexical.getBinaryDataPointsFlat(stickArray);

        StickDouble[] reverseStickArray = first_lexical.getRefinedDataArray(inflatedBinArray);
        StickDouble[] reverseFlatStickArray = first_lexical.getRefinedDataArrayFlat(flatBinArray);

        @Test
        public void testPreflightDataStick1(){
            assertEquals(3,stick1.getO());
            assertEquals(9,stick1.getH());
            assertEquals(1,stick1.getL());
            assertEquals(5,stick1.getC());
            assertEquals(1.25,stick1.getV());
        }

        @Test
        public void testFlatArrayEquivalents1(){
            boolean[] manuallyFlatInflatedBin = BinaryTools.genConcatenatedBoolArrays(binStick1);
            assertTrue(BinaryTools.isEqualBoolArray(binFlatStick1,manuallyFlatInflatedBin));
        }

        @Test
        public void testSingleDataInflatedBinStick1(){
            //Check Stick
            assertEquals(44,utcBin1.length);
            assertEquals(1000,BinaryTools.toUnsignedLong(utcBin1));

            //Open
            assertEquals(31,openWholeBin1.length);
            assertEquals(3,BinaryTools.toUnsignedInt(openWholeBin1));

            assertEquals(15,openFractionBin1.length);
            assertEquals(0,BinaryTools.toUnsignedInt(openFractionBin1));

            //High
            assertEquals(31,highWholeBin1.length);
            assertEquals(9,BinaryTools.toUnsignedInt(highWholeBin1));

            assertEquals(15,highFractionBin1.length);
            assertEquals(0,BinaryTools.toUnsignedInt(highFractionBin1));

            //Low
            assertEquals(31,lowWholeBin1.length);
            assertEquals(1,BinaryTools.toUnsignedInt(lowWholeBin1));

            assertEquals(15,lowFractionBin1.length);
            assertEquals(0,BinaryTools.toUnsignedInt(lowFractionBin1));

            //Close
            assertEquals(31,closeWholeBin1.length);
            assertEquals(5,BinaryTools.toUnsignedInt(closeWholeBin1));

            assertEquals(15,closeFractionBin1.length);
            assertEquals(0,BinaryTools.toUnsignedInt(closeFractionBin1));

            //Volume
            assertEquals(31,volumeWholeBin1.length);
            assertEquals(1,BinaryTools.toUnsignedInt(volumeWholeBin1));

            assertEquals(15,volumeFractionBin1.length);
            assertEquals(25000,BinaryTools.toUnsignedInt(volumeFractionBin1)); //5 Decimal Digits

            //Total Data length
            assertEquals(274,expected_data_len); //44 + 4*31 + 4*15 + 31 + 15
            assertEquals(expected_data_len,first_lexical.getDataBitLength());
            assertEquals(expected_data_len,binFlatStick1.length);
        }

        @Test
        public void testDecodedUTC1(){assertEquals(stick1.UTC,reverseStick1.getUTC());}

        @Test
        public void testDecodedOpen1(){assertEquals(stick1.O,reverseStick1.getO());}

        @Test
        public void testDecodedHigh1(){assertEquals(stick1.H,reverseStick1.getH());}

        @Test
        public void testDecodedLow1(){assertEquals(stick1.L,reverseStick1.getL());}

        @Test
        public void testDecodedClose1(){assertEquals(stick1.C,reverseStick1.getC());}

        @Test
        public void testDecodedVolume1(){assertEquals(stick1.V,reverseStick1.getV());}

        @Test
        public void testDecodedStick1toOriginalStick1(){assertTrue(StickDouble.isEqual(stick1, reverseStick1));}

        @Test
        public void testDecodedFlatUTC1(){assertEquals(stick1.UTC,reverseFlatStick1.getUTC());}

        @Test
        public void testDecodedFlatOpen1(){assertEquals(stick1.O,reverseFlatStick1.getO());}

        @Test
        public void testDecodedFlatHigh1(){assertEquals(stick1.H,reverseFlatStick1.getH());}

        @Test
        public void testDecodedFlatLow1(){assertEquals(stick1.L,reverseFlatStick1.getL());}

        @Test
        public void testDecodedFlatClose1(){assertEquals(stick1.C,reverseFlatStick1.getC());}

        @Test
        public void testDecodedFlatVolume1(){assertEquals(stick1.V,reverseFlatStick1.getV());}

        @Test
        public void testDecodedFlatStick1toOriginalStick1(){assertTrue(StickDouble.isEqual(stick2, reverseStick2));}

        @Test
        public void testMultipleSticksOneElement(){
            CandleStickFixedDouble[] singleElementStickArray = new CandleStickFixedDouble[]{stick1};

            boolean[][] singleInflatedBin = first_lexical.getBinaryData(stick1);
            boolean[][][] inflatedBinArray = first_lexical.getBinaryDataPoints(singleElementStickArray);
            boolean[] singleFlatBin = first_lexical.getBinaryDataFlat(stick1);
            boolean[] flatBinArray = first_lexical.getBinaryDataPointsFlat(singleElementStickArray);

            boolean[] manuallySingleFlattenedBin = BinaryTools.genConcatenatedBoolArrays(singleInflatedBin);
            boolean[] manuallySingleFlattenedBinArray = BinaryTools.genConcatenatedBoolArrays(inflatedBinArray[0]);

            //ALL 4 single arrays must be equal (length and values)
            assertTrue(BinaryTools.isEqualBoolArray(manuallySingleFlattenedBin,manuallySingleFlattenedBinArray));
            assertTrue(BinaryTools.isEqualBoolArray(flatBinArray,manuallySingleFlattenedBin));
            assertTrue(BinaryTools.isEqualBoolArray(singleFlatBin,flatBinArray));

            //Extra confirmation
            assertEquals(274,expected_data_len); //Only one data point
            assertEquals(expected_data_len,first_lexical.getDataBitLength());
            assertEquals(expected_data_len,flatBinArray.length);
        }

        @Test
        public void testMultipleSticksTwoELements(){
            boolean[] manuallyFlatBinArray = BinaryTools.genConcatenatedBoolArrays(inflatedBinArray);

            assertFalse(StickDouble.isEqual(stick1, stick2));
            assertTrue(BinaryTools.isEqualBoolArray(flatBinArray,manuallyFlatBinArray));

            //Extra Confirmation
            assertEquals(2*expected_data_len,flatBinArray.length); //two sticks == twice as many bits
            assertEquals(2*expected_data_len,manuallyFlatBinArray.length);
        }

        @Test
        public void testDecodedTwoElements(){
            assertTrue(StickDouble.isEqual(stick1, reverseFlatStick1));
            assertTrue(StickDouble.isEqual(stick2, reverseFlatStick2));
        }
    }
}
