package tradedatacorp.smelter.filesmelter;

import tradedatacorp.smelter.Smelter;

public interface FileSmelter<RawT,RefinedT,ParamT,FileT> extends Smelter<RawT,RefinedT,ParamT>{
    public void unionFiles();
}