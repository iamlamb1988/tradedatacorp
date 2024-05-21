package tradedatacorp.warehouse;

public interface WarehouseStorer{
	public Object storeOne(Object validData);
	public Object store(Object validDataCollection);
}