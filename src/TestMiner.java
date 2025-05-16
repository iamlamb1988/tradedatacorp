import tradedatacorp.miner.stickminer.PolygonIO_CryptoMiner;

public class TestMiner{
    public static void main(String[] args){
        System.out.println("Hello Miner!");
		PolygonIO_CryptoMiner miner = new PolygonIO_CryptoMiner("120&apiKey=NuslrjAqkue1coRYRnJVSMv1vgUh_GN8");
		String jsonString = miner.mine("TODO", "TODO", 1735689600000L-5*(60*1000), 1735689600000L);
        System.out.println(jsonString);
    }
}
