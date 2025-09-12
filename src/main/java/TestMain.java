import tradedatacorp.tools.time.QuarterInterval;
import tradedatacorp.tools.time.MonthInterval;
import tradedatacorp.warehouse.OHLCV_BinaryWarehouse;

import java.util.GregorianCalendar;
import java.time.ZonedDateTime;

public class TestMain{
    public static void main(String[] args){
        System.out.println("Warehouse testing.");
        OHLCV_BinaryWarehouse warehouse = new OHLCV_BinaryWarehouse();

        String result = warehouse.initialize(new String[]{"/root/test/mydb"});
        System.out.println(result);

        QuarterInterval tmpQtr = new QuarterInterval("Quarter", 1757621890000L);
        MonthInterval tmpMnth = new MonthInterval("Month", 1757621890000L);

        GregorianCalendar tmpQtrCalStart = new GregorianCalendar();
        tmpQtrCalStart.setTimeInMillis(tmpQtr.getStartUTC());

        GregorianCalendar tmpQtrCalEnd = new GregorianCalendar();
        tmpQtrCalEnd.setTimeInMillis(tmpQtr.getEndUTC());

        GregorianCalendar tmpMonthCalStart = new GregorianCalendar();
        tmpMonthCalStart.setTimeInMillis(tmpMnth.getStartUTC());

        GregorianCalendar tmpMonthCalEnd = new GregorianCalendar();
        tmpMonthCalEnd.setTimeInMillis(tmpMnth.getEndUTC());

        System.out.println("Quarter Start: "+tmpQtrCalStart.toZonedDateTime().toString());
        System.out.println("Quarter End:   "+tmpQtrCalEnd.toZonedDateTime().toString());
        System.out.println("Month Start:   "+tmpMonthCalStart.toZonedDateTime().toString());
        System.out.println("Month End:     "+tmpMonthCalEnd.toZonedDateTime().toString());
    }
}