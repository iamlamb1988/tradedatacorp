/**
 * @author Bruce Lamb
 * @since 10 SEP 2025
 */
package tradedatacorp.warehouse;

import java.util.Collection;

public interface WarehousePicker<SingleT>{
    public Collection<SingleT> pickToCollection(String TickerSymbol, long UTC_Start, long UTC_End);
    public SingleT[] pickToArray(String TickerSymbol, long UTC_Start, long UTC_End);
}