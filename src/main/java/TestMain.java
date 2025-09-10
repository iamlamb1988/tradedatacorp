import tradedatacorp.warehouse.OHLCV_BinaryWarehouse;

import java.nio.file.Path;

public class TestMain{
    public static void main(String[] args){
        System.out.println("Warehouse testing.");
        OHLCV_BinaryWarehouse warehouse = new OHLCV_BinaryWarehouse();

        // Path databasePath = Path.of("/root/test/mydb");
        // System.out.println("STATUS: "+warehouse.connect(databasePath));

		String result = warehouse.initialize(new String[]{"/root/test/mydb"});
		System.out.println("DEBUG RESULT: "+result);
    }
}