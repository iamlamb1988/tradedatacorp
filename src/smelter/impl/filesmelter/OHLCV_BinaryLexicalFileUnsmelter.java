/**
 * @author Bruce Lamb
 * @since 16 AUG 2025
 */
package tradedatacorp.smelter.filesmelter;
// package smelter.impl.filesmelter; //DEBUG TODO change back to package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.tools.binarytools.BinaryTools;
import tradedatacorp.item.stick.primitive.StickDouble;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * Reads binary files encoded with {@link OHLCV_BinaryLexical} and reconstructs a collection of {@link StickDouble} objects from the binary files.
 */
public class OHLCV_BinaryLexicalFileUnsmelter{
    int fileReadByteChunkSize;

    /**
     * Constructs an unsmelter with a specified byte chunk size for file reading.
     *
     * @param defaultFileReadByteChunkSize the byte chunk size to use for reading the file; if negative, defaults to 64 bytes.
     */
    public OHLCV_BinaryLexicalFileUnsmelter(int defaultFileReadByteChunkSize){
        if(defaultFileReadByteChunkSize < 0) fileReadByteChunkSize = 64;
        else fileReadByteChunkSize = defaultFileReadByteChunkSize;
    }

    /**
     * Constructs an unsmelter with a default file read chunk size of 64 bytes.
     */
    public OHLCV_BinaryLexicalFileUnsmelter(){
        this(64);
    }

    /**
     * Returns a {@link Collection} of {@link StickDouble} objects read in by an encoded file by an {@link OHLCV_BinaryLexical}.
     * @param originalBinaryFile The file pathname that will be decoded to construct return value. This file must be in the format provided by {@link OHLCV_BinaryLexical} instance.
     * @return a {@link Collection} of {@link StickDouble} objects read in by {@code BinaryFile}.
     */
    public Collection<StickDouble> unsmelt(String originalBinaryFile){
        //1. Construct and read header bytes into file
        FileReader dataReader = new FileReader(originalBinaryFile);
        HeaderReaderHelperBundle headerReader = new HeaderReaderHelperBundle(dataReader);
        headerReader.readHeader();
        OHLCV_BinaryLexical lexical = headerReader.lexical;

        byte[] byteArray = new byte[fileReadByteChunkSize];
        boolean[] dataBinArray = new boolean[lexical.getDataBitLength()];
        int byteCount;
        ArrayDeque<Boolean> bitQueue = new ArrayDeque<Boolean>(fileReadByteChunkSize << 3);
        ArrayList<StickDouble> stickList = new ArrayList<StickDouble>(lexical.getDataCount());

        //2. Fill in excess bits into the bitQueue (if any)
        if(headerReader.firstDataBitIndex != 0){
            int value = headerReader.lastByteValue & 0xFF;
            for(int i=headerReader.firstDataBitIndex; i<8; ++i){
                bitQueue.add(Boolean.valueOf(((value >>> (8-i-1)) & 1) == 1));
            }
        }

        //Main loop for gathering data
        byteCount = dataReader.readBytes(byteArray);
        do{
            //3. Set all bits from byteCHunk
            for(int i=0; i<byteCount; ++i){
                int tmp=byteArray[i] & 0xFF;
                for(int j=0; j<8; ++j){bitQueue.add(Boolean.valueOf(((tmp >>> (8-j-1)) & 1) == 1));}
            }

            //4. Read in datapoints to final collection
            while(bitQueue.size() >= lexical.getDataBitLength()){
                //4.1 Set data point bits
                for(int i=0; i<lexical.getDataBitLength(); ++i){
                    dataBinArray[i]=bitQueue.remove().booleanValue();
                }

                //4.2 Add data point to collection
                stickList.add(lexical.getRefinedDataFlat(dataBinArray));
            }

            byteCount = dataReader.readBytes(byteArray);
        }while(byteCount != -1 || bitQueue.size() >= lexical.getDataBitLength());

        //5. Close file and return
        dataReader.finalizeData();

        return stickList;
    }

    //TODO
    public Collection<StickDouble> unsmeltFromTo(Path originalBinaryFile, int fromIndex, int toIndex, boolean isFile){
        return unsmeltFromQuantity(originalBinaryFile, fromIndex, toIndex-fromIndex+1, isFile);
    }

    //TODO
    public Collection<StickDouble> unsmeltFromQuantity(String originalBinaryFilePathName, int fromIndex, int quantity, boolean isFile){
        return unsmeltFromQuantity(Paths.get(originalBinaryFilePathName), fromIndex, quantity, isFile);
    }

