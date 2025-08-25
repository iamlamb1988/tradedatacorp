/**
 * @author Bruce Lamb
 * @since 25 AUG 2025
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

    public BitByteTrack(long bytes, long bits){
        constructorHelper();
        addMultiple(bytes, bits);
    }

    public BitByteTrack(long n, long bytes, long bits){
        constructorHelper();
        addMultiple(n, bytes, bits);
    }

    public BitByteTrack(long bits){
        constructorHelper();
        addMultiple(bits);
    }

    public BitByteTrack(BitByteTrack initialValue){
        byteIndex = initialValue.byteIndex;
        bitIndex = initialValue.bitIndex;
    }

    public BitByteTrack(long n, BitByteTrack multiple){
        byteIndex = multiple.byteIndex;
        bitIndex = multiple.bitIndex;
        addMultiple(n - 1, multiple.byteIndex, multiple.bitIndex);
    }

    public long getByteIndex(){return byteIndex;}
    public byte getBitIndex(){return bitIndex;}

    public void addMultiple(long bytes, long bits){
        addMultiple(1, bytes, bits);
    }

    public void addMultiple(long n, long bytes, long bits){
        long incomingBytes = n * bytes;
        long incomingBits = n * bits + bitIndex;
        if(incomingBits < 0){
            long borrowBytes = Math.absExact(incomingBits/8);
            if(incomingBits%8 != 0){
                ++borrowBytes;
                incomingBytes -= borrowBytes;
                incomingBits += (borrowBytes << 3);
            }else{
                incomingBytes -= borrowBytes;
                incomingBits = 0;
            }
        }
        long extraBytes = incomingBits >>> 3;
        
        bitIndex = (byte)(incomingBits - (extraBytes << 3));
        byteIndex += incomingBytes + extraBytes;
    }

    public void addMultiple(long bits){addMultiple(1, 0, bits);}

    public void addMultiple(BitByteTrack multiplier){
        addMultiple(1, multiplier.byteIndex, multiplier.bitIndex);
    }

    public void addMultiple(long n, BitByteTrack multiplier){
        addMultiple(n, multiplier.byteIndex, multiplier.bitIndex);
    }

    public void subtractMultiple(long bytes, long bits){
        addMultiple(-bytes, -bits);
    }

    public void subtractMultiple(long n, long bytes, long bits){
        addMultiple(-n, bytes, bits);
    }

    public void subtractMultiple(long bits){
        addMultiple(-bits);
    }

    public void subtractMultiple(BitByteTrack multiplier){
        addMultiple(-1, multiplier.byteIndex, multiplier.bitIndex);
    }

    public void subtractMultiple(long n, BitByteTrack multiplier){
        addMultiple(-n, multiplier.byteIndex, multiplier.bitIndex);
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

    public void round(){
        if(bitIndex < 4) roundDown();
        else roundUp();
    }
}