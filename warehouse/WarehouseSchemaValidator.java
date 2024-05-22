/**
 * @author Bruce Lamb
 * @since 22 MAY 2024
 */

package tradedatacorp.warehouse;

/**
 * Ensures the warehouse schema (database, naming conventions, datatypes) are compliant to receive and store data.
 */
public interface WarehouseSchemaValidator{
	/**
	 * Requires successful connection or access to warehouse location.
	 * @return 
	 * {@code
	 *   <msg>
	 *   <status>[VALID]</status>
	 *   <warning>[WARNING]</warning>
	 *   <error>[ERROR]</error>
	 * </msg>
	 * }
	 *
	 * XML tag: msg will always have a status tag. Tags "warning" and "error" are optional
	 *
	 * VALID: "OK", "NONCOMPLIANT"
	 */
	public String validateSchema();
}