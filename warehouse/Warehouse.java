package tradedatacorp.warehouse;

public interface Warehouse{
	public Object connect(String credentials);
	public Object connectStatus();
}