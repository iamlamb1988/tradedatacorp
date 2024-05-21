package tradedatacorp.warehouse;

public interface WarehousePicker{
	Object pick(String TickerSymbol, long UTC_Start, long UTC_End);
}