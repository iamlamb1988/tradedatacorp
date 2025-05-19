/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.Set;
import java.util.Hashtable;
import java.util.ArrayList;

public class JSON_Object implements JSON_Item{
    public static final byte NULL = 0;
    public static final byte STRING = 1;
    public static final byte INTEGER = 2;
    public static final byte DECIMAL = 3;
    public static final byte BOOLEAN = 4;
    public static final byte ARRAY = 5;
    public static final byte OBJECT = 6;

    private String key;
    private Hashtable<String,JSON_Item> attributeMap;

    public JSON_Object(String key){
        this.key = key;
        attributeMap = new Hashtable<String,JSON_Item>();
    }

    @Override
    public byte getType(){return JSON_Object.OBJECT;}

    @Override
    public JSON_Item getValue(){return this;}

    public void addJSON_Attribute(String key, JSON_Item attr){attributeMap.put(key,attr);}

    public Set<String> getKeySet(){return attributeMap.keySet();}
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

    public int getAttributeCount(){return attributeMap.size();}

    public JSON_Item getItem(String key){return attributeMap.get(key);}
}