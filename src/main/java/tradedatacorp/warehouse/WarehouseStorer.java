/**
 * @author Bruce Lamb
 * @since 21 MAY 2024
 */
package tradedatacorp.warehouse;

public interface WarehouseStorer{
	public Object storeOne(Object validData);
	public Object store(Object validDataCollection);
}