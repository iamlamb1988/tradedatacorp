import tradedatacorp.miner.PolygonIO_CryptoMiner;

import java.net.http.HttpResponse;

public class TestMiner{
    public static void main(String[] args){
        System.out.println("Hello Miner!");
		PolygonIO_CryptoMiner miner = new PolygonIO_CryptoMiner("NuslrjAqkue1coRYRnJVSMv1vgUh_GN8");
		HttpResponse<String> responseFromPolygonIO = miner.mineResponseByUTCRange("X:BTCUSD", "minute", 1735689600001L-5*(60*1000), 1735689600000L);

        System.out.println("STATUS: "+responseFromPolygonIO.statusCode());
        System.out.println("BODY:\n"+responseFromPolygonIO.body());
    }
}
