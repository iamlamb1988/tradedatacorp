/**
 * @author Bruce Lamb
 * @since 20 JAN 2025
 */
package tradedatacorp.smelter.lexical.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tradedatacorp.smelter.lexical.binary.BinaryTools;

public class BinaryToolsTest{
    final boolean T = true;
    final boolean F = false;

    @Nested
    @DisplayName("Small Binary Conversions")
    class SmallBinaryConversionTest{
        boolean[] five = new boolean[]{T,F,T}; // 101 == 5 unsigned == -3 2sComp
        boolean[] seven = new boolean[]{T,T,T}; // 111 = 7 signed == -1 2sComp
        boolean[] another_seven = new boolean[]{F,T,T,T}; // 0111 = 7 signed and 2sComp
        boolean[] empty = new boolean[0];

        @Test
        public void testIntegerConversions(){
            assertEquals(5,BinaryTools.toUnsignedInt(five));
            assertEquals(-3,BinaryTools.to2sCompInt(five));
            assertEquals(5,BinaryTools.toUnsignedLong(five));
            assertEquals(-3,BinaryTools.to2sCompLong(five));

            assertEquals(7,BinaryTools.toUnsignedInt(seven));
            assertEquals(-1,BinaryTools.to2sCompInt(seven));
            assertEquals(7,BinaryTools.toUnsignedLong(seven));
            assertEquals(-1,BinaryTools.to2sCompLong(seven));

            assertEquals(7,BinaryTools.toUnsignedInt(another_seven));
            assertEquals(7,BinaryTools.to2sCompInt(another_seven));
            assertEquals(7,BinaryTools.toUnsignedLong(another_seven));
            assertEquals(7,BinaryTools.to2sCompLong(another_seven));
        }

        @Test
        public void testExceptions(){
            assertThrows(IndexOutOfBoundsException.class,() -> BinaryTools.toUnsignedInt(empty));
            assertThrows(IndexOutOfBoundsException.class,() -> BinaryTools.to2sCompInt(empty));
            assertThrows(IndexOutOfBoundsException.class,() -> BinaryTools.toUnsignedLong(empty));
            assertThrows(IndexOutOfBoundsException.class,() -> BinaryTools.to2sCompLong(empty));
        }
    }

    @Nested
    @DisplayName("Binary Array Comparisons")
    class SmallArrayComparisonTest{
        boolean[] three = new boolean[]{F,T,T}; //011 == 3 signed and 2sComp
        boolean[] seven = new boolean[]{T,T,T}; // 111 = 7 signed == -1 2sComp
        boolean[] another_seven = new boolean[]{F,T,T,T}; // 0111 = 7 signed and 2sComp
        boolean[] empty1 = new boolean[0];
        boolean[] empty2 = new boolean[0];

        @Test
        public void testCompareClones(){
            boolean[] cloned_three = BinaryTools.genClone(three);
            boolean[] cloned_seven = BinaryTools.genClone(seven);

            assertTrue(BinaryTools.isEqualBoolArray(three,cloned_three));
            assertTrue(BinaryTools.isEqualBoolArray(seven,cloned_seven));

            assertTrue(BinaryTools.isEqualUnsignedIntValue(three,cloned_three));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(seven,cloned_seven));

            assertTrue(BinaryTools.isEqual2sCompIntValue(three,cloned_three));
            assertTrue(BinaryTools.isEqual2sCompIntValue(seven,cloned_seven));
        }

