/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Represents a JSON boolean value (true or false).
 */
public class JSON_Boolean implements JSON_Item{
    private Boolean bool;

    /**
     * Constructs a JSON_Boolean with the specified value.
     * @param value The boolean value.
     */
    public JSON_Boolean(boolean value){bool=Boolean.valueOf(value);}

    @Override
    public byte getType(){return JSON_Object.BOOLEAN;}

    /**
     * Returns the primitive boolean value.
     * @return The boolean value.
     */
    public boolean getBooleanValue(){return bool.booleanValue();}
}