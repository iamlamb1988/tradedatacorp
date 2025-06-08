/**
 * @author Bruce Lamb
 * @since 6 JUN 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.smelter.lexical.binary.Original;
import tradedatacorp.smelter.lexical.binary.BinaryTools;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;
import java.time.Instant;
import java.time.Duration;

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
    public void addData(StickDouble dataStick){
        synchronized(crucible){crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));}
    }

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
    public void smelt(){writeDataToNewFile(targetFile,crucible);}

    //FileSmelterStateful Overrides
    public void smeltToFile(Path destinationPathName){writeDataToNewFile(destinationPathName,crucible);}

    //OriginalFileSmelter methods
    public void setTargetFile(String relativePathName){targetFile = Path.of(relativePathName);}
    public void setAbsoluteTargetFile(String absolutePathName){targetFile = Paths.get(absolutePathName);}

    //TODO
    //Will write all the data points so a specified file.
    public void writeDataToNewFile(Path file, ArrayDeque<boolean[]> dataQueue){
        //1. Initialize variables
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
        byte[] moltenByteChunk = new byte[fileWriteByteChunkSize]; //Chunk to be actively written when full.

        CrucibleToHotCrucible worker1;
        HotCrucibleToBitAligner worker2;
        BitAlignerToMoltenData worker3;
        MoltenDataToFile worker4;

        synchronized(binaryTranslator){
            //2. Set boolean[] header based on binaryLexical settings and localCrubible size.
            synchronized(dataQueue){
                hotCrucible = new ArrayDeque<boolean[]>(dataQueue.size());
                binaryTranslator.setDataCount(dataQueue.size());
                header = binaryTranslator.getBinaryHeaderFlat();
                moltenData = new ArrayDeque<Byte>(((dataQueue.size() + 1) >>> 3) + ((header.length + 1) >>> 3));
            }
        }

        //3. Add full bytes of header to molten data.
        int fullHeaderBytes = header.length >>> 3; //everything except last complete byte (if it exists)
        for(int i=0; i<fullHeaderBytes; ++i){
            moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedIntFromBoolSubset(header,i << 3,8)));
        }

        //4. If not memory alligned, add last part of header. (Should skip loop if perfectly aligned by 8 bits)
        for(int i=fullHeaderBytes << 3; i<header.length; ++i){
            bitAligner.add(Boolean.valueOf(header[i]));
        }

        //5. Start the data point assembly line
        worker1 = new CrucibleToHotCrucible(hotCrucible);
        worker2 = new HotCrucibleToBitAligner(worker1, hotCrucible, bitAligner);
        worker3 = new BitAlignerToMoltenData(worker2, bitAligner, moltenData);
        worker4 = new MoltenDataToFile(worker3, moltenData, resultFile, fileWriteByteChunkSize,500);

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
        }catch(Exception err){}
    }

    //Assembly line interfaces (5 resources, 4 threads) AKA Producer -> Consumer line
    //This class is dedicated to functions writeDataToNewFile() for threaded work.
    private abstract class TempAssemblyWorker{
        protected volatile boolean isFinished;
        private TempAssemblyWorker(){isFinished=false;}
    }

    //TODO
    //These are tightly coupled classes but that's ok, they will ONLY be used for file writeDataToNewFile() method.
    //Resource locks: this.crucible, local hotCrucible
    private class CrucibleToHotCrucible extends TempAssemblyWorker implements Runnable{
        private ArrayDeque<boolean[]> hotCrucible;
        private CrucibleToHotCrucible(ArrayDeque<boolean[]> product){
            super();
            hotCrucible = product;
        }

        @Override
        public void run(){
            //simply move ALL elements from global Crucible to HotCrucible. should be a VERY easy task (right?)
            synchronized (crucible){
                while(!crucible.isEmpty()){synchronized(hotCrucible){hotCrucible.add(crucible.remove());}}
            }
            isFinished=true;
        }
    }

    //TODO
    //Resource locks: local hotCrucible, local bitAligner
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

            synchronized(hotCrucible){isHotCrucibleEmpty = hotCrucible.isEmpty();}

            while(!producer.isFinished || !isHotCrucibleEmpty){
                synchronized(hotCrucible){ currentDataStick = hotCrucible.remove();}
                for(int i=0; i<currentDataStick.length; ++i){
                    synchronized (bitAligner){bitAligner.add(Boolean.valueOf(currentDataStick[i]));}
                }
                synchronized(hotCrucible){isHotCrucibleEmpty = hotCrucible.isEmpty();}
            }
            isFinished=true;
        }
    }

    //Resource locks: local bitAligner, local moltenData
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

            synchronized(bitAligner){doesBitAlignerHave8Bits = bitAligner.size() >= 8;}

            //Full 8-bit character case
            while(!producer.isFinished){
                if(doesBitAlignerHave8Bits){
                    for(int i=0; i<8; ++i){currentByte[i] = bitAligner.remove().booleanValue();}
                    synchronized(moltenData){moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedInt(currentByte)));}
                }
                synchronized(bitAligner){doesBitAlignerHave8Bits = bitAligner.size() >= 8;}
            }

            //No longer need to sync bitAligner, this is the only thread that will use it at this point.
            if(!bitAligner.isEmpty()){
                //Fill in last full bytes.
                while(bitAligner.size()>=8){
                    for(int i=0; i<8; ++i){currentByte[i]=bitAligner.remove().booleanValue();}
                    synchronized(moltenData){moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedInt(currentByte)));}
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

                    synchronized(moltenData){moltenData.add(Byte.valueOf((byte)BinaryTools.toUnsignedInt(currentByte)));}
                }
            }

            isFinished = true;
        }
    }

    //Resource locks: local moltenData, local resultFile
    private class MoltenDataToFile extends TempAssemblyWorker implements Runnable{
        private BitAlignerToMoltenData producer;
        private ArrayDeque<Byte> moltenData;
        private FileOutputStream resultFile;
        private byte[] moltenByteChunk;
        private int nextChunkIndex;
        private final int BYTE_CHUNK_SIZE;
        private final int MAX_WAIT_TIME_MS;

        private MoltenDataToFile(BitAlignerToMoltenData producer, ArrayDeque<Byte> productSource, FileOutputStream resultFile, int byteChunkSize, int maxWaitTimeMilliseconds){
            super();
            this.producer = producer;
            moltenData = productSource;
            this.resultFile = resultFile;
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
            boolean isShort = true; //Is the current state of moltenByteChunk incomplete?

            while(!producer.isFinished){
                //1. Add bytes to the moltenByteChunk
                if(nextChunkIndex < BYTE_CHUNK_SIZE){
                    while(nextChunkIndex < BYTE_CHUNK_SIZE){
                        synchronized(moltenData){
                            if(moltenData.isEmpty() && nextChunkIndex > 0) {
                                isShort = true;
                                break;
                            }
                            moltenByteChunk[nextChunkIndex] = moltenData.remove().byteValue();
                            ++nextChunkIndex;
                        }
                    }

                    if(isShort && Duration.between(timeStart,Instant.now()).toMillis() >= MAX_WAIT_TIME_MS){
                        shortByteChunk = new byte[nextChunkIndex];
                        for(int i=0; i<nextChunkIndex; ++i){
                            shortByteChunk[i] = moltenByteChunk[i];
                        }
                        try{resultFile.write(shortByteChunk);}
                        catch(Exception err){err.printStackTrace();}
                        timeStart = Instant.now();
                        isShort = false;
                        nextChunkIndex = 0;
                    }
                }else{
                    try{resultFile.write(moltenByteChunk);}
                    catch(Exception err){err.printStackTrace();}
                    timeStart = Instant.now();
                    isShort = false;
                    nextChunkIndex = 0;
                }
            }

            //Handle last bytes. At this point, this is the last working thread alive, no synchronizing required anymore.
            //If partially filled then write partial bytes
            if(nextChunkIndex > 0){
                try{resultFile.write(moltenByteChunk,0,nextChunkIndex);}
                catch(Exception err){err.printStackTrace();}
            }

            //Write as many full remaining byte chunks as possible
            while(moltenData.size() >= BYTE_CHUNK_SIZE){
                for(int i=0; i<BYTE_CHUNK_SIZE; ++i){moltenByteChunk[i] = moltenData.remove().byteValue();}
                try{resultFile.write(moltenByteChunk);}
                catch(Exception err){err.printStackTrace();}
            }

            //Write last partial byte chunk (if it exists)
            shortByteChunk = new byte[moltenData.size()];
            nextChunkIndex=0;
            while(!moltenData.isEmpty()){
                shortByteChunk[nextChunkIndex] = moltenData.remove().byteValue();
                ++nextChunkIndex;
            }
            try{
                resultFile.write(shortByteChunk);
                resultFile.close();
            }
            catch(Exception err){err.printStackTrace();}
            isFinished = true;
        }
    }
}