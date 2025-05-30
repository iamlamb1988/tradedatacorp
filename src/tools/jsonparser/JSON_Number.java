/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Interface for JSON numeric types {@link JSON_Integer} and {@link JSON_Decimal}}
 * Extends {@link JSON_Item}.
 */
public interface JSON_Number extends JSON_Item{
    /**
     * Returns the value as a double.
     * @return The numeric value as double.
     */
    public double getDecimalValue();
}