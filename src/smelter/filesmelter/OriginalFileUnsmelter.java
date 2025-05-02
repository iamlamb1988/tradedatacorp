/**
 * @author Bruce Lamb
 * @since 1 MAY 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.smelter.lexical.binary.BinaryTools;

import java.io.FileInputStream;
import java.nio.file.Path;

public class OriginalFileUnsmelter{
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
        for(int i=0; i<binH1.length; ++i){
            binH1[i] = new boolean[Original.getHeader1BitLength(i)];
            for(int k=0; k<binH1[i].length; ++flatIndex, ++k){
                binH1[i][k] = binArray[flatIndex];
            }
            System.out.println("DEBUG: binH1["+i+"] length: "+binH1[i].length);
        }

        System.out.println("DEBUG Is H1 Len == flatIndex: "+(Original.H1_TOTAL_LEN == flatIndex));

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

        try {byteCount=binFile.read(byteArray);}catch(Exception err){err.printStackTrace();}
        byteArray = new byte[byteCount];
        binArray = new boolean[byteCount >>> 3];

        //1.7 Set flattened h2 bin values
        //1.7.1 Set first Excess bits
        for(int i=0; i<excessBinArr.length; ++i){
            binArray[i]=excessBinArr[i];
        }

        //1.7.2 Set remaining flattened h2 bits
        for(int i=0; i<byteCount; ++i){
            BinaryTools.setSubsetUnsignedInt((i << 3) + excessBinArr.length,8,byteArray[i],binArray);
        }

        //1.8 Set H2 from flattened h2 bits
        flatIndex=0;
        for(int i=0; i<binH2.length; ++i, ++flatIndex){
            for(int j=0;j<binH2[i].length; ++i){
                binH2[i][j]=binArray[flatIndex];
            }
        }

        //2. Generate BinaryLexical with appropriate Symbol Name, BitLengths and values from H1.
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

        //DEBUG SECTION
        System.out.println("DEBUG");
        System.out.println("Symbol: "+lexical.getSymbol());
        System.out.println("Interval: "+lexical.getInterval());
        System.out.println("Data bit length: "+lexical.getDataBitLength());
        System.out.println("END DEBUG");
        //END DEBUG SECTION

        //3. Read first data point into a fixed size boolean[]. The length will be the exact match IAW binaryTranslator.
        //4. Split and parse each field (ll fields).
        //5. Create double value for OHLCV by dividing out the fractional field by the correct multiple of 10 IAW lexical.
        //6. Create new StickDouble instance and add it to "return" collection.
        //7.
        try{binFile.close();}catch(Exception err){err.printStackTrace();}
    }
}