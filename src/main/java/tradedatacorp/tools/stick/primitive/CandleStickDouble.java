/**
 * @author Bruce Lamb
 * @since 18 FEB 2025
 */
package tradedatacorp.tools.stick.primitive;

import tradedatacorp.tools.stick.primitive.mod.Modifydbl;

/**
 * Standard Lite information containing basic Candlestick data.
 * This CandleStick data type is more suitable for Crypto currencies for a few reasons.
 * 1 reason is that each Crypto has a unique non-standard precision on it's value to currency ratio.
 * Volumes and prices may need to extend to several decimal places.
 * Unlike the US stockmarket the most decimal points a value may need is 4 or 5. A different CandleStick implementation may be needed for US Stocks.
 */
public class CandleStickDouble implements StickDouble, Modifydbl{
    private long UTC;
    private double O;
    private double H;
    private double L;
    private double C;
    private double V;

    //CandleStickDouble Constructors:
    /**
     * Constructs a new CandleStickDouble with the specified candlestick data.
     * @param utc_timestamp The UTC timestamp (milliseconds) of the closing value. Represents the number of milliseconds elapsed since January 1, 1970 (epoch time).
     * @param open The opening price of the candlestick.
     * @param high The highest price reached during the candlestick's time period.
     * @param low The lowest price reached during the candlestick's time period.
     * @param close The closing price of the candlestick.
     * @param volume The trading volume during the candlestick's time period.
     */
    public CandleStickDouble(
        long utc_timestamp,
        double open,
        double high,
        double low,
        double close,
        double volume
    ){
        UTC=utc_timestamp;
        O=open;
        H=high;
        L=low;
        C=close;
        V=volume;
    }

    //StickDouble Interface methods:
    /**
     * Returns the UTC timestamp (milliseconds) of closing value.
     * @return The UTC timestamp (milliseconds) of the closing value of this stick. The number millisieconds that has elapsed since 1 JAN 1970.
     * Also indicates the Greenwich Mean Time (GMT) zone or Zulu time.
     */
    @Override
    public long getUTC(){return UTC;}

    /**
     * Returns the opening value of this Stick.
     * @return The opening value of this Stick.
     */
    @Override
    public double getO(){return O;}

    /**
     * Returns the highest value of this Stick.
     * @return The highest value of this Stick.
     */
    @Override
    public double getH(){return H;}

    /**
     * Returns the lowest value of this Stick.
     * @return The lowest value of this Stick.
     */
    @Override
    public double getL(){return L;}

    /**
     * Returns the closing value of this Stick.
     * @return The closing value of this Stick.
     */
    @Override
    public double getC(){return C;}

    /**
     * Returns the volume traded of this Stick.
     * @return The volume traded of this Stick.
     */
    @Override
    public double getV(){return V;}

    //Modifydbl Inteface methods:
    /**
     * Sets a new Open alue.
     */
    @Override
    public void setO(double open){O=open;}

    /**
     * Sets a new High value. Does not check if Stick is still valid.
     */
    @Override
    public void setH(double high){H=high;}

    /**
     * Sets a new Low value. Does not check if Stick is still valid.
     */
    @Override
    public void setL(double low){L=low;}

    /**
     * Sets a new Close value.
     */
    @Override
    public void setC(double close){C=close;}

    /**
     * Sets a new Volume value.
     */
    @Override
    public void setV(double volume){V=volume;}

    //Comparable Interface methods:
    /**
     * Returns the time difference between this stick and the next
     * @return The time difference from the 2nd candlestick to this.
     */
    @Override
    public int compareTo(StickDouble otherStick){return Long.compare(UTC,otherStick.getUTC());}
}