package tradedatacorp.smelter.lexical.binary;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    class SmallTest{
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
}
