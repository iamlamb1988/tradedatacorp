/**
 * @author Bruce Lamb
 * @since 31 MAY 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.smelter.lexical.binary.Original;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;

//TODO: Planning in progress. This class is responsible for writing a binary file specifically adhering to an Original Lexical.
public class OriginalFileSmelter implements FileSmelterStateful<StickDouble>{
    private Original binaryTranslator; //Translates from ? to flattened bin (type boolean[])
    private Path targetFile;
    private ArrayDeque<boolean[]> crucible;
    private int fileWriteByteChunkSize = 64;

    //Constructor
    //TODO
    public OriginalFileSmelter(Original originalTranslator){
        binaryTranslator = originalTranslator.clone();
        targetFile = null;
        crucible = new ArrayDeque<boolean[]>();
    }

    //Smelter Overrides
    public void smelt(StickDouble dataStick){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(dataStick){
            rawDataQueue = new ArrayDeque<>(1);
            rawDataQueue.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
        writeDataToNewFile(targetFile, rawDataQueue);
    }

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
        //1. Empty crucible into local ArrayList (will keep this as the only Thread adding elements to it)
        //2. Set boolean[] header based on binaryLexical settings and localCrubible size.
        //3. Clone the header locally (Thread safe operation)

        //NOTE: At this point local header and local crucible is set.
        //this.binaryLexical and this.crucible is now safe to change and update
        //because these local instances are locked in for file writing.

        //4. Open this.targetFile to begin writing (OutputFileStream)
        //5. Write the binary header.
        //6. begin streaming and writing a file.
        //May need Threads to keep this smooth
        //May need more planning
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

    public void writeDataToNewFile(Path file, ArrayDeque<boolean[]> dataQueue){
        //1. Initialize variables
        //1.1 Open resultFile to begin writing (OutputFileStream)
        FileOutputStream resultFile;
        try{
            resultFile = new FileOutputStream(file.toFile(),false);
        }
        catch(Exception err){
            err.printStackTrace();
            return;
        }

        //1.2 Initialize working variables
        ArrayDeque<boolean[]> hotCrucible;
        ArrayDeque<Boolean> bitAligner = new ArrayDeque<Boolean>(); //Used to store partial bits for alignment.
        ArrayDeque<Byte> moltenData; //bytes ready to be written
        boolean[] header;
        boolean[] currentDataStick; //tmp variable to sqeeze into a byte.
        boolean[] currentByte = new boolean[8]; // tmp variable, current byte being "smelted".
        byte[] moltenByteChunk = new byte[fileWriteByteChunkSize]; //Chunk to be actively written when full.
        //2. Prepare assembly line threads

    }

    //Assembly line interfaces (5 resoruces, 4 threads)
    private class CrucibleToHotCrucible implements Runnable{
        private int MAX_TOTAL_COUNT;
        private int MAX_DELAY_MS;

        private boolean isFinished;
        private int count;

        private CrucibleToHotCrucible(){
            isFinished = false;
            count = 0;
        }

        @Override
        public void run(){

        }
    }

    private class HotCrucibleToBitAligner implements Runnable{
        @Override
        public void run(){}
    }

    private class BitAlignerToMoltenData implements Runnable{
        @Override
        public void run(){}
    }

    private class MoltenDataToFile implements Runnable{
        @Override
        public void run(){}
    }
}
