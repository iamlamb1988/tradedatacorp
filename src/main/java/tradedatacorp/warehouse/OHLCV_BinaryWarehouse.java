/**
 * @author Bruce Lamb
 * @since 20 OCT 2025
 */
package tradedatacorp.warehouse;

import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;
import tradedatacorp.tools.stick.primitive.CandleStickFixedDouble;
import tradedatacorp.tools.stick.primitive.StickDouble;
import tradedatacorp.tools.stick.info.StickHeader;
import tradedatacorp.tools.stick.info.StickTimeFrame;
import tradedatacorp.tools.time.TimeTier;

import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * A warehouse implementation for storing OHLCV candlestick data in a filesystem.
 * This is a machine that is intended to constantly run but can be turned on or off without corruption.
 * This instance will make use of {@link OHLCV_BinaryLexical} to super compress files. Will be able to quickly fetch data from
 * files in it's compressed form utilizing unique super compressed format.
 * Elaboration coming soon upon completion of JUnit tests.
 */
public class OHLCV_BinaryWarehouse implements
    WarehouseInitializer<String, String[]>,
    WarehousePowerable<String, String[]>,
    WarehouseDataStorage<String, Path>,
    WarehouseStorer<StickDouble, Boolean>,
    WarehousePicker<StickDouble>
{
    private File rootDataDir;
    private boolean isPoweredOn;
    private HashSet<InformationStick> uncheckedIngest;

    private Thread t1Categorizer;

    public OHLCV_BinaryWarehouse(){
        isPoweredOn = false;
        uncheckedIngest = new HashSet<InformationStick>();
    }

    // WarehouseInitializer<String, String[]> Overrides
    @Override
    public String initialize(String[] initArgs){
        StringBuilder strBldr = new StringBuilder();
        String filePathName = initArgs[0];
        String connectResult;
        String returnMessage;

        //Create directory structure if it does not exist
        Path rootDataDirectory = Path.of(filePathName);
        if(Files.isDirectory(rootDataDirectory)){
            strBldr.append("found existing directory "+filePathName); //continuation sentence
        }else if(Files.exists(rootDataDirectory)){
            return "INITIALIZE FAILED: "+filePathName+" is a file.\n";
        }
        else{
            try{
                Files.createDirectory(rootDataDirectory);
                strBldr.append("created directory "+filePathName); //continuation sentence
            }catch(Exception err){}
        }

        //create finalized consoldiated directory
        connectResult = connect(rootDataDirectory);
        if(connectResult.startsWith("CONNECT SUCCESS:")){
            returnMessage = "Successfully "+strBldr.toString()+" and connected.\n";
            strBldr.setLength(0);
            strBldr.append(returnMessage);
        }else{
            return "INITIALIZE FAILED: Successfully "+strBldr.toString()+" but failed to connected.\n";
        }

        //create ingestion directory
        Path ingestSubdir = rootDataDirectory.resolve("ingest");
        if(Files.isDirectory(ingestSubdir)){
            strBldr.append(ingestSubdir.toString()+" successfully found.\n");
            return "INITIALIZE SUCCESS: "+strBldr.toString();
        }else if(Files.exists(ingestSubdir)){
            strBldr.append("ingest is a file and cannot create directory.\n");
            return "INITIALIZE FAILED: "+strBldr.toString();
        }else{
            try{
                Files.createDirectory(ingestSubdir);
                strBldr.append(ingestSubdir.toString()+" successfully created.\n");
                return "INITIALIZE SUCCESS: "+strBldr.toString();
            }catch(Exception err){}
            return "INITIALIZE FAILED: ???";
        }
        //TODO create validated directory.
        //IF data exists MUST validate structure and naming
    }

    //Warehouse<String, String> Overrides
    /**
     * Establishes a directory on filesystem for data.
     *
     * @return the status of current connection.
     */
    @Override
    public String connect(Path credentials){
        if(credentials != null){
            File f = credentials.toFile();
            if(!f.exists()) return "FAILURE: "+f.toString()+" does not exit.";
            if(!f.isDirectory()) return "FAILURE: "+f.toString()+" not a directory.";
            rootDataDir = f;
            return "CONNECT SUCCESS: "+rootDataDir.toString();
        }
        return "CONNECT FAILURE";
    }

    /**
     * Returns the current status of the connection.
     *
     * @return the status of current connection.
     * CONNECTED -> If {@code rootDataDir} is connected established
     */
    @Override
    public String connectionStatus(){return null;}

    // WarehousePowerable<String, String[]> Overrides
    /**
     * Powers warehouse operations on or off IAW configuration passed in.
     *
     * @params configurations
     * To power all functions on properly, {@code configurations[0] = "ALL_ON"}.
     * To power all functions off properly, {@code configurations[0] = "ALL_OFF"}.
     * More options to come for targeted functionality.
     */
    @Override
    public String powerSwitch(String... configurations){
        if(configurations == null) return null;
        if(configurations.length == 0 || configurations[0].equals("ALL_ON")){
            isPoweredOn = true;
            t1Categorizer = new Thread(new T1_CategorizeUncheckedData(), "Categorizer");
            t1Categorizer.start();
            return "DEBUG TODO POWERON RESULT"; //TODO: Fix the output
        }else if(configurations[0].equals("ALL_OFF")){
            isPoweredOn = false;
            return "DEBUG TODO POWEROFF RESULT"; //TODO: Fix the output
        }

        //TODO: special cases here
        return "DEBUG TODO SPECIAL POWERING RESULT"; //TODO: Fix the output
    }

    // WarehouseStorer<StickDouble, Boolean> Overrides
    @Override
    public Boolean storeOne(StickDouble candidate){
        if(candidate instanceof StickHeader && candidate instanceof StickTimeFrame){
            synchronized(uncheckedIngest){
                if(candidate instanceof CandleStickFixedDouble){
                    uncheckedIngest.add(
                        new InformationWrapperStick(
                            (CandleStickFixedDouble)candidate,
                            ((StickHeader)candidate).getName(),
                            ((StickTimeFrame)candidate).getInterval()
                        )
                    );
                }else{
                    uncheckedIngest.add(
                        new InformationDirectStick(
                            ((StickHeader)candidate).getName(),
                            ((StickTimeFrame)candidate).getInterval(),
                            candidate.getUTC(),
                            candidate.getO(),
                            candidate.getH(),
                            candidate.getL(),
                            candidate.getC(),
                            candidate.getV()
                        )
                    );
                }
            }
            return Boolean.valueOf(true);
        }
        return Boolean.valueOf(false);
    }

    @Override
    public Boolean store(StickDouble[] candidateArray){
        Tu_Input_SaveDataArray tu = new Tu_Input_SaveDataArray(candidateArray);
        new Thread(tu).start();
        return Boolean.valueOf(tu.isAllTrue);
    }

    public Boolean store(Collection<StickDouble> validDataCollection){return null;}

    //WarehousePicker<StickDouble> Overrides
    public Collection<StickDouble> pickToCollection(String TickerSymbol, long UTC_Start, long UTC_End){return null;}
    public StickDouble[] pickToArray(String TickerSymbol, long UTC_Start, long UTC_End){return null;}

    //OHLCV_BinaryWarehouse methods

    //OHLCV_BinaryWarehouse private classes
    //Marker Interface to ensure header and timeframe exist
    private interface InformationStick extends StickDouble, StickHeader, StickTimeFrame{}

    private class InformationDirectStick extends CandleStickFixedDouble implements InformationStick{
        String symbol;
        int interval;

        public InformationDirectStick(
            String symbolName,
            int intervalSeconds,
            long utc_timestamp,
            double open,
            double high,
            double low,
            double close,
            double volume
        ){
            super(utc_timestamp, open, high, low, close, volume);
            symbol = symbolName;
            interval = intervalSeconds;
        }

        public InformationDirectStick(
            StickDouble stick,
            String symbolName,
            int intervalSeconds
        ){
            this(
                symbolName,
                intervalSeconds,
                stick.getUTC(), stick.getO(), stick.getH(), stick.getL(), stick.getC(), stick.getV()
            );
        }

        // StickHeader Overrides
        @Override
        public String getName(){return symbol;}

        @Override
        public String getSymbol(){return symbol;}

        //StickTimeFrame Overrides
        @Override
        public int getInterval(){return interval;}
    }

    private class InformationWrapperStick implements InformationStick{
        CandleStickFixedDouble wrapperStickRef;
        String symbol;
        int interval;
        InformationWrapperStick(CandleStickFixedDouble stick, String symbolName, int intervalSeconds){
            wrapperStickRef = stick;
            symbol = symbolName;
            interval = intervalSeconds;
        }

        //StickDouble Overrides
        @Override
        public long getUTC(){return wrapperStickRef.UTC;}

        @Override
        public double getO(){return wrapperStickRef.O;}

        @Override
        public double getH(){return wrapperStickRef.H;}

        @Override
        public double getL(){return wrapperStickRef.L;}

        @Override
        public double getC(){return wrapperStickRef.C;}

        @Override
        public double getV(){return wrapperStickRef.V;}

        @Override
        public int compareTo(StickDouble otherStick){return Long.compare(wrapperStickRef.UTC, otherStick.getUTC());}

        // StickHeader Overrides
        @Override
        public String getName(){return symbol;}

        @Override
        public String getSymbol(){return symbol;}

        //StickTimeFrame Overrides
        @Override
        public int getInterval(){return interval;}
    }

    private class CheckedCacheStick extends CandleStickFixedDouble{
        HashSet<CacheReason> reasonList;
        public CheckedCacheStick(
            long utc_timestamp,
            double open,
            double high,
            double low,
            double close,
            double volume
        ){
            super(utc_timestamp, open, high, low, close, volume);
            reasonList = new HashSet<CacheReason>();
        }
    }
    private class SymbolIntervalTracker{
        String symbolName;
        final int INTERVAL = -1; //TODO
        TimeTier[] fileFunnel;
        ArrayList<CheckedCacheStick> localCache;
        Path sourceFile;
    }
    private class TimeTierMeta{}
    private abstract class CacheReason{
        boolean isValid;
    }
    private class FileWriteReason extends CacheReason{}
    private class TimedReason extends CacheReason{}

    //Special Threads (See Resource Allocation Graph for details)
    private abstract class TempAssemblyWorker{
        protected volatile boolean isFinished;
        private TempAssemblyWorker(){isFinished=false;}
    }

    /**
     * Threaded User input. Each batch of user data will be on it's own thread.
     * This is the entrypoint of new data
     */
    private class Tu_Input_SaveDataCollection extends TempAssemblyWorker implements Runnable{
        Iterator<StickDouble> it;
        boolean isAllTrue;

        Tu_Input_SaveDataCollection(Collection<StickDouble> newDataCollection){
            it = newDataCollection.iterator();
            isAllTrue = true;
        }

        @Override
        public void run(){
            while(it.hasNext()){
                if(!storeOne(it.next())) isAllTrue = false;
                it.remove();
            }
            isFinished = true;
        }
    }

    //from R(u, ?) -> R(1)
    //R(u, ?) is the user input primitive array.
    //R(1) is the collection of immutable data points.
    //This thread is repsonsible for converting any OHLCV data into an immutable header object
    private class Tu_Input_SaveDataArray extends TempAssemblyWorker implements Runnable{
        StickDouble userInputArray[];
        boolean isAllTrue;

        Tu_Input_SaveDataArray(StickDouble[] newUserInputArray){
            userInputArray = newUserInputArray;
            isAllTrue = true;
        }

        @Override
        public void run(){
            for(StickDouble candidateStick : userInputArray){
                if(!storeOne(candidateStick)) isAllTrue = false;
            }
        }
    }

    //The only top level thread, may be a bottle neck.
    private class T1_CategorizeUncheckedData extends TempAssemblyWorker implements Runnable{
        @Override
        public void run(){
            System.out.println("Thread 1: Powered on. Begin listening for unchecked data to appropriate vetting collection.");
            //WHILE: power is on
            //   WHILE: unchecked collection had data
            //      1. Remove data element
            //      2. Check symbol, interval
            //      3. Move to Sorted Entry ArrayList for full vetting.

            isFinished = true;
        }
    }
}
