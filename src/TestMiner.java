import tradedatacorp.miner.stickminer.PolygonIO_CryptoMiner;

public class TestMiner{
    public static void main(String[] args){
        System.out.println("Hello Miner!");
		PolygonIO_CryptoMiner miner = new PolygonIO_CryptoMiner("dummy");
		miner.mine("TODO", "TODO", 0, 0);
    }
}
