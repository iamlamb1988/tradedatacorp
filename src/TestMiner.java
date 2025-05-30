import tradedatacorp.miner.PolygonIO_CryptoMiner;
import tradedatacorp.tools.jsonparser.*;
import tradedatacorp.item.stick.primitive.StickDouble;

import java.util.ArrayList;
import java.net.http.HttpResponse;

public class TestMiner{
    public static void main(String[] args){
        System.out.println("Hello Miner!");
		PolygonIO_CryptoMiner miner = new PolygonIO_CryptoMiner("NuslrjAqkue1coRYRnJVSMv1vgUh_GN8");
		HttpResponse<String> responseFromPolygonIO = miner.mineResponseByUTCRange("X:BTCUSD", "minute", 1735689600001L-2*(60*1000), 1735689600000L);

        System.out.println("STATUS: "+responseFromPolygonIO.statusCode());
        System.out.println("BODY:\n"+responseFromPolygonIO.body());

        StickDouble[] stickArray=miner.getSticksFromOHLCV_JSON(responseFromPolygonIO.body());

        for(StickDouble stick : stickArray){
            System.out.println(
                "UTC: "+stick.getUTC()+
                " O: "+stick.getO()+
                " H: "+stick.getH()+
                " L: "+stick.getL()+
                " C: "+stick.getC()+
                " V: "+stick.getV()
            );
        }
    }
}