/**
 * @author Bruce Lamb
 * @since 16 MAY 2025
 */
package tradedatacorp.miner;

import tradedatacorp.web.api.StickMinerAPI_ByUTCRange;

import java.util.Collection;
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
public class PolygonIO_CryptoMiner implements StickMinerAPI_ByUTCRange{
    private String apiKey;
    HttpClient pickaxe;

    //Constructor
    public PolygonIO_CryptoMiner(String apiKey){
        this.apiKey=apiKey;
        pickaxe = HttpClient.newHttpClient();
    }

    //StickMinerAPI_ByUTCRange Overrides
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
    @Override
    public String mineByUTCRange(String tickerSymbol, String interval, long utc_from, long utc_to){
        HttpRequest req = mineRequestByUTCRangeHelper(tickerSymbol, interval, Long.toString(utc_from), Long.toString(utc_to));
        HttpResponse<String> rawOre = mineResponseByUTCRangeHelper(req);
        if(rawOre != null){return rawOre.body();}
        else{return "Issue";}
    }

    @Override
    public String mineByUTCRange(String tickerSymbol, String interval, String utc_from, String utc_to){
        HttpRequest req = mineRequestByUTCRangeHelper(tickerSymbol, interval, utc_from, utc_to);
        HttpResponse<String> rawOre = mineResponseByUTCRangeHelper(req);
        if(rawOre != null){return rawOre.body();}
        else{return "Issue";}
    }

    @Override
    public HttpResponse<String> mineResponseByUTCRange(String tickerSymbol, String interval, long utc_from, long utc_to){
        HttpRequest req = mineRequestByUTCRangeHelper(tickerSymbol, interval, Long.toString(utc_from), Long.toString(utc_to));
        return mineResponseByUTCRangeHelper(req);
    }

    @Override
    public HttpResponse<String> mineResponseByUTCRange(String tickerSymbol, String interval, String utc_from, String utc_to){
        HttpRequest req = mineRequestByUTCRangeHelper(tickerSymbol, interval, utc_from, utc_to);
        return mineResponseByUTCRangeHelper(req);
    }

    //StickInfernalMinerAPI_ByUTCRange Overrides

    //Original methods
    public void setKey(String newApiKey){apiKey=newApiKey;}

    private HttpRequest mineRequestByUTCRangeHelper(String tickerSymbol, String interval, String utc_from, String utc_to){
        return HttpRequest.newBuilder()
            .GET()
            .uri(URI.create("https://api.polygon.io/v2/aggs/ticker/"+tickerSymbol+"/range/1/"+interval+"/"+utc_from+'/'+utc_to+"?adjusted=true&sort=asc&limit=120&apiKey="+apiKey))
            .build();
    }

    private HttpResponse<String> mineResponseByUTCRangeHelper(HttpRequest req){
        HttpResponse<String> rawOre;
        try{
            rawOre = pickaxe.send(req,BodyHandlers.ofString());
            return rawOre;
        }catch(Exception err){return null;}
    }
}