    //TODO
    public Collection<StickDouble> unsmeltFromQuantity(Path originalBinaryFile, int fromIndex, int quantity, boolean isFile){
        if(fromIndex < 0 || quantity < 0) return null;
        if(quantity == 0) return new ArrayList<StickDouble>(0);

        //1. Construct and read header bytes into file
        DataReader dataReader = new FileReader(originalBinaryFile);
        HeaderReaderHelperBundle headerReader = new HeaderReaderHelperBundle(dataReader);
        headerReader.readHeader();
        OHLCV_BinaryLexical lexical = headerReader.lexical;
        byte[] byteArray = new byte[fileReadByteChunkSize];
        boolean[] dataBinArray = new boolean[lexical.getDataBitLength()];

        int byteCount;
        int byteReadSize; //Will not exceed this.fileReadByteChunkSize.
        int tmpByteValue; //Leave as int to avoid implicity bit shift promotion to an unsigned value.
        int tmpIndex;
        ArrayDeque<Boolean> bitQueue = new ArrayDeque<Boolean>(fileReadByteChunkSize << 3);
        ArrayList<StickDouble> stickList = new ArrayList<StickDouble>(quantity);

        BitByteTrack multiplier = new BitByteTrack(lexical.getDataBitLength()); //used to jump to Byte and Bit of from index.
        //NOTE: For firstDataPoint byteIndex only, 0 index indicates current index, 1 indicates next index in file.
        //This is NOT the same throughout the rest of index use.
        BitByteTrack firstDataPoint = new BitByteTrack(
            1,
            headerReader.firstDataBitIndex == 0 ? 1 : 0,
            headerReader.firstDataBitIndex
        );
        BitByteTrack nextDataPoint = null;
        BitByteTrack lastDataPoint;

        //2. Update from Byte Index after header read. skip to point in file with relative index and bit value
        if(firstDataPoint.byteIndex == 0 && fromIndex == 0){ //First firstDatapoint IS the starting target data point.
            for(int i=firstDataPoint.bitIndex; i<8; ++i){
                bitQueue.add(Boolean.valueOf(((headerReader.lastByteValue >>> (7-i)) & 1) == 1 ? true : false));
            }
            nextDataPoint = new BitByteTrack();
        }else if(firstDataPoint.byteIndex == 1 && fromIndex == 0){ //First firstDatapoint IS the starting target data point and byte aligned.
            //Should now jump to readBytes until data is full
        }else{ //Will need to skip up to the first target data point.
            nextDataPoint = new BitByteTrack(firstDataPoint);
            nextDataPoint.addMultiple(fromIndex, multiplier);

            int bytesToSkip = (int)(nextDataPoint.byteIndex - 1);
            if(bytesToSkip == -1){ //Rare case, if targetByte is still in the first byte with header.
                //TODO need to stuff as many bits into bitqueue as possible (1 to 7 bits)
            }else if (bytesToSkip == 0){ //target byte is in next header.
                //Nothing to do here.
            }else{
                dataReader.skip(bytesToSkip);
            }
            nextDataPoint.byteIndex = 0;
        }

        //3. Read in first batch of bits and handle first
        lastDataPoint = new BitByteTrack(nextDataPoint);
        lastDataPoint.addMultiple(quantity, multiplier);
        byteCount = (int)(lastDataPoint.byteIndex + 1); //Maximum number of bytes to read ( may not need all of them)

        byteCount = byteCount < fileReadByteChunkSize ? byteCount : fileReadByteChunkSize;
        byteCount = dataReader.readBytes(byteArray, 0, byteCount);

        //3.1 read first batch of data bytes from file
        boolean[] DEBUG_queVal = new boolean[byteCount << 3];

        tmpByteValue = byteArray[0];
        for(int i=nextDataPoint.bitIndex; i<8; ++i){
            bitQueue.add(Boolean.valueOf(((tmpByteValue >>> (7-i)) & 1) == 1 ? true : false));
            DEBUG_queVal[i] = ((tmpByteValue >>> (7-i)) & 1) == 1 ? true : false;
        }

        for(int i=1; i<byteCount; ++i){
            tmpByteValue = byteArray[i];
            for(int j=0; j<8; ++j){
                bitQueue.add(Boolean.valueOf(((tmpByteValue >>> (7-j)) & 1) == 1 ? true : false));
                DEBUG_queVal[(i << 3) + j] = ((tmpByteValue >>> (7-j)) & 1) == 1 ? true : false;
            }
        }

        //4. Read full data bytes from file.
        tmpIndex=0;
        while(stickList.size() < quantity){
            while(tmpIndex < byteCount){
                tmpByteValue=byteArray[tmpIndex];
                for(int i=0; i<8; ++i){
                    bitQueue.add(Boolean.valueOf(((tmpByteValue >>> (7-i)) & 1) == 1 ? true : false));
                }
                ++tmpIndex;
            }

            while(stickList.size() < quantity && bitQueue.size() >= lexical.getDataBitLength()){
                for(int i=0; i<lexical.getDataBitLength(); ++i){
                    dataBinArray[i] = bitQueue.remove().booleanValue();
                }
                stickList.add(lexical.getRefinedDataFlat(dataBinArray));
            }
            tmpIndex = 0;
            byteCount = dataReader.readBytes(byteArray);
        }

        //8. Cleanup
        dataReader.finalizeData();

        return stickList;
    }

