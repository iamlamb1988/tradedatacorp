/**
 * @author Bruce Lamb
 * @since 07 SEP 2025
 */
package tradedatacorp.warehouse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;

import tradedatacorp.smelter.lexical.binary.OHLCV_BinaryLexical;

/**
 * A warehouse implementation for storing OHLCV candlestick data in a filesystem.
 * This is a machine that is intended to constantly run but can be turned on or off without corruption.
 * This instance will make use of {@link OHLCV_BinaryLexical} to super compress files. Will be able to quickly fetch data from
 * files in it's compressed form utilizing unique super compressed format.
 * Elaboration coming soon upon completion of JUnit tests.
 */
public class OHLCV_BinaryWarehouse implements
    WarehouseInitializer<String, String[]>,
    Warehouse<String, Path>
{
    private File rootDataDir;

    // WarehouseInitializer<String, String[]> Overrides
    @Override
    public String initialize(String[] initArgs){
        StringBuilder strBldr = new StringBuilder();
        String filePathName = initArgs[0];
        String connectResult;

        //Create directory structure if it does not exist
        Path rootDataDirectory = Path.of(filePathName);
        if(Files.isDirectory(rootDataDirectory)){
            System.out.println("DEBUG: directory exists.");
            strBldr.append("found existing directory "+filePathName);
        }else if(Files.exists(rootDataDirectory)){
            System.out.println("DEBUG: Not a directory... skipping.");
            return "INITIALIZE FAILED: "+filePathName+" is a file.\n";
        }
        else{
            try{
                System.out.println("DEBUG: Creating Directory...");
                Files.createDirectory(rootDataDirectory);
                strBldr.append("created directory "+filePathName);
            }catch(Exception err){}
        }

        //create finalized consoldiated directory
        connectResult = connect(rootDataDirectory);
        if(connectResult.startsWith("CONNECT SUCCESS:")){
            return "INITIALIZE SUCCESS: Successfully "+strBldr.toString()+" and connected.\n";
        }else{
            return "INITIALIZE FAILED: Successfully "+strBldr.toString()+" but failed to connected.\n";
        }

        //create ingestion directory
        //create validated directory
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
}