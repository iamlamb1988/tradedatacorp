/**
 * @author Bruce Lamb
 * @since 25 AUG 2025
 */
package tradedatacorp.tools.binarytools;

/**
 * Represents a position in a byte stream using a combination of
 * `byteIndex` and `bitIndex`.
 *
 * - `byteIndex` can be negative or positive (unbounded).
 * - `bitIndex` is always normalized to the range [0–7].
 *
 * Documentation will represent the state of BitByteTrack instances as [<byteIndex>, <bitIndex>]
 *
 * Conceptually, a BitByteTrack is like a mixed number:
 * [byteIndex, bitIndex]  ≈  byteIndex + (bitIndex / 8).
 *
 * This makes it useful for working with binary formats where fields
 * don't always align to byte boundaries.
 *
 * Typical use cases:
 * - Jumping to a bit offset inside a byte buffer.
 * - Incrementally advancing through variable-length binary data.
 * - Subtracting or adding positions expressed in bits or mixed byte/bit units.
 *
 * Example:
 *   [2, 10] → normalizes to [3, 2]
 *   [5, -3] → normalizes to [4, 5]
 *   [0, 19] → normalizes to [2, 3]
 *   [-1, 9] → normalizes to [0, 1]
 *
 * All arithmetic operations ensure normalization so that `bitIndex` always remains in [0–7].
 */
public class BitByteTrack{
    private long byteIndex;
    private byte bitIndex;

    /**
     * This is a helper function to reduce repeated functions in constructor calls.
     */
    private void constructorHelper(){
        byteIndex = 0;
        bitIndex = 0;
    }

    /**
     * Creates an instance of [0, 0]
     */
    public BitByteTrack(){constructorHelper();}

    /**
     * Creates a tracker initialized with a given number of bytes and bits.
     * Automatically normalizes so bitIndex is in [0–7].
     *
     * @param bytes The starting byte offset.
     * @param bits The starting bit offset (normalized into range [0–7]).
     *
     * Example: new BitByteTrack(9, 5)  -> [9,5]
     * Example: new BitByteTrack(2, -1) -> [1,7]
     * Example: new BitByteTrack(1, 10) -> [2,2]
     */
    public BitByteTrack(long bytes, long bits){
        constructorHelper();
        addMultiple(1, bytes, bits);
    }

    /**
     * Constructs a tracker by multiplying the given offset [bytes, bits]
     * by a factor n. Normalizes n * [bytes, bits] -> [byteIndex, bitIndex].
     *
     * @param n The multiplier.
     * @param bytes The base byte offset to multiply.
     * @param bits The base bit offset to multiply (normalized into range [0–7]).
     *
     * Example: new BitByteTrack(2, 3, 5) -> 2*[3, 5] -> [6, 10] -> [7,2]
     */
    public BitByteTrack(long n, long bytes, long bits){
        constructorHelper();
        addMultiple(n, bytes, bits);
    }

    /**
     * Constructs a tracker by multiplying the given offset [0, bits]
     * by a factor n. Normalizes n * [0, bits] -> [byteIndex, bitIndex].
     *
     * @param n The multiplier.
     * @param bits The base bit offset to multiply (normalized into range [0–7]).
     *
     * Example: new BitByteTrack(2, 15) -> 2*[0, 15] -> [0, 30] -> [3, 6]
     */
    public BitByteTrack(long bits){
        constructorHelper();
        addMultiple(1, 0, bits);
    }

    /**
     * Copy constructor. Creates a new tracker initialized to the same state
     * as another BitByteTrack instance.
     *
     * @param initialValue The existing tracker to copy.
     *
     * Example:
     *   BitByteTrack t1 = new BitByteTrack(2, 5); -> [2,5]
     *   BitByteTrack t2 = new BitByteTrack(t1);  -> [2,5]
     */
    public BitByteTrack(BitByteTrack initialValue){
        byteIndex = initialValue.byteIndex;
        bitIndex = initialValue.bitIndex;
    }

    /**
     * Copy constructor. Creates a new tracker initialized to the same state
     * as another BitByteTrack instance.
     *
     * @param n The multiplier.
     * @param multiple The tracker to multiply by n.
     *
     * Example:
     *   BitByteTrack t1 = new BitByteTrack(2, 3);  -> [2, 3]
     *   BitByteTrack t2 = new BitByteTrack(2, t1); -> 2*[2, 3] -> [4, 6]
     */
    public BitByteTrack(long n, BitByteTrack multiple){
        byteIndex = multiple.byteIndex;
        bitIndex = multiple.bitIndex;
        addMultiple(n - 1, multiple.byteIndex, multiple.bitIndex);
    }

    /**
     * Returns the byte index value.
     * @return the byte index value.
     */
    public long getByteIndex(){return byteIndex;}

    /**
     * Returns the bit index value.
     * @return the bit index value.
     */
    public byte getBitIndex(){return bitIndex;}

    /**
     * Adds offset [bytes, bits] to itself, then normalizes.
     *
     * @param bytes The number of bytes to add.
     * @param bits The number of bits to add (normalized into range [0–7]).
     *
     * Example:
     *   Initial state: [2, 4]
     *   [2, 4] + [1, 7] -> [3, 11] -> [4, 3]
     */
    public void addMultiple(long bytes, long bits){
        addMultiple(1, bytes, bits);
    }

    /**
     * Adds n copies offset [bytes, bits] to this tracker, then normalizes.
     *
     * @param n How many times to add the offset.
     * @param bytes The base byte offset.
     * @param bits The base bit offset.
     *
     * Example:
     *   Initial state: [7, 1]
     *   [7, 1] + 3*[2, 3] -> [7, 1] + [6, 9] -> [13, 10] -> [14, 2]
     */
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

