package tradedatacorp.tools.jsonparser;

public class JSON_Null implements JSON_Attribute{
    private String key;

    public JSON_Null(String key){
        this.key=key;
    }

    @Override
    public byte getType(){return JSON_Object.NULL;}

    @Override
    public String getKey(){return key;}

    public Object getValue(){return null;}
}