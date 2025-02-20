/**
 * @author Bruce Lamb
 * @since 18 FEB 2025
 */
package tradedatacorp.item.stick.primitive.mod;

/**
 * This interface should only be used on implementations of Stickdbl
 * This is intended to modify candlestick data.
 */
public interface Modifydbl{
	/**
	 * This sets the value of Open over it's current Open value.
	 * @param open
	 */
	public void setO(double open);

	/**
	 * This sets the value of Open over it's current Open value.
	 * @param high
	 */
	public void setH(double high);

	/**
	 * This sets the value of Open over it's current Open value.
	 * @param low
	 */
	public void setL(double low);

	/**
	 * This sets the value of Open over it's current Open value.
	 * @param close
	 */
	public void setC(double close);

	/**
	 * This sets the value of Open over it's current Open value.
	 * @param volume
	 */
	public void setV(double volume);
}