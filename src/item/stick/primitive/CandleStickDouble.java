package tradedatacorp.item.stick.primitive;

import tradedatacorp.item.stick.primitive.mod.Modifydbl;

/**
 * Standard Lite information containing basic Candlestick data.
 * This CandleStick data type is more suitable for Crypto currencies for a few reasons.
 * 1 reason is that each Crypto has a unique non-standard precision on it's value to currency ratio.
 * Volumes and prices may need to extend to several decimal places.
 * Unlike the US stockmarket the most decimal points a value may need is 4 or 5. A different CandleStick implementation may be needed for US Stocks.
 */
public class CandleStickDouble implements StickDouble, Modifydbl{
	private double O;
	private double H;
	private double L;
	private double C;
	private double V;

	//CandleStickdbl Constructors
	public CandleStickDouble(
		double open,
		double high,
		double low,
		double close,
		double volume
	){
		O=open;
		H=high;
		L=low;
		C=close;
		V=volume;
	}

	//Stickdbl Interface methods:
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

	//Modifydbl Inteface methods:
	@Override
	public void setO(double open){O=open;}

	@Override
	public void setH(double high){H=high;}

	@Override
	public void setL(double low){L=low;}

	@Override
	public void setC(double close){C=close;}

	@Override
	public void setV(double volume){V=volume;}
}