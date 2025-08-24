/**
 * @author Bruce Lamb
 * @since 22 JUN 2025
 */
package tradedatacorp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Paths;
import java.nio.file.Path;

/**
 * This class only used for fetching unit tests. Root of all files will be located in {@code tradedatacorp/test/resources}
 */
public class TestResourceFetcher{
    // public static final String RELATIVE_FULL_PATH = "../../resources/";
    private ClassLoader classLoader;
    public TestResourceFetcher(){
        classLoader = this.getClass().getClassLoader();
    }

    public Path getFilePath(String filePathName){
        // if(filePathName != null  && !filePathName.isEmpty()){
        //     char firstChar = filePathName.charAt(0);
        //     if(firstChar != '/') filePathName = RELATIVE_FULL_PATH + filePathName;
        // }
        try{
            URL resourceURL = classLoader.getResource(filePathName);
            if(resourceURL == null) throw new Exception("Resource not found: " + filePathName);
            return Paths.get(resourceURL.toURI());
        }catch (Exception err){
            err.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the file contents of a textfile within the classpath.
     * 
     * @param filePathName the pathName relative to {@code tradedatacorp/test/resources/} directory.
     * Returns the text string.
     */
    public String getTextFileContents(String filePathName){
        // if(filePathName != null  && !filePathName.isEmpty()){
        //     char firstChar = filePathName.charAt(0);
        //     if(firstChar != '/') filePathName = RELATIVE_FULL_PATH + filePathName;
        // }
        StringBuilder sb = new StringBuilder();
        try{
            InputStream is = classLoader.getResourceAsStream(filePathName);
            if(is == null) throw new Exception("File path: "+filePathName+" is null.");
            InputStreamReader reader = new InputStreamReader(is);
            while(reader.ready()){
                sb.append((char)reader.read());
            }
            reader.close();
        }catch(Exception err){err.printStackTrace(); return null;}
        return sb.toString();
    }
}