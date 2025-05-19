/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public class JSON_Integer implements JSON_Item{
    private String key;
    private Long integer;

    public JSON_Integer(String key, long value){
        this.key=key;
        integer=Long.valueOf(value);
    }

    @Override
    public byte getType(){return JSON_Object.INTEGER;}

    @Override
    public JSON_Item getValue(){return this;}

    public long getIntegerValue(){return integer.longValue();}
}