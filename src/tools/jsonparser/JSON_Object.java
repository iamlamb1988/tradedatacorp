/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.Set;

import tradedatacorp.tools.jsonparser.JSON_CompositeMap;
import tradedatacorp.tools.jsonparser.JSON_Item;

import java.util.Hashtable;
import java.util.ArrayList;

/**
 * Represents a JSON object (key-value pairs).
 */
public class JSON_Object implements JSON_CompositeMap{
    private Hashtable<String,JSON_Item> attributeMap;

    /**
     * Constructs an empty JSON object.
     */
    JSON_Object(){attributeMap = new Hashtable<String,JSON_Item>();}

    //JSON_Item Overrides
    @Override
    public byte getType(){return JSON_Item.OBJECT;}

    //JSON_CompositeMap Overrides
    @Override
    public void addJSON_Attribute(String key, JSON_Item attr){attributeMap.put(key,attr);}

    @Override
    public JSON_Item getJSON_Attribute(String key){return attributeMap.get(key);}

    @Override
    public int getKeyCount(){return attributeMap.size();}

    /**
     * Returns the set of keys in the object.
     * @return The set of attribute names.
     */
    public Set<String> getKeySet(){return attributeMap.keySet();}

    /**
     * Returns the keys as an array of strings.
     * @return An array containing all attribute names.
     */
    public String[] getKeyArray(){
        String[] r=new String[attributeMap.size()];
        Set<String> keySet = attributeMap.keySet();
        int i=0;
        for(String key : keySet){
            r[i]=key;
            ++i;
        }
        return r;
    }
}