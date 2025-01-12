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
        boolean[] seven = new boolean[]{T,T,T}; // 111 = 7 signed == -1 2sComp
        boolean[] another_seven = new boolean[]{F,T,T,T}; // 0111 = 7 signed and 2sComp
        boolean[] three = new boolean[]{F,T,T}; //011 == 3 signed and 2sComp
        boolean[] empty1 = new boolean[0];
        boolean[] empty2 = new boolean[0];

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
}
