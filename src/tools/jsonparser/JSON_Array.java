/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.ArrayList;

/**
 * Represents a JSON array (ordered list of items).
 * Each element in the array is a {@link JSON_Item}.
 */
public class JSON_Array implements JSON_CompositeArray{
    private ArrayList<JSON_Item> elementList;

    /**
     * Constructs an empty JSON array.
     */
    public JSON_Array(){elementList=new ArrayList<JSON_Item>();}

    //JSON_Item Overrides
    @Override
    public byte getType(){return JSON_Object.ARRAY;}

    //JSON_CompositeArray
    @Override
    public void addJSON_Item(JSON_Item item){elementList.add(item);}

    @Override
    public JSON_Item getItem(int index){return elementList.get(index);}

    /**
     * Returns the number of items in the array.
     * @return The item count.
     */
    public int getItemCount(){return elementList.size();}

     /**
     * Returns the type code of the item at the specified index.
     * @param index The index of the item.
     * @return The type code.
     */
    public byte getItemType(int index){return elementList.get(index).getType();}
}