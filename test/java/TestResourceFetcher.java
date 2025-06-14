package tradedatacorp.test.java;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class only used for fetching test resources.
 */
public class TestResourceFetcher{
    public static final String RELATIVE_FULL_PATH = "tradedatacorp/test/resources/";
    private ClassLoader classLoader;
    public TestResourceFetcher(){
        classLoader = this.getClass().getClassLoader();
    }

    /**
     * Retrieves the file contents of a textfile within the classpath.
     * 
     * @param filePathName the pathName relative to {@code tradedatacorp/test/resources/} directory.
     * Returns the text string.
     */
    public String getTextFileContents(String filePathName){
        if(filePathName != null  && !filePathName.isEmpty()){
            char firstChar = filePathName.charAt(0);
            if(firstChar != '/') filePathName = RELATIVE_FULL_PATH + filePathName;
        }
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