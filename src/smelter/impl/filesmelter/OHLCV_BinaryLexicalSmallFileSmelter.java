/**
 * @author Bruce Lamb
 * @since 04 JUL 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.tools.binarytools.BinaryTools;
import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.smelter.filesmelter.FileSmelterStateful;
import tradedatacorp.smelter.stringsmelter.StringSmelterStateful;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;

/**
 * This class writes StickDouble types to a compressed binary file that is formated to an OHLCV_BinaryLexical binaryTranslator.
 * This class is a functional prototype that will be used as a blueprint for {@link OHLCV_BinaryLexicalSmallFileSmelter}.
 */
public class OHLCV_BinaryLexicalSmallFileSmelter implements StringSmelterStateful<StickDouble>, FileSmelterStateful<StickDouble>{
    private OHLCV_BinaryLexical binaryTranslator; //Translates from ? to flattened bin (type boolean[])
    private Path targetFile;
    private ArrayDeque<boolean[]> crucible;
    private int fileWriteByteChunkSize = 64;

    //Constructor
    /**
     * Creates an instance of this class from a clone of {@code originalTranslator}
     * @param originalTranslator Lexical that will be used to encode and decode {@link StickDouble} within crucible
     */
    public OHLCV_BinaryLexicalSmallFileSmelter(OHLCV_BinaryLexical originalTranslator){
        binaryTranslator = originalTranslator.clone();
        targetFile = null;
        crucible = new ArrayDeque<boolean[]>();
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

    //FileSmelter Overrides from FileSmelterStateful
    /**
     * Writes stored {@code dataStick} to {@code destinationPathName} in accordance with the {@code binaryLexical}.
     * @param dataStick The data stick that will be written to binary file.
     * @param destinationPathName The resultant file that will be created.
     */
    @Override
    public void smeltToFile(StickDouble dataStick, Path destinationPathName){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(dataStick){
            rawDataQueue = new ArrayDeque<>(1);
            rawDataQueue.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
        writeDataToNewFile(destinationPathName, rawDataQueue, true);
    }

    /**
     * Writes stored {@code stickArray} to {@code destinationPathName} in accordance with the {@code binaryLexical}.
     * @param stickArray The array of data sticks that will be written to binary file.
     * @param destinationPathName The resultant file that will be created.
     */
    @Override
    public void smeltToFile(StickDouble[] stickArray, Path destinationPathName){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(stickArray){
            rawDataQueue = new ArrayDeque<>(stickArray.length);
            for(StickDouble stick : stickArray){
                rawDataQueue.add(binaryTranslator.getBinaryDataFlat(stick));
            }
        }
        writeDataToNewFile(destinationPathName, rawDataQueue, true);
    }

    /**
     * Writes stored {@code stickDataCollection} to {@code targetFile} in accordance with the {@code binaryLexical}.
     * @param stickDataCollection The collection of data sticks that will be written to binary file.
     * @param destinationPathName The resultant file that will be created.
     */
    @Override
    public void smeltToFile(Collection<StickDouble> stickDataCollection, Path destinationPathName){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(stickDataCollection){
            rawDataQueue = new ArrayDeque<boolean[]>(stickDataCollection.size());
            for(StickDouble stick : stickDataCollection){
                rawDataQueue.add(binaryTranslator.getBinaryDataFlat(stick));
            }
        }
        writeDataToNewFile(destinationPathName, rawDataQueue, true);
    }

    //FileSmelterStateful Overrides
    @Override
    public void smeltToFile(StickDouble dataStick){smeltToFile(dataStick,targetFile);}

    @Override
    public void smeltToFile(StickDouble[] rawDataArray){smeltToFile(rawDataArray,targetFile);}

    @Override
    public void smeltToFile(Collection<StickDouble> rawDataCollection){smeltToFile(rawDataCollection,targetFile);}

    @Override
    public void smeltToFile(Path destinationPathName){writeDataToNewFile(destinationPathName,crucible,true);}

    @Override
    public void smeltToFile(){writeDataToNewFile(targetFile,crucible,true);}

    //StringSmelter Overrides
    /**
     * Processes a single data element.
     *
     * @param dataStick the data element to process
     */
    @Override
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

    //StringSmelterStateful Overrides
    public String smeltToString(){return writeDataToNewFile(targetFile,crucible,false);}

    //OHLCV_BinaryLexicalSmallFileSmelter methods
    /**
     * The core function that is used to write binary data in {@code dataQueue} to  to {@code file}.
     * @param file the target file {@link Path} where binary data will be written; the file will be created or overwritten.
     * This will have no relevance if {@code toFile} is true.
     * @param dataQueue a queue of boolean arrays, each representing a single data record to serialize and write.
     * * NOTE: If binaryLexical bits change, the dataQueue boolean[] may be incompatible with change.
     * @param toFile will write to specified file if true, otherwise will return content of file as string
     * @return The content of the binary file. If {@code toFile} is false then will return null
     * Each character represents an 8 bit ASCII char value.
     * NOTE: Java primitive char's are 16 bits but should be treated as 8 bits.
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
        /**
         * Writes bytes to String or File based on implementation child class.
         * @param nextBytes
         */
        protected abstract void writeBytes(byte[] nextBytes);

        /**
         * Writes bytes to String or File based on implementation child class.
         * @param nextBytes The byte array that will be written
         * @param startIndex The starting inclusive index of byte array
         * @param length The length of the array that will be written.
         */
        protected abstract void writeBytes(byte[] nextBytes, int startIndex, int length);

        /**
         * Returns the string or closes the file based on implementing child class.
         * @return returns the string or closes the file based on implementing child class.
         */
        protected abstract String finalizeData();
    }

    private class FileWriter extends DataWriter{
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