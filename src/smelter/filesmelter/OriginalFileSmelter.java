/**
 * @author Bruce Lamb
 * @since 3 MAR 2025
 */
package tradedatacorp.smelter.filesmelter;

import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.smelter.lexical.binary.Original;

import java.nio.file.Path;
import java.util.Collection;
import java.util.ArrayDeque;

//TODO: Planning in progress. This class is responsible for writing a binary file specifically adhering to an Original Lexical.
public class OriginalFileSmelter implements FileSmelterStateful<StickDouble>{
    private Original binaryTranslator; //Translates from ? to flattened bin (type boolean[])
    private Path targetFile;
    private ArrayDeque<boolean[]> crucible;
    //private ArrayList<char> preProcessedCrucible ? will change if allignment changes.

    //Constructor
    //TODO
    public OriginalFileSmelter(Original originalTranslator){
        binaryTranslator = originalTranslator.clone();
        targetFile = null;
        crucible = new ArrayDeque<boolean[]>();
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

    //TODO
    public void smelt(){
        //1. Empty crucible into local ArrayList (will keep this as the only Thread adding elements to it)
        //2. Set boolean[] header based on binaryLexical settings and localCrubible size.
        //3. Clone the header locally (Thread safe operation)

        //NOTE: At this point local header and local crucible is set.
        //this.binaryLexical and this.crucible is now safe to change and update
        //because these local instances are locked in for file writing.

        //4. Open this.targetFile to begin writing (OutputFileStream)
        //5. Write the binary header.
        //6. begin streaming and writing a file.
        //May need Threads to keep this smooth
        //May need more planning
    }

    //FileSmelterStateful Overrides
    //TODO
    public void smeltToFile(Path destinationPathName){
        //1. Set destinationPathName to this.targetFile
        //2. Smelt the file (smelt())
   
    }

    //OriginalFileSmelter methods
    public void setTargetFile(String relativePathName){}
    public void setAbsoluteTargetFile(String absolutePathName){}
}
