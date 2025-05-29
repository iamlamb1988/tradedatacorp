import tradedatacorp.miner.PolygonIO_CryptoMiner;
import tradedatacorp.tools.jsonparser.*;

import java.util.ArrayList;
import java.net.http.HttpResponse;

public class TestMiner{
    public static void main(String[] args){
        System.out.println("Hello Miner!");
		// PolygonIO_CryptoMiner miner = new PolygonIO_CryptoMiner("NuslrjAqkue1coRYRnJVSMv1vgUh_GN8");
		// HttpResponse<String> responseFromPolygonIO = miner.mineResponseByUTCRange("X:BTCUSD", "minute", 1735689600001L-2*(60*1000), 1735689600000L);

        // System.out.println("STATUS: "+responseFromPolygonIO.statusCode());
        // System.out.println("BODY:\n"+responseFromPolygonIO.body());
        String array2 = "{\"L1\":[] ,\"L2\" :[ ], \"L3\": [    ]}";

        System.out.println(array2);
    
        // JSON_Object x = JSON_Parser.parse(responseFromPolygonIO.body());
        JSON_Object x = JSON_Parser.parse(array2);
        
        System.out.println("Praparing to iterate through keys");
        printValues(x);
    }

    public static void printValues(JSON_Object json){
        String[] jsonKey = json.getKeyArray();
        System.out.println("NEXT OBJECT:");

        for(String key : jsonKey){
            System.out.println("KEY: "+key);
            JSON_Item item = json.getJSON_Attribute(key);

            switch(item.getType()){
                case 0:
                    System.out.println("TYPE: NULL");
                    break;
                case 1:
                    System.out.println("TYPE: Boolean");
                    JSON_Boolean itemBool = (JSON_Boolean)item;
                    System.out.println("VALUE: "+itemBool.getBooleanValue());
                    break;
                case 2:
                    System.out.println("TYPE: String");
                    JSON_String itemStr = (JSON_String)item;
                    System.out.println("VALUE: "+itemStr.getStringValue());
                    break;
                case 3:
                    System.out.println("TYPE: Integer");
                    JSON_Integer itemInt = (JSON_Integer)item;
                    System.out.println("VALUE: "+itemInt.getIntegerValue());
                    break;
                case 4:
                    System.out.println("TYPE: Decimal");
                    JSON_Decimal itemDec = (JSON_Decimal)item;
                    System.out.println("VALUE: "+itemDec.getDecimalValue());
                    break;
                case 5:
                    System.out.println("TYPE: Array");
                    JSON_Array itemArr = (JSON_Array)item;
                    printArrayValues(itemArr);
                    break;
                case 6:
                    System.out.println("TYPE: Object");
                    JSON_Object itemObj = (JSON_Object)item;
                    printValues(itemObj);
                    break;
            }
        }
        System.out.println();
    }

    public static void printArrayValues(JSON_Array jsonArr){
        ArrayList<JSON_Item> itemList = jsonArr.getArray();
        for(JSON_Item item : itemList){
            switch(item.getType()){
                case 0:
                    System.out.println("TYPE: NULL");
                    break;
                case 1:
                    System.out.println("TYPE: Boolean");
                    JSON_Boolean itemBool = (JSON_Boolean)item;
                    System.out.println("VALUE: "+itemBool.getBooleanValue());
                    break;
                case 2:
                    System.out.println("TYPE: String");
                    JSON_String itemStr = (JSON_String)item;
                    System.out.println("VALUE: "+itemStr.getStringValue());
                    break;
                case 3:
                    System.out.println("TYPE: Integer");
                    JSON_Integer itemInt = (JSON_Integer)item;
                    System.out.println("VALUE: "+itemInt.getIntegerValue());
                    break;
                case 4:
                    System.out.println("TYPE: Decimal");
                    JSON_Decimal itemDec = (JSON_Decimal)item;
                    System.out.println("VALUE: "+itemDec.getDecimalValue());
                    break;
                case 5:
                    System.out.println("TYPE: Array");
                    JSON_Array itemArr = (JSON_Array)item;
                    printArrayValues(itemArr);
                    break;
                case 6:
                    System.out.println("TYPE: Object");
                    JSON_Object itemObj = (JSON_Object)item;
                    printValues(itemObj);
                    break;
            }
        }
    }
}