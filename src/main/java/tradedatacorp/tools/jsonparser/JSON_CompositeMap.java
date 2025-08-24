/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Interface for {@link JSON_Object} types. Allows adding and retrieve items by key.
 */
public interface JSON_CompositeMap extends JSON_Composite{
    /**
     * Adds a key-value pair to the object.
     * @param key The attribute name.
     * @param value The value to associate with the key.
     */
    public void addJSON_Attribute(String key, JSON_Item value);

    /**
     * Retrieves the value associated with the given key.
     * @param key The attribute name.
     * @return The JSON_Item associated with the key, or null if not present.
     */
    public JSON_Item getJSON_Attribute(String key);

    /**
     * Returns the number of keys in the object.
     * @return The number of keys in the object.
     */
    public int getKeyCount();
}