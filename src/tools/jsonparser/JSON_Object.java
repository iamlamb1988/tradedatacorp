package tradedatacorp.tools.jsonparser;

import java.util.ArrayList;

public class JSON_Object implements JSON_Attribute{
    public static final byte NULL = 0;
    public static final byte STRING = 1;
    public static final byte INTEGER = 2;
    public static final byte DECIMAL = 3;
    public static final byte BOOLEAN = 4;
    public static final byte ARRAY = 5;
    public static final byte OBJECT = 6;

    private String key;
    private ArrayList<JSON_Attribute> attributeList;

    public JSON_Object(String key){
        this.key=key;
        attributeList = new ArrayList<JSON_Attribute>();
    }

    @Override
    public byte getType(){return JSON_Object.OBJECT;}

    @Override
    public String getKey(){return key;}

    public int getAttributeCount(){return attributeList.size();}

    public byte getAttributeType(int index){return attributeList.get(index).getType();}
    public JSON_Attribute getAttribute(int index){return attributeList.get(index);}

    public void addAttribute(JSON_Attribute attribute){attributeList.add(attribute);}
}