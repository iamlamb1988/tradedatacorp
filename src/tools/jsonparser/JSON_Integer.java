/**
 * @author Bruce Lamb
 * @since 19 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public class JSON_Integer implements JSON_Number{
    private Long integer;

    public JSON_Integer(long value){
        integer=Long.valueOf(value);
    }

    @Override
    public byte getType(){return JSON_Object.INTEGER;}

    @Override
    public JSON_Item getValue(){return this;}

    public long getIntegerValue(){return integer.longValue();}
}