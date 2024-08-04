package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.Smelter;

public interface FileSmelter<InputT,RefinedT,ParamT,FileT> extends Smelter<InputT,RefinedT,ParamT>{
    public void unionFiles();
}