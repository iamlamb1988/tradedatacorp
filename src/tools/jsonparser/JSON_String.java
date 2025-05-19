/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public class JSON_String implements JSON_Item{
    private String value;

    public JSON_String(String value){this.value=value;}

    @Override
    public byte getType(){return JSON_Object.STRING;}

    @Override
    public JSON_Item getValue(){return this;}

    public String getStringValue(){return value;}
}