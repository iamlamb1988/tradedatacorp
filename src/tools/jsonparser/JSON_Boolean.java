package tradedatacorp.tools.jsonparser;

public class JSON_Boolean implements JSON_Attribute{
    private String key;
    private Boolean bool;

    public JSON_Boolean(boolean value){
        this.key=key;
        bool=Boolean.valueOf(value);
    }

    @Override
    public byte getType(){return JSON_Object.BOOLEAN;}

    @Override
    public String getKey(){return key;}

    public boolean getValue(){return bool.booleanValue();}
}