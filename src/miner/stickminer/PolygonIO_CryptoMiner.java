/**
 * @author Bruce Lamb
 * @since 14 MAY 2025
 */
package tradedatacorp.miner.stickminer;

import java.util.TimeZone;
import java.time.ZonedDateTime;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * This class makes use of API's at https://polygon.io/
 */
public class PolygonIO_CryptoMiner implements StickMiner{
    private String apiKey;
    HttpClient pickaxe;

    public PolygonIO_CryptoMiner(String apiKey){
        this.apiKey=apiKey;
        pickaxe = HttpClient.newHttpClient();
    }

    public void setKey(String newApiKey){apiKey=newApiKey;}

    /**
     * Specific documentation located at: https://polygon.io/docs/rest/crypto/aggregates/custom-bars
     * 
     * At time of documentation here is the general use GET request format from polygon.io.
     * GET
     * /v2/aggs/ticker/{cryptoTicker}/range/{multiplier}/{timespan}/{from}/{to}
     * @param ticker
     * @param timespan
     * @param utc_from
     * @param utc_to
     * @return
     */
    public String mine(String ticker, String timespan, long utc_from, long utc_to){
        HttpRequest req = HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.polygon.io/v2/aggs/ticker/X:BTCUSD/range/1/minute/"+utc_from+'/'+utc_to+"?adjusted=true&sort=asc&limit="+apiKey))
            .build();

        HttpResponse<String> rawOre;
        try{
            rawOre = pickaxe.send(req,BodyHandlers.ofString());
            System.out.println("Status: "+rawOre.statusCode());
            return rawOre.body();
        }catch(Exception err){return "Issue";}
    }
}