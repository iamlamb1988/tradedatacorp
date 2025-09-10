import tradedatacorp.warehouse.OHLCV_BinaryWarehouse;

import java.nio.file.Path;

public class TestMain{
    public static void main(String[] args){
        System.out.println("Warehouse testing.");
        OHLCV_BinaryWarehouse warehouse = new OHLCV_BinaryWarehouse();

        String result = warehouse.initialize(new String[]{"/root/test/mydb"});
        System.out.println(result);
    }
}