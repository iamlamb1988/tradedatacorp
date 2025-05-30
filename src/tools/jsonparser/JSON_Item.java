/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Base interface for all JSON value types.
 * Defines the type constants and methods to get the type and value of a JSON item.
 */
public interface JSON_Item{
    public static final byte NULL = 0;
    public static final byte BOOLEAN = 1;
    public static final byte STRING = 2;
    public static final byte INTEGER = 3;
    public static final byte DECIMAL = 4;
    public static final byte ARRAY = 5;
    public static final byte OBJECT = 6;

    /**
     * Returns the type of this JSON item.
     * @return The type code (see static constants).
     */
    public byte getType();

    /**
     * Returns the value of this JSON item.
     * @return The item itself (for objects/arrays) or a primitive wrapper.
     */
    public JSON_Item getValue();
}