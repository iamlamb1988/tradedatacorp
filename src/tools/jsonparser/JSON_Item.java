/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

public interface JSON_Item{
    public static final byte NULL = 0;
    public static final byte BOOLEAN = 1;
    public static final byte STRING = 2;
    public static final byte INTEGER = 3;
    public static final byte DECIMAL = 4;
    public static final byte ARRAY = 5;
    public static final byte OBJECT = 6;

    public byte getType();
    public JSON_Item getValue();
}
