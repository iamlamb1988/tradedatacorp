package tradedatacorp.item.stick.primitive.mod;

/**
 * This interface should only be used on implementations of Stickdbl
 * This is intended to modify candlestick data.
 */
public interface Modifydbl{
	public void setO(double open);
	public void setH(double high);
	public void setL(double low);
	public void setC(double close);
	public void setV(double volume);
}