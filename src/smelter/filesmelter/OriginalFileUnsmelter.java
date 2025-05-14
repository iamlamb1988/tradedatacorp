/**
 * @author Bruce Lamb
 * @since 10 MAY 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.smelter.lexical.binary.BinaryTools;

import tradedatacorp.item.stick.primitive.StickDouble;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * The purpose of this class is to return a {@link Collection} of {@link StickDouble} objects from a file encoded by an {@code Original} binary Lexical.
 * This is intended for small files and may have perfomance issues with large files.
 * This is the prototype class that will be the basis for handling large files.
 */
public class OriginalFileUnsmelter{
    private int fileReadByteChunkSize = 64;

    /**
     * Returns a {@link Collection} of {@link StickDouble} objects read in by an encoded file by an {@link Original}.
     * @param originalBinaryFile The file pathname that will be decoded to construct return value. This file must be in the format provided by {@link Original} instance.
     * @return a {@link Collection} of {@link StickDouble} objects read in by {@code originalBinaryFile}.
     */
    public Collection<StickDouble> unsmelt(String originalBinaryFile){
        //1. Open binary file in Original binary format.
        //1.0 Open binary file in Original binary format.
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
        if(Original.H1_TOTAL_LEN%8 == 0) byteCount = (Original.H1_TOTAL_LEN >>> 3);
        else byteCount = (Original.H1_TOTAL_LEN >>> 3) + 1;

        byte[] byteArray = new byte[byteCount];
        boolean[] binArray = new boolean[byteCount << 3];
        boolean[][] binH1 = new boolean[Original.H1_COUNT][];
        boolean[][] binH2 = new boolean[Original.H2_COUNT][];

        try {byteCount=binFile.read(byteArray);}catch(Exception err){err.printStackTrace();}

        //1.2 Read flattened H1 array from byte array (plus extra)
        for(int i=0; i<byteCount; ++i){
            BinaryTools.setSubsetUnsignedInt(i << 3,8,byteArray[i],binArray);
        }

        //1.3 Create and set each H1 binary array from flattened H1 array
        int flatIndex=0;

        for(int i=0; i<binH1.length; ++i){
            binH1[i] = new boolean[Original.getHeader1BitLength(i)];
            for(int k=0; k<binH1[i].length; ++flatIndex, ++k){
                binH1[i][k] = binArray[flatIndex];
            }
        }

        //1.4 Set the lengths of the H2 fields from the translated H1 values.
        binH2[0] = new boolean[BinaryTools.toUnsignedInt(binH1[Original.H_INDEX_SYM_LEN])];
        binH2[1] = new boolean[BinaryTools.toUnsignedInt(binH1[Original.H_INDEX_CT_LEN])];
        binH2[2] = new boolean[BinaryTools.toUnsignedInt(binH1[Original.H_INDEX_H_GAP_LEN])];
        int h2_len = binH2[0].length + binH2[1].length + binH2[2].length;

        //1.5 Set excess bits in (if any) to be read in
        boolean[] excessBinArr = new boolean[binArray.length-flatIndex];

        while(flatIndex < binArray.length){ //flatIndex starts at Original.H1_COUNT, sets old excess to this point
            excessBinArr[flatIndex-Original.H1_COUNT] = binArray[flatIndex];
            ++flatIndex;
        }

        //1.6 Read in next batch of next bytes to include to include all of H2
        byteCount = h2_len - excessBinArr.length; //temporary bit count (not byte count yet)
        if(byteCount%8 == 0) byteCount = (byteCount >>> 3);
        else byteCount = (byteCount >>> 3) + 1;

        byteArray = new byte[byteCount];
        binArray = new boolean[byteCount << 3];

        try {byteCount=binFile.read(byteArray);}catch(Exception err){err.printStackTrace();}

        //1.7 Set flattened h2 bin values
        //1.7.1 Set first Excess bits
        for(int i=0; i<excessBinArr.length; ++i){binArray[i]=excessBinArr[i];}

        //1.7.2 Set remaining flattened h2 bits
        for(int i=0; i<byteCount; ++i){
            BinaryTools.setSubsetUnsignedInt((i << 3) + excessBinArr.length,8,byteArray[i],binArray);
        }

        //1.8 Set H2 from flattened h2 bits
        flatIndex=0;
        for(int i=0; i<binH2.length; ++i){
            for(int j=0;j<binH2[i].length; ++j, ++flatIndex){
                binH2[i][j]=binArray[flatIndex];
            }
        }

        //2. Generate working objects to parse all data points.
        Original lexical = new Original(
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
        for(int i=flatIndex; i<binArray.length; ++i){bitQueue.add(Boolean.valueOf(binArray[i]));}

        byteArray = new byte[fileReadByteChunkSize];
        binArray = new boolean[lexical.getDataBitLength()];

        //Main loop for gathering data
        try{byteCount=binFile.read(byteArray);}catch(Exception err){}

        do{
            //4. Set all bits from byteCHunk
            for(int i=0; i<byteCount; ++i){
                int tmp=byteArray[i] & 0xFF;
                for(int j=7; j>=0; --j){bitQueue.add(((tmp >> j) & 1) == 1);}
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
}