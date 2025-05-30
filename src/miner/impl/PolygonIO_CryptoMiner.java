/**
 * @author Bruce Lamb
 * @since 16 MAY 2025
 */
package tradedatacorp.miner;

import tradedatacorp.web.api.StickMinerAPI_ByUTCRange;
import tradedatacorp.web.api.StickInfernalMinerAPI_ByUTCRange;
import tradedatacorp.tools.jsonparser.JSON_Parser;
import tradedatacorp.tools.jsonparser.JSON_Object;
import tradedatacorp.tools.jsonparser.JSON_Array;
import tradedatacorp.tools.jsonparser.JSON_Number;
import tradedatacorp.tools.jsonparser.JSON_Integer;
import tradedatacorp.item.stick.primitive.StickDouble;
import tradedatacorp.item.stick.primitive.CandleStickFixedDouble;

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
public class PolygonIO_CryptoMiner implements StickMinerAPI_ByUTCRange, StickInfernalMinerAPI_ByUTCRange<StickDouble>{
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
    @Override
    public StickDouble[] refineArrayByUTCRange(String tickerSymbol, String interval, long utc_from, long utc_to){
        StickDouble[] r; //return value
        String jsonStringR = mineByUTCRange(tickerSymbol,interval,utc_from,utc_to); //Polygon.io API call
        return getSticksFromOHLCV_JSON(jsonStringR);
    }
    public StickDouble[] refineArrayByUTCRange(String tickerSymbol, String interval, String utc_from, String utc_to){return null;}
    public StickDouble[] refineArrayByUTCRange(HttpResponse<String> res){return null;}
    public Collection<StickDouble> refineCollectionByUTCRange(String tickerSymbol, String interval, long utc_from, long utc_to){return null;}
    public Collection<StickDouble> refineCollectionByUTCRange(String tickerSymbol, String interval, String utc_from, String utc_to){return null;}
    public Collection<StickDouble> refineCollectionByUTCRange(HttpResponse<String> res){return null;}

    //Original methods
    public void setKey(String newApiKey){apiKey=newApiKey;}

    //TODO
    //Must be a valid JSONSTRING containing attribute "results" array of objects with keys "t", "o", "h", "l", "c", "v".
    public StickDouble[] getSticksFromOHLCV_JSON(String polygonIO_JSON_String){
        StickDouble[] r;
        JSON_Object jsonObjects = JSON_Parser.parse(polygonIO_JSON_String);

        JSON_Array jsonOHLCV_Arr = (JSON_Array)jsonObjects.getJSON_Attribute("results");
        String UTC="t",
               O="o",
               H="h",
               L="l",
               C="c",
               V="v";

        r = new StickDouble[jsonOHLCV_Arr.getItemCount()];
        for(int i=0; i<r.length; ++i){
            JSON_Object jsonStick = (JSON_Object)jsonOHLCV_Arr.getItem(i);
            r[i] = new CandleStickFixedDouble(
                ((JSON_Integer)jsonStick.getJSON_Attribute(UTC)).getIntegerValue(),
                ((JSON_Number)jsonStick.getJSON_Attribute(O)).getDecimalValue(),
                ((JSON_Number)jsonStick.getJSON_Attribute(H)).getDecimalValue(),
                ((JSON_Number)jsonStick.getJSON_Attribute(L)).getDecimalValue(),
                ((JSON_Number)jsonStick.getJSON_Attribute(C)).getDecimalValue(),
                ((JSON_Number)jsonStick.getJSON_Attribute(V)).getDecimalValue()
            );
        }

        return r;
    }

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