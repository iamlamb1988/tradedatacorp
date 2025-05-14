/**
 * @author Bruce Lamb
 * @since 19 APR 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.smelter.lexical.binary.BinaryTools;
import tradedatacorp.smelter.lexical.binary.Original;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;

/**
 * This class writes StickDouble types to a compressed binary file that is formated to an Original binaryTranslator.
 * This class is a functional prototype that will be used as a blueprint to write a fully streamlined thread safe filewriter.
 * NOTE: If binary Lexical changes while data is in the crucible, the crucible data may be corrupt. Will change on full class implementation.
 */
public class OriginalSmallFileSmelter implements FileSmelterStateful<StickDouble>{
    private Original binaryTranslator; //Translates from ? to flattened bin (type boolean[])
    private Path targetFile;
    private ArrayDeque<boolean[]> crucible;
    private int fileWriteByteChunkSize = 64;

    //Thread safe handling variables for binaryLexical instance
    private int lexicalReaderCount;
    private int lexicalWriterRequestCount;
    private boolean isLexicalWriting;

    private void threadSafeLexicalRead(Runnable readMethod){
        synchronized(binaryTranslator){
            while(isLexicalWriting || lexicalWriterRequestCount > 0){
                try{
                    wait();
                }
                catch(Exception err){
                    err.printStackTrace();
                }
            }
            ++lexicalReaderCount;
        }
        readMethod.run();
        synchronized(binaryTranslator){
            --lexicalReaderCount;
            notifyAll();
        }
    }

    private void threadSafeLexicalWrite(Runnable writeMethod){
        boolean requested = false;
        synchronized(binaryTranslator){
            if(lexicalReaderCount > 0 || isLexicalWriting){
                requested = true;
                ++lexicalWriterRequestCount;
                while(lexicalReaderCount > 0 || isLexicalWriting){
                    try{
                        wait();
                    }
                    catch(Exception err){
                        err.printStackTrace();
                    }
                }
            }
            isLexicalWriting = true;
            if(requested) --lexicalWriterRequestCount;
            writeMethod.run();
            isLexicalWriting = false;
            notifyAll();
        }
    }

    //Constructor
    /**
     * Creates an instance of this class from a clone of {@code originalTranslator}
     * @param originalTranslator
     */
    public OriginalSmallFileSmelter(Original originalTranslator){
        binaryTranslator = originalTranslator.clone();
        targetFile = null;
        crucible = new ArrayDeque<boolean[]>();

        lexicalReaderCount = lexicalWriterRequestCount = 0;
        isLexicalWriting = false;
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
        writeDataToNewFile(targetFile, rawDataQueue);
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
        writeDataToNewFile(targetFile, rawDataQueue);
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
        writeDataToNewFile(targetFile, rawDataQueue);
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
    public void smelt(){writeDataToNewFile(targetFile,crucible);}

    //FileSmelterStateful Overrides
    /**
     * Writes stored data in {@code crucible} to {@code destinationPathName} in accordance with the {@code binaryLexical}.
     * @param destinationPathName The file where the binary data will be written to.
     */
    @Override
    public void smeltToFile(Path destinationPathName){writeDataToNewFile(destinationPathName,crucible);}

    //OriginalFileSmelter methods
    /**
     * The core function that is used to write binary data in {@code dataQueue} to  to {@code file}.
     * @param file The file where the binary data will be written to.
     * @param dataQueue The queue containing the binary data that adheres to {@code binaryLexical}.
     * NOTE: If binaryLexical bits change, the dataQueue boolean[] may be incompatible with change.
     */
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
                try{resultFile.write(moltenByteChunk);}
                catch(Exception err){ err.printStackTrace();}
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
            try{resultFile.write(moltenByteChunk);}
            catch(Exception err){ err.printStackTrace();}
        }

        //7.2 Write as many full bytes as possible (if any, should be less than fileWriteByteChunkSize)
        if(moltenData.size()>0){
            moltenByteChunk = new byte[moltenData.size()];
            for(int i=0; moltenData.size()>0; ++i){moltenByteChunk[i] = moltenData.remove().byteValue();}
            try{resultFile.write(moltenByteChunk);}
            catch(Exception err){ err.printStackTrace();}
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
            try{resultFile.write((byte)(BinaryTools.toUnsignedInt(lastByte)));}
            catch(Exception err){ err.printStackTrace();}
        }

        //8. Close file.
        try{resultFile.close();}catch(Exception err){err.printStackTrace();}
    }

    /**
     * Sets the relative path and name to target file.
     * The relative path is based on the location of current working directory.
     * @param relativePathName The path of the file for default write operations.
     */
    public void setTargetFile(String relativePathName){
        targetFile = Path.of(relativePathName);
    }

    /**
     * Sets the absolute path and name to target file.
     * The relative path is based on the location of current working directory.
     * @param absolutePathName The path of the file for default write operations.
     */
    public void setAbsoluteTargetFile(String absolutePathName){
        targetFile = Paths.get(absolutePathName);
    }

    /**
     * Sets the absolute path from a relative name respective to current working directory.
     * The relative path is based on the location of current working directory.
     * @param relativePathName The path of the file for default write operations.
     */
    public void setAbsoluteFromRelativeTargetFile(String relativePathName){
        targetFile = Path.of(relativePathName).toAbsolutePath();
    }
}
