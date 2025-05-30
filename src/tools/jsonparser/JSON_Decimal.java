/**
 * @author Bruce Lamb
 * @since 19 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public class JSON_Decimal implements JSON_Number{
    private Double decimal;

    public JSON_Decimal(double value){
        decimal=Double.valueOf(value);
    }

    @Override
    public byte getType(){return JSON_Object.DECIMAL;}

    @Override
    public JSON_Item getValue(){return this;}

    public double getDecimalValue(){return decimal.doubleValue();}
}