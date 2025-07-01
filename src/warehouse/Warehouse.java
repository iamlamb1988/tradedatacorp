/**
 * @author Bruce Lamb
 * @since 01 JUL 2025
 */

package tradedatacorp.warehouse;

/**
 * This interface is a representation of a warehouse that stores large pools of CandleStick trading data.
 * This is agnostic of specific data storage such as SQL Database, NoSQL Database, FileSystem, API Site, etc.
 * This interface is intended to be used in conjuction with the helper Warehouse interfaces.
 */
public interface Warehouse{
    /**
     * Attempts to establish a connection to a warehouse.
     * @param credentials credentials required for persistent connection to warehouse.
     * @return a message regarding state of connection attempt
     */
    public String connect(String credentials);
}
