/**
 * @author Bruce Lamb
 * @since 07 SEP 2025
 */

package tradedatacorp.warehouse;

/**
 * This interface provides initialization setup for a warehouse.
 */
public interface WarehouseInitializer<ResultT>{
    public ResultT initialize();
}