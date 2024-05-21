package tradedatacorp.warehouse;

public interface WarehouseStorer{
	Object storeOne(Object validData);
	Object store(Object validDataCollection);
}