import tradedatacorp.miner.PolygonIO_CryptoMiner;
import tradedatacorp.tools.jsonparser.JSON_Parser;
import tradedatacorp.tools.jsonparser.JSON_Item;
import tradedatacorp.tools.jsonparser.JSON_Object;
import tradedatacorp.tools.jsonparser.JSON_String;

import java.net.http.HttpResponse;

public class TestMiner{
    public static void main(String[] args){
        System.out.println("Hello Miner!");
		PolygonIO_CryptoMiner miner = new PolygonIO_CryptoMiner("NuslrjAqkue1coRYRnJVSMv1vgUh_GN8");
		HttpResponse<String> responseFromPolygonIO = miner.mineResponseByUTCRange("X:BTCUSD", "minute", 1735689600001L-2*(60*1000), 1735689600000L);

        System.out.println("STATUS: "+responseFromPolygonIO.statusCode());
        System.out.println("BODY:\n"+responseFromPolygonIO.body());
    
        System.out.println("Debug test for json parser.");
        JSON_Object x = JSON_Parser.parse(responseFromPolygonIO.body());
        String[] xKey = x.getKeyArray();

        for(String key : xKey){
            System.out.println("Key: "+key);
            JSON_Item item = x.getItem(key);

            switch(item.getType()){
                case 0:
                    System.out.println("TYPE: NULL");
                    break;
                case 1:
                    System.out.println("TYPE: String");
                    JSON_String itemStr = (JSON_String)item;
                    System.out.println("VALUE: "+itemStr.getStringValue());
                    break;
            }
        }
    }
}
