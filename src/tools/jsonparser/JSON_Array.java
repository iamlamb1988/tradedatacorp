/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.ArrayList;

/**
 * Represents a JSON array (ordered list of items).
 */
public class JSON_Array implements JSON_CompositeArray{
    private ArrayList<JSON_Item> elementList;

    public JSON_Array(){elementList=new ArrayList<JSON_Item>();}

    //JSON_Item Overrides
    @Override
    public byte getType(){return JSON_Object.ARRAY;}

    @Override
    public JSON_Item getValue(){return this;}

    //JSON_CompositeArray
    @Override
    public void addJSON_Item(JSON_Item item){elementList.add(item);}

    @Override
    public JSON_Item getItem(int index){return elementList.get(index);}

    public ArrayList<JSON_Item> getArray(){return elementList;}

    public int getItemCount(){return elementList.size();}

    public byte getItemType(int index){return elementList.get(index).getType();}
}