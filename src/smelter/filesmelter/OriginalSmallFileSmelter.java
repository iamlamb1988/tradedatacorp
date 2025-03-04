/**
 * @author Bruce Lamb
 * @since 4 MAR 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.smelter.lexical.binary.BinaryTools;
import tradedatacorp.smelter.lexical.binary.Original;

import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;

//TODO: Planning in progress. This class is intended to write all data to a file all at once.
//NOTE: This is meant for "small" files. "small" may mean different things based on Java version and system.
public class OriginalSmallFileSmelter implements FileSmelterStateful<StickDouble>{
    private Original binaryTranslator; //Translates from ? to flattened bin (type boolean[])
    private Path targetFile;
    private ArrayDeque<boolean[]> crucible;

    //Constructor
    //TODO
    public OriginalSmallFileSmelter(Original originalTranslator){
        binaryTranslator = originalTranslator.clone();
        targetFile = null;
        crucible = new ArrayDeque<boolean[]>();
    }

    //Smelter Overrides
    public void smelt(StickDouble dataStick){}
    public void smelt(StickDouble[] rawDataArray){}
    public void smelt(Collection<StickDouble> rawDataArray){}

    //SmelterStateful Overrides
    public void addData(StickDouble dataStick){crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));}

    public void addData(StickDouble[] dataStickArray){
        for(StickDouble dataStick : dataStickArray){
            crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
    }

    public void addData(Collection<StickDouble> dataStickCollection){
        for(StickDouble dataStick : dataStickCollection){
            crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
    }

    //TODO
    public void smelt(){
        
        //1. Open this.targetFile to begin writing (OutputFileStream)
        //TODO

        ArrayDeque<boolean[]> hotCrucible;
        ArrayDeque<Boolean> bitAligner = new ArrayDeque<Boolean>(); //Used to store partial bits for alignment.
        ArrayDeque<Byte> moltenData; //bytes ready to be written
        boolean[] header;
        boolean[] currentDataStick; //tmp variable to sqeeze into a byte.
        boolean[] currentByte = new boolean[8]; // tmp variable, current byte being "smelted".
        byte moltenByte; //tmp variable, current byte actively being written to file
        synchronized (binaryTranslator) {
            //2. Empty crucible into local ArrayList (will keep this as the only Thread adding elements to it)
            synchronized (crucible) {
                hotCrucible = new ArrayDeque<boolean[]>(crucible.size());
                while(!crucible.isEmpty()){
                    hotCrucible.add(crucible.remove());
                }
            }
            //3. Set boolean[] header based on binaryLexical settings and localCrubible size.
            binaryTranslator.setDataCount(hotCrucible.size());
            header = binaryTranslator.getBinaryHeaderFlat();
            moltenData = new ArrayDeque<Byte>(((crucible.size() + 1) >>> 3) + ((header.length + 1) >>> 3));
        }

        //4. Write full bytes of header.
        int fullHeaderBytes = header.length >>> 3;
        for(int i=0; i<fullHeaderBytes; ++i){
            moltenData.add((byte)BinaryTools.toUnsignedIntFromBoolSubset(header,i,8));
        }

        //5. If not memory alligned, write last part of header. (Should skip loop if perfectly aligned by 8 bits)
        for(int i=fullHeaderBytes; i<header.length; ++i){
            bitAligner.add(header[i]);
        }

        //6. Stuff and write 8 bits at a time.
        while(!hotCrucible.isEmpty()){
            currentDataStick = hotCrucible.remove();
            for(int i=0; i<currentDataStick.length; ++i){
                bitAligner.add(currentDataStick[i]);
            }

            //Squeeze as many full 8 bit sets to a moltenByte as possible.
            while(bitAligner.size() >= 8){
                for(int i=0; i<8; ++i){
                    currentByte[i] = bitAligner.remove();
                }
                moltenData.add((byte)BinaryTools.toUnsignedInt(currentByte));
            }

            //TODO: handle writing, either one char at a time, or wait until it fills up a little bit..
            while(!moltenData.isEmpty()){
                moltenByte = moltenData.remove();
                //TODO: Write the moltenByte to the file.
            }
        }

        //7. Finish last bits (should be 7 or less) and fill with 0's until a 8 bits are complete
        //TODO

        //8. Close file.
    }

    //FileSmelterStateful Overrides
    //TODO
    public void smeltToFile(Path destinationPathName){
        //1. Set destinationPathName to this.targetFile
        //2. Smelt the file (smelt())
   
    }

    //OriginalFileSmelter methods
    public void setTargetFile(String relativePathName){}
    public void setAbsoluteTargetFile(String absolutePathName){}
}
