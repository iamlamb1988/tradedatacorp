package tradedatacorp.item.stick.primitive;

/**
 * Standard Lite information containing basic Candlestick data.
 * This CandleStick data type is more suitable for Crypto currencies for a few reasons.
 * 1 reason is that each Crypto has a unique non-standard precision on it's value to currency ratio.
 * Volumes and prices may need to extend to several decimal places.
 * Unlike the US stockmarket the most decimal points a value may need is 4 or 5. A different CandleStick implementation may be needed for US Stocks.
 */
public interface StickDouble extends PrimitiveStick{
	public double getO();
	public double getH();
	public double getL();
	public double getC();
	public double getV();
}