        @Test
        public void testCompareSeven(){
            assertFalse(BinaryTools.isEqualBoolArray(seven,another_seven));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(seven,another_seven));
        }

        @Test
        public void testFalseComparisons(){
            assertFalse(BinaryTools.isEqualBoolArray(three,seven));
            assertFalse(BinaryTools.isEqualUnsignedIntValue(three,seven));

            assertFalse(BinaryTools.isEqualBoolArray(three,another_seven));
            assertFalse(BinaryTools.isEqualUnsignedIntValue(three,another_seven));
        }

        @Test
        public void testEmptyBooleanArray(){
            assertTrue(BinaryTools.isEqualBoolArray(empty1,empty2));
        }

        @Test
        public void testExceptions(){
            assertThrows(IndexOutOfBoundsException.class,() -> BinaryTools.isEqualUnsignedIntValue(empty2,empty1));
            assertThrows(IndexOutOfBoundsException.class,() -> BinaryTools.isEqualUnsignedIntValue(empty1,three));
            assertThrows(IndexOutOfBoundsException.class,() -> BinaryTools.isEqualUnsignedIntValue(empty2,seven));
        }
    }

    @Nested
    @DisplayName("Small Integers to Binary Arrays")
    class SmallIntegerToArrayConversions{
        boolean[] three = new boolean[]{F,T,T}; //011 == 3 signed and 2sComp
        boolean[] five = new boolean[]{T,F,T}; // 101 == 5 unsigned == -3 2sComp
        boolean[] seven = new boolean[]{T,T,T}; // 111 = 7 signed == -1 2sComp
        boolean[] another_seven = new boolean[]{F,T,T,T}; // 0111 = 7 signed and 2sComp

        int int_three=3;
        long long_three=3;

        int int_five=5;
        long long_five=5;

        int int_seven = 7;
        long long_seven = 7;

        @Test
        public void testGenerateAndCompareThree(){
            boolean[] new_three_5_bits = BinaryTools.genBoolArrayFromUnsignedInt(int_three,(byte)5); //00011
            boolean[] another_new_three_3_bits = BinaryTools.genBoolArrayFromUnsignedInt(int_three,(byte)3); //011

            assertTrue(BinaryTools.isEqualUnsignedIntValue(three,new_three_5_bits));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(three,another_new_three_3_bits));

            assertFalse(BinaryTools.isEqualBoolArray(three,new_three_5_bits));
            assertTrue(BinaryTools.isEqualBoolArray(three,another_new_three_3_bits));

            boolean[] new_long_three_5_bits = BinaryTools.genBoolArrayFromUnsignedLong(long_three,(byte)5); //00011
            boolean[] another_new_long_three_3_bits = BinaryTools.genBoolArrayFromUnsignedLong(long_three,(byte)3); //011

            assertTrue(BinaryTools.isEqualUnsignedLongValue(three,new_three_5_bits));
            assertTrue(BinaryTools.isEqualUnsignedLongValue(three,another_new_three_3_bits));

            assertFalse(BinaryTools.isEqualBoolArray(three,new_three_5_bits));
            assertTrue(BinaryTools.isEqualBoolArray(three,another_new_three_3_bits));

            boolean[] minimum_bits_three = BinaryTools.genUnsignedIntToMinimumBoolArray(int_three);

            assertEquals(2,minimum_bits_three.length);
            assertTrue(BinaryTools.isEqualBoolArray(minimum_bits_three,new boolean[]{T,T}));
        }
    }

    @Nested
    @DisplayName("Small Array Negations")
    class SmallArrayNegations{
        boolean[] negative_twelve = new boolean[]{T,F,T,F,F}; //10100 == 20 unsigned == -12 2sComp
        boolean[] negative_five = new boolean[]{T,T,T,F,T,T}; //111011 == -5 2s comp == 59 unsigned

        boolean[] twelve = new boolean[]{F,T,T,F,F}; //01100
        boolean[] generated_twelve = BinaryTools.gen2sCompNegate(negative_twelve);

        boolean[] five = new boolean[]{F,F,F,T,F,T};
        boolean[] generated_five = BinaryTools.gen2sCompNegate(negative_five);

        @Test
        public void testUnsigendAnd2sCompPreflight(){
            assertEquals(-12,BinaryTools.to2sCompInt(negative_twelve));
            assertEquals(-5,BinaryTools.to2sCompInt(negative_five));

            assertEquals(20,BinaryTools.toUnsignedInt(negative_twelve));
            assertEquals(59,BinaryTools.toUnsignedInt(negative_five));
        }

        @Test
        public void test2sCompFiveNegations(){
            assertTrue(BinaryTools.isEqualBoolArray(five,generated_five));
            assertEquals(5,BinaryTools.toUnsignedInt(generated_five));
            assertEquals(5,BinaryTools.to2sCompInt(generated_five));
            assertEquals(5,BinaryTools.toUnsignedInt(five));
            assertEquals(5,BinaryTools.to2sCompInt(five));
        }

        @Test
        public void test2sCompTwelveNegations(){
            assertTrue(BinaryTools.isEqualBoolArray(twelve,generated_twelve));
            assertEquals(12,BinaryTools.toUnsignedInt(generated_twelve));
            assertEquals(12,BinaryTools.to2sCompInt(generated_twelve));
            assertEquals(12,BinaryTools.toUnsignedInt(twelve));
            assertEquals(12,BinaryTools.to2sCompInt(twelve));
        }
    }

    @Nested
    @DisplayName("Small Array Set Mutations")
    class SmallArraySetMutations{
        boolean[] three_3bit = new boolean[]{F,T,T};
        boolean[] negative_three_3bit = new boolean[]{T,F,T};

        @Test
        public void testThreeMutations(){
            boolean[] three_bits = new boolean[3];
            //pre flight check
            assertEquals(3,BinaryTools.to2sCompInt(three_3bit));
            assertEquals(3,BinaryTools.toUnsignedInt(three_3bit));

            assertEquals(-3,BinaryTools.to2sCompInt(negative_three_3bit));
            assertEquals(5,BinaryTools.toUnsignedInt(negative_three_3bit));

            //Mutation and inspection
            BinaryTools.setUnsignedIntToBoolArray(3,three_bits);

            assertTrue(BinaryTools.isEqualBoolArray(three_3bit,three_bits));
            assertTrue(BinaryTools.isEqual2sCompIntValue(three_3bit,three_bits));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(three_3bit,three_bits));

            assertEquals(3,BinaryTools.to2sCompInt(three_bits));
            assertEquals(3,BinaryTools.toUnsignedInt(three_bits));

            //Mutation and inspection
            BinaryTools.setUnsignedIntToBoolArray(5,three_bits);

            assertTrue(BinaryTools.isEqualBoolArray(negative_three_3bit,three_bits));
            assertTrue(BinaryTools.isEqual2sCompIntValue(negative_three_3bit,three_bits));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(negative_three_3bit,three_bits));

            assertEquals(-3,BinaryTools.to2sCompInt(three_bits));
            assertEquals(5,BinaryTools.toUnsignedInt(three_bits));
        }
    }

    @Nested
    @DisplayName("Small Array Negation Mutations")
    class SmallArrayMutations{
        boolean[] negative_twelve = new boolean[]{T,F,T,F,F}; //10100 == 20 unsigned == -12 2sComp
        boolean[] negative_five = new boolean[]{T,T,T,F,T,T}; //111011 == -5 2s comp == 59 unsigned

        boolean[] five = new boolean[]{F,F,F,T,F,T}; //000101 == 5 2s comp and unsigned
        boolean[] twelve = new boolean[]{F,T,T,F,F};

        @Test
        public void testNegativeFiveMutation(){
            boolean[] six_bits = BinaryTools.genClone(negative_five);

            //preflight check of successful clone
            assertTrue(BinaryTools.isEqualBoolArray(negative_five,six_bits));
            assertTrue(BinaryTools.isEqual2sCompIntValue(negative_five,six_bits));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(negative_five,six_bits));

            assertEquals(-5,BinaryTools.to2sCompInt(six_bits));
            assertEquals(59,BinaryTools.toUnsignedInt(six_bits));

            //Mutation and inspection
            BinaryTools.set2sCompNegate(six_bits); //Mutation: negates the existing array (from -5 to 5)

            assertTrue(BinaryTools.isEqualBoolArray(five,six_bits));
            assertTrue(BinaryTools.isEqual2sCompIntValue(five,six_bits));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(five,six_bits));

            assertEquals(5,BinaryTools.to2sCompInt(six_bits));
            assertEquals(5,BinaryTools.toUnsignedInt(six_bits));
        }

        @Test
        public void testNegativeTwelveMutation(){
            boolean[] five_bits = BinaryTools.genClone(negative_twelve);

            //preflight check of successful clone
            assertTrue(BinaryTools.isEqualBoolArray(negative_twelve,five_bits));
            assertTrue(BinaryTools.isEqual2sCompIntValue(negative_twelve,five_bits));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(negative_twelve,five_bits));

            assertEquals(-12,BinaryTools.to2sCompInt(five_bits));
            assertEquals(20,BinaryTools.toUnsignedInt(five_bits));

            //Mutation and inspection
            BinaryTools.set2sCompNegate(five_bits); //Mutation: negates the existing array (from -12 to 12)

            assertTrue(BinaryTools.isEqualBoolArray(twelve,five_bits));
            assertTrue(BinaryTools.isEqual2sCompIntValue(twelve,five_bits));
            assertTrue(BinaryTools.isEqualUnsignedIntValue(twelve,five_bits));

            assertEquals(12,BinaryTools.to2sCompInt(five_bits));
            assertEquals(12,BinaryTools.toUnsignedInt(five_bits));
        }
    }

    @Nested
    @DisplayName("Small strings to 8 bit chars")
    class SmallStringsWith8bitChar{
        String cat1 = "cat";
        String cat2 = "Cat";
        String cat3 = "CAT";

        byte ascii_A = (byte)'A';
        byte ascii_a = (byte)'a';
        byte ascii_C = (byte)'C';
        byte ascii_c = (byte)'c';
        byte ascii_T = (byte)'T';
        byte ascii_t = (byte)'t';

        boolean[] bin_A = new boolean[]{F,T,F,F,F,F,F,T}; //65
        boolean[] bin_a = new boolean[]{F,T,T,F,F,F,F,T}; //97
        boolean[] bin_C = new boolean[]{F,T,F,F,F,F,T,T}; //67
        boolean[] bin_c = new boolean[]{F,T,T,F,F,F,T,T}; //99
        boolean[] bin_T = new boolean[]{F,T,F,T,F,T,F,F}; //84
        boolean[] bin_t = new boolean[]{F,T,T,T,F,T,F,F}; //116

        @Test
        public void testChars(){
            assertEquals(65,(byte)'A');
            assertEquals(97,(byte)'a');
            assertEquals(67,(byte)'C');
            assertEquals(99,(byte)'c');
            assertEquals(84,(byte)'T');
            assertEquals(116,(byte)'t');

            assertEquals(65,BinaryTools.toUnsignedInt(bin_A));
            assertEquals(97,BinaryTools.toUnsignedInt(bin_a));
            assertEquals(67,BinaryTools.toUnsignedInt(bin_C));
            assertEquals(99,BinaryTools.toUnsignedInt(bin_c));
            assertEquals(84,BinaryTools.toUnsignedInt(bin_T));
            assertEquals(116,BinaryTools.toUnsignedInt(bin_t));
        }

        @Test
        public void testCat1(){
            boolean[] cat1_8bitCharBin = BinaryTools.genBoolArrayFrom8BitCharString(cat1); // cat
            boolean[] another_cat1_8bitCharBin = BinaryTools.genConcatenatedBoolArrays(bin_c, bin_a, bin_t);
            String another_cat1 = BinaryTools.genStringFrom8BitBoolCharRep(cat1_8bitCharBin);

            assertEquals(cat1,another_cat1);
            assertEquals(8*3,cat1_8bitCharBin.length);
            assertTrue(BinaryTools.isEqualBoolArray(cat1_8bitCharBin,another_cat1_8bitCharBin));
        }

        @Test
        public void testCat2(){
            boolean[] cat2_8bitCharBin = BinaryTools.genBoolArrayFrom8BitCharString(cat2); // Cat
            boolean[] another_cat2_8bitCharBin = BinaryTools.genConcatenatedBoolArrays(bin_C, bin_a, bin_t);
            String another_cat2 = BinaryTools.genStringFrom8BitBoolCharRep(cat2_8bitCharBin);

            assertEquals(cat2,another_cat2);
            assertEquals(8*3,cat2_8bitCharBin.length);
            assertTrue(BinaryTools.isEqualBoolArray(cat2_8bitCharBin,another_cat2_8bitCharBin));
        }

        @Test
        public void testCat3(){
            boolean[] cat3_8bitCharBin = BinaryTools.genBoolArrayFrom8BitCharString(cat3); // CAT
            boolean[] another_cat3_8bitCharBin = BinaryTools.genConcatenatedBoolArrays(bin_C, bin_A, bin_T);
            String another_cat3 = BinaryTools.genStringFrom8BitBoolCharRep(cat3_8bitCharBin);

            assertEquals(cat3,another_cat3);
            assertEquals(8*3,cat3_8bitCharBin.length);
            assertTrue(BinaryTools.isEqualBoolArray(cat3_8bitCharBin,another_cat3_8bitCharBin));
        }
    }
}
