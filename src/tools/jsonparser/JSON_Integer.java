/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Represents a JSON integer number.
 */
public class JSON_Integer implements JSON_Number{
    private Long integer;

    public JSON_Integer(long value){
        integer=Long.valueOf(value);
    }

    //JSON_Item Overrides
    @Override
    public byte getType(){return JSON_Object.INTEGER;}

    //JSON_Number Overrides
    @Override
    public double getDecimalValue(){return integer.doubleValue();}

    /**
     * Returns the primitive long integer value.
     * @return The integer value.
     */
    public long getIntegerValue(){return integer.longValue();}
}