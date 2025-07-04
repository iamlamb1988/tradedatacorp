/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */
package tradedatacorp.smelter.filesmelter;

public class OHLCV_ExpectedResourceValues{
    private OHLCV_ExpectedResourceValues(){throw new AssertionError("This class should not be instantiated.");}

    public static byte[] expectedOneDatapoint(){
        return new byte[]{
            (byte)0x00, //Index 0
            (byte)0x00, //Index 1
            (byte)0x00, //Index 2
            (byte)0x07, //Index 3
            (byte)0x83, //Index 4
            (byte)0x16, //Index 5
            (byte)0x01, //Index 6
            (byte)0x04, //Index 7
            (byte)0x10, //Index 8
            (byte)0x41, //Index 9
            (byte)0x10, //Index 10
            (byte)0x2A, //Index 11
            (byte)0x22, //Index 12
            (byte)0xA9, //Index 13
            (byte)0xAA, //Index 14
            (byte)0x1C, //Index 15
            (byte)0x40, //Index 16
            (byte)0x90, //Index 17
            (byte)0x20, //Index 18
            (byte)0x50, //Index 19
            (byte)0xA5  //Index 20
        };
    }

    public static byte[] expectedTwoDatapoints(){
        return new byte[]{
            (byte)0x00, //Index 0
            (byte)0x00, //Index 1
            (byte)0x00, //Index 2
            (byte)0x07, //Index 3
            (byte)0x83, //Index 4
            (byte)0x16, //Index 5
            (byte)0x01, //Index 6
            (byte)0x04, //Index 7
            (byte)0x10, //Index 8
            (byte)0x41, //Index 9
            (byte)0x10, //Index 10
            (byte)0x2A, //Index 11
            (byte)0x22, //Index 12
            (byte)0xA9, //Index 13
            (byte)0xAA, //Index 14
            (byte)0x2C, //Index 15
            (byte)0x40, //Index 16
            (byte)0x90, //Index 17
            (byte)0x20, //Index 18
            (byte)0x50, //Index 19
            (byte)0xA5, //Index 20
            (byte)0xD4, //Index 21
            (byte)0x19, //Index 22
            (byte)0x72, //Index 23
            (byte)0x25, //Index 24
            (byte)0x0F, //Index 25
            (byte)0x60  //Index 26
        };
    }
}