/**
 * @author Bruce Lamb
 * @since 1 MAR 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.smelter.lexical.binary.Original;

import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayList;

//TODO: This is the skeleton of what needs to be implemented.
public class OriginalFileSmelter implements FileSmelterStateful<StickDouble>{
    private Original binaryTranslator; //Translates from ? to flattened bin (type boolean[])
    private Path targetFile;
    private ArrayList<boolean[]> crucible; //Perhaps a Queus (FIFO) instead of list?

    //Constructor
    //TODO
    public OriginalFileSmelter(Original originalTranslator){
        //TODO
    }

    //Smelter Overrides
    public void smelt(StickDouble dataStick){}
    public void smelt(StickDouble[] rawDataArray){}
    public void smelt(Collection<StickDouble> rawDataArray){}

    //SmelterStateful Overrides
    public void addData(StickDouble dataStick){crucible.add(binaryTranslator.getBinaryDataFlat(dataStick));}

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

    public void smelt(){}

    //FileSmelterStateful Overrides
    public void smeltToFile(Path destinationPathName){}
}
