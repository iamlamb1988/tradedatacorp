/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.Set;

import tradedatacorp.tools.jsonparser.JSON_CompositeMap;
import tradedatacorp.tools.jsonparser.JSON_Item;

import java.util.Hashtable;
import java.util.ArrayList;

public class JSON_Object implements JSON_CompositeMap{
    public static final byte NULL = 0;
    public static final byte BOOLEAN = 1;
    public static final byte STRING = 2;
    public static final byte INTEGER = 3;
    public static final byte DECIMAL = 4;
    public static final byte ARRAY = 5;
    public static final byte OBJECT = 6;

    private Hashtable<String,JSON_Item> attributeMap;
    JSON_Object(){attributeMap = new Hashtable<String,JSON_Item>();}

    //JSON_Item Overrides
    @Override
    public byte getType(){return JSON_Object.OBJECT;}

    @Override
    public JSON_Item getValue(){return this;}

    //JSON_CompositeMap Overrides
    @Override
    public void addJSON_Attribute(String key, JSON_Item attr){attributeMap.put(key,attr);}

    @Override
    public JSON_Item getJSON_Attribute(String key){return attributeMap.get(key);}

    @Override
    public int getKeyCount(){return attributeMap.size();}

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
}