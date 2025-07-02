/**
 * @author Bruce Lamb
 * @since 16 JUN 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.tools.binarytools.BinaryTools;
import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;

/**
 * This class writes StickDouble types to a compressed binary file that is formated to an OHLCV_BinaryLexical binaryTranslator.
 * This class is a functional prototype that will be used as a blueprint for {@link OHLCV_BinaryLexicalSmallFileSmelter}.
 */
public class OHLCV_BinaryLexicalSmallFileSmelter implements FileSmelterStateful<StickDouble>{
    private OHLCV_BinaryLexical binaryTranslator; //Translates from ? to flattened bin (type boolean[])
    private Path targetFile;
    private ArrayDeque<boolean[]> crucible;
    private int fileWriteByteChunkSize = 64;

    //Constructor
    /**
     * Creates an instance of this class from a clone of {@code originalTranslator}
     * @param originalTranslator
     */
    public OHLCV_BinaryLexicalSmallFileSmelter(OHLCV_BinaryLexical originalTranslator){
        binaryTranslator = originalTranslator.clone();
        targetFile = null;
        crucible = new ArrayDeque<boolean[]>();
    }

    //Smelter Overrides
    /**
     * Writes stored {@code dataStick} to {@code targetFile} in accordance with the {@code binaryLexical}.
     * @param dataStick The data stick that will be written to binary file.
     */
    public void smelt(StickDouble dataStick){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(dataStick){
            rawDataQueue = new ArrayDeque<>(1);
            rawDataQueue.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
        writeDataToNewFile(targetFile, rawDataQueue, true);
    }

    /**
     * Writes stored {@code stickArray} to {@code targetFile} in accordance with the {@code binaryLexical}.
     * @param stickArray The array of data sticks that will be written to binary file.
     */
    public void smelt(StickDouble[] stickArray){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(stickArray){
            rawDataQueue = new ArrayDeque<>(stickArray.length);
            for(StickDouble stick : stickArray){
                rawDataQueue.add(binaryTranslator.getBinaryDataFlat(stick));
            }
        }
        writeDataToNewFile(targetFile, rawDataQueue, true);
    }

    /**
     * Writes stored {@code stickDataCollection} to {@code targetFile} in accordance with the {@code binaryLexical}.
     * @param stickDataCollection The collection of data sticks that will be written to binary file.
     */
    public void smelt(Collection<StickDouble> stickDataCollection){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(stickDataCollection){
            rawDataQueue = new ArrayDeque<boolean[]>(stickDataCollection.size());
            for(StickDouble stick : stickDataCollection){
                rawDataQueue.add(binaryTranslator.getBinaryDataFlat(stick));
            }
        }
        writeDataToNewFile(targetFile, rawDataQueue, true);
    }

    /**
     * Processes a single data element.
     *
     * @param dataStick the data element to process
     */
    public String smeltToString(StickDouble dataStick){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(dataStick){
            rawDataQueue = new ArrayDeque<>(1);
            rawDataQueue.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
        return writeDataToNewFile(targetFile, rawDataQueue, false);
    }

     /**
     * Processes an array of data elements.
     *
     * @param stickArray the array of data elements to process
     */
    public String smeltToString(StickDouble[] stickArray){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(stickArray){
            rawDataQueue = new ArrayDeque<>(stickArray.length);
            for(StickDouble stick : stickArray){
                rawDataQueue.add(binaryTranslator.getBinaryDataFlat(stick));
            }
        }
        return writeDataToNewFile(targetFile, rawDataQueue, false);
    }

    /**
     * Processes a collection of data elements.
     *
     * @param stickDataCollection the collection of data elements to process
     */
    public String smeltToString(Collection<StickDouble> stickDataCollection){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(stickDataCollection){
            rawDataQueue = new ArrayDeque<boolean[]>(stickDataCollection.size());
            for(StickDouble stick : stickDataCollection){
                rawDataQueue.add(binaryTranslator.getBinaryDataFlat(stick));
            }
        }
        return writeDataToNewFile(targetFile, rawDataQueue, false);
    }

    //SmelterStateful Overrides
    /**
     * Adds {@code dataStick} to {@code crucible}.
     * @param dataStick The value to be added to the {@code crucible} after binary conversion with {@code binaryLexical}.
     */
    @Override
    public void addData(StickDouble dataStick){
        synchronized(crucible){crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));}
    }

