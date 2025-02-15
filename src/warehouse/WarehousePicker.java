package tradedatacorp.warehouse;

public interface WarehousePicker{
	public Object pick(String TickerSymbol, long UTC_Start, long UTC_End);
}