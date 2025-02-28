/**
 * @author Bruce Lamb
 * @since 28 FEB 2025
 */
package tradedatacorp.smelter.filesmelter;

import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayList;

import tradedatacorp.smelter.filesmelter.FileSmelterStateful;

//TODO: This is the skeleton of what needs to be implemented
public class OriginalFileSmelter implements FileSmelterStateful<boolean[]>{
    // private ArrayList<Byte> crucible; Working on what fields this class should have

    //Smelter Overrides
    public void smelt(boolean[] rawDataElement){}
    public void smelt(boolean[][] rawDataArray){}
    public void smelt(Collection<boolean[]> rawDataArray){}

    //SmelterStateful Overrides
    public void addData(boolean[] rawData){}
    public void addData(boolean[][] rawData){}
    public void addData(Collection<boolean[]> rawData){}
    public void smelt(){}

    //FileSmelterStateful Overrides
    public void smeltToFile(Path destinationPathName){}
}
