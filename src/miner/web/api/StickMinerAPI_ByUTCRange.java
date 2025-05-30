package tradedatacorp.web.api;

import tradedatacorp.miner.Miner;

import java.net.http.HttpResponse;

public interface StickMinerAPI_ByUTCRange extends Miner{
    public String mineByUTCRange(String tickerSymbol, String interval, long utc_from, long utc_to);
    public String mineByUTCRange(String tickerSymbol, String interval, String utc_from, String utc_to);
    public HttpResponse<String> mineResponseByUTCRange(String tickerSymbol, String interval, long utc_from, long utc_to);
    public HttpResponse<String> mineResponseByUTCRange(String tickerSymbol, String interval, String utc_from, String utc_to);
}