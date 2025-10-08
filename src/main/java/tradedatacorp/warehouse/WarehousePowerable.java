/**
 * @author Bruce Lamb
 * @since 20 SEP 2025
 */

package tradedatacorp.warehouse;

/**
 * This interface is used to power on or off the warehouse.
 * Specific details specified by the class implementations.
 * This can power the entire warehouse or partial elements defined by configurations.
 */
public interface WarehousePowerable<ResultT, ConfigsT>{
    public ResultT powerSwitch(ConfigsT configurations);
}