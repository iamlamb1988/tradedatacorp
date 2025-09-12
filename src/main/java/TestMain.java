import tradedatacorp.tools.time.QuarterInterval;
import tradedatacorp.tools.time.MonthInterval;
import tradedatacorp.warehouse.OHLCV_BinaryWarehouse;

import java.util.TimeZone;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TestMain{
    public static void main(String[] args){
        System.out.println("Warehouse testing.");
        OHLCV_BinaryWarehouse warehouse = new OHLCV_BinaryWarehouse();

        String result = warehouse.initialize(new String[]{"/root/test/mydb"});
        System.out.println(result);

        long utcRef = 1757621890000L;
        TimeZone CST = TimeZone.getTimeZone("US/Central");
        TimeZone EST = TimeZone.getTimeZone("US/Eastern");

        printTimeRange(utcRef, CST);
        System.out.println("\n");
        printTimeRange(utcRef, EST);
    }

    public static void printTimeRange(long utcRef, TimeZone tz){
        DateTimeFormatter utcFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
        System.out.println("Timestamp Ref: "+utcRef);
        System.out.println("Timezone:      "+tz.getID());
        QuarterInterval tmpQtr = new QuarterInterval("Quarter", tz, utcRef);
        MonthInterval tmpMnth = new MonthInterval("Month", tz, utcRef);
        System.out.println("Quarter Start: "+tmpQtr.getFormattedStartTime());
        System.out.println("Quarter End:   "+tmpQtr.getFormattedEndTime());

        ZonedDateTime utcStartFormat = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(tmpQtr.getStartUTC()),
            TimeZone.getTimeZone("UTC").toZoneId()
        );
        ZonedDateTime utcEndFormat = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(tmpQtr.getEndUTC()),
            TimeZone.getTimeZone("UTC").toZoneId()
        );

        System.out.println("Quarter UTC Start: "+tmpQtr.getStartUTC());
        System.out.println("Quarter UTC End:   "+tmpQtr.getEndUTC());
        System.out.println("Quarter UTC Start: "+utcStartFormat.format(utcFormatter));
        System.out.println("Quarter UTC End:   "+utcEndFormat.format(utcFormatter));

        System.out.println("Month Start:   "+tmpMnth.getFormattedStartTime());
        System.out.println("Month End:     "+tmpMnth.getFormattedEndTime());
        utcStartFormat = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(tmpMnth.getStartUTC()),
            TimeZone.getTimeZone("UTC").toZoneId()
        );
        utcEndFormat = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(tmpMnth.getEndUTC()),
            TimeZone.getTimeZone("UTC").toZoneId()
        );

        System.out.println("Month UTC Start:   "+tmpMnth.getStartUTC());
        System.out.println("Month UTC End:     "+tmpMnth.getEndUTC());
        System.out.println("Month UTC Start:   "+utcStartFormat.format(utcFormatter));
        System.out.println("Month UTC End:     "+utcEndFormat.format(utcFormatter));
    }
}