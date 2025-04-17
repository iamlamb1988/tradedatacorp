/**
 * @author Bruce Lamb
 * @since 5 APR 2025
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

//NOTE: This is meant for "small" files. Will write to entire file all at once.
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
    //TODO
    public OriginalSmallFileSmelter(Original originalTranslator){
        binaryTranslator = originalTranslator.clone();
        targetFile = null;
        crucible = new ArrayDeque<boolean[]>();

        lexicalReaderCount = lexicalWriterRequestCount = 0;
        isLexicalWriting = false;
    }

    //Smelter Overrides
    //TODO
    public void smelt(StickDouble dataStick){}

    //TODO
    public void smelt(StickDouble[] rawDataArray){}

    //TODO
    public void smelt(Collection<StickDouble> rawDataArray){}

    //SmelterStateful Overrides
    @Override
    public void addData(StickDouble dataStick){
        synchronized(crucible){crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));}
    }

    @Override
    public void addData(StickDouble[] dataStickArray){
        synchronized(crucible){
            for(StickDouble dataStick : dataStickArray){crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));}
        }
    }

    @Override
    public void addData(Collection<StickDouble> dataStickCollection){
        synchronized(crucible){
            for(StickDouble dataStick : dataStickCollection){
                crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));
            }
        }
    }

    @Override
    public void smelt(){
        //1. Open this.targetFile to begin writing (OutputFileStream)
        FileOutputStream file;
        try{
            file = new FileOutputStream(targetFile.toFile(),false);
        }
        catch(Exception err){
            err.printStackTrace();
            return;
        }

        ArrayDeque<boolean[]> hotCrucible;
        ArrayDeque<Boolean> bitAligner = new ArrayDeque<Boolean>(); //Used to store partial bits for alignment.
        ArrayDeque<Byte> moltenData; //bytes ready to be written
        boolean[] header;
        boolean[] currentDataStick; //tmp variable to sqeeze into a byte.
        boolean[] currentByte = new boolean[8]; // tmp variable, current byte being "smelted".
        byte moltenByte; //tmp variable, current byte actively being added to the moltenByteChunk
        byte[] moltenByteChunk = new byte[fileWriteByteChunkSize]; //Chunk to be actively written when full.
        synchronized (binaryTranslator){
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
        int fullHeaderBytes = header.length >>> 3; //everything except last complete byte (if it exists)
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

            while(moltenData.size() >= fileWriteByteChunkSize){
                for(int i=0; i<fileWriteByteChunkSize; ++i){moltenByteChunk[i] = moltenData.remove().byteValue();}
                try{file.write(moltenByteChunk);}
                catch(Exception err){ err.printStackTrace();}
            }
        }

        //7. Finish writing final bytes to file
        //7.1 Write as many full chunks as possible
        while(bitAligner.size() >= 8){
            for(int i=0; i<8; ++i){
                currentByte[i] = bitAligner.remove();
            }
            moltenData.add((byte)BinaryTools.toUnsignedInt(currentByte));
        }

        while(moltenData.size() >= fileWriteByteChunkSize){
            for(int i=0; i<fileWriteByteChunkSize; ++i){moltenByteChunk[i] = moltenData.remove().byteValue();}
            try{file.write(moltenByteChunk);}
            catch(Exception err){ err.printStackTrace();}
        }
        
        //7.2 Write as many full bytes as possible (if any)
        if(moltenData.size()>0){
            moltenByteChunk = new byte[moltenData.size()];
            for(int i=0; moltenData.size()>0; ++i){
                moltenByteChunk[i] = moltenData.remove().byteValue();
            }
            try{file.write(moltenByteChunk);}
            catch(Exception err){ err.printStackTrace();}
        }

        //7.3 Write last incomplete byte to file with appropriate left shift (extra bits will be)
        if(bitAligner.size() > 0){
            boolean[] lastByte = new boolean[8];
            for(int i=0; bitAligner.size()>0; ++i){
                lastByte[i]=bitAligner.remove().booleanValue();
            }
            try{file.write((byte)(BinaryTools.toUnsignedInt(lastByte)));}
            catch(Exception err){ err.printStackTrace();}
        }

        //8. Close file.
        try{file.close();}catch(Exception err){err.printStackTrace();}
    }

    //FileSmelterStateful Overrides
    @Override
    public void smeltToFile(Path destinationPathName){
        targetFile = destinationPathName;
        smelt();
    }

    //OriginalFileSmelter methods
    //TODO Need to validate input
    public void setTargetFile(String relativePathName){
        targetFile = Path.of(relativePathName);
    }

    //TODO Need to validate input
    public void setAbsoluteTargetFile(String absolutePathName){
        targetFile = Paths.get(absolutePathName);
    }

    //TODO Need to validate input
    public void setAbsoluteFromRelativeTargetFile(String relativePathName){
        targetFile = Path.of(relativePathName).toAbsolutePath();
    }

    //TODO Make this reusable
    private void writeDataToNewFile(Path file, Collection<boolean[]> data){

    }
}
