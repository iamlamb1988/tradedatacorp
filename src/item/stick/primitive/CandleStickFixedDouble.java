package tradedatacorp.item.stick.primitive;

/**
 * Standard Lite information containing basic Candlestick data.
 * This CandleStick data type is more suitable for Crypto currencies for a few reasons.
 * 1 reason is that each Crypto has a unique non-standard precision on it's value to currency ratio.
 * Volumes and prices may need to extend to several decimal places.
 * Unlike the US stockmarket the most decimal points a value may need is 4 or 5. A different CandleStick implementation may be needed for US Stocks.
 */
public class CandleStickFixedDouble implements StickDouble{
	public final long UTC;
	public final double O;
	public final double H;
	public final double L;
	public final double C;
	public final double V;

	//CandleStickFixedDouble Constructors
	public CandleStickFixedDouble(
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
	@Override
	public long getUTC(){return UTC;}

	@Override
	public double getO(){return O;}

	@Override
	public double getH(){return H;}

	@Override
	public double getL(){return L;}

	@Override
	public double getC(){return C;}

	@Override
	public double getV(){return V;}
}