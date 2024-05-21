package tradedatacorp.warehouse;

public interface Warehouse{
	Object connect(String credentials);
	Object connectStatus();
}