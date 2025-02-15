/**
 * @author Bruce Lamb
 * @since 21 MAY 2024
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
	 * @return an xml message tag, msg in the following format:
	 * {@code
	 * <msg>
	 *   <status>[CONNECTION]</status>
	 *   <warning>[WARNING]</warning>
	 *   <error>[ERROR]</error>
	 * </msg>
	 * }
	 * 
	 * XML tag: msg will always have a status tag. Will only contain an error tag if an error has occured.
	 * 
	 * CONNECTION: "OK", "FAILED"
	 * ERROR: Will state the nature of the error. Based on specific implementation
	 */
	public String connect(String credentials);

	/**
	 * @return "CONNECTED", "NONE", "STATELESS"
	 */
	public String connectStatus();
}
