/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Represents a JSON string value.
 */
public class JSON_String implements JSON_Item{
    private String value;

    /**
     * Constructs a JSON_String with the given value.
     * @param value The string value.
     */
    public JSON_String(String value){this.value=value;}

    @Override
    public byte getType(){return JSON_Object.STRING;}

    /**
     * Returns the underlying string value.
     * @return The string value.
     */
    public String getStringValue(){return value;}
}