    /**
     * Adds {@code dataStickArray} to {@code crucible}.
     * @param dataStickArray The values to be added to the {@code crucible} after binary conversion with {@code binaryLexical}.
     */
    @Override
    public void addData(StickDouble[] dataStickArray){
        synchronized(crucible){
            for(StickDouble dataStick : dataStickArray){crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));}
        }
    }

    /**
     * Adds {@code dataStickCollection} to {@code crucible}.
     * @param dataStickCollection The values to be added to {@code crucible} after binary conversion with {@code binaryLexical}.
     */
    @Override
    public void addData(Collection<StickDouble> dataStickCollection){
        synchronized(crucible){
            for(StickDouble dataStick : dataStickCollection){
                crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));
            }
        }
    }

    /**
     * Writes stored data in {@code crucible} to {@code targetFile} in accordance with the {@code binaryLexical}.
     */
    @Override
    public void smelt(){writeDataToNewFile(targetFile, crucible, true);}

    //FileSmelterStateful Overrides
    /**
     * Writes stored data in {@code crucible} to {@code destinationPathName} in accordance with the {@code binaryLexical}.
     * @param destinationPathName The file where the binary data will be written to.
     */
    @Override
    public void smeltToFile(Path destinationPathName){writeDataToNewFile(destinationPathName,crucible,true);}

    //OHLCV_BinaryLexicalFileSmelter methods
    /**
     * The core function that is used to write binary data in {@code dataQueue} to  to {@code file}.
     * @param file The file where the binary data will be written to.
     * @param dataQueue The queue containing the binary data that adheres to {@code binaryLexical}.
     * NOTE: If binaryLexical bits change, the dataQueue boolean[] may be incompatible with change.
     */
    public String writeDataToNewFile(Path file, ArrayDeque<boolean[]> dataQueue, boolean toFile){
        //1. Initialize variables
        //1.1 Open resultFile to begin writing (OutputFileStream)
        FileOutputStream resultFile;
        DataWriter returnData;
        if(toFile) returnData = new FileWriter(file);
        else returnData = new StringWriter();

        //1.2 Initialize working variables
        ArrayDeque<boolean[]> hotCrucible;
        ArrayDeque<Boolean> bitAligner = new ArrayDeque<Boolean>(); //Used to store partial bits for alignment.
        ArrayDeque<Byte> moltenData; //bytes ready to be written
        boolean[] header;
        boolean[] currentDataStick; //tmp variable to squeeze into a byte.
        boolean[] currentByte = new boolean[8]; // tmp variable, current byte being "smelted".
        byte[] moltenByteChunk = new byte[fileWriteByteChunkSize]; //Chunk to be actively written when full.

        synchronized (binaryTranslator){
            //2. Empty dataCollection into local ArrayList (will keep this as the only Thread adding elements to it)
            synchronized (dataQueue) {
                hotCrucible = new ArrayDeque<boolean[]>(dataQueue.size());
                while(!dataQueue.isEmpty()){
                    hotCrucible.add(dataQueue.remove());
                }
            }

            //3. Set boolean[] header based on binaryLexical settings and localCrubible size.
            binaryTranslator.setDataCount(hotCrucible.size());
            header = binaryTranslator.getBinaryHeaderFlat();
            moltenData = new ArrayDeque<Byte>(((dataQueue.size() + 1) >>> 3) + ((header.length + 1) >>> 3));
        }

        //4. Add full bytes of header to molten data.
        int fullHeaderBytes = header.length >>> 3; //everything except last complete byte (if it exists)
        for(int i=0; i<fullHeaderBytes; ++i){
            moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedIntFromBoolSubset(header,i << 3,8)));
        }

        //5. If not memory alligned, add last part of header. (Should skip loop if perfectly aligned by 8 bits)
        for(int i=fullHeaderBytes << 3; i<header.length; ++i){
            bitAligner.add(Boolean.valueOf(header[i]));
        }

        //6. Stuff and write 8 bits at a time.
        while(!hotCrucible.isEmpty()){
            currentDataStick = hotCrucible.remove();
            for(int i=0; i<currentDataStick.length; ++i){bitAligner.add(currentDataStick[i]);}

            //Squeeze as many full 8 bit sets to a moltenByte as possible.
            while(bitAligner.size() >= 8){
                for(int i=0; i<8; ++i){currentByte[i] = bitAligner.remove();}
                moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedInt(currentByte)));
            }

            while(moltenData.size() >= fileWriteByteChunkSize){
                for(int i=0; i<fileWriteByteChunkSize; ++i){moltenByteChunk[i] = moltenData.remove().byteValue();}
                returnData.writeBytes(moltenByteChunk);
            }
        }

        //7. Finish writing final bytes to file
        //7.1 Write as many full chunks as possible
        while(bitAligner.size() >= 8){
            for(int i=0; i<8; ++i){currentByte[i] = bitAligner.remove();}
            moltenData.add((byte)BinaryTools.toUnsignedInt(currentByte));
        }

        while(moltenData.size() >= fileWriteByteChunkSize){
            for(int i=0; i<fileWriteByteChunkSize; ++i){moltenByteChunk[i] = moltenData.remove().byteValue();}
            returnData.writeBytes(moltenByteChunk);
        }

        //7.2 Write as many full bytes as possible (if any, should be less than fileWriteByteChunkSize)
        if(moltenData.size()>0){
            moltenByteChunk = new byte[moltenData.size()];
            for(int i=0; moltenData.size()>0; ++i){moltenByteChunk[i] = moltenData.remove().byteValue();}
            returnData.writeBytes(moltenByteChunk);
        }

        //7.3 Write last incomplete byte to file with appropriate left shift (extra bits will be)
        if(bitAligner.size() > 0){
            boolean[] lastByte = new boolean[8];
            int i=0;
            //7.3.1 Fill in bits from the left
            while(bitAligner.size()>0){
                lastByte[i]=bitAligner.remove().booleanValue();
                ++i;
            }

            //7.3.2 Pad remaining 0s to the right
            while(i<8){
                lastByte[i]=false;
                ++i;
            }
            returnData.writeBytes(new byte[]{(byte)(BinaryTools.toUnsignedInt(lastByte))});
        }

        //8. Close file.
        return returnData.finalizeData();
    }

    /**
     * Sets the preset target file path for future file write operations.
     *
     * @param path the Path file location.
     */
    public void setTargetFile(Path path){targetFile = path;}

    /**
     * Sets the relative path and name to target file.
     * The relative path is based on the location of current working directory.
     * @param relativePathName The path of the file for default write operations.
     */
    public void setTargetFile(String relativePathName){targetFile = Path.of(relativePathName);}

    /**
     * Sets the absolute path and name to target file.
     * The relative path is based on the location of current working directory.
     * @param absolutePathName The path of the file for default write operations.
     */
    public void setAbsoluteTargetFile(String absolutePathName){targetFile = Paths.get(absolutePathName);}

    /**
     * Sets the absolute path from a relative name respective to current working directory.
     * The relative path is based on the location of current working directory.
     * @param relativePathName The path of the file for default write operations.
     */
    public void setAbsoluteFromRelativeTargetFile(String relativePathName){
        targetFile = Path.of(relativePathName).toAbsolutePath();
    }

    private abstract class DataWriter{
        protected abstract void writeBytes(byte[] nextBytes);
        protected abstract void writeBytes(byte[] nextBytes, int startIndex, int length);
        protected abstract String finalizeData();
    }

    protected class FileWriter extends DataWriter{
        private FileOutputStream resultFile;
        private FileWriter(Path path){
            try{resultFile = new FileOutputStream(path.toFile(),false);}
            catch(Exception err){err.printStackTrace();}
        }

        @Override
        protected void writeBytes(byte[] nextBytes){
            try{resultFile.write(nextBytes);}
            catch(Exception err){
                err.printStackTrace();
                try{resultFile.close();}
                catch(Exception err2){err2.printStackTrace();}
            }
        }

        @Override
        protected void writeBytes(byte[] nextBytes, int startIndex, int length){
            try{resultFile.write(nextBytes,0,length);}
            catch(Exception err){
                err.printStackTrace();
                try{resultFile.close();}
                catch(Exception err2){err2.printStackTrace();}
            }
        }

        @Override
        protected String finalizeData(){
            try{resultFile.close();}
            catch(Exception err){err.printStackTrace();}
            return null;
        }
    }

    private class StringWriter extends DataWriter{
        private StringBuilder strbldr;
        private StringWriter(){strbldr = new StringBuilder();}

        @Override
        protected void writeBytes(byte[] nextBytes){
            for(byte b : nextBytes){strbldr.append((char)b);}
        }

        @Override
        protected void writeBytes(byte[] nextBytes, int startIndex, int length){
            int limit = startIndex + length;
            for(; startIndex<limit; ++startIndex){strbldr.append((char)nextBytes[startIndex]);}
        }

        @Override
        protected String finalizeData(){return strbldr.toString();}
    }

}