    private abstract class DataReader{
        protected abstract int readBytes(byte[] nextBytes);
        protected abstract int readBytes(byte[] nextBytes, int startIndex, int length);
        protected abstract long skip(long numberOfBytesToSkip);
        protected abstract void finalizeData();
    }

    private class FileReader extends DataReader{
        private FileInputStream reader;

        private FileReader(String filePathName){
            try{reader = new FileInputStream(filePathName);}
            catch(Exception err){err.printStackTrace();}
        }

        private FileReader(Path filePath){
            try{reader = new FileInputStream(filePath.toFile());}
            catch(Exception err){err.printStackTrace();}
        }

        @Override
        protected int readBytes(byte[] nextBytes){
            int readBytes=0;
            try{
                readBytes = reader.read(nextBytes);
            }catch(Exception err){err.printStackTrace();}
            return readBytes;
        }

        @Override
        protected int readBytes(byte[] nextBytes, int startIndex, int length){
            int readBytes=0;
            try{
                readBytes = reader.read(nextBytes,startIndex,length);
            }
            catch(Exception err){err.printStackTrace();}
            return readBytes;
        }

        @Override
        protected long skip(long numberOfBytesToSkip){
            try{reader.skip(numberOfBytesToSkip);}
            catch(Exception err){System.err.println();}
            return -1;
        }

        @Override
        protected void finalizeData(){
            try{reader.close();}
            catch(Exception err){err.printStackTrace();}
        }
    }

    //TODO
    private class StringReader extends DataReader{
        private StringBuilder strbldr; //TODO: No file writing here!!

        private StringReader(){strbldr = new StringBuilder();}

        @Override
        protected int readBytes(byte[] nextBytes){
            for(byte b : nextBytes){strbldr.append((char)b);}
            return nextBytes.length;
        }

        @Override
        protected int readBytes(byte[] nextBytes, int startIndex, int length){
            int end = startIndex + length;
            for(int i=startIndex; i<end; ++i){strbldr.append((char)nextBytes[i]);}
            return length;
        }

        @Override
        protected long skip(long numberOfBytesToSkip){
           return -1; //TODO
        }

        //TODO
        @Override
        protected void finalizeData(){}
    }

    /**
     * This is a helper private class that contains information for reading header and translating relevant parts for data reading.
     * Will not close the file upon completion.
     */
    private class HeaderReaderHelperBundle{
        int DEBUG_H1_ByteCountRead = 0;
        int DEBUG_H2_ByteCountRead = 0;
        int DEBUG_H_ByteCountRead = 0;
        OHLCV_BinaryLexical lexical;
        DataReader reader;
        byte lastByteValue; //value of byte containing last bit of H2
        byte firstDataBitIndex; // IF 0, datapoint is on the next Byte, not this "lastByteValue"

        private HeaderReaderHelperBundle(DataReader reader){
            this.reader = reader;
        }

