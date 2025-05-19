/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public class JSON_Decimal implements JSON_Item{
    private Double decimal;
    private String key;

    public JSON_Decimal(double value){
        this.key=key;
        decimal=Double.valueOf(value);
    }

    @Override
    public byte getType(){return JSON_Object.DECIMAL;}

    @Override
    public JSON_Item getValue(){return this;}

    public double getDoubleValue(){return decimal.doubleValue();}
}