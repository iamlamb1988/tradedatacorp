/**
 * @author Bruce Lamb
 * @since 07 SEP 2025
 */

package tradedatacorp.warehouse;

/**
 * This interface represents the location of Data location.
 * This is agnostic of specific data storage such as SQL Database, NoSQL Database, FileSystem, API Site, etc.
 * This will determine if data is accessible and determines health and status of the connection.
 * Will return specific results, ResultT, regarding information about successful connection to data or data corruption.
 */
public interface WarehouseDataStorage<ResultT, CredsT>{
    /**
     * Attempts to establish a connection to a warehouse.
     *
     * @param credentials credentials required for persistent connection to warehouse.
     * @return a message regarding state of connection attempt.
     */
    public ResultT connect(CredsT credentials);

    /**
     * Returns the status of the warehouse connection.
     *
     * @return The status of the warehouse connection.
     */
    public ResultT connectionStatus();
}