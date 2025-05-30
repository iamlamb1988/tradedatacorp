/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Interface for {@link JSON_Object} types. Allows adding and retrieve items by key.
 */
public interface JSON_CompositeMap extends JSON_Composite{
    
    public void addJSON_Attribute(String key, JSON_Item value);
    public JSON_Item getJSON_Attribute(String key);
    public int getKeyCount();
}