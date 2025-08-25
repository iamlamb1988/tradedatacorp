/**
 * @author Bruce Lamb
 * @since 25 AUG 2025
 */
package tradedatacorp.tools.binarytools;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BitByteTrackTest{
    @Nested
    @DisplayName("BitByteTrack Constructor Tests")
    class constructorTestClass{
        @Test
        public void defaultConstructorTest(){
            BitByteTrack t = new BitByteTrack();

            assertEquals(0, t.getByteIndex());
            assertEquals(0, t.getBitIndex());
        }

        @Test
        public void constructor1p1Test(){
            BitByteTrack t = new BitByteTrack(3, 5);

            assertEquals(3, t.getByteIndex());
            assertEquals(5, t.getBitIndex());
        }

        @Test
        public void constructor1p2Test(){
            BitByteTrack t = new BitByteTrack(2, -1);
            // Logical Steps:
            // [2, -1]
            // [1, -1 + 8] //borrow 8 bits from ByteIndex
            // [1, 7]

            assertEquals(1, t.getByteIndex());
            assertEquals(7, t.getBitIndex());
        }

        @Test
        public void constructor1p3Test(){
            BitByteTrack t = new BitByteTrack(5, -20);
            // Logical Steps:
            // [5, -20]
            // [2, -20 + 24] //borrow 3*8 bits from ByteIndex
            // [2, 4]

            assertEquals(2, t.getByteIndex());
            assertEquals(4, t.getBitIndex());
        }

        @Test
        public void constructor1p4Test(){
            BitByteTrack t = new BitByteTrack(-1, 9);
            // Logical Steps:
            // [-1, 9]
            // [-1 + 1, 9 - 8 + 24]
            // [0, 1]

            assertEquals(0, t.getByteIndex());
            assertEquals(1, t.getBitIndex());
        }

        @Test
        public void constructor2p1Test(){
            BitByteTrack t = new BitByteTrack(1, 3, 5);

            assertEquals(3, t.getByteIndex());
            assertEquals(5, t.getBitIndex());
        }

        @Test
        public void constructor2p2Test(){
            BitByteTrack t = new BitByteTrack(2, 3, 5);
            // Logical Steps:
            // 2 * [3, 5]
            // [6, 10]
            // [7, 2]

            assertEquals(7, t.getByteIndex());
            assertEquals(2, t.getBitIndex());
        }

        @Test
        public void constructor2p3Test(){
            BitByteTrack t = new BitByteTrack(1, 2, -3);
            // Logical Steps:
            // 1 * [2, -3]
            // [2 - 1, -3 + 8]
            // [1, 5]

            assertEquals(1, t.getByteIndex());
            assertEquals(5, t.getBitIndex());
        }

        @Test
        public void constructor2p4Test(){
            BitByteTrack t = new BitByteTrack(-2, 2, 5);
            // Logical Steps:
            // -2 * [2, 5]
            // [-4, -10]
            // [-4 -2, -10 + 16]
            // [-6, 6]

            assertEquals(-6, t.getByteIndex());
            assertEquals(6, t.getBitIndex());
        }

        @Test
        public void constructor3p1Test(){
            BitByteTrack t = new BitByteTrack(6);

            assertEquals(0, t.getByteIndex());
            assertEquals(6, t.getBitIndex());
        }

        @Test
        public void constructor3p2Test(){
            BitByteTrack t = new BitByteTrack(19);
            // Logical Steps:
            // [0, 19]
            // [0, 16 + 3]
            // [0, 2*8 + 3]
            // [2, 3]

            assertEquals(2, t.getByteIndex());
            assertEquals(3, t.getBitIndex());
        }

        @Test
        public void constructor3p3Test(){
            BitByteTrack t = new BitByteTrack(-25);
            // Logical Steps:
            // [0, -25]
            // [0 - 4, -25 +32]
            // [-4, 7]

            assertEquals(-4, t.getByteIndex());
            assertEquals(7, t.getBitIndex());
        }

        @Test
        public void constructor4p1Test(){
            BitByteTrack tmp = new BitByteTrack(1, 1);
            BitByteTrack t = new BitByteTrack(tmp);

            assertEquals(1, t.getByteIndex());
            assertEquals(1, t.getBitIndex());
        }

        @Test
        public void constructor4p2Test(){
            BitByteTrack tmp = new BitByteTrack(1, 10);
            BitByteTrack t = new BitByteTrack(tmp);
            // Logical Steps:
            // [1, 10]
            // [1 + 1, 10 - 8]
            // [2, 2]

            assertEquals(2, t.getByteIndex());
            assertEquals(2, t.getBitIndex());
        }

        @Test
        public void constructor5p1Test(){
            BitByteTrack tmp = new BitByteTrack(1, 1);
            BitByteTrack t = new BitByteTrack(1, tmp);

            assertEquals(1, t.getByteIndex());
            assertEquals(1, t.getBitIndex());
        }

        @Test
        public void constructor5p2Test(){
            BitByteTrack tmp = new BitByteTrack(1, 1);
            BitByteTrack t = new BitByteTrack(2, tmp);

            assertEquals(2, t.getByteIndex());
            assertEquals(2, t.getBitIndex());
        }

        @Test
        public void constructor5p3Test(){
            BitByteTrack tmp = new BitByteTrack(1, 1);
            BitByteTrack t = new BitByteTrack(-3, tmp);
            // Logical Steps:
            // -3 * [1, 1]
            // [-3, -3]
            // [-3 - 1, -3 + 8]
            // [-4, 5]

            assertEquals(-4, t.getByteIndex());
            assertEquals(5, t.getBitIndex());
        }
    }

    @Nested
    @DisplayName("BitByteTrack Addition Tests")
    class additionTestClass{
        @Test
        public void addition1p1Test(){
            BitByteTrack t = new BitByteTrack(5,4);
            t.addMultiple(2,2);

            assertEquals(7, t.getByteIndex());
            assertEquals(6, t.getBitIndex());
        }

        @Test
        public void addition1p2Test(){
            BitByteTrack t = new BitByteTrack(2,4);
            t.addMultiple(1,7);
            // Logical Steps:
            // [2, 4] + [1, 7]
            // [3, 11]
            // [4, 3]

            assertEquals(4, t.getByteIndex());
            assertEquals(3, t.getBitIndex());
        }

        @Test
        public void addition2p1Test(){
            BitByteTrack t = new BitByteTrack(7,1);
            t.addMultiple(3, 2,3);
            // Logical Steps:
            // [7, 1] + 3 * [2, 3]
            // [7, 1] + [6, 9]
            // [13, 10]
            // [14, 2]

            assertEquals(14, t.getByteIndex());
            assertEquals(2, t.getBitIndex());
        }

        @Test
        public void addition2p2Test(){
            BitByteTrack t = new BitByteTrack(-3,2);
            t.addMultiple(-2, 5,3);
            // Logical Steps:
            // [-3, 2] + (-2) * [5, 3]
            // [-3, 2] + [-10, -6]
            // [-13, -4]
            // [-14, 4]

            assertEquals(-14, t.getByteIndex());
            assertEquals(4, t.getBitIndex());
        }

        @Test
        public void addition3p1Test(){
            BitByteTrack t = new BitByteTrack(-4,6);
            t.addMultiple(25);
            // Logical Steps:
            // [-4, 6] + [0, 25]
            // [-4, 31]
            // [-4 + 3, 31 - 24]
            // [-1, 7]

            assertEquals(-1, t.getByteIndex());
            assertEquals(7, t.getBitIndex());
        }

        @Test
        public void addition4p1Test(){
            BitByteTrack t = new BitByteTrack(2,5);
            BitByteTrack mult = new BitByteTrack(2, 2);
            t.addMultiple(mult);

            assertEquals(4, t.getByteIndex());
            assertEquals(7, t.getBitIndex());
        }

        @Test
        public void addition5p1Test(){
            BitByteTrack t = new BitByteTrack(2,5);
            BitByteTrack mult = new BitByteTrack(3, 3);
            t.addMultiple(5, mult);
            // Logical Steps:
            // [2, 5] + 5 * [3, 3]
            // [2, 5] + [15, 15]
            // [17, 20]
            // [17 + 2, 20 - 16]
            // [19, 4]

            assertEquals(19, t.getByteIndex());
            assertEquals(4, t.getBitIndex());
        }
    }

    @Nested
    class subtractionestClass{
        @Test
        public void subtraction1p1Test(){
            BitByteTrack t = new BitByteTrack(5,4);
            t.subtractMultiple(2,2);

            assertEquals(3, t.getByteIndex());
            assertEquals(2, t.getBitIndex());
        }

        @Test
        public void subtraction1p2Test(){
            BitByteTrack t = new BitByteTrack(2,4);
            t.subtractMultiple(1,7);
            // Logical Steps:
            // [2, 4] - [1, 7]
            // [1, -3]
            // [1 - 1, -3 + 8]
            // [0, 5]

            assertEquals(0, t.getByteIndex());
            assertEquals(5, t.getBitIndex());
        }

        @Test
        public void subtraction2p1Test(){
            BitByteTrack t = new BitByteTrack(7,1);
            t.subtractMultiple(3, 2,3);
            // Logical Steps:
            // [7, 1] - 3 * [2, 3]
            // [7, 1] + [-6, -9]
            // [1, -8]
            // [0, 0]

            assertEquals(0, t.getByteIndex());
            assertEquals(0, t.getBitIndex());
        }

        @Test
        public void subtraction2p2Test(){
            BitByteTrack t = new BitByteTrack(-3,2);
            t.subtractMultiple(-2, 5,3);
            // Logical Steps:
            // [-3, 2] - (-2) * [5, 3]
            // [-3, 2] + [10, 6]
            // [7, 8]
            // [8, 0]

            assertEquals(8, t.getByteIndex());
            assertEquals(0, t.getBitIndex());
        }

        @Test
        public void subtraction3p1Test(){
            BitByteTrack t = new BitByteTrack(-4,6);
            t.subtractMultiple(25);
            // Logical Steps:
            // [-4, 6] - [0, 25]
            // [-4, -19]
            // [-4 -3, -19 + 24]
            // [-7, 5]

            assertEquals(-7, t.getByteIndex());
            assertEquals(5, t.getBitIndex());
        }

        @Test
        public void subtraction4p1Test(){
            BitByteTrack t = new BitByteTrack(2,5);
            BitByteTrack mult = new BitByteTrack(2, 2);
            t.subtractMultiple(mult);

            assertEquals(0, t.getByteIndex());
            assertEquals(3, t.getBitIndex());
        }

        @Test
        public void subtraction5p1Test(){
            BitByteTrack t = new BitByteTrack(2,5);
            BitByteTrack mult = new BitByteTrack(3, 3);
            t.subtractMultiple(5, mult);
            // Logical Steps:
            // [2, 5] - 5 * [3, 3]
            // [2, 5] + [-15, -15]
            // [-13, -10]
            // [-13 - 2, -10 + 16]
            // [-15, 6]

            assertEquals(-15, t.getByteIndex());
            assertEquals(6, t.getBitIndex());
        }
    }
}