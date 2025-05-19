import tradedatacorp.miner.PolygonIO_CryptoMiner;
import tradedatacorp.tools.jsonparser.JSON_Parser;
import tradedatacorp.tools.jsonparser.*;

import java.net.http.HttpResponse;

public class TestMiner{
    public static void main(String[] args){
        System.out.println("Hello Miner!");
		// PolygonIO_CryptoMiner miner = new PolygonIO_CryptoMiner("NuslrjAqkue1coRYRnJVSMv1vgUh_GN8");
		// HttpResponse<String> responseFromPolygonIO = miner.mineResponseByUTCRange("X:BTCUSD", "minute", 1735689600001L-2*(60*1000), 1735689600000L);

        // System.out.println("STATUS: "+responseFromPolygonIO.statusCode());
        // System.out.println("BODY:\n"+responseFromPolygonIO.body());
        String testHelloWorld = "{\"hello\":\"world\"}";
        String testNumber = "{\"integer\":7}";
        String testStringAndNumber = "{\"hello\":\"world\",\"int\":7}";
        String pi = "{\"pi\":3.1415}";
        String empty = "{}";
        String empty2 = "{    }";
        String testBool = "{\"A\" : true, \"B\":false, \"C\"   : true}";

        System.out.println(testBool);
    
        System.out.println("Debug test for json parser.");
        // JSON_Object x = JSON_Parser.parse(responseFromPolygonIO.body());
        JSON_Object x = JSON_Parser.parse(testHelloWorld),
                    y = JSON_Parser.parse(testNumber),
                    z = JSON_Parser.parse(testStringAndNumber),
                    a = JSON_Parser.parse(pi),
                    b = JSON_Parser.parse(empty),
                    c = JSON_Parser.parse(empty2),
                    d = JSON_Parser.parse(testBool);
        
        System.out.println("Praparing to iterate through keys");
        printValues(x);
        printValues(y);
        printValues(z);
        printValues(a);
        printValues(b);
        printValues(c);
        printValues(d);
    }

    public static void printValues(JSON_Object json){
        String[] jsonKey = json.getKeyArray();
        System.out.println("NEXT OBJECT:");

        for(String key : jsonKey){
            System.out.println("KEY: "+key);
            JSON_Item item = json.getItem(key);

            switch(item.getType()){
                case 0:
                    System.out.println("TYPE: NULL");
                    break;
                case 1:
                    System.out.println("TYPE: Boolean");
                    JSON_Boolean itemBool = (JSON_Boolean)item;
                    System.out.println("VALUE: "+itemBool.getBooleanValue());
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
            }
        }
        System.out.println();
    }
}
