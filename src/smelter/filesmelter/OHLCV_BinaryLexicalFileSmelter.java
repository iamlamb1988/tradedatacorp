/**
 * @author Bruce Lamb
 * @since 01 JUL 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.tools.binarytools.BinaryTools;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayDeque;
import java.time.Instant;
import java.time.Duration;

/**
 * The OHLCV_BinaryLexicalFileSmelter class writes {@link StickDouble} objects to a compressed binary file formatted for use with an {@link OHLCV_BinaryLexical} translator.
 * This class has a preset Path that represents the file location and name of where the file will be written.
 * This class has a {@link OHLCV_BinaryLexical} that specifies the bit compression formats of OHLCV Stick data.
 */
 public class OHLCV_BinaryLexicalFileSmelter implements FileSmelterStateful<StickDouble>{
    private OHLCV_BinaryLexical binaryTranslator; //Translates from ? to flattened bin (type boolean[])
    private Path targetFile;
    private ArrayDeque<boolean[]> crucible;
    private int fileWriteByteChunkSize = 64;

    //Constructor
    /**
     * Constructs an {@code OHLCV_BinaryLexicalFileSmelter} using the provided {@link OHLCV_BinaryLexical} translator.
     * This will not use this classes {@link OHLCV_BinaryLexical}.
     * 
     * @param originalTranslator The binary lexical translator used to define the bit compression format for output files.
     */
    public OHLCV_BinaryLexicalFileSmelter(OHLCV_BinaryLexical originalTranslator){
        binaryTranslator = originalTranslator.clone();
        targetFile = null;
        crucible = new ArrayDeque<boolean[]>();
    }

    //Smelter Overrides
    /**
     * Writes a file to the preset target path using the preset {@link OHLCV_BinaryLexical}, containing exactly one data point represented by the given {@code dataStick}.
     * Note: Data loss may occur if this class's lexical bitfields are too small to represent the data.
     * 
     * @param dataStick The {@link StickDouble} instance to serialize and write to the file.
     */
    @Override
    public void smelt(StickDouble dataStick){
        ArrayDeque<boolean[]> rawDataQueue;
        synchronized(dataStick){
            rawDataQueue = new ArrayDeque<>(1);
            rawDataQueue.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
        writeDataToNewFile(targetFile, rawDataQueue, true);
    }

    /**
     * Writes a file to the preset target path using the preset {@link OHLCV_BinaryLexical}, 
     * containing data points represented by the provided array of {@link StickDouble} instances.
     * Note: Data loss may occur if this class's lexical bitfields are too small to represent the data points.
     *
     * @param rawDataArray the array of {@link StickDouble} instances to serialize and write to the file.
     */
    @Override
    public void smelt(StickDouble[] rawDataArray){
        synchronized(rawDataArray){
            ArrayDeque<boolean[]> hotCrucible = new ArrayDeque<boolean[]>(rawDataArray.length);
            for(int i=0; i<rawDataArray.length; ++i){
                hotCrucible.add(binaryTranslator.getBinaryDataFlat(rawDataArray[i]));
            }
            writeDataToNewFile(targetFile, hotCrucible, true);
        }
    }

    /**
     * Writes a file to the preset target path using the preset {@link OHLCV_BinaryLexical}, 
     * containing data points represented by the provided collection of {@link StickDouble} instances.
     * Note: Data loss may occur if this class's lexical bitfields are too small to represent the data points.
     *
     * @param rawDataCollection the collection of {@link StickDouble} instances to serialize and write to the file.
     */
    @Override
    public void smelt(Collection<StickDouble> rawDataCollection){
        boolean isQueue;
        synchronized(rawDataCollection){isQueue = (rawDataCollection instanceof ArrayDeque<StickDouble>);}
        if(isQueue) smeltQueueToFile(targetFile,(ArrayDeque<StickDouble>)rawDataCollection);

        ArrayDeque<boolean[]> hotCrucible;
        synchronized(rawDataCollection){
            hotCrucible = new ArrayDeque<boolean[]>(rawDataCollection.size());
            Iterator<StickDouble> it = rawDataCollection.iterator();
            StickDouble next;
            while(it.hasNext()){
                next = it.next();
                hotCrucible.add(binaryTranslator.getBinaryDataFlat(next));
            }
        }
        writeDataToNewFile(targetFile,hotCrucible,true);
    }

    /**
     * Processes a single data element.
     *
     * @param rawDataElement the data element to process
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
     * @param rawDataArray the array of data elements to process
     */
    public String smeltToString(StickDouble[] rawDataArray){
        synchronized(rawDataArray){
            ArrayDeque<boolean[]> hotCrucible = new ArrayDeque<boolean[]>(rawDataArray.length);
            for(int i=0; i<rawDataArray.length; ++i){
                hotCrucible.add(binaryTranslator.getBinaryDataFlat(rawDataArray[i]));
            }
            return writeDataToNewFile(targetFile, hotCrucible, false);
        }
    }

    /**
     * Processes a collection of data elements.
     *
     * @param rawDataCollection the collection of data elements to process
     */
    public String smeltToString(Collection<StickDouble> rawDataCollection){
        boolean isQueue;
        synchronized(rawDataCollection){isQueue = (rawDataCollection instanceof ArrayDeque<StickDouble>);}
        if(isQueue) smeltQueueToFile(targetFile,(ArrayDeque<StickDouble>)rawDataCollection);

        ArrayDeque<boolean[]> hotCrucible;
        synchronized(rawDataCollection){
            hotCrucible = new ArrayDeque<boolean[]>(rawDataCollection.size());
            Iterator<StickDouble> it = rawDataCollection.iterator();
            StickDouble next;
            while(it.hasNext()){
                next = it.next();
                hotCrucible.add(binaryTranslator.getBinaryDataFlat(next));
            }
        }
        return writeDataToNewFile(targetFile,hotCrucible,false);
    }

    //SmelterStateful Overrides
    /**
     * Adds a single {@link StickDouble} instance to this class's crucible for later processing.
     *
     * @param dataStick the {@link StickDouble} element to add to the crucible.
     */
    @Override
    public void addData(StickDouble dataStick){
        synchronized(crucible){crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));}
    }

    /**
     * Adds an array of {@link StickDouble} instances to this class's crucible for later processing.
     *
     * @param dataStickArray the {@link StickDouble} array of elements to add to the crucible.
     */
    @Override
    public void addData(StickDouble[] dataStickArray){
        for(StickDouble dataStick : dataStickArray){
            crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
    }

    /**
     * Adds a collection of {@link StickDouble} instances to this class's crucible for later processing.
     *
     * @param dataStickCollection the {@link StickDouble} collection of elements to add to the crucible.
     */
    @Override
    public void addData(Collection<StickDouble> dataStickCollection){
        for(StickDouble dataStick : dataStickCollection){
            crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));
        }
    }

    /**
     * Writes all data currently stored in this class's crucible to the preset target file.
     * All data will be removed from the crucible upon completion.
     */
    @Override
    public void smelt(){writeDataToNewFile(targetFile,crucible,true);}

    //FileSmelterStateful Overrides
    /**
     * Writes all data currently stored in this class's crucible to the specified target file.
     * All data will be removed from the crucible upon completion.
     */
    @Override
    public void smeltToFile(Path destinationPathName){writeDataToNewFile(destinationPathName,crucible,true);}

    //OHLCV_BinaryLexicalFileSmelter methods
    /**
     * Returns string of relative file path
     * @return String of relative file path
     */
    public String getTargetStringFile(){return targetFile.toString();}

    /**
     * Sets the preset target file path for future file write operations.
     *
     * @param path the Path file location.
     */
    public void setTargetFile(Path path){targetFile = path;}

    /**
     * Sets the preset target file path for future file write operations.
     *
     * @param relativePathName the relative path name to use as the target file location.
     */
    public void setTargetFile(String relativePathName){targetFile = Path.of(relativePathName);}

     /**
     * Sets the preset target file path for future file write operations to the absolute path.
     *
     * @param absolutePathName the absolute path name to use as the target file location.
     */
    public void setAbsoluteTargetFile(String absolutePathName){targetFile = Paths.get(absolutePathName);}

    /**
     * Processes all {@link StickDouble} data sticks contained in the provided queue and writes them to a file at the specified destination path.
     * Each data stick is converted to a binary representation using the preset {@link OHLCV_BinaryLexical}, then written sequentially to the output file.
     *
     * @param destinationPathName the path and filename for the output file.
     * @param stickQueue the queue of {@link StickDouble} instances to process and write.
     */
    public void smeltQueueToFile(Path destinationPathName, ArrayDeque<StickDouble> stickQueue){
        ArrayDeque<boolean[]> hotCrucible;
        synchronized(stickQueue){
            hotCrucible = new ArrayDeque<boolean[]>(stickQueue.size());
            while(!stickQueue.isEmpty()){
                synchronized(binaryTranslator){
                    hotCrucible.add(binaryTranslator.getBinaryDataFlat(stickQueue.remove()));
                }
            }   
        }
        writeDataToNewFile(destinationPathName, hotCrucible, true);
    }

    /**
     * Writes the contents of a queue of binary data to a file at the specified path.
     * <p>
     * This method performs a multi-stage, threaded transformation and serialization pipeline:
     * <ul>
     *   <li>Prepares a binary header according to the {@link binaryTranslator}'s settings.</li>
     *   <li>Converts and aligns data bits from the input queue for file output.</li>
     *   <li>Spawns four worker threads to process and write the data in parallel, maximizing throughput.</li>
     *   <li>The output file is always overwritten, never appended.</li>
     * </ul>
     * <p>
     * The method is **thread-safe** with respect to the provided {@code dataQueue} and uses synchronization to protect shared resources.
     * </p>
     * <p>
     * <b>Side Effects:</b> The provided {@code dataQueue} is not modified by this method. However, the output file at {@code file} will be overwritten.
     * </p>
     * <p>
     * <b>Exceptions:</b> Any I/O or thread interruption exceptions are caught and printed to standard error; the method will return early on file open failure.
     * </p>
     *
     * @param file the target file {@link Path} where binary data will be written; the file will be created or overwritten.
     * @param dataQueue a queue of boolean arrays, each representing a single data record to serialize and write.
     */
    public String writeDataToNewFile(Path file, ArrayDeque<boolean[]> dataQueue, boolean toFile){
        //1. Initialize variables
        DataWriter returnData;
        if(toFile) returnData = new FileWriter(file);
        else returnData = new StringWriter();

        //1.2 Initialize working variables
        ArrayDeque<boolean[]> hotCrucible;
        ArrayDeque<Boolean> bitAligner = new ArrayDeque<Boolean>(); //Used to store partial bits for alignment.
        ArrayDeque<Byte> moltenData; //bytes ready to be written
        boolean[] header;
        byte[] moltenByteChunk = new byte[fileWriteByteChunkSize]; //Chunk to be actively written when full.

        synchronized(binaryTranslator){
            //2. Set boolean[] header based on binaryLexical settings and localCrubible size.
            synchronized(dataQueue){
                hotCrucible = new ArrayDeque<boolean[]>(dataQueue.size());
                binaryTranslator.setDataCount(dataQueue.size());
                header = binaryTranslator.getBinaryHeaderFlat();
                moltenData = new ArrayDeque<Byte>(((dataQueue.size() + header.length + 1) >>> 3));
            }
        }

        //3. Add full bytes of header to molten data.
        int fullHeaderBytes = header.length >>> 3; //everything except last complete byte (if it exists)
        for(int i=0; i<fullHeaderBytes; ++i){moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedIntFromBoolSubset(header,i << 3,8)));}

        //4. If not memory alligned, add last part of header. (Should skip loop if perfectly aligned by 8 bits)
        for(int i=fullHeaderBytes << 3; i<header.length; ++i){bitAligner.add(Boolean.valueOf(header[i]));}

        //5. Start the data point assembly line
        CrucibleToHotCrucible worker1 = new CrucibleToHotCrucible(hotCrucible);
        HotCrucibleToBitAligner worker2 = new HotCrucibleToBitAligner(worker1, hotCrucible, bitAligner);
        BitAlignerToMoltenData worker3 = new BitAlignerToMoltenData(worker2, bitAligner, moltenData);
        MoltenDataToFile worker4 = new MoltenDataToFile(worker3, moltenData, returnData, fileWriteByteChunkSize,500);

        Thread t1 = new Thread(worker1, "worker1");
        Thread t2 = new Thread(worker2, "worker2");
        Thread t3 = new Thread(worker3, "worker3");
        Thread t4 = new Thread(worker4, "worker4");

        t1.start();
        t2.start();
        t3.start();
        t4.start();

        try{
            t1.join();
            t2.join();
            t3.join();
            t4.join();
        }catch(Exception err){err.printStackTrace();}
        while(!worker4.isFinished){
            synchronized(returnData){
                try{returnData.wait();}
                catch(Exception err){err.printStackTrace();}
            }
        }
        return returnData.finalizeData();
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

    /**
     * Abstract base class for worker threads used in the {@link #writeDataToNewFile} file writing pipeline.
     * This class is part of a producer-consumer assembly line, coordinating 4 threads across 5 resources
     * to process and write data in parallel.
     * The {@code isFinished} flag is used to signal when a worker has completed its task.
     *
     * @see #writeDataToNewFile(Path, ArrayDeque, true)
     */
    private abstract class TempAssemblyWorker{
        protected volatile boolean isFinished;
        private TempAssemblyWorker(){isFinished=false;}
    }

    /**
     * Responsible for moving data from the core {@code crucible} object to a local {@code hotCrucible} queue.
     * This operation is performed with synchronization to ensure thread safety, allowing subsequent pipeline stages
     * to operate on a thread-local copy of the data.
     * Resource locks: {@code this.crucible}, local {@code hotCrucible}
     * Used internally as the first stage of the file writing pipeline.
     */
    private class CrucibleToHotCrucible extends TempAssemblyWorker implements Runnable{
        private ArrayDeque<boolean[]> hotCrucible;
        private CrucibleToHotCrucible(ArrayDeque<boolean[]> product){
            super();
            hotCrucible = product;
        }

        @Override
        public void run(){
            synchronized(crucible){
                while(!crucible.isEmpty()){
                    synchronized(hotCrucible){
                        hotCrucible.add(crucible.remove());
                        hotCrucible.notifyAll();
                    }
                }
            }
            isFinished=true;
            synchronized(hotCrucible){hotCrucible.notifyAll();}
        }
    }

    /**
     * Responsible for removing elements from the {@code hotCrucible} queue and converting them to their bit representation
     * before placing them into the {@code bitAligner} queue. Synchronization ensures thread safety during the transfer and conversion process.
     * Resource locks: local {@code hotCrucible}, local {@code bitAligner}.
     * Used internally as the second stage of the file writing pipeline.
     */
    private class HotCrucibleToBitAligner extends TempAssemblyWorker implements Runnable{
        private CrucibleToHotCrucible producer;
        private ArrayDeque<boolean[]> hotCrucible;
        private ArrayDeque<Boolean> bitAligner;
        private boolean isFinished;

        private HotCrucibleToBitAligner(CrucibleToHotCrucible producer, ArrayDeque<boolean[]> productSource, ArrayDeque<Boolean> productDestination){
            super();
            this.producer = producer;
            hotCrucible = productSource;
            bitAligner = productDestination;
        }

        @Override
        public void run(){
            boolean[] currentDataStick;
            boolean isHotCrucibleEmpty;

            while(!producer.isFinished){
                synchronized(hotCrucible){isHotCrucibleEmpty = hotCrucible.isEmpty();}
                if(isHotCrucibleEmpty) synchronized(hotCrucible){
                    try{hotCrucible.wait();}
                    catch(Exception err){err.printStackTrace();}
                }
                else{
                    synchronized(hotCrucible){currentDataStick = hotCrucible.remove();}
                    for(int i=0; i<currentDataStick.length; ++i){
                        synchronized(bitAligner){
                            bitAligner.add(Boolean.valueOf(currentDataStick[i]));
                            bitAligner.notifyAll();
                        }
                    }
                }
            }

            //No longer need to sync hotCrucible, this is the only thread that will use it at this point.
            while(!hotCrucible.isEmpty()){
                currentDataStick = hotCrucible.remove();
                for(int i=0; i<currentDataStick.length; ++i){
                    synchronized(bitAligner){
                        bitAligner.add(Boolean.valueOf(currentDataStick[i]));
                        bitAligner.notifyAll();
                    }
                }
            }
            isFinished=true;
            synchronized(bitAligner){bitAligner.notifyAll();}
        }
    }

    /**
     * Responsible for extracting 8 bits at a time from {@code bitAligner} and converting them into a single byte,
     * which is then placed into {@code moltenData} queue.
     * For the last byte, if fewer than 8 bits remain, the remaining bits are filled with 0s on the right to complete the byte,
     * ensuring proper byte alignment for file writing.
     * Resource locks: local {@code bitAligner}, local {@code moltenData}.
     * Used internally as the third stage of the file writing pipeline.
     */
    private class BitAlignerToMoltenData extends TempAssemblyWorker implements Runnable{
        private HotCrucibleToBitAligner producer;
        private ArrayDeque<Boolean> bitAligner;
        private ArrayDeque<Byte> moltenData;

        private boolean[] currentByte;

        private BitAlignerToMoltenData(HotCrucibleToBitAligner producer, ArrayDeque<Boolean> productSource, ArrayDeque<Byte> productDestination){
            super();
            this.producer = producer;
            bitAligner = productSource;
            moltenData = productDestination;
            currentByte = new boolean[8];
        }

        @Override
        public void run(){
            boolean doesBitAlignerHave8Bits; //At least 8 bits. This var prevents blocking BitAligner use.

            //Full 8-bit character case
            while(!producer.isFinished){
                synchronized(bitAligner){doesBitAlignerHave8Bits = bitAligner.size() >= 8;}
                if(!doesBitAlignerHave8Bits) synchronized(bitAligner){
                    try{bitAligner.wait();}
                    catch(Exception err){err.printStackTrace();}
                }else{
                    for(int i=0; i<8; ++i){
                        synchronized(bitAligner){
                            currentByte[i] = bitAligner.remove().booleanValue();
                            bitAligner.notifyAll();
                        }
                    }
                    synchronized(moltenData){
                        moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedInt(currentByte)));
                        moltenData.notifyAll();
                    }
                }
            }

            //No longer need to sync bitAligner, this is the only thread that will use it at this point.
            if(!bitAligner.isEmpty()){
                //Fill in last full bytes.
                while(bitAligner.size()>=8){
                    for(int i=0; i<8; ++i){currentByte[i]=bitAligner.remove().booleanValue();}
                    synchronized(moltenData){
                        moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedInt(currentByte)));
                        moltenData.notifyAll();
                    }
                }

                //Fill in last incomplete byte if any
                if(!bitAligner.isEmpty()){
                    int i=0;
                    while(bitAligner.size()>0){
                        currentByte[i] = bitAligner.remove().booleanValue();
                        ++i;
                    }

                    //Pad remaining 0s to the right
                    while(i<8){
                        currentByte[i] = false;
                        ++i;
                    }

                    synchronized(moltenData){ 
                        moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedInt(currentByte)));
                        moltenData.notifyAll();
                    }
                }
            }

            isFinished = true;
            synchronized(moltenData){moltenData.notifyAll();}
        }
    }

    /**
     * Responsible for writing bytes stored in {@code moltenData} to the {@code resultFile},
     * serving as the final stage of the file writing pipeline where data is written to disk.
     * Resource locks: local {@code moltenData}, local {@code resultFile}.
     * Used internally as the fourth stage of the file writing pipeline.
     */
    private class MoltenDataToFile extends TempAssemblyWorker implements Runnable{
        private BitAlignerToMoltenData producer;
        private ArrayDeque<Byte> moltenData;
        private DataWriter result;
        private byte[] moltenByteChunk;
        private int nextChunkIndex;
        private final int BYTE_CHUNK_SIZE;
        private final int MAX_WAIT_TIME_MS;

        private MoltenDataToFile(BitAlignerToMoltenData producer, ArrayDeque<Byte> productSource, DataWriter result, int byteChunkSize, int maxWaitTimeMilliseconds){
            super();
            this.producer = producer;
            moltenData = productSource;
            this.result = result;
            moltenByteChunk = new byte[byteChunkSize];
            nextChunkIndex = 0;
            BYTE_CHUNK_SIZE=byteChunkSize;
            MAX_WAIT_TIME_MS=maxWaitTimeMilliseconds;
        }

        // Potential timeout to write incomplete chunks? Nagels algorithm?
        @Override
        public void run(){
            Instant timeStart = Instant.now();
            byte[] shortByteChunk;
            boolean isMoltenDataEmpty = true;
            boolean isShort = true; //Is the current state of moltenByteChunk incomplete?
            int tmpMaxByteIndex;

            while(!producer.isFinished){
                synchronized(moltenData){isMoltenDataEmpty = moltenData.isEmpty();}
                if(isMoltenDataEmpty) synchronized(moltenData){
                    try{moltenData.wait();}
                    catch(Exception err){err.printStackTrace();}
                }
                else{
                    //1. Add bytes to the moltenByteChunk
                    synchronized(moltenData){tmpMaxByteIndex = (moltenData.size() < BYTE_CHUNK_SIZE ? moltenData.size() : BYTE_CHUNK_SIZE);}
                    for(int i=nextChunkIndex; i<tmpMaxByteIndex; ++i){
                        synchronized(moltenData){moltenByteChunk[nextChunkIndex] = moltenData.remove().byteValue();}
                        ++nextChunkIndex;
                    }

                    isShort = (nextChunkIndex != BYTE_CHUNK_SIZE);
                    if(isShort){
                        if(Duration.between(timeStart,Instant.now()).toMillis() >= MAX_WAIT_TIME_MS){
                            result.writeBytes(moltenByteChunk,0,nextChunkIndex);
                            timeStart = Instant.now();
                            nextChunkIndex=0;
                        }
                    }else{
                        result.writeBytes(moltenByteChunk);
                        timeStart = Instant.now();
                        nextChunkIndex=0;
                    }
                }
            }

            //Handle last bytes. At this point, this is the last working thread alive, no more synchronizing.
            //If partially filled then write partial bytes
            if(nextChunkIndex > 0){result.writeBytes(moltenByteChunk,0,nextChunkIndex);}

            //Write as many full remaining byte chunks as possible
            while(moltenData.size() >= BYTE_CHUNK_SIZE){
                for(int i=0; i<BYTE_CHUNK_SIZE; ++i){moltenByteChunk[i] = moltenData.remove().byteValue();}
                result.writeBytes(moltenByteChunk);
            }

            //Write last partial byte chunk (if it exists)
            shortByteChunk = new byte[moltenData.size()];
            nextChunkIndex=0;
            while(!moltenData.isEmpty()){
                shortByteChunk[nextChunkIndex] = moltenData.remove().byteValue();
                ++nextChunkIndex;
            }
            result.writeBytes(shortByteChunk);
            isFinished = true;
        }
    }
}