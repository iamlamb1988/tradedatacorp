/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public class JSON_Boolean implements JSON_Item{
    private Boolean bool;

    public JSON_Boolean(boolean value){bool=Boolean.valueOf(value);}

    @Override
    public byte getType(){return JSON_Object.BOOLEAN;}

    @Override
    public JSON_Item getValue(){return this;}

    public boolean getBooleanValue(){return bool.booleanValue();}
}