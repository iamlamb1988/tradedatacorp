/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Represents a JSON decimal (floating-point) number.
 */
public class JSON_Decimal implements JSON_Number{
    private Double decimal;

    /**
     * Constructs a JSON_Decimal with the given value.
     * @param value The double value.
     */
    public JSON_Decimal(double value){
        decimal=Double.valueOf(value);
    }

    @Override
    public byte getType(){return JSON_Object.DECIMAL;}

    /**
     * Returns the decimal value.
     * @return The decimal value.
     */
    public double getDecimalValue(){return decimal.doubleValue();}
}