    /**
     * Adds a bit-only offset [0, bits].
     *
     * @param bits The number of bits to add (normalized into range [0–7]).
     *
     * Example:
     *   Initial state: [1, 6]
     *   [1, 6] + [0, 19] -> [1, 25] → [4, 1]
     */
    public void addMultiple(long bits){addMultiple(1, 0, bits);}

    /**
     * Adds offset of a BitByteTrack’s [byteIndex, bitIndex] to this tracker, then normalizes.
     *
     * @param multiplier The tracker whose values will be added.
     *
     * Example:
     *   Initial state: [2, 5]
     *   [2, 5] + [2, 2] -> [4, 7]
     */
    public void addMultiple(BitByteTrack multiplier){
        addMultiple(1, multiplier.byteIndex, multiplier.bitIndex);
    }

    /**
     * Adds n copies offset a BitByteTracker to this tracker, then normalizes.
     *
     * @param n The multiplier.
     * @param multiplier The tracker to use as the repeated offset.
     *
     * Example:
     *   Initial state: [2, 5]
     *   [2, 5] + 5 * [3, 3] -> [2, 5] + [15, 15] -> [17, 20] -> [19, 4]
     */
    public void addMultiple(long n, BitByteTrack multiplier){
        addMultiple(n, multiplier.byteIndex, multiplier.bitIndex);
    }

    /**
     * Subtracts offset [bytes, bits] from this tracker, then normalizes.
     *
     * @param bytes The number of bytes to subtract.
     * @param bits The number of bits to subtract.
     *
     * Example:
     *   Initial state: [2, 4]
     *   [2, 4] - [1, 7] -> [1, -3] -> [0, 5]
     */
    public void subtractMultiple(long bytes, long bits){
        addMultiple(-1, bytes, bits);
    }

    /**
     * Subtracts n copies of offset [bytes, bits] from this tracker, then normalizes.
     *
     * @param n The multiplier.
     * @param bytes The base byte offset to subtract.
     * @param bits The base bit offset to subtract.
     *
     * Example:
     *   Initial state: [7, 1]
     *   [7, 1] - 3*[2, 3] -> [7, 1] + [-6, -9] -> [1, -8] -> [0, 0]
     */
    public void subtractMultiple(long n, long bytes, long bits){
        addMultiple(-n, bytes, bits);
    }

    /**
     * Subtracts a bit-only offset [0, bits] from this tracker, then normalizes.
     *
     * @param bits The number of bits to subtract.
     *
     * Example:
     *   Initial state: [-4, 6]
     *   [-4, 6] - [0, 25] -> [-4, -19] -> [-7, 5]
     */
    public void subtractMultiple(long bits){
        addMultiple(-1, 0, bits);
    }

    /**
     * Subtracts another BitByteTrack's [byteIndex, bitIndex] offset from this tracker.
     *
     * @param multiplier The tracker whose values will be subtracted.
     *
     * Example:
     *   Initial state: [2, 5]
     *   [2, 5] - [2, 2] -> [0, 3]
     */
    public void subtractMultiple(BitByteTrack multiplier){
        addMultiple(-1, multiplier.byteIndex, multiplier.bitIndex);
    }

    /**
     * Subtracts n copies of another BitByteTrack's [byteIndex, bitIndex]
     * from this tracker, then normalizes.
     *
     * @param n The multiplier.
     * @param multiplier The tracker to subtract multiple times.
     *
     * Example:
     *   Initial state: [2, 5]
     *   [2, 5] - 5*[3, 3] -> [2, 5] + [-15, -15] -> [-13, -10] -> [-15, 6]
     */
    public void subtractMultiple(long n, BitByteTrack multiplier){
        addMultiple(-n, multiplier.byteIndex, multiplier.bitIndex);
    }

    /**
     * Rounds this state up the next full byte if bitIndex is not zero. bitIndex will be zero.
     *
     * Example:
     *   [1, 1] -> [2, 0]
     *   [2, 5] -> [3, 0]
     *   [4, 0] -> [4, 0] (no change)
     */
    public void roundUp(){
        if(bitIndex != 0){
            ++byteIndex;
            bitIndex = 0;
        }
    }

    /**
     * Rounds this state down to the nearest full byte by clearing the bitIndex.
     * byteIndex remains unchanged, bitIndex will be zero.
     *
     * Example:
     *   [1, 1] -> [1, 0]
     *   [2, 5] -> [2, 0]
     *   [4, 0] -> [4, 0] (no change)
     */
    public void roundDown(){bitIndex = 0;}

    /**
     * Rounds this state to the nearest full byte:
     * - If bitIndex < 4, rounds down.
     * - If bitIndex >= 4, rounds up.
     *
     * Example:
     *   [1, 3] -> [1, 0] (round down)
     *   [2, 4] -> [3, 0] (round up)
     *   [5, 7] -> [6, 0] (round up)
     *   [6, 0] -> [6, 0] (no change)
     */
    public void round(){
        if(bitIndex < 4) roundDown();
        else roundUp();
    }

    /**
     * Clears the byteIndex by setting it to zero, while leaving the bitIndex unchanged.
     *
     * Example:
     *   [5, 3] -> [0, 3]
     *   [-2, 7] -> [0, 7]
     *   [0, 1] -> [0, 1] (no change to bytes)
     */
    public void clearBytes(){byteIndex = 0;}
}