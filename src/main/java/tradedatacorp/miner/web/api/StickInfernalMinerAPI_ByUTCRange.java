package tradedatacorp.miner.web.api;

import tradedatacorp.miner.Miner;
import tradedatacorp.tools.stick.Stick;

import java.util.Collection;
import java.net.http.HttpResponse;

public interface StickInfernalMinerAPI_ByUTCRange<R extends Stick> extends Miner{
    public R[] refineArrayByUTCRange(String tickerSymbol, String interval, long utc_from, long utc_to);
    public R[] refineArrayByUTCRange(String tickerSymbol, String interval, String utc_from, String utc_to);
    public R[] refineArrayByUTCRange(HttpResponse<String> res);
    public Collection<R> refineCollectionByUTCRange(String tickerSymbol, String interval, long utc_from, long utc_to);
    public Collection<R> refineCollectionByUTCRange(String tickerSymbol, String interval, String utc_from, String utc_to);
    public Collection<R> refineCollectionByUTCRange(HttpResponse<String> res);
}