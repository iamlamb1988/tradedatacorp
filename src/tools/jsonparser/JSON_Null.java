/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public class JSON_Null implements JSON_Item{
    @Override
    public byte getType(){return JSON_Object.NULL;}

    /**
     * Returns null to represent the JSON null value.
     * @return null.
     */
    public JSON_Item getValue(){return null;}
}