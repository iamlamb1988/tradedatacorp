package tradedatacorp.tools.jsonparser;

public class JSON_Integer implements JSON_Attribute{
    private String key;
    private Long integer;

    public JSON_Integer(String key, long value){
        this.key=key;
        integer=Long.valueOf(value);
    }

    @Override
    public byte getType(){return JSON_Object.INTEGER;}

    @Override
    public String getKey(){return key;}

    public long getValue(){return integer.longValue();}
}