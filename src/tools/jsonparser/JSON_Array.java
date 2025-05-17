package tradedatacorp.tools.jsonparser;

import java.util.ArrayList;

public class JSON_Array implements JSON_Attribute{
    private String key;
    private ArrayList<JSON_Attribute> elementList;

    public JSON_Array(String key){
        this.key=key;
        elementList=new ArrayList<JSON_Attribute>();
    }

    @Override
    public byte getType(){return JSON_Object.ARRAY;}

    @Override
    public String getKey(){return key;}

    public ArrayList<JSON_Attribute> getArray(){return elementList;}

    public int getElementCount(){return elementList.size();}

    public byte getElementType(int index){return elementList.get(index).getType();}
    public JSON_Attribute getAttribute(int index){return elementList.get(index);}

    public void addAttribute(JSON_Attribute element){elementList.add(element);}
}