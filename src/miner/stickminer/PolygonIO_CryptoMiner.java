/**
 * @author Bruce Lamb
 * @since 14 MAY 2025
 */
package tradedatacorp.miner.stickminer;

public class PolygonIO_CryptoMiner implements StickMiner{
    private String apiKey;

    public PolygonIO_CryptoMiner(String apiKey){this.apiKey=apiKey;}

    public void setKey(String newApiKey){apiKey=newApiKey;}

    /**
     * Specific documentation located at: https://polygon.io/docs/rest/crypto/aggregates/custom-bars
     * 
     * At time of documentation here is the general use GET request format.
     * GET
     * /v2/aggs/ticker/{cryptoTicker}/range/{multiplier}/{timespan}/{from}/{to}
     * @return
     */
    public String mine(String ticker, String timespan, int quantity){return "";}
}