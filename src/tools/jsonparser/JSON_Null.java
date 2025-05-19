/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public class JSON_Null implements JSON_Item{
    @Override
    public byte getType(){return JSON_Object.NULL;}

    @Override
    public JSON_Item getValue(){return null;}
}