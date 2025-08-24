/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Base interface for all JSON value types.
 * Defines the type constants and methods to get the type and value of a JSON item.
 */
public interface JSON_Item{
    /** Type code for null. */
    public static final byte NULL = 0;

    /** Type code for boolean. */
    public static final byte BOOLEAN = 1;

    /** Type code for string. */
    public static final byte STRING = 2;

    /** Type code for integer. */
    public static final byte INTEGER = 3;

    /** Type code for decimal. */
    public static final byte DECIMAL = 4;

    /** Type code for array. */
    public static final byte ARRAY = 5;

    /** Type code for object. */
    public static final byte OBJECT = 6;

    /**
     * Returns the type of this JSON item.
     * @return The type code (see static constants).
     */
    public byte getType();
}