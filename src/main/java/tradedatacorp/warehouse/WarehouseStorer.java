/**
 * @author Bruce Lamb
 * @since 10 SEP 2025
 */
package tradedatacorp.warehouse;

import java.util.Collection;

public interface WarehouseStorer<SingleT, ResultT>{
    public ResultT storeOne(SingleT validData);
    public ResultT store(SingleT[] validDataCollection);
    public ResultT store(Collection<SingleT> validDataCollection);
}