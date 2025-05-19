/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.ArrayList;

public class JSON_Array implements JSON_Item{
    private ArrayList<JSON_Item> elementList;

    public JSON_Array(){elementList=new ArrayList<JSON_Item>();}

    @Override
    public byte getType(){return JSON_Object.ARRAY;}

    @Override
    public JSON_Item getValue(){return this;}

    public ArrayList<JSON_Item> getArray(){return elementList;}

    public int getElementCount(){return elementList.size();}

    public byte getElementType(int index){return elementList.get(index).getType();}
    public JSON_Item getItem(int index){return elementList.get(index);}

    public void addAttribute(JSON_Item element){elementList.add(element);}
}