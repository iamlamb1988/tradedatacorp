/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.tools.binarytools.BinaryTools;
import tradedatacorp.item.stick.primitive.StickDouble;

import java.io.FileInputStream;
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
        //1. Open binary file.
        //1.0 Open binary file in OHLCV_BinaryLexical format.
        FileInputStream binFile;
        try{
            binFile = new FileInputStream(originalBinaryFile);
        }
        catch(Exception err){
            err.printStackTrace();
            return null;
        }

        //1.1 Read H1 bytes (all static lengths) plus extra.
        int byteCount;
        if(OHLCV_BinaryLexical.H1_TOTAL_LEN%8 == 0) byteCount = (OHLCV_BinaryLexical.H1_TOTAL_LEN >>> 3);
        else byteCount = (OHLCV_BinaryLexical.H1_TOTAL_LEN >>> 3) + 1;

        byte[] byteArray = new byte[byteCount];
        boolean[] binArray = new boolean[byteCount << 3];
        boolean[][] binH1 = new boolean[OHLCV_BinaryLexical.H1_COUNT][];
        boolean[][] binH2 = new boolean[OHLCV_BinaryLexical.H2_COUNT][];

        try{byteCount=binFile.read(byteArray);}catch(Exception err){err.printStackTrace();}

        //1.2 Read flattened H1 array from byte array (plus extra)
        for(int i=0; i<byteCount; ++i){
            BinaryTools.setSubsetUnsignedInt(i << 3,8,byteArray[i] & 0xFF,binArray);
        }

        //1.3 Create and set each H1 binary array from flattened H1 array
        int flatIndex=0;
        for(int i=0; i<binH1.length; ++i){
            binH1[i] = new boolean[OHLCV_BinaryLexical.getHeader1BitLength(i)];
            for(int k=0; k<binH1[i].length; ++flatIndex, ++k){binH1[i][k] = binArray[flatIndex];}
        }

        //1.4 Set the lengths of the H2 fields from the translated H1 values.
        binH2[0] = new boolean[BinaryTools.toUnsignedInt(binH1[OHLCV_BinaryLexical.H_INDEX_SYM_LEN])];
        binH2[1] = new boolean[BinaryTools.toUnsignedInt(binH1[OHLCV_BinaryLexical.H_INDEX_CT_LEN])];
        binH2[2] = new boolean[BinaryTools.toUnsignedInt(binH1[OHLCV_BinaryLexical.H_INDEX_H_GAP_LEN])];
        int h2_len = binH2[0].length + binH2[1].length + binH2[2].length;

        //1.5 Read in next batch of next bytes to include to include all of H2
        byte excessBits = (byte)(binArray.length - flatIndex);
        byte h2startbyteIndex;
        if(OHLCV_BinaryLexical.H1_TOTAL_LEN%8 == 0){
            h2startbyteIndex = (byte)0;
            flatIndex = 0;
            byteCount = (h2_len >>> 3);
            byteArray = new byte[byteCount];
            binArray = new boolean[byteCount << 3];
            try{byteCount=binFile.read(byteArray);}catch(Exception err){err.printStackTrace();}
        }else{
            h2startbyteIndex = (byte)1;
            flatIndex = 8 - excessBits;
            byte firstH2Byte = byteArray[byteArray.length - 1];
            byteCount = (h2_len >>> 3) + 1;
            byteArray = new byte[byteCount];
            binArray = new boolean[byteCount << 3];
            byteArray[0] = firstH2Byte;
            try{byteCount=binFile.read(byteArray,1,byteCount - 1);}catch(Exception err){err.printStackTrace();}
        }

        //1.6 Process new byte Array elements into new respective bin array.
        for(int i=0; i<byteArray.length; ++i){
            BinaryTools.setSubsetUnsignedInt(i << 3,8,byteArray[i] & 0xFF,binArray);
        }

        //1.7 Set H2 from flattened h2 bits
        for(int i=0; i<binH2.length; ++i){
            for(int j=0; j<binH2[i].length; ++j, ++flatIndex){
                binH2[i][j]=binArray[flatIndex];
            }
        }

        //2. Generate working objects to parse all data points.
        OHLCV_BinaryLexical lexical = new OHLCV_BinaryLexical(
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

        ArrayDeque<Boolean> bitQueue = new ArrayDeque<Boolean>(fileReadByteChunkSize << 3);
        ArrayList<StickDouble> stickList = new ArrayList<StickDouble>(lexical.getDataCount());

        //3. Fill in excess bits into the bitQueue
        for(int i=flatIndex; i<binArray.length; ++i){
            bitQueue.add(Boolean.valueOf(binArray[i]));
        }

        byteArray = new byte[fileReadByteChunkSize];
        binArray = new boolean[lexical.getDataBitLength()];

        //Main loop for gathering data
        try{byteCount=binFile.read(byteArray);}catch(Exception err){}

        do{
            //4. Set all bits from byteCHunk
            for(int i=0; i<byteCount; ++i){
                int tmp=byteArray[i] & 0xFF;
                for(int j=0; j<8; ++j){bitQueue.add(Boolean.valueOf((((tmp >>> (8-j-1)) & 1) == 1)));}
            }

            //5. Read in datapoints to final collection
            while(bitQueue.size() >= lexical.getDataBitLength()){
                //5.1 Set data point bits
                for(int i=0; i<lexical.getDataBitLength(); ++i){binArray[i]=bitQueue.remove();}

                //5.2 Add data point to collection
                stickList.add(lexical.getRefinedDataFlat(binArray));
            }

            try{byteCount=binFile.read(byteArray);}catch(Exception err){}
        }while(byteCount == fileReadByteChunkSize);

        //6. Close file and return
        try{binFile.close();}catch(Exception err){err.printStackTrace();}

        return stickList;
    }

    //TODO
    public Collection<StickDouble> unsmeltFromTo(String originalBinaryFile, int fromIndex, int toIndex){
        return null;
    }

    //TODO
    public Collection<StickDouble> unsmeltFromQuantity(String originalBinaryFile, int fromIndex, int quantity){
        return null;
    }

    private abstract class DataReader{
        protected abstract int readBytes(byte[] nextBytes);
        protected abstract int readBytes(byte[] nextBytes, int startIndex, int length);
        protected abstract String finalizeData();
    }

    private class FileReader extends DataReader{
        private FileInputStream reader;

        private FileReader(Path filePath){
            try{reader = new FileInputStream(filePath.toFile());}
            catch(Exception err){err.printStackTrace();}
        }

        @Override
        protected int readBytes(byte[] nextBytes){
            try{return reader.read(nextBytes);}
            catch(Exception err){err.printStackTrace();}
        }

        @Override
        protected int readBytes(byte[] nextBytes, int startIndex, int length){
            try{return reader.read(nextBytes,startIndex,length);}
            catch(Exception err){err.printStackTrace();}
        }

        @Override
        protected String finalizeData(){
            try{reader.close();}
            catch(Exception err){err.printStackTrace();}
            return null;
        }
    }

    private class StringReader extends DataReader{

    }

    /**
     * This is a helper private class that contains information for reading header and translating relevant parts for data reading.
     * 
     */
    private class HeaderReaderHelperBundle{
        OHLCV_BinaryLexical lexical;
        boolean[][] binH1;
        boolean[][] binH2;
        int totalBytesRead;

        private HeaderReaderHelperBundle(DataReader reader){
            binH1 = new boolean[OHLCV_BinaryLexical.H1_COUNT][];
            binH2 = new boolean[OHLCV_BinaryLexical.H2_COUNT][];

            int byteCount; //For H1
            if(OHLCV_BinaryLexical.H1_TOTAL_LEN%8 == 0) byteCount = (OHLCV_BinaryLexical.H1_TOTAL_LEN >>> 3);
            else byteCount = (OHLCV_BinaryLexical.H1_TOTAL_LEN >>> 3) + 1;
        }
    }

    private class BitByteTrack{
        long byteIndex;
        byte bitIndex;

        private void addMultiple(long n, long bytes, byte bits){}
    }
}