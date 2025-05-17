package tradedatacorp.tools.jsonparser;

public class JSON_Decimal implements JSON_Attribute{
    private Double decimal;
    private String key;

    public JSON_Decimal(double value){
        this.key=key;
        decimal=Double.valueOf(value);
    }

    @Override
    public byte getType(){return JSON_Object.DECIMAL;}

    @Override
    public String getKey(){return key;}

    public double getValue(){return decimal.doubleValue();}
}