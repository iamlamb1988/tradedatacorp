/**
 * @author Bruce Lamb
 * @since 18 FEB 2025
 */
package tradedatacorp.item.stick.primitive;

/**
 * Standard Lite information containing basic Candlestick data. Each relevant field is of primitive type double, hence the name of the interface.
 * This CandleStick data type is more suitable for Crypto currencies for a few reasons.
 * 1 reason is that each Crypto has a unique non-standard precision on it's value to currency ratio.
 * Volumes and prices may need to extend to several decimal places.
 * Unlike the US stockmarket the most decimal points a value may need is 4 or 5. A different CandleStick implementation may be needed for US Stocks.
 */
public interface StickDouble extends PrimitiveStick, Comparable<StickDouble> {
    /**
     * Returns the UTC timestamp (milliseconds) of closing value.
     * @return The UTC timestamp (milliseconds) of the closing value of this stick. The number millisieconds that has elapsed since 1 JAN 1970.
     * Also indicates the Greenwich Mean Time (GMT) zone or Zulu time. 
     */
    public long getUTC();

    /**
     * Returns the opening value of this Stick.
     * @return The opening value of this Stick.
     */
    public double getO();

    /**
     * Returns the highest value of this Stick.
     * @return The highest value of this Stick.
     */
    public double getH();

    /**
     * Returns the lowest value of this Stick.
     * @return The lowest value of this Stick.
     */
    public double getL();

    /**
     * Returns the closing value of this Stick.
     * @return The closing value of this Stick.
     */
    public double getC();

    /**
     * Returns the volume traded of this Stick.
     * @return The volume traded of this Stick.
     */
    public double getV();

    /**
     * Compares two StickDouble instances for equality.
     * @param stick1 the first StickDouble instance to be compared.
     * @param stick2 the second StickDouble instance to be compared.
     * @return true if all six fields (UTC, O, H, L, C, V) are equivalent; otherwise, false.
     */
    public static boolean isEqual(StickDouble stick1, StickDouble stick2){
        return
            stick1.getUTC() == stick2.getUTC() &&
            stick1.getO() == stick2.getO() &&
            stick1.getH() == stick2.getH() &&
            stick1.getL() == stick2.getL() &&
            stick1.getC() == stick2.getC() &&
            stick1.getV() == stick2.getV();
    }
}