/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Interface for {@link JSON_Array} types. Allows adding and retrieving items to the array.
 */
public interface JSON_CompositeArray extends JSON_Composite{
    /**
     * Adds a JSON item to the array.
     * @param value The item to add.
     */
    public void addJSON_Item(JSON_Item value);

    /**
     * Retrieves the JSON item at the specified index.
     * @param index The index of the array the item is located.
     */
    public JSON_Item getItem(int index);
}