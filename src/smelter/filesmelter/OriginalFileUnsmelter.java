/**
 * @author Bruce Lamb
 * @since 1 MAY 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.smelter.lexical.binary.BinaryTools;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;

public class OriginalFileUnsmelter{
    private int fileReadByteChunkSize = 64;

    public void unsmelt(String originalBinaryFile){
        //1. Open binary file in Original binary format.
        //1.0 Open binary file in Original binary format.
        FileInputStream binFile;
        try{
            binFile = new FileInputStream(originalBinaryFile);
        }
        catch(Exception err){
            err.printStackTrace();
            return;
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

        //DEBUG SECTION
        System.out.println("DEBUG: START Step 1.3");
        for(int i=0; i<binH1.length; ++i){
            binH1[i] = new boolean[Original.getHeader1BitLength(i)];
            for(int k=0; k<binH1[i].length; ++flatIndex, ++k){
                binH1[i][k] = binArray[flatIndex];
            }
            System.out.println("DEBUG: binH1["+i+"] length: "+binH1[i].length);
        }

        System.out.println("DEBUG Print flattened H1 bits");
        System.out.println("DEBUG: Is H1 Len == flatIndex: "+(Original.H1_TOTAL_LEN == flatIndex));
        for(int i=0; i<Original.H1_TOTAL_LEN; ++i){
            System.out.println("DEBUG: Flat H1_["+i+"]: "+binArray[i]);
        }

        System.out.println("DEBUG Print inflated H1 bits");
        int DEBUG_total_index=0;
        for(int i=0; i<binH1.length; ++i){
            for(int j=0; j<binH1[i].length; ++j, ++DEBUG_total_index){
                System.out.println("DEBUG: H1["+i+"]["+j+"]_["+DEBUG_total_index+"]: "+binH1[i][j]);
            }
        }
        System.out.println("DEBUG: END Step 1.3");
        //END DEBUG SECTION

        //1.4 Set the lengths of the H2 fields from the translated H1 values.
        binH2[0] = new boolean[BinaryTools.toUnsignedInt(binH1[Original.H_INDEX_SYM_LEN])];
        binH2[1] = new boolean[BinaryTools.toUnsignedInt(binH1[Original.H_INDEX_CT_LEN])];
        binH2[2] = new boolean[BinaryTools.toUnsignedInt(binH1[Original.H_INDEX_H_GAP_LEN])];
        int h2_len = binH2[0].length + binH2[1].length + binH2[2].length;

        //DEBUG SECTION
        System.out.println("DEBUG: START Step 1.4");
        System.out.println("DEBUG: binH2[0] length: "+binH2[0].length);
        System.out.println("DEBUG: binH2[1] length: "+binH2[1].length);
        System.out.println("DEBUG: binH2[2] length: "+binH2[2].length);
        System.out.println("DEBUG: binH2 total length: "+h2_len);
        System.out.println("DEBUG: END Step 1.4");
        //END DEBUG SECTION

        //1.5 Set excess bits in (if any) to be read in
        boolean[] excessBinArr = new boolean[binArray.length-flatIndex];

        while(flatIndex < binArray.length){ //flatIndex starts at Original.H1_COUNT, sets old excess to this point
            excessBinArr[flatIndex-Original.H1_COUNT] = binArray[flatIndex];
            ++flatIndex;
        }

        //DEBUG SECTION
        System.out.println("DEBUG: START Step 1.5");
        System.out.println("DEBUG: excess Bit Size: "+excessBinArr.length);
        System.out.println("DEBUG: END Step 1.5");
        //END DEBUG SECTION

        //1.6 Read in next batch of next bytes to include to include all of H2
        byteCount = h2_len - excessBinArr.length; //temporary bit count (not byte count yet)
        if(byteCount%8 == 0) byteCount = (byteCount >>> 3);
        else byteCount = (byteCount >>> 3) + 1;

        byteArray = new byte[byteCount];
        binArray = new boolean[byteCount << 3];

        try {byteCount=binFile.read(byteArray);}catch(Exception err){err.printStackTrace();}

        //DEBUG SECTION
        System.out.println("DEBUG: START Step 1.6");
        System.out.println("DEBUG: next byteArray Length: "+byteArray.length);
        System.out.println("DEBUG: next binArray Length: "+binArray.length);
        System.out.println("DEBUG: END Step 1.6");
        //END DEBUG SECTION

        //1.7 Set flattened h2 bin values
        //1.7.1 Set first Excess bits
        for(int i=0; i<excessBinArr.length; ++i){binArray[i]=excessBinArr[i];}

        //DEBUG SECTION
        System.out.println("DEBUG: START Step 1.7.1");
        System.out.println("DEBUG: excessBinArr Length: "+excessBinArr.length);
        System.out.println("DEBUG: END Step 1.7.1");
        //END DEBUG SECTION

        //1.7.2 Set remaining flattened h2 bits
        for(int i=0; i<byteCount; ++i){
            BinaryTools.setSubsetUnsignedInt((i << 3) + excessBinArr.length,8,byteArray[i],binArray);
        }

        //DEBUG SECTION
        System.out.println("DEBUG: START Step 1.7.2");
        System.out.println("DEBUG: END Step 1.7.2");
        //END DEBUG SECTION

        //1.8 Set H2 from flattened h2 bits
        flatIndex=0;
        for(int i=0; i<binH2.length; ++i){
            for(int j=0;j<binH2[i].length; ++j, ++flatIndex){
                binH2[i][j]=binArray[flatIndex];
            }
        }

        //DEBUG SECTION
        System.out.println("DEBUG: START Step 1.8");
        System.out.println("DEBUG Print flattened H2 bits");
        for(int i=0; i<h2_len; ++i){
            System.out.println("DEBUG: Flat H2_["+i+"]: "+binArray[i]);
        }

        System.out.println("DEBUG Print inflated H2 bits");
        int DEBUG_total_index2=0;
        for(int i=0; i<binH2.length; ++i){
            for(int j=0; j<binH2[i].length; ++j, ++DEBUG_total_index2){
                System.out.println("DEBUG: H2["+i+"]["+j+"]_["+DEBUG_total_index2+"]: "+binH2[i][j]);
            }
        }

        System.out.println("DEBUG: END Step 1.8");
        //END DEBUG SECTION

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

        //DEBUG SECTION
        System.out.println("DEBUG: START Step 2");
        System.out.println("DEBUG: Symbol: "+lexical.getSymbol());
        System.out.println("DEBUG: Interval: "+lexical.getInterval());
        System.out.println("DEBUG: Data bit length: "+lexical.getDataBitLength());
        System.out.println("DEBUG: Data count: "+lexical.getDataCount());
        System.out.println("DEBUG: END Step 2");
        //END DEBUG SECTION

        //3. Fill in excess bits into the bitQueue
        for(int i=flatIndex; i<binArray.length; ++i){
            bitQueue.add(Boolean.valueOf(binArray[i]));
            //DEBUG SECTION
            System.out.println("DEBUG: excess data bit: "+binArray[i]);
            //END DEBUG SECTION
        }

        byteArray = new byte[fileReadByteChunkSize];
        binArray = new boolean[lexical.getDataBitLength()];

        //Main loop for gathering data
        try{byteCount=binFile.read(byteArray);}catch(Exception err){}

        do{
            //4. Set all bits from byteCHunk
            for(int i=0; i<byteCount; ++i){
                byte tmp=byteArray[i];
                for(int j=0; j<8; ++j){
                    bitQueue.add(Boolean.valueOf(tmp%2 == 1));
                    tmp = (byte)(tmp >>> 1);
                }
            }

            //5. Read in datapoints to final collection
            while(bitQueue.size() >= lexical.getDataBitLength()){
                //5.1 Set data point bits
                for(int i=0; i<lexical.getDataBitLength(); ++i){
                    binArray[i]=bitQueue.remove();
                }

                //5.2 Add data point to collection
                stickList.add(lexical.getRefinedDataFlat(binArray));
            }

            try{byteCount=binFile.read(byteArray);}catch(Exception err){}
        }while(byteCount == fileReadByteChunkSize);

        //6. Handle last chunk where possible

        //7. Close file
        try{binFile.close();}catch(Exception err){err.printStackTrace();}
        //DEBUG SECTION
        for(StickDouble stick : stickList){
            System.out.print("DEBUG: UTC: "+stick.getUTC()+" ");
            System.out.print("DEBUG: O: "+stick.getO()+" ");
            System.out.print("DEBUG: H: "+stick.getH()+" ");
            System.out.print("DEBUG: L: "+stick.getL()+" ");
            System.out.print("DEBUG: C: "+stick.getC()+" ");
            System.out.println("DEBUG: V: "+stick.getV()+" ");
        }
        //END DEBUG SECTION
    }
}