        void readHeader(){
            //0. Initialize fields
            boolean[][] binH1 = new boolean[OHLCV_BinaryLexical.H1_COUNT][];
            boolean[][] binH2 = new boolean[OHLCV_BinaryLexical.H2_COUNT][];

            int byteCount; //For H1
            if(OHLCV_BinaryLexical.H1_TOTAL_LEN%8 == 0) byteCount = (OHLCV_BinaryLexical.H1_TOTAL_LEN >>> 3);
            else byteCount = (OHLCV_BinaryLexical.H1_TOTAL_LEN >>> 3) + 1;

            byte[] byteChunk = new byte[byteCount];
            boolean[] flatBinH1plus = new boolean[byteCount << 3];
            boolean[] flatBinH2plus;

            byteCount = reader.readBytes(byteChunk);
            DEBUG_H1_ByteCountRead+=byteCount;
            DEBUG_H_ByteCountRead+=byteCount;

            //1. Extract H1 bits into flat Header 1
            for(int i=0; i<byteCount; ++i){
                BinaryTools.setSubsetUnsignedInt(i << 3,8,byteChunk[i] & 0xFF,flatBinH1plus);
            }

            //2. Set each H1 binary array from flattened H1 array
            int flatIndex=0;
            for(int i=0; i<binH1.length; ++i){
                binH1[i] = new boolean[OHLCV_BinaryLexical.getHeader1BitLength(i)];
                for(int j=0; j<binH1[i].length; ++flatIndex, ++j){binH1[i][j] = flatBinH1plus[flatIndex];}
            }

            //3. Set the lengths of the H2 fields from the translated H1 values.
            binH2[0] = new boolean[BinaryTools.toUnsignedInt(binH1[OHLCV_BinaryLexical.H_INDEX_SYM_LEN])];
            binH2[1] = new boolean[BinaryTools.toUnsignedInt(binH1[OHLCV_BinaryLexical.H_INDEX_CT_LEN])];
            binH2[2] = new boolean[BinaryTools.toUnsignedInt(binH1[OHLCV_BinaryLexical.H_INDEX_H_GAP_LEN])];
            int h2_bit_length = binH2[0].length + binH2[1].length + binH2[2].length;

            //4. Read in next batch of next bytes to include to include all of H2
            byte excessBits = (byte)(flatBinH1plus.length - flatIndex);
            byte h2startbyteIndex;
            if(OHLCV_BinaryLexical.H1_TOTAL_LEN%8 == 0){ //If no overflow bits from H1 to H2
                h2startbyteIndex = (byte)0;
                flatIndex = 0;
                byteCount = (h2_bit_length >>> 3);
                byteChunk = new byte[byteCount];
                flatBinH2plus = new boolean[byteCount << 3];
                DEBUG_H2_ByteCountRead=reader.readBytes(byteChunk);
                DEBUG_H_ByteCountRead+=DEBUG_H2_ByteCountRead;
            }else{
                h2startbyteIndex = (byte)1;
                flatIndex = 8 - excessBits;
                byte firstH2Byte = byteChunk[byteChunk.length - 1];
                byteCount = (h2_bit_length >>> 3) + 1;
                byteChunk = new byte[byteCount];
                flatBinH2plus = new boolean[byteCount << 3];
                byteChunk[0] = firstH2Byte;
                DEBUG_H2_ByteCountRead=reader.readBytes(byteChunk,1,byteCount - 1);
                DEBUG_H_ByteCountRead+=DEBUG_H2_ByteCountRead;
            }

            //5. Process new byte Array elements into new respective bin array.
            for(int i=0; i<byteChunk.length; ++i){
                BinaryTools.setSubsetUnsignedInt(i << 3,8,byteChunk[i] & 0xFF,flatBinH2plus);
            }

            //6. Set H2 from flattened h2 bits
            for(int i=0; i<binH2.length; ++i){
                for(int j=0; j<binH2[i].length; ++j, ++flatIndex){binH2[i][j]=flatBinH2plus[flatIndex];}
            }

            //7. Generate lexical
            lexical = new OHLCV_BinaryLexical(
                binH1[0],
                binH1[1],
                binH1[2],
                binH1[3],
                binH1[4],
                binH1[5],
                binH1[6],
                binH1[7],
                binH1[8],
                binH1[9],
                binH1[10],
                binH2[0],
                binH2[1],
                binH2[2]
            );

            lastByteValue = byteChunk[byteChunk.length - 1];
            firstDataBitIndex = (byte)(lexical.getHeaderBitLength()%8);
        }
    }

    //Could be a public generic use binary tool in tools package....
    private class BitByteTrack{
        long byteIndex;
        byte bitIndex;

        private void constructorHelper(){
            byteIndex = 0;
            bitIndex = 0;
        }

        private BitByteTrack(){constructorHelper();}

        private BitByteTrack(long n, long bytes, long bits){
            constructorHelper();
            addMultiple(n, bytes, bits);
        }

        private BitByteTrack(long n, long bits){
            constructorHelper();
            addMultiple(n,bits);
        }

        private BitByteTrack(long bits){
            constructorHelper();
            addMultiple(0, bits);
        }

        private BitByteTrack(BitByteTrack initialValue){
            byteIndex = initialValue.byteIndex;
            bitIndex = initialValue.bitIndex;
        }

        private void addMultiple(long n, long bytes, long bits){
            long incomingBytes = n * bytes;
            long incomingBits = bits + bitIndex;
            long extraBytes = incomingBits >>> 3;
            long remainingBits = incomingBits - (extraBytes << 3);
            byteIndex += incomingBytes + extraBytes;
            bitIndex = (byte)remainingBits;
        }

        private void addMultiple(long n, long bits){addMultiple(n,0, bits);}

        private void addMultiple(long bits){addMultiple(1, 0, bits);}

        private void addMultiple(long n, BitByteTrack multiplier){
            addMultiple(n, multiplier.byteIndex, multiplier.bitIndex);
        }
    }
}