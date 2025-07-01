/**
 * @author Bruce Lamb
 * @since 30 JUN 2025
 */
package tradedatacorp.warehouse;

/**
 * A warehouse implementation for storing OHLCV candlestick data in a filesystem.
 * This is a machine that is intended to constantly run but can be turned on or off without corruption.
 * This instance will make use of {@link OHLCV_BinaryLexical} to super compress files. Will be able to quickly fetch data from
 * files in it's compressed form utilizing unique super compressed format.
 * Elaboration coming soon upon completion of JUnit tests.
 */
public class OHLCV_BinaryWarehouse implements Warehouse{
    //Warehouse Overrides
    @Override
    public String connect(String credentials){
        return null;
    }
}