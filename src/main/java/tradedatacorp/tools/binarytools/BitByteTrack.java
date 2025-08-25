/**
 * @author Bruce Lamb
 * @since 24 AUG 2025
 */
package tradedatacorp.tools.binarytools;

public class BitByteTrack{
    private long byteIndex;
    private byte bitIndex;

    private void constructorHelper(){
        byteIndex = 0;
        bitIndex = 0;
    }

    public BitByteTrack(){constructorHelper();}

    public BitByteTrack(long n, long bytes, long bits){
        constructorHelper();
        addMultiple(n, bytes, bits);
    }

    public BitByteTrack(long n, long bits){
        constructorHelper();
        addMultiple(n,bits);
    }

    public BitByteTrack(long bits){
        constructorHelper();
        addMultiple(bits);
    }

    public BitByteTrack(BitByteTrack initialValue){
        byteIndex = initialValue.byteIndex;
        bitIndex = initialValue.bitIndex;
    }

    public long getByteIndex(){return byteIndex;}
    public byte getBitIndex(){return bitIndex;}

    public void addMultiple(long n, long bytes, long bits){
        long incomingBytes = n * bytes;
        long incomingBits = n * bits + bitIndex;
        long extraBytes = incomingBits >>> 3;
        bitIndex = (byte)(incomingBits - (extraBytes << 3));
        byteIndex += incomingBytes + extraBytes;
    }

    public void addMultiple(long n, long bits){addMultiple(n,0, bits);}

    public void addMultiple(long bits){addMultiple(1, 0, bits);}

    public void addMultiple(long n, BitByteTrack multiplier){
        addMultiple(n, multiplier.byteIndex, multiplier.bitIndex);
    }

    public void subtractMultiple(long bits){
        long deductedBytes = (bits >>> 3);
        long remaingBitsToDeduct = bits - (deductedBytes << 3);
        byteIndex -= deductedBytes;
        bitIndex -= (byte)remaingBitsToDeduct;
        if(bitIndex < 0){
            byteIndex -= 1;
            bitIndex += 8;
        }
    }

    public void roundUp(){
        if(bitIndex != 0){
            ++byteIndex;
            bitIndex = 0;
        }
    }

    public void roundDown(){
        bitIndex = 0